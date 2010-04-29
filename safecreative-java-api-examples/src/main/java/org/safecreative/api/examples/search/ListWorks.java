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
package org.safecreative.api.examples.search;

import java.util.List;
import java.util.Locale;
import org.safecreative.api.wrapper.ListPage;
import org.safecreative.api.wrapper.SafeCreativeAPIWrapper;
import org.safecreative.api.wrapper.SearchMethod;
import org.safecreative.api.wrapper.model.Work;

/**
 * Simple search by work type
 *
 * @author mpolo@safecreative.org
 */
public class ListWorks {

    public static void main(String[] args) throws Exception {
        //No api keys needed for public search
        SafeCreativeAPIWrapper api = new SafeCreativeAPIWrapper(null,null);
        api.setLocale(Locale.getDefault());
        String workType = null;
        if(args.length == 1) {
            workType = args[0];
        } else {
            //Show available work type codes:
            List<Work.Type> workTypes = api.getWorkTypes();
            StringBuilder codes = new StringBuilder();            
            boolean firstCode = false;
            for(Work.Type type : workTypes) {
                if(firstCode) {
                    codes.append(" | ");
                }
                codes.append(type.getCode());
                firstCode = true;
            }
            System.out.println("Usage ListWorks ["+codes+"]");
            System.exit(1);
        }
        ListPage<Work> works =  api.searchWorksByFields(
                SearchMethod.WORK_TYPE,workType,
                SearchMethod.DOWNLOADABLE,true
        );
        for(Work work : works) {
            System.out.println("Found "+work);
        }
    }

}
