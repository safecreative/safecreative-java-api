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

import java.net.URL;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.safecreative.api.wrapper.model.License;
import org.safecreative.api.wrapper.model.Link;
import org.safecreative.api.wrapper.model.User;
import org.safecreative.api.wrapper.model.Work;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;

/**
 * XStream Work converter
 *
 * @author mpolo@safecreative.org
 * @author jguillo@safecreative.org
 */
public class WorkConverter extends AbstractModelConverter {

    private static Logger log = LoggerFactory.getLogger(WorkConverter.class);
    private LicenseConverter licenseConverter;

    public boolean canConvert(Class type) {
        return Work.class.equals(type);
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        Work work = new Work();
        //Code
        reader.moveDown();
        work.setCode(reader.getValue());
        reader.moveUp();
        //Title
        reader.moveDown();
        work.setTitle(reader.getValue());
        reader.moveUp();
        String node;
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            node = reader.getNodeName();
            log.trace("Unmarshalling node {}",node);
            if (node.equals("entrydate")) {
                Date entryDate = readDate(reader);
                if(entryDate == null) {
                    throw new ConversionException("bad entrydate " + reader.getValue());
                }
                work.setEntryDate(entryDate);
                reader.moveUp();
                continue;
            }
            if (node.equals("updatedate")) {
                Date date = readDate(reader);
                if(date == null) {
                    throw new ConversionException("bad updatedate " + reader.getValue());
                }
                work.setUpdateDate(date);
                reader.moveUp();
                continue;
            }
            if (node.equals("excerpt")) {
                work.setExcerpt(reader.getValue());
                reader.moveUp();
                continue;
            }
            if (node.equals("tags")) {
                work.setTags(reader.getValue());
                reader.moveUp();
                continue;
            }
            if (node.equals("thumbnail")) {
                work.setThumbnail(readUrl(reader));
                reader.moveUp();
                continue;
            }
            if (node.equals("links")) {
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
            if (node.equals("relations")) {
                reader.moveDown();
                reader.moveUp();
                continue;
            }
            if (node.equals("authors")) {
                work.setAuthors(readUsers(reader));
                continue;
            }
            if (node.equals("rights-holders")) {
                work.setRightHolders(readUsers(reader));
                continue;
            }
            if (node.equals("license")) {
                if(licenseConverter == null) {
                    //Lazy load
                    licenseConverter = new LicenseConverter();
                }
                License license = (License) context.convertAnother(work, License.class, licenseConverter);
                work.setLicense(license);
                continue;
            }
            if (node.equals("human-url")) {
                work.setHumanUrl(readUrl(reader));
                reader.moveUp();
                continue;
            }
            if (node.equals("machine-url")) {
                work.setApiUrl(readUrl(reader));
                reader.moveUp();
                continue;
            }
            if (node.equals("allowdownload")) {
                work.setAllowdownload(Boolean.valueOf(reader.getValue()));
                reader.moveUp();
                continue;
            }
            if (node.equals("worktype")) {
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
            if (node.equals("worktypegroup")) {
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
            String node;
            while (reader.hasMoreChildren()) {
                reader.moveDown();
                node = reader.getNodeName();
                if (node.equals("human-url")) {
                    user.setProfileUrl(readUrl(reader));
                }
                if (node.equals("image")) {
                    user.setThumbnailUrl(readUrl(reader));
                }
                reader.moveUp();
            }
            users.add(user);
            reader.moveUp();
        }
        reader.moveUp();
        return users;
    }




}
