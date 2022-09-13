package com.pdgc.tests.conflictcheck.ConflictRollupTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.pdgc.conflictcheck.service.ConflictCheckRunner;
import com.pdgc.conflictcheck.service.ConflictRollup.RollupType;
import com.pdgc.conflictcheck.service.OverrideApplier;
import com.pdgc.conflictcheck.service.TestConflictCheckRunner;
import com.pdgc.conflictcheck.structures.Conflict;
import com.pdgc.conflictcheck.structures.TestConflict;
import com.pdgc.conflictcheck.structures.TestConflictKey;
import com.pdgc.conflictcheck.structures.builders.TestConflictSourceGroupKeyBuilder;
import com.pdgc.conflictcheck.structures.component.ConflictStatus;
import com.pdgc.conflictcheck.structures.component.override.ConflictOverride;
import com.pdgc.conflictcheck.structures.component.override.ConflictOverrideType;
import com.pdgc.conflictcheck.structures.lookup.readonly.ConflictConstants;
import com.pdgc.general.structures.Language;
import com.pdgc.general.structures.Media;
import com.pdgc.general.structures.Product;
import com.pdgc.general.structures.Territory;
import com.pdgc.general.structures.container.impl.PMTL;
import com.pdgc.general.structures.proxystruct.aggregate.impl.AggregateLanguage;
import com.pdgc.general.structures.proxystruct.aggregate.impl.AggregateMedia;
import com.pdgc.general.structures.proxystruct.aggregate.impl.AggregateProduct;
import com.pdgc.general.structures.proxystruct.aggregate.impl.AggregateTerritory;

public class ConflictRollupTest_rollupFullOverride extends ConflictRollupTest {
	// full season that's able to roll up to season level where a full override
	// exists for the leaf-level conflicts
	@Test
	public void rollupFullOverrideTest() {
		ConflictOverride fullSeasonOverride = new ConflictOverride(
			1L, 
			1L,
			new ConflictOverrideType(-1L, "randomType"),
			"dummy override comment", 
			new TestConflictKey(
				ConflictConstants.UNAVAILABLE_CORP_RIGHTS_CONFLICT,
				TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(simpleEpisodeLicense),
				TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(null),
				new PMTL(NCIS_SEASON_1, basc, dallas, english),
				simpleEpisodeLicense.getTerm(),
				simpleEpisodeLicense.getTimePeriod()
			)
		);

		List<ConflictOverride> overrides = new ArrayList<ConflictOverride>();
		overrides.add(fullSeasonOverride);
		
		List<TestConflict> leafConflicts = new ArrayList<>();
		{
			for (Product episode : productHierarchy.getLeaves(NCIS_SEASON_1)) {
				leafConflicts.add(new TestConflict(
					new TestConflictKey(
						ConflictConstants.UNAVAILABLE_CORP_RIGHTS_CONFLICT,
						TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(simpleEpisodeLicense),
						TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(null),
						new PMTL(episode, basc, dallas, english),
						simpleEpisodeLicense.getTerm(),
						simpleEpisodeLicense.getTimePeriod()
					),
					null,
					ConflictStatus.DEFAULT,
					null
				));
			}
		}

		List<RollupType> rollupOrder = Arrays.asList(RollupType.PRODUCT, RollupType.MEDIA, RollupType.TERRITORY, RollupType.LANGUAGE);
		
		ConflictCheckRunner<TestConflict> conflictCheckRunner = new TestConflictCheckRunner<>(
			new OverrideApplier(null),
			productHierarchy,
			mediaHierarchy,
			territoryHierarchy,
			languageHierarchy,
			productDictionary::get,
			mediaDictionary::get,
			territoryDictionary::get,
			languageDictionary::get
		);
		
		List<TestConflict> rolledConflicts = conflictCheckRunner.generateRolledConflicts(
			leafConflicts, 
			overrides,
			conflictBuilder,
			rollupOrder,
			productHierarchy, 
			mediaHierarchy, 
			territoryHierarchy,
			languageHierarchy,
			productDictionary::get
		);
		
		Collection<Product> sourceProducts;
		Collection<Media> sourceMedias;
		Collection<Territory> sourceTerritories;
		Collection<Language> sourceLanguages;
		
		assertEquals(1, rolledConflicts.size());
		
		{
			sourceProducts = Collections.singleton(NCIS_SEASON_1);
			sourceMedias = Collections.singleton(basc);
			sourceTerritories = Collections.singleton(dallas);
			sourceLanguages = Collections.singleton(english);
			
			assertTrue(sanitizedContains(
				rolledConflicts,
				new TestConflictKey(
					ConflictConstants.UNAVAILABLE_CORP_RIGHTS_CONFLICT,
					TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(simpleEpisodeLicense),
					TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(null),
					new PMTL(
						new AggregateProduct(sourceProducts),
						new AggregateMedia(sourceMedias),
						new AggregateTerritory(sourceTerritories),
						new AggregateLanguage(sourceLanguages)
					),
					simpleEpisodeLicense.getTerm(),
					simpleEpisodeLicense.getTimePeriod()
				), 
				fullConflictKeyEquivalence::equivalent,
				productHierarchy,
				mediaHierarchy,
				territoryHierarchy,
				languageHierarchy
			));
			
			Conflict rolledConflict = rolledConflicts.get(0);
			
			assertEquals(1, rolledConflict.getConflictOverrides().size());
			assertTrue(sanitizedContains(
				rolledConflict.getConflictOverrides(), 
				fullSeasonOverride.getConflictKey(), 
				fullConflictKeyEquivalence::equivalent,
				productHierarchy,
				mediaHierarchy,
				territoryHierarchy,
				languageHierarchy
			));
			assertEquals(ConflictStatus.RESOLVED, rolledConflict.getConflictStatus());
		}
	}
}
