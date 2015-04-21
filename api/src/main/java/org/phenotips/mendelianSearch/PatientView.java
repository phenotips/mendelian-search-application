package org.phenotips.mendelianSearch;

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

    void setOwner(String owner);

    void setPhenotype(List<String> phenotype);

    void setVariants(List<GAVariant> variants);

    void setPhenotypeScore(double phenotypeScore);
}
