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

import org.phenotips.vocabulary.Vocabulary;
import org.phenotips.vocabulary.VocabularyTerm;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSON;

public class MockDisease implements VocabularyTerm
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
    public Set<VocabularyTerm> getParents()
    {
        return null;
    }

    @Override
    public Set<VocabularyTerm> getAncestors()
    {
        return null;
    }

    @Override
    public Set<VocabularyTerm> getAncestorsAndSelf()
    {
        Set<VocabularyTerm> result = new HashSet<VocabularyTerm>();
        result.add(this);
        return result;
    }

    @Override
    public long getDistanceTo(VocabularyTerm other)
    {
        return 0;
    }

    @Override
    public Object get(String name)
    {
        return this.data.get(name);
    }

    @Override
    public Vocabulary getOntology()
    {
        return null;
    }

    @Override
    public JSON toJson() throws Exception
    {
        return null;
    }

}
