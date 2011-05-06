package org.safecreative.api.examples.work;

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
import java.io.FileNotFoundException;
import java.io.IOException;

import org.safecreative.api.ApiException;
import org.safecreative.api.examples.util.LoadAPI;
import org.safecreative.api.wrapper.SafeCreativeAPIWrapper;
import org.safecreative.api.wrapper.model.Profile;

/**
 * Register a work file, uses the user's first profile 
 * 
 * @author vcalderon@safecreative.org
 */
public class RegisterWork {

	/**
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws ApiException 
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException, ApiException {
		SafeCreativeAPIWrapper api = LoadAPI.load();
		api.setLocale(null); // error if locale is defined in API Wrapper
		
	    String filePath = "mylocalfile.txt";
	    String workCode;
	    
	    // load first profile
	    Profile profile = api.getProfiles().get(0);
	    
	    File file = new File(filePath);
	    System.out.println(file.exists());
	    workCode = api.workRegister("myfile", file, profile, null);
	    
	    System.out.println("Registered work code: " + workCode);
	}

}
