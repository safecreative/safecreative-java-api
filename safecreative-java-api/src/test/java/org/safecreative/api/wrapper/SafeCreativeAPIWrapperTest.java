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

import com.thoughtworks.xstream.XStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.safecreative.api.ApiException;
import org.safecreative.api.SafeCreativeAPI;
import static org.junit.Assert.*;
import org.safecreative.api.SafeCreativeAPI.AuthkeyLevel;
import org.safecreative.api.SafeCreativeAPITestProperties;
import org.safecreative.api.util.IOHelper;
import org.safecreative.api.wrapper.converters.WorkEntryConverter;
import org.safecreative.api.wrapper.model.AuthKey;
import org.safecreative.api.wrapper.model.AuthKeyState;
import org.safecreative.api.wrapper.model.Country;
import org.safecreative.api.wrapper.model.UserLink;
import org.safecreative.api.wrapper.model.Work;

/**
 *
 * @author mpolo
 */
public class SafeCreativeAPIWrapperTest {
    private static SafeCreativeAPIWrapper instance;
    private static SafeCreativeAPITestProperties testProperties;

    @BeforeClass
    public static void setUpClass() throws Exception {
        testProperties = SafeCreativeAPITestProperties.getInstance();
        instance = new SafeCreativeAPIWrapper(testProperties.getSharedKey(), testProperties.getPrivateKey());
        instance.setBaseUrl(testProperties.getBaseUrl());
        instance.setBaseSearchUrl(testProperties.getBaseSearchUrl());
        try {
            instance.setAuthKey(testProperties.getAuthKey(),testProperties.getAuthPrivateKey());
        }catch(Exception ex) {
            AuthKey authKey = instance.createAuth(AuthkeyLevel.MANAGE);
            System.out.println(
                    "Created AuthKey\nauthKey="+authKey.getAuthkey()+
                    "\nauthPrivateKey="+authKey.getPrivatekey()+
                    "\nGoto manage url:"+authKey.getManageUrl()+
                    "\nand save auth/PrivateKey keys in "+SafeCreativeAPITestProperties.PROPERTY_FILE);
            throw ex;
        }
        
    }

    @Before
    public void setUp() throws Exception {
        instance.setBaseUrl(testProperties.getBaseUrl());
        instance.setBaseSearchUrl(testProperties.getBaseSearchUrl());
        instance.setAuthKey(testProperties.getAuthKey(),testProperties.getAuthPrivateKey());
    }


    /**
     * Test of getBaseSearchUrl method, of class SafeCreativeAPIWrapper.
     */
    @Test
    public void testGetBaseSearchUrl() throws MalformedURLException  {
        System.out.println("getBaseSearchUrl");
        String result = instance.getBaseSearchUrl();
        assertNotNull(result);
        System.out.println("Result: "+ new URL(result));
    }


    /**
     * Test of getBaseUrl method, of class SafeCreativeAPIWrapper.
     */
    @Test
    public void testGetBaseUrl() throws MalformedURLException {
        System.out.println("getBaseUrl");
        String result = instance.getBaseUrl();
        assertNotNull(result);
        System.out.println("Result: "+ new URL(result));
    }

    /**
     * Test of getVersion method, of class SafeCreativeAPIWrapper.
     */
    @Test
    public void testGetVersion() throws Exception {
        System.out.println("getVersion");
        String result = instance.getVersion();
        assertNotNull(result);        
        assertTrue(StringUtils.isNotBlank(result));
        System.out.println("Result: "+ result);
    }

    /**
     * Test of createAuth method, of class SafeCreativeAPIWrapper.
     */
    @Test
    public void testCreateAuth() throws Exception {
        System.out.println("createAuth");
        AuthKey result = instance.createAuth();
        assertNotNull(result);
        System.out.println("Result: "+ result);
    }

    /**
     * Test of checkAuth method, of class SafeCreativeAPIWrapper.
     */
    @Test
    public void testCheckAuth_AuthKey() throws Exception {
        System.out.println("checkAuth");
        AuthKey authKey = instance.createAuth();
        AuthKeyState result = instance.checkAuth(authKey);
        assertNotNull(result);
        assertTrue(!result.isAuthorized());
        System.out.println("Result: "+ result);
    }

    /**
     * Test of checkAuth method, of class SafeCreativeAPIWrapper.
     */
    @Test
    public void testCheckAuth_String() throws Exception {
        System.out.println("checkAuth");
        String authKey = instance.createAuth().getAuthkey();
        AuthKeyState result = instance.checkAuth(authKey);
        assertNotNull(result);
        assertTrue(!result.isAuthorized());
        System.out.println("Result: "+ result);
    }


    /**
     * Test of createAuth method, of class SafeCreativeAPIWrapper.
     */
    @Test
    public void testCreateAuth_SafeCreativeAPIAuthkeyLevel() throws Exception {
        System.out.println("createAuth");
        for(AuthkeyLevel level : AuthkeyLevel.values()) {
            AuthKey result = instance.createAuth(level);
            assertNotNull(result);
            System.out.println("Result: "+ result);
        }
    }

    /**
     * Test of getWorkTypes method, of class SafeCreativeAPIWrapper.
     */
    @Test
    public void testGetWorkTypes() throws Exception {
        System.out.println("getWorkTypes");
        List<Work.Type> result = instance.getWorkTypes();
        assertNotNull(result);
        assertFalse(result.isEmpty());
        System.out.println("Result: "+ result);
    }

    /**
     * Test of getWorkLanguages method, of class SafeCreativeAPIWrapper.
     */
    @Test
    public void testGetWorkLanguage() throws Exception {
        System.out.println("getWorkLanguages");
        List<Work.Language> result = instance.getWorkLanguages();
        assertNotNull(result);
        assertFalse(result.isEmpty());
        System.out.println("Result: "+ result);
    }

    /**
     * Test of getCountries method, of class SafeCreativeAPIWrapper.
     */
    @Test
    public void testGetCountries() throws Exception {
        System.out.println("getCountries");
        List<Country> result = instance.getCountries();
        assertNotNull(result);
        assertFalse(result.isEmpty());
        System.out.println("Result: "+ result);
    }

    /**
     * Test of setLocale method, of class SafeCreativeAPIWrapper.
     */
    @Test
    public void testSetLocale() throws Exception {
        System.out.println("setLocale");
        //Use production:
        instance.setBaseUrl(SafeCreativeAPIWrapper.DEFAULT_API_URL); 
        instance.setLocale(Locale.ENGLISH);
        //Use a basic localized list:
        List<Work.Language> resultEN = instance.getWorkLanguages();
        assertNotNull(resultEN);
        assertFalse(resultEN.isEmpty());
        System.out.println("Result EN: "+ resultEN);
        for(Work.Language language : resultEN) {
            if("ES".equals(language.getCode())) {
                assertEquals("Spanish", language.getName());
                break;
            }
        }
        //Check list in Spanish
        instance.setLocale(new Locale("es"));
        List<Work.Language> resultES = instance.getWorkLanguages();
        assertNotNull(resultES);
        assertFalse(resultES.isEmpty());
        System.out.println("Result ES: "+ resultES);
        for(Work.Language language : resultES) {
            if("ES".equals(language.getCode())) {
                assertEquals("Español", language.getName());
                break;
            }
        }
    }

    /**
     * Test of getWork method, of class SafeCreativeAPIWrapper.
     */
    @Test
    public void testGetWork() throws Exception {
        System.out.println("getWork");
        XStream xs = new XStream();
        xs.registerConverter(new WorkEntryConverter());
        String xml = IOHelper.readString(getClass().getResourceAsStream("/work.get.xml"));
        System.out.println("work response "+xml);
        Work work = instance.readObject(Work.class, xml,xs);
        assertNotNull(work);
        assertTrue(SafeCreativeAPI.isValidCode(work.getCode()));
    }

    /**
     * Test of getWorkDownload method, of class SafeCreativeAPIWrapper.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testWorkDownload() throws Exception {
        System.out.println("getWorkDownload");
        //Find any public downloadable works        
        ListPage<Work> results = instance.searchWorksByFields(
                SearchMethod.DOWNLOADABLE,true
        );
        assertNotNull(results);
        assertTrue(results.getSize() > 0);
        Work work = results.getList().get(0);
        System.out.println("Getting download url of work: "+ work);
        URL result = instance.getWorkDownload(work.getCode(), false);
        assertNotNull(result);
        System.out.println("Result: "+ result);
    }

    /**
     * Test of un/linkUser methods, of class SafeCreativeAPIWrapper.
     */
    @Test
    public void testUserLinkingUnLinking() throws Exception {
        if(!testProperties.isPartner()) return;
        System.out.println("userLink");
        UserLink userLink = instance.userLink("alice@wonderland.tv", AuthkeyLevel.GET, "Alice", "Wander", "Wonders");
        assertNotNull(userLink);
        assertTrue(SafeCreativeAPI.isValidCode(userLink.getCode()));
        System.out.println("userUnLink");
        assertTrue(instance.userUnLink(userLink.getCode()));
    }

    /**
     * Test of searchWorksByFields method, of class SafeCreativeAPIWrapper.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testSearchWorksByFields() throws Exception {
        System.out.println("searchWorksByFields");
        ListPage<Work> results = instance.searchWorksByFields(
                SearchMethod.WORK_TYPE,Work.Type.PHOTO,
                SearchMethod.DOWNLOADABLE,true
        );
        assertNotNull(results);
        assertTrue(results.getSize() > 0);
        //Last page
        results = instance.searchWorksByFields(results.getPageTotal(),
                SearchMethod.WORK_TYPE,Work.Type.PHOTO,
                SearchMethod.DOWNLOADABLE,true
        );
        assertNotNull(results);
        assertTrue(results.getSize() > 0);
        //Out of bounds page
        results = instance.searchWorksByFields(results.getPageTotal()+1,
                SearchMethod.WORK_TYPE,Work.Type.PHOTO,
                SearchMethod.DOWNLOADABLE,true
        );
        assertNotNull(results);
        assertTrue(results.getSize() == 0);
    }

    /**
     * Test of searchWorksByQuery method, of class SafeCreativeAPIWrapper.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testSearchWorksByQuery() throws Exception {
        System.out.println("searchWorksByQuery");
        ListPage<Work> results = instance.searchWorksByQuery(1,"Mario Pena");
        assertNotNull(results);
        assertTrue(results.getSize() > 0);
        instance.setBaseSearchUrl(SafeCreativeAPIWrapper.DEFAULT_API_SEARCH_URL); //Use prod
        results = instance.searchWorksByQuery(1,SearchMethod.USER_NAME+":magnatune");
        assertNotNull(results);
        assertTrue(results.getSize() > 0);
    }
    
    /**
     * Test of searchWorksByHashMD5 method, of class SafeCreativeAPIWrapper.
     */
    @Test
    public void testSearchWorksByHashMD5() throws Exception {
        System.out.println("searchWorksByHashMD5");
        instance.setBaseUrl(SafeCreativeAPIWrapper.DEFAULT_API_URL); //Use prod
        ListPage<Work> results = instance.searchWorksByHashMD5("22f5ce4f4bb5f49625b664927d5854d8");
        assertNotNull(results);
        assertTrue(results.getSize() == 1);
        System.out.println("Result: "+ results.getList().get(0));
    }

    /**
     * Test of searchWorksByContent method, of class SafeCreativeAPIWrapper.
     */
    @Test
    public void testSearchWorksByContent() throws Exception {
        System.out.println("searchWorksByContent");
        instance.setBaseSearchUrl(SafeCreativeAPIWrapper.DEFAULT_API_SEARCH_URL); //Use prod
        List<Work> results = instance.searchWorksByContent(SearchMethod.WORK_CNT_MD5,"22f5ce4f4bb5f49625b664927d5854d8");
        assertNotNull(results);
        assertTrue(results.size() == 1);
        System.out.println("Result: "+ results.get(0));
    }

    /**
     * Test of callComponent method, of class SafeCreativeAPIWrapper.
     */
    @Test
    public void testCallComponent() throws Exception {
        System.out.println("callComponent");
        String component = "version";        
        String result = instance.callComponent(component);
        assertNotNull(result);
        System.out.println("Result: "+ result);
    }

    /**
     * Test of checkReady method, of class SafeCreativeAPIWrapper.
     */
    @Test
    public void testCheckReady() throws Exception {
        System.out.println("checkReady");
        String response = "<restvalueresponse><state>ready</state></restvalueresponse>";
        boolean expResult = true;
        boolean result = instance.checkReady(response);
        assertEquals(expResult, result);
    }

    /**
     * Test of checkState method, of class SafeCreativeAPIWrapper.
     */
    @Test
    public void testCheckState() throws Exception {
        System.out.println("checkState");
        String response = "<restvalueresponse><state>continue</state></restvalueresponse>";
        String expected = "continue";
        boolean expResult = true;
        boolean result = instance.checkState(response, expected);
        assertEquals(expResult, result);
        assertFalse(instance.checkState(response, ""));
    }

    /**
     * Test of checkError method, of class SafeCreativeAPIWrapper.
     */
    @Test
    public void testCheckError() throws Exception {
        System.out.println("checkError");
        String response = "<error><errorId>code</errorId><errorMessage>message</errorMessage></error>";
        try {
            instance.checkError(response);
            fail("Expected ApiException");
        }catch(ApiException ex) {
            assertEquals("code",ex.getErrorCode() );
            assertEquals("message",ex.getMessage() );
        }
    }

}