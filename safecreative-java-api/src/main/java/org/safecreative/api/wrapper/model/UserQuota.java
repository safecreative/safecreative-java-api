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

package org.safecreative.api.wrapper.model;

/**
 * Represents user's disk and registration quota
 * @author vcalderon@safecreative.org
 */
public class UserQuota {
    private String userCode;
    private long diskUsage;
    private long diskQuota;
    private long monthlyRegistrations;
    private long monthlyRegistrationsQuota;

    public long getDiskQuota() {
        return diskQuota;
    }

    public void setDiskQuota(long diskQuota) {
        this.diskQuota = diskQuota;
    }

    public long getDiskUsage() {
        return diskUsage;
    }

    public void setDiskUsage(long diskUsage) {
        this.diskUsage = diskUsage;
    }

    public long getMonthlyResgistrations() {
        return monthlyRegistrations;
    }

    public void setMonthlyResgistrations(long monthlyResgistrations) {
        this.monthlyRegistrations = monthlyResgistrations;
    }

    public long getMonthlyResgistrationsQuota() {
        return monthlyRegistrationsQuota;
    }

    public void setMonthlyResgistrationsQuota(long monthlyResgistrationsQuota) {
        this.monthlyRegistrationsQuota = monthlyResgistrationsQuota;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }
}
