package org.phenotips.mendelianSearch.phenotype;

import org.phenotips.data.Patient;
import org.phenotips.ontology.OntologyTerm;

import org.xwiki.component.annotation.Role;

import java.util.List;
import java.util.Map;

/**
 * Provides patient centered methods for computing phenotype scores
 *
 * @version $Id$
 */

@Role
public interface PatientPhenotypeScorer
{

    /**
     * Computes scores between 0 and 1 for each patient in the input.
     *
     * @param phenotype The phenotype against which to compute
     * @param patients The patients to compute scores for
     * @return A map of the inputed patients and their scores
     */
    Map<Patient, Double> getScores(List<OntologyTerm> phenotype, List<Patient> patients);
}
