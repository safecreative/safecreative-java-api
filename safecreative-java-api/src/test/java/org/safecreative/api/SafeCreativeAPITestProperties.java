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
package org.safecreative.api;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import org.apache.commons.lang.StringUtils;

/**
 * Manages the testing properties.
 * @author mpolo@safecreative.org
 */
public class SafeCreativeAPITestProperties {
    private final static SafeCreativeAPITestProperties instance = new SafeCreativeAPITestProperties();
    private final static String PROPERTY_FILE   =  "safecreative-api-tests.properties";

    private final static String SHARED_KEY      =  "sharedKey";
    private final static String PRIVATE_KEY     =  "privateKey";
    private final static String BASE_URL        =  "baseUrl";
    
    private Properties properties;

    private SafeCreativeAPITestProperties() {
    }

    public static SafeCreativeAPITestProperties getInstance() {
        return instance;
    }

    public String getSharedKey() {
        return getProperty(SHARED_KEY);
    }

    public String getPrivateKey() {
        return getProperty(PRIVATE_KEY);
    }

    public String getBaseUrl() {
        return getProperty(BASE_URL);
    }


    private String getProperty(String name) {
        if(properties == null) {
            loadProperties();
        }
        String value = properties.getProperty(name);
        if(StringUtils.isEmpty(value)) {
            throw new RuntimeException(String.format("Empty property %s",name));
        }
        return value;
    }

    private void loadProperties() {
        properties = new Properties();
        String home = System.getProperty("user.home");
        File propertyFile = new File(new File(home),PROPERTY_FILE);
        try {
            properties.load(new FileReader(propertyFile));
        } catch (IOException ex) {
            throw new RuntimeException("Loading property file "+propertyFile,ex);
        }
    }



}
