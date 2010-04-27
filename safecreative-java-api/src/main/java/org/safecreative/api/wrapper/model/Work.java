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

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 *
 * @author mpolo@safecreative.org
 * @author jguillo@safecreative.org
 */
public class Work {
    private String code;
    private String title;
    private Date entryDate;
    private String excerpt;
    private String tags;
    private boolean allowdownload;
    private String thumbnail;
    private List<Link> links;
    private List<User> authors;
    private List<User> rightHolders;
    private License license;
    private Type worktype;
    private TypeGroup worktypegroup;


    public static class Type {
        private String code;
        private String name;

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
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * @param name the name to set
         */
        public void setName(String name) {
            this.name = name;
        }
        
    }

    public static class TypeGroup extends Type {

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
     * @return the thumbnail
     */
    public String getThumbnail() {
        return thumbnail;
    }

    /**
     * @param thumbnail the thumbnail to set
     */
    public void setThumbnail(String thumbnail) {
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
     * @return the worktype
     */
    public Type getWorktype() {
        return worktype;
    }

    /**
     * @param worktype the worktype to set
     */
    public void setWorktype(Type worktype) {
        this.worktype = worktype;
    }

    /**
     * @return the worktypegroup
     */
    public TypeGroup getWorktypegroup() {
        return worktypegroup;
    }

    /**
     * @param worktypegroup the worktypegroup to set
     */
    public void setWorktypegroup(TypeGroup worktypegroup) {
        this.worktypegroup = worktypegroup;
    }


}

