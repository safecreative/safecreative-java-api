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

import java.net.URL;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.safecreative.api.SafeCreativeAPI.AuthkeyLevel;

/**
 * SafeCreativeAPI low level tests
 * @author mpolo@safecreative.org
 */
public class SafeCreativeAPITest {
    private static SafeCreativeAPI api;
    
    private String authkey = "";
    private String privatekey = "";

    public SafeCreativeAPITest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        SafeCreativeAPITestProperties testProperties = SafeCreativeAPITestProperties.getInstance();
        api = new SafeCreativeAPI(testProperties.getSharedKey(), testProperties.getPrivateKey());
        api.setBaseUrl(testProperties.getBaseUrl());
    }

    /**
     * Test of getManageAuthkeyUrl method, of class SafeCreativeAPI.
     */
    @Before
    public void createAuth() throws Exception {
        System.out.println("createAuth");
        Map params = api.createParams("component", "authkey.create", "sharedkey", api.getSharedKey());
        String result = api.callSigned(params, true, false);
        assertNotNull(result);
        System.out.println("Result: "+ result);
        if(api.isError(result)) {
            throw new ApiException("authkey.create error. Result: " + result);
        }
        authkey = api.evalXml(result, "/authkeycreate/authkey");
        privatekey = api.evalXml(result, "/authkeycreate/privatekey");
        if (authkey == null || privatekey == null) {
            throw new ApiException("authkey.create error. Result: " + result);
        }
    }

    /**
     * Test of getManageAuthkeyUrl method, of class SafeCreativeAPI.
     */
    @Test
    public void testGetManageAuthkeyUrl() throws Exception {
        System.out.println("getManageAuthkeyUrl");
        AuthkeyLevel level = AuthkeyLevel.MANAGE;
        String result = api.getManageAuthkeyUrl(authkey, privatekey, level);
        assertNotNull(result);
        System.out.println("Result: "+ new URL(result));
    }

    /**
     * Test of getNonceKey method, of class SafeCreativeAPI.
     */
    @Test
    public void testGetNonceKey() {
        System.out.println("getNonceKey");                   
        String result = api.getNonceKey(authkey);
        assertNotNull(result);
        System.out.println("Result: "+ result);
    }

    /**
     * Test of getAuthKeyState method, of class SafeCreativeAPI.
     */
    @Test
    public void testGetAuthKeyState() {
        System.out.println("getAuthKeyState");
        String result = api.getAuthKeyState(authkey);
        assertNotNull(result);
        System.out.println("Result: "+ result);
    }

    /**
     * Test of getZTime method, of class SafeCreativeAPI.
     */
    @Test
    public void testGetZTime() {
        System.out.println("getZTime");        
        String result = api.getZTime();
        assertTrue(StringUtils.isNumeric(result));
        long ztime = Long.parseLong(result);
        assertTrue(ztime > 0);
    }

    /**
     * Test of createParams method, of class SafeCreativeAPI.
     */
    @Test
    public void testCreateParams() {
        System.out.println("createParams");        
        Map result = api.createParams("key","value");
        assertTrue(result.containsKey("key"));
        assertEquals("value", result.get("key"));
    }

    /**
     * Test of isError method, of class SafeCreativeAPI.
     */
    @Test
    public void testIsError() {
        System.out.println("isError");
        assertEquals(false, api.isError(""));
    }

    /**
     * Test of getErrorCode method, of class SafeCreativeAPI.
     */
    @Test
    public void testGetErrorCode() {
        System.out.println("getErrorCode");
        String response = "<error><errorId>code</errorId></error>";
        String expResult = "code";
        String result = api.getErrorCode(response);
        assertEquals(expResult, result);
        response = "<exception><exceptionId>code</exceptionId></exception>";
        result = api.getErrorCode(response);
        assertEquals(expResult, result);
    }

    /**
     * Test of getErrorMessage method, of class SafeCreativeAPI.
     */
    @Test
    public void testGetErrorMessage() {
        System.out.println("getErrorMessage");
        String response = "<error><errorId>code</errorId><errorMessage>message</errorMessage></error>";
        String expResult = "message";
        String result = api.getErrorMessage(response);
        assertEquals(expResult, result);
        response = "<exception><exceptionId>code</exceptionId><exceptionMessage>message</exceptionMessage></exception>";
        result = api.getErrorMessage(response);
        assertEquals(expResult, result);
    }

    /**
     * Test of hasXmlElement method, of class SafeCreativeAPI.
     */
    @Test
    public void testHasXmlElement() {
        System.out.println("hasXmlElement");
        String response = "<error><errorId>code</errorId><errorMessage>message</errorMessage></error>";
        assertTrue(api.hasXmlElement(response, "error"));
        assertTrue(api.hasXmlElement(response, "errorId"));
        assertTrue(api.hasXmlElement(response, "errorMessage"));
        assertFalse(api.hasXmlElement(response, "notfound"));
        assertFalse(api.hasXmlElement("<test>test", "test"));
        assertFalse(api.hasXmlElement("</test><test>", "test"));
        assertTrue(api.hasXmlElement("<test>test</test>", "test"));
    }

    /**
     * Test of evalXml method, of class SafeCreativeAPI.
     */
    @Test
    public void testEvalXml() {
        System.out.println("evalXml");
        String xml = "<rest><restValue>test</restValue></rest>";
        String path = "/rest/restValue";
        String expResult = "test";
        String result = api.evalXml(xml, path);
        assertEquals(expResult, result);
    }


}