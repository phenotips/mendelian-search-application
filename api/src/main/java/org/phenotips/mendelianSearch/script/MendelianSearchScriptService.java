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

import org.xwiki.component.annotation.Component;
import org.xwiki.stability.Unstable;

import java.util.List;
import java.util.Map;

import javax.inject.Named;
import javax.inject.Singleton;

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
public class MendelianSearchScriptService
{

    /**
     * Get a list of patients matching the specified input parameters.
     *
     * @param geneName The name of the gene being investigated
     * @param phenotype A list of symptoms being examined
     * @param variantEffects All variants return will have an effect listed in variantEffects.
     * @param variantFrequencies Cut-off frequencies for the variant search. Currently supporting values for 'EXAC' and
     *            'PhenomeCentral'
     * @param phenotypeMatching either 'strict' or 'fuzzy'
     * @param matchGene A value of {@code false} will return patients NOT matching the variant parameters.
     * @param matchPhenotype A value of {@code false} will return patients NOT matching the phenotype parameters.
     * @return A JSONArray of patients.
     */
    public JSONArray search(String geneName, List<String> phenotype, List<String> variantEffects,
        Map<String, String> variantFrequencies, String phenotypeMatching, boolean matchGene, boolean matchPhenotype)
    {
        MendelianSearchRequest request = new MendelianSearchRequest();
        return null;
    }

    /**
     * Get a list of patients matching the specified input parameters.
     *
     * @param geneName The name of the gene being investigated
     * @param phenotype A list of symptoms being examined
     * @param variantEffects All variants return will have an effect listed in variantEffects.
     * @param variantFrequencies Cut-off frequencies for the variant search. Currently supporting values for 'EXAC' and
     *            'PhenomeCentral'
     * @param phenotypeMatching either 'strict' or 'fuzzy'
     * @return Returns a JSONObject. Structure of the JSON object will differ whether or not phenotypeMatching is
     *         'strict' or 'fuzzy'. A value of 'strict' will result in a JSONObject with four keys: "withBoth",
     *         "withGeneOnly", "withPhenotypeOnly" and "withNeither". Each key will map to an integer value. A value of
     *         'fuzzy' will result in an JSONObject with two keys: "withGene" and "withoutGene". Each key will contain
     *         an array of doubles representing the phenotype scores for patients in each category.
     */
    public JSONObject count(String geneName, List<String> phenotype, List<String> variantEffects,
        Map<String, String> variantFrequencies, String phenotypeMatching)
    {
        return null;
    }
}
