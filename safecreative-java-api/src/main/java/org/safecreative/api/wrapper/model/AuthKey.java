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
package org.safecreative.api.wrapper.model;

import java.io.Serializable;

/**
 * Represents an authorization key
 *
 * @author mpolo@safecreative.org
 * @author jguillo@safecreative.org
 */
public class AuthKey implements Serializable{

    private String authkey;
    private String privatekey;
    private String manageUrl;

    public AuthKey() {
    }

    public AuthKey(String authkey, String privatekey) {
        this(authkey, privatekey, null);
    }

    public AuthKey(String authkey, String privatekey, String manageUrl) {
        this.authkey = authkey;
        this.privatekey = privatekey;
        this.manageUrl = manageUrl;
    }



    public void setAuthkey(String authkey) {
        this.authkey = authkey;
    }


    public String getAuthkey() {
        return authkey;
    }

    public void setManageUrl(String manageUrl) {
        this.manageUrl = manageUrl;
    }


    public String getManageUrl() {
        return manageUrl;
    }

    public void setPrivatekey(String privatekey) {
        this.privatekey = privatekey;
    }

    public String getPrivatekey() {
        return privatekey;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName()+"[auth:"+authkey+",private:"+privatekey+",manageURL:"+manageUrl+"]";
    }


}
