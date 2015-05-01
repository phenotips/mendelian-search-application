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

import org.phenotips.mendelianSearch.PatientView;

import org.xwiki.component.annotation.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.inject.Singleton;

import net.sf.json.JSON;
import net.sf.json.JSONObject;

/**
 * @Version $Id$
 */

@Singleton
public class PatientViewUtils
{
    public static void sortPatientViewJSONs(List<JSONObject> views, String key, boolean ascending)
    {
        views.sort(generateComparator(key, ascending));
    }

    private static Comparator<JSONObject> generateComparator(final String key, final boolean ascending){
        return new Comparator<JSONObject>()
        {
            @Override
            public int compare(JSONObject pv1, JSONObject pv2)
            {
                int compareVal = pv1.get(key).toString().compareTo(pv2.get(key).toString());
                if(ascending) {
                    return compareVal;
                } else {
                    return (-compareVal);
                }
            }
        };
    }

    public static List<JSONObject> paginatePatientViewJSON (List<JSONObject> views, int page, int elementsPerPage) {
        int startIndex = (page-1) * (elementsPerPage);
        int stopIndex = startIndex + elementsPerPage;
        if (startIndex > views.size()){
            startIndex = elementsPerPage * (views.size() / elementsPerPage);
        }
        if (stopIndex > views.size()){
            stopIndex = views.size();
        }
        List<PatientView> result = new ArrayList<PatientView>();
        return views.subList(startIndex, stopIndex);
    }
}
