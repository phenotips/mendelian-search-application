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

import org.phenotips.mendelianSearch.mocks.MockHPO;
import org.phenotips.mendelianSearch.mocks.MockMIM;
import org.phenotips.vocabulary.Vocabulary;
import org.phenotips.vocabulary.VocabularyManager;
import org.phenotips.vocabulary.VocabularyTerm;

import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.test.mockito.MockitoComponentMockingRule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;

/**
 * Tests for the "default" {@link PhenotypeScorer} implementation, {@link DefaultPhenotypeScorer}.
 *
 * @version $Id$
 */
public class DefaultPhenotypeScorerTest
{
    @Rule
    public final MockitoComponentMockingRule<DefaultPhenotypeScorer> mocker =
        new MockitoComponentMockingRule<DefaultPhenotypeScorer>(DefaultPhenotypeScorer.class);

    private Vocabulary mim;

    private Vocabulary hpo;

    private VocabularyManager vocabularyManager;

    private static Map<String, VocabularyTerm> h = new HashMap<String, VocabularyTerm>();

    private static final double EPS = 1e-9;

    @Before
    public void setup() throws ComponentLookupException
    {
        // Setup Ontologies
        this.mim = new MockMIM();
        this.hpo = new MockHPO();

        MockitoAnnotations.initMocks(this);
        this.vocabularyManager = this.mocker.getInstance(VocabularyManager.class);

        for (String id : this.hpo.getAliases()) {
            Mockito.doReturn(this.hpo.getTerm(id)).when(this.vocabularyManager).resolveTerm(id);
        }

        Mockito.doReturn(this.mim).when(this.vocabularyManager).getVocabulary("MIM");
        Mockito.doReturn(this.hpo).when(this.vocabularyManager).getVocabulary("HPO");

    }

    @Test
    public void testIdenticalPhenotypes() throws ComponentLookupException
    {
        List<VocabularyTerm> phenotype = new ArrayList<>();
        phenotype.add(this.hpo.getTerm("HP:0100543"));
        phenotype.add(this.hpo.getTerm("HP:0011842"));
        phenotype.add(this.hpo.getTerm("HP:0001382"));

        double target = 1.0;
        assertEquals("Two identical phenotypes should return 1", target,
            this.mocker.getComponentUnderTest().getScore(phenotype, phenotype), EPS);
    }

    @Test
    public void testEmptyPhenotypes() throws ComponentLookupException
    {
        List<VocabularyTerm> p1 = new ArrayList<>();
        List<VocabularyTerm> p2 = new ArrayList<>();
        p1.add(this.hpo.getTerm("HP:0100543"));
        p1.add(this.hpo.getTerm("HP:0011842"));
        p1.add(this.hpo.getTerm("HP:0001382"));

        double target = 0.0;
        assertEquals("Empty phenotype", target,
            this.mocker.getComponentUnderTest().getScore(p1, p2), EPS);
        assertEquals("Empty phenotype", target,
            this.mocker.getComponentUnderTest().getScore(p2, p1), EPS);
        assertEquals("Empty phenotype", target,
            this.mocker.getComponentUnderTest().getScore(p2, p2), EPS);
    }

    @Test
    public void testNormalPhenotypeMatch()
    {
    }

    @Test
    public void testNullVocabularyTerms() throws ComponentLookupException
    {
        List<VocabularyTerm> p1 = new ArrayList<>();
        List<VocabularyTerm> p2 = new ArrayList<>();
        p1.add(null);
        p2.add(this.hpo.getTerm("HP:0100543"));

        double target = 0.0;
        assertEquals("Empty phenotype", target,
            this.mocker.getComponentUnderTest().getScore(p1, p2), EPS);
        assertEquals("Empty phenotype", target,
            this.mocker.getComponentUnderTest().getScore(p2, p1), EPS);
    }

}
