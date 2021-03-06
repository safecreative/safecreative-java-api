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
import com.thoughtworks.xstream.converters.Converter;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.commons.httpclient.methods.multipart.ByteArrayPartSource;
import org.apache.commons.httpclient.methods.multipart.FilePartSource;
import org.apache.commons.httpclient.methods.multipart.PartSource;
import org.apache.commons.lang.StringUtils;
import org.safecreative.api.ApiException;
import org.safecreative.api.RegisterWork;
import org.safecreative.api.SafeCreativeAPI;
import org.safecreative.api.SafeCreativeAPI.AuthkeyLevel;
import org.safecreative.api.UploadProgressListener;
import org.safecreative.api.util.Digest;
import org.safecreative.api.wrapper.converters.DownloadInfoConverter;
import org.safecreative.api.wrapper.converters.LicenseConverter;
import org.safecreative.api.wrapper.converters.LicenseFeatureConverter;
import org.safecreative.api.wrapper.converters.ListPageConverter;
import org.safecreative.api.wrapper.converters.UserConverter;
import org.safecreative.api.wrapper.converters.WorkConverter;
import org.safecreative.api.wrapper.model.AuthKey;
import org.safecreative.api.wrapper.model.AuthKeyState;
import org.safecreative.api.wrapper.model.Country;
import org.safecreative.api.wrapper.model.DownloadInfo;
import org.safecreative.api.wrapper.model.License;
import org.safecreative.api.wrapper.model.LicenseFeatureObject;
import org.safecreative.api.wrapper.model.Link;
import org.safecreative.api.wrapper.model.Profile;
import org.safecreative.api.wrapper.model.User;
import org.safecreative.api.wrapper.model.UserLink;
import org.safecreative.api.wrapper.model.UserQuota;
import org.safecreative.api.wrapper.model.Work;
import org.safecreative.api.wrapper.util.ParamsBuilder;
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
    private final static String ERROR_USER_NOTFOUND = "UserNotFound";
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
     * Gets current authorization key pair
     *
     * @return the authKey key pair
     */
    public AuthKey getAuthKey() {
        return authKey;
    }

    /**
     * Sets current authorization key pair
     *
     * @param authKey the authKey key pair to set
     */
    public void setAuthKey(AuthKey authKey) {
        this.authKey = authKey;
        if (authKey != null) {
            api.setAuthKey(authKey.getAuthkey());
            api.setPrivateAuthKey(authKey.getPrivatekey());
        } else {
            api.setAuthKey(null);
            api.setPrivateAuthKey(null);
        }
    }

    /**
     * Sets current authorization key pair
     * @param authKey public auth key
     * @param authPrivateKey private auth key
     */
    public void setAuthKey(String authKey, String authPrivateKey) {
        setAuthKey(new AuthKey(authKey, authPrivateKey));
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
        if (state.isAuthorized()) {
            AuthkeyLevel level = AuthkeyLevel.valueOf(api.evalXml(result, "/authkeystate/level").toUpperCase());
            state.setLevel(level);
        }
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
        return createAuth(authkeyLevel, false);
    }   
    
    /**
     * Create a new authorization key
     * @param authkeyLevel api authorization access level
     * @param embeddable if <code>true</code> then allow remote key auth embeddable in popups etc.., 
     * otherwise default full navigation
     * @return authorization key
     * @throws ApiException
     */
    @SuppressWarnings("unchecked")
    public AuthKey createAuth(AuthkeyLevel authkeyLevel,boolean embeddable) throws ApiException {
        setApiUrl();
        Map params = api.createParams("component", "authkey.create", "sharedkey", api.getSharedKey());
        String result = api.callSigned(params, true, false, false);
        checkError(result);
        String auth = api.evalXml(result, "/authkeycreate/authkey");
        String authPrivate = api.evalXml(result, "/authkeycreate/privatekey");
        if (auth == null || authPrivate == null) {
            throw new ApiException("authkey.create error. Result: " + result);
        }
        authKey = new AuthKey();
        authKey.setAuthkey(auth);
        authKey.setPrivatekey(authPrivate);
        String manageUrl = api.getManageAuthkeyUrl(
                auth, authPrivate, 
                authkeyLevel == null ? AuthkeyLevel.MANAGE : authkeyLevel,
                embeddable
        );
        authKey.setManageUrl(manageUrl);        
        return authKey;
    }

    ////////////////////////////////////////////////////////////////////////////
    // User methods
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Get public user info
     * @param code
     * @return User or <code>null</code> if none found
     * @throws ApiException
     */
    public User getUser(String code) throws ApiException {
        setApiUrl();
        String result = null;
        try {
            result = callComponentSigned("user.get",getApi().getPrivateKey(),true,false,true, "code", code,"sharedkey",getApi().getSharedKey());
        } catch (ApiException ex) {
            if (ERROR_USER_NOTFOUND.equals(ex.getErrorCode())) {
                return null;
            }
            throw ex;
        }
        return readObject(User.class, result,new UserConverter());
    }

    /**
     * Get user quota info of currently authorized user
     *
     * @return UserQuota or <code>null</code> if not authorized
     * @throws ApiException
     */
    public UserQuota getUserQuota() throws ApiException {
        return getUserQuota(getAuthKey());
    }

    /**
     * Get user quota info
     *
     * @param authKey user's authKey to get quota
     * @return UserQuota or <code>null</code> if not authorized
     * @throws ApiException
     */
    public UserQuota getUserQuota(AuthKey authKey) throws ApiException {
        setApiUrl();
        String response = null;
        UserQuota quota = null;
        try {
            response = callComponentSigned("user.quota",getApi().getPrivateKey(),true,false,false,
                    "authkey", authKey.getAuthkey(),"sharedkey",getApi().getSharedKey());
        } catch (ApiException ex) {
            if (SafeCreativeAPI.NOT_AUTHORIZED_ERROR.equals(ex.getErrorCode())) {
                return null;
            }
            throw ex;
        }

        XStream xs = createXStream();
        xs.alias("userquota", UserQuota.class);
        xs.aliasField("usercode", UserQuota.class, "userCode");

        try {
            quota = (UserQuota) xs.fromXML(response);
        } catch (Exception e) {
            log.error("Parsing xml response", e);
        }
        return quota;
    }


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
    public UserLink userLink(String mail, AuthkeyLevel level,
            String firstName, String middleName, String lastName) throws ApiException {
        return userLink(mail, level, firstName, middleName, lastName, null, null, null, null, null);
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
     * @return user link information
     * @see UserLink
     * @throws ApiException
     */
    @SuppressWarnings("unchecked")
    public UserLink userLink(String mail, AuthkeyLevel level,
            String firstName, String middleName, String lastName,
            String addressline1, String addressline2,
            String addresszip, String addresscity, String addresscountry) throws ApiException {
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
        String result = api.callSigned(params, true, false, false);
        checkError(result);
        log.debug("user.link result:\n{}", result);
        XStream xs = createXStream();
        xs.aliasField("usercode", UserLink.class, "code");
        xs.aliasField("authkey", UserLink.class, "authKey");
        xs.aliasField("privatekey", UserLink.class, "authPrivateKey");
        return readObject(UserLink.class, result,xs);
    }

    /**
     * Modify user's data
     *
     * @param mail mail user's mail address
     * @param firstName
     * @param middleName
     * @param lastName
     * @param addressline1
     * @param addressline2
     * @param addresszip
     * @param addresscity
     * @param addresscountry
     * @return <code>true</code> on success
     * @throws ApiException
     */
    @SuppressWarnings("unchecked")
    public boolean userModify(String mail,
            String firstName, String middleName, String lastName,
            String addressline1, String addressline2,
            String addresszip, String addresscity, String addresscountry) throws ApiException {
        setApiUrl();
        checkAuthKey(authKey);
        Map params = api.createParams("component", "user.modify", "authKey", authKey.getAuthkey());
        params.put("mail", mail);
        params.put("firstname", firstName);
        params.put("middlename", middleName);
        params.put("lastname", lastName);
        params.put("addressline1", addressline1);
        params.put("addressline2", addressline2);
        params.put("addresszip", addresszip);
        params.put("addresscity", addresscity);
        params.put("addresscountry", addresscountry);
        String result = api.callSigned(params, true, false);
        checkError(result);
        log.debug("user.modify result:\n{}", result);
        return checkReady(result);
    }

    /**
     * Unlinks a user
     * @param userCode
     * @return
     * @throws ApiException
     */
    @SuppressWarnings("unchecked")
    public boolean userUnLink(String userCode) throws ApiException {
        setApiUrl();
        Map params = api.createParams("component", "user.unlink", "sharedkey", api.getSharedKey());
        params.put("usercode", userCode);
        String result = api.callSigned(params, true, false,false);
        checkError(result);
        log.debug("user.unlink result:\n{}", result);
        return checkReady(result);
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
        setApiUrl();
        checkAuthKey(authKey);
        String result = callComponentSigned("user.profiles",authKey,true,false,false);
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
        setApiUrl();
        checkAuthKey(authKey);
        String result = callComponentSigned("user.licenses",authKey,true,false,true,"page",page);
        return readListPage(result, License.class, new LicenseConverter());
    }

    /**
     * Get a map of the license features.
     *
     * @return Map of license features.
     * @throws ApiException
     */
    @SuppressWarnings("unchecked")
    public EnumMap<License.Feature, LicenseFeatureObject> getLicenseFeatures() throws ApiException {
        setApiUrl();
        String result = callComponent("license.features");
        XStream xs = createXStream();

        // alias for license features
        xs.registerConverter(new LicenseFeatureConverter());

        List<LicenseFeatureObject> features = readList(result, "features", "feature", LicenseFeatureObject.class, xs);
        log.debug("License features {}", features);

        // generate map
        EnumMap<License.Feature, LicenseFeatureObject> featuresMap =
                new EnumMap<License.Feature, LicenseFeatureObject>(License.Feature.class);
        for (LicenseFeatureObject feature : features) {
            featuresMap.put(feature.getFeature(), feature);
        }

        return featuresMap;
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
    public List<Work.TypeGroup> getWorkTypesTree() throws ApiException {
        setApiUrl();
        String result = callComponent("work.types.tree");
        XStream xs = createXStream();

        // alias for worktype
        xs.aliasField("worktypes", Work.TypeGroup.class, "workTypes");
        xs.alias("worktype", Work.Type.class);

        List<Work.TypeGroup> workTypeGroups = readList(result, "worktypegroups", "worktypegroup", Work.TypeGroup.class, xs);
        log.debug("Work Type Groups {}", workTypeGroups);
        return workTypeGroups;
    }

    @SuppressWarnings("unchecked")
    public List<Work.Language> getWorkLanguages() throws ApiException {
        setApiUrl();
        String result = callComponent("work.languages");
        List<Work.Language> workLanguages = readList(result, "worklanguages", "language", Work.Language.class);
        log.debug("Work Languages {}", workLanguages);
        return workLanguages;
    }

    @SuppressWarnings("unchecked")
    public List<Country> getCountries() throws ApiException {
        setApiUrl();
        String result = callComponent("user.countries");
        List<Country> countries = readList(result, "countries", "country", Country.class);
        log.debug("Countries {}", countries);
        return countries;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Work methods
    ////////////////////////////////////////////////////////////////////////////
    
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
        return readObject(Work.class, result,new WorkConverter());
    }

    /**
     * Get private work info
     * @param code
     * @return Work or <code>null</code> if none found
     * @throws ApiException
     */
    public Work getWorkPrivate(String code) throws ApiException {
        return getWorkPrivate(code, getAuthKey());
    }

    /**
     * Get private work info
     * @param code
     * @param authKey authKey user authorization key
     * @return Work or <code>null</code> if none found
     * @throws ApiException
     */
    public Work getWorkPrivate(String code, AuthKey authKey) throws ApiException {
        setApiUrl();
        checkAuthKey(authKey);
        String result = null;
        try {
            result = callComponentSigned("work.get.private", authKey, true, false, true, "code", code);
        } catch (ApiException ex) {
            if (ERROR_WORK_NOTFOUND.equals(ex.getErrorCode())) {
                return null;
            }
            throw ex;
        }        
        return readObject(Work.class, result,new WorkConverter());
    }

    /**
     * Gets user's registered works list
     *
     * @return List of user's registered works
     * @throws ApiException
     */
    public ListPage<Work> getWorkList() throws ApiException {
        return getWorkList(1, getAuthKey());
    }

    /**
     * Gets user's registered works list
     *
     * @param page page number
     * @return List of user's registered works
     * @throws ApiException
     */
    public ListPage<Work> getWorkList(int page) throws ApiException {
        return getWorkList(page, getAuthKey());
    }

    /**
     * Gets user's registered works list
     *
     * @param page page number
     * @param authKey authKey user authorization key
     * @return List of user's registered works
     * @throws ApiException
     */
    public ListPage<Work> getWorkList(int page, AuthKey authKey) throws ApiException {
        setApiUrl();
        checkAuthKey(authKey);
        
        String result = callComponentSigned("work.list", authKey, true, false, true, "page", String.valueOf(page));
        ListPage<Work> results = readWorkListPage(result);
        return results;
    }

    /**
     * Deletes a work
     * @param code work's registry code
     * @return <code>true</code> on success
     * @throws ApiException
     */
    public boolean workDelete(String code) throws ApiException {
        setApiUrl();        
        String result = callComponentSigned("work.delete", getAuthKey(), true, false, false, "code",code);
        return checkReady(result);
    }

    /**
     * Gets a work's download info.
     * To access this URL the work must be in REGISTERED state.
     *
     * @param code work's registry code
     * @param owner <code>true</code> if user is owner else
     * you can get the URL to download any downloadable (Registered with public
     * access and allow download) work
     * @return download info
     * @throws ApiException
     */
    public DownloadInfo getWorkDownload(String code,boolean owner) throws ApiException {
        return getWorkDownload(code, owner, true);
    }

    /**
     * Gets a work's download info.
     * To access this URL the work must be in REGISTERED state.
     *
     * @param code work's registry code
     * @param owner <code>true</code> if user is owner else
     * you can get the URL to download any downloadable (Registered with public
     * access and allow download) work
     * @return download info
     * @throws ApiException
     */
    public DownloadInfo getWorkDownload(String code,boolean owner, boolean isAttachment) throws ApiException {
        setApiUrl();
        String result;
        if(owner) {
            checkAuthKey(authKey);
            result = callComponentSigned(
                "work.download.private" ,authKey,true,false,false,
                "code",code);
        } else {
            result = callComponentSigned(
                "work.download" ,getApi().getPrivateKey(),true,false,false,
                "sharedkey",getApi().getSharedKey(),"code",code,"isattachment",isAttachment);            
        }        
        return readObject(DownloadInfo.class, result, new DownloadInfoConverter());
    }

    ////////////////////////////////////////////////////////////////////////////
    // Registration methods
    ////////////////////////////////////////////////////////////////////////////

	// by text
	public String workRegister(String title,String content,Profile profile,List<String> tags,Link...links) throws ApiException {
		Map<String, String> params = api.createParams();
		params.put("text", content);
		if(tags != null) {
			StringBuilder sb = new StringBuilder();
			boolean hasMore = false;
			for(String tag:tags) {
				if(hasMore) {
					sb.append(',');
				}
				sb.append(tag.trim());
				hasMore = true;
			}
			params.put("tags", sb.toString());
		}
		int linkCounter = 1;
		for(Link link : links) {
			params.put("link"+linkCounter++,ParamsBuilder.linkString(link));
		}
		return workRegister(params, createWork(title), profile);
	}
	
    // by file

    /**
     * Registers a file using a registration profile
     * Uploads work by file
     *
     * @param title Work's title
     * @param file File to register
     * @param profile Registration profile
     * @param uploadProgressListener Upload progress listener
     * @return Work's registration code
     * @throws ApiException,FileNotFoundException
     */
    public String workRegister(String title,File file,Profile profile,UploadProgressListener uploadProgressListener) throws ApiException, FileNotFoundException {
        return workRegister(file, profile, createWork(title), uploadProgressListener);
    }

    /**
     * Registers a file using a registration profile and/or a work object containing the registration parameters
     * Uploads work by file
     *
     * @param file File to register
     * @param profile Registration profile
     * @param work work containing register parameters to override those of defined profile
     * @param uploadProgressListener Upload progress listener
     * @return Work's registration code
     * @throws ApiException,FileNotFoundException
     */
    public String workRegister(File file,Profile profile,Work work,
            UploadProgressListener uploadProgressListener) throws ApiException, FileNotFoundException {
        Map<String, String> params = api.createParams();

        // call uploadFile
        String ticket = uploadFile(file, file.getName(), true, uploadProgressListener);
        if (ticket != null) {
            params.put("uploadticket", ticket);
        }

        // call common register
        return workRegister(params, work, profile);
    }

    // by ticket
    
    /**
     * Registers a file using a registration profile and/or a work object containing the registration parameters
     * Uses an already uploaded file
     *
     * @param ticket upload ticket of uploaded file to use
     * @param profile Registration profile
     * @param work work containing register parameters to override those of defined profile
     * @return Work's registration code
     * @throws ApiException
     */
    public String workRegister(String ticket, Work work, Profile profile) throws ApiException {
        Map<String, String> params = api.createParams();

        if (ticket != null) {
            params.put("uploadticket", ticket);
        }

        // call common register
        return workRegister(params, work, profile);
    }


    // by url

    /**
     * Registers a file using a registration profile
     * Uploads work by URL
     *
     * @param title Work's title
     * @param url URL of file
     * @param profile Registration profile
     * @param fileName name of file
     * @param fileSize size of file
     * @param checkSum checksum of file
     * @return Work's registration code
     * @throws ApiException
     */
    public String workRegister(String title,URL url,Profile profile,long fileSize,String checkSum ) throws ApiException {
        return workRegister(title,url,profile,url.getFile(),fileSize,checkSum);
    }

    public String workRegister(String title,URL url,Profile profile,String fileName,long fileSize,String checkSum ) throws ApiException {
        return workRegister(url,profile,createWork(title),url.getFile(),fileSize,checkSum);
    }
    
    /**
     * Registers a file using a registration profile and/or a work object containing the registration parameters
     * Uploads work by URL
     *
     * @param url URL to register
     * @param profile Registration profile
     * @param work work containing register parameters to override those of defined profile
     * @return Work's registration code
     * @throws ApiException
     */
    public String workRegister(URL url,Profile profile,Work work,
            String fileName,long fileSize,String checkSum ) throws ApiException {
        Map<String, String> params = api.createParams();

        // set parameters
        params.put("url",url.toString());
        params.put("filename",fileName);
        params.put("size",String.valueOf(fileSize));
        params.put("checksum",checkSum);

        // call common register
        return workRegister(params, work, profile);
    }

    // common
    public String workRegister(Map<String, String> registerParams, Work work, Profile profile) throws ApiException {
        // proccess extra fields
        if (profile != null) {
            registerParams.put("profile", profile.getCode());
        }

        return workRegisterCall(work, registerParams);
    }

    // by file

    /**
     * Updates a file using a work object containing the registration parameters.
     * Uploads a new version of work by file
     *
     * @param file File to upload
     * @param work work containing register parameters used to update work
	 * @param uploadProgressListener Upload progress listener
     * @return true on success
     * @throws ApiException,FileNotFoundException
     */
    public boolean workUpdate(File file, Work work, UploadProgressListener uploadListener) throws ApiException, FileNotFoundException {
        return workUpdate(file, work, null, null, uploadListener);
    }

    /**
     * Updates a file using a work object containing the registration parameters.
     * Uploads a new version of work by file
     *
     * @param file File to upload
     * @param work work containing register parameters used to update work
     * @param extraTags tags to add to the existing tags, if null dont use extratags
     * @param extraLinks links to add to the existing links, if null dont use extralinks	
	 * @param uploadProgressListener Upload progress listener
     * @return true on success
     * @throws ApiException,FileNotFoundException
     */	
    public boolean workUpdate(File file, Work work, String extraTags, List<Link> extraLinks,
            UploadProgressListener uploadProgressListener) throws ApiException, FileNotFoundException {
        Map<String, String> params = api.createParams();
        
        // call uploadFile
        String ticket = uploadFile(file, file.getName(), true, uploadProgressListener);
        if (ticket != null) {
            params.put("uploadticket", ticket);
        }

        // call common update
        return workUpdate(params, work, extraTags, extraLinks);
    }

    // by ticket

    /**
     * Updates a file using a work object containing the registration parameters.
     * Uses an already uploaded file
     *
     * @param ticket upload ticket of uploaded file to use
     * @param work work containing register parameters to override those of defined profile
     * @param extraTags tags to add to the existing tags, if null dont use extratags
     * @param extraLinks links to add to the existing links, if null dont use extralinks
     * @return true on success
     * @throws ApiException
     */
    public boolean workUpdate(String ticket, Work work) throws ApiException {
        return workUpdate(ticket, work, null, null);
    }

    public boolean workUpdate(String ticket, Work work, String extraTags,
            List<Link> extraLinks) throws ApiException {
        Map<String, String> params = api.createParams();

        if (ticket != null) {
            params.put("uploadticket", ticket);
        }

        // call common update
        return workUpdate(params, work, extraTags, extraLinks);
    }

    // by url

    /**
     * Updates a file using a work object containing the registration parameters.
     * Uploads a new version of work by URL
     *
     * @param url URL of file
     * @param fileName name of file
     * @param fileSize size of file
     * @param checkSum checksum of file
     * @param work work containing register parameters used to update work
     * @return true on success
     * @throws ApiException
     */
    public boolean workUpdate(URL url, String fileName, long fileSize, String checkSum,Work work) throws ApiException {
        return workUpdate(url, fileName, fileSize, checkSum, work, null, null);
    }
    
    /**
     * Updates a file using a work object containing the registration parameters.
     * Uploads a new version of work by URL
     *
     * @param url URL of file
     * @param fileName name of file
     * @param fileSize size of file
     * @param checkSum checksum of file
     * @param work work containing register parameters used to update work
     * @param extraTags tags to add to the existing tags, if null dont use extratags
     * @param extraLinks links to add to the existing links, if null dont use extralinks
     * @return true on success
     * @throws ApiException
     */	
    public boolean workUpdate(URL url, String fileName, long fileSize,
            String checkSum, Work work, String extraTags, List<Link> extraLinks) throws ApiException {
        Map<String, String> params = api.createParams();

        // set parameters
        params.put("url",url.toString());
        params.put("filename",fileName);
        params.put("size",String.valueOf(fileSize));
        params.put("checksum",checkSum);

        // call common update
        return workUpdate(params, work, extraTags, extraLinks);
    }

    /**
     * Updates a file using a work object containing the registration parameters.
     *
     * @param work work containing register parameters used to update work
     * @return true on success
     * @throws ApiException
     */
    public boolean workUpdate(Work work) throws ApiException {
        return workUpdate(work, null, null);
    }

    /**
     * Updates a file using a work object containing the registration parameters.
     *
     * @param work work containing register parameters used to update work
     * @param extraTags tags to add to the existing tags, if null dont use extratags
     * @param extraLinks links to add to the existing links, if null dont use extralinks
     * @return true on success
     * @throws ApiException
     */	
    public boolean workUpdate(Work work, String extraTags, List<Link> extraLinks) throws ApiException {
        Map<String, String> params = api.createParams();

        // call common update
        return workUpdate(params, work, extraTags, extraLinks);
    }

    // common
    private boolean workUpdate(Map<String, String> updateParams, Work work, String extraTags, List<Link> extraLinks) throws ApiException {
        // proccess extra fields
        if (extraTags != null) {
            updateParams.put("extratags", extraTags);
        }
        if (extraLinks != null) {
            int i = 1;
            for (Link link : extraLinks) {
                updateParams.put("extralink" + i, ParamsBuilder.linkString(link));
                i++;
            }
        }

        String workCode = workRegisterCall(work, updateParams);
        return work.getCode().equals(workCode);
    }

    ////////////////////////////////////////////////////////////////////////////
    // SEARCH methods
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Search works by search field.
     *     
     * @param page page number
     * @param fieldValues Variable list of pairs of search field,value
     * @return List of found works
     * @throws ApiException
     * @see SearchMethod
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
        String result = callComponent("search.byfields",fieldParamList.toArray());
        ListPage<Work> results = readWorkListPage(result);
        return results;
    }


    /**
     * Search works by content (Aka Semantic query).
     *     
     * @param method A SearchMethod.WORK_CNT_XXX value
     * @param value value
     * @return List of found works
     * @throws ApiException
     */
    public List<Work> searchWorksByContent(SearchMethod method,String value) throws ApiException {
        if(!method.name().startsWith("WORK_CNT_")) {
            throw new IllegalArgumentException("Bad search method "+method);
        }
        setApiSearchUrl();
        String result = callComponent("semantic.query",method.getFieldName(),value);
        XStream xs = createXStream();
        xs.registerConverter(new WorkConverter());
        List<Work> results = readList(result, "works", "work", Work.class,xs);
        return results;
    }

    /**
     * Search works by MD5 hash.
     * @param md5 value
     * @return First page list of found works
     * @throws ApiException
     */
    public ListPage<Work> searchWorksByHashMD5(String md5) throws ApiException {
        return searchWorksByHash(1, SearchMethod.HASH_MD5, md5);
    }

    /**
     * Search works by SHA-1 hash.
     * @param sha1 value
     * @return First page list of found works
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
        String result = callComponent("search.byhash",method.getFieldName(),value,"page",page);
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
        String result = callComponent("search.byquery","query",query,"page",page);
        ListPage<Work> results = readWorkListPage(result);
        return results;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Internal api helpers
    ////////////////////////////////////////////////////////////////////////////
    protected String callComponent(String component, Object... params) throws ApiException {
        Map<String, String> allParams = createParams(component);
        if (params != null && params.length > 0) {
            allParams.putAll(api.createParams(params));
        }
        if(getLocale() != null) {
            api.addLocale(allParams);
        }
        return call(allParams);
    }

    protected String call(Map<String, String> params) throws ApiException {
        String result = null;
        try {
            result = api.call(params);
        } catch (Exception ex) {
            throw ApiException.wrap(ex);
        }
        checkError(result);
        return result;
    }

    protected String callComponentSigned(String component,AuthKey authKey,boolean ztime,boolean noncekey,boolean addLocale,Object... params) throws ApiException {
        checkAuthKey(authKey);
        Map<String, String> allParams = api.createParams("component",component,"authkey", authKey.getAuthkey());
        if (params != null && params.length > 0) {
            allParams.putAll(api.createParams(params));
        }
        return callSigned(authKey.getPrivatekey(),ztime,noncekey,addLocale,allParams);
    }

    protected String callComponentSigned(String component,String privateKey,boolean ztime,boolean noncekey,boolean addLocale,Object... params) throws ApiException {
        Map<String, String> allParams = api.createParams("component", component);
        if (params != null && params.length > 0) {
            allParams.putAll(api.createParams(params));
        }
        return callSigned(privateKey,ztime,noncekey,addLocale,allParams);
    }

    @SuppressWarnings("unchecked")
    protected String callSigned(String privateKey,boolean ztime,boolean noncekey,boolean addLocale,Map<String, String> params) throws ApiException {
        String result = null;
        try {
            result = api.callSigned(params, privateKey, ztime, noncekey,addLocale);
        } catch (Exception ex) {           
            throw ApiException.wrap(ex);
        }
        checkError(result);
        return result;
    }

    protected boolean checkReady(String response) throws ApiException {
        return checkState(response, STATE_READY);
    }

    protected boolean checkState(String response, String expected) throws ApiException {
        try {
            String state = api.getResponseState(response);
            return expected.equals(state);
        } catch (Exception ex) {
            throw ApiException.wrap(ex);
        }
    }

    protected void checkError(String response) throws ApiException {
        if (api.isError(response)) {
            try {
                String errorCode = api.getErrorCode(response);
                String errorMessage = api.getErrorMessage(response);
                throw new ApiException(errorCode, errorMessage);
            } catch (ApiException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new ApiException(ex,null,response);
            }
        }
    }

    protected Map<String, String> createParams(String component) {
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
        return readListPage(response, Work.class, new WorkConverter());
    }

    @SuppressWarnings("unchecked")
    protected <T extends Object> ListPage<T> readListPage(String response,Class<T> clazz,Converter converter) {
        XStream xs = createXStream();        
        xs.registerConverter(new ListPageConverter<T>(clazz,converter));                
        ListPage<T> listPage = readObject(ListPage.class, response,xs);        
        return listPage;
    }

    private <T extends Object> List<T> readList(String response, String listElement, String element, Class<T> clazz) {
        return readList(response, listElement, element, clazz,null);
    }

    @SuppressWarnings("unchecked")
    private <T extends Object> List<T> readList(String response, String listElement, String element, Class<T> clazz,XStream xs) {
        if(xs == null)  {
            xs = createXStream();
        }
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
        XStream xs = createXStream();
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
    protected <T> T readObject(Class<T> clazz, String response,Converter converter) {
        XStream xs = createXStream();
        if(converter != null) {
            xs.registerConverter(converter);
        }
        return readObject(clazz, response,xs);
    }

    @SuppressWarnings("unchecked")
    <T> T readObject(Class<T> clazz, String response,XStream xs) {
        if(xs == null)  {
            xs = createXStream();
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

    protected void setApiUrl() {
        api.setBaseUrl(getBaseUrl());
    }

    protected void setApiSearchUrl() {
        api.setBaseUrl(getBaseSearchUrl());
    }

    private void checkAuthKey(AuthKey authKey) throws ApiException {
        if(authKey == null || authKey.getAuthkey() == null || authKey.getPrivatekey() == null) {
            throw new ApiException("authkey needed. Set o create one");
        }
    }

    /**
     * Calls work.register for register and updating works
     *
     * @param work register/update parameters
     * @param params already defined parameters, containing profile, extra fields, and/or file uploading parameters
     * @return registered work code
     * @throws ApiException
     */
    private String workRegisterCall(Work work, Map<String, String> params) throws ApiException {
        setApiUrl();
        checkAuthKey(authKey);

        params.put("component", "work.register");
        params.put("authkey", authKey.getAuthkey());
        params = ParamsBuilder.buildWorkParams(params, work);

        String result = callSigned(authKey.getPrivatekey(), true, true, false, params);

        return api.evalXml(result, "/workregistry/code");
    }

    /**
     * Uploads a file for register or update a work
     * 
     * @param file file to upload
     * @param fileName filename to use for upload file
     * @param byPost true - upload file using POST, false - upload by API
	 * @param uploadListener Upload progress listener to notify if not <code>null</code>* 
     * @return upload ticket or null if fails
     * @throws ApiException,FileNotFoundException
     */
    public String uploadFile(File file, String fileName, boolean byPost,UploadProgressListener uploadListener) throws ApiException, FileNotFoundException {
		return uploadFileSource(new FilePartSource(fileName, file), byPost, uploadListener);
	}

	
    /**
     * Uploads a data buffer for register or update a work via post
     * 
     * @param data Byte array of data to upload
     * @param fileName filename to use for upload file
     * @return upload ticket or null if fails
     * @throws ApiException
     */
    public String uploadBytes(byte[] data, String fileName) throws ApiException {
		return uploadBytes(data, fileName, true,null);
	}
	
    /**
     * Uploads a data buffer for register or update a work
     * 
     * @param data Byte array of data to upload
     * @param fileName filename to use for upload file
     * @param byPost true - upload file using POST, false - upload by API
	 * @param uploadListener Upload progress listener to notify if not <code>null</code>
     * @return upload ticket or null if fails
     * @throws ApiException
     */
    public String uploadBytes(byte[] data, String fileName, boolean byPost,UploadProgressListener uploadListener) throws ApiException {
		return uploadFileSource(new ByteArrayPartSource(fileName, data), byPost, uploadListener);
	}	
	
    /**
     * Uploads a file part source for register or update a work
     * 
     * @param fileSource <code>PartSource</code> to upload 
     * @param byPost true - upload file using POST, false - upload by API
	 * @param uploadListener Upload progress listener to notify if not <code>null</code>* 
	 * @param checkSum Optional SHA-1 checksum if available, otherwise it will be calculated again for the file part source
     * @return upload ticket or null if fails
     * @throws ApiException
     */
    public String uploadFileSource(PartSource fileSource, boolean byPost,UploadProgressListener uploadListener,String... checkSum) throws ApiException {
        if(fileSource == null) {
            return null;
        } else {
            // Look up
            Map<String, String> params = api.createParams("component", "work.upload.lookup");
            params.put("authkey", authKey.getAuthkey());
            params.put("filename", fileSource.getFileName());
            if (byPost) {
                params.put("bypost", "true");
            }
            String response = api.callSigned(params, authKey.getPrivatekey(), true, false);
            checkError(response);

            String uploadURL = api.evalXml(response, "/workuploadlookup/uploadurl");
            log.debug("Upload URL: {}", uploadURL);
            String uploadID = api.evalXml(response, "/workuploadlookup/uploadid");
            log.debug("Upload id: {}", uploadID);

            // Upload
            String uploadTicket = null;
            RegisterWork registerer = new RegisterWork(api);
            registerer.setUploadProgressListener(uploadListener);

            api.setBaseUrl(uploadURL);
            if (byPost) {
                ////////////////////////////////////////////////////////////////////
                //POST Upload:
                log.info("registerWork upload by post file: {}", fileSource.getFileName());
                params = api.createParams("uploadid", uploadID);
                uploadTicket = registerer.postFile(uploadURL, params, fileSource);
                log.info("Successfully uploaded file: {}", fileSource);
            } else {
                ////////////////////////////////////////////////////////////////////
                //API Upload:
                try {
                    log.info("registerWork upload by API file: {}", fileSource.getFileName());
					String sha1CheckSum = null;
					if(checkSum != null && checkSum.length == 1) {
						sha1CheckSum = checkSum[0];
					}
					if(StringUtils.isBlank(sha1CheckSum)) {
						log.info("Building SHA-1 checksum");
						sha1CheckSum = Digest.getHexDigest(fileSource.createInputStream(), Digest.SHA1);
					}
                    uploadTicket = registerer.uploadFile(
							uploadURL, uploadID, fileSource, sha1CheckSum							
					);
                    log.info("Successfully uploaded file: {}", fileSource.getFileName());
                }
                catch (Exception ex) {
                    throw ApiException.wrap(ex);
                }
            }
            log.debug("uploadTicket {}", uploadTicket);
            return uploadTicket;
        }
    }

    /**
     * Check if upload ticket status is ready
     *
     * @param uploadTicket
     * @return <code>true</code> on ready update ticket
     * @throws ApiException
     */
    public boolean uploadTicketStatus(String uploadTicket) throws ApiException {
        setApiUrl();
        checkAuthKey(authKey);
        Map<String, String> params = api.createParams("component", "work.uploadticket.status");
        params.put("authkey", authKey.getAuthkey());
        params.put("uploadticket", uploadTicket);
        String result = api.callSigned(params, authKey.getPrivatekey(), true, false);
        checkError(result);
        log.debug("work.uploadticket.status result:\n{}", result);
        return checkReady(result);
    }


	protected Work createWork(String title) {
		Work work = new Work();
		work.setTitle(title);
		return work;
	}

	private XStream createXStream() {				
		return new XStream();
	}

}
