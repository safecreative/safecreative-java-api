/*
	Copyright 2010 Safe Creative

	This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.safecreative.api.examples.profiles;

import org.safecreative.api.SafeCreativeAPI.AuthkeyLevel;
import org.safecreative.api.wrapper.SafeCreativeAPIWrapper;
import org.safecreative.api.wrapper.model.AuthKey;
import org.safecreative.api.wrapper.model.Profile;

/**
 * List defined user's registry profiles
 * 
 * @author mpolo@safecreative.org
 */
public class ListProfiles {

    public static void main(String[] args) throws Exception {
        if(args.length != 2) {
            System.err.println("Usage ListProfiles sharedKey privateKey");
            System.exit(1);
        }
        SafeCreativeAPIWrapper api = new SafeCreativeAPIWrapper(args[0],args[1]);
        api.setBaseUrl(SafeCreativeAPIWrapper.ARENA_URL); //use arena test server
        AuthKey authKey = api.createAuth(AuthkeyLevel.GET);
        System.out.println("Go to authorize url: "+authKey.getManageUrl()+" and press ENTER key to continue");
        System.in.read(); //Wait for key
        for(Profile profile : api.getProfiles(authKey)) {
            System.out.println("Profile code: "+profile.getCode() + " Name: " + profile);
        }
    }
}
