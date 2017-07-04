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

import org.phenotips.vocabulary.Vocabulary;
import org.phenotips.vocabulary.VocabularyManager;
import org.phenotips.vocabulary.VocabularyTerm;

import org.xwiki.component.annotation.Component;
import org.xwiki.component.phase.Initializable;
import org.xwiki.component.phase.InitializationException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.solr.common.params.CommonParams;
import org.slf4j.Logger;

/**
 * Restates the getPhenotypeScore method used in the DefaultPatientSimilarityView class of patient-network. All other
 * methods have been copied from either DefaultPatientSimilarityView or DefaultPatientSimilarityViewFactory
 *
 * @version $Id$
 */
@Component
@Singleton
public class DefaultPhenotypeScorer implements PhenotypeScorer, Initializable
{
    /** The root of the phenotypic abnormality portion of HPO. */
    private static final String HP_ROOT = "HP:0000118";

    /** Small value used to round things too close to 0 or 1. */
    private static final double EPS = 1e-9;

    /** A string variable for "id".*/
    private static final String ID_STRING = "id";

    /** Pre-computed term information content (-logp), for each node t (i.e. t.inf). */
    private Map<VocabularyTerm, Double> termICs;

    @Inject
    private Logger logger;

    /** Provides access to the term vocabulary. */
    @Inject
    private VocabularyManager vocabularyManager;

    @Override
    public void initialize() throws InitializationException
    {
        this.logger.info("Initializing...");

        // Load the OMIM/HPO mappings
        Vocabulary mim = this.vocabularyManager.getVocabulary("MIM");
        Vocabulary hpo = this.vocabularyManager.getVocabulary("HPO");
        VocabularyTerm hpRoot = hpo.getTerm(HP_ROOT);

        // Pre-compute HPO descendant lookups
        Map<VocabularyTerm, Collection<VocabularyTerm>> termChildren = getChildrenMap(hpo);
        Map<VocabularyTerm, Collection<VocabularyTerm>> termDescendants = getDescendantsMap(hpRoot, termChildren);

        // Compute prior frequencies of phenotypes (based on disease frequencies and phenotype prevalence)
        Map<VocabularyTerm, Double> termFreq = getTermFrequencies(mim, hpo, termDescendants.keySet());

        // Pre-compute term information content (-logp), for each node t (i.e. t.inf).
        this.termICs = findTermICs(termFreq, termDescendants);

        this.logger.info("Initialized.");
    }

    @Override
    public double getScore(List<VocabularyTerm> p1, List<VocabularyTerm> p2)
    {
        return this.getScore(p1, p2, true);
    }

    @Override
    public double getScoreAgainstReference(List<VocabularyTerm> query, List<VocabularyTerm> reference)
    {
        return this.getScore(query, reference, false);
    }

    @Override
    public List<Map<String, Object>> getDetailedMatches(List<VocabularyTerm> q, List<VocabularyTerm> m)
    {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        double maxIC;

        //This is a far from optimal greedy approach to creating matches.
        // Each trait (qt) from q is matched with its best match in m (mt). mt is then removed from m.
        // So although mt may be the best match for qt, qt may not be the best match for mt.
        for (VocabularyTerm t : q) {
            if (m.isEmpty()) {
                break;
            }
            maxIC = 0;
            VocabularyTerm bestMatch = null;
            VocabularyTerm lcs = null;
            for (VocabularyTerm tPrime : m) {
                VocabularyTerm tempLcs = this.findBestCommonAncestor(t, tPrime);
                if (tempLcs != null && this.termICs.get(tempLcs) > maxIC) {
                    lcs = tempLcs;
                    maxIC = this.termICs.get(tempLcs);
                    bestMatch = tPrime;
                }
            }
            if (bestMatch != null && lcs != null) {
                m.remove(bestMatch);
                Map<String, Object> matchView = this.createMatchView(t, bestMatch, lcs);
                result.add(matchView);
            }
        }
        return result;
    }

    private Map<String, Object> createMatchView(VocabularyTerm a, VocabularyTerm b, VocabularyTerm lcs)
    {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("a", this.getTermData(a));
        result.put("b", this.getTermData(b));
        result.put("lcs", this.getTermData(lcs));
        return result;
    }

    /**
     * Get the phenotypic similarity score for two sets of phenotypes.
     * The value of symmetric determines the method in which the similarity is calculated:
     * <ul>
     *      <li>{@code true} Match and ref are treated equally.
     *          The IC of the intersection is divided by the IC of the union.</li>
     *      <li>{@code false} Match is compared to ref.
     *          The IC of the intersection is divided by the IC of the the reference.</li>
     * </ul>
     * @param match the first set of HPO terms Used as the match list
     * @param ref the second set of HPO terms. Used as the reference list.
     * @param symmetric Defines the denominator for the score calculation.
     *
     * @return the similarity score, between 0 (a poor match) and 1 (a good match)
     */
    private Double getScore(List<VocabularyTerm> match, List<VocabularyTerm> ref, boolean symmetric)
    {
        if (match.isEmpty() || ref.isEmpty()) {
            return 0.0;
        } else {
            // Get ancestors for both patients
            Set<VocabularyTerm> refAncestors = getAncestors(ref);
            Set<VocabularyTerm> matchAncestors = getAncestors(match);

            if (refAncestors.isEmpty() || matchAncestors.isEmpty()) {
                return 0.0;
            } else {
                // Score overlapping ancestors
                Set<VocabularyTerm> commonAncestors = new HashSet<VocabularyTerm>();
                commonAncestors.addAll(refAncestors);
                commonAncestors.retainAll(matchAncestors);

                Set<VocabularyTerm> allAncestors = new HashSet<VocabularyTerm>();
                allAncestors.addAll(refAncestors);
                allAncestors.addAll(matchAncestors);
                Double denominator = symmetric ? getTermICs(allAncestors) : getTermICs(refAncestors);

                return getTermICs(commonAncestors) / denominator;
            }
        }
    }


    private Map<String, Object> getTermData(VocabularyTerm term)
    {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put(ID_STRING, term.getId());
        result.put("IC", this.termICs.get(term));
        result.put("label", term.getName());
        return result;
    }

    private VocabularyTerm findBestCommonAncestor(VocabularyTerm t, VocabularyTerm tPrime)
    {

        Set<VocabularyTerm> allAncestors = getAncestors(Collections.singletonList(t));
        Set<VocabularyTerm> tPrimeAncestors = getAncestors(Collections.singletonList(tPrime));

        allAncestors.retainAll(tPrimeAncestors);
        if (allAncestors.isEmpty()) {
            return null;
        }

        double maxTermIC = 0;
        VocabularyTerm bestCommonAncestor = null;
        for (VocabularyTerm ancestor : allAncestors) {
            Double ic = this.termICs.get(ancestor);
            if (ic != null && ic > maxTermIC) {
                maxTermIC = this.termICs.get(ancestor);
                bestCommonAncestor = ancestor;
            }
        }
        return bestCommonAncestor;
    }

    /**
     * Return the total IC across a collection of terms.
     *
     * @param terms (should include implied ancestors) that are present in the patient
     * @return the total IC for all the terms
     */
    private double getTermICs(Collection<VocabularyTerm> terms)
    {
        double cost = 0;
        for (VocabularyTerm term : terms) {
            Double ic = this.termICs.get(term);
            if (ic == null) {
                ic = 0.0;
            }
            cost += ic;
        }
        return cost;
    }

    /**
     * Return the set of terms implied by a collection of features in the vocabulary.
     *
     * @param terms a collection of terms
     * @return all provided VocabularyTerm terms and their ancestors
     */
    private Set<VocabularyTerm> getAncestors(Collection<VocabularyTerm> terms)
    {
        Set<VocabularyTerm> ancestors = new HashSet<VocabularyTerm>();
        for (VocabularyTerm term : terms) {
            // Add all ancestors
            if (term != null) {
                ancestors.addAll(term.getAncestorsAndSelf());
            }
        }
        return ancestors;
    }

    /**
     * Return a mapping from VocabularyTerms to their children in the given vocabulary.
     *
     * @param vocabulary the vocabulary
     * @return a map from each term to the children in vocabulary
     */
    private Map<VocabularyTerm, Collection<VocabularyTerm>> getChildrenMap(Vocabulary vocabulary)
    {
        Map<VocabularyTerm, Collection<VocabularyTerm>> children =
            new HashMap<VocabularyTerm, Collection<VocabularyTerm>>();
        this.logger.info("Getting all children of vocabulary terms...");
        Collection<VocabularyTerm> terms = queryAllTerms(vocabulary);
        for (VocabularyTerm term : terms) {
            for (VocabularyTerm parent : term.getParents()) {
                // Add term to parent's set of children
                Collection<VocabularyTerm> parentChildren = children.get(parent);
                if (parentChildren == null) {
                    parentChildren = new ArrayList<VocabularyTerm>();
                    children.put(parent, parentChildren);
                }
                parentChildren.add(term);
            }
        }
        this.logger.info(String.format("cached children of %d vocabulary terms.", children.size()));
        return children;
    }

    /**
     * Return all terms in the vocabulary.
     *
     * @param vocabulary the vocabulary to query
     * @return a Collection of all VocabularyTerms in the vocabulary
     */
    private Collection<VocabularyTerm> queryAllTerms(Vocabulary vocabulary)
    {
        Map<String, String> queryAll = new HashMap<String, String>();
        queryAll.put(ID_STRING, "*");
        Map<String, String> queryAllParams = new HashMap<String, String>();
        queryAllParams.put(CommonParams.ROWS, String.valueOf(vocabulary.size()));
        Collection<VocabularyTerm> results = vocabulary.search(queryAll, queryAllParams);
        this.logger.info(String.format("  ... found %d entries.", results.size()));
        return results;
    }

    /**
     * Return a mapping from VocabularyTerms to their descendants in the part of the vocabulary under root.
     *
     * @param root the root of the vocabulary to explore
     * @param termChildren a map from each vocabulary term to its children
     * @return a map from each term to the descendants in vocabulary
     */
    private Map<VocabularyTerm, Collection<VocabularyTerm>> getDescendantsMap(VocabularyTerm root,
        Map<VocabularyTerm, Collection<VocabularyTerm>> termChildren)
    {
        Map<VocabularyTerm, Collection<VocabularyTerm>> termDescendants =
            new HashMap<VocabularyTerm, Collection<VocabularyTerm>>();
        setDescendantsMap(root, termChildren, termDescendants);
        return termDescendants;
    }

    /**
     * Helper method to recursively fill a map with the descendants of all terms under a root term.
     *
     * @param root the root of the vocabulary to explore
     * @param termChildren a map from each vocabulary term to its children
     * @param termDescendants a partially-complete map of terms to descendants, filled in by this method
     */
    private void setDescendantsMap(VocabularyTerm root, Map<VocabularyTerm, Collection<VocabularyTerm>> termChildren,
        Map<VocabularyTerm, Collection<VocabularyTerm>> termDescendants)
    {
        if (termDescendants.containsKey(root)) {
            return;
        }
        // Compute descendants from children
        Collection<VocabularyTerm> descendants = new HashSet<VocabularyTerm>();
        Collection<VocabularyTerm> children = termChildren.get(root);
        if (children != null) {
            for (VocabularyTerm child : children) {
                // Recurse on child
                setDescendantsMap(child, termChildren, termDescendants);
                // On return, termDescendants[child] should be non-null
                Collection<VocabularyTerm> childDescendants = termDescendants.get(child);
                if (childDescendants != null) {
                    descendants.addAll(childDescendants);
                } else {
                    this.logger.warn("Descendants were null after recursion");
                }
            }
        }
        descendants.add(root);
        termDescendants.put(root, descendants);
    }

    /**
     * Return the observed frequency distribution across provided HPO terms seen in MIM.
     *
     * @param mim the MIM vocabulary with diseases and symptom frequencies
     * @param hpo the human phenotype vocabulary
     * @param allowedTerms only frequencies for a subset of these terms will be returned
     * @return a map from VocabularyTerm to the absolute frequency (sum over all terms ~1)
     */
    @SuppressWarnings("unchecked")
    private Map<VocabularyTerm, Double> getTermFrequencies(Vocabulary mim, Vocabulary hpo,
        Collection<VocabularyTerm> allowedTerms)
    {
        Map<VocabularyTerm, Double> termFreq = new HashMap<VocabularyTerm, Double>();
        double freqDenom = 0.0;
        // Add up frequencies of each term across diseases
        Collection<VocabularyTerm> diseases = queryAllTerms(mim);
        Set<VocabularyTerm> ignoredSymptoms = new HashSet<VocabularyTerm>();
        for (VocabularyTerm disease : diseases) {
            // Get a Collection<String> of symptom HP IDs, or null
            Object symptomNames = disease.get("actual_symptom");
            if (symptomNames != null) {
                if (symptomNames instanceof Collection<?>) {
                    for (String symptomName : ((Collection<String>) symptomNames)) {
                        VocabularyTerm symptom = hpo.getTerm(symptomName);
                        if (!allowedTerms.contains(symptom)) {
                            ignoredSymptoms.add(symptom);
                            continue;
                        }
                        // Ideally use frequency with which symptom occurs in disease
                        // This information isn't prevalent or reliable yet, however
                        double freq = 1.0;
                        freqDenom += freq;
                        // Add to accumulated term frequency
                        Double prevFreq = termFreq.get(symptom);
                        if (prevFreq != null) {
                            freq += prevFreq;
                        }
                        termFreq.put(symptom, freq);
                    }
                } else {
                    String err = "Solr returned non-collection symptoms: " + String.valueOf(symptomNames);
                    this.logger.error(err);
                    throw new RuntimeException(err);
                }
            }
        }
        if (!ignoredSymptoms.isEmpty()) {
            this.logger.warn(String.format("Ignored %d symptoms", ignoredSymptoms.size()));
        }

        this.logger.info("Normalizing term frequency distribution...");
        // Normalize all the term frequencies to be a proper distribution
        for (Map.Entry<VocabularyTerm, Double> entry : termFreq.entrySet()) {
            entry.setValue(limitProb(entry.getValue() / freqDenom));
        }

        return termFreq;
    }

    /**
     * Bound probability to between (0, 1) exclusive.
     *
     * @param prob probability to bound
     * @return probability bounded between (0, 1) exclusive
     */
    private static double limitProb(double prob)
    {
        return Math.min(Math.max(prob, EPS), 1 - EPS);
    }

    /**
     * Return the information content of each VocabularyTerm in termFreq.
     *
     * @param termFreq the absolute frequency of each VocabularyTerm
     * @param termDescendants the descendants of each VocabularyTerm
     * @return a map from each term to the information content of that term
     */
    private Map<VocabularyTerm, Double> findTermICs(Map<VocabularyTerm, Double> termFreq,
        Map<VocabularyTerm, Collection<VocabularyTerm>> termDescendants)
    {
        Map<VocabularyTerm, Double> returnTermICs = new HashMap<VocabularyTerm, Double>();

        for (VocabularyTerm term : termFreq.keySet()) {
            Collection<VocabularyTerm> descendants = termDescendants.get(term);
            if (descendants == null) {
                this.logger.warn("Found no descendants of term: " + term.getId());
            }
            // Sum up frequencies of all descendants
            double probMass = 0.0;
            for (VocabularyTerm descendant : descendants) {
                Double freq = termFreq.get(descendant);
                if (freq != null) {
                    probMass += freq;
                }
            }

            if (HP_ROOT.equals(term.getId())) {
                this.logger.warn(String
                    .format("Probability mass under %s should be 1.0, was: %.6f", HP_ROOT, probMass));
            }
            if (probMass > EPS) {
                probMass = limitProb(probMass);
                returnTermICs.put(term, -Math.log(probMass));
            }
        }
        return returnTermICs;
    }
}
