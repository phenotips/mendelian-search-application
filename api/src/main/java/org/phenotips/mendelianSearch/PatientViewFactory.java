package org.phenotips.mendelianSearch;

import org.xwiki.component.annotation.Role;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ga4gh.GAVariant;

@Role
public interface PatientViewFactory
{
    PatientView createPatientView(String id, List<GAVariant> variants, double score);

    List<PatientView> createPatientViews(Set<String> ids, Map<String, List<GAVariant>> variantMap,
        Map<String, Double> scores);

}
