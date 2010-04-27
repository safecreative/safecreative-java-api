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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.safecreative.api.SafeCreativeAPI.*;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.lang.StringUtils;
import org.safecreative.api.util.Base64;
import org.safecreative.api.util.IOHelper;
import org.safecreative.api.wrapper.model.Link;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple low level file registration using Safe Creative's API via post/get
 *
 * @author mpolo@safecreative.org
 * @author jguillo@safecreative.org
 */
public class RegisterWork {

    private static Logger log = LoggerFactory.getLogger(RegisterWork.class);
    private SafeCreativeAPI api;
    private String workTpe;
    private String excerpt;
    private String license;
    private String tags;
    private String code;
    private String profile;
    private UploadProgressListener uploadProgressListener;
    private boolean registryPublic = true;
    private boolean registryFinal = false;
    private boolean allowdownload = true;
    private boolean userauthor = true;
    private HttpClient client;
    private boolean postUpload = true;
    private List<String> links;
    private String compositionOf;
    //private Map<RelationType,List<String>> relations;

    public RegisterWork(SafeCreativeAPI api) throws Exception {
        this.api = api;
        links = new ArrayList<String>();
    }

    public void setUploadProgressListener(UploadProgressListener uploadProgressListener) {
        this.uploadProgressListener = uploadProgressListener;
    }

    public void setRegistryFinal(boolean registryFinal) {
        this.registryFinal = registryFinal;
    }

    public boolean isRegistryFinal() {
        return registryFinal;
    }

    public void setRegistryPublic(boolean registryPublic) {
        this.registryPublic = registryPublic;
    }

    public boolean isRegistryPublic() {
        return registryPublic;
    }

    public void setAllowdownload(boolean allowdownload) {
        this.allowdownload = allowdownload;
    }

    public boolean isAllowdownload() {
        return allowdownload;
    }

    public void setUserauthor(boolean userauthor) {
        this.userauthor = userauthor;
    }

    public boolean isUserauthor() {
        return userauthor;
    }

    public void setCompositionOf(String compositionOf) {
        this.compositionOf = compositionOf;
    }

    public void setExcerpt(String excerpt) {
        this.excerpt = excerpt;
    }

    public String getExcerpt() {
        return excerpt;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code the code to set
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return the profile
     */
    public String getProfile() {
        return profile;
    }

    /**
     * @param profile the profile to set
     */
    public void setProfile(String profile) {
        this.profile = profile;
    }

    public void setWorkTpe(String workTpe) {
        this.workTpe = workTpe;
    }

    public String getWorkTpe() {
        return workTpe;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getLicense() {
        return license;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getTags() {
        return tags == null ? "" : tags;
    }

    public void addLink(Link link) {
        links.add(link.getUrl() + "|" + link.getName() + "|" + link.getType().name());
    }

    public void addRelations(String workCode, RelationType type) {
        //TODO relations.add(url + "|" + name + "|" + type.toString());
    }

    public void setPostUpload(boolean postUpload) {
        this.postUpload = postUpload;
    }

    public boolean isPostUpload() {
        return postUpload;
    }


    public String registerWork(String title, String fileName, File workFile, String checksum) throws Exception {
        String baseUrl = api.getBaseUrl();
        ////////////////////////////////////////////////////////////////////
        //Look up:
        log.debug("registerWork Lookup upload server");
        Map params = api.createParams("component", "work.upload.lookup");

        params.put("authkey", api.getAuthKey());
        if(workFile != null) {
            params.put("filename", fileName == null ? workFile.getName() : fileName);
        }
        if (postUpload) {
            params.put("bypost", "true");
        }
        String response = api.callSigned(params, api.getPrivateAuthKey(), true, false);
        checkError(params, response);
        String uploadURL = api.evalXml(response, "/workuploadlookup/uploadurl");
        URL url = new URL(uploadURL);
        log.debug("Upload URL: {}", url);
        String uploadID = api.evalXml(response, "/workuploadlookup/uploadid");
        log.debug("Upload id: {}", uploadID);
        String uploadTicket = null;
        if(workFile != null) {
            api.setBaseUrl(uploadURL);
            if (postUpload) {
                ////////////////////////////////////////////////////////////////////
                //POST Upload:
                log.info("registerWork upload by post file: {}", workFile);
                params = api.createParams("uploadid", uploadID);
                uploadTicket = postFile(uploadURL, params, workFile);
                log.info("Successfully uploaded file: {}", workFile);
            } else {
                ////////////////////////////////////////////////////////////////////
                //Begin:
                log.info("registerWork upload begin");                
                params = api.createParams("component", "work.upload.begin");
                params.put("authkey", api.getAuthKey());
                params.put("uploadid", uploadID);
                response = api.callSigned(params, api.getPrivateAuthKey(), true, false);
                checkError(params, response);
                log.debug("response {}", response);
                String state = api.getResponseState(response);
                if (!"ready".equalsIgnoreCase(state)) {
                    throw new RuntimeException("Unexpected work.upload.begin state " + state);
                }

                ////////////////////////////////////////////////////////////////////
                //Chunk:
                int chunkSize = 50 * 1024;
                long offset = 0;
                int readed = 0;
                int percent = 0;
                byte[] chunkBuffer = new byte[chunkSize];
                byte[] data;
                long uploadSize = workFile.length();
                InputStream is = null;
                if (uploadProgressListener != null) {
                    uploadProgressListener.uploadProgress(percent, offset, uploadSize);
                }
                try {
                    is = new FileInputStream(workFile);
                    while (offset < uploadSize) {
                        readed = is.read(chunkBuffer);
                        if (readed <= 0) {
                            break;
                        }

                        data = new byte[readed];
                        System.arraycopy(chunkBuffer, 0, data, 0, readed);
                        params = api.createParams("component", "work.upload.chunk");
                        params.put("authkey", api.getAuthKey());

                        params.put("uploadid", uploadID);
                        params.put("offset", String.valueOf(offset));
                        String encoded = Base64.encodeBytes(data);
                        params.put("data", encoded);
                        response = api.callSigned(params, api.getPrivateAuthKey(), true, false);
                        checkError(params, response);
                        state = api.getResponseState("workuploadchunk", response);
                        if (!"continue".equalsIgnoreCase(state)) {
                            throw new RuntimeException("Unexpected work.upload.chunk state " + state);
                        }
                        offset += readed;
                        percent = (int) ((100f * (float) offset) / (float) uploadSize);
                        log.debug("CHUNK UPLOADED {}%", percent);
                        if (uploadProgressListener != null) {
                            uploadProgressListener.uploadProgress(percent, offset, uploadSize);
                        }
                    }
                } finally {
                    IOHelper.closeQuietly(is);
                }
                log.info("Work uploaded successfully!");
                if (uploadProgressListener != null) {
                    uploadProgressListener.uploadProgress(100, uploadSize, uploadSize);
                }

                ////////////////////////////////////////////////////////////////////
                //Commit:
                log.info("COMMIT");
                params = api.createParams("component", "work.upload.commit");
                params.put("authkey", api.getAuthKey());

                params.put("uploadid", uploadID);
                params.put("length", String.valueOf(uploadSize));
                params.put("checksum", checksum);

                response = api.callSigned(params, api.getPrivateAuthKey(), true, false);
                checkError(params, response);
                uploadTicket = api.evalXml(response, "/workuploadcommit/uploadticket");
            }
            log.debug("uploadTicket {}", uploadTicket);
        }
        ////////////////////////////////////////////////////////////////////
        //Work registration:
        log.info("REGISTRATION");
        api.setBaseUrl(baseUrl);
        params = api.createParams("component", "work.register");
        params.put("authkey", api.getAuthKey());
        if(uploadTicket != null) {
            params.put("uploadticket", uploadTicket);
        }

        params.put("title", title);
        params.put("excerpt", excerpt);
        params.put("final", registryFinal ? "1" : "0");
        if (StringUtils.isNotBlank(code)) {
            params.put("code", getCode());
        }
        if (StringUtils.isEmpty(profile)) {
            params.put("worktype", getWorkTpe());
            params.put("license", getLicense());
            params.put("tags", getTags());
            params.put("allowdownload", allowdownload ? "1" : "0");
            params.put("userauthor", userauthor ? "1" : "0");
            params.put("registrypublic", registryPublic ? "1" : "0");
        } else {
            params.put("profile", getProfile());
        }
        if (compositionOf != null) {
            params.put("compositionof", compositionOf);
        }

        for (int i = 0; i < links.size(); i++) {
            params.put("link" + (i + 1), links.get(i));
        }

        String result = api.callSigned(params, api.getPrivateAuthKey(), true, true);
        checkError(params, result);
        log.debug("work: {}", result);
        return api.evalXml(result, "/workregistry/code");
    }



    private String postFile(String uri, Map<String, String> params, File file) {
        try {
            log.debug(String.format("api request by post: \n%s/%s\n",
                    uri, file.getName()));
            if (client == null) {
                client = new HttpClient();
            }
            PostMethod post = new PostMethod(uri);
            Part[] parts = new Part[params.size() + 1];
            int i = 0;
            for (Map.Entry<String, String> param : params.entrySet()) {
                parts[i++] = new StringPart(param.getKey(), param.getValue());
            }
            parts[i] = new FilePart("file", file, "application/octet-stream", null);
            //TODO notify upload progress
            MultipartRequestEntity entity = new MultipartRequestEntity(parts, post.getParams());
            post.setRequestEntity(entity);
            int status = client.executeMethod(post);
            if (status != HttpURLConnection.HTTP_OK) {
                throw new IOException("Response error " + status);
            }

            return post.getResponseBodyAsString();

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void checkError(Map params,String result) throws ApiException{
        if (api.isError(result)) {
            String component = String.valueOf(params.get("component"));
            log.warn("{} error {}",component,result);
            throw new ApiException(api.getErrorCode(result),api.getErrorMessage(result));
        }
    }
}
