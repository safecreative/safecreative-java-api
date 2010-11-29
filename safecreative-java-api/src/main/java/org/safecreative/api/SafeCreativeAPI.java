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
package org.safecreative.api;

import org.safecreative.api.util.Digest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.apache.commons.lang.StringUtils;
import org.safecreative.api.util.IOHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

/**
 * SafeCreative API frontend
 *
 * @author mpolo@safecreative.org
 * @author jguillo@safecreative.org
 */
public class SafeCreativeAPI {

    public static final String DEFAULT_ENCODING = "UTF-8";
    public static final String API_ENDPOINT = "/v2/";
    public static final String MANAGE_ENDPOINT = "/api-ui/authkey.edit?";
    public static final String STATE_REGISTERED = "REGISTERED";
    public static final String STATE_PRE_REGISTERED = "PRE_REGISTERED";
    public static final String NOT_AUTHORIZED_ERROR = "NotAuthorized";
    private static Logger log = LoggerFactory.getLogger(SafeCreativeAPI.class);
    private String baseUrl;    
    private Long timeOffset;
    private XPathFactory xpathFactory;
    private String sharedKey, privateKey;
    private String authKey, privateAuthKey;
    private Locale locale;

    public enum AuthkeyLevel {
        GET, ADD, MANAGE
    }

    public enum RelationType {
        COMPOSITION, DERIVATION, VERSION, RELATED
    }

    public SafeCreativeAPI(String sharedKey, String privateKey) {
        this.sharedKey = sharedKey;
        this.privateKey = privateKey;
        this.xpathFactory = XPathFactory.newInstance();        
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setAuthKey(String authKey) {
        this.authKey = authKey;
    }

    public String getAuthKey() {
        return authKey;
    }

    public void setPrivateAuthKey(String privateAuthKey) {
        this.privateAuthKey = privateAuthKey;
    }

    public String getPrivateAuthKey() {
        return privateAuthKey;
    }

    public String getSharedKey() {
        return sharedKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public static boolean isValidCode(String code) {
        return StringUtils.isNotBlank(code) && StringUtils.isNumeric(code) && code.trim().length() == 13;
    }

    /**
     * @return the locale
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * @param locale the locale to set
     */
    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    @SuppressWarnings("unchecked")
    public String getManageAuthkeyUrl(String authkey, String privatekey, AuthkeyLevel level) {
        if(authkey == null) {
            throw new IllegalArgumentException("null auth key");
        }
        if(privatekey == null) {
            throw new IllegalArgumentException("null private key");
        }
        if(level == null) {
            throw new IllegalArgumentException("null auth key level");
        }
        Map params = createParams();
        params.put("level", level.toString());
        params.put("authkey", authkey);
        params.put("sharedkey", sharedKey);
        params.put("ztime", getZTime());
        return baseUrl + MANAGE_ENDPOINT + signParams(params, privatekey);
    }

    public String getNonceKey(String authKey) {
        return evalXml(getAuthKeyState(authKey), "/authkeystate/noncekey");
    }

    @SuppressWarnings("unchecked")
    public String getAuthKeyState(String authKey) {
        if(authKey == null) {
            throw new IllegalArgumentException("null auth key");
        }
        Map params = createParams("component", "authkey.state", "authkey", authKey, "sharedkey", sharedKey);
        return callSigned(params, true, false);
    }

    public String getZTime() {
        if (timeOffset == null) {
            String result = call("component=ztime");
            String value = evalXml(result, "/ztime");
            if (value == null) {
                throw new RuntimeException("bad ztime");
            }
            long ztime = Long.parseLong(value);
            timeOffset = ztime - getSystemTime();
            return value;
        }
        return Long.toString(timeOffset + getSystemTime());
    }

    //////////////////////////////////////////////////////// CORE:
    public Map<String, String> createParams(Object... values) {
        if (values != null && values.length % 2 != 0) {
            throw new IllegalArgumentException("odd value array size");
        }
        Map<String, String> result = new HashMap<String, String>();
        if (values != null) {
            int size = values.length;
            Object value;
            for (int i = 0; i < size; i += 2) {
                value = values[i + 1];
                result.put(String.valueOf(values[i]), value == null ? null : String.valueOf(value));
            }
        }
        return result;
    }

    public String callSigned(Map<String, String> params, boolean ztime, boolean noncekey) {
        return callSigned(params, ztime, noncekey,true);
    }

    public String callSigned(Map<String, String> params, boolean ztime, boolean noncekey, boolean addLocale) {
        return callSigned(params, privateKey, ztime, noncekey,addLocale);
    }

    public String callSigned(Map<String, String> params, String privateKey, boolean ztime, boolean noncekey) {
        return callSigned(params, privateKey,ztime, noncekey,true);
    }

    public String callSigned(Map<String, String> params, String privateKey, boolean ztime, boolean noncekey,boolean addLocale) {
        if (ztime) {
            params.put("ztime", getZTime());
        }
        if (noncekey) {
            params.put("noncekey", getNonceKey(authKey));
        }
        if (addLocale) {
            addLocale(params);
        }
        return call(signParams(params, privateKey));
    }

    public String call(Map<String, String> params) {
        return call(params,true);
    }

    public String call(Map<String, String> params,boolean addLocale) {
        StringBuilder encoded = new StringBuilder();
        if(addLocale) {
            addLocale(params);
        }
        for (String param : params.keySet()) {
            String value = params.get(param);
            try {
                encoded.append("&" + param + "=" + (value == null ? "" : URLEncoder.encode(value, DEFAULT_ENCODING)));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        return call(encoded.toString().substring(1));
    }

    public String call(String params) {
        String uri = baseUrl + API_ENDPOINT;
        OutputStream os = null;
        try {
            log.debug(String.format("api request: \n%s?%s\n", uri, params));
            URL url = new URL(uri);
            URLConnection conn = url.openConnection();
            conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded;charset=" + DEFAULT_ENCODING);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            os = conn.getOutputStream();
            os.write(params.getBytes(DEFAULT_ENCODING));
            String response = readString(conn.getInputStream());
            log.debug(String.format("api response:\n %s\n", response));
            return response;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            IOHelper.closeQuietly(os);
        }
    }

    public String signParams(Map<String, String> params, String privatekey) {
        StringBuilder unencoded = new StringBuilder();
        StringBuilder encoded = new StringBuilder();

        List<String> keys = new ArrayList<String>();
        keys.addAll(params.keySet());
        Collections.sort(keys);

        for (String key : keys) {
            String value = params.get(key);
            if (value == null) {
                log.debug("null param {}", key);
                continue;
            }
            unencoded.append("&" + key + "=" + value);
            try {
                encoded.append("&" + key + "=" + URLEncoder.encode(value, DEFAULT_ENCODING));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        String en = encoded.toString().substring(1);
        String un = unencoded.toString().substring(1);
        return en + "&signature=" + Digest.getHexDigest(privatekey + "&" + un, Digest.SHA1);
    }

    public String getResponseState(String response) throws Exception {
        return getResponseState("restvalueresponse", response);
    }

    public String getResponseState(String responseType, String response) throws Exception {
        return evalXml(response, String.format("/%s/state", responseType));
    }

    public boolean isError(String result) {
        return !StringUtils.isEmpty(result) && (
                (hasXmlElement(result, "error") && hasXmlElement(result, "errorId")) ||
                (hasXmlElement(result, "exception") && hasXmlElement(result, "exceptionId")) );
    }

    public String getErrorCode(String response) {
        if (response.indexOf("error") != -1) {
            return evalXml(response, "/error/errorId");
        }
        return evalXml(response, "/exception/exceptionId");
    }

    public String getErrorMessage(String response) {
        if (response.indexOf("error") != -1) {
            return evalXml(response, "/error/errorMessage");
        }
        return evalXml(response, "/exception/exceptionMessage");
    }

    public String evalXml(String xml, String path) {
        XPath xpath = xpathFactory.newXPath();
        InputSource source = new InputSource(new StringReader(xml));
        String result = null;
        try {
            result = xpath.evaluate(path, source);
        } catch (XPathExpressionException ex) {
        }
        return result;
    }

    /**
     * Fast xml element check
     * @param response xml response
     * @param element element name to find
     * @return <code>true</code> id a pair of xml element tags is withing the xml response
     */
    public boolean hasXmlElement(String response,String element) {
        String beginElement = "<" + element + ">";
        String endElement = "</" + element + ">";
        int beginIdx = response.indexOf(beginElement);
        int endIdx = response.indexOf(endElement);
        return beginIdx >= 0 && beginIdx < endIdx;
    }
    
    public void addLocale(Map<String, String> params) {
        if (!params.containsKey("locale") && locale != null) {
            params.put("locale", locale.toString());
        }
    }


    protected String readString(InputStream in) throws IOException {
        return IOHelper.readString(in, DEFAULT_ENCODING);
    }

    private long getSystemTime() {
        return System.currentTimeMillis();
    }
}

