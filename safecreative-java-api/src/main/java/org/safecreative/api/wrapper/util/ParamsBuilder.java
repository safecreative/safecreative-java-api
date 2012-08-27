/*
Copyright (c) 2010-2012 Safe Creative (http://www.safecreative.org)

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

package org.safecreative.api.wrapper.util;

import java.util.List;
import java.util.Map;
import org.safecreative.api.wrapper.model.Link;
import org.safecreative.api.wrapper.model.Work;

/**
 * Class to build api call parameters
 *
 * @author mpolo@safecreative.org
 * @author vcalderon@safecreative.org
 */
public class ParamsBuilder {

    /**
     * @param params existing parameter set
     * @param work work from with extract parameters
     * @return existing parameter set with 'work' parameters
     */
    public static Map<String, String> buildWorkParams(Map<String, String> params, Work work) {
		if(Work.WorkState.REGISTERED.equals(work.getState())) {
			params.put("final", "1");
		}
        // add editable fields only if in pre-register or not defined
        if (work.getState() == null || work.getState() == Work.WorkState.PRE_REGISTERED) {
            if (work.getTitle() != null) {
                params.put("title", work.getTitle());
            }
            if (work.getUserAlias() != null) {
                params.put("alias", work.getUserAlias());
            }
        }
        if (work.getCode() != null) {
            params.put("code", work.getCode());
        }
        if (work.getExcerpt() != null) {
            params.put("excerpt", work.getExcerpt());
        }
        if (work.getTags() != null) {
            params.put("tags", work.getTags());
        }
        if (work.isAllowDownload() != null) {
            params.put("allowdownload", work.isAllowDownload() ? "1" : "0");
        }
        if (work.isRegistryPublic() != null) {
            params.put("registrypublic", work.isRegistryPublic() ? "1" : "0");
        }
        if (work.getLicense() != null) {
            params.put("license", work.getLicense().getCode());
        }
        if (work.getType() != null) {
            params.put("worktype", work.getType().getCode());
        }
        if (work.getLanguage() != null) {
            params.put("language", work.getLanguage().getCode());
        }
        if (work.isUseAlias() != null) {
            params.put("usealias", work.isUseAlias() ? "1" : "0");
        }
        if (work.isUserAuthor() != null) {
            params.put("userauthor", work.isUserAuthor() ? "1" : "0");
        }
        if (work.isUserRights() != null) {
            params.put("userrights", work.isUserRights() ? "1" : "0");
        }
        if (work.getObservations() != null) {
            params.put("obs", work.getObservations());
        }
        if (work.getLinks() != null) {
            int i = 1;
            for (Link link : work.getLinks()) {                
                params.put("link" + i++, linkString(link));
            }
        }
        if (work.getRelations(Work.RelationType.DERIVATION).size() > 0) {
            String relatedString = toCSV(work.getRelations(Work.RelationType.DERIVATION));
            params.put("derivationof", relatedString);
        }
        if (work.getRelations(Work.RelationType.COMPOSITION).size() > 0) {
            String relatedString = toCSV(work.getRelations(Work.RelationType.COMPOSITION));
            params.put("compositionof", relatedString);
        }
        if (work.getRelations(Work.RelationType.VERSION).size() > 0) {
            String relatedString = toCSV(work.getRelations(Work.RelationType.VERSION));
            params.put("versionof", relatedString);
        }
        
        return params;
    }

    /**
     * @param link Link to convert, must be not null
     * @return String of link formated for API register
     */
    public static String linkString(Link link) {
        return link.getUrl() + "|" + link.getName() + "|" + link.getType();
    }

    /**
     * @param works list of works to convert, must not be null
     * @return comma-separated string of works's codes of works in 'works'
     */
    private static String toCSV(List<Work> works) {
        boolean first = true;
        String string = "";
        for (Work work : works) {
            if (!first) {
                string += " ,";
            } else {
                first = false;
            }
            string += work.getCode();
        }

        return string;
    }

}