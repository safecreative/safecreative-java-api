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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.safecreative.api.wrapper.model.CodeableEntity;
import org.safecreative.api.wrapper.model.License;
import org.safecreative.api.wrapper.model.Link;
import org.safecreative.api.wrapper.model.Metadata;
import org.safecreative.api.wrapper.model.User;

/**
 * Represents a work
 * 
 * @author mpolo@safecreative.org
 * @author jguillo@safecreative.org
 */
public class Work implements Serializable{
    public enum RelationType {
		VERSION, DERIVATION, COMPOSITION, RELATED
	}
    public enum WorkState {
		PROCESSING, PRE_REGISTERED, REGISTERED
	}
    private String code;
    private String title;
    private Date entryDate;
    private Date updateDate;
    private String excerpt;
    private String tags;
    private Boolean allowDownload;
    private Boolean allowEvaluation;
    private Boolean allowSale;
    private Boolean allowLicensing;
    private Boolean registryPublic;
    private URL thumbnail;
    private List<Link> links;
    private List<User> authors;
    private List<User> rightHolders;
    private List<User> informers;
    private Map<RelationType,List<Work>> relationMap;
    private Metadata metadata;
    private License license;
    private URL humanUrl;
    private URL apiUrl;
    private String mimeType;
    private Type type;
    private TypeGroup typeGroup;
    private Language language;
    private WorkState state;
    private String observations;
    private Boolean useAlias;
    private String userAlias;
    private Boolean userAuthor;
    private Boolean userRights;

    public static class Language extends CodeableEntity {
    	/**
		 * Constructor with given code
		 * @param code 
		 */
		public Language(String code) {
			super(code);
		}       
		
    	    	
		/**
		 * Create a new instance from code
		 * @param code
		 * @return new Language instance initialized with code
		 */
		public static Language fromCode(String code) {
			return new Language(code);
		}
    }

    public static class Type extends CodeableEntity {
        /**
         * Common work type codes:
         */
    	// LITERARY
        public static final String ARTICLE      	=   "article";
        public static final String NARRATIVE    	=   "narrative";
        public static final String TECHNICAL     	=   "technical";
        public static final String EDUCATION      	=   "education";
        public static final String LITERARY_OTHER	=   "literature";
        
        // AUDIOVISUAL
        public static final String FEATURE_FILM =   "ffilm";
        public static final String MEDIUM_FILM	=   "mfilm";
        public static final String SHORT_FILM 	=   "sfilm";
        public static final String TV_FILM		=   "tvfilm";
        public static final String SERIAL		=   "serial";
        public static final String TELENOVELA 	=   "telenovela";
        public static final String DOCUMENTARY 	=   "documental";
        public static final String CARTOON      =   "cartoon";
        public static final String ANIMATION 	=   "animation";
        public static final String FORMAT 		=   "format";
        public static final String SCRIPT 		=   "script";
        public static final String VIDEO 		=   "video";
        
        // AUDIO
        public static final String MUSIC        =   "music";
        public static final String PODCAST      =   "podcast";
        public static final String AUDIO_OTHER  =   "audio";
        
        // VISUAL ARTS
        public static final String DRAWING      	=   "drawing";
        public static final String PHOTO        	=   "photo";
        public static final String DRAMA 			=   "drama";
        public static final String MULTIMEDIA   	=   "multimedia";
        public static final String MODEL_3D			=   "model3D";
        public static final String SCULPTURE 		=   "sculpture";
        public static final String ARTISTIC_OTHER 	=   "artistic";
        
        // PROJECTS
        public static final String ARCHITECTURE		=   "architecture";
        public static final String SOFTWARE     	=   "software";
        public static final String MODEL			=   "model";
        public static final String PROJECT_OTHER 	=   "project";

		/**
		 * Constructor with given code
		 * @param code 
		 */
		public Type(String code) {
			super(code);
		}       
		
		/**
		 * Create a new instance from code
		 * @param code
		 * @return new Type instance initialized with code
		 */
		public static Type fromCode(String code) {
			return new Type(code);
		}
    }

    public static class TypeGroup extends CodeableEntity {
        private List<Type> workTypes;

        /**
         * @return the workTypes
         */
        public List<Type> getWorkTypes() {
            return workTypes;
        }

        /**
         * @param workTypes the workTypes to set
         */
        public void setWorkTypes(List<Type> workTypes) {
            this.workTypes = workTypes;
        }
    }

	/**
	 * Default constructor
	 */
	public Work() {
	}

	/**
	 * Constructor with given code
	 * @param code 
	 */	
	public Work(String code)  {
		this.code = code;
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
    public Boolean isAllowDownload() {
        return allowDownload;
    }

    /**
     * @param allowdownload the allowdownload to set
     */
    public void setAllowDownload(Boolean allowDownload) {
        this.allowDownload = allowDownload;
    }

    /**
     * @return the registryPublic
     */
    public Boolean isRegistryPublic() {
        return registryPublic;
    }

    /**
     * @param registryPublic the registryPublic to set
     */
    public void setRegistryPublic(Boolean registryPublic) {
        this.registryPublic = registryPublic;
    }

    /**
     * @return the allowSale
     */
    public Boolean isAllowSale() {
        return allowSale;
    }

    /**
     * @param allowSale the allowSale to set
     */
    public void setAllowSale(Boolean allowSale) {
        this.allowSale = allowSale;
    }

    /**
     * @return the allowLicensing
     */
    public Boolean isAllowLicensing() {
        return allowLicensing;
    }

    /**
     * @param allowLicensing the allowLicensing to set
     */
    public void setAllowLicensing(Boolean allowLicensing) {
        this.allowLicensing = allowLicensing;
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
	 * Adds a link
	 * @param name Name of link
	 * @param type Type of link
	 * @param url Url of link
	 */
	public void addLink(String name, Link.Type type, String url) throws MalformedURLException {
		addLink(name, type, new URL(url));
	}

	
	/**
	 * Adds a link
	 * @param name Name of link
	 * @param type Type of link
	 * @param url <code>URL</code> of link
	 */
	public void addLink(String name, Link.Type type, URL url) {
		addLink(new Link(name, type, url));
	}
	
	/**
	 * Add a link
	 */
	public void addLink(Link link) {
		getLinks().add(link);
	}

    /**
     * @return the links
     */
    public List<Link> getLinks() {
        return links == null ? links = new LinkedList<Link>() : links;
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
     * @return the informers
     */
    public List<User> getInformers() {
        return informers;
    }

    /**
     * @param informers the informers to set
     */
    public void setInformers(List<User> informers) {
        this.informers = informers;
    }


    /**
     * Sets a list of related works
     * @param relationType
     * @param works
     */
    public void setRelations(RelationType relationType,List<Work> works) {
        if(relationMap == null) {
            relationMap = new HashMap<RelationType,List<Work>>();
        }
        relationMap.put(relationType, works);
    }

    /**
     * Gets a list of related works
     * @param relationType
     * @return
     */
    public List<Work> getRelations(RelationType relationType) {
        if(relationMap == null || !relationMap.containsKey(relationType)) {
            setRelations(relationType, new LinkedList<Work>());
        }
        return relationMap.get(relationType);
    }

    /**
	 * @return the metadata
	 */
	public Metadata getMetadata() {
		if(metadata == null) {
			metadata = new Metadata();
		}
		return metadata;
	}

	/**
	 * @param metadata the metadata to set
	 */
	public void setMetadata(Metadata metadata) {
		this.metadata = metadata;
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
     * @return the mimeType
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * @param mimeType the mimeType to set
     */
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
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

    /**
     * @return allowEvaluation
     */
	public Boolean isAllowEvaluation() {
		return allowEvaluation;
	}

	/**
	 * @param allowEvaluation the allowEvaluation to set
	 */
	public void setAllowEvaluation(Boolean allowEvaluation) {
		this.allowEvaluation = allowEvaluation;
	}

    /**
     * @return the observations
     */
    public String getObservations() {
        return observations;
    }

    /**
     * @param observations the observations to set
     */
    public void setObservations(String observations) {
        this.observations = observations;
    }

    /**
     * @return the workState
     */
    public WorkState getState() {
        return state;
    }

    /**
     * @param state the workState to set
     */
    public void setState(WorkState state) {
        this.state = state;
    }

    /**
     * @return the useAlias
     */
    public Boolean isUseAlias() {
        return useAlias;
    }

    /**
     * @param useAlias the useAlias to set
     */
    public void setUseAlias(Boolean useAlias) {
        this.useAlias = useAlias;
    }

    /**
     * @return the userAlias
     */
    public String getUserAlias() {
        return userAlias;
    }

    /**
     * @param userAlias the userAlias to set
     */
    public void setUserAlias(String userAlias) {
        this.userAlias = userAlias;
    }

    /**
     * @return the userAuthor
     */
    public Boolean isUserAuthor() {
        return userAuthor;
    }

    /**
     * @param userAuthor the userAuthor to set
     */
    public void setUserAuthor(Boolean userAuthor) {
        this.userAuthor = userAuthor;
    }

    /**
     * @return the userRights
     */
    public Boolean isUserRights() {
        return userRights;
    }

    /**
     * @param userRights the userRights to set
     */
    public void setUserRights(Boolean userRights) {
        this.userRights = userRights;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName()+"[code:"+code+",title:"+title+",type:"+type+",typeGroup:"+typeGroup+",license:"+license+"]";
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        Work other = (Work) o;
        return this.code.equals(other.code);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + (this.code != null ? this.code.hashCode() : 0);
        return hash;
    }

}

