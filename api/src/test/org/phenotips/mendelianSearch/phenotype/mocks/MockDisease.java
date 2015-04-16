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
package org.phenotips.mendelianSearch.phenotype.mocks;

import org.phenotips.ontology.OntologyService;
import org.phenotips.ontology.OntologyTerm;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSON;

public class MockDisease implements OntologyTerm
{
    private Map<String, Object> data;

    public MockDisease(String id, String name, Collection<String> hpoSymptoms)
    {
        this.data = new HashMap<String, Object>();
        this.data.put("id", id);
        this.data.put("name", name);
        this.data.put("actual_symptom", hpoSymptoms);
    }

    @Override
    public String getId()
    {
        return (String) this.data.get("id");
    }

    @Override
    public String getName()
    {
        return (String) this.data.get("name");
    }

    @Override
    public String getDescription()
    {
        return null;
    }

    @Override
    public Set<OntologyTerm> getParents()
    {
        return null;
    }

    @Override
    public Set<OntologyTerm> getAncestors()
    {
        return null;
    }

    @Override
    public Set<OntologyTerm> getAncestorsAndSelf()
    {
        Set<OntologyTerm> result = new HashSet<OntologyTerm>();
        result.add(this);
        return result;
    }

    @Override
    public long getDistanceTo(OntologyTerm other)
    {
        return 0;
    }

    @Override
    public Object get(String name)
    {
        return this.data.get(name);
    }

    @Override
    public OntologyService getOntology()
    {
        return null;
    }

    @Override
    public JSON toJson() throws Exception
    {
        return null;
    }

}
