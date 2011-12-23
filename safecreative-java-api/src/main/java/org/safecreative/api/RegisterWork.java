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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import org.safecreative.api.SafeCreativeAPI.*;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.ByteArrayPartSource;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.FilePartSource;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.PartSource;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.lang.StringUtils;
import org.safecreative.api.util.Base64;
import org.safecreative.api.util.IOHelper;
import org.safecreative.api.wrapper.model.Work;
import org.safecreative.api.wrapper.util.ParamsBuilder;
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

	/**
	 * Constructor
	 * @param api
	 */
	public RegisterWork(SafeCreativeAPI api) {
        this.api = api;
    }

	/**
	 * 
	 * @param uploadProgressListener
	 */
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

	/**	
	 * @param workFile File to register
	 * @param customValues Work containing register parameters to override those of defined profile,
	 * optional if license is defined	 
	 * @param checksum 
	 * @return registration code
	 * @throws Exception  
     */
    public String registerWork(File workFile,Work work, String checksum) throws Exception {
		return registerWork(workFile.getName(), workFile, work, checksum);
	}
		
	/**
	 * @param fileName File name
	 * @param workFile File to register
	 * @param customValues Work containing register parameters to override those of defined profile,
	 * optional if license is defined	 
	 * @param checksum 
	 * @return registration code
	 * @throws Exception  
     */
    public String registerWork(String fileName, File workFile,Work work, String checksum) throws Exception {
		return registerWork(fileName,new FilePartSource(fileName,workFile),work,checksum);
	}
	
	/**
	 * @param fileName File name
	 * @param workFile byte array to register
	 * @param customValues Work containing register parameters to override those of defined profile,
	 * optional if license is defined	 
	 * @param checksum 
	 * @return registration code
	 * @throws Exception  
     */
    public String registerWork(String fileName, byte[] workFile,Work work, String checksum) throws Exception {
		return registerWork(fileName,new ByteArrayPartSource(fileName,workFile),work,checksum);
	}	

    /**
	 * @param fileName File name
	 * @param workFile PartSource data to register
	 * @param work Work containing register parameters to override those of defined profile,
	 * optional if license is defined	 
	 * @param checksum 
	 * @return registration code
	 * @throws Exception  
     */
    public String registerWork(String fileName, PartSource workFile,Work work, String checksum) throws Exception {
		if (StringUtils.isEmpty(getProfile()) && work == null) { // ERROR
            throw new IllegalArgumentException("No profile or parameters defined");
        }		
        String baseUrl = api.getBaseUrl();
        ////////////////////////////////////////////////////////////////////
        //Look up:
        log.debug("registerWork Lookup upload server");
        Map params = api.createParams("component", "work.upload.lookup");

        params.put("authkey", api.getAuthKey());
        if(workFile != null) {
			String name = fileName == null ? workFile.getFileName() : fileName;
			if(name != null) {
				params.put("filename", name);
			}
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
		
		if (!StringUtils.isEmpty(getProfile())) {
			params.put("profile", getProfile());
		}
		if (work != null) {
			params = ParamsBuilder.buildWorkParams(params, work);
		}

        String result = api.callSigned(params, api.getPrivateAuthKey(), true, true);
        checkError(params, result);
        log.debug("work: {}", result);
        return api.evalXml(result, "/workregistry/code");
    }

	
	/**
	 * Post data using an instance of <code>PartSource</code>
	 * @param uploadUrl
	 * @param params
	 * @param file <code>PartSource</code> to upload
	 * @return uploadticket
	 */
    public String postFile(String uploadUrl, Map<String, String> params, final PartSource file) {
		PostMethod post = null;
        try {
            log.debug(String.format("api request by post: \n%s/%s\n",uploadUrl, file.getFileName()));
            if (client == null) {
                client = new HttpClient();
            }
            post = new PostMethod(uploadUrl);
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
                        super.sendData(new PostUploadProgressListenerOutputStream(out,uploadProgressListener,file.getLength()));
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
            throw new RuntimeException(e);
        } finally {
			if(post != null) {
				post.releaseConnection();
			}
		}
    }

	
	/**
	 * Uploads a file
	 * @param uploadURL
	 * @param uploadID
	 * @param file File to upload
	 * @param checksum
	 * @return uploadticket
	 * @throws Exception
	 */
	public String uploadFile(String uploadURL, String uploadID, final File file, String checksum) throws Exception {
		return uploadFile(uploadURL, uploadID, new FilePartSource(file), checksum);
	}	
	
	/**
	 * Uploads a file
	 * @param uploadURL
	 * @param uploadID
	 * @param file <code>PartSource</code> to upload
	 * @param checksum
	 * @return uploadticket
	 * @throws Exception
	 */
	public String uploadFile(String uploadURL, String uploadID, final PartSource file, String checksum) throws Exception {
        String response;
        Map<String, String> params;
		api.setBaseUrl(uploadURL);
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
        long uploadSize = file.getLength();
        InputStream is = null;
        if (uploadProgressListener != null) {
            uploadProgressListener.uploadProgress(percent, offset, uploadSize);
        }
        try {
            is = file.createInputStream();
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