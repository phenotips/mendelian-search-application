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
package org.phenotips.mendelianSearch.script;

import org.phenotips.mendelianSearch.MendelianSearch;
import org.phenotips.mendelianSearch.MendelianSearchRequestFactory;
import org.phenotips.mendelianSearch.PatientView;
import org.phenotips.mendelianSearch.internal.MendelianSearchRequest;
import org.phenotips.mendelianSearch.internal.MendelianVariantCategory;
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

import org.json.JSONObject;

import com.xpn.xwiki.web.XWikiRequest;

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
        metaData.put("numberOfResults", patientJSONs.size());

        //Sort data
        String sortString = "sort";
        String ascendingString = "asc";
        String sortKey = (request.get(sortString) != null) ? (String) request.get(sortString) : "patientId";
        boolean ascending = (request.get(ascendingString) != null) ? (boolean) request.get(ascendingString) : true;
        PatientViewUtils.sortPatientViewJSONs(patientJSONs, sortKey, ascending);

        //Paginate data
        String pageString = "page";
        int page = (request.get(pageString) != null) ? (int) request.get(pageString) : 1;
        String resultsPerPageString = "resultsPerPage";
        int resultsPerPage = (request.get(resultsPerPageString) != null) ? (int) request.get(resultsPerPageString) : 20;
        patientJSONs = PatientViewUtils.paginatePatientViewJSON(patientJSONs, page, resultsPerPage);

        response.put("meta", metaData);
        response.put("patients", patientJSONs);
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

        JSONObject result = new JSONObject(overview);

        return result;
    }

    /**
     * Get a map of all searchable variant effects, grouped into broader variant categories.
     *
     * @return An ordered map of categories summarizing all of the variant effects used by the variant store.
     */
    public Map<String, MendelianVariantCategory> getVariantCategories()
    {
        return this.ms.getVariantCategories();
    }


    private List<JSONObject> convertViewsToArrayOfJSON(List<PatientView> views)
    {
        List<JSONObject> result = new ArrayList<JSONObject>();
        Integer ids = this.ms.findValidIds().size();
        for (PatientView view : views) {
            if ("open".equals(view.getType())) {
                result.add(view.toJSON(ids));
            }
        }
        return result;
    }

}
