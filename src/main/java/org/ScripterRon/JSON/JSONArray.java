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

import java.io.CharConversionException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;

/**
 * JSON array container (extends ArrayList)
 *
 * @param   <E>                                 Collection element
 */
public class JSONArray<E> extends ArrayList<E> implements JSONAware {

    /**
     * Create an array container
     */
    public JSONArray() {
        super();
    }

    /**
     * Create an array container with an initial capacity
     *
     * @param   initCapacity                    Initial capacity
     */
    public JSONArray(int initCapacity) {
        super(initCapacity);
    }

    /**
     * Create a formatted string from a JSON list
     *
     * @return                                  JSON string
     * @throws  CharConversionException         Invalid Unicode character in string value
     * @throws  UnsupportedEncodingException    Unsupported data type
     */
    @Override
    public String toJSONString() throws CharConversionException, UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder(512);
        JSONValue.encodeArray(this, sb);
        return sb.toString();
    }

    /**
     * Create a formatted string from a JSON list
     *
     * @param   sb                              String builder
     * @throws  CharConversionException         Invalid Unicode character in string value
     * @throws  UnsupportedEncodingException    Unsupported data type
     */
    @Override
    public void toJSONString(StringBuilder sb) throws CharConversionException, UnsupportedEncodingException {
        JSONValue.encodeArray(this, sb);
    }

    /**
     * Write a JSON-formatted string to the supplied writer
     *
     * @param   writer                          Output writer
     * @throws  CharConversionException         Invalid Unicode character in string value
     * @throws  IOException                     I/O error occurred
     * @throws  UnsupportedEncodingException    Unsupported data type
     */
    @Override
    public void writeJSONString(Writer writer) throws CharConversionException, UnsupportedEncodingException,
                                                      IOException {
        writer.write(toJSONString());
    }
}
