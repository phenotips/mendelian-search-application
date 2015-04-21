package org.phenotips.mendelianSearch.internal;

import org.phenotips.data.Feature;
import org.phenotips.data.Patient;
import org.phenotips.data.PatientRepository;
import org.phenotips.data.permissions.AccessLevel;
import org.phenotips.data.permissions.PermissionsManager;
import org.phenotips.mendelianSearch.PatientView;
import org.phenotips.mendelianSearch.PatientViewFactory;
import org.phenotips.ontology.OntologyManager;

import org.xwiki.component.annotation.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.ga4gh.GAVariant;

@Component
@Singleton
public class DefaultPatientViewFactory implements PatientViewFactory
{
    @Inject
    private PermissionsManager pm;

    @Inject
    @Named("view")
    private AccessLevel viewAccess;

    @Inject
    private PatientRepository pr;

    @Inject
    private OntologyManager om;

    private final static String undisclosed_marker = "?";

    @Override
    public PatientView createPatientView(String id, List<GAVariant> variants, double phenotypeScore)
    {
        PatientView view;
        Patient patient = this.pr.getPatientById(id);
        if (patient == null) {
            patient = this.pr.getPatientByExternalId(id);
        }
        if (patient == null) {
            return null;
        }
        boolean hasAccess = this.hasPatientAccess(patient);
        if (hasAccess) {
            view = this.generateOpenPatientView(patient, variants, phenotypeScore);
        } else {
            view = this.generateRestrictedPatientView(patient, variants, phenotypeScore);
        }
        return view;
    }

    @Override
    public List<PatientView> createPatientViews(Set<String> ids, Map<String, List<GAVariant>> variantMap,
        Map<String, Double> scores)
        {
        List<PatientView> result = new ArrayList<PatientView>();
        if (ids == null || ids.isEmpty()) {
            return result;
        }
        for (String id : ids) {
            List<GAVariant> variants = variantMap.get(id);
            double phenotypeScore = scores.get(id);
            result.add(this.createPatientView(id, variants, phenotypeScore));
        }
        return result;
        }

    private PatientView generateRestrictedPatientView(Patient patient, List<GAVariant> variants, double phenotypeScore)
    {
        PatientView view = new DefaultPatientView();
        view.setType("restricted");
        view.setPatientId(undisclosed_marker);
        view.setOwner(undisclosed_marker);
        view.setPhenotype(new ArrayList<String>());
        view.setPhenotypeScore(phenotypeScore);
        view.setVariants(new ArrayList<GAVariant>());
        return view;
    }

    private PatientView generateOpenPatientView(Patient patient, List<GAVariant> variants, double phenotypeScore)
    {
        PatientView view = new DefaultPatientView();
        view.setType("open");

        String id = patient.getId();
        String owner = this.pm.getPatientAccess(patient).getOwner().getUsername();
        List<String> phenotype = this.getDisplayedPatientPhenotype(patient);

        view.setPatientId(id);
        view.setOwner(owner);
        view.setPhenotype(phenotype);
        view.setPhenotypeScore(phenotypeScore);
        view.setVariants(variants);
        return view;
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
        return this.pm.getPatientAccess(patient).hasAccessLevel(this.viewAccess);
    }
}
