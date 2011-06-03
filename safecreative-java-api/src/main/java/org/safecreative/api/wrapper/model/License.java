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
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a license.
 *
 * @author mpolo@safecreative.org
 * @author jguillo@safecreative.org
 */
public class License extends CodeableEntity {
    
    public enum Feature {
        RECOGNITION,
        DERIVATIONS,
        DISTRIBUTION,
        COMMERCIAL,
        TIMELIMITED;

        /**
         * Class used for store locale-specific values
         */
        private final class FeatureObject extends CodeableEntity {
            // relation of usable FeatureValues for this Feature
            private EnumMap<FeatureValue, Boolean> useValues =
                    new EnumMap<FeatureValue, Boolean>(FeatureValue.class);
            private String shortName;

            private FeatureObject() {
                // default to false
                resetUseValues();
            }

            public String getShortName() {
                return shortName;
            }

            public void setShortName(String shortName) {
                this.shortName = shortName;
            }

            public EnumMap<FeatureValue, Boolean> getUseValues() {
                return useValues;
            }

            /**
             * Sets all use values to false
             */
            public void resetUseValues() {
                for (FeatureValue value : FeatureValue.values()) {
                    useValues.put(value, Boolean.FALSE);
                }
            }
        }

        private FeatureObject object = new FeatureObject();

        public String getCode() {
            return object.getCode();
        }

        public void setCode(String code) {
            object.setCode(code);
        }

        public String getName() {
            return object.getName();
        }

        public void setName(String name) {
            object.setName(name);
        }

        public String getShortName() {
            return object.getShortName();
        }

        public void setShortName(String shortName) {
            object.setShortName(shortName);
        }

        public EnumMap<FeatureValue, Boolean> getUseValues() {
            return object.getUseValues();
        }

        /**
         * Sets all use values to false
         */
        public void resetUseValues() {
            object.resetUseValues();
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() + "[code:" + getCode() + ",name:" + getName() + "]";
        }
    }
    
	public enum FeatureValue {
		NOT_APPLICABLE, YES, YES_WITH_RESTRICTIONS, NO, INHERITANCE
	}

    private String shortName;
    private Date endDate;
    private URL url;
    private Country jurisdiction;
    private Map<Feature,FeatureValue> features;

    /**
     * @return the url
     */
    public URL getUrl() {
        return url;
    }

    /**
     * @param url the url to set
     */
    public void setUrl(URL url) {
        this.url = url;
    }

    /**
     * @return the shortName
     */
    public String getShortName() {
        return shortName;
    }

    /**
     * @param shortName the shortName to set
     */
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    /**
     * @return the endDate
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * @param endDate the endDate to set
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
     * @return the jurisdiction
     */
    public Country getJurisdiction() {
        return jurisdiction;
    }

    /**
     * @param jurisdiction the jurisdiction to set
     */
    public void setJurisdiction(Country jurisdiction) {
        this.jurisdiction = jurisdiction;
    }

    /**
     * @return the features
     */
    public Map<Feature,FeatureValue> getFeatures() {
        if(features == null) {
            features = new HashMap<Feature,FeatureValue>(Feature.values().length);
        }
        return features;
    }

    /**
     * Gets the license feature's value
     * @param feature
     * @return feature value
     */
    public FeatureValue getFeature(Feature feature) {
        return getFeatures().get(feature);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName()+"[code:"+getCode()+",shortName:"+shortName+",url:"+url+"]";
    }
}
