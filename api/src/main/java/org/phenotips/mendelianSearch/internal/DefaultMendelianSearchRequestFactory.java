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

package org.phenotips.mendelianSearch.internal;

import org.phenotips.mendelianSearch.MendelianSearchRequestFactory;
import org.phenotips.mendelianSearch.script.MendelianSearchRequest;

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

    private String variantEffectsKey = "variantEffects";

    private String alleleFrequenciesKey = "alleleFrequencies";

    private String varSearchKey = "variantSearch";

    private String chrKey = "varChr";

    private String posKey = "varPos";

    private String refKey = "varRef";

    private String altKey = "varAlt";

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
        request.set(this.chrKey, rawRequest.getParameter(this.chrKey));
        request.set(this.posKey, Integer.parseInt(rawRequest.getParameter(this.posKey)));
        request.set(this.refKey, rawRequest.getParameter(this.refKey));
        request.set(this.altKey, rawRequest.getParameter(this.altKey));
        request.set(this.varSearchKey, Integer.parseInt(rawRequest.getParameter(this.varSearchKey)));
        request.set("phenotypeMatching", rawRequest.getParameter("phenotype-matching"));
        request.set(this.matchGeneKey, Integer.parseInt(rawRequest.getParameter(this.matchGeneKey)));
        request.set(this.matchPhenotypeKey, Integer.parseInt(rawRequest.getParameter(this.matchPhenotypeKey)));

        return request;
    }

    private String generateRequestId(){
        return UUID.randomUUID().toString();
    }
}
