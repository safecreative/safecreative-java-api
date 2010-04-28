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

import org.apache.commons.lang.StringUtils;
import org.safecreative.api.SafeCreativeAPI;


/**
 * Represents a work registration profile.
 *
 * @author mpolo@safecreative.org
 * @author jguillo@safecreative.org
 */
public class Profile implements Comparable {
    
    private String code;
    private String name;

    public Profile() {
    }

    public Profile(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static Profile fromCode(String code) {
        Profile result = null;
        if(SafeCreativeAPI.isValidCode(code)) {
            result = new Profile(code);
        }
        return result;
    }

    @Override
    public int hashCode() {
        return code == null ? -1 : code.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(compareTo(obj) != 0) {
            return false;
        }
        return hashCode() == obj.hashCode();
    }

    @Override
    public int compareTo(Object o) {
        if(o == null || !(o instanceof Profile)) {
            return -1;
        }
        return name.compareTo(((Profile)o).name);
    }

    @Override
    public String toString() {
        return StringUtils.isBlank(name) ? "" : name;
    }
}
