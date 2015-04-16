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
package org.phenotips.mendelianSearch.phenotype.mocks;

import org.phenotips.ontology.OntologyService;
import org.phenotips.ontology.OntologyTerm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MockMIM implements OntologyService
{
    private Map<String, OntologyTerm> ontology;

    public MockMIM()
    {
        this.ontology = new HashMap<String, OntologyTerm>();

        ArrayList<String> symptoms = new ArrayList<String>();
        symptoms.add("HP:0011729");
        symptoms.add("HP:0001367");
        OntologyTerm arthritis = new MockDisease("MIM:1", "arthritis", new ArrayList<String>(symptoms));
        this.ontology.put("MIM:1", arthritis);

        symptoms.clear();
        symptoms.add("HP:0001382");
        symptoms.add("HP:0011729");
        OntologyTerm tooFlexible = new MockDisease("MIM:2", "tooFlexible", new ArrayList<String>(symptoms));
        this.ontology.put("MIM:2", tooFlexible);

        symptoms.clear();
        symptoms.add(null);
        OntologyTerm normal = new MockDisease("MIM:3", "normal", new ArrayList<String>(symptoms));
        this.ontology.put("MIM:3", normal);

        symptoms.clear();
        OntologyTerm NullSymptoms = new MockDisease("MIM:4", "NullSymptoms", null);
        this.ontology.put("MIM:4", NullSymptoms);
    }

    @Override
    public OntologyTerm getTerm(String id)
    {
        return this.ontology.get(id);
    }

    @Override
    public Set<OntologyTerm> getTerms(Collection<String> ids)
    {
        return new HashSet<OntologyTerm>(this.ontology.values());
    }

    /**
     * Not a function search. Returns all terms in the Ontology.
     *
     * @param fieldValues Ignored
     * @return
     */
    @Override
    public Set<OntologyTerm> search(Map<String, ?> fieldValues)
    {
        return new HashSet<OntologyTerm>(this.ontology.values());
    }

    /**
     * Not a function search. Returns all terms in the Ontology.
     *
     * @param fieldValues Ignored
     * @param queryOptions Ignored
     * @return
     */
    @Override
    public Set<OntologyTerm> search(Map<String, ?> fieldValues, Map<String, String> queryOptions)
    {
        return new HashSet<OntologyTerm>(this.ontology.values());
    }

    @Override
    public long count(Map<String, ?> fieldValues)
    {
        return this.ontology.size();
    }

    @Override
    public long getDistance(String fromTermId, String toTermId)
    {
        return 0;
    }

    @Override
    public long getDistance(OntologyTerm fromTerm, OntologyTerm toTerm)
    {
        return 0;
    }

    @Override
    public Set<String> getAliases()
    {
        return this.ontology.keySet();
    }

    @Override
    public long size()
    {
        return this.ontology.size();
    }

    @Override
    public int reindex(String ontologyUrl)
    {
        return 0;
    }

    @Override
    public String getDefaultOntologyLocation()
    {
        return null;
    }

    @Override
    public String getVersion()
    {
        return "1";
    }

    @Override
    public Set<OntologyTerm> termSuggest(String query, Integer rows, String sort, String customFq)
    {
        return null;
    }

}
