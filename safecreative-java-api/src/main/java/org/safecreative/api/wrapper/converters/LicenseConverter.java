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
package org.safecreative.api.wrapper.converters;

import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import java.util.Date;
import org.safecreative.api.wrapper.model.License;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * XStream License converter
 *
 * @author mpolo@safecreative.org
 * @author jguillo@safecreative.org
 */
public class LicenseConverter extends AbstractModelConverter {
    private static Logger log = LoggerFactory.getLogger(LicenseConverter.class);

    public boolean canConvert(Class type) {
        return License.class.equals(type);
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        License license = new License();
        reader.moveDown();
        license.setCode(reader.getValue());
        reader.moveUp();
        reader.moveDown();
        license.setName(reader.getValue());
        reader.moveUp();
        reader.moveDown();
        license.setShortName(reader.getValue());
        reader.moveUp();

        reader.moveDown();
        if(reader.getNodeName().equals("endDate")) {
            Date endDate = readDate(reader);
            license.setEndDate(endDate);
            reader.moveUp();
            reader.moveDown();
        }
        if(reader.getNodeName().equals("human-url")) {
            license.setUrl(readUrl(reader));
            reader.moveUp();
            reader.moveDown();
        }

        String feature;
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            feature = reader.getNodeName().toUpperCase();
            try {
                license.getFeatures().put(License.Feature.valueOf(feature), License.FeatureValue.valueOf(reader.getValue()));
            } catch (Exception ex) {
                log.error("bad license feature " + feature + ":" + reader.getValue(), ex);
            }
            reader.moveUp();
        }
        reader.moveUp();        
        return license;
    }
}
