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
package org.phenotips.mendelianSearch;

import org.phenotips.mendelianSearch.script.MendelianSearchRequest;

import org.xwiki.component.annotation.Role;

import java.util.List;

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

}
