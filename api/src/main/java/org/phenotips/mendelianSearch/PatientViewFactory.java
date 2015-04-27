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

import org.xwiki.component.annotation.Role;
import org.xwiki.component.phase.Initializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ga4gh.GAVariant;

@Role
public interface PatientViewFactory extends Initializable
{
    PatientView createPatientView(String id, List<GAVariant> variants, double score);

    List<PatientView> createPatientViews(Set<String> ids, Map<String, List<GAVariant>> variantMap,
        Map<String, Double> scores);

}
