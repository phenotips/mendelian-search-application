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
package org.phenotips.mendelianSearch.phenotype;

import org.phenotips.data.Feature;
import org.phenotips.data.Patient;
import org.phenotips.ontology.OntologyManager;
import org.phenotips.ontology.OntologyTerm;

import org.xwiki.component.annotation.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

/**
 * Default implementation of a {@link PatientPhenotypeScorer}.
 *
 * @version $Id$
 */
@Component
public class DefaultPatientPhenotypeScorer implements PatientPhenotypeScorer
{
    @Inject
    private PhenotypeScorer scorer;

    /** Provides access to the term ontology. */
    @Inject
    private OntologyManager ontologyManager;

    @Override
    public Map<Patient, Double> getScores(List<OntologyTerm> phenotype, Set<Patient> patients)
    {
        Map<Patient, Double> patientScores = new HashMap<Patient, Double>();
        for (Patient patient : patients) {
            if (patient == null) {
                continue;
            }
            patientScores.put(patient, this.scorer.getScore(phenotype, this.getPresentPatientTerms(patient)));
        }
        return patientScores;
    }

    /**
     * Return a (potentially empty) collection of terms present in the patient.
     *
     * @param patient
     * @return a collection of terms present in the patient
     */
    private List<OntologyTerm> getPresentPatientTerms(Patient patient)
    {
        List<OntologyTerm> terms = new ArrayList<OntologyTerm>();
        if (patient.getFeatures().isEmpty()) {
            return terms;
        }
        for (Feature feature : patient.getFeatures()) {
            if (!feature.isPresent()) {
                continue;
            }

            OntologyTerm term = this.ontologyManager.resolveTerm(feature.getId());
            if (term != null) {
                // Only add resolvable terms
                terms.add(term);
            }
        }
        return terms;
    }
}
