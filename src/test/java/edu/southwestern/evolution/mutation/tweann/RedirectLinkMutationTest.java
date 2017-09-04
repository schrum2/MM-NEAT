package edu.southwestern.evolution.mutation.tweann;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.TWEANNGenotype;
import edu.southwestern.evolution.genotypes.TWEANNGenotype.LinkGene;
import edu.southwestern.parameters.Parameters;
//TODO
public class RedirectLinkMutationTest {

	TWEANNGenotype tg1;
	RedirectLinkMutation rlm;
	@Before
	public void setUp() throws Exception {
		MMNEAT.clearClasses();
		Parameters.initializeParameterCollections(
				new String[] { "io:false", "netio:false", "recurrency:false", "redirectLinkRate:1.0" });
		MMNEAT.loadClasses();
		tg1 = new TWEANNGenotype();
		rlm = new RedirectLinkMutation();
	}

	@After
	public void tearDown() throws Exception {
		tg1 = null;
		rlm = null;
		MMNEAT.clearClasses();
	}
	
	/**
	 * Tests that links can be redirected after one is deleted.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void test() {
		TWEANNGenotype copy = (TWEANNGenotype) tg1.copy();
		ArrayList<LinkGene> links = (ArrayList<LinkGene>) tg1.links.clone();
		int numLinks = links.size();
		//System.out.println("before: " + tg1);
		do {
			rlm.mutate(copy);
		} while(copy.links.size() != numLinks && (copy = (TWEANNGenotype) tg1.copy()) != null);
		//rlm.mutate(tg1);
		//System.out.println("after: " + tg1);
		LinkGene missingLink = null;
		for(LinkGene link : links) {
			if(!copy.links.contains(link)){
				missingLink = link;
			}
		}
		//assert that missingLink has been reassigned (because link was still contained in copy)
		assertTrue("all links match!", missingLink != null);
		LinkGene foundLink = null;
		for(LinkGene link : copy.links) {
			if(link.weight == missingLink.weight) {
				foundLink = link;
			}
		}
		//assert that foundLink has been reassigned (because link was still contained in LinkGene)
		assertTrue(foundLink != null);
	}

}
