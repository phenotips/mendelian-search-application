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
import org.phenotips.mendelianSearch.genotype.VariantStore;
import org.phenotips.mendelianSearch.phenotype.PatientPhenotypeScorer;
import org.phenotips.mendelianSearch.script.MendelianSearchRequest;
import org.phenotips.ontology.OntologyManager;
import org.phenotips.ontology.OntologyTerm;

import org.xwiki.query.Query;
import org.xwiki.query.QueryException;
import org.xwiki.query.QueryManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.slf4j.Logger;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Default implementation of {@link MendelianSearch}.
 *
 * @version $Id$
 */
public class DefaultMendelianSearch implements MendelianSearch
{
    @Inject
    private Logger logger;

    @Inject
    private VariantStore variantStore;

    @Inject
    private PatientPhenotypeScorer patientPhenotypeScorer;

    @Inject
    private QueryManager qm;

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

        // First query the variant store and receive a JSONArray of patient variant information --> store in List.
        Map<String, JSONObject> matchingVariants =
            this.variantStore.findPatients((String) request.get("geneSymbol"),
                (List<String>) request.get("variantEffects"), (Map<String, Double>) request.get("alleleFrequencies"));
        Set<String> matchingIds = matchingVariants.keySet();

        // Find the set of all IDs or patients in PhenoTips
        Set<String> nonMatchingIds = this.getNonMatchingIds(matchingIds);

        // Generate variant result for non-matching ids
        Map<String, JSONObject> nonMatchingVariants = new HashMap<String, JSONObject>();
        for (String id : nonMatchingIds) {
            nonMatchingVariants.put(id, this.variantStore.getTopVariants(id, 5));
        }

        // Convert the request list of HPO terms into OntologyTerms
        List<OntologyTerm> phenotype = new ArrayList<OntologyTerm>();
        for (String termId : (List<String>) request.get(this.phenotypeString)) {
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

    private JSONArray generateResult(Set<Patient> patients, Map<String, JSONObject> variants,
        Map<Patient, Double> phenotypeScores)
    {
        JSONArray result = new JSONArray();
        for (Patient patient : patients) {
            JSONObject patientResult = new JSONObject();
            patientResult.element("patientID", patient.getId());
            patientResult.element("variants", variants.get(patient.getId()));
            patientResult.element("phenotypeScore", phenotypeScores.get(patient));
            List<String> dPhenotype = new ArrayList<String>();
            for (Feature term : patient.getFeatures()) {
                dPhenotype.add(this.om.resolveTerm(term.getId()).getName());
            }
            patientResult.element(this.phenotypeString, dPhenotype);
            patientResult.element("owner", this.pm.getPatientAccess(patient).getOwner().getUsername());
            result.add(patientResult);
        }
        return result;

    }

    private Set<String> getNonMatchingIds(Set<String> matchingIds)
    {
        try {
            Query q =
                this.qm.createQuery(
                    "select patient.identifier from Document doc, doc.object(PhenoTips.PatientClass) as patient"
                        + " where patient.identifier is not null order by patient.identifier desc", Query.XWQL);
            List<String> queryResult = q.execute();
            Set<String> allIds = new HashSet<String>(queryResult);

            allIds.removeAll(matchingIds);
            return allIds;
        } catch (QueryException e) {
            this.logger.warn("Could not generate a list of all patient Ids:" + e.getMessage());
            return null;
        }
    }
}
