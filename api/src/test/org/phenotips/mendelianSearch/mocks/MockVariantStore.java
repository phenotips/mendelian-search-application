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
package org.phenotips.mendelianSearch.mocks;

import org.phenotips.variantStoreIntegration.VariantStoreService;
import org.phenotips.variantstore.shared.VariantStoreException;

import org.xwiki.component.phase.InitializationException;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import org.ga4gh.GAVariant;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * A dummy implementation of the variant store's query methods. Stores patients with ids P0000001 through P0000005.
 * Returned variants are rubbish.
 *
 * @version $Id$
 */
public class MockVariantStore implements VariantStoreService
{
    private static final String PATIENT_PREFIX = "P000000";

    private Map<String, JSONArray> patients;

    /**
     * {@inheritDoc}
     *
     * @see org.xwiki.component.phase.Initializable#initialize()
     */
    public void initialize() throws InitializationException
    {
        this.patients = new HashMap<String, JSONArray>();
        String geneSymbol = "AAA34";
        String ref = "ATC";
        String alt = "-";
        String position = "45215587452158";
        Double score = Math.random();
        String effect = "MISSENSE";

        for (int i = 1; i < 10; i++) {
            String id = MockVariantStore.PATIENT_PREFIX + i;
            JSONArray variants = new JSONArray();
            for (int j = 0; j < 3; j++) {
                JSONObject variant = new JSONObject();
                variant.element("geneSymbol", geneSymbol);
                variant.element("ref", ref);
                variant.element("alt", alt);
                variant.element("chr", String.valueOf(Math.round(Math.random() * 21)));
                variant.element("position", position);
                variant.element("score", score);
                variant.element("effect", effect);
                variants.add(variant);
            }
            this.patients.put(id, variants);
        }

    }

    @Override
    public Map<String, List<GAVariant>> getIndividualsWithGene(String geneSymbol, List<String> variantEffects,
        Map<String, Double> alleleFrequencies)
        {
        // its just going to return patients 1 through 5

        Map<String, JSONArray> result = new HashMap<String, JSONArray>();
        for (int i = 1; i < 6; i++) {
            result.put(MockVariantStore.PATIENT_PREFIX + i, this.patients.get(PATIENT_PREFIX + i));
        }

        return new HashMap<String, List<GAVariant>>();
        }

    @Override
    public JSONArray getTopHarmfullVariants(String patientId, int k)
    {
        int max = (k > this.patients.get(patientId).size()) ? this.patients.get(patientId).size() : k;
        JSONArray result = new JSONArray();
        result.addAll(this.patients.get(patientId).subList(0, max));
        return result;
    }

    @Override
    public Map<String, List<GAVariant>> getIndividualsWithVariant(String chr, int pos, String ref, String alt)
    {
        Map<String, JSONArray> result = new HashMap<String, JSONArray>();
        for (int i = 1; i < 3; i++) {
            result.put(MockVariantStore.PATIENT_PREFIX + i, this.patients.get(PATIENT_PREFIX + i));
        }
        return new HashMap<String, List<GAVariant>>();
    }

    @Override
    public List<String> getIndividuals()
    {
        List<String> ids = new ArrayList<String>();
        for (int i = 1; i < 10; i++) {
            ids.add(MockVariantStore.PATIENT_PREFIX + i);
        }
        return ids;
    }

    @Override
    public void stop()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public Future addIndividual(String id, boolean isPublic, Path file) throws VariantStoreException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Future removeIndividual(String id) throws VariantStoreException
    {
        // TODO Auto-generated method stub
        return null;
    }

}
