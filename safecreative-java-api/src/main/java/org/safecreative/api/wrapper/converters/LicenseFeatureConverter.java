/*
Copyright (c) 2011 Safe Creative (http://www.safecreative.org)

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

package org.safecreative.api.wrapper.converters;

import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import org.safecreative.api.wrapper.model.License;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * XStream License Feature converter
 *
 * @author vcalderon@safecreative.org
 */
public class LicenseFeatureConverter extends AbstractModelConverter {
    private static Logger log = LoggerFactory.getLogger(LicenseConverter.class);
    
    public boolean canConvert(Class type) {
        return License.Feature.class.equals(type);
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        if(!reader.hasMoreChildren()) {
            return null;
        }
        License.Feature feature;

        reader.moveDown();
        String code = reader.getValue();
        feature = License.Feature.valueOf(code.toUpperCase());
        feature.setCode(code);
        reader.moveUp();

        reader.moveDown();
        feature.setName(reader.getValue());
        reader.moveUp();

        reader.moveDown();
        feature.setShortName(reader.getValue());
        reader.moveUp();

        reader.moveDown();

        // reset usage to false
        feature.resetUseValues();

        String valueStr;
        License.FeatureValue value;

        // read values
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            valueStr = reader.getValue().toUpperCase();

            try {
                value = License.FeatureValue.valueOf(valueStr);
                feature.getUseValues().put(value, Boolean.TRUE);
            } catch (Exception ex) {
                log.error("bad feature value " + valueStr + ":" + reader.getValue(), ex);
            }
            reader.moveUp();
        }
        reader.moveUp();

        return feature;
    }
}
