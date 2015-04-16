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

import org.phenotips.data.Feature;
import org.phenotips.data.Patient;
import org.phenotips.data.PatientRepository;
import org.phenotips.data.permissions.PermissionsManager;
import org.phenotips.mendelianSearch.phenotype.PatientPhenotypeScorer;
import org.phenotips.mendelianSearch.script.MendelianSearchRequest;
import org.phenotips.ontology.OntologyManager;
import org.phenotips.ontology.OntologyTerm;
import org.phenotips.variantStoreIntegration.VariantStoreService;

import org.xwiki.component.annotation.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Default implementation of {@link MendelianSearch}.
 *
 * @version $Id$
 */
@Component
public class DefaultMendelianSearch implements MendelianSearch
{

    @Inject
    private VariantStoreService variantStore;

    @Inject
    private PatientPhenotypeScorer patientPhenotypeScorer;

    @Inject
    private OntologyManager om;

    @Inject
    private PatientRepository pr;

    @Inject
    private PermissionsManager pm;

    private String phenotypeString = "phenotype";

    @SuppressWarnings("unchecked")
    @Override
    public JSONObject search(MendelianSearchRequest request)
    {
        Map<String, JSONArray> matchingVariants;
        String variantSearchKey = "variantSearch";
        // First query the variant store and receive a JSONArray of patient variant information --> store in List.
        if (request.get(variantSearchKey) != null && (int) request.get(variantSearchKey) == 1) {
            matchingVariants =
                this.variantStore.getIndividualsWithVariant((String) request.get("varChr"),
                    (int) request.get("varPos"),
                    (String) request.get("varRef"), (String) request.get("varAlt"));
        } else {
            matchingVariants =
                this.variantStore.getIndividualsWithGene((String) request.get("geneSymbol"),
                    (List<String>) request.get("variantEffects"),
                    (Map<String, Double>) request.get("alleleFrequencies"));
        }

        Set<String> matchingIds = new HashSet<String>(matchingVariants.keySet());

        // Find the set of all IDs or patients in PhenoTips (with variants?)
        Set<String> nonMatchingIds = this.getNonMatchingIds(matchingIds);

        // Generate variant result for non-matching ids
        Map<String, JSONArray> nonMatchingVariants = new HashMap<String, JSONArray>();
        for (String id : nonMatchingIds) {
            nonMatchingVariants.put(id, this.variantStore.getTopHarmfullVariants(id, 5));
        }

        // HACK: variant store returns external ids not internal IDS must convert
        Set<String> keySet = new HashSet<String>(matchingVariants.keySet());
        for (String external : keySet) {
            String newKey = this.getPatientInternalFromExternal(external);
            JSONArray newValue = matchingVariants.get(external);
            matchingVariants.remove(external);
            matchingVariants.put(newKey, newValue);
        }
        keySet = new HashSet<String>(nonMatchingVariants.keySet());
        for (String external : keySet) {
            String newKey = this.getPatientInternalFromExternal(external);
            JSONArray newValue = nonMatchingVariants.get(external);
            nonMatchingVariants.remove(external);
            nonMatchingVariants.put(newKey, newValue);
        }
        keySet = new HashSet<String>(matchingIds);
        for (String external : keySet) {
            String internal = this.getPatientInternalFromExternal(external);
            matchingIds.remove(external);
            matchingIds.add(internal);
        }
        keySet = new HashSet<String>(nonMatchingIds);
        for (String external : keySet) {
            String internal = this.getPatientInternalFromExternal(external);
            nonMatchingIds.remove(external);
            nonMatchingIds.add(internal);
        }
        // End of Hack

        // Convert the request list of HPO ids into OntologyTerms
        List<OntologyTerm> phenotype = new ArrayList<OntologyTerm>();
        List<String> hpoIds = (List<String>) request.get(this.phenotypeString);
        for (String termId : hpoIds) {
            phenotype.add(this.om.resolveTerm(termId));
        }

        Set<Patient> matchingPatients = new HashSet<Patient>();
        Set<Patient> nonMatchingPatients = new HashSet<Patient>();
        for (String patientId : matchingIds) {
            matchingPatients.add(this.pr.getPatientById(patientId));
        }
        for (String patientId : nonMatchingIds) {
            nonMatchingPatients.add(this.pr.getPatientById(patientId));

        }
        Map<Patient, Double> matchingScores = this.patientPhenotypeScorer.getScores(phenotype, matchingPatients);
        Map<Patient, Double> nonMatchingScores = this.patientPhenotypeScorer.getScores(phenotype, nonMatchingPatients);

        // Create a complex JSON array that will be returned to the user. TODO: This is far from ideal. Each patient
        // should be represented by a
        // view similar to the PatientSimilarity view in patient-network which will be able to handle things like
        // access levels
        JSONObject result = new JSONObject();
        result.element("matching", this.generateResult(matchingPatients, matchingVariants, matchingScores));
        result.element("nonMatching", this.generateResult(nonMatchingPatients, nonMatchingVariants, nonMatchingScores));

        return result;
    }

    private JSONArray generateResult(Set<Patient> patients, Map<String, JSONArray> variants,
        Map<Patient, Double> phenotypeScores)
    {
        JSONArray result = new JSONArray();

        if (patients.size() == 0 || variants.size() == 0) {
            return result;
        }

        for (Patient patient : patients) {
            JSONObject patientResult = new JSONObject();
            String patientIDKey = "patientID";
            if (patient.getExternalId() == null || "".equals(patient.getExternalId())) {
                patientResult.element(patientIDKey, "-");
            } else {
                patientResult.element(patientIDKey, patient.getExternalId());
            }
            patientResult.element("variants", variants.get(patient.getId()));
            patientResult.element("phenotypeScore", phenotypeScores.get(patient));
            List<String> dPhenotype = new ArrayList<String>();
            if (!patient.getFeatures().isEmpty()) {
                for (Feature feature : patient.getFeatures()) {
                    OntologyTerm term = this.om.resolveTerm(feature.getId());
                    if (term != null) {
                        dPhenotype.add(term.getName());
                    }
                }
            }
            patientResult.element(this.phenotypeString, dPhenotype);
            patientResult.element("owner", this.pm.getPatientAccess(patient).getOwner().getUsername());
            result.add(patientResult);
        }
        return result;

    }

    private Set<String> getNonMatchingIds(Set<String> matchingIds)
    {
        Set<String> allIds = new HashSet<String>(this.variantStore.getIndividuals());
        allIds.removeAll(matchingIds);
        return allIds;
    }

    private String getPatientExternalFromInternal(String internal)
    {
        return this.pr.getPatientById(internal).getExternalId();
    }

    private String getPatientInternalFromExternal(String external)
    {
        Patient patient = this.pr.getPatientByExternalId(external);
        if (patient == null) {
            return null;
        }
        return this.pr.getPatientByExternalId(external).getId();
    }
}
