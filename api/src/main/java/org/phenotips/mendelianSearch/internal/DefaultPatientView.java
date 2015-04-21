package org.phenotips.mendelianSearch.internal;

import org.phenotips.mendelianSearch.PatientView;

import java.util.List;
import java.util.Map;

import org.ga4gh.GAVariant;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class DefaultPatientView implements PatientView
{
    private String type;

    private String patientID;

    private String owner;

    private double phenotypeScore;

    private List<String> phenotype;

    private List<GAVariant> variants;

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
        result.element("owner", this.owner);
        result.element("phenotypeScore", this.phenotypeScore);

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

}