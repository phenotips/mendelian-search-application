/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/
 */
package org.phenotips.mendelianSearch.internal;

import java.util.HashMap;
import java.util.Map;

/**
 * An object representation of the query made by a user interacting witht the mendelian search application. Just wraps a
 * map.
 *
 * @version $Id$
 */
public class MendelianSearchRequest
{
    private Map<String, Object> requestParams;

    private String id;

    /**
     * Constructor for a new request.
     */
    public MendelianSearchRequest()
    {
        this.requestParams = new HashMap<String, Object>();
    }

    /**
     * @param param the key
     * @param value the value
     */
    public void set(String param, Object value)
    {
        this.requestParams.put(param, value);
    }

    /**
     * @param param the key
     * @return value associated with that key
     */
    public Object get(String param)
    {
        return this.requestParams.get(param);
    }

    /**
     * @return the unique id of this request.
     */
    public String getId()
    {
        return id;
    }

    /**
     * @param id set the id of this request. Should be unique.
     */
    public void setId(String id)
    {
        this.id = id;
    }
}
