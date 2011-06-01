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
import java.io.OutputStream;
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
import org.safecreative.api.wrapper.model.Work;
import org.safecreative.api.wrapper.util.ParamsMerger;
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
    private String code;
    private String profile;
    private UploadProgressListener uploadProgressListener;
    private boolean registryFinal = false;
    private HttpClient client;
    private boolean postUpload = true;

    public RegisterWork(SafeCreativeAPI api) {
        this.api = api;
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

    public void setPostUpload(boolean postUpload) {
        this.postUpload = postUpload;
    }

    public boolean isPostUpload() {
        return postUpload;
    }


    /*public String registerWork(String title, String fileName, File workFile, String checksum) throws Exception {
        return registerWork(title, fileName, workFile, null, checksum);
    }*/

    /**
     * @param title title of the register work, used only if customValues is null
     * @param customValues work containing register parameters to override those of defined profile,
     * optional if license is defined
     */
    public String registerWork(String fileName, File workFile,
            Work customValues, String checksum) throws Exception {
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
                uploadTicket = uploadFile(uploadURL, uploadID, workFile, checksum);
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

        params.put("final", registryFinal ? "1" : "0");
        if (StringUtils.isNotBlank(code)) {
            params.put("code", getCode());
        }
        if (StringUtils.isEmpty(profile) && customValues == null) { // ERROR
            throw new IllegalArgumentException("No profile or parameters defined");
        } else {
            if (!StringUtils.isEmpty(profile)) {
                params.put("profile", getProfile());
            }
            if (customValues != null) {
                params = ParamsMerger.mergeWork(params, customValues);
            }
        }

        String result = api.callSigned(params, api.getPrivateAuthKey(), true, true);
        checkError(params, result);
        log.debug("work: {}", result);
        return api.evalXml(result, "/workregistry/code");
    }



    public String postFile(String uri, Map<String, String> params, final File file) {
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
            parts[i] = new FilePart("file", file, "application/octet-stream", null) {

                @Override
                protected void sendData(OutputStream out) throws IOException {
                    //TODO notify upload progress
                    if(uploadProgressListener == null) {
                        super.sendData(out);
                    } else {
                        super.sendData(new PostUploadProgressListenerOutputStream(out,uploadProgressListener,file.length()));
                    }
                }

            };
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

    public String uploadFile(String uri, String uploadID, final File file, String checksum) throws Exception {
        String response;
        Map<String, String> params;

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
        long uploadSize = file.length();
        InputStream is = null;
        if (uploadProgressListener != null) {
            uploadProgressListener.uploadProgress(percent, offset, uploadSize);
        }
        try {
            is = new FileInputStream(file);
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
        return api.evalXml(response, "/workuploadcommit/uploadticket");
    }

    private void checkError(Map params,String result) throws ApiException{
        if (api.isError(result)) {
            String component = String.valueOf(params.get("component"));
            log.warn("{} error {}",component,result);
            throw new ApiException(api.getErrorCode(result),api.getErrorMessage(result));
        }
    }
}
