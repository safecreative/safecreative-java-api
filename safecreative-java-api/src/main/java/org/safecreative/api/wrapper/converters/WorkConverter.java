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
import org.safecreative.api.wrapper.model.Work.RelationType;
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
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            unmarshalWork(work,reader,context);
            reader.moveUp();
        }
        return work;
    }

    protected boolean unmarshalWork(Work work, HierarchicalStreamReader reader, UnmarshallingContext context) {
        boolean processedNode = false;
        String node = reader.getNodeName();
        log.trace("Unmarshalling node {}",node);
        if (node.equals("entrydate")) {
            Date entryDate = readDate(reader);
            if(entryDate == null) {
                throw new ConversionException("bad entrydate " + reader.getValue());
            }
            work.setEntryDate(entryDate);
            processedNode = true;
        }else
        if (node.equals("updatedate")) {
            Date date = readDate(reader);
            if(date == null) {
                throw new ConversionException("bad updatedate " + reader.getValue());
            }
            work.setUpdateDate(date);
            processedNode = true;
        }else
        if (node.equals("excerpt")) {
            work.setExcerpt(reader.getValue());
            processedNode = true;
        }else
        if (node.equals("tags")) {
            work.setTags(reader.getValue());
            processedNode = true;
        }else
        if (node.equals("thumbnail")) {
            work.setThumbnail(readUrl(reader));
            processedNode = true;
        }else
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
            processedNode = true;
        }else
        if (node.equals("relations")) {
            setWorkRelations(reader,work);
            processedNode = true;
        }else
        if (node.equals("authors")) {
            work.setAuthors(readUsers(reader));
            processedNode = true;
        }else
        if (node.equals("rights-holders")) {
            work.setRightHolders(readUsers(reader));
            processedNode = true;
        }else
        if (node.equals("informers")) {
            work.setInformers(readUsers(reader));
            processedNode = true;
        }else
        if (node.equals("license")) {
            if(licenseConverter == null) {
                //Lazy load
                licenseConverter = new LicenseConverter();
            }
            License license = (License) context.convertAnother(work, License.class, licenseConverter);
            work.setLicense(license);
            processedNode = true;
        }else
        if (node.equals("human-url")) {
            work.setHumanUrl(readUrl(reader));
            processedNode = true;
        }else
        if (node.equals("machine-url")) {
            work.setApiUrl(readUrl(reader));
            processedNode = true;
        }else
        if (node.equals("allowdownload")) {
            work.setAllowDownload(Boolean.valueOf(reader.getValue()));
            processedNode = true;
        }else
        if (node.equals("registrypublic")) {
            work.setRegistryPublic(Boolean.valueOf(reader.getValue()));
            processedNode = true;
        }else
        if (node.equals("allowsale")) {
            work.setAllowSale(Boolean.valueOf(reader.getValue()));
            processedNode = true;
        }else
        if (node.equals("allowlicensing")) {
            work.setAllowLicensing(Boolean.valueOf(reader.getValue()));
            processedNode = true;
        }else
        if (node.equals("worktype")) {
            Work.Type type = new Work.Type();
            reader.moveDown();
            type.setCode(reader.getValue());
            reader.moveUp();
            reader.moveDown();
            type.setName(reader.getValue());
            reader.moveUp();
            work.setType(type);
            processedNode = true;
        }else
        if (node.equals("worktypegroup")) {
            Work.TypeGroup typeGroup = new Work.TypeGroup();
            reader.moveDown();
            typeGroup.setCode(reader.getValue());
            reader.moveUp();
            reader.moveDown();
            typeGroup.setName(reader.getValue());
            reader.moveUp();
            work.setTypeGroup(typeGroup);
            processedNode = true;
        }
        return processedNode;
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
        return users;
    }

    private void setWorkRelations(HierarchicalStreamReader reader, Work work) {
        List<Work> relatedWorks;
        Work related;
        RelationType relationType;
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            relationType = RelationType.valueOf(reader.getAttribute("type"));
            relatedWorks = new LinkedList<Work>();
            while (reader.hasMoreChildren()) {
                reader.moveDown();
                related = new Work();
                related.setTitle(reader.getAttribute("name"));
                related.setCode(reader.getAttribute("code"));
                relatedWorks.add(related);
                reader.moveUp();
            }
            reader.moveUp();
            work.setRelations(relationType,relatedWorks);
        }
    }

}
