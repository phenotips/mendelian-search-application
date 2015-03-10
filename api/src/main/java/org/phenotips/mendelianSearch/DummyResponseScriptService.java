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
package org.phenotips.mendelianSearch;

import org.xwiki.component.annotation.Component;
import org.xwiki.script.service.ScriptService;
import org.xwiki.stability.Unstable;

import java.util.List;

import javax.inject.Named;
import javax.inject.Singleton;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * API that provides a dummy response to allow UI development.
 *
 * @version $Id$
 * @since 1.0
 */
@Unstable
@Component
@Named("DummyMendelianSearch")
@Singleton
public class DummyResponseScriptService implements ScriptService
{
    private final String withGene = "withGene";

    private final String resultsPerPage = "resultsPerPage";

    private final String matching = "matching";

    /**
     * Generates a dummy patient for ui building.
     *
     * @param gene gene
     * @param phenotype phenotype
     * @param matching String
     * @param freqExac double
     * @param freqPC double
     * @param resultsPerPage integer
     * @param page integer
     * @return a list of patients in a JSONArray
     */
    public JSONArray search(String gene, List<String> phenotype, String matching,
        double freqExac, double freqPC, int resultsPerPage, int page)
    {
        JSONArray response = new JSONArray();

        if ((gene.isEmpty()) || phenotype.isEmpty()) {
            return null;
        }

        for (int i = 0; i < resultsPerPage; i++) {
            response.add(this.generateDummyPatient());
        }
        return response;
    }

    /**
     * @param gene gene
     * @param phenotype phenotype
     * @param matching String
     * @param variantEffects key
     * @param freqExac double
     * @param freqPC double
     * @return a count of patients in the four categories
     */
    public JSONObject count(String gene, String[] phenotype, String matching, String[] variantEffects,
        double freqExac, double freqPC)
    {
        boolean simpleResult = "strict".equals(matching);
        JSONObject result = new JSONObject();

        if (simpleResult) {
            result.element("withBoth", 30);
            result.element(this.withGene, 20);
            result.element("withPhenotype", 10);
            result.element("withNeither", 234);
            return result;
        } else {
            double[] scores = { 0.43, 0.45, 0.64, 0.32, 0.54, 0.56, 0.54, 0.34, 0.45, 0.46 };
            result.element(this.withGene, scores);
            double[] otherScores = { 0.23, 0.12, 0.45, 0.35, 0.21, 0.24, 0.12, 0.34, 0.21, 0.42, 0.31 };
            result.element("withoutGene", otherScores);
            return result;
        }
    }

    private JSONObject generateDummyPatient()
    {
        JSONObject patient = new JSONObject();
        patient.element("id", "P" + (((Double) Math.floor(Math.random() * 1000))).intValue());
        patient.element("owner", "RANDOMOWNER");
        patient.element("diagnosis", "RANDOM_DISEASE_COULD_BE_A_LONG_STRING");

        String[] phenotype = { "Big head", "Long arms", "Allodynia" };
        patient.element("phenotype", phenotype);
        String position = "position";
        String ref = "ref";
        String chr = "chr";
        String alt = "alt";
        String type = "type";
        String score = "score";
        JSONArray variants = new JSONArray();
        JSONObject v = new JSONObject();
        v.element(position, "4532183485231732");
        v.element(chr, "21");
        v.element(type, "MISSENSE");
        v.element(ref, "T");
        v.element(alt, "TTATATATA");
        v.element(score, 0.6);
        variants.add(v);

        v.element(position, "453218348521732");
        v.element(chr, "X");
        v.element(type, "NONSENSE");
        v.element(ref, "A");
        v.element(alt, "TATATATATATA");
        v.element(score, 0.98);
        variants.add(v);
        patient.element("genotype", variants);

        return patient;
    }
}
