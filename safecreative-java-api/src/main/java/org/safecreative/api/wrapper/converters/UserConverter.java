/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.safecreative.api.wrapper.converters;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import java.util.Date;
import org.safecreative.api.wrapper.model.Country;
import org.safecreative.api.wrapper.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * XStream User converter
 *
 * @author mpolo@safecreative.org
 */
public class UserConverter extends AbstractModelConverter {
    private static Logger log = LoggerFactory.getLogger(UserConverter.class);

    public boolean canConvert(Class type) {
        return User.class.equals(type);
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        User user = new User();
        //Code
        reader.moveDown();
        user.setCode(reader.getValue());
        reader.moveUp();
        //Name
        reader.moveDown();
        user.setName(reader.getValue());
        reader.moveUp();
        String node;
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            unmarshalUser(user, reader, context);
            reader.moveUp();
        }
        return user;
    }

    protected boolean unmarshalUser(User user, HierarchicalStreamReader reader, UnmarshallingContext context) {
        boolean processedNode = false;
        String node = reader.getNodeName();
        log.trace("Unmarshalling node {}",node);
        if (node.equals("country")) {
            Country country = new Country();
            reader.moveDown();
            country.setCode(reader.getValue());
            reader.moveUp();
            reader.moveDown();
            country.setName(reader.getValue());
            user.setCountry(country);
            reader.moveUp();
        }else
        if (node.equals("email")) {
            user.setEmail(reader.getValue());
        }else
        if (node.equals("human-url")) {
            user.setProfileUrl(readUrl(reader));
        }else
        if (node.equals("entrydate")) {
            Date entryDate = readDate(reader);
            if(entryDate == null) {
                throw new ConversionException("bad entrydate " + reader.getValue());
            }
            user.setEntryDate(entryDate);
        }else
        if (node.equals("thumbnail")) {
            user.setThumbnailUrl(readUrl(reader));
        }
        return processedNode;
    }
}
