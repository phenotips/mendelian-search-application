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


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.sf.json.JSONObject;

/**
 * Static utility class for handling patient views.
 * @version $Id$
 */
public final class PatientViewUtils
{

    private PatientViewUtils()
    {
        throw new AssertionError();
    }
    /**
     * Sorts JSON objects using a specified key.
     * @param views The patient view JSON objects to be sorted.
     * @param key The key in the JSON object which maps to the value to be sorted.
     * @param ascending True if sort order should be ascending, flase for descending.
     */
    public static void sortPatientViewJSONs(List<JSONObject> views, String key, boolean ascending)
    {
        Collections.sort(views, generateComparator(key, ascending));
    }

    private static Comparator<JSONObject> generateComparator(final String key, final boolean ascending) {
        return new Comparator<JSONObject>()
        {
            @Override
            public int compare(JSONObject pv1, JSONObject pv2)
            {
                int compareVal = pv1.get(key).toString().compareTo(pv2.get(key).toString());
                if (ascending) {
                    return compareVal;
                } else {
                    return (-compareVal);
                }
            }
        };
    }

    /**
     * Trims a list of JSONs based on inputed page and results per page.
     * This method is out of bounds safe and will return the last possible page if index is too high.
     * @param views The list of patient view jsons to be paginated.
     * @param page The page number.
     * @param elementsPerPage The number of JSONs to be returned in the final list.
     * @return The trimmed list of JSON objects representing the specified page.
     */
    public static List<JSONObject> paginatePatientViewJSON(List<JSONObject> views, int page, int elementsPerPage) {
        if (page < 1 || elementsPerPage < 1) {
            return new ArrayList<JSONObject>();
        }
        int startIndex = (page - 1) * (elementsPerPage);
        int stopIndex = startIndex + elementsPerPage;
        if (startIndex > views.size()) {
            startIndex = elementsPerPage * (views.size() / elementsPerPage);
        }
        if (stopIndex > views.size()) {
            stopIndex = views.size();
        }
        return views.subList(startIndex, stopIndex);
    }
}
