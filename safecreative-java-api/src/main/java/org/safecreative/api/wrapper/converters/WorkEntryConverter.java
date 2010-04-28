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

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.safecreative.api.wrapper.model.License;

import org.safecreative.api.wrapper.model.Link;
import org.safecreative.api.wrapper.model.User;
import org.safecreative.api.wrapper.model.Work;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * XStream Work converter
 *
 * @author mpolo@safecreative.org
 * @author jguillo@safecreative.org
 */
public class WorkEntryConverter implements Converter {

    private static Logger log = LoggerFactory.getLogger(WorkEntryConverter.class);
    private final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    public boolean canConvert(Class type) {
        return Work.class.equals(type);
    }

    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        throw new UnsupportedOperationException("Not supported");
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        Work work = new Work();
        reader.moveDown();
        work.setCode(reader.getValue());
        reader.moveUp();
        reader.moveDown();
        work.setTitle(reader.getValue());
        reader.moveUp();
        reader.moveDown();
        try {
            work.setEntryDate(dateFormat.parse(reader.getValue()));
        } catch (ParseException ex) {
            throw new ConversionException("bad entrydate " + reader.getValue(), ex);
        }
        reader.moveUp();
        reader.moveDown();
        work.setExcerpt(reader.getValue());
        reader.moveUp();
        reader.moveDown();
        work.setTags(reader.getValue());
        reader.moveUp();

        while (reader.hasMoreChildren()) {
            reader.moveDown();
            if (reader.getNodeName().equals("thumbnail")) {
                work.setThumbnail(reader.getValue());
                reader.moveUp();
                continue;
            }
            if (reader.getNodeName().equals("links")) {
                List<Link> links = new LinkedList<Link>();
                Link link;
                while (reader.hasMoreChildren()) {
                    reader.moveDown();
                    link = new Link();
                    link.setName(reader.getAttribute("name"));
                    link.setType(Link.Type.valueOf(reader.getAttribute("type")));
                    URL url = readUrl(reader);
                    link.setUrl(url);
                    if (url != null) {
                        links.add(link);
                    }
                    reader.moveUp();
                }
                work.setLinks(links);
                reader.moveUp();
                continue;
            }
            if (reader.getNodeName().equals("relations")) {
                reader.moveDown();
                reader.moveUp();
                continue;
            }
            if (reader.getNodeName().equals("authors")) {
                work.setAuthors(readUsers(reader));
                continue;
            }
            if (reader.getNodeName().equals("rights-holders")) {
                work.setRightHolders(readUsers(reader));
                continue;
            }
            if (reader.getNodeName().equals("license")) {
                work.setLicense(readLicense(reader));
                continue;
            }
            if (reader.getNodeName().equals("human-url")) {
                work.setHumanUrl(readUrl(reader));
                reader.moveUp();
                continue;
            }
            if (reader.getNodeName().equals("machine-url")) {
                work.setApiUrl(readUrl(reader));
                reader.moveUp();
                continue;
            }
            if (reader.getNodeName().equals("allowdownload")) {
                work.setAllowdownload(Boolean.valueOf(reader.getValue()));
                reader.moveUp();
                continue;
            }
            if (reader.getNodeName().equals("worktype")) {
                Work.Type type = new Work.Type();
                reader.moveDown();
                type.setCode(reader.getValue());
                reader.moveUp();
                reader.moveDown();
                type.setName(reader.getValue());
                reader.moveUp();
                work.setType(type);
                reader.moveUp();
                continue;
            }
            if (reader.getNodeName().equals("worktypegroup")) {
                Work.TypeGroup typeGroup = new Work.TypeGroup();
                reader.moveDown();
                typeGroup.setCode(reader.getValue());
                reader.moveUp();
                reader.moveDown();
                typeGroup.setName(reader.getValue());
                reader.moveUp();
                work.setTypeGroup(typeGroup);
                reader.moveUp();
                continue;
            }
            reader.moveUp();
        }
        return work;
    }

    private List<User> readUsers(HierarchicalStreamReader reader) {
        List<User> users = new LinkedList<User>();

        while (reader.hasMoreChildren()) {
            reader.moveDown();
            User user = new User();
            reader.moveDown();
            user.setCode(reader.getValue());
            reader.moveUp();
            reader.moveDown();
            user.setName(reader.getValue());
            reader.moveUp();
            reader.moveDown();
            user.setUrl(readUrl(reader));
            reader.moveUp();
            users.add(user);
        }
        reader.moveUp();
        reader.moveUp();
        return users;
    }

    private License readLicense(HierarchicalStreamReader reader) {
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
        try {
            license.setEndDate(StringUtils.isBlank(reader.getValue()) ? null : dateFormat.parse(reader.getValue()));
        } catch (ParseException ex) {
            log.error("bad entrydate " + reader.getValue(), ex);
        }
        reader.moveUp();
        reader.moveDown();
        license.setUrl(readUrl(reader));
        reader.moveUp();
        reader.moveDown();
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
        reader.moveUp();
        return license;
    }

    private URL readUrl(HierarchicalStreamReader reader) {
        URL result = null;
        try {
            result = new URL(reader.getValue());
        } catch (MalformedURLException ex) {
            log.error(String.format("bad url %s for element %s", reader.getValue(), reader.getNodeName()), ex);
        }
        return result;
    }
}
