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
public class Profile extends CodeableEntity {
    // should extend from CodeableEntity

    public Profile() {
    }

    public Profile(String code) {
        setCode(code);
    }

    public static Profile fromCode(String code) {
        Profile result = null;
        if(SafeCreativeAPI.isValidCode(code)) {
            result = new Profile(code);
        }
        return result;
    }

    @Override
    public String toString() {
        return StringUtils.isBlank(getName()) ? "" : getName();
    }

    // Class implementing all the fields of a profile.
    // Should be merged with Profile when the API supports all the fields of a profile.
    /*public static class ExtendedProfile extends Profile {
        private String tags;
        private boolean allowDownload;
        private boolean registryPublic;
        private License license;
        private Work.Type workType;
        private Language language;
        private boolean useAlias;
        private String userAlias;
        private boolean userAuthor;
        private boolean userRights;

        public boolean isAllowDownload() {
            return allowDownload;
        }

        public void setAllowDownload(boolean allowDownload) {
            this.allowDownload = allowDownload;
        }

        public Language getLanguage() {
            return language;
        }

        public void setLanguage(Language language) {
            this.language = language;
        }

        public License getLicense() {
            return license;
        }

        public void setLicense(License license) {
            this.license = license;
        }

        public boolean isRegistryPublic() {
            return registryPublic;
        }

        public void setRegistryPublic(boolean registryPublic) {
            this.registryPublic = registryPublic;
        }

        public String getTags() {
            return tags;
        }

        public void setTags(String tags) {
            this.tags = tags;
        }

        public boolean isUseAlias() {
            return useAlias;
        }

        public void setUseAlias(boolean useAlias) {
            this.useAlias = useAlias;
        }

        public String getUserAlias() {
            return userAlias;
        }

        public void setUserAlias(String userAlias) {
            this.userAlias = userAlias;
        }

        public boolean isUserAuthor() {
            return userAuthor;
        }

        public void setUserAuthor(boolean userAuthor) {
            this.userAuthor = userAuthor;
        }

        public boolean isUserRights() {
            return userRights;
        }

        public void setUserRights(boolean userRights) {
            this.userRights = userRights;
        }

        public Type getWorkType() {
            return workType;
        }

        public void setWorkType(Type workType) {
            this.workType = workType;
        }
    }*/
}
