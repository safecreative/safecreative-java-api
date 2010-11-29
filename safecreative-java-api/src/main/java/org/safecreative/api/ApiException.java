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

import org.apache.commons.lang.StringUtils;

/**
 * Api's main exception class
 *
 * @author mpolo@safecreative.org
 * @author jguillo@safecreative.org
 */
public class ApiException extends Exception {
    private String errorCode;

    /**
     * Constructs an instance of <code>ApiException</code> with the specified detail message.
     * @param errorMessage the detail message.
     */
    public ApiException(String errorMessage) {
        super(errorMessage);
    }

    /**
     * Constructs an instance of <code>ApiException</code> with the specified error code and detail message.
     * @param errorCode the error code.
     * @param errorMessage the detail message.
     */
    public ApiException(String errorCode,String errorMessage) {
        super(StringUtils.isBlank(errorMessage) ? errorCode : errorMessage);
        this.errorCode = errorCode;
    }

    /**
     * Constructs an instance of <code>ApiException</code> with the specified cause.
     * @param cause the cause.
     */
    public ApiException(Throwable cause) {
        super(cause);
    }

    /**
     * Returns the error code
     * @return error code
     */
    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        if(getErrorCode() != null && !getErrorCode().equals(getMessage())) {
            sb.append(" Error code: ").append(getErrorCode());
        }
        if(getCause() != null) {
            sb.append(" Cause: ").append(getCause().toString());
        }
        return sb.toString();
    }
}
