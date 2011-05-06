package org.safecreative.api.examples.user;

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

import java.io.FileNotFoundException;
import java.io.IOException;

import org.safecreative.api.ApiException;
import org.safecreative.api.examples.util.LoadAPI;
import org.safecreative.api.wrapper.SafeCreativeAPIWrapper;
import org.safecreative.api.wrapper.model.User;
import org.safecreative.api.wrapper.model.Work;

/**
 * Get author's code and name from work code
 * 
 * @author vcalderon@safecreative.org
 */
public class AuthorWorkCode {

	/**
	 * @throws ApiException 
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException, ApiException {
		SafeCreativeAPIWrapper api = LoadAPI.loadPublic();
		
	    String workCode = "1103290161723";
	    Work work = api.getWork(workCode);
	    
	    System.out.println("Work code: " + work.getCode());
	    for(User author : work.getAuthors()) {
            System.out.println("Author code: " + author.getCode() + " Name: " + author.getName());
        }

	}

}
