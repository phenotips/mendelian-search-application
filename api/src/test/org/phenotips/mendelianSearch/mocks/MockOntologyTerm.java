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
package org.phenotips.mendelianSearch.mocks;

import org.phenotips.ontology.OntologyService;
import org.phenotips.ontology.OntologyTerm;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import net.sf.json.JSON;
import net.sf.json.JSONObject;

/**
 * Simple mock for an ontology term, responding with pre-specified values.
 *
 * @version $Id$
 */
public class MockOntologyTerm implements OntologyTerm
{
    private final String id;

    private final Set<OntologyTerm> parents;

    private final Set<OntologyTerm> ancestors;

    /**
     * Create a simple Mock OntologyTerm.
     *
     * @param id the id of the term (e.g. "HP:0123456")
     * @param parents the parents of the term (or null)
     */
    public MockOntologyTerm(String id, Collection<OntologyTerm> parents)
    {
        this.id = id;
        this.parents = new HashSet<OntologyTerm>();
        this.ancestors = new HashSet<OntologyTerm>();

        if (parents != null) {
            this.parents.addAll(parents);
            // Add parents and ancestors of parents to get all ancestors
            this.ancestors.addAll(parents);
            for (OntologyTerm parent : this.parents) {
                this.ancestors.addAll(parent.getAncestors());
            }
        }
    }

    /**
     * Create a simple Mock OntologyTerm.
     *
     * @param id the id of the term (e.g. "HP:0123456")
     * @param parents the parents of the term (or null)
     * @param ancestors the ancestors of the term (or null)
     */
    public MockOntologyTerm(String id, Collection<OntologyTerm> parents, Collection<OntologyTerm> ancestors)
    {
        this.id = id;
        this.parents = new HashSet<OntologyTerm>();
        this.ancestors = new HashSet<OntologyTerm>();

        if (parents != null) {
            this.parents.addAll(parents);
        }
        if (ancestors != null) {
            this.ancestors.addAll(ancestors);
        }
    }

    @Override
    public String getId()
    {
        return this.id;
    }

    @Override
    public String getName()
    {
        // Not used
        return null;
    }

    @Override
    public String getDescription()
    {
        // Not used
        return null;
    }

    @Override
    public Set<OntologyTerm> getParents()
    {
        return this.parents;
    }

    @Override
    public Set<OntologyTerm> getAncestorsAndSelf()
    {
        Set<OntologyTerm> result = new LinkedHashSet<OntologyTerm>();
        result.add(this);
        result.addAll(this.ancestors);
        return result;
    }

    @Override
    public Set<OntologyTerm> getAncestors()
    {
        return this.ancestors;
    }

    @Override
    public Object get(String name)
    {
        // Not used
        return null;
    }

    @Override
    public OntologyService getOntology()
    {
        // Not used
        return null;
    }

    @Override
    public long getDistanceTo(OntologyTerm arg0)
    {
        // Not used
        return 0;
    }

    @Override
    public JSON toJson()
    {
        return new JSONObject();
    }
}
