package edu.utexas.cs.nn.evolution.genotypes;

import static org.junit.Assert.*;

import java.util.ArrayList;
import org.junit.Before;
import org.junit.Test;

import edu.utexas.cs.nn.util.stats.StatisticsUtilities;
import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.EvolutionaryHistory;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype.LinkGene;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype.NodeGene;
import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;

public class TWEANNGenotypeTest {

	final static int MUTATIONS1 = 10;

	@Before
	public void setup() {
		MMNEAT.clearClasses();
		EvolutionaryHistory.setInnovation(0);
		EvolutionaryHistory.setHighestGenotypeId(0);
	}

	@Test
	public void test_Equals() {
		Parameters.initializeParameterCollections(
				new String[] { "io:false", "netio:false", "allowMultipleFunctions:true", "recurrency:false" });
		MMNEAT.loadClasses();

		TWEANNGenotype tg1 = new TWEANNGenotype(5, 2, 0);
		TWEANNGenotype tg2 = new TWEANNGenotype(5, 2, 0);

		MMNEAT.genotype = tg1.copy();
		EvolutionaryHistory.initArchetype(0);

		TWEANNGenotype check = tg1;
		assertTrue(check.equals(tg1));
		assertFalse(tg1.equals(tg2));
		for (int i = 0; i < MUTATIONS1; i++) {
			tg1.mutate();
			tg2.mutate();
		}
	}

	@Test
	public void test_sameStructure() {
		Parameters.initializeParameterCollections(
				new String[] { "io:false", "netio:false", "allowMultipleFunctions:true", "recurrency:false" });
		CommonConstants.freezeBeforeModeMutation = true;
		MMNEAT.loadClasses();

		TWEANNGenotype tg1 = new TWEANNGenotype(5, 2, 0);
		TWEANNGenotype tg2 = new TWEANNGenotype(5, 2, 0);
		MMNEAT.genotype = tg1.copy();
		EvolutionaryHistory.initArchetype(0);

		for (int i = 0; i < MUTATIONS1; i++) {
			tg1.mutate();
			tg2.mutate();
		}

		TWEANNGenotype tg1Copy = (TWEANNGenotype) tg1.copy();
		assertTrue(TWEANNGenotype.sameStructure(tg1Copy, tg1));
		assertFalse(TWEANNGenotype.sameStructure(tg1, tg2));
	}

	@Test
	public void test_getLinksBetween() {
		TWEANNGenotype tg1 = new TWEANNGenotype(2, 1, 0);
		LinkGene lg = tg1.getLinkBetween(-1, -3);
		assertTrue(lg != null);
		assertTrue(lg.sourceInnovation == -1);
		assertTrue(lg.targetInnovation == -3);
	}

	@Test
	public void test_biggestInnovation() {
		Parameters.initializeParameterCollections(
				new String[] { "io:false", "netio:false", "allowMultipleFunctions:true", "recurrency:false" });

		MMNEAT.loadClasses();
		TWEANNGenotype tg1 = new TWEANNGenotype(5, 2, 0);
		TWEANNGenotype tg2 = new TWEANNGenotype(5, 2, 0);
		MMNEAT.genotype = tg1.copy();
		EvolutionaryHistory.initArchetype(0);

		for (int i = 0; i < MUTATIONS1; i++) {
			tg1.mutate();
			tg2.mutate();
		}

		ArrayList<NodeGene> tg1Genes = tg1.nodes;
		ArrayList<LinkGene> tg1Links = tg1.links;

		long[] tg1innoGene = new long[tg1Genes.size()];
		long[] tg1innoLink = new long[tg1Links.size()];
		for (int i = 0; i < tg1Genes.size(); i++) {
			tg1innoGene[i] = tg1Genes.get(i).innovation;
		}
		for (int i = 0; i < tg1Links.size(); i++) {
			tg1innoLink[i] = tg1Links.get(i).innovation;
		}
		long trueMaxInnoGene = StatisticsUtilities.maximum(tg1innoGene);
		long trueMaxInnoLink = StatisticsUtilities.maximum(tg1innoLink);
		long trueMaxInno = Math.max(trueMaxInnoGene, trueMaxInnoLink);
		long maxInno = tg1.biggestInnovation();

		assertTrue(trueMaxInno == maxInno);
	}
}
