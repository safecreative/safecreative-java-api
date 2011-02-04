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

import java.net.URL;
import java.util.Date;



/**
 * Represents an user account.
 *
 * @author mpolo@safecreative.org
 * @author jguillo@safecreative.org
 */
public class User extends CodeableEntity {
    private URL profileUrl;
    private URL thumbnailUrl;
    private String email;
    private String alias;
    private Date entryDate;
    private Country country;

    /**
     * @return the profileUrl
     */
    public URL getProfileUrl() {
        return profileUrl;
    }

    /**
     * @param profileUrl the profileUrl to set
     */
    public void setProfileUrl(URL profileUrl) {
        this.profileUrl = profileUrl;
    }

    /**
     * @return the thumbnailUrl
     */
    public URL getThumbnailUrl() {
        return thumbnailUrl;
    }

    /**
     * @param thumbnailUrl the thumbnailUrl to set
     */
    public void setThumbnailUrl(URL thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }


    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the entryDate
     */
    public Date getEntryDate() {
        return entryDate;
    }

    /**
     * @param entryDate the entryDate to set
     */
    public void setEntryDate(Date entryDate) {
        this.entryDate = entryDate;
    }
    
    /**
     * @return the country
     */
    public Country getCountry() {
        return country;
    }

    /**
     * @param country the country to set
     */
    public void setCountry(Country country) {
        this.country = country;
    }
    
    /**
	 * @param alias the alias to set
	 */
	public void setAlias(String alias) {
		this.alias = alias;
	}

	/**
	 * @return the alias
	 */
	public String getAlias() {
		return alias;
	}

	@Override
    public String toString() {
        return getClass().getSimpleName()+"[code:"+getCode()+",name:"+getName()+",profile:"+profileUrl+",thumbnail:"+thumbnailUrl+"]";
    }
}
