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

import org.phenotips.data.Disorder;

import java.util.List;

import org.ga4gh.GAVariant;

import org.json.JSONObject;

/**
 * A container to be used to store patient information for the mendelian search application.
 * Allows different fields to be set and provides a method to summarize patient in a JSON object.
 * @version $Id$
 */
public interface PatientView
{

    /**
     * @return "open" or "restricted"
     */
    String getType();

    /**
     * Summarize patient information in a JSON object.
     *
     * <pre>
     *
     *      {
     *          "patientId" : <patient id>,
     *          "owner"     : <patient owner>,
     *          "phenotypeScore : <double score>,
     *          "phenotype" : [<String trait>, ...]
     *          "variants"  : [
     *              {
     *                  "geneSymbol : <String geneSymbol>,
     *                  "start"  : <Long position>,
     *                  "referenceBases"       : <String reference bases>,
     *                  "alternateBases"       : <String alternate bases or "-">,
     *                  "score"     : <Double exomiser score>,
     *                  "effect"    : <String predicted gene effect>
     *              },
     *              ...
     *          ]
     *
     *
     * </pre>
     *
     * @return a JSON object in the following format:
     */
    JSONObject toJSON();

    /**
     * Summarize patient information in a JSON object.
     * @param individualIDs a list of all valid patient ids to use in the search
     *
     * @return variant JSON object
     */
    JSONObject toJSON(Integer individualIDs);

    /**
     * Set the patient view type.
     * @param type 'restricted' or 'open'
     */
    void setType(String type);

    /**
     * Set the patient id.
     * @param id a valid phenotips internal id
     */
    void setPatientId(String id);

    /**
     * Set the url to the patient document.
     * @param patientURL a valid url
     */
    void setPatientURL(String patientURL);

    /**
     * Set the patient owner.
     * @param owner The owner of the patient.
     */
    void setOwner(String owner);

    /**
     * Set the patient phenotype.
     * @param phenotype A list of pretty name symptoms.
     */
    void setPhenotype(List<String> phenotype);

    /**
     * Set the patient variants.
     * @param variants a list of variants.
     */
    void setVariants(List<GAVariant> variants);

    /**
     * Set the patient phenotype.
     * @param phenotypeScore score between 0 and 1.
     */
    void setPhenotypeScore(double phenotypeScore);

    /**
     * Set the gene status for the patient.
     * @param newStatus 'solved', 'candidate' or 'rejected'
     */
    void setGeneStatus(String newStatus);

    /**
     * Set the patient diagnosis.
     * @param diagnosis A list of disorders to add to the patient view.
     */
    void setDiagnosis(List<Disorder> diagnosis);
}
