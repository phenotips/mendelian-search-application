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

import org.phenotips.mendelianSearch.MendelianSearchRequestFactory;

import org.xwiki.component.annotation.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.inject.Singleton;

import com.xpn.xwiki.web.XWikiRequest;

/**
 * @Version $Id$
 */
@Component
@Singleton
public class DefaultMendelianSearchRequestFactory implements MendelianSearchRequestFactory
{
    // The search keys
    private String requestId = "id";

    private String geneKey = "geneSymbol";

    private String phenotypeKey = "phenotype";

    private String phenotypeMatchingKey = "phenotypeMatching";

    private String variantEffectsKey = "variantEffects";

    private String alleleFrequenciesKey = "alleleFrequencies";

    private String sortKey = "sort";

    private String ascKey = "asc";

    private String pageKey = "page";

    private String resultsPerPageKey = "resultsPerPage";

    //The view keys

    private String matchGeneKey = "matchGene";

    private String matchPhenotypeKey = "matchPhenotype";


    @Override public MendelianSearchRequest makeRequest(XWikiRequest rawRequest)
    {
        MendelianSearchRequest request = new MendelianSearchRequest();
        request.setId(this.generateRequestId());
        request.set(this.geneKey, rawRequest.getParameter("gene"));
        request.set(this.phenotypeKey, Arrays.asList(rawRequest.getParameterValues(this.phenotypeKey)));
        request.set(this.variantEffectsKey, Arrays.asList(rawRequest.getParameterValues("variant-effect")));

        Map<String, String> alleleFrequencies = new HashMap<String, String>();
        alleleFrequencies.put("PhenomeCentral", rawRequest.getParameter("allele-freq-pc"));
        alleleFrequencies.put("EXAC", rawRequest.getParameter("allele-freq-exac"));

        request.set(this.alleleFrequenciesKey, alleleFrequencies);
        request.set(this.phenotypeMatchingKey, rawRequest.getParameter("phenotype-matching"));
        request.set(this.matchGeneKey, Integer.parseInt(rawRequest.getParameter(this.matchGeneKey)));
        request.set(this.matchPhenotypeKey, Integer.parseInt(rawRequest.getParameter(this.matchPhenotypeKey)));

        if (rawRequest.getParameter(this.pageKey) != null) {
            request.set(this.sortKey, rawRequest.getParameter(this.sortKey));
            request.set(this.ascKey, Boolean.parseBoolean(rawRequest.getParameter(this.ascKey)));
            request.set(this.pageKey, Integer.parseInt(rawRequest.getParameter(this.pageKey)));
            request.set(this.resultsPerPageKey, Integer.parseInt(rawRequest.getParameter(this.resultsPerPageKey)));
        }
        return request;
    }

    private String generateRequestId(){
        return UUID.randomUUID().toString();
    }
}
