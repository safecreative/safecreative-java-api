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
import org.safecreative.api.SafeCreativeAPI.AuthkeyLevel;

/**
 * Represents an authorization key state
 *
 * @author mpolo@safecreative.org
 * @author jguillo@safecreative.org
 */
public class AuthKeyState implements Serializable{

    private String code;
    private String authorized;
    private AuthkeyLevel level;

    public void setAuthorized(String authorized) {
        this.authorized = authorized;
    }


    public boolean isAuthorized() {
        return Boolean.valueOf(authorized);
    }

    public void setCode(String code) {
        this.code = code;
    }
    
    public String getCode() {
        return code;
    }

    public AuthkeyLevel getLevel() {
        return level;
    }

    public void setLevel(AuthkeyLevel level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName()+"[code:"+code+",authorized:"+isAuthorized()+"]";
    }

}
