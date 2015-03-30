package org.phenotips.mendelianSearch.phenotype;

import org.phenotips.ontology.OntologyTerm;

import org.xwiki.component.annotation.Role;

import java.util.List;

@Role
public interface PhenotypeScorer
{
    double getScore(List<OntologyTerm> p1, List<OntologyTerm> p2);
}
