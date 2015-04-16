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
package org.phenotips.mendelianSearch.genotype;

import org.xwiki.component.annotation.Component;
import org.xwiki.component.phase.InitializationException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Named;
import javax.inject.Singleton;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * A dummy implementation of the variant store's query methods. Stores patients with ids P0000001 through P0000005.
 * Returned variants are rubbish.
 *
 * @version $Id$
 */
@Component
@Named("DummyStore")
@Singleton
public class MockVariantStore implements VariantStore
{
    private static final String PATIENT_PREFIX = "P000000";

    private Map<String, JSONArray> patients;

    /**
     * {@inheritDoc}
     *
     * @see org.xwiki.component.phase.Initializable#initialize()
     */
    @Override
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
    public Map<String, JSONArray> findPatients(String geneSymbol, List<String> variantEffects,
        Map<String, Double> alleleFrequencies)
    {
        // its just going to return patients 1 through 5

        Map<String, JSONArray> result = new HashMap<String, JSONArray>();
        for (int i = 1; i < 6; i++) {
            result.put(MockVariantStore.PATIENT_PREFIX + i, this.patients.get(PATIENT_PREFIX + i));
        }
        return result;
    }

    @Override
    public JSONArray getTopVariants(String patientId, int k)
    {
        int max = (k > this.patients.get(patientId).size()) ? this.patients.get(patientId).size() : k;
        JSONArray result = new JSONArray();
        result.addAll(this.patients.get(patientId).subList(0, max));
        return result;
    }

    @Override
    public Set<String> getAllPatientIds()
    {
        Set<String> ids = new HashSet<String>();
        for (int i = 1; i < 10; i++) {
            ids.add(MockVariantStore.PATIENT_PREFIX + i);
        }
        return ids;
    }

    @Override
    public Map<String, JSONArray> findPatients(String chr, int pos, String ref, String alt)
    {
        Map<String, JSONArray> result = new HashMap<String, JSONArray>();
        for (int i = 1; i < 3; i++) {
            result.put(MockVariantStore.PATIENT_PREFIX + i, this.patients.get(PATIENT_PREFIX + i));
        }
        return result;
    }

}
