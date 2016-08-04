/*
 * Copyright 2015-2016 Ronald W Hoffman.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ScripterRon.JSON;

import java.io.CharConversionException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Routines for encoding JSON strings
 */
public class JSONValue {

    /** String escape pattern */
    private static final Pattern pattern = Pattern.compile(
            "[\"\\\\\\u0008\\f\\n\\r\\t/\\u0000-\\u001f\\u007f-\\u009f\\u2000-\\u20ff\\ud800-\\udbff]");

    /**
     * Create a formatted string from a list
     *
     * @param   list                            List
     * @param   sb                              String builder
     * @throws  CharConversionException         Invalid Unicode character in string value
     * @throws  UnsupportedEncodingException    Unsupported data type
     */
    public static void encodeArray(List<? extends Object> list, StringBuilder sb)
                                                throws CharConversionException, UnsupportedEncodingException {
        if (list == null) {
            sb.append("null");
            return;
        }
        boolean firstElement = true;
        sb.append('[');
        for (Object obj : list) {
            if (firstElement) {
                firstElement = false;
            } else {
                sb.append(',');
            }
            encodeValue(obj, sb);
        }
        sb.append(']');
    }

    /**
     * Create a formatted string from a map
     *
     * @param   map                             Map
     * @param   sb                              String builder
     * @throws  CharConversionException         Invalid Unicode character in string value
     * @throws  UnsupportedEncodingException    Unsupported data type
     */
    @SuppressWarnings("unchecked")
    public static void encodeObject(Map<? extends Object, ? extends Object> map, StringBuilder sb)
                                                throws CharConversionException, UnsupportedEncodingException {
        if (map == null) {
            sb.append("null");
            return;
        }
        Set<Map.Entry<Object, Object>> entries = (Set)map.entrySet();
        Iterator<Map.Entry<Object, Object>> it = entries.iterator();
        boolean firstElement = true;
        sb.append('{');
        while (it.hasNext()) {
            Map.Entry<Object, Object> entry = it.next();
            Object key = entry.getKey();
            Object value = entry.getValue();
            if (!(key instanceof String)) {
                throw new UnsupportedEncodingException("JSON map key is not a string");
            }
            if (firstElement) {
                firstElement = false;
            } else {
                sb.append(',');
            }
            sb.append('\"').append((String)key).append("\":");
            encodeValue(value, sb);
        }
        sb.append('}');
    }

    /**
     * Encode a JSON value
     *
     * @param   value                           JSON value
     * @param   sb                              String builder
     * @throws  CharConversionException         Invalid Unicode character in string value
     * @throws  UnsupportedEncodingException    Unsupported data type
     */
    @SuppressWarnings("unchecked")
    public static void encodeValue(Object value, StringBuilder sb)
                                                throws CharConversionException, UnsupportedEncodingException {
        if (value == null) {
            sb.append("null");
        } else if (value instanceof String) {
            sb.append('\"');
            escapeString((String)value, sb);
            sb.append('\"');
        } else if (value instanceof Double) {
            if (((Double)value).isInfinite() || ((Double)value).isNaN())
                sb.append("null");
            else
                sb.append(value.toString());
        } else if (value instanceof Float) {
            if (((Float)value).isInfinite() || ((Float)value).isNaN())
                sb.append("null");
            else
                sb.append(value.toString());
        } else if (value instanceof Number) {
            sb.append(value.toString());
        } else if (value instanceof Boolean) {
            sb.append(value.toString());
        } else if (value instanceof JSONAware) {
            ((JSONAware)value).toJSONString(sb);
        } else if (value instanceof Map) {
            encodeObject((Map<Object, Object>)value, sb);
        } else if (value instanceof List) {
            encodeArray((List<Object>)value, sb);
        } else {
            throw new UnsupportedEncodingException("Unsupported JSON data type");
        }
    }

    /**
     * Escape control characters in a string and append them to the string buffer
     *
     * @param   string                      String to be written
     * @param   sb                          String builder
     * @throws  CharConversionException     Invalid Unicode character
     */
    private static void escapeString(String string, StringBuilder sb)
                                            throws CharConversionException {
        if (string.length() == 0) {
            return;
        }
        //
        // Find the next special character in the string
        //
        int start = 0;
        Matcher matcher = pattern.matcher(string);
        while (matcher.find(start)) {
            int pos = matcher.start();
            if (pos > start) {
                sb.append(string.substring(start, pos));
            }
            start = pos + 1;
            //
            // Check for a valid Unicode codepoint
            //
            int ch = string.codePointAt(pos);
            if (!Character.isValidCodePoint(ch)) {
                throw new CharConversionException("Invalid Unicode character in JSON string value");
            }
            //
            // Process a supplementary codepoint
            //
            if (Character.isSupplementaryCodePoint(ch)) {
                sb.appendCodePoint(ch);
                start++;
                continue;
            }
            //
            // Escape control characters
            //
            char c = string.charAt(pos);
            switch (c) {
                case '"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                case '/':
                    sb.append("\\/");
                    break;
                default:
                    if((c>='\u0000' && c<='\u001F') || (c>='\u007F' && c<='\u009F') || (c>='\u2000' && c<='\u20FF')){
                        sb.append("\\u").append(String.format("%04X", (int)c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        //
        // Append the remainder of the string
        //
        if (start == 0) {
            sb.append(string);
        } else if (start < string.length()) {
            sb.append(string.substring(start));
        }
    }
}
