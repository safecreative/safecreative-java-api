/*
 Copyright (c) Safe Creative (http://www.safecreative.org)

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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;

/**
 * Metadata holder
 * 
 * @author mpolo@safecreative.org
 */
public class Metadata implements Serializable,Iterable<List<Metadata.Entry>> {
	private static final long serialVersionUID = 1L;
	
	public static class Entry implements Serializable {
		private static final long serialVersionUID = 1L;
		private String namespace;
		private String name;
		private String value;
		private Map<String,Object> attributes;	
		
		public Entry(String namespace,String name,String value) {
			this.namespace = namespace;
			this.name = name;
			this.value = value;
		}
		
		/**
		 * @return the namespace
		 */
		public String getNamespace() {
			return namespace;
		}
		/**
		 * @param namespace the namespace to set
		 */
		public void setNamespace(String namespace) {
			this.namespace = namespace;
		}
		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}
		/**
		 * @param name the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}
		/**
		 * @return the value
		 */
		public String getValue() {
			return value;
		}
		/**
		 * @param value the value to set
		 */
		public void setValue(String value) {
			this.value = value;
		}
		
		/**
		 * @return the attributes
		 */
		public Map<String, Object> getAttributes() {
			if(attributes == null) {
				attributes = new TreeMap<String,Object>();
			}
			return attributes;
		}

		/**
		 * @param attributes the attributes to set
		 */
		public void setAttributes(Map<String, Object> attributes) {
			this.attributes = attributes;
		}

		/**
		 * Return the namespace:name combination or this entry's name
		 * @return key value
		 */
		public String getKey() {
			return namespace == null ? name : namespace + ":" + name ;
		}
		
		@Override
		public String toString() {		
			return getValue();
		}
	}
		
	private Map<String,List<Entry>> entryMap;
	private Map<String,String> namespaceMap;
	
	public Metadata() {	
		entryMap = new LinkedHashMap<String, List<Entry>>();
	}
	
	public Metadata addNamespace(String namespace, String uri) {
		if(namespaceMap == null) {
			namespaceMap = new HashMap<String,String>();
		}
		namespaceMap.put(namespace, uri);
		return this;
	}
	
	public String getNamespaceUri(String namespace) {
		return namespaceMap == null ? null : namespaceMap.get(namespace);
	}
	
    /**
     * Returns <tt>true</tt> if this metadata contains no entries.
     *
     * @return <tt>true</tt> if this metadata contains no entries
     */
    public boolean isEmpty() {
    	return entryMap.isEmpty();
    }
	
	public Metadata add(Entry entry) {		
		List<Entry> values = get(entry.getKey());
		if(values == null) {
			values = new LinkedList<Entry>();
			entryMap.put(entry.getKey(), values);
		}
		values.add(entry);		
		return this;
	}
	
	public Metadata add(String namespace,String name,String value) {
		return add(new Entry(namespace,name,value));
	}
	
	public List<Entry> get(String key) {
		return entryMap.get(key);
	}
	
	public String getValue(String key) {
		List<Entry> values = get(key);
		if(values == null || values.isEmpty()) {
			return null;
		}
		if(values.size() >1) {
			return StringUtils.join(values, ',');
		}
		return values.get(0).getValue();
	}

	public Iterator<List<Entry>> iterator() {
		return entryMap.values().iterator();
	}

}