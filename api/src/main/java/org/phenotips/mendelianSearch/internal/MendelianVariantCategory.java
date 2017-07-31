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

import java.util.List;

/**
 * A MendelianVariantCategory object describes a single category of variants that
 * may be used to filter searches in the Mendelian Search Application.
 * A variant category contains multiple variant effects that are used to query the
 * variant store. It also contains information about whether or not the category
 * is included in a default search.
 *
 * @version $Id$
 * @since 1.0
 */
public class MendelianVariantCategory
{
    /** See {@link #getVariantEffects()}. */
    private List<String> variantEffects;

    /** See {@link #getSelectedByDefault()}. */
    private boolean selectedByDefault;


    /**
     * Construct a new MendelianVariantCategory, containing a list of associated variant effects
     * and a default/non-default search status. The object is presently immutable.
     *
     * @param effects    a List of all the effects represented by this category
     * @param isDefault  whether or not the category is included in the search by default
     */
    public MendelianVariantCategory(List<String> effects, boolean isDefault)
    {
        this.variantEffects = effects;
        this.selectedByDefault = isDefault;
    }

    /**
     * Get the variant effects/mutation types contained within this variant category.
     *
     * @return Returns a List of the variant effects
     */
    public List<String> getVariantEffects()
    {
        return this.variantEffects;
    }

    /**
     * Determine whether or not this variant category is selected by default to be
     * included in the search.
     *
     * @return Returns true if the category is selected/included in the search by default
     */
    public boolean getSelectedByDefault()
    {
        return this.selectedByDefault;
    }
}
