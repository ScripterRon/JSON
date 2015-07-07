/*
 * Copyright 2015 Ronald W Hoffman.
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

import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>JSON parser</p>
 *
 * <p>The JSON container returned by the parser will be either an array or an object
 * depending on the type of JSON string being parsed.  The default container factory
 * will create JSONArray and JSONObject containers.</p>
 *
 * <p>The following data types will be returned by the parser:</p>
 * <ul>
 * <li>Boolean - boolean value
 * <li>Long - integer numeric value
 * <li>Double - floating-point numeric value
 * <li>String - string value
 * <li>List - array container created by the container factory
 * <li>Map - object container created by the container factory
 * </ul>
 */
public class JSONParser {

    /** Default container factory instance */
    private static final DefaultContainerFactory defaultFactory = new DefaultContainerFactory();

    /** JSON pattern */
    private static final Pattern pattern = Pattern.compile("[\\[\\]\\{\\}\":,\\\\]");

    /** Empty data value */
    private static final Object emptyValue = new Object();

    /**
     * Parse a formatted JSON string
     *
     * @param   reader                  Input reader
     * @return                          JSON container
     * @throws  IOException             I/O error occurred
     * @throws  ParseException          Error parsing string
     */
    public static Object parse(Reader reader) throws IOException, ParseException {
        return parse(reader, defaultFactory);
    }

    /**
     * Parse a formatted JSON string
     *
     * @param   reader                  Input reader
     * @param   factory                 JSON factory
     * @return                          JSON container
     * @throws  IOException             I/O error occurred
     * @throws  ParseException          Error parsing string
     */
    public static Object parse(Reader reader, JSONFactory factory) throws IOException, ParseException {
        StringBuilder sb = new StringBuilder(512);
        char[] cbuf = new char[64];
        int count;
        while (true) {
            count = reader.read(cbuf);
            if (count < 0)
                break;
            if (count > 0)
                sb.append(cbuf, 0, count);
        }
        return parse(sb.toString(), factory);
    }

    /**
     * Parse a formatted JSON string
     *
     * @param   string                  JSON string
     * @return                          JSON container
     * @throws  ParseException          Error parsing string
     */
    public static Object parse(String string) throws ParseException {
        return parse(string, defaultFactory);
    }

    /**
     * Parse a formatted JSON string
     *
     * @param   string                  JSON string
     * @param   factory                 JSON container factory
     * @return                          JSON container
     * @throws  ParseException          Error parsing string
     */
    public static Object parse(String string, JSONFactory factory) throws ParseException {
        Object container;
        Matcher matcher = pattern.matcher(string);
        if (!matcher.find())
            throw new ParseException("Missing JSON container sequence", 0);
        int pos = matcher.start();
        if (pos > 0 && string.substring(0, pos).trim().length() > 0)
            throw new ParseException("Extraneous characters before start of container sequence", 0);
        //
        // Process the first container
        //
        switch (string.charAt(pos)) {
            case '[':
                List<Object> list = factory.createArrayContainer();
                parseArray(list, string, matcher, factory);
                container = list;
                break;
            case '{':
                Map<String, Object> map = factory.createObjectContainer();
                parseObject(map, string, matcher, factory);
                container = map;
                break;
            default:
                throw new ParseException("Extraneous characters before start of container sequence", pos);
        }
        return container;
    }

    /**
     * Parse a JSON array sequence
     *
     * @param   container               JSON array container
     * @param   string                  JSON string
     * @param   matcher                 Pattern matcher
     * @param   factory                 Container factory
     * @return                          JSON container
     * @throws  ParseException          Error parsing string
     */
    private static void parseArray(List<Object> container, String string, Matcher matcher, JSONFactory factory)
                                        throws ParseException {
        while (true) {
            Object value = parseValue(string, matcher, factory);
            if (value != emptyValue)
                container.add(value);
            char ch = string.charAt(matcher.start());
            if (ch == '}')
                throw new ParseException("Illegal array sequence termination", matcher.start());
            if (ch == ']')
                break;
        }
    }

    /**
     * Parse a JSON object sequence
     *
     * @param   container               JSON object container
     * @param   string                  JSON string
     * @param   matcher                 Pattern matcher
     * @param   factory                 Container factory
     * @return                          JSON container
     * @throws  ParseException          Error parsing string
     */
    private static void parseObject(Map<String, Object> container, String string, Matcher matcher,
                                        JSONFactory factory) throws ParseException {
        while (true) {
            Object value;
            //
            // Get the entry key
            //
            int keyPos = matcher.end();
            value = parseValue(string, matcher, null);
            if (value == null || !(value instanceof String))
                throw new ParseException("Invalid object sequence key", keyPos);
            String key = (String)value;
            //
            // Get the entry value
            //
            value = parseValue(string, matcher, factory);
            if (value != emptyValue)
                container.put(key, value);
            char ch = string.charAt(matcher.start());
            if (ch == ']')
                throw new ParseException("Illegal object sequence termination", matcher.start());
            if (ch == '}')
                break;
        }
    }

    /**
     * Parse a JSON value
     *
     * @param   string                  JSON string
     * @param   matcher                 Pattern matcher
     * @param   factory                 Container factory or null if container values not allowed
     * @return                          Data value
     * @throws  ParseException          Error parsing string
     */
    private static Object parseValue(String string, Matcher matcher, JSONFactory factory) throws ParseException {
        Object value = null;
        StringBuilder sb = new StringBuilder(128);
        int start = matcher.end();
        int valueStart = start;
        boolean inString = false;
        boolean foundValue = false;
        boolean stringValue = false;
        //
        // Parse the data value
        //
        while (!foundValue && matcher.find(start)) {
            int pos = matcher.start();
            char ch = string.charAt(pos);
            if (inString) {
                if (pos > start)
                    sb.append(string.substring(start, pos));
                switch (ch) {
                    case '\\':
                        if (++pos >= string.length())
                            throw new ParseException("End of data before end of string", pos);
                        ch = string.charAt(pos);
                        switch (ch) {
                            case '"':
                                sb.append('\"');
                                break;
                            case '\\':
                                sb.append('\\');
                                break;
                            case 'b':
                                sb.append('\b');
                                break;
                            case 'f':
                                sb.append('\f');
                                break;
                            case 'n':
                                sb.append('\n');
                                break;
                            case 'r':
                                sb.append('\r');
                                break;
                            case 't':
                                sb.append('\t');
                                break;
                            case '/':
                                sb.append('/');
                                break;
                            case 'u':
                                if (pos+5 > string.length())
                                    throw new ParseException("Invalid Unicode escape sequence '"+
                                                             string.substring(pos)+"'", pos);
                                try {
                                    int cp = Integer.valueOf(string.substring(pos+1, pos+5), 16);
                                    sb.appendCodePoint(cp);
                                    pos += 4;
                                } catch (NumberFormatException exc) {
                                    throw new ParseException("Invalid Unicode escape sequece '"+
                                                             string.substring(pos, pos+5)+"'", pos);
                                }
                                break;
                            default:
                                throw new ParseException("Illegal string escape sequence '"+ch+"'", pos);
                        }
                        break;
                    case '\"':
                        inString = false;
                        stringValue = true;
                        break;
                    default:
                        sb.append(ch);
                }
            } else {
                if (pos > start)
                    sb.append(string.substring(start, pos).trim());
                switch (ch) {
                    case '{':
                        if (sb.length() > 0 || value != null || stringValue)
                            throw new ParseException("Illegal object sequence start", pos);
                        if (factory == null)
                            throw new ParseException("Invalid object key", pos);
                        Map<String, Object> map = factory.createObjectContainer();
                        parseObject(map, string, matcher, factory);
                        value = map;
                        pos = matcher.start();
                        break;
                    case '[':
                        if (sb.length() > 0 || value != null || stringValue)
                            throw new ParseException("Illegal array sequence start", pos);
                        if (factory == null)
                            throw new ParseException("Invalid object key", pos);
                        List<Object> list = factory.createArrayContainer();
                        parseArray(list, string, matcher, factory);
                        value = list;
                        pos = matcher.start();
                        break;
                    case '}':
                    case ']':
                    case ',':
                    case ':':
                        foundValue = true;
                        break;
                    case '\"':
                        if (sb.length() > 0 || value != null || stringValue)
                            throw new ParseException("Illegal string start", pos);
                        inString = true;
                        break;
                    default:
                        if (value != null || stringValue)
                            throw new ParseException("Extraneous characters after end of data value", pos);
                        sb.append(ch);
                }
            }
            start = pos + 1;
        }
        if (!foundValue)
            throw new ParseException("End of data parsing JSON value '"+
                                     string.substring(valueStart)+"'", valueStart);
        //
        // Stop now if the data value is a container sequence
        //
        if (value != null)
            return value;
        //
        // Create the data object from the JSON string
        //
        String vstring = sb.toString();
        if (stringValue) {
            for (int i=0; i<vstring.length(); i++) {
                int cp = vstring.codePointAt(i);
                if (!Character.isValidCodePoint(cp))
                    throw new ParseException("Invalid Unicode character in string '"+
                                             vstring+"'", valueStart);
                if (Character.isSupplementaryCodePoint(cp))
                    i++;
            }
            value = vstring;
        } else if (vstring.length() == 0) {
            value = emptyValue;
        } else {
            try {
                if (vstring.equalsIgnoreCase("null")) {
                    value = null;
                } else if (vstring.equalsIgnoreCase("true")) {
                    value = true;
                } else if (vstring.equalsIgnoreCase("false")) {
                    value = false;
                } else if (vstring.indexOf('.') >= 0) {
                    value = Double.valueOf(vstring);
                } else {
                    value = Long.valueOf(vstring);
                }
            } catch (NumberFormatException exc) {
                throw new ParseException("Invalid numeric value '"+vstring+"'", valueStart);
            }
        }
        return value;
    }

    /**
     * Default container factory
     */
    private static class DefaultContainerFactory implements JSONFactory {

        /**
        * Create an object container for use by the JSON parser
        *
        * @return                      Object container
        */
        @Override
        public Map<String, Object> createObjectContainer() {
            return new JSONObject<>();
        }

        /**
        * Create an array container for use by the JSON parser
        *
        * @return                      Array container
        */
        @Override
        public List<Object> createArrayContainer() {
            return new JSONArray<>();
        }
    }
}
