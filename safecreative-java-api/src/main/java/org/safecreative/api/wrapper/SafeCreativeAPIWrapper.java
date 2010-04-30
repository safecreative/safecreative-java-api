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
import java.io.File;
import java.io.FileInputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.safecreative.api.RegisterWork;
import org.safecreative.api.SafeCreativeAPI;
import org.safecreative.api.SafeCreativeAPI.AuthkeyLevel;
import org.safecreative.api.UploadProgressListener;
import org.safecreative.api.util.Digest;
import org.safecreative.api.wrapper.converters.LicenseConverter;
import org.safecreative.api.wrapper.converters.ListPageConverter;
import org.safecreative.api.wrapper.converters.WorkEntryConverter;
import org.safecreative.api.wrapper.model.AuthKey;
import org.safecreative.api.wrapper.model.AuthKeyState;
import org.safecreative.api.wrapper.model.License;
import org.safecreative.api.wrapper.model.Profile;
import org.safecreative.api.wrapper.model.UserLink;
import org.safecreative.api.wrapper.model.Work;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SafeCreativeAPI main wrapper
 *
 * @author mpolo@safecreative.org
 * @author jguillo@safecreative.org
 */
public class SafeCreativeAPIWrapper {

    public final static String DEFAULT_API_URL = "https://api.safecreative.org";
    public final static String DEFAULT_API_SEARCH_URL = "http://api-search.safecreative.org";
    public final static String ARENA_URL = "https://arena.safecreative.org";

    private static Logger log = LoggerFactory.getLogger(SafeCreativeAPIWrapper.class);
    private final static String STATE_READY = "ready";
    private final static String ERROR_WORK_NOTFOUND = "WorkNotFound";
    private SafeCreativeAPI api;
    private String baseUrl;
    private String baseSearchUrl;

    private AuthKey authKey;

    /**
     * Constructor
     *
     * @param sharedKey api's shared key
     * @param privateKey api's privatekey key
     */
    public SafeCreativeAPIWrapper(String sharedKey, String privateKey) {
        this(new SafeCreativeAPI(sharedKey, privateKey));
    }

    /**
     * Constructor using low level api interface
     * @param api low level interface
     */
    public SafeCreativeAPIWrapper(SafeCreativeAPI api) {
        this.api = api;
    }

    /**
     * Get low level api interface
     *
     * @return low level api interface
     */
    public SafeCreativeAPI getApi() {
        return api;
    }

    /**
     * Gets api search endpoint url
     *
     * @return api search endpoint url
     */
    public String getBaseSearchUrl() {
        return StringUtils.defaultIfEmpty(baseSearchUrl, DEFAULT_API_SEARCH_URL);
    }

    /**
     * Sets api search endpoint url
     *
     * @param baseSearchUrl api search endpoint url
     */
    public void setBaseSearchUrl(String baseSearchUrl) {
        this.baseSearchUrl = baseSearchUrl;
    }

    /**
     * Sets api endpoint url
     *
     * @param baseUrl api endpoint url
     */
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    /**
     * Gets api endpoint url
     *
     * @return api endpoint url
     */
    public String getBaseUrl() {
        return StringUtils.defaultIfEmpty(baseUrl, DEFAULT_API_URL);
    }

    /**
     * Gets current authorization key
     *
     * @return the authKey
     */
    public AuthKey getAuthKey() {
        return authKey;
    }

    /**
     * Sets current authorization key
     *
     * @param authKey the authKey to set
     */
    public void setAuthKey(AuthKey authKey) {
        this.authKey = authKey;
    }

    /**
     * @return the locale
     */
    public Locale getLocale() {
        return api.getLocale();
    }

    /**
     * @param locale the locale to set
     */
    public void setLocale(Locale locale) {
        api.setLocale(locale);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Authorization methods
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Check authorization state
     *
     * @param authKey authorization key
     * @return authorization state
     * @see AuthKeyState
     * @throws ApiException
     */
    public AuthKeyState checkAuth(AuthKey authKey) throws ApiException {
        return checkAuth(authKey.getAuthkey());
    }

    /**
     * Check authorization state
     *
     * @param authKey authorization key
     * @return authorization state
     * @see AuthKeyState
     * @throws ApiException
     */
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

    /**
     * Create a new authorization key with default <code>MANAGE</code> access
     * level
     *
     * @return authorization key
     * @throws ApiException
     */
    public AuthKey createAuth() throws ApiException {
        return createAuth(AuthkeyLevel.MANAGE);
    }

    /**
     * Create a new authorization key
     * @param authkeyLevel api authorization access level
     * @return authorization key
     * @throws ApiException
     */
    @SuppressWarnings("unchecked")
    public AuthKey createAuth(AuthkeyLevel authkeyLevel) throws ApiException {
        setApiUrl();
        Map params = api.createParams("component", "authkey.create", "sharedkey", api.getSharedKey());
        String result = api.callSigned(params, true, false);
        checkError(result);
        String auth = api.evalXml(result, "/authkeycreate/authkey");
        String authPrivate = api.evalXml(result, "/authkeycreate/privatekey");
        if (auth == null || authPrivate == null) {
            throw new ApiException("authkey.create error. Result: " + result);
        }
        authKey = new AuthKey();
        authKey.setAuthkey(auth);
        authKey.setPrivatekey(authPrivate);
        authKey.setManageUrl(api.getManageAuthkeyUrl(auth, authPrivate, authkeyLevel == null ? AuthkeyLevel.MANAGE : authkeyLevel));
        return authKey;
    }

    ////////////////////////////////////////////////////////////////////////////
    // User methods
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Create/Update a user by using a mail address
     *
     * @param mail user's mail address
     * @param level api authorization access level
     * @param firstName
     * @param middleName
     * @param lastName
     * @return user link information
     * @see UserLink
     * @throws ApiException
     */
    public UserLink linkUser(String mail, AuthkeyLevel level,
            String firstName, String middleName, String lastName) throws ApiException {
        return linkUser(mail, level, firstName, middleName, lastName, null, null, null, null, null, null);
    }

    /**
     * Create/Update a user by using a mail address
     *
     * @param mail mail user's mail address
     * @param level api authorization access level
     * @param firstName
     * @param middleName
     * @param lastName
     * @param addressline1
     * @param addressline2
     * @param addresszip
     * @param addresscity
     * @param addresscountry
     * @param locale
     * @return user link information
     * @see UserLink
     * @throws ApiException
     */
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

    ////////////////////////////////////////////////////////////////////////////
    // Information methods
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Retrieves current api version. This method can also be used to check api
     * access.
     * @return api's current version
     * @throws ApiException
     */
    public String getVersion() throws ApiException {
        if (api.getBaseUrl() == null) {
            api.setBaseUrl(getBaseUrl());
        }
        String result = callComponent("version");
        String version = api.evalXml(result, "/version");
        return version;
    }

    /**
     * Get user defined registration profiles using last authorization
     *
     * @return List of registration profiles
     * @throws ApiException
     */
    public List<Profile> getProfiles() throws ApiException {
        return getProfiles(authKey);
    }

    /**
     * Get user defined registration profiles
     *
     * @param authKey user authorization key
     * @return List of registration profiles
     * @throws ApiException
     */
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
        log.debug("Profiles {}", profiles);
        return profiles;
    }

    /**
     * Get user available licenses including custom licenses
     *
     * @return First list page of user avaliable licenses
     * @throws ApiException
     */
    public ListPage<License> getLicenses() throws ApiException {
        return getLicenses(1);
    }

    /**
     * Get user available licenses including custom licenses
     *
     * @param page page number
     * @return List of user avaliable licenses
     * @throws ApiException
     */
    public ListPage<License> getLicenses(int page) throws ApiException {
        return getLicenses(page,authKey);
    }
    /**
     * Get user available licenses including custom licenses
     *
     * @param page page number
     * @param authKey authKey user authorization key
     * @return List of user avaliable licenses
     * @throws ApiException
     */
    @SuppressWarnings("unchecked")
    public ListPage<License> getLicenses(int page,AuthKey authKey) throws ApiException {
        if (authKey == null) {
            throw new ApiException("null auth key");
        }
        setApiUrl();
        Map params = api.createParams("component", "user.licenses", "authkey", authKey.getAuthkey());
        String result = api.callSigned(params, authKey.getPrivatekey(), true, false);
        checkError(result);
        return readListPage(result, License.class, new LicenseConverter());
    }

    @SuppressWarnings("unchecked")
    public List<Work.Type> getWorkTypes() throws ApiException {
        setApiUrl();
        String result = callComponent("work.types");
        List<Work.Type> workTypes = readList(result, "worktypes", "worktype", Work.Type.class);
        log.debug("Work Types {}", workTypes);
        return workTypes;
    }

    @SuppressWarnings("unchecked")
    public List<Work.Language> getWorkLanguages() throws ApiException {
        setApiUrl();
        String result = callComponent("work.languages");
        List<Work.Language> workLanguages = readList(result, "worklanguages", "language", Work.Language.class);
        log.debug("Work Languages {}", workLanguages);
        return workLanguages;
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
        return readObject(Work.class, result,new WorkEntryConverter());
    }

    ////////////////////////////////////////////////////////////////////////////
    // Registration methods
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Registers a file using a registration profile
     *
     * @param title Work's title
     * @param file File to register
     * @param profile Registration profile
     * @param uploadProgressListener Upload progress listener
     * @return Work's registration code
     * @throws ApiException
     */
    public String registerWork(String title,File file,Profile profile,UploadProgressListener uploadProgressListener) throws ApiException {
        setApiUrl();
        byte[] digest;
        log.debug("Calculating file {} {} checksum", file, Digest.SHA1);
        try {
            digest = Digest.getBytesDigest(new FileInputStream(file), Digest.SHA1);
        } catch (Exception ex) {
            throw new ApiException(ex);
        }
        String checkSum = Digest.toHex(digest);
        log.info("File {} checksum: {}", file, checkSum);
        RegisterWork registerWork = new RegisterWork(api);
        registerWork.setProfile(profile.getCode());
        registerWork.setUploadProgressListener(uploadProgressListener);
        api.setAuthKey(authKey.getAuthkey());
        api.setPrivateAuthKey(authKey.getPrivatekey());
        String workCode = null;
        try {
            workCode = registerWork.registerWork(title, file.getName(), file, null);
        } catch (Exception e) {
            if (SafeCreativeAPI.NOT_AUTHORIZED_ERROR.equals(e.getMessage())) {
                log.warn("Not authorized to update/register {}", e);
                throw new ApiException(SafeCreativeAPI.NOT_AUTHORIZED_ERROR,e.getLocalizedMessage());
            }
            throw new ApiException(e);
        }
        return workCode;
    }

    ////////////////////////////////////////////////////////////////////////////
    // SEARCH methods
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Search works by search field.
     *
     * @param fieldValues Variable list of pairs of search field,value
     * @return List of found works
     * @throws ApiException
     */
    public ListPage<Work> searchWorksByFields(Object...fieldValues) throws ApiException {
        return searchWorksByFields(1,fieldValues);
    }

    /**
     * Search works by search field.
     *
     * @see SearchField
     * @param page page number
     * @param fieldValues Variable list of pairs of search field,value
     * @return List of found works
     * @throws ApiException
     */
    public ListPage<Work> searchWorksByFields(int page,Object...fieldValues) throws ApiException {        
        if (fieldValues != null && fieldValues.length % 2 != 0) {
            throw new IllegalArgumentException("odd field value array size");
        }
        Map<String, String> params = api.createParams(fieldValues);
        List<String> fieldParamList = new LinkedList<String>();
        int field=1;
        for(String fieldName : params.keySet()) {
            fieldParamList.add("field"+field);
            fieldParamList.add(fieldName);
            fieldParamList.add("value"+field);
            fieldParamList.add(params.get(fieldName));
            field++;
        }
        fieldParamList.add("page");
        fieldParamList.add(String.valueOf(page));
        setApiSearchUrl();
        String result = callComponent("search.byfields",fieldParamList.toArray(new String[fieldParamList.size()]));
        ListPage<Work> results = readWorkListPage(result);
        return results;
    }

    /**
     * Search works by MD5 hash.
     * @param md5 value
     * @return List of found works
     * @throws ApiException
     */
    public ListPage<Work> searchWorksByHashMD5(String md5) throws ApiException {
        return searchWorksByHash(1, SearchMethod.HASH_MD5, md5);
    }

    /**
     * Search works by SHA-1 hash.
     * @param sha1 value
     * @return List of found works
     * @throws ApiException
     */
    public ListPage<Work> searchWorksByHashSHA1(String sha1) throws ApiException {
        return searchWorksByHash(1, SearchMethod.HASH_SHA1, sha1);
    }


    /**
     * Search works by hash.
     *
     * @param page page number
     * @param method A SearchMethod.HASH_XXX value
     * @param value value
     * @return List of found works
     * @throws ApiException
     */
    public ListPage<Work> searchWorksByHash(int page,SearchMethod method,String value) throws ApiException {
        if(!method.name().startsWith("HASH_")) {
            throw new IllegalArgumentException("Bad search method "+method);
        }
        //Direct search by hash (use main api servers instead of search servers):
        setApiUrl();
        String result = callComponent("search.byhash",method.getFieldName(),value);
        ListPage<Work> results = readWorkListPage(result);
        return results;
    }

    /**
     * Search works by text query.
     *
     * @param query text query
     * @return First page list of found works
     * @throws ApiException
     */
    public ListPage<Work> searchWorksByQuery(String query) throws ApiException {
        return searchWorksByQuery(1, query);
    }

    /**
     * Search works by text query.
     * @param page page number
     * @param query text query
     * @return List of found works
     * @throws ApiException
     */
    public ListPage<Work> searchWorksByQuery(int page,String query) throws ApiException {
        setApiSearchUrl();
        String result = callComponent("search.byquery","query",query);
        ListPage<Work> results = readWorkListPage(result);
        return results;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Internal api helpers
    ////////////////////////////////////////////////////////////////////////////
    String callComponent(String component, String... params) throws ApiException {
        Map<String, String> p = createParams(component);
        if (params != null && params.length > 0) {
            p.putAll(api.createParams((Object[])params));
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

    private ListPage<Work> readWorkListPage(String response) {
        return readListPage(response, Work.class, new WorkEntryConverter());
    }

    @SuppressWarnings("unchecked")
    private <T extends Object> ListPage<T> readListPage(String response,Class<T> clazz,Converter converter) {
        XStream xs = new XStream();        
        xs.registerConverter(new ListPageConverter<T>(clazz,converter));                
        ListPage<T> listPage = readObject(ListPage.class, response,xs);        
        return listPage;
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
