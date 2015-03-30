package org.phenotips.mendelianSearch.phenotype;

import org.phenotips.data.Feature;
import org.phenotips.data.Patient;
import org.phenotips.ontology.OntologyManager;
import org.phenotips.ontology.OntologyService;
import org.phenotips.ontology.OntologyTerm;

import org.xwiki.component.annotation.Component;
import org.xwiki.component.phase.Initializable;
import org.xwiki.component.phase.InitializationException;

import java.util.ArrayList;
import java.util.Collection;
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
public class DefaultPhenotypeScorer implements Initializable, PhenotypeScorer
{
    /** The root of the phenotypic abnormality portion of HPO. */
    private static final String HP_ROOT = "HP:0000118";

    /** Small value used to round things too close to 0 or 1. */
    private static final double EPS = 1e-9;

    /** Pre-computed term information content (-logp), for each node t (i.e. t.inf). */
    private Map<OntologyTerm, Double> termICs;

    @Inject
    private Logger logger;

    /** Provides access to the term ontology. */
    @Inject
    protected OntologyManager ontologyManager;

    @Override
    public void initialize() throws InitializationException
    {
        this.logger.info("Initializing...");

        // Load the OMIM/HPO mappings
        OntologyService mim = this.ontologyManager.getOntology("MIM");
        OntologyService hpo = this.ontologyManager.getOntology("HPO");
        OntologyTerm hpRoot = hpo.getTerm(HP_ROOT);

        // Pre-compute HPO descendant lookups
        Map<OntologyTerm, Collection<OntologyTerm>> termChildren = getChildrenMap(hpo);
        Map<OntologyTerm, Collection<OntologyTerm>> termDescendants = getDescendantsMap(hpRoot, termChildren);

        // Compute prior frequencies of phenotypes (based on disease frequencies and phenotype prevalence)
        Map<OntologyTerm, Double> termFreq = getTermFrequencies(mim, hpo, termDescendants.keySet());

        // Pre-compute term information content (-logp), for each node t (i.e. t.inf).
        Map<OntologyTerm, Double> termICs = getTermICs(termFreq, termDescendants);

        // Give data to views to use
        this.logger.info("Setting view globals...");
        this.termICs = termICs;

        this.logger.info("Initialized.");
    }

    /**
     * Get the phenotypic similarity score for two sets of phenotypes.
     *
     * @param p1 the first set of HPO terms
     * @param p2 the second set of HPO terms
     * @return the similarity score, between 0 (a poor match) and 1 (a good match)
     */
    public double getScore(List<OntologyTerm> p1, List<OntologyTerm> p2)
    {
        if (p1.isEmpty() || p2.isEmpty()) {
            return 0.0;
        } else {
            // Get ancestors for both patients
            Set<OntologyTerm> refAncestors = getAncestors(p1);
            Set<OntologyTerm> matchAncestors = getAncestors(p2);

            if (refAncestors.isEmpty() || matchAncestors.isEmpty()) {
                return 0.0;
            } else {
                // Score overlapping ancestors
                Set<OntologyTerm> commonAncestors = new HashSet<OntologyTerm>();
                commonAncestors.addAll(refAncestors);
                commonAncestors.retainAll(matchAncestors);

                Set<OntologyTerm> allAncestors = new HashSet<OntologyTerm>();
                allAncestors.addAll(refAncestors);
                allAncestors.addAll(matchAncestors);

                return getTermICs(commonAncestors) / getTermICs(allAncestors);
            }
        }
    }

    /**
     * Return the total IC across a collection of terms.
     *
     * @param terms (should include implied ancestors) that are present in the patient
     * @return the total IC for all the terms
     */
    private double getTermICs(Collection<OntologyTerm> terms)
    {
        double cost = 0;
        for (OntologyTerm term : terms) {
            Double ic = this.termICs.get(term);
            if (ic == null) {
                ic = 0.0;
            }
            cost += ic;
        }
        return cost;
    }

    /**
     * Return a (potentially empty) collection of terms present in the patient.
     *
     * @param patient
     * @return a collection of terms present in the patient
     */
    private Collection<OntologyTerm> getPresentPatientTerms(Patient patient)
    {
        Set<OntologyTerm> terms = new HashSet<OntologyTerm>();
        for (Feature feature : patient.getFeatures()) {
            if (!feature.isPresent()) {
                continue;
            }

            OntologyTerm term = this.ontologyManager.resolveTerm(feature.getId());
            if (term != null) {
                // Only add resolvable terms
                terms.add(term);
            }
        }
        return terms;
    }

    /**
     * Return the set of terms implied by a collection of features in the ontology.
     *
     * @param terms a collection of terms
     * @return all provided OntologyTerm terms and their ancestors
     */
    private Set<OntologyTerm> getAncestors(Collection<OntologyTerm> terms)
    {
        Set<OntologyTerm> ancestors = new HashSet<OntologyTerm>(terms);
        for (OntologyTerm term : terms) {
            // Add all ancestors
            ancestors.addAll(term.getAncestorsAndSelf());
        }
        return ancestors;
    }

    /**
     * Return a mapping from OntologyTerms to their children in the given ontology.
     *
     * @param ontology the ontology
     * @return a map from each term to the children in ontology
     */
    private Map<OntologyTerm, Collection<OntologyTerm>> getChildrenMap(OntologyService ontology)
    {
        Map<OntologyTerm, Collection<OntologyTerm>> children = new HashMap<OntologyTerm, Collection<OntologyTerm>>();
        this.logger.info("Getting all children of ontology terms...");
        Collection<OntologyTerm> terms = queryAllTerms(ontology);
        for (OntologyTerm term : terms) {
            for (OntologyTerm parent : term.getParents()) {
                // Add term to parent's set of children
                Collection<OntologyTerm> parentChildren = children.get(parent);
                if (parentChildren == null) {
                    parentChildren = new ArrayList<OntologyTerm>();
                    children.put(parent, parentChildren);
                }
                parentChildren.add(term);
            }
        }
        this.logger.info(String.format("cached children of %d ontology terms.", children.size()));
        return children;
    }

    /**
     * Return all terms in the ontology.
     *
     * @param ontology the ontology to query
     * @return a Collection of all OntologyTerms in the ontology
     */
    private Collection<OntologyTerm> queryAllTerms(OntologyService ontology)
    {
        this.logger.info("Querying all terms in ontology: " + ontology.getAliases().iterator().next());
        Map<String, String> queryAll = new HashMap<String, String>();
        queryAll.put("id", "*");
        Map<String, String> queryAllParams = new HashMap<String, String>();
        queryAllParams.put(CommonParams.ROWS, String.valueOf(ontology.size()));
        Collection<OntologyTerm> results = ontology.search(queryAll, queryAllParams);
        this.logger.info(String.format("  ... found %d entries.", results.size()));
        return results;
    }

    /**
     * Return a mapping from OntologyTerms to their descendants in the part of the ontology under root.
     *
     * @param root the root of the ontology to explore
     * @param termChildren a map from each ontology term to its children
     * @return a map from each term to the descendants in ontology
     */
    private Map<OntologyTerm, Collection<OntologyTerm>> getDescendantsMap(OntologyTerm root,
        Map<OntologyTerm, Collection<OntologyTerm>> termChildren)
    {
        Map<OntologyTerm, Collection<OntologyTerm>> termDescendants =
            new HashMap<OntologyTerm, Collection<OntologyTerm>>();
        setDescendantsMap(root, termChildren, termDescendants);
        return termDescendants;
    }

    /**
     * Helper method to recursively fill a map with the descendants of all terms under a root term.
     *
     * @param root the root of the ontology to explore
     * @param termChildren a map from each ontology term to its children
     * @param termDescendants a partially-complete map of terms to descendants, filled in by this method
     */
    private void setDescendantsMap(OntologyTerm root, Map<OntologyTerm, Collection<OntologyTerm>> termChildren,
        Map<OntologyTerm, Collection<OntologyTerm>> termDescendants)
    {
        if (termDescendants.containsKey(root)) {
            return;
        }
        // Compute descendants from children
        Collection<OntologyTerm> descendants = new HashSet<OntologyTerm>();
        Collection<OntologyTerm> children = termChildren.get(root);
        if (children != null) {
            for (OntologyTerm child : children) {
                // Recurse on child
                setDescendantsMap(child, termChildren, termDescendants);
                // On return, termDescendants[child] should be non-null
                Collection<OntologyTerm> childDescendants = termDescendants.get(child);
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
     * @param mim the MIM ontology with diseases and symptom frequencies
     * @param hpo the human phenotype ontology
     * @param allowedTerms only frequencies for a subset of these terms will be returned
     * @return a map from OntologyTerm to the absolute frequency (sum over all terms ~1)
     */
    @SuppressWarnings("unchecked")
    private Map<OntologyTerm, Double> getTermFrequencies(OntologyService mim, OntologyService hpo,
        Collection<OntologyTerm> allowedTerms)
        {
        Map<OntologyTerm, Double> termFreq = new HashMap<OntologyTerm, Double>();
        double freqDenom = 0.0;
        // Add up frequencies of each term across diseases
        Collection<OntologyTerm> diseases = queryAllTerms(mim);
        Set<OntologyTerm> ignoredSymptoms = new HashSet<OntologyTerm>();
        for (OntologyTerm disease : diseases) {
            // Get a Collection<String> of symptom HP IDs, or null
            Object symptomNames = disease.get("actual_symptom");
            if (symptomNames != null) {
                if (symptomNames instanceof Collection<?>) {
                    for (String symptomName : ((Collection<String>) symptomNames)) {
                        OntologyTerm symptom = hpo.getTerm(symptomName);
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
        for (Map.Entry<OntologyTerm, Double> entry : termFreq.entrySet()) {
            entry.setValue(limitProb(entry.getValue() / freqDenom));
        }

        return termFreq;
        }

    /**
     * Bound probability to between (0, 1) exclusive.
     *
     * @param prob
     * @return probability bounded between (0, 1) exclusive
     */
    private static double limitProb(double prob)
    {
        return Math.min(Math.max(prob, EPS), 1 - EPS);
    }

    /**
     * Return the information content of each OntologyTerm in termFreq.
     *
     * @param termFreq the absolute frequency of each OntologyTerm
     * @param termDescendants the descendants of each OntologyTerm
     * @return a map from each term to the information content of that term
     */
    private Map<OntologyTerm, Double> getTermICs(Map<OntologyTerm, Double> termFreq,
        Map<OntologyTerm, Collection<OntologyTerm>> termDescendants)
    {
        Map<OntologyTerm, Double> termICs = new HashMap<OntologyTerm, Double>();

        for (OntologyTerm term : termFreq.keySet()) {
            Collection<OntologyTerm> descendants = termDescendants.get(term);
            if (descendants == null) {
                this.logger.warn("Found no descendants of term: " + term.getId());
            }
            // Sum up frequencies of all descendants
            double probMass = 0.0;
            for (OntologyTerm descendant : descendants) {
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
                termICs.put(term, -Math.log(probMass));
            }
        }
        return termICs;
    }
}
