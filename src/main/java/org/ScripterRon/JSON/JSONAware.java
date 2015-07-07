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

/**
 * A JSON-aware object supports creating JSON-formatted strings from JSON containers
 */
public interface JSONAware {

    /**
     * Create a formatted string from a JSON-aware object
     *
     * @return                                  JSON string
     * @throws  CharConversionException         Invalid Unicode character in string value
     * @throws  UnsupportedEncodingException    Unsupported data type
     */
    public String toJSONString() throws CharConversionException, UnsupportedEncodingException;

    /**
     * Create a formatted string from a JSON-aware object
     *
     * @param   sb                              String builder
     * @throws  CharConversionException         Invalid Unicode character in string value
     * @throws  UnsupportedEncodingException    Unsupported data type
     */
    public void toJSONString(StringBuilder sb) throws CharConversionException, UnsupportedEncodingException;

    /**
     * Write a formatted string for a JSON-aware object
     *
     * @param   writer                          Output writer
     * @throws  CharConversionException         Invalid Unicode character in string value
     * @throws  IOException                     I/O error occurred
     * @throws  UnsupportedEncodingException    Unsupported data type
     */
    public void writeJSONString(Writer writer)
                                throws CharConversionException, UnsupportedEncodingException, IOException;
}
