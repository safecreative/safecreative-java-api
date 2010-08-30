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
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Represents a work
 * 
 * @author mpolo@safecreative.org
 * @author jguillo@safecreative.org
 */
public class Work {
    private String code;
    private String title;
    private Date entryDate;
    private Date updateDate;
    private String excerpt;
    private String tags;
    private boolean allowdownload;
    private boolean allowSale;
    private boolean allowLicensing;
    private boolean registryPublic;
    private URL thumbnail;
    private List<Link> links;
    private List<User> authors;
    private List<User> rightHolders;
    private License license;
    private URL humanUrl;
    private URL apiUrl;
    private Type type;
    private TypeGroup typeGroup;
    private Language language;

    public static class Language extends CodeableEntity {
        
    }

    public static class Type extends CodeableEntity {
        /**
         * Common work type codes:
         */
        public static final String ARTICLE      =   "article";
        public static final String DOCUMENT     =   "document";
        public static final String ARTISTIC     =   "artistic";
        public static final String LITERATURE   =   "literature";
        public static final String SOFTWARE     =   "software";
        public static final String MULTIMEDIA   =   "multimedia";
        public static final String MUSIC        =   "music";
        public static final String PODCAST      =   "podcast";
        public static final String AUDIO        =   "audio";
        public static final String VIDEO        =   "video";
        public static final String DRAWING      =   "drawing";
        public static final String PHOTO        =   "photo";        
        
    }

    public static class TypeGroup extends CodeableEntity {

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
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
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
     * @return the updateDate
     */
    public Date getUpdateDate() {
        return updateDate;
    }

    /**
     * @param updateDate the updateDate to set
     */
    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    /**
     * @return the excerpt
     */
    public String getExcerpt() {
        return excerpt;
    }

    /**
     * @param excerpt the excerpt to set
     */
    public void setExcerpt(String excerpt) {
        this.excerpt = excerpt;
    }

    /**
     * @return the tags
     */
    public String getTags() {
        return tags;
    }

    /**
     * @param tags the tags to set
     */
    public void setTags(String tags) {
        this.tags = tags;
    }

    /**
     * @return the allowdownload
     */
    public boolean isAllowdownload() {
        return allowdownload;
    }

    /**
     * @param allowdownload the allowdownload to set
     */
    public void setAllowdownload(boolean allowdownload) {
        this.allowdownload = allowdownload;
    }

    /**
     * @return the registryPublic
     */
    public boolean isRegistryPublic() {
        return registryPublic;
    }

    /**
     * @param registryPublic the registryPublic to set
     */
    public void setRegistryPublic(boolean registryPublic) {
        this.registryPublic = registryPublic;
    }

    /**
     * @return the thumbnail
     */
    public URL getThumbnail() {
        return thumbnail;
    }

    /**
     * @param thumbnail the thumbnail to set
     */
    public void setThumbnail(URL thumbnail) {
        this.thumbnail = thumbnail;
    }

    /**
     * @return the links
     */
    public List<Link> getLinks() {
        return links == null ? links = Collections.emptyList() : links;
    }

    /**
     * @param links the links to set
     */
    public void setLinks(List<Link> links) {
        this.links = links;
    }

    /**
     * @return the authors
     */
    public List<User> getAuthors() {
        return authors;
    }

    /**
     * @param authors the authors to set
     */
    public void setAuthors(List<User> authors) {
        this.authors = authors;
    }

    /**
     * @return the rightHolders
     */
    public List<User> getRightHolders() {
        return rightHolders;
    }

    /**
     * @param rightHolders the rightHolders to set
     */
    public void setRightHolders(List<User> rightHolders) {
        this.rightHolders = rightHolders;
    }

    /**
     * @return the license
     */
    public License getLicense() {
        return license;
    }

    /**
     * @param license the license to set
     */
    public void setLicense(License license) {
        this.license = license;
    }

    /**
     * @return the humanUrl
     */
    public URL getHumanUrl() {
        return humanUrl;
    }

    /**
     * @param humanUrl the humanUrl to set
     */
    public void setHumanUrl(URL humanUrl) {
        this.humanUrl = humanUrl;
    }

    /**
     * @return the apiUrl
     */
    public URL getApiUrl() {
        return apiUrl;
    }

    /**
     * @param apiUrl the apiUrl to set
     */
    public void setApiUrl(URL apiUrl) {
        this.apiUrl = apiUrl;
    }
    /**
     * @return the type
     */
    public Type getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(Type type) {
        this.type = type;
    }

    /**
     * @return the typeGroup
     */
    public TypeGroup getTypeGroup() {
        return typeGroup;
    }

    /**
     * @param typeGroup the typeGroup to set
     */
    public void setTypeGroup(TypeGroup typeGroup) {
        this.typeGroup = typeGroup;
    }

    /**
     * @return the language
     */
    public Language getLanguage() {
        return language;
    }

    /**
     * @param language the language to set
     */
    public void setLanguage(Language language) {
        this.language = language;
    }


    @Override
    public String toString() {
        return getClass().getSimpleName()+"[code:"+code+",title:"+title+",type:"+type+",typeGroup:"+typeGroup+",license:"+license+"]";
    }

	public boolean isAllowSale() {
		return allowSale;
	}

	public void setAllowSale(boolean allowSale) {
		this.allowSale = allowSale;
	}

	public boolean isAllowLicensing() {
		return allowLicensing;
	}

	public void setAllowLicensing(boolean allowLicensing) {
		this.allowLicensing = allowLicensing;
	}
}

