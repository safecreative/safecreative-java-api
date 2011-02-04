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

import org.safecreative.api.wrapper.model.DownloadInfo;
import org.safecreative.api.wrapper.model.DownloadInfo.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;

/**
 * XStream DownloadInfo converter
 *
 * @author mpolo@safecreative.org
 */
public class DownloadInfoConverter extends AbstractModelConverter {
    private static Logger log = LoggerFactory.getLogger(DownloadInfoConverter.class);

    public boolean canConvert(Class type) {
        return DownloadInfo.class.equals(type);
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        DownloadInfo info = new DownloadInfo();
        while(reader.hasMoreChildren()) {
            reader.moveDown();
            if("url".equals(reader.getNodeName())) {
                info.setUrl(readUrl(reader));
            } else if("mimetype".equals(reader.getNodeName())) {
                info.setMimeType(reader.getValue());
            } else if("type".equals(reader.getNodeName())) {
            	try {
	            	Type t = Enum.valueOf(Type.class, reader.getValue());
	                info.setType(t);
            	}
            	catch (IllegalArgumentException e) {}
            }
            reader.moveUp();
        }
        return info;
    }

}