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
package org.phenotips.mendelianSearch.genotype;

import org.xwiki.component.annotation.Role;
import org.xwiki.component.phase.Initializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;

/**
 * Working interface for the variant store query api.
 *
 * @version $Id$
 */
@Role
public interface VariantStore extends Initializable
{
    /**
     * Queries the variant store for patients which pass the specified filters.
     *
     * @param geneSymbol Only patients with mutations in the specified gene will be returned
     * @param variantEffects Only variants matching one of the effects specified in this list will be considered
     * @param alleleFrequencies Only variants with allele frequencies less than the ones specified in this map will be
     *            considered. Map contains keys 'EXAC' and 'PhenomeCentral' mapping to double percentage values.
     * @return An array of JSON objects with two keys: 'PatientID' and 'Relevant-Variants'. 'Relevant-Variants' maps to
     *         an array of variant objects containing a 'position', 'ref', 'alt', 'effect' and 'score'.
     */
    Map<String, JSONArray> findPatients(String geneSymbol, List<String> variantEffects,
        Map<String, Double> alleleFrequencies);

    /**
     * Return variant information for the specified patient.
     *
     * @param patientId The patient whose variants are being queried
     * @param k The number of variants to return
     * @return Returns up to k top variants with the highest exomiser scores from the specified patient.
     */
    JSONArray getTopVariants(String patientId, int k);

    /**
     * @return A set of all patient ids that have associated genetic data.
     */
    Set<String> getAllPatientIds();
}
