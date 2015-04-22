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

import org.phenotips.data.Patient;
import org.phenotips.ontology.OntologyTerm;

import org.xwiki.component.annotation.Role;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Provides patient oriented methods for computing phenotype scores. Relies heavily on a PhenotypeScorer.
 *
 * @version $Id$
 */

@Role
public interface PatientPhenotypeScorer
{

    /**
     * Computes scores between 0 and 1 for each patient in the patients list. Scores are computed against the phenotype
     * provided in the arguments.
     *
     * @param phenotype The phenotype against which to compute
     * @param patients The patients to compute scores for
     * @return A map with patients as keys and double scores as values
     */
    Map<Patient, Double> getScores(List<OntologyTerm> phenotype, Set<Patient> patients);

    /**
     * Computes scores between 0 and 1 for each patient in the id set. Scores are computed against the phenotype
     * provided in the arguments.
     *
     * @param phenotype
     * @param ids
     * @return A map with patient ids as keys and double scores as values
     */
    Map<String, Double> getScoresById(List<OntologyTerm> phenotype, Set<String> ids);

}
