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

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

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

    private static final String SOLVED_STRING = "solved";

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
        Patient patient = this.pr.getPatientById(id);
        if (patient == null) {
            patient = this.pr.getPatientByExternalId(id);
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
        List<String> candidateGenes = this.getPatientGenes(patient, "genes");

        if (this.isGeneSolved(patient, geneSymbol)) {
            return SOLVED_STRING;
        }

        if (candidateGenes.contains(geneSymbol)) {
            return "candidate";
        }

        List<String> rejectedGenes = this.getPatientGenes(patient, "rejectedGenes");
        if (rejectedGenes.contains(geneSymbol)) {
            return "rejected";
        }

        return "";
    }

    private List<String> getPatientGenes(Patient patient, String name)
    {
        List<String> result = new ArrayList<String>();
        PatientData data = patient.getData(name);
        if (data != null) {
            for (Object datum : data) {
                Map<String, String> gene = (Map<String, String>) datum;
                result.add(gene.get("gene"));
            }
        }
        return result;
    }

    private boolean isGeneSolved(Patient patient, String geneSymbol)
    {
        PatientData solvedData = patient.getData(SOLVED_STRING);
        if (solvedData != null) {
            if ("1".equals(solvedData.get(SOLVED_STRING))) {
                if (geneSymbol.equals(solvedData.get("solved__gene_id"))) {
                    return true;
                }
            }
        }
        return false;
    }

    private List<Disorder> getPatientDiagnosis(Patient patient)
    {
        List<Disorder> result = new ArrayList<Disorder>();
        Set<? extends Disorder> disorders = patient.getDisorders();
        if (!disorders.isEmpty()) {
            for (Disorder dis : disorders) {
                result.add(dis);
            }
        }
        return result;
    }

}
