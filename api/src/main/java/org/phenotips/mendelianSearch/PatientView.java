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

import org.phenotips.data.Disorder;

import java.util.List;

import org.ga4gh.GAVariant;

import net.sf.json.JSONObject;

public interface PatientView
{

    /**
     * @return "open" or "restricted"
     */
    String getType();

    /**
     * Summarize patient information in a JSON object
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
     *                  "position"  : <Long position>,
     *                  "ref"       : <String reference bases>,
     *                  "alt"       : <String alternate bases or "-">,
     *                  "score"     : <Double exomiser score>,
     *                  "effect"    : <String predicted gene effect
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

    void setType(String type);

    void setPatientId(String id);

    void setPatientURL(String patientURL);

    void setOwner(String owner);

    void setPhenotype(List<String> phenotype);

    void setVariants(List<GAVariant> variants);

    void setPhenotypeScore(double phenotypeScore);

    void setGeneStatus(String newStatus);

    void setDiagnosis(List<Disorder> diagnosis);

}
