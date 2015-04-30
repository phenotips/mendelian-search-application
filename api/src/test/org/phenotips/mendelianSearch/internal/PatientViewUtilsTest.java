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

package org.phenotips.mendelianSearch.internal;

import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.test.mockito.MockitoComponentMockingRule;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import net.sf.json.JSONObject;

/**
 * @Version $Id$
 */
public class PatientViewUtilsTest
{
    @Rule
    public final MockitoComponentMockingRule<PatientViewUtils> mocker =
        new MockitoComponentMockingRule<PatientViewUtils>(PatientViewUtils.class);

    @Test
    public void testSortJSONs() throws ComponentLookupException
    {
        String NAME_STRING = "name";
        String SCORE_STRING = "score";
        JSONObject o1 = new JSONObject();
        o1.element(NAME_STRING, "Bob");
        o1.element(SCORE_STRING, 0.003201);
        JSONObject o2 = new JSONObject();
        o2.element(NAME_STRING, "Alice");
        o2.element(SCORE_STRING, 0.90);
        JSONObject o3 = new JSONObject();
        o3.element(NAME_STRING, "Zach");
        o3.element(SCORE_STRING, 0.75);

        List<JSONObject> testJSON = new ArrayList<JSONObject>();

        testJSON.add(o2);
        testJSON.add(o3);
        testJSON.add(o1);

        PatientViewUtils utils = mocker.getComponentUnderTest();

        utils.sortPatientViewJSONs(testJSON, NAME_STRING, true);
        Assert.assertEquals(testJSON.get(0), o2);
        Assert.assertEquals(testJSON.get(2), o3);

        utils.sortPatientViewJSONs(testJSON, NAME_STRING, false);
        Assert.assertEquals(testJSON.get(0), o3);
        Assert.assertEquals(testJSON.get(2), o2);

        utils.sortPatientViewJSONs(testJSON, SCORE_STRING, true);
        Assert.assertEquals(testJSON.get(0), o1);
        Assert.assertEquals(testJSON.get(2), o2);

        utils.sortPatientViewJSONs(testJSON, SCORE_STRING, false);
        Assert.assertEquals(testJSON.get(0), o2);
        Assert.assertEquals(testJSON.get(2), o1);
    }
}
