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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * ListPage is a holder for paginated results
 * 
 * @param <T> Paginated item class
 * @author mpolo@safecreative.org
 * @author jguillo@safecreative.org
 */
public class ListPage<T> implements Iterable<T>{
    private int recordTotal;
	private int pageTotal;
	private List<T> list;

    public ListPage() {
        this.list = new LinkedList<T>();
    }

    /**
     * Adds an item
     * @param item
     */
    public void add(T item) {
        getList().add(item);
    }

    /**
     * Get list page size
     * @return page size
     */
    public int getSize() {
        return getList().size();
    }

    /**
     * @return the recordTotal
     */
    public int getRecordTotal() {
        return recordTotal;
    }

    /**
     * @param recordTotal the recordTotal to set
     */
    public void setRecordTotal(int recordTotal) {
        this.recordTotal = recordTotal;
    }

    /**
     * @return the pageTotal
     */
    public int getPageTotal() {
        return pageTotal;
    }

    /**
     * @param pageTotal the pageTotal to set
     */
    public void setPageTotal(int pageTotal) {
        this.pageTotal = pageTotal;
    }

    /**
     * @return the list of items
     */
    public List<T> getList() {
        return list;
    }

    /**
     * Implements Iterable<T>
     * @return Iterator<T>
     */
    public Iterator<T> iterator() {
        return getList().iterator();
    }

}
