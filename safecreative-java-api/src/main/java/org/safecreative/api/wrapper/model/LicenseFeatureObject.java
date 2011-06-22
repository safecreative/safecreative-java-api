package org.safecreative.api.wrapper.model;

import java.util.EnumMap;
import org.safecreative.api.wrapper.model.License.Feature;

/**
 * Class used for store locale-specific values
 */
public class LicenseFeatureObject extends CodeableEntity {

    private License.Feature feature;
    // relation of usable FeatureValues for this Feature
    private EnumMap<License.FeatureValue, Boolean> useValues = new EnumMap<License.FeatureValue, Boolean>(License.FeatureValue.class);
    private String shortName;

    public LicenseFeatureObject(License.Feature feature) {
        this.feature = feature;

        // default to false
        resetUseValues();
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public Feature getFeature() {
        return feature;
    }

    public EnumMap<License.FeatureValue, Boolean> getUseValues() {
        return useValues;
    }

    /**
     * Sets all use values to false
     */
    private void resetUseValues() {
        for (License.FeatureValue value : License.FeatureValue.values()) {
            useValues.put(value, Boolean.FALSE);
        }
    }
}
