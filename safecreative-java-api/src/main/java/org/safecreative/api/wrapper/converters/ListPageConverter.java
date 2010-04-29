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
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.safecreative.api.wrapper.ListPage;

/**
 * XStream ListPage converter
 *
 * @param <T>
 * @author mpolo@safecreative.org
 * @author jguillo@safecreative.org
 */
public class ListPageConverter<T> extends AbstractModelConverter {
    private Class<T> clazz;
    private Converter itemConverter;

    public ListPageConverter(Class<T> clazz, Converter itemConverter) {
        this.clazz = clazz;
        this.itemConverter = itemConverter;
    }

    public boolean canConvert(Class type) {
        return ListPage.class.equals(type);
    }

    @SuppressWarnings("unchecked")
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        ListPage<T> listPage = new ListPage<T>();
        reader.moveDown();
        listPage.setRecordTotal(Integer.parseInt(reader.getValue()));
        reader.moveUp();
        reader.moveDown();
        listPage.setPageTotal(Integer.parseInt(reader.getValue()));
        reader.moveUp();
        //Read list items
        reader.moveDown();
        String node = reader.getNodeName();
        while(reader.hasMoreChildren()) {
            reader.moveDown();
            T item = (T)context.convertAnother(listPage, clazz, itemConverter);
            listPage.add(item);
            reader.moveUp();
        }
        reader.moveUp();
        return listPage;
    }
}
