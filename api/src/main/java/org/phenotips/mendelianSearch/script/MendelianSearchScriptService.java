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
package org.phenotips.mendelianSearch.script;

import org.phenotips.mendelianSearch.MendelianSearch;
import org.phenotips.mendelianSearch.PatientView;

import org.xwiki.component.annotation.Component;
import org.xwiki.script.service.ScriptService;
import org.xwiki.stability.Unstable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.xpn.xwiki.web.XWikiRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * API that provides methods for using the mendelian search application.
 *
 * @version $Id$
 * @since 1.0
 */
@Unstable
@Component
@Named("MendelianSearch")
@Singleton
public class MendelianSearchScriptService implements ScriptService
{
    @Inject
    private MendelianSearch ms;

    // The keys
    private String geneKey = "geneSymbol";

    private String phenotypeKey = "phenotype";

    private String variantEffectsKey = "variantEffects";

    private String alleleFrequenciesKey = "alleleFrequencies";

    private String varSearchKey = "variantSearch";

    private String chrKey = "varChr";

    private String posKey = "varPos";

    private String refKey = "varRef";

    private String altKey = "varAlt";

    /**
     * Get a list of patients matching the specified input parameters.
     *
     * @param rawRequest the request from the UI
     * @return A JSONArray of patients.
     */
    public JSONArray search(XWikiRequest rawRequest)
    {
        MendelianSearchRequest request = this.buildMendelianSearchRequest(rawRequest);

        List<PatientView> views = this.ms.search(request);

        return this.convertViewsToJSONArray(views);
    }

    /**
     * Get a list of patients matching the specified input parameters.
     *
     * @param rawRequest the request from the UI
     * @return Returns a JSONObject. Structure of the JSON object will differ whether or not phenotypeMatching is
     *         'strict' or 'fuzzy'. A value of 'strict' will result in a JSONObject with four keys: "withBoth",
     *         "withGeneOnly", "withPhenotypeOnly" and "withNeither". Each key will map to an integer value. A value of
     *         'fuzzy' will result in an JSONObject with two keys: "withGene" and "withoutGene". Each key will contain
     *         an array of doubles representing the phenotype scores for patients in each category.
     */
    public JSONObject count(XWikiRequest rawRequest)
    {
        MendelianSearchRequest request = this.buildMendelianSearchRequest(rawRequest);

        JSONObject searchResult = this.ms.search(request);
        JSONObject result = new JSONObject();

        List<Double> scores = new ArrayList<Double>();
        String scoreKey = "phenotypeScore";

        JSONArray patients = searchResult.getJSONArray("matching");
        for (int i = 0; i < patients.size(); i++) {
            JSONObject patient = patients.getJSONObject(i);
            scores.add(patient.getDouble(scoreKey));
        }
        result.element("withGene", scores);

        scores.clear();

        patients = searchResult.getJSONArray("nonMatching");
        for (int i = 0; i < patients.size(); i++) {
            JSONObject patient = patients.getJSONObject(i);
            scores.add(patient.getDouble(scoreKey));
        }
        result.element("withoutGene", scores);

        return result;
    }

    /**
     * @return A list of variant effects used by the variant store.
     */
    public String[] getVariantEffects()
    {
        String[] effects =
        { "MISSENSE", "FS_DELETION", "FS_INSERTION", "NON_FS_DELETION", "NON_FS_INSERTION", "STOPGAIN", "STOPLOSS",
        "FS_DUPLICATION", "SPLICING", "NON_FS_DUPLICATION", "FS_SUBSTITUTION", "NON_FS_SUBSTITUTION", "STARTLOSS",
        "ncRNA_EXONIC", "ncRNA_SPLICING", "UTR3", "UTR5", "SYNONYMOUS", "INTRONIC", "ncRNA_INTRONIC", "UPSTREAM",
        "DOWNSTREAM", "INTERGENIC" };
        return effects;

    }

    private MendelianSearchRequest buildMendelianSearchRequest(XWikiRequest in)
    {
        MendelianSearchRequest request = new MendelianSearchRequest();
        request.set(this.geneKey, in.getParameter("gene"));
        request.set(this.phenotypeKey, Arrays.asList(in.getParameterValues(this.phenotypeKey)));
        request.set(this.variantEffectsKey, Arrays.asList(in.getParameterValues("variant-effect")));

        Map<String, String> alleleFrequencies = new HashMap<String, String>();
        alleleFrequencies.put("PhenomeCentral", in.getParameter("allele-freq-pc"));
        alleleFrequencies.put("EXAC", in.getParameter("allele-freq-exac"));
        request.set(this.alleleFrequenciesKey, alleleFrequencies);
        request.set(this.chrKey, in.getParameter(this.chrKey));
        request.set(this.posKey, Integer.parseInt(in.getParameter(this.posKey)));
        request.set(this.refKey, in.getParameter(this.refKey));
        request.set(this.altKey, in.getParameter(this.altKey));
        request.set(this.varSearchKey, Integer.parseInt(in.getParameter(this.varSearchKey)));

        return request;
    }

    private JSONArray convertViewsToJSONArray(List<PatientView> views)
    {
        JSONArray result = new JSONArray();
        for (PatientView view : views) {
            result.add(view.toJSON());
        }
        return result;
    }
}
