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
package org.phenotips.mendelianSearch;

import org.phenotips.mendelianSearch.internal.MendelianSearchRequest;

import org.xwiki.component.annotation.Role;

import java.util.List;
import java.util.Map;

/**
 * The main controller for the Mendelian Search Application (aka Gene Genie).
 *
 * @version $Id$
 */
@Role
public interface MendelianSearch
{
    /**
     * The basic search method.
     *
     * @param request the query
     * @return returns a list of patient views
     */
    List<PatientView> search(MendelianSearchRequest request);

    /**
     * A context specific method which will return different maps depending on the values stored in the request.
     * Currently supported are:
     * <ul>
     * <li>If request fuzzy phenotype searching is requested then respone will be a map with two keys "withGene" and
     * "withoutGene" which map to arrays of double scores</li>
     * </ul>
     *
     * @param request the query
     * @return Depending on the request different objects will be returned.
     */
    Map<String, Object> getOverview(MendelianSearchRequest request);

}
