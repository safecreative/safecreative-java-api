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

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * XStream base converter class
 *
 * @author mpolo@safecreative.org
 * @author jguillo@safecreative.org
 */
public abstract class AbstractModelConverter implements Converter{
    private static Logger log = LoggerFactory.getLogger(AbstractModelConverter.class);
    
    private final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        throw new UnsupportedOperationException("Not supported");
    }

    protected Date readDate(HierarchicalStreamReader reader) {
        Date result = null;
        try {
            result = dateFormat.parse(reader.getValue());
        } catch (ParseException ex) {

        }
        return result;
    }

    protected URL readUrl(HierarchicalStreamReader reader) {
        URL result = null;
        try {
            result = new URL(reader.getValue());
        } catch (MalformedURLException ex) {
            log.error(String.format("bad url %s for element %s", reader.getValue(), reader.getNodeName()), ex);
        }
        return result;
    }
}
