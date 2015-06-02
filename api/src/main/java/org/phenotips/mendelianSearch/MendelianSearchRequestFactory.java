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

import com.xpn.xwiki.web.XWikiRequest;

/**
 * @version $Id$
 */

@Role
public interface MendelianSearchRequestFactory
{
    /**
     * Converts an {@link XWikiRequest} to a {@link MendelianSearchRequest}.
     * Performs necessary sanitation to the incoming request.
     * @param rawRequest The request sent by the ui.
     * @return the mendelian search request to be used in the query.
     */
    MendelianSearchRequest makeRequest(XWikiRequest rawRequest);
}
