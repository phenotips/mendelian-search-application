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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @version $Id$
 */
public class MockHPO implements Vocabulary
{
    private Map<String, VocabularyTerm> vocabulary;

    public MockHPO()
    {
        this.vocabulary = new HashMap<String, VocabularyTerm>();
        Set<VocabularyTerm> ancestors = new HashSet<VocabularyTerm>();
        VocabularyTerm all = new MockVocabularyTerm("HP:0000001", Collections.<VocabularyTerm>emptySet(),
            Collections.<VocabularyTerm>emptySet());
        ancestors.add(all);
        this.vocabulary.put("HP:0000001", all);
        VocabularyTerm phenotypes =
            new MockVocabularyTerm("HP:0000118", Collections.singleton(all), new HashSet<VocabularyTerm>(ancestors));
        ancestors.add(phenotypes);
        this.vocabulary.put("HP:0000118", phenotypes);

        VocabularyTerm abnormalNS =
            new MockVocabularyTerm("HP:0000707", Collections.singleton(phenotypes), new HashSet<VocabularyTerm>(ancestors));
        ancestors.add(abnormalNS);
        this.vocabulary.put("HP:0000707", abnormalNS);

        VocabularyTerm abnormalCNS =
            new MockVocabularyTerm("HP:0002011", Collections.singleton(abnormalNS), new HashSet<VocabularyTerm>(ancestors));
        ancestors.add(abnormalCNS);
        this.vocabulary.put("HP:0002011", abnormalCNS);

        VocabularyTerm abnormalHMF =
            new MockVocabularyTerm("HP:0011446", Collections.singleton(abnormalCNS), new HashSet<VocabularyTerm>(ancestors));
        ancestors.add(abnormalHMF);
        this.vocabulary.put("HP:0011446", abnormalHMF);

        VocabularyTerm cognImp =
            new MockVocabularyTerm("HP:0100543", Collections.singleton(abnormalHMF), new HashSet<VocabularyTerm>(ancestors));
        ancestors.add(cognImp);
        this.vocabulary.put("HP:0100543", cognImp);

        VocabularyTerm intDis =
            new MockVocabularyTerm("HP:0001249", Collections.singleton(cognImp), new HashSet<VocabularyTerm>(ancestors));
        ancestors.add(intDis);
        this.vocabulary.put("HP:0001249", intDis);

        VocabularyTerm mildIntDis =
            new MockVocabularyTerm("HP:0001256", Collections.singleton(intDis), new HashSet<VocabularyTerm>(ancestors));
        ancestors.add(mildIntDis);
        this.vocabulary.put("HP:0001256", mildIntDis);

        ancestors.clear();
        ancestors.add(all);
        ancestors.add(phenotypes);

        VocabularyTerm abnormalSkelS =
            new MockVocabularyTerm("HP:0000924", Collections.singleton(phenotypes), new HashSet<VocabularyTerm>(ancestors));
        ancestors.add(abnormalSkelS);
        this.vocabulary.put("HP:0000924", abnormalSkelS);

        VocabularyTerm abnormalSkelM =
            new MockVocabularyTerm("HP:0011842", Collections.singleton(abnormalSkelS), new HashSet<VocabularyTerm>(
                ancestors));
        ancestors.add(abnormalSkelM);
        this.vocabulary.put("HP:0011842", abnormalSkelM);

        VocabularyTerm abnormalJointMorph =
            new MockVocabularyTerm("HP:0001367", Collections.singleton(abnormalSkelM), new HashSet<VocabularyTerm>(
                ancestors));
        ancestors.add(abnormalJointMorph);
        this.vocabulary.put("HP:0001367", abnormalJointMorph);

        VocabularyTerm abnormalJointMob =
            new MockVocabularyTerm("HP:0011729", Collections.singleton(abnormalJointMorph), new HashSet<VocabularyTerm>(
                ancestors));
        ancestors.add(abnormalJointMob);
        this.vocabulary.put("HP:0011729", abnormalJointMob);

        VocabularyTerm jointHyperm =
            new MockVocabularyTerm("HP:0001382", Collections.singleton(abnormalJointMob), new HashSet<VocabularyTerm>(
                ancestors));
        ancestors.add(jointHyperm);
        this.vocabulary.put("HP:0001382", jointHyperm);
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
     * Non-functional search method. Always returns all terms.
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
     * Non-functional search method. Always returns all terms.
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

    /**
     * Non-functional search method. Always returns the size of the vocabulary.
     *
     * @param fieldValues Ignored
     * @return
     */
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
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * !IMPORTANT! Returns the KeySet of the vocabulary rather than aliases. Useful for testing.
     *
     * @return
     */
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
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getVersion()
    {
        return "1.0";
    }

    @Override
    public Set<VocabularyTerm> termSuggest(String query, Integer rows, String sort, String customFq)
    {
        return null;
    }

}
