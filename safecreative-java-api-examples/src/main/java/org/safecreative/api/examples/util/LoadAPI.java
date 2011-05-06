package org.safecreative.api.examples.util;

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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

import org.safecreative.api.wrapper.SafeCreativeAPIWrapper;

/**
 * @author vcalderon@safecreative.org
 */
public class LoadAPI {

	/**
	 * Load and configure API Wrapper for Arena from keys's file
	 * 
	 * @return API Wrapper
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static SafeCreativeAPIWrapper load() throws FileNotFoundException, IOException {
		// load keys file
		String keysPath = "arena.keys";
		File f = new File(keysPath);
		
		// read keys
        Properties p = new Properties();
        p.load(new FileInputStream(f));
        String sharedkey = p.getProperty("sharedkey");
        String privatekey = p.getProperty("privatekey");
        String authkey = p.getProperty("authkey");
        String authprivatekey = p.getProperty("authprivatekey");
		
		// load API
		SafeCreativeAPIWrapper api = new SafeCreativeAPIWrapper(sharedkey, privatekey);
		api.setBaseUrl(SafeCreativeAPIWrapper.ARENA_URL); //use arena test server
		api.setBaseSearchUrl(SafeCreativeAPIWrapper.ARENA_URL);
		api.setLocale(new Locale("es"));
		api.setAuthKey(authkey, authprivatekey);
		
		return api;
	}
	
	/**
	 * Load and configure API Wrapper for Arena without keys
	 * 
	 * @return API Wrapper
	 */
	public static SafeCreativeAPIWrapper loadPublic() {
		SafeCreativeAPIWrapper api = new SafeCreativeAPIWrapper(null, null);
		api.setBaseUrl(SafeCreativeAPIWrapper.ARENA_URL); //use arena test server
		api.setBaseSearchUrl(SafeCreativeAPIWrapper.ARENA_URL);
		api.setLocale(new Locale("es"));
		
		return api;
	}

}
