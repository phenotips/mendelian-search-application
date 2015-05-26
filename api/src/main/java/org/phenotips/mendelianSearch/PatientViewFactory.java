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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.phenotips.mendelianSearch;

import org.phenotips.mendelianSearch.internal.MendelianSearchRequest;

import org.xwiki.component.annotation.Role;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ga4gh.GAVariant;

/**
 * Factory for creating {@link PatientView}.
 * @version $Id$
 */
@Role
public interface PatientViewFactory
{
    /**
     * Create a single patient view.
     * @param id The patient internal id
     * @param variants The variants associated with this patient.
     * @param score The phenotype score of this patient
     * @param request The request that provides context to the patient view.
     * @return The patient view
     */
    PatientView createPatientView(String id, List<GAVariant> variants, Double score, MendelianSearchRequest request);

    /**
     * Create multiple patient views. Note that the ids specified in the params must be consistent.
     * @param ids A set of valid patient ids.
     * @param variantMap A map from patient id to list of variants
     * @param scores A map of patient id to phenotype score
     * @param request The request that provides context to the patient view.
     * @return A list of patient views.
     */
    List<PatientView> createPatientViews(Set<String> ids, Map<String, List<GAVariant>> variantMap,
        Map<String, Double> scores, MendelianSearchRequest request);

}
