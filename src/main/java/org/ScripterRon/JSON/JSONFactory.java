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

import java.util.List;
import java.util.Map;

/**
 * Create JSON object and array containers
 */
public interface JSONFactory {

    /**
     * Create an object container for use by the JSON parser
     *
     * @return                      Object container
     */
    public Map<String, Object> createObjectContainer();

    /**
     * Create an array container for use by the JSON parser
     *
     * @return                      Array container
     */
    public List<Object> createArrayContainer();
}
