/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.phenotips.mendelianSearch.script;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * An object representation of the query made by a user interacting witht the mendelian search application. Just wraps a
 * map. Currently used keys:
 * <ul>
 * <li>geneSymbol</li>
 * <li>phenotype</li>
 * <li>variantEffects</li>
 * <li>alleleFrequencies</li>
 * </ul>
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

    public String getPhenotypeMatching()
    {
        return (String) this.requestParams.get("phenotypeMatching");
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }
}
