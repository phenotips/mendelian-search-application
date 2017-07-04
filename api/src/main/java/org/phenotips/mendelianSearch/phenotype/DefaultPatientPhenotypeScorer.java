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
package org.phenotips.mendelianSearch.phenotype;

import org.phenotips.data.Feature;
import org.phenotips.data.Patient;
import org.phenotips.data.PatientRepository;
import org.phenotips.data.permissions.PermissionsManager;
import org.phenotips.data.permissions.internal.visibility.HiddenVisibility;
import org.phenotips.vocabulary.VocabularyManager;
import org.phenotips.vocabulary.VocabularyTerm;

import org.xwiki.component.annotation.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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

    /** Provides access to the term vocabulary. */
    @Inject
    private VocabularyManager vocabularyManager;

    @Inject
    private PermissionsManager pm;

    @Inject
    private PatientRepository pr;

    @Override
    public Map<Patient, Double> getScores(List<VocabularyTerm> phenotype, Set<Patient> patients)
    {
        Map<Patient, Double> patientScores = new HashMap<Patient, Double>();
        for (Patient patient : patients) {
            if (patient == null
                || (this.pm.getPatientAccess(patient).getVisibility().compareTo(new HiddenVisibility()) <= 0)) {
                continue;
            }
            patientScores.put(patient,
                this.scorer.getScoreAgainstReference(phenotype, this.getPresentPatientTerms(patient)));
        }
        return patientScores;
    }

    @Override
    public Map<String, Double> getScoresById(List<VocabularyTerm> phenotype, Set<String> ids)
    {
        Set<Patient> patients = new HashSet<Patient>();
        for (String id : ids) {
            Patient patient = this.pr.get(id);
            patients.add(patient);
        }
        Map<Patient, Double> patientMap = this.getScores(phenotype, patients);

        Map<String, Double> result = new HashMap<String, Double>();
        for (Patient patient : patientMap.keySet()) {
            result.put(patient.getId(), patientMap.get(patient));
        }
        return result;
    }

    /**
     * Return a (potentially empty) collection of terms present in the patient.
     *
     * @param patient the patient
     * @return a collection of terms present in the patient
     */
    private List<VocabularyTerm> getPresentPatientTerms(Patient patient)
    {
        List<VocabularyTerm> terms = new ArrayList<>();
        if (patient.getFeatures().isEmpty()) {
            return terms;
        }
        for (Feature feature : patient.getFeatures()) {
            if (!feature.isPresent()) {
                continue;
            }

            VocabularyTerm term = this.vocabularyManager.resolveTerm(feature.getId());
            if (term != null) {
                // Only add resolvable terms
                terms.add(term);
            }
        }
        return terms;
    }
}
