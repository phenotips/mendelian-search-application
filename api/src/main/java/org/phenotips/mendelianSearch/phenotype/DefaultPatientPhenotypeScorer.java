package org.phenotips.mendelianSearch.phenotype;

import org.phenotips.data.Patient;
import org.phenotips.ontology.OntologyTerm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

public class DefaultPatientPhenotypeScorer implements PatientPhenotypeScorer
{
    @Inject
    private PhenotypeScorer scorer;

    @Override
    public Map<Patient, Double> getScores(List<OntologyTerm> phenotype, List<Patient> patients)
    {
        Map<Patient, Double> patientScores = new HashMap<Patient, Double>();

        return patientScores;
    }
}
