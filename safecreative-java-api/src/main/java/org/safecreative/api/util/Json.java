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
package org.safecreative.api.util;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Json helper methods
 *
 * @author mpolo@safecreative.org
 *
 */
public final class Json {

	public static final String UNDEFINED = "undefined";

	public static String toJson(Map map) {
		return mapToJSObject(map, false);
	}

	public static String mapToJSObject(Map map, boolean humanize) {
		return mapToJSObject(new StringBuilder(), map, humanize).toString();
	}

	public static StringBuilder mapToJSObject(StringBuilder ctx, Map map) {
		return mapToJSObject(ctx, map, false);
	}

	public static StringBuilder mapToJSObject(StringBuilder ctx, Map map, boolean humanize) {
		Object value;
		boolean hasMore = false;
		if (map != null && !map.isEmpty()) {
			ctx.append("{");
			if (humanize) {
				ctx.append('\n');
			}
			for (Object key : map.keySet()) {
				value = map.get(key);
				if (hasMore) {
					ctx.append(",");
				}
				if (humanize) {
					ctx.append('\t');
				}
				ctx.append(quote(key.toString().trim())).append(':');
				appendValue(ctx, value);
				hasMore = true;
			}
			if (humanize) {
				ctx.append('\n');
			}
			ctx.append("}");
			if (humanize) {
				ctx.append('\n');
			}
		}
		return ctx;
	}

	public static StringBuilder collectionToJSObject(StringBuilder ctx, Object... values) {
		return collectionToJSObject(ctx, Arrays.asList(values));
	}

	public static StringBuilder collectionToJSObject(StringBuilder ctx, List values) {
		boolean hasMore = false;
		ctx.append('[');
		if (values != null) {
			for (Object value : values) {
				if (hasMore) {
					ctx.append(',');
				}
				appendValue(ctx, value);
				hasMore = true;
			}
		}
		ctx.append(']');
		return ctx;
	}

	/**
	 * Produce a string in double quotes with backslash sequences in all the
	 * right places. A backslash will be inserted within </, producing <\/,
	 * allowing JSON text to be delivered in HTML. In JSON text, a string cannot
	 * contain a control character or an unescaped quote or backslash.
	 *
	 * @param string A String
	 * @return A String correctly formatted for insertion in a JSON text.
	 */
	public static String quote(String string) {
		if (string == null || string.length() == 0) {
			return "\"\"";
		}

		char b;
		char c = 0;
		String hhhh;
		int i;
		int len = string.length();
		StringBuffer sb = new StringBuffer(len + 4);

		sb.append('"');
		for (i = 0; i < len; i += 1) {
			b = c;
			c = string.charAt(i);
			switch (c) {
				case '\\':
				case '"':
					sb.append('\\');
					sb.append(c);
					break;
				case '/':
					if (b == '<') {
						sb.append('\\');
					}
					sb.append(c);
					break;
				case '\b':
					sb.append("\\b");
					break;
				case '\t':
					sb.append("\\t");
					break;
				case '\n':
					sb.append("\\n");
					break;
				case '\f':
					sb.append("\\f");
					break;
				case '\r':
					sb.append("\\r");
					break;
				default:
					if (c < ' ' || (c >= '\u0080' && c < '\u00a0')
							|| (c >= '\u2000' && c < '\u2100')) {
						hhhh = "000" + Integer.toHexString(c);
						sb.append("\\u" + hhhh.substring(hhhh.length() - 4));
					} else {
						sb.append(c);
					}
			}
		}
		sb.append('"');
		return sb.toString();
	}

	private static void appendValue(StringBuilder ctx, Object value) {
		if (value == null) {
			ctx.append(UNDEFINED);
		} else {
			if (value instanceof String) {
				ctx.append(quote((String) value));
			} else if (value instanceof Map) {
				mapToJSObject(ctx, (Map) value);
			} else if (value instanceof Object[]) {
				collectionToJSObject(ctx, (Object[]) value);
			} else if (value instanceof List) {
				collectionToJSObject(ctx, (List) value);
			} else {
				ctx.append(value);
			}
		}
	}
}
