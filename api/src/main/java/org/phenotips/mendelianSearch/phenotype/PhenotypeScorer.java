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

import org.phenotips.vocabulary.VocabularyTerm;

import org.xwiki.component.annotation.Role;

import java.util.List;
import java.util.Map;

/**
 * Tool used to compute a similarity score between 0 and 1 for two sets of phenotypes. A phenotypeScorer does only is
 * only aware of vocabulary terms and does not have a concept of a patient or access level.
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
    double getScore(List<VocabularyTerm> p1, List<VocabularyTerm> p2);

    /**
     * Gets the phenotypic similarity score asymmetrically.
     * This differs from the getScore method by asking, "How well does the query match with the reference?"
     * @param query the first set of HPO terms
     * @param reference the second set of HPO terms
     * @return the similarity score, between 0 (a poor match) and 1 (a good match)
     */
    double getScoreAgainstReference(List<VocabularyTerm> query, List<VocabularyTerm> reference);

    /** Returns a map of detailed matches between the terms in q and m.
     *  Matches are stored in Maps with the following structure:
     *  {
     *      a:{
     *          id:,
     *          IC:,
     *          label:,
     *      },
     *      b:{
     *          id:,
     *          IC:,
     *          label:,
     *      },
     *      lcs:{
     *          id:,
     *          IC:,
     *          label:,
     *      }
     *  }
     *
     * @param q the first set of HPO terms
     * @param m the second set of HPO terms
     * @return A detailed view of matches.
     */
    List<Map<String, Object>> getDetailedMatches(List<VocabularyTerm> q, List<VocabularyTerm> m);
}
