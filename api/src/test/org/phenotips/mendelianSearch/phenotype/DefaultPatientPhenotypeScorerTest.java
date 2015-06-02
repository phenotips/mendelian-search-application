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
package org.phenotips.mendelianSearch.phenotype;

import org.phenotips.data.Feature;
import org.phenotips.data.Patient;
import org.phenotips.mendelianSearch.mocks.MockVocabularyTerm;
import org.phenotips.vocabulary.VocabularyManager;
import org.phenotips.vocabulary.VocabularyTerm;

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

/**
 * Default implementation of {@link PatientPhenotypeScorer}.
 * @version $Id$
 */
public class DefaultPatientPhenotypeScorerTest
{

    private static Map<String, VocabularyTerm> testVocabularyTerms;

    /**
     * The component under test.
     */
    @Rule
    public final MockitoComponentMockingRule<DefaultPatientPhenotypeScorer> mocker =
        new MockitoComponentMockingRule<DefaultPatientPhenotypeScorer>(DefaultPatientPhenotypeScorer.class);

    //Reference to the VocabularyManager used by the component.
    private VocabularyManager vocabularyManager;

    //Reference to the PhenotypeScorer used by the component.
    private PhenotypeScorer scorer;

    /**
     * Before each test, mock the scorer and vocabulary manager methods.
     * @throws ComponentLookupException If the test component cannot be found
     */
    @SuppressWarnings("unchecked")
    @Before
    public void setup() throws ComponentLookupException
    {
        MockitoAnnotations.initMocks(this);
        this.vocabularyManager = this.mocker.getInstance(VocabularyManager.class);
        this.scorer = this.mocker.getInstance(PhenotypeScorer.class);

        when(this.scorer.getScore(Matchers.anyList(), Matchers.anyList())).thenReturn(Math.random());

        for (String term : DefaultPatientPhenotypeScorerTest.testVocabularyTerms.keySet()) {
            when(this.vocabularyManager.resolveTerm(term)).thenReturn(testVocabularyTerms.get(term));
        }

    }

    /**
     *  Tests for normal behaviour on a list of three patients with varying present features
     */
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

            // Returns a random term from the testVocabularyTerm keySet
            when(mockFeature.getId()).thenReturn(
                (String) testVocabularyTerms.keySet().toArray()[(int) (Math.random() * testVocabularyTerms.size() - 1)]);
            when(mockFeature3.getId()).thenReturn(null);

            Set<Feature> mockPatientFeatures = new HashSet<Feature>();
            mockPatientFeatures.add(mockFeature);
            mockPatientFeatures.add(mockFeature2);
            mockPatientFeatures.add(mockFeature3);

            Mockito.doReturn(mockPatientFeatures).when(mockPatient).getFeatures();

            patientList.add(mockPatient);
        }
        List<VocabularyTerm> phenotype = new ArrayList<>();
        phenotype.add(testVocabularyTerms.get("HP:0001367"));
        phenotype.add(testVocabularyTerms.get("HP:0001382"));
        phenotype.add(testVocabularyTerms.get("HP:0011729"));

        Map<Patient, Double> result = this.mocker.getComponentUnderTest().getScores(phenotype, patientList);
        assertEquals(numPatients, result.size());
    }

    /**
     * Sets up a mocked HPO vocabulary for use in tests
     */
    @BeforeClass
    public static void setupOntology() throws ComponentLookupException, CacheException
    {

        Set<VocabularyTerm> ancestors = new HashSet<VocabularyTerm>();
        testVocabularyTerms = new HashMap<String, VocabularyTerm>();

        VocabularyTerm all = new MockVocabularyTerm("HP:0000001", Collections.<VocabularyTerm>emptySet(),
            Collections.<VocabularyTerm>emptySet());
        ancestors.add(all);
        testVocabularyTerms.put("HP:0000001", all);
        VocabularyTerm phenotypes =
            new MockVocabularyTerm("HP:0000118", Collections.singleton(all), new HashSet<VocabularyTerm>(ancestors));
        ancestors.add(phenotypes);
        testVocabularyTerms.put("HP:0000118", phenotypes);
        VocabularyTerm abnormalNS =
            new MockVocabularyTerm("HP:0000707", Collections.singleton(phenotypes), new HashSet<VocabularyTerm>(ancestors));
        ancestors.add(abnormalNS);
        testVocabularyTerms.put("HP:0000707", abnormalNS);

        VocabularyTerm abnormalCNS =
            new MockVocabularyTerm("HP:0002011", Collections.singleton(abnormalNS), new HashSet<VocabularyTerm>(ancestors));
        ancestors.add(abnormalCNS);
        testVocabularyTerms.put("HP:0002011", abnormalCNS);

        VocabularyTerm abnormalHMF =
            new MockVocabularyTerm("HP:0011446", Collections.singleton(abnormalCNS), new HashSet<VocabularyTerm>(ancestors));
        ancestors.add(abnormalHMF);
        testVocabularyTerms.put("HP:0011446", abnormalHMF);

        VocabularyTerm cognImp =
            new MockVocabularyTerm("HP:0100543", Collections.singleton(abnormalHMF), new HashSet<VocabularyTerm>(ancestors));
        ancestors.add(cognImp);
        testVocabularyTerms.put("HP:0100543", cognImp);

        VocabularyTerm intDis =
            new MockVocabularyTerm("HP:0001249", Collections.singleton(cognImp), new HashSet<VocabularyTerm>(ancestors));
        ancestors.add(intDis);
        testVocabularyTerms.put("HP:0001249", intDis);

        VocabularyTerm mildIntDis =
            new MockVocabularyTerm("HP:0001256", Collections.singleton(intDis), new HashSet<VocabularyTerm>(ancestors));
        ancestors.add(mildIntDis);
        testVocabularyTerms.put("HP:0001256", mildIntDis);

        ancestors.clear();
        ancestors.add(all);
        ancestors.add(phenotypes);
        VocabularyTerm abnormalSkelS =
            new MockVocabularyTerm("HP:0000924", Collections.singleton(phenotypes), new HashSet<VocabularyTerm>(ancestors));
        ancestors.add(abnormalSkelS);
        testVocabularyTerms.put("HP:0000924", abnormalSkelS);

        VocabularyTerm abnormalSkelM =
            new MockVocabularyTerm("HP:0011842", Collections.singleton(abnormalSkelS), new HashSet<VocabularyTerm>(
                ancestors));
        ancestors.add(abnormalSkelM);
        testVocabularyTerms.put("HP:0011842", abnormalSkelM);

        VocabularyTerm abnormalJointMorph =
            new MockVocabularyTerm("HP:0001367", Collections.singleton(abnormalSkelM), new HashSet<VocabularyTerm>(
                ancestors));
        ancestors.add(abnormalJointMorph);
        testVocabularyTerms.put("HP:0001367", abnormalJointMorph);

        VocabularyTerm abnormalJointMob =
            new MockVocabularyTerm("HP:0011729", Collections.singleton(abnormalJointMorph), new HashSet<VocabularyTerm>(
                ancestors));
        ancestors.add(abnormalJointMob);
        testVocabularyTerms.put("HP:0011729", abnormalJointMob);

        VocabularyTerm jointHyperm =
            new MockVocabularyTerm("HP:0001382", Collections.singleton(abnormalJointMob), new HashSet<VocabularyTerm>(
                ancestors));
        ancestors.add(jointHyperm);
        testVocabularyTerms.put("HP:0001382", jointHyperm);

    }
}
