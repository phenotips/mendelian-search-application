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
package org.phenotips.mendelianSearch.script;

import org.phenotips.mendelianSearch.MendelianSearch;
import org.phenotips.mendelianSearch.MendelianSearchRequestFactory;
import org.phenotips.mendelianSearch.PatientView;
import org.phenotips.mendelianSearch.internal.MendelianSearchRequest;
import org.phenotips.mendelianSearch.internal.PatientViewUtils;

import org.xwiki.component.annotation.Component;
import org.xwiki.script.service.ScriptService;
import org.xwiki.stability.Unstable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.xpn.xwiki.web.XWikiRequest;

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

    @Inject
    private MendelianSearchRequestFactory requestFactory;



    /**
     * Get a list of patients matching the specified input parameters.
     *
     * @param rawRequest the request from the UI
     * @return A JSONArray of patients.
     */
    public JSONObject search(XWikiRequest rawRequest)
    {
        JSONObject response = new JSONObject();
        JSONObject metaData = new JSONObject();

        MendelianSearchRequest request = this.requestFactory.makeRequest(rawRequest);

        List<PatientView> views = this.ms.search(request);
        List<JSONObject> patientJSONs = this.convertViewsToArrayOfJSON(views);
        metaData.element("numberOfResults", patientJSONs.size());

        //Sort data
        String sortKey = (request.get("sort") != null) ? (String) request.get("sort") : "patientId";
        boolean ascending = (request.get("asc") != null) ? (boolean) request.get("asc") : true;
        PatientViewUtils.sortPatientViewJSONs(patientJSONs, sortKey, ascending);

        //Paginate data
        int page = (request.get("page") != null) ? (int) request.get("page") : 1;
        int resultsPerPage = (request.get("resultsPerPage") != null) ? (int) request.get("resultsPerPage") : 20;
        patientJSONs = PatientViewUtils.paginatePatientViewJSON(patientJSONs, page, resultsPerPage);

        response.element("meta", metaData);
        response.element("patients", patientJSONs);
        return response;
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
    public JSONObject getOverview(XWikiRequest rawRequest)
    {
        MendelianSearchRequest request = this.requestFactory.makeRequest(rawRequest);

        Map<String, Object> overview = this.ms.getOverview(request);

        JSONObject result = JSONObject.fromObject(overview);

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


    private List<JSONObject> convertViewsToArrayOfJSON(List<PatientView> views)
    {
        List<JSONObject> result = new ArrayList<JSONObject>();
        for (PatientView view : views) {
            if (view.values().equals("open")) {
                result.add(view.toJSON());
            }
        }
        return result;
    }

}
