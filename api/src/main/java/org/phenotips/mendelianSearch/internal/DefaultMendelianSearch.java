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

import org.phenotips.mendelianSearch.MendelianSearch;
import org.phenotips.mendelianSearch.PatientView;
import org.phenotips.mendelianSearch.PatientViewFactory;
import org.phenotips.mendelianSearch.phenotype.PatientPhenotypeScorer;
import org.phenotips.variantStoreIntegration.VariantStoreService;
import org.phenotips.vocabulary.VocabularyManager;
import org.phenotips.vocabulary.VocabularyTerm;

import org.xwiki.component.annotation.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.ga4gh.GAVariant;

/**
 * Default implementation of {@link MendelianSearch}.
 *
 * @version $Id$
 */
@Component
public class DefaultMendelianSearch implements MendelianSearch
{

    @Inject
    private VariantStoreService variantStore;

    @Inject
    private PatientPhenotypeScorer patientPhenotypeScorer;

    @Inject
    private VocabularyManager om;

    @Inject
    private PatientViewFactory pvf;

    private Map<String, MendelianVariantCategory> variantCategories;

    @Override
    public List<PatientView> search(MendelianSearchRequest request)
    {
        Set<String> allIds = this.findValidIds();
        Map<String, List<GAVariant>> matchingGenotype = this.findIdsMatchingGenotype(request, allIds);

        Set<String> matchingPhenotype = this.findIdsMatchingPhenotype(request, allIds);

        Set<String> matchedIds = new HashSet<String>(matchingGenotype.keySet());
        matchedIds.retainAll(matchingPhenotype);

        Map<String, Double> scores = this.scorePatientPhenotypes(request, matchedIds);

        List<PatientView> views = this.pvf.createPatientViews(matchedIds, matchingGenotype, scores, request);

        return views;
    }

    @Override
    public Map<String, Object> getOverview(MendelianSearchRequest request)
    {
        Map<String, Object> result = null;
        if ("fuzzy".equals(request.get("phenotypeMatching"))) {
            result = this.getFuzzyOverview(request);
        }
        return result;
    }

    private Map<String, Object> getFuzzyOverview(MendelianSearchRequest request)
    {
        Set<String> allIds = this.findValidIds();
        Map<String, List<GAVariant>> matchingGenotype = this.findIdsMatchingGenotype(request, allIds);

        Set<String> matchingIds = matchingGenotype.keySet();
        allIds.removeAll(matchingIds);
        Set<String> nonMatchingIds = allIds;

        Map<String, Double> matchingScores = this.scorePatientPhenotypes(request, matchingIds);
        Map<String, Double> nonMatchingScores = this.scorePatientPhenotypes(request, nonMatchingIds);

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("withGene", matchingScores.values());
        result.put("withoutGene", nonMatchingScores.values());

        return result;
    }

    private Map<String, Double> scorePatientPhenotypes(MendelianSearchRequest request, Set<String> ids)
    {
        @SuppressWarnings("unchecked")
        List<String> hpoIds = (List<String>) request.get("phenotype");

        // Convert the request list of HPO ids into VocabularyTerms
        List<VocabularyTerm> phenotype = new ArrayList<>();
        for (String termId : hpoIds) {
            phenotype.add(this.om.resolveTerm(termId));
        }
        return this.patientPhenotypeScorer.getScoresById(phenotype, ids);
    }

    /**
     * Filters a set of Ids to those which match the input request.
     *
     * @param request Phenotype filter parameters should be present in the request
     * @param ids The set of all valid Ids which may be returned.
     * @return
     */
    private Set<String> findIdsMatchingPhenotype(MendelianSearchRequest request, Set<String> ids)
    {
        //Right now only the matching type 'fuzzy' so just return the ids.
        return ids;
    }

    /**
     * Finds a map of Ids to variants which pass the filters specified in the request.
     *
     * @param request Variant filter parameters should be specified in the request
     * @param ids The set of all valid Ids which may be returned.
     * @return A map of patient ids to variants
     */
    @SuppressWarnings("unchecked")
    private Map<String, List<GAVariant>> findIdsMatchingGenotype(MendelianSearchRequest request, Set<String> ids)
    {
        Map<String, List<GAVariant>> matchingVariants;

        Map<String, MendelianVariantCategory> varCategories = getVariantCategories();
        List<String> varCategoryNames = (List<String>) request.get("variantCategoryNames");
        List<String> varEffects = new ArrayList<String>();
        for (String category : varCategoryNames) {
            varEffects.addAll(varCategories.get(category).getVariantEffects());
        }

        String gene = (String) request.get("geneSymbol");
        // First query the variant store and receive a JSONArray of patient variant information --> store in List.
        matchingVariants = this.variantStore.getIndividualsWithGene(gene, varEffects,
            (Map<String, Double>) request.get("alleleFrequencies"));

        if ((int) request.get("matchGene") == 1) {
            return matchingVariants;
        }
        Set<String> nonMatchingIds = new HashSet<String>(ids);
        nonMatchingIds.removeAll(matchingVariants.keySet());
        Map<String, List<GAVariant>> nonMatchingVariants = new HashMap<String, List<GAVariant>>();
        for (String id : nonMatchingIds) {
            nonMatchingVariants.put(id, this.variantStore.getTopHarmfullVariantsForGene(id, gene, 5));
        }
        return nonMatchingVariants;
    }

    @Override
    public Set<String> findValidIds()
    {
        return new HashSet<String>(this.variantStore.getAllIndividuals());
    }

    @Override
    public Map<String, MendelianVariantCategory> getVariantCategories() {
        if (this.variantCategories == null) {
            this.variantCategories = new LinkedHashMap<String, MendelianVariantCategory>();

            List<String> fsInDelEffects = Arrays.asList("frameshift_truncation",
                "frameshift_elongation", "frameshift_variant", "internal_feature_elongation",
                "feature_truncation");
            List<String> inframeInDelEffects = Arrays.asList("disruptive_inframe_deletion", "inframe_insertion",
                "disruptive_inframe_insertion", "inframe_deletion");
            List<String> splicingEffects = Arrays.asList("splice_acceptor_variant", "splice_donor_variant",
                "splice_region_variant", "exon_loss_variant", "splicing_variant");
            List<String> nonsenseEffects = Arrays.asList("stop_gained");
            List<String> missenseEffects = Arrays.asList("missense_variant", "rare_amino_acid_variant");
            List<String> otherEffects = Arrays.asList("stop_lost", "start_lost", "chromosome_number_variation",
                "mnv", "complex_substitution", "transcript_ablation", "5_prime_utr_truncation",
                "3_prime_utr_truncation", "stop_retained_variant", "initiator_codon_variant",
                "synonymous_variant", "coding_transcript_intron_variant", "non_coding_transcript_exon_variant",
                "non_coding_transcript_intron_variant", "5_prime_UTR_premature_start_codon_gain_variant",
                "5_prime_utr_variant", "3_prime_utr_variant", "direct_tandem_duplication",
                "upstream_gene_variant", "downstream_gene_variant", "intergenic_variant",
                "tf_binding_site_variant", "regulatory_region_variant", "conserved_intron_variant",
                "intragenic_variant", "conserved_intergenic_variant", "structural_variant",
                "coding_sequence_variant", "intron_variant", "exon_variant", "miRNA", "gene_variant",
                "coding_transcript_variant", "non_coding_transcript_variant", "  transcript_variant",
                "intergenic_region", "chromosome", "sequence_variant");

            MendelianVariantCategory fsInDelCategory = new MendelianVariantCategory(fsInDelEffects, true);
            MendelianVariantCategory inframeInDelCategory = new MendelianVariantCategory(inframeInDelEffects, true);
            MendelianVariantCategory splicingCategory = new MendelianVariantCategory(splicingEffects, true);
            MendelianVariantCategory nonsenseCategory = new MendelianVariantCategory(nonsenseEffects, true);
            MendelianVariantCategory missenseCategory = new MendelianVariantCategory(missenseEffects, true);
            MendelianVariantCategory otherCategory = new MendelianVariantCategory(otherEffects, false);

            this.variantCategories.put("fsInDel", fsInDelCategory);
            this.variantCategories.put("inframeInDel", inframeInDelCategory);
            this.variantCategories.put("splicing", splicingCategory);
            this.variantCategories.put("nonsense", nonsenseCategory);
            this.variantCategories.put("missense", missenseCategory);
            this.variantCategories.put("other", otherCategory);
        }
        return this.variantCategories;
    }
}
