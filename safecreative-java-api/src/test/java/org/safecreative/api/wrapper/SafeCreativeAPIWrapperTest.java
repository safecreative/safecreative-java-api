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
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.safecreative.api.ApiException;
import org.safecreative.api.SafeCreativeAPI;
import static org.junit.Assert.*;
import static org.junit.Assume.*;
import org.safecreative.api.SafeCreativeAPI.AuthkeyLevel;
import org.safecreative.api.SafeCreativeAPITestProperties;
import org.safecreative.api.util.IOHelper;
import org.safecreative.api.wrapper.converters.WorkConverter;
import org.safecreative.api.wrapper.model.AuthKey;
import org.safecreative.api.wrapper.model.AuthKeyState;
import org.safecreative.api.wrapper.model.Country;
import org.safecreative.api.wrapper.model.DownloadInfo;
import org.safecreative.api.wrapper.model.License;
import org.safecreative.api.wrapper.model.LicenseFeatureObject;
import org.safecreative.api.wrapper.model.User;
import org.safecreative.api.wrapper.model.UserLink;
import org.safecreative.api.wrapper.model.UserQuota;
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
        assertEquals(testProperties.getBaseSearchUrl(), result);
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
        assertEquals(testProperties.getBaseUrl(), result);
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
     * Test of getWorkTypesTree method, of class SafeCreativeAPIWrapper.
     */
    @Test
    public void testGetWorkTypesTree() throws Exception {
        System.out.println("getWorkTypesTree");
        List<Work.TypeGroup> result = instance.getWorkTypesTree();
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
     * Test of getLicenses method, of class SafeCreativeAPIWrapper.
     */
    @Test
    public void testGetLicenses() throws Exception {
        System.out.println("getLicenses");
        List<License> result = instance.getLicenses().getList();
        assertNotNull(result);
        assertFalse(result.isEmpty());
        System.out.println("Result: "+ result);
    }

    /**
     * Test of getLicenseFeatures method, of class SafeCreativeAPIWrapper.
     */
    @Test
    public void testGetLicenseFeatures() throws Exception {
        System.out.println("getLicenseFeatures");
        EnumMap<License.Feature, LicenseFeatureObject> result = instance.getLicenseFeatures();
        assertNotNull(result);
        assertFalse(result.isEmpty());

        License.Feature feature = License.Feature.COMMERCIAL;
        assertNotNull(result.get(feature).getCode());
        assertNotNull(result.get(feature).getShortName());
        
        boolean someIsTrue = false;
        for (License.FeatureValue value : License.FeatureValue.values()) {
            if (result.get(feature).getUseValues().get(value)) {
                someIsTrue = true;
                break;
            }
        }

        // has read some values
        assertTrue(someIsTrue);
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
        xs.registerConverter(new WorkConverter());
        String xml = IOHelper.readString(getClass().getResourceAsStream("/work.get.xml"));
        System.out.println("work response "+xml);
        Work work = instance.readObject(Work.class, xml,xs);
        assertNotNull(work);
        assertTrue(SafeCreativeAPI.isValidCode(work.getCode()));
        assertNotNull(work.getLinks());
        assertEquals("http://www.flickr.com/photos/mrmx/3141714714/",work.getLinks().get(0).getUrl().toExternalForm());
        assertEquals("image/jpeg", work.getMimeType());
        assertEquals("0804290061501", work.getAuthors().get(0).getCode());
        assertEquals("0804290061501", work.getRightHolders().get(0).getCode());
        assertEquals("0907030200236", work.getRelations(Work.RelationType.RELATED).get(0).getCode());
        assertEquals("0907030200236", work.getRelations(Work.RelationType.VERSION).get(0).getCode());
        assertEquals("0907030200236", work.getRelations(Work.RelationType.DERIVATION).get(0).getCode());
        assertEquals(2,work.getRelations(Work.RelationType.COMPOSITION).size());
        assertEquals("1702034400231", work.getRelations(Work.RelationType.COMPOSITION).get(0).getCode());
        assertEquals("0902030200236", work.getRelations(Work.RelationType.COMPOSITION).get(1).getCode());
        assertTrue(work.isAllowDownload());
        assertTrue(work.isAllowLicensing());
        assertFalse(work.isAllowSale());
        assertEquals("EN",work.getLanguage().getCode());
    }

    /**
     * Test of getWorkPrivate method, of class SafeCreativeAPIWrapper.
     */
    @Test
    public void testGetWorkPrivate() throws Exception {
        System.out.println("getWorkPrivate");
        XStream xs = new XStream();
        xs.registerConverter(new WorkConverter());
        String xml = IOHelper.readString(getClass().getResourceAsStream("/work.get.private.xml"));
        System.out.println("work response "+xml);
        Work work = instance.readObject(Work.class, xml,xs);
        assertNotNull(work);
        assertTrue(SafeCreativeAPI.isValidCode(work.getCode()));
        assertNotNull(work.getLinks());
        assertEquals("http://www.flickr.com/photos/mrmx/3141714714/",work.getLinks().get(0).getUrl().toExternalForm());
        assertEquals("image/jpeg", work.getMimeType());
        assertTrue(work.isAllowDownload());
        assertTrue(work.isAllowLicensing());
        assertFalse(work.isAllowSale());
        assertEquals("EN",work.getLanguage().getCode());
        assertEquals(Work.WorkState.REGISTERED, work.getState());
        assertEquals("test observations", work.getObservations());
        assertTrue(work.isRegistryPublic());
        assertTrue(work.isUseAlias());
        assertTrue(work.isUserAuthor());
        assertTrue(work.isUserRights());
        assertEquals("batman", work.getUserAlias());
    }

    /**
     * Test of getWorkList method, of class SafeCreativeAPIWrapper.
     */
    @Test
    public void testGetWorkList() throws Exception {
        System.out.println("getWorkList");
        
        // check keys
        AuthKeyState state = instance.checkAuth(instance.getAuthKey());

        assumeTrue(state.isAuthorized());

        ListPage<Work> results = instance.getWorkList();

        assertNotNull(results);
        assertFalse(results.getList().isEmpty());
        System.out.println("Result: "+ results);
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
		boolean atleastOneDownload = false;
		for(Work work : results.getList()) {
			System.out.println("Getting download url of work: "+ work);
			try {
				DownloadInfo result = instance.getWorkDownload(work.getCode(), false);
				assertNotNull(result);
				atleastOneDownload = true;
				System.out.println("Result: "+ result);
				break;
			}catch(ApiException ex){
				if(!"NotAuthorized".equals(ex.getErrorCode())) {
					throw ex;
				}				
			}
		}
		assertTrue(atleastOneDownload);
    }

    /**
     * Test of getUser method, of class SafeCreativeAPIWrapper.
     */
    @Test
    public void testGetUser() throws Exception {
        System.out.println("getUser");
        User result = instance.getUser(testProperties.getUserCode());
        assertNotNull(result);
        //assertFalse(result.isEmpty());
        System.out.println("Result: "+ result);
    }

    /**
     * Test of getUserQuota method, of class SafeCreativeAPIWrapper.
     */
    @Test
    public void testGetUserQuota() throws Exception {
        System.out.println("getUserQuota");

        // check keys
        AuthKeyState state = instance.checkAuth(instance.getAuthKey());

        assumeTrue(state.isAuthorized());
        
        UserQuota result = instance.getUserQuota();
        assertNotNull(result);
        assertEquals(testProperties.getUserCode(), result.getUserCode());
        System.out.println("Result: " + result);
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

    /**
     * Locale parameter should not be passed to components that dont require locale
     */
    @Test
    public void testLocaleNotRequired() throws Exception {
        System.out.println("LocaleNotRequired");

        instance.setLocale(Locale.ENGLISH);
        try {
            testCheckAuth_AuthKey();
            setUp();
            testWorkRegister();
        } catch (ApiException ex) {
            fail(ex.toString());
        }

        List<Country> result = instance.getCountries();
        assertNotNull(result);
        assertFalse(result.isEmpty());
        System.out.println("Result: "+ result);
    }

    /**
     * Test of workDelete method, of class SafeCreativeAPIWrapper.
     */
    @Test
    public void testWorkDelete() throws Exception {
        System.out.println("workDelete");

        // check keys
        AuthKeyState state = instance.checkAuth(instance.getAuthKey());
        assumeTrue(state.isAuthorized());
        assumeTrue(state.getLevel() == AuthkeyLevel.MANAGE);

	    File file = testProperties.getUploadFile();
	    assumeTrue(file.exists());
	    String workCode;

        try {
            workCode = instance.workRegister("Test registered file", file, null, null);
            System.out.println("Registered work code: " + workCode);
            Work work = instance.getWorkPrivate(workCode);
            assumeNotNull(work);
            assertTrue(instance.workDelete(workCode));
            assertFalse(instance.getWorkList().getList().contains(work));
        } catch (ApiException ex) {}



    }

    /**
     * Test of workRegister method, of class SafeCreativeAPIWrapper.
     */
    @Test
    public void testWorkRegister() throws Exception {
        System.out.println("workRegister");

        // check keys
        AuthKeyState state = instance.checkAuth(instance.getAuthKey());
        assumeTrue(state.isAuthorized());
        assumeTrue(state.getLevel() == AuthkeyLevel.ADD || state.getLevel() == AuthkeyLevel.MANAGE);

	    File file = testProperties.getUploadFile();
	    assumeTrue(file.exists());
	    String workCode;

        workCode = instance.workRegister("Test registered work", file, null, null);
        System.out.println("Registered work code: " + workCode);
        assertNotNull(instance.getWorkPrivate(workCode));

        // try to delete work
        try {
            instance.workDelete(workCode);
        } catch (ApiException ex) {}
    }

    /**
     * Test of workUpdate method, of class SafeCreativeAPIWrapper.
     */
    @Test
    public void testWorkUpdate() throws Exception {
        System.out.println("workUpdate");

        // check keys
        AuthKeyState state = instance.checkAuth(instance.getAuthKey());
        assumeTrue(state.isAuthorized());
        assumeTrue(state.getLevel() == AuthkeyLevel.ADD || state.getLevel() == AuthkeyLevel.MANAGE);

	    File file = testProperties.getUploadFile();
	    assumeTrue(file.exists());
	    String workCode;

        workCode = instance.workRegister("Test registered work", file, null, null);
        System.out.println("Registered work code: " + workCode);

        String newTitle = "the new title";
        Work work = new Work();
        work.setCode(workCode);
        work.setTitle(newTitle);

        instance.workUpdate(work);
        assertEquals(instance.getWorkPrivate(workCode).getTitle(), newTitle);

        // try to delete work
        try {
            instance.workDelete(workCode);
        } catch (ApiException ex) {}
    }
}