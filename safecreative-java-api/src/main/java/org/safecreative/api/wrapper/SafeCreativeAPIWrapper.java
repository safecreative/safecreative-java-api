/*
Copyright (c) 2010 Safe Creative (http://www.safecreative.org)

Permission is hereby granted, free of charge, to any person
obtaining a copy of this software and associated documentation
files (the "Software"), to deal in the Software without
restriction, including without limitation the rights to use,
copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the
Software is furnished to do so, subject to the following
conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
OTHER DEALINGS IN THE SOFTWARE.
 */
package org.safecreative.api.wrapper;

import org.safecreative.api.ApiException;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.basic.DateConverter;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.safecreative.api.SafeCreativeAPI;
import org.safecreative.api.SafeCreativeAPI.AuthkeyLevel;
import org.safecreative.api.wrapper.converters.WorkEntryConverter;
import org.safecreative.api.wrapper.model.AuthKey;
import org.safecreative.api.wrapper.model.AuthKeyState;
import org.safecreative.api.wrapper.model.Profile;
import org.safecreative.api.wrapper.model.UserLink;
import org.safecreative.api.wrapper.model.Work;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SafeCreativeAPI main wrapper
 * @author mpolo@safecreative.org
 * @author jguillo@safecreative.org
 */
public class SafeCreativeAPIWrapper {

    public final static String DEFAULT_API_URL = "https://api.safecreative.org";
    public final static String DEFAULT_API_SEARCH_URL = "http://api-search.safecreative.org";

    private static Logger log = LoggerFactory.getLogger(SafeCreativeAPIWrapper.class);
    private final static String STATE_READY = "ready";
    private final static String ERROR_WORK_NOTFOUND = "WorkNotFound";
    private SafeCreativeAPI api;
    private String baseUrl;
    private String baseSearchUrl;

    public SafeCreativeAPIWrapper(String sharedKey, String privateKey) {
        this(new SafeCreativeAPI(sharedKey, privateKey));
    }

    public SafeCreativeAPIWrapper(SafeCreativeAPI api) {
        this.api = api;
    }

    public SafeCreativeAPI getApi() {
        return api;
    }

    public String getBaseSearchUrl() {
        return StringUtils.defaultIfEmpty(baseSearchUrl, DEFAULT_API_SEARCH_URL);
    }

    public void setBaseSearchUrl(String baseSearchUrl) {
        this.baseSearchUrl = baseSearchUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getBaseUrl() {
        return StringUtils.defaultIfEmpty(baseUrl, DEFAULT_API_URL);
    }

    public String getVersion() throws ApiException {
        if (api.getBaseUrl() == null) {
            api.setBaseUrl(getBaseUrl());
        }
        String result = callComponent("version");
        String version = api.evalXml(result, "/version");
        return version;
    }

    public AuthKeyState checkAuth(AuthKey authKey) throws ApiException {
        return checkAuth(authKey.getAuthkey());
    }

    public AuthKeyState checkAuth(String authKey) throws ApiException {
        setApiUrl();
        String result = api.getAuthKeyState(authKey);
        checkError(result);
        String usercode = api.evalXml(result, "/authkeystate/usercode");
        String authorized = api.evalXml(result, "/authkeystate/authorized");
        AuthKeyState state = new AuthKeyState();
        state.setAuthorized(authorized);
        state.setCode(usercode);
        return state;
    }

    public AuthKey createAuth() throws ApiException {
        return createAuth(AuthkeyLevel.MANAGE);
    }

    @SuppressWarnings("unchecked")
    public AuthKey createAuth(AuthkeyLevel authkeyLevel) throws ApiException {
        setApiUrl();
        Map params = api.createParams("component", "authkey.create", "sharedkey", api.getSharedKey());
        String result = api.callSigned(params, true, false);
        checkError(result);
        String authKey = api.evalXml(result, "/authkeycreate/authkey");
        String authPrivateKey = api.evalXml(result, "/authkeycreate/privatekey");
        if (authKey == null || authPrivateKey == null) {
            throw new ApiException("authkey.create error. Result: " + result);
        }
        AuthKey auth = new AuthKey();
        auth.setAuthkey(authKey);
        auth.setPrivatekey(authPrivateKey);
        auth.setManageUrl(api.getManageAuthkeyUrl(authKey, authPrivateKey, authkeyLevel == null ? AuthkeyLevel.MANAGE : authkeyLevel));
        return auth;
    }

    public UserLink linkUser(String mail, AuthkeyLevel level,
            String firstName, String middleName, String lastName) throws ApiException {
        return linkUser(mail, level, firstName, middleName, lastName, null, null, null, null, null, null);
    }

    @SuppressWarnings("unchecked")
    public UserLink linkUser(String mail, AuthkeyLevel level,
            String firstName, String middleName, String lastName,
            String addressline1, String addressline2,
            String addresszip, String addresscity, String addresscountry, String locale) throws ApiException {
        setApiUrl();
        Map params = api.createParams("component", "user.link", "sharedkey", api.getSharedKey());
        params.put("mail", mail);
        params.put("level", level.name());
        params.put("firstname", firstName);
        params.put("middlename", middleName);
        params.put("lastname", lastName);
        params.put("addressline1", addressline1);
        params.put("addressline2", addressline2);
        params.put("addresszip", addresszip);
        params.put("addresscity", addresscity);
        params.put("addresscountry", addresscountry);
        params.put("locale", locale);
        String result = api.callSigned(params, true, false);
        checkError(result);
        log.debug("user.link result:\n{}", result);
        return readObject(UserLink.class, result);
    }

    @SuppressWarnings("unchecked")
    public List<Profile> getProfiles(AuthKey authKey) throws ApiException {
        if (authKey == null) {
            throw new ApiException("null auth key");
        }
        setApiUrl();
        Map params = api.createParams("component", "user.profiles", "authkey", authKey.getAuthkey());
        String result = api.callSigned(params, authKey.getPrivatekey(), true, false);
        checkError(result);
        List<Profile> profiles = readList(result, "profiles", "profile", Profile.class);
        log.info("Profiles {}", profiles);
        return profiles;
    }

    /**
     * Get public work info
     * @param code
     * @return Work or <code>null</code> if none found
     * @throws ApiException
     */
    public Work getWork(String code) throws ApiException {
        setApiUrl();
        String result = null;
        try {
            result = callComponent("work.get", "code", code);
        } catch (ApiException ex) {
            if (ERROR_WORK_NOTFOUND.equals(ex.getErrorCode())) {
                return null;
            }
            throw ex;
        }
        XStream xs = new XStream();
        xs.registerConverter(new WorkEntryConverter());
        Work work = readObject(Work.class, result,xs);
        return work;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Internal api helpers
    ////////////////////////////////////////////////////////////////////////////
    String callComponent(String component, String... params) throws ApiException {
        Map<String, String> p = createParams(component);
        if (params != null && params.length > 0) {
            p.putAll(api.createParams(params));
        }
        String result = null;
        try {
            result = api.call(p);
        } catch (Exception ex) {
            if (ex instanceof ApiException) {
                throw (ApiException) ex;
            }
            //Wrap
            Throwable cause = ex.getCause();
            throw new ApiException(cause);
        }
        checkError(result);
        return result;
    }

    //TODO
    //public String callComponentSigned(String component, String... params) throws ApiException {
    boolean checkReady(String response) throws ApiException {
        return checkState(response, STATE_READY);
    }

    boolean checkState(String response, String expected) throws ApiException {
        try {
            String state = api.getResponseState(response);
            return expected.equals(state);
        } catch (Exception ex) {
            throw new ApiException(ex);
        }
    }

    void checkError(String response) throws ApiException {
        if (api.isError(response)) {
            try {
                String errorCode = api.getErrorCode(response);
                String errorMessage = api.getErrorMessage(response);
                throw new ApiException(errorCode, errorMessage);
            } catch (Exception ex) {
                if (ex instanceof ApiException) {
                    throw (ApiException) ex;
                }
                throw new ApiException(ex);
            }
        }
    }

    private Map<String, String> createParams(String component) {
        return api.createParams("component", component);
    }

    private String readString(String response) {
        Object result = readObject(response);
        if (result == null) {
            return null;
        }
        return result.toString();
    }

    @SuppressWarnings("unchecked")
    private <T extends Object> List<T> readList(String response, String listElement, String element, Class<T> clazz) {
        XStream xs = new XStream();
        xs.alias(listElement, List.class);
        xs.alias(element, clazz);
        Object result = null;
        try {
            result = xs.fromXML(response);
        } catch (Exception e) {
            log.error("Parsing xml response", e);
        }
        return (List<T>) result;
    }

    private Object readObject(String response) {
        XStream xs = new XStream();
        Object result = null;
        try {
            result = xs.fromXML(response);
        } catch (Exception e) {
            log.error("Parsing xml response", e);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private <T> T readObject(Class<T> clazz, String response) {
        return readObject(clazz, response,(Converter)null);
    }

    @SuppressWarnings("unchecked")
    private <T> T readObject(Class<T> clazz, String response,Converter converter) {
        XStream xs = new XStream();
        if(converter != null) {
            xs.registerConverter(converter);
        }
        return readObject(clazz, response,xs);
    }

    @SuppressWarnings("unchecked")
    <T> T readObject(Class<T> clazz, String response,XStream xs) {
        if(xs == null)  {
            xs = new XStream();
        }
        xs.alias(clazz.getSimpleName().toLowerCase(), clazz);
        T result = null;
        try {
            result = (T) xs.fromXML(response);
        } catch (Exception e) {
            log.error("Parsing xml response", e);
        }
        return result;
    }

    private void setApiUrl() {
        api.setBaseUrl(getBaseUrl());
    }

    private void setApiSearchUrl() {
        api.setBaseUrl(getBaseSearchUrl());
    }
}
