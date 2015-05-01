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
package org.phenotips.mendelianSearch.internal;

import org.phenotips.data.Disorder;
import org.phenotips.mendelianSearch.PatientView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ga4gh.GAVariant;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

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

    @Override
    public String getType()
    {
        return this.type;
    }

    @Override
    public JSONObject toJSON()
    {
        JSONObject result = new JSONObject();

        result.element("patientId", this.patientID);
        result.element("patientURL", this.patientURL);
        result.element("owner", this.owner);
        result.element("phenotypeScore", this.phenotypeScore);
        result.element("geneStatus", this.geneStatus);
        result.element("diagnosis", this.disordersToJSON(this.diagnosis));

        JSONArray variantJSONs = new JSONArray();
        if (!this.variants.isEmpty()) {
            for (GAVariant v : this.variants) {
                variantJSONs.add(this.GAVariantToJSON(v));
            }
        }
        result.element("variants", variantJSONs);
        result.element("phenotype", this.phenotype);
        return result;
    }

    private List<JSONObject> disordersToJSON(List<Disorder> diagnosis)
    {
        List<JSONObject> result = new ArrayList<JSONObject>();
        if (diagnosis == null ||diagnosis.isEmpty()){
            return result;
        }
        for(Disorder disorder : diagnosis){
            JSONObject disorderJSON = new JSONObject();
            disorderJSON.element("id", disorder.getId());
            disorderJSON.element("name", disorder.getName());
            result.add(disorderJSON);
        }
        return result;
    }

    private JSONObject GAVariantToJSON(GAVariant rawV)
    {
        JSONObject v = new JSONObject();
        v.put("position", rawV.getStart());
        v.put("ref", rawV.getReferenceBases());
        v.put("chr", rawV.getReferenceName());
        // We are only showing the first possible alternates.
        List<String> alternates = rawV.getAlternateBases();
        if (alternates != null && !alternates.isEmpty()) {
            v.put("alt", alternates.get(0));
        } else {
            v.put("alt", "");
        }

        Map<String, List<String>> rawVInfo = rawV.getInfo();

        List<String> exomiserScore = rawVInfo.get("EXOMISER_VARIANT_SCORE");
        if (exomiserScore != null && !exomiserScore.isEmpty()) {
            v.put("score", Double.parseDouble(exomiserScore.get(0)));
        }

        List<String> geneEffect = rawVInfo.get("GENE_EFFECT");
        if (geneEffect != null && !geneEffect.isEmpty()) {
            v.put("effect", geneEffect.get(0));
        }

        List<String> geneSymbol = rawVInfo.get("GENE");
        if (geneSymbol != null && !geneSymbol.isEmpty()) {
            v.put("geneSymbol", geneSymbol.get(0));
        }

        return v;
    }

    public DefaultPatientView()
    {
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

    public String getGeneStatus()
    {
        return this.geneStatus;
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

    public String getPatientURL()
    {
        return patientURL;
    }

    @Override
    public void setPatientURL(String patientURL)
    {
        this.patientURL = patientURL;
    }

    public List<Disorder> getDiagnosis()
    {
        return diagnosis;
    }

    @Override
    public void setDiagnosis(List<Disorder> diagnosis)
    {
        this.diagnosis = diagnosis;
    }
}
