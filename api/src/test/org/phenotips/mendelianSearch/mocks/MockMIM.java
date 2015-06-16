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

import org.phenotips.vocabulary.Vocabulary;
import org.phenotips.vocabulary.VocabularyTerm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MockMIM implements Vocabulary
{
    private Map<String, VocabularyTerm> vocabulary;

    public MockMIM()
    {
        this.vocabulary = new HashMap<String, VocabularyTerm>();

        ArrayList<String> symptoms = new ArrayList<String>();
        symptoms.add("HP:0011729");
        symptoms.add("HP:0001367");
        VocabularyTerm arthritis = new MockDisease("MIM:1", "arthritis", new ArrayList<String>(symptoms));
        this.vocabulary.put("MIM:1", arthritis);

        symptoms.clear();
        symptoms.add("HP:0001382");
        symptoms.add("HP:0011729");
        VocabularyTerm tooFlexible = new MockDisease("MIM:2", "tooFlexible", new ArrayList<String>(symptoms));
        this.vocabulary.put("MIM:2", tooFlexible);

        symptoms.clear();
        symptoms.add(null);
        VocabularyTerm normal = new MockDisease("MIM:3", "normal", new ArrayList<String>(symptoms));
        this.vocabulary.put("MIM:3", normal);

        symptoms.clear();
        VocabularyTerm NullSymptoms = new MockDisease("MIM:4", "NullSymptoms", null);
        this.vocabulary.put("MIM:4", NullSymptoms);
    }

    @Override
    public VocabularyTerm getTerm(String id)
    {
        return this.vocabulary.get(id);
    }

    @Override
    public Set<VocabularyTerm> getTerms(Collection<String> ids)
    {
        return new HashSet<VocabularyTerm>(this.vocabulary.values());
    }

    /**
     * Not a function search. Returns all terms in the Ontology.
     *
     * @param fieldValues Ignored
     * @return
     */
    @Override
    public Set<VocabularyTerm> search(Map<String, ?> fieldValues)
    {
        return new HashSet<VocabularyTerm>(this.vocabulary.values());
    }

    /**
     * Not a function search. Returns all terms in the Ontology.
     *
     * @param fieldValues Ignored
     * @param queryOptions Ignored
     * @return
     */
    @Override
    public Set<VocabularyTerm> search(Map<String, ?> fieldValues, Map<String, String> queryOptions)
    {
        return new HashSet<VocabularyTerm>(this.vocabulary.values());
    }

    @Override
    public long count(Map<String, ?> fieldValues)
    {
        return this.vocabulary.size();
    }

    @Override
    public long getDistance(String fromTermId, String toTermId)
    {
        return 0;
    }

    @Override
    public long getDistance(VocabularyTerm fromTerm, VocabularyTerm toTerm)
    {
        return 0;
    }

    @Override
    public Set<String> getAliases()
    {
        return this.vocabulary.keySet();
    }

    @Override
    public long size()
    {
        return this.vocabulary.size();
    }

    @Override
    public int reindex(String vocabularyUrl)
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
    public Set<VocabularyTerm> termSuggest(String query, Integer rows, String sort, String customFq)
    {
        return null;
    }

}
