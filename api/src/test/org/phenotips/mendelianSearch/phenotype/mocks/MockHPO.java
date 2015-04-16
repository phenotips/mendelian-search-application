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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @version $Id$
 */
public class MockHPO implements OntologyService
{
    private Map<String, OntologyTerm> ontology;

    public MockHPO()
    {
        this.ontology = new HashMap<String, OntologyTerm>();
        Set<OntologyTerm> ancestors = new HashSet<OntologyTerm>();
        OntologyTerm all = new MockOntologyTerm("HP:0000001", Collections.<OntologyTerm>emptySet(),
            Collections.<OntologyTerm>emptySet());
        ancestors.add(all);
        this.ontology.put("HP:0000001", all);
        OntologyTerm phenotypes =
            new MockOntologyTerm("HP:0000118", Collections.singleton(all), new HashSet<OntologyTerm>(ancestors));
        ancestors.add(phenotypes);
        this.ontology.put("HP:0000118", phenotypes);

        OntologyTerm abnormalNS =
            new MockOntologyTerm("HP:0000707", Collections.singleton(phenotypes), new HashSet<OntologyTerm>(ancestors));
        ancestors.add(abnormalNS);
        this.ontology.put("HP:0000707", abnormalNS);

        OntologyTerm abnormalCNS =
            new MockOntologyTerm("HP:0002011", Collections.singleton(abnormalNS), new HashSet<OntologyTerm>(ancestors));
        ancestors.add(abnormalCNS);
        this.ontology.put("HP:0002011", abnormalCNS);

        OntologyTerm abnormalHMF =
            new MockOntologyTerm("HP:0011446", Collections.singleton(abnormalCNS), new HashSet<OntologyTerm>(ancestors));
        ancestors.add(abnormalHMF);
        this.ontology.put("HP:0011446", abnormalHMF);

        OntologyTerm cognImp =
            new MockOntologyTerm("HP:0100543", Collections.singleton(abnormalHMF), new HashSet<OntologyTerm>(ancestors));
        ancestors.add(cognImp);
        this.ontology.put("HP:0100543", cognImp);

        OntologyTerm intDis =
            new MockOntologyTerm("HP:0001249", Collections.singleton(cognImp), new HashSet<OntologyTerm>(ancestors));
        ancestors.add(intDis);
        this.ontology.put("HP:0001249", intDis);

        OntologyTerm mildIntDis =
            new MockOntologyTerm("HP:0001256", Collections.singleton(intDis), new HashSet<OntologyTerm>(ancestors));
        ancestors.add(mildIntDis);
        this.ontology.put("HP:0001256", mildIntDis);

        ancestors.clear();
        ancestors.add(all);
        ancestors.add(phenotypes);

        OntologyTerm abnormalSkelS =
            new MockOntologyTerm("HP:0000924", Collections.singleton(phenotypes), new HashSet<OntologyTerm>(ancestors));
        ancestors.add(abnormalSkelS);
        this.ontology.put("HP:0000924", abnormalSkelS);

        OntologyTerm abnormalSkelM =
            new MockOntologyTerm("HP:0011842", Collections.singleton(abnormalSkelS), new HashSet<OntologyTerm>(
                ancestors));
        ancestors.add(abnormalSkelM);
        this.ontology.put("HP:0011842", abnormalSkelM);

        OntologyTerm abnormalJointMorph =
            new MockOntologyTerm("HP:0001367", Collections.singleton(abnormalSkelM), new HashSet<OntologyTerm>(
                ancestors));
        ancestors.add(abnormalJointMorph);
        this.ontology.put("HP:0001367", abnormalJointMorph);

        OntologyTerm abnormalJointMob =
            new MockOntologyTerm("HP:0011729", Collections.singleton(abnormalJointMorph), new HashSet<OntologyTerm>(
                ancestors));
        ancestors.add(abnormalJointMob);
        this.ontology.put("HP:0011729", abnormalJointMob);

        OntologyTerm jointHyperm =
            new MockOntologyTerm("HP:0001382", Collections.singleton(abnormalJointMob), new HashSet<OntologyTerm>(
                ancestors));
        ancestors.add(jointHyperm);
        this.ontology.put("HP:0001382", jointHyperm);
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
     * Non-functional search method. Always returns all terms.
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
     * Non-functional search method. Always returns all terms.
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

    /**
     * Non-functional search method. Always returns the size of the ontology.
     *
     * @param fieldValues Ignored
     * @return
     */
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
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * !IMPORTANT! Returns the KeySet of the ontology rather than aliases. Useful for testing.
     *
     * @return
     */
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
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getVersion()
    {
        return "1.0";
    }

    @Override
    public Set<OntologyTerm> termSuggest(String query, Integer rows, String sort, String customFq)
    {
        return null;
    }

}
