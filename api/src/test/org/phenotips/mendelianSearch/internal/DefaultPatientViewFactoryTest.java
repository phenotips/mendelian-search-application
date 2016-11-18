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

import org.phenotips.data.Feature;
import org.phenotips.data.Patient;
import org.phenotips.data.PatientRepository;
import org.phenotips.data.permissions.AccessLevel;
import org.phenotips.data.permissions.Owner;
import org.phenotips.data.permissions.PatientAccess;
import org.phenotips.data.permissions.PermissionsManager;
import org.phenotips.mendelianSearch.PatientView;
import org.phenotips.vocabulary.VocabularyManager;

import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.test.mockito.MockitoComponentMockingRule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ga4gh.GAVariant;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.json.JSONArray;
import org.json.JSONObject;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class DefaultPatientViewFactoryTest
{
    @Rule
    public final MockitoComponentMockingRule<DefaultPatientViewFactory> mocker =
        new MockitoComponentMockingRule<DefaultPatientViewFactory>(DefaultPatientViewFactory.class);

    private PermissionsManager pm;

    private PatientRepository pr;

    private VocabularyManager om;

    private AccessLevel viewAccess;

    private MendelianSearchRequest request;

    @Before
    public void setup() throws ComponentLookupException
    {
        MockitoAnnotations.initMocks(this);
        this.pr = this.mocker.getInstance(PatientRepository.class);
        this.om = this.mocker.getInstance(VocabularyManager.class);
        this.pm = this.mocker.getInstance(PermissionsManager.class);
        this.viewAccess = this.mocker.getInstance(AccessLevel.class, "view");

        request = mock(MendelianSearchRequest.class);
    }

    @Test
    public void testCreateOpenPatientView() throws ComponentLookupException
    {
        String id = "123";
        List<GAVariant> variants = new ArrayList<GAVariant>();
        GAVariant v1 = mock(GAVariant.class);
        variants.add(v1);
        GAVariant v2 = mock(GAVariant.class);
        variants.add(v2);
        GAVariant v3 = mock(GAVariant.class);
        variants.add(v3);
        double score = 0.5;

        Patient p = mock(Patient.class);
        Mockito.doReturn(p).when(this.pr).getPatientByExternalId(id);

        PatientAccess mockedAccess = mock(PatientAccess.class);
        Mockito.doReturn(mockedAccess).when(this.pm).getPatientAccess(p);
        Mockito.doReturn(true).when(mockedAccess).hasAccessLevel(this.viewAccess);

        Mockito.doReturn("123").when(p).getId();
        Mockito.doReturn(mockedAccess).when(this.pm).getPatientAccess(p);
        Owner mockedOwner = mock(Owner.class);
        Mockito.doReturn(mockedOwner).when(mockedAccess).getOwner();

        Mockito.doReturn("Bob").when(mockedOwner).getUsername();

        // Build mockFeatures
        Set<Feature> mockFeatures = new HashSet<Feature>();
        Feature f1 = mock(Feature.class);
        mockFeatures.add(f1);
        Mockito.doReturn("tremors").when(f1).getName();
        Mockito.doReturn(false).when(f1).isPresent();
        Feature f2 = mock(Feature.class);
        mockFeatures.add(f2);
        Mockito.doReturn(true).when(f2).isPresent();
        Mockito.doReturn("bumps").when(f2).getName();
        Feature f3 = mock(Feature.class);
        mockFeatures.add(f3);
        Mockito.doReturn(true).when(f3).isPresent();
        Mockito.doReturn("bruises").when(f3).getName();

        Mockito.doReturn(mockFeatures).when(p).getFeatures();

        PatientView result = this.mocker.getComponentUnderTest().createPatientView(id, variants, score, this.request);
        JSONObject resultJSON = result.toJSON();
        assertEquals(result.getType(), "open");
        assertEquals(resultJSON.get("patientId"), "123");
        assertEquals(resultJSON.get("owner"), "Bob");
        JSONArray resultVariants = (JSONArray) resultJSON.get("variants");
        assert (resultVariants.size() > 0);

        Object[] resultPhenotype = ((List<String>) resultJSON.get("phenotype")).toArray();
        Object[] expectedPhenotype = { "bumps", "bruises" };
        Arrays.sort(expectedPhenotype);
        Arrays.sort(resultPhenotype);
        assertArrayEquals(expectedPhenotype, resultPhenotype);

    }

    @Test
    public void testCreateRestrictedPatientView() throws ComponentLookupException
    {
        String id = "123";
        List<GAVariant> variants = new ArrayList<GAVariant>();
        GAVariant v1 = mock(GAVariant.class);
        variants.add(v1);
        GAVariant v2 = mock(GAVariant.class);
        variants.add(v2);
        GAVariant v3 = mock(GAVariant.class);
        variants.add(v3);
        double score = 0.5;

        Patient p = mock(Patient.class);
        Mockito.doReturn(p).when(this.pr).getPatientByExternalId(id);

        PatientAccess mockedAccess = mock(PatientAccess.class);
        Mockito.doReturn(mockedAccess).when(this.pm).getPatientAccess(p);
        Mockito.doReturn(false).when(mockedAccess).hasAccessLevel(this.viewAccess);

        Mockito.doReturn("123").when(p).getId();
        Mockito.doReturn(mockedAccess).when(this.pm).getPatientAccess(p);
        Owner mockedOwner = mock(Owner.class);
        Mockito.doReturn(mockedOwner).when(mockedAccess).getOwner();

        Mockito.doReturn("Bob").when(mockedOwner).getUsername();

        // Build mockFeatures
        Set<Feature> mockFeatures = new HashSet<Feature>();
        Feature f1 = mock(Feature.class);
        mockFeatures.add(f1);
        Mockito.doReturn("tremors").when(f1).getName();
        Mockito.doReturn(false).when(f1).isPresent();
        Feature f2 = mock(Feature.class);
        mockFeatures.add(f2);
        Mockito.doReturn(true).when(f2).isPresent();
        Mockito.doReturn("bumps").when(f2).getName();
        Feature f3 = mock(Feature.class);
        mockFeatures.add(f3);
        Mockito.doReturn(true).when(f3).isPresent();
        Mockito.doReturn("bruises").when(f3).getName();

        Mockito.doReturn(mockFeatures).when(p).getFeatures();

        PatientView result = this.mocker.getComponentUnderTest().createPatientView(id, variants, score, this.request);
        JSONObject resultJSON = result.toJSON();
        assertEquals(result.getType(), "restricted");
        assertEquals(resultJSON.get("patientId"), "?");
        assertEquals(resultJSON.get("owner"), "?");
        JSONArray resultVariants = (JSONArray) resultJSON.get("variants");
        assertEquals(resultVariants.size(), 0);
        List<String> resultPhenotype = (List<String>) resultJSON.get("phenotype");
        assertEquals(resultPhenotype.size(), 0);

    }

    @Test
    public void testEmptyVariantList() throws ComponentLookupException
    {
        String id = "123";
        List<GAVariant> variants = new ArrayList<GAVariant>();

        double score = 0.5;

        Patient p = mock(Patient.class);
        Mockito.doReturn(p).when(this.pr).getPatientByExternalId(id);

        PatientAccess mockedAccess = mock(PatientAccess.class);
        Mockito.doReturn(mockedAccess).when(this.pm).getPatientAccess(p);
        Mockito.doReturn(true).when(mockedAccess).hasAccessLevel(this.viewAccess);

        Mockito.doReturn("123").when(p).getId();
        Mockito.doReturn(mockedAccess).when(this.pm).getPatientAccess(p);
        Owner mockedOwner = mock(Owner.class);
        Mockito.doReturn(mockedOwner).when(mockedAccess).getOwner();

        Mockito.doReturn("Bob").when(mockedOwner).getUsername();

        // Build mockFeatures
        Set<Feature> mockFeatures = new HashSet<Feature>();
        Feature f1 = mock(Feature.class);
        mockFeatures.add(f1);
        Mockito.doReturn("tremors").when(f1).getName();
        Mockito.doReturn(false).when(f1).isPresent();
        Feature f2 = mock(Feature.class);
        mockFeatures.add(f2);
        Mockito.doReturn(true).when(f2).isPresent();
        Mockito.doReturn("bumps").when(f2).getName();
        Feature f3 = mock(Feature.class);
        mockFeatures.add(f3);
        Mockito.doReturn(true).when(f3).isPresent();
        Mockito.doReturn("bruises").when(f3).getName();

        Mockito.doReturn(mockFeatures).when(p).getFeatures();

        PatientView result = this.mocker.getComponentUnderTest().createPatientView(id, variants, score, this.request);
        JSONObject resultJSON = result.toJSON();
        assertEquals(result.getType(), "open");
        assertEquals(resultJSON.get("patientId"), "123");
        assertEquals(resultJSON.get("owner"), "Bob");
        JSONArray resultVariants = (JSONArray) resultJSON.get("variants");
        assert (resultVariants.size() == 0);

        Object[] resultPhenotype = ((List<String>) resultJSON.get("phenotype")).toArray();
        Object[] expectedPhenotype = { "bumps", "bruises" };
        Arrays.sort(expectedPhenotype);
        Arrays.sort(resultPhenotype);
        assertArrayEquals(expectedPhenotype, resultPhenotype);
    }

}
