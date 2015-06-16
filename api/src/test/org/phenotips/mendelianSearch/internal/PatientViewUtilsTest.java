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
package org.phenotips.mendelianSearch.internal;

import org.phenotips.mendelianSearch.PatientView;

import org.xwiki.component.manager.ComponentLookupException;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import net.sf.json.JSONObject;

import static org.mockito.Mockito.mock;

/**
 * @Version $Id$
 */
public class PatientViewUtilsTest
{

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

        PatientViewUtils.sortPatientViewJSONs(testJSON, NAME_STRING, true);
        Assert.assertEquals(testJSON.get(0), o2);
        Assert.assertEquals(testJSON.get(2), o3);

        PatientViewUtils.sortPatientViewJSONs(testJSON, NAME_STRING, false);
        Assert.assertEquals(testJSON.get(0), o3);
        Assert.assertEquals(testJSON.get(2), o2);

        PatientViewUtils.sortPatientViewJSONs(testJSON, SCORE_STRING, true);
        Assert.assertEquals(testJSON.get(0), o1);
        Assert.assertEquals(testJSON.get(2), o2);

        PatientViewUtils.sortPatientViewJSONs(testJSON, SCORE_STRING, false);
        Assert.assertEquals(testJSON.get(0), o2);
        Assert.assertEquals(testJSON.get(2), o1);
    }

    @Test
    public void testPaginatePatientViews(){
        String POSITION_STRING = "pos";
        List<JSONObject> views = new ArrayList<JSONObject>();
        for (int i = 1; i < 100; i++){
            JSONObject mockViewJSON = new JSONObject();
            //In order to test pagination the mocked view will return its index when .values() is called.
            mockViewJSON.element(POSITION_STRING, "" + i);
            views.add(mockViewJSON);
        }

        List<JSONObject> testViews = PatientViewUtils.paginatePatientViewJSON(views, 3, 20);
        Assert.assertEquals(20, testViews.size());
        Assert.assertEquals("41", testViews.get(0).get(POSITION_STRING));
        Assert.assertEquals("60", testViews.get(19).get(POSITION_STRING));

        testViews = PatientViewUtils.paginatePatientViewJSON(views, 1000, 15);
        Assert.assertEquals("91", testViews.get(0).get(POSITION_STRING));
        Assert.assertEquals("99", testViews.get(testViews.size()-1).get(POSITION_STRING));

        testViews = PatientViewUtils.paginatePatientViewJSON(views, 1, 1000);
        Assert.assertEquals(99, testViews.size());
        Assert.assertEquals("1", testViews.get(0).get(POSITION_STRING));
        Assert.assertEquals("99", testViews.get(98).get(POSITION_STRING));
    }
}
