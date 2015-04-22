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
package org.phenotips.mendelianSearch.phenotype;

import org.phenotips.data.Feature;
import org.phenotips.data.Patient;
import org.phenotips.mendelianSearch.mocks.MockOntologyTerm;
import org.phenotips.ontology.OntologyManager;
import org.phenotips.ontology.OntologyTerm;

import org.xwiki.cache.CacheException;
import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.test.mockito.MockitoComponentMockingRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DefaultPatientPhenotypeScorerTest
{
    @Rule
    public final MockitoComponentMockingRule<DefaultPatientPhenotypeScorer> mocker =
        new MockitoComponentMockingRule<DefaultPatientPhenotypeScorer>(DefaultPatientPhenotypeScorer.class);

    private OntologyManager ontologyManager;

    private PhenotypeScorer scorer;

    private static Map<String, OntologyTerm> testOntologyTerms;

    @SuppressWarnings("unchecked")
    @Before
    public void setup() throws ComponentLookupException
    {
        MockitoAnnotations.initMocks(this);
        this.ontologyManager = this.mocker.getInstance(OntologyManager.class);
        this.scorer = this.mocker.getInstance(PhenotypeScorer.class);

        when(this.scorer.getScore(Matchers.anyList(), Matchers.anyList())).thenReturn(Math.random());

        for (String term : DefaultPatientPhenotypeScorerTest.testOntologyTerms.keySet()) {
            when(this.ontologyManager.resolveTerm(term)).thenReturn(testOntologyTerms.get(term));
        }

    }

    @Test
    public void testNormalPatientList() throws ComponentLookupException
    {
        int numPatients = 10;
        Set<Patient> patientList = new HashSet<Patient>();
        for (int i = 0; i < numPatients; i++) {
            Patient mockPatient = mock(Patient.class);

            Feature mockFeature = mock(Feature.class);
            when(mockFeature.isPresent()).thenReturn(true);
            Feature mockFeature2 = mock(Feature.class);
            when(mockFeature2.isPresent()).thenReturn(false);
            Feature mockFeature3 = mock(Feature.class);
            when(mockFeature3.isPresent()).thenReturn(true);

            // Returns a random term from the testOntologyTerm keySet
            when(mockFeature.getId()).thenReturn(
                (String) testOntologyTerms.keySet().toArray()[(int) (Math.random() * testOntologyTerms.size() - 1)]);
            when(mockFeature3.getId()).thenReturn(null);

            Set<Feature> mockPatientFeatures = new HashSet<Feature>();
            mockPatientFeatures.add(mockFeature);
            mockPatientFeatures.add(mockFeature2);
            mockPatientFeatures.add(mockFeature3);

            Mockito.doReturn(mockPatientFeatures).when(mockPatient).getFeatures();

            patientList.add(mockPatient);
        }
        List<OntologyTerm> phenotype = new ArrayList<OntologyTerm>();
        phenotype.add(testOntologyTerms.get("HP:0001367"));
        phenotype.add(testOntologyTerms.get("HP:0001382"));
        phenotype.add(testOntologyTerms.get("HP:0011729"));

        Map<Patient, Double> result = this.mocker.getComponentUnderTest().getScores(phenotype, patientList);
        assertEquals(numPatients, result.size());
    }

    @BeforeClass
    public static void setupOntology() throws ComponentLookupException, CacheException
    {

        Set<OntologyTerm> ancestors = new HashSet<OntologyTerm>();
        testOntologyTerms = new HashMap<String, OntologyTerm>();

        OntologyTerm all = new MockOntologyTerm("HP:0000001", Collections.<OntologyTerm>emptySet(),
            Collections.<OntologyTerm>emptySet());
        ancestors.add(all);
        testOntologyTerms.put("HP:0000001", all);
        OntologyTerm phenotypes =
            new MockOntologyTerm("HP:0000118", Collections.singleton(all), new HashSet<OntologyTerm>(ancestors));
        ancestors.add(phenotypes);
        testOntologyTerms.put("HP:0000118", phenotypes);
        OntologyTerm abnormalNS =
            new MockOntologyTerm("HP:0000707", Collections.singleton(phenotypes), new HashSet<OntologyTerm>(ancestors));
        ancestors.add(abnormalNS);
        testOntologyTerms.put("HP:0000707", abnormalNS);

        OntologyTerm abnormalCNS =
            new MockOntologyTerm("HP:0002011", Collections.singleton(abnormalNS), new HashSet<OntologyTerm>(ancestors));
        ancestors.add(abnormalCNS);
        testOntologyTerms.put("HP:0002011", abnormalCNS);

        OntologyTerm abnormalHMF =
            new MockOntologyTerm("HP:0011446", Collections.singleton(abnormalCNS), new HashSet<OntologyTerm>(ancestors));
        ancestors.add(abnormalHMF);
        testOntologyTerms.put("HP:0011446", abnormalHMF);

        OntologyTerm cognImp =
            new MockOntologyTerm("HP:0100543", Collections.singleton(abnormalHMF), new HashSet<OntologyTerm>(ancestors));
        ancestors.add(cognImp);
        testOntologyTerms.put("HP:0100543", cognImp);

        OntologyTerm intDis =
            new MockOntologyTerm("HP:0001249", Collections.singleton(cognImp), new HashSet<OntologyTerm>(ancestors));
        ancestors.add(intDis);
        testOntologyTerms.put("HP:0001249", intDis);

        OntologyTerm mildIntDis =
            new MockOntologyTerm("HP:0001256", Collections.singleton(intDis), new HashSet<OntologyTerm>(ancestors));
        ancestors.add(mildIntDis);
        testOntologyTerms.put("HP:0001256", mildIntDis);

        ancestors.clear();
        ancestors.add(all);
        ancestors.add(phenotypes);
        OntologyTerm abnormalSkelS =
            new MockOntologyTerm("HP:0000924", Collections.singleton(phenotypes), new HashSet<OntologyTerm>(ancestors));
        ancestors.add(abnormalSkelS);
        testOntologyTerms.put("HP:0000924", abnormalSkelS);

        OntologyTerm abnormalSkelM =
            new MockOntologyTerm("HP:0011842", Collections.singleton(abnormalSkelS), new HashSet<OntologyTerm>(
                ancestors));
        ancestors.add(abnormalSkelM);
        testOntologyTerms.put("HP:0011842", abnormalSkelM);

        OntologyTerm abnormalJointMorph =
            new MockOntologyTerm("HP:0001367", Collections.singleton(abnormalSkelM), new HashSet<OntologyTerm>(
                ancestors));
        ancestors.add(abnormalJointMorph);
        testOntologyTerms.put("HP:0001367", abnormalJointMorph);

        OntologyTerm abnormalJointMob =
            new MockOntologyTerm("HP:0011729", Collections.singleton(abnormalJointMorph), new HashSet<OntologyTerm>(
                ancestors));
        ancestors.add(abnormalJointMob);
        testOntologyTerms.put("HP:0011729", abnormalJointMob);

        OntologyTerm jointHyperm =
            new MockOntologyTerm("HP:0001382", Collections.singleton(abnormalJointMob), new HashSet<OntologyTerm>(
                ancestors));
        ancestors.add(jointHyperm);
        testOntologyTerms.put("HP:0001382", jointHyperm);

    }
}
