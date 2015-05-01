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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.phenotips.mendelianSearch.phenotype;

import org.phenotips.ontology.OntologyTerm;

import org.xwiki.component.annotation.Role;

import java.util.List;

/**
 * Tool used to compute a similarity score between 0 and 1 for two sets of phenotypes. A phenotypeScorer does only is
 * only aware of ontology terms and does not have a concept of a patient or access level.
 *
 * @version $Id$
 */
@Role
public interface PhenotypeScorer
{
    /**
     * Get the phenotypic similarity score for two sets of phenotypes.
     *
     * @param p1 the first set of HPO terms
     * @param p2 the second set of HPO terms
     * @return the similarity score, between 0 (a poor match) and 1 (a good match)
     */
    double getScore(List<OntologyTerm> p1, List<OntologyTerm> p2);
}
