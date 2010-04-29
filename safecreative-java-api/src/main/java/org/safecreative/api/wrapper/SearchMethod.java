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
package org.safecreative.api.wrapper;

/**
 * Enumeration of available search methods: fields,hashes
 *
 * @author mpolo@safecreative.org
 * @author jguillo@safecreative.org
 */
public enum SearchMethod {
    //Field methods: (Using searchWorksByFields)
    USER_CODE       ("user.code"),
    USER_NAME       ("user.name"),
    WORK_CODE       ("code"),
    WORK_TITLE      ("name"),
    WORK_EXCERPT    ("excerpt"),
    WORK_NOTES      ("obs"),
    WORK_TAGS       ("tag"),
    WORK_TYPE       ("workType.code"),
    WORK_TYPE_NAME  ("workType.name"),
    WORK_TYPE_GROUP ("workTypeGroup.code"),
    WORK_TYPE_GROUP_NAME  ("workTypeGroup.name"),
    LICENSE_CODE    ("license.code"),
    LICENSE_NAME    ("license.name"),
    LICENSE_SHORT_NAME    ("license.shortName"),
    DOWNLOADABLE    ("allowDownload"),

    //Hash methods: (Using searchWorksByHash)
    HASH_MD5        ("md5"),
    HASH_SHA1       ("sha1")
    ;


    private String fieldName;

    private SearchMethod(String fieldName) {
        this.fieldName = fieldName;
    }

    /**
     * Get this field name
     * @return
     */
    public String getFieldName() {
        return fieldName;
    }

    @Override
    public String toString() {
        return getFieldName();
    }
}
