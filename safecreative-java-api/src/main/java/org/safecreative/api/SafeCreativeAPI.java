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

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.apache.commons.lang.StringUtils;
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
    
    public static final String STATE_REGISTERED 		= "REGISTERED";
	public static final String STATE_PRE_REGISTERED 	= "PRE_REGISTERED";
	public static final String NOT_AUTHORIZED_ERROR		= "NotAuthorized";
    
	private static Logger log = LoggerFactory.getLogger(SafeCreativeAPI.class);
    private String baseUrl;
    private char[] charBuf;
    private Long timeOffset;
    private XPathFactory xpathFactory;
    private String sharedKey, privateKey;
    private String authKey, privateAuthKey;

    public enum AuthkeyLevel {
        GET, ADD, MANAGE
    }

    public enum RelationType {
        COMPOSITION,DERIVATION,VERSION,RELATED
    }

    public enum LinkType {
        INFO,DOWNLOAD
    }


    public SafeCreativeAPI(String sharedKey, String privateKey) {
        this.sharedKey = sharedKey;
        this.privateKey = privateKey;
        this.xpathFactory = XPathFactory.newInstance();
        charBuf = new char[8192];
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

    public String getManageAuthkeyUrl(String authkey, String privatekey, AuthkeyLevel level) {
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

    public String getAuthKeyState(String authKey) {
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
    public Map<String, String> createParams(String... values) {
        if (values != null && values.length % 2 != 0) {
            throw new IllegalArgumentException("odd value array size");
        }
        Map<String, String> result = new HashMap<String, String>();
        if (values != null) {
            int size = values.length;
            for (int i = 0; i < size; i += 2) {
                result.put(values[i], values[i + 1]);
            }
        }
        return result;
    }

    public String callSigned(Map<String, String> params, boolean ztime, boolean noncekey) {
        return callSigned(params, privateKey, ztime, noncekey);
    }

    public String callSigned(Map<String, String> params, String privateKey, boolean ztime, boolean noncekey) {
        if (ztime) {
            params.put("ztime", getZTime());
        }
        if (noncekey) {
            params.put("noncekey", getNonceKey(authKey));
        }
        return call(signParams(params, privateKey));
    }

    public String call(Map<String, String> params) {
        StringBuilder encoded = new StringBuilder();
        for (String param : params.keySet()) {
            String value = params.get(param);
            try {
                encoded.append("&" + param + "=" + URLEncoder.encode(value, DEFAULT_ENCODING));
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
            closeQuietly(os);
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
            if(value == null) {
                log.debug("null param {}",key);
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
		return StringUtils.isEmpty(result) || result.indexOf("error") != -1 || result.indexOf("exception") != -1 ;
	}

    public String getErrorCode(String response) {
        if(response.indexOf("error") != -1) {
            return evalXml(response, "/error/errorId");
        } 
        return evalXml(response, "/exception/exceptionId");
    }

    public String getErrorMessage(String response)  {
        if(response.indexOf("error") != -1) {
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

    protected String readString(InputStream in) throws IOException {
        return readString(new InputStreamReader(in), DEFAULT_ENCODING);
    }

    protected String readString(Reader in, String charset) throws IOException {
        int len = 0;
        StringWriter out = new StringWriter();
        try {
            while ((len = in.read(charBuf)) != -1) {
                out.write(charBuf, 0, len);
            }
        } finally {
            closeQuietly(in);
            closeQuietly(out);
        }
        return out.toString();
    }

    protected void closeQuietly(Closeable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private long getSystemTime() {
        return System.currentTimeMillis();
    }




}

