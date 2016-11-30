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
package org.phenotips.mendelianSearch.internal;

import org.phenotips.data.Disorder;
import org.phenotips.mendelianSearch.PatientView;
import org.phenotips.variantStoreIntegration.VariantStoreVariant;

import java.util.ArrayList;
import java.util.List;

import org.ga4gh.GAVariant;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Default patient view for storing response information.
 *
 * @version $Id$
 */
public class DefaultPatientView implements PatientView
{
    private String type;

    private String patientID;

    private String patientURL;

    private String owner;

    private double phenotypeScore;

    private List<String> phenotype;

    private List<GAVariant> variants;

    private String geneStatus;

    private List<Disorder> diagnosis;

    /**
     * Empty constructor for a DefaultPatientView.
     */
    public DefaultPatientView()
    {
    }

    @Override
    public String getType()
    {
        return this.type;
    }

    @Override
    public JSONObject toJSON()
    {
        JSONObject result = new JSONObject();

        result.put("patientId", this.patientID);
        result.put("patientURL", this.patientURL);
        result.put("owner", this.owner);
        result.put("phenotypeScore", this.phenotypeScore);
        result.put("geneStatus", this.geneStatus);
        result.put("diagnosis", this.disordersToJSON(this.diagnosis));

        JSONArray variantJSONs = new JSONArray();
        if (!this.variants.isEmpty()) {
            for (GAVariant v : this.variants) {
                variantJSONs.put(this.convertGAVariantToJSON(v));
            }
        }
        result.put("variants", variantJSONs);
        result.put("phenotype", this.phenotype);
        return result;
    }

    private List<JSONObject> disordersToJSON(List<Disorder> diagnosis)
    {
        List<JSONObject> result = new ArrayList<JSONObject>();
        if (diagnosis == null || diagnosis.isEmpty()) {
            return result;
        }
        for (Disorder disorder : diagnosis) {
            JSONObject disorderJSON = new JSONObject();
            disorderJSON.put("id", disorder.getId());
            disorderJSON.put("name", disorder.getName());
            result.add(disorderJSON);
        }
        return result;
    }

    private JSONObject convertGAVariantToJSON(GAVariant rawV)
    {
        return (new VariantStoreVariant(rawV)).toJSON();
    }

    @Override
    public void setType(String type)
    {
        this.type = type;
    }

    @Override
    public void setOwner(String owner)
    {
        this.owner = owner;
    }

    @Override
    public void setPhenotypeScore(double phenotypeScore)
    {
        this.phenotypeScore = phenotypeScore;
    }

    @Override
    public void setGeneStatus(String newStatus)
    {
        this.geneStatus = newStatus;
    }

    @Override
    public void setPhenotype(List<String> phenotype)
    {
        this.phenotype = phenotype;
    }

    @Override
    public void setVariants(List<GAVariant> variants)
    {
        this.variants = variants;
    }

    @Override
    public void setPatientId(String id)
    {
        this.patientID = id;
    }

    @Override
    public void setPatientURL(String patientURL)
    {
        this.patientURL = patientURL;
    }

    @Override
    public void setDiagnosis(List<Disorder> diagnosis)
    {
        this.diagnosis = diagnosis;
    }
}
