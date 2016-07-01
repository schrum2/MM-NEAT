package edu.utexas.cs.nn.evolution.mutation.tweann;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype.LinkGene;
import edu.utexas.cs.nn.parameters.Parameters;
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

	@SuppressWarnings("unchecked")
	@Test
	public void test() {
		ArrayList<LinkGene> links = (ArrayList<LinkGene>) tg1.links.clone();
		rlm.mutate(tg1);
		LinkGene missingLink = null;
		for(LinkGene link : links) {
			if(!tg1.links.contains(link)){
				missingLink = link;
			}
		}
		assertTrue("all links match!", missingLink != null);
		LinkGene foundLink = null;
		for(LinkGene link : tg1.links) {
			if(link.weight == missingLink.weight) {
				foundLink = link;
			}
		}
		assertTrue(foundLink != null);
	}

}
