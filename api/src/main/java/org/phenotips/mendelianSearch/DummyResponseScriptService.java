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
    /**
     * Generates a dummy patient for ui building.
     *
     * @param gene gene
     * @param phenotype phenotype
     * @param maxPatients max patients
     * @return a list of patients in a JSONArray
     */
    public JSONArray search(String gene, List<String> phenotype, int maxPatients)
    {
        JSONArray response = new JSONArray();
        for (int i = 0; i < maxPatients; i++) {
            response.add(this.generateDummyPatient());
        }
        return response;
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
        JSONArray variants = new JSONArray();
        JSONObject v1 = new JSONObject();
        v1.element(position, "4532183485231732");
        v1.element(chr, "21");
        v1.element(type, "MISSENSE");
        v1.element(ref, "T");
        v1.element(alt, "TTATATATA");
        variants.add(v1);
        JSONObject v2 = new JSONObject();
        v2.element(position, "453218348521732");
        v2.element(chr, "X");
        v2.element(type, "NONSENSE");
        v2.element(ref, "A");
        v2.element(alt, "TATATATATATA");
        variants.add(v2);
        patient.element("genotype", variants);

        return patient;
    }
}
