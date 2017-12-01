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

import org.phenotips.data.Disorder;
import org.phenotips.data.Feature;
import org.phenotips.data.Patient;
import org.phenotips.data.PatientData;
import org.phenotips.data.PatientRepository;
import org.phenotips.data.permissions.AccessLevel;
import org.phenotips.data.permissions.PatientAccess;
import org.phenotips.data.permissions.PermissionsManager;
import org.phenotips.data.permissions.internal.visibility.HiddenVisibility;
import org.phenotips.mendelianSearch.PatientView;
import org.phenotips.mendelianSearch.PatientViewFactory;

import org.xwiki.component.annotation.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.apache.commons.lang3.StringUtils;
import org.ga4gh.GAVariant;

import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.XWikiContext;

/**
 * Default implementation of {@link PatientViewFactory}.
 * @version $Id$
 */
@Component
@Singleton
public class DefaultPatientViewFactory implements PatientViewFactory
{
    private static final String UNDISCLOSED_MARKER = "?";

    @Inject
    private PermissionsManager pm;

    @Inject
    @Named("view")
    private AccessLevel viewAccess;

    @Inject
    private PatientRepository pr;

    @Inject
    private Provider<XWikiContext> xcontext;

    @Override
    public PatientView createPatientView(String id, List<GAVariant> variants, Double phenotypeScore,
        MendelianSearchRequest request)
    {
        PatientView view;
        Patient patient = this.pr.get(id);
        if (patient == null) {
            patient = this.pr.getByName(id);
        }
        if ((patient == null) || (variants == null) || (phenotypeScore == null)) {
            return null;
        }
        boolean hasAccess = this.hasPatientAccess(patient);
        if (hasAccess) {
            view = this.generateOpenPatientView(patient, variants, phenotypeScore, request);
        } else {
            view = this.generateRestrictedPatientView(patient, variants, phenotypeScore, request);
        }
        return view;
    }

    @Override
    public List<PatientView> createPatientViews(Set<String> ids, Map<String, List<GAVariant>> variantMap,
        Map<String, Double> scores, MendelianSearchRequest request)
    {
        List<PatientView> result = new ArrayList<PatientView>();
        if (ids == null || ids.isEmpty()) {
            return result;
        }
        for (String id : ids) {
            List<GAVariant> variants = variantMap.get(id);
            Double phenotypeScore = scores.get(id);
            PatientView view = this.createPatientView(id, variants, phenotypeScore, request);
            if (view != null) {
                result.add(view);
            }
        }
        return result;
    }

    private PatientView generateRestrictedPatientView(Patient patient, List<GAVariant> variants,
        double phenotypeScore, MendelianSearchRequest request)
    {
        PatientView view = new DefaultPatientView();
        view.setType("restricted");
        view.setPatientId(UNDISCLOSED_MARKER);
        view.setOwner(UNDISCLOSED_MARKER);
        view.setPhenotype(new ArrayList<String>());
        view.setPhenotypeScore(phenotypeScore);
        view.setVariants(new ArrayList<GAVariant>());
        return view;
    }

    private PatientView generateOpenPatientView(Patient patient, List<GAVariant> variants,
        double phenotypeScore, MendelianSearchRequest request)
    {
        PatientView view = new DefaultPatientView();
        view.setType("open");

        String id = patient.getId();
        String owner = this.pm.getPatientAccess(patient).getOwner().getUsername();
        List<String> phenotype = this.getDisplayedPatientPhenotype(patient);

        view.setPatientId(id);
        view.setPatientURL(this.getPatientURL(patient));
        view.setGeneStatus(this.getPatientGeneStatus(patient, (String) request.get("geneSymbol")));
        view.setOwner(owner);
        view.setDiagnosis(this.getPatientDiagnosis(patient));

        view.setPhenotype(phenotype);
        view.setPhenotypeScore(phenotypeScore);
        view.setVariants(variants);

        return view;
    }

    private String getPatientURL(Patient patient)
    {
        XWiki xWiki = this.xcontext.get().getWiki();
        return xWiki.getURL(patient.getDocument(), "view",
            this.xcontext.get());
    }

    private List<String> getDisplayedPatientPhenotype(Patient patient)
    {
        List<String> result = new ArrayList<String>();
        if (!patient.getFeatures().isEmpty()) {
            for (Feature feature : patient.getFeatures()) {
                if (!feature.isPresent()) {
                    continue;
                }
                result.add(feature.getName());
            }
        }
        return result;
    }

    private boolean hasPatientAccess(Patient patient)
    {
        PatientAccess pa = this.pm.getPatientAccess(patient);
        return pa.hasAccessLevel(this.viewAccess) && (pa.getVisibility().compareTo(new HiddenVisibility()) > 0);
    }

    private String getPatientGeneStatus(Patient patient, String geneSymbol)
    {
        PatientData<Map<String, String>> allGenes = patient.getData("genes");
        if (allGenes != null && allGenes.isIndexed()) {
            for (Map<String, String> gene : allGenes) {
                String geneName = gene.get("gene");
                if (StringUtils.isBlank(geneName) || !gene.equals(geneSymbol)) {
                    continue;
                }
                return gene.get("status");
            }
        }
        return "";
    }

    private List<Disorder> getPatientDiagnosis(Patient patient)
    {
        List<Disorder> result = new ArrayList<Disorder>();
        PatientData<Disorder> disorders = patient.getData("disorders");
        if (disorders != null) {
            Iterator<Disorder> iterator = disorders.iterator();
            while (iterator.hasNext()) {
                Disorder disease = iterator.next();
                result.add(disease);
            }
        }
        return result;
    }
}
