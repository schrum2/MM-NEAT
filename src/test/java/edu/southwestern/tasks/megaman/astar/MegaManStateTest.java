package edu.southwestern.tasks.megaman.astar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.megaman.MegaManVGLCUtil;
import edu.southwestern.tasks.megaman.astar.MegaManState.MegaManAction;
import edu.southwestern.util.search.AStarSearch;
import edu.southwestern.util.search.Search;

// Imports for debugging
//import java.util.HashSet;
//import java.awt.image.BufferedImage;
//import edu.southwestern.tasks.interactive.InteractiveGANLevelEvolutionTask;
//import edu.southwestern.tasks.interactive.megaman.MegaManGANLevelBreederTask;
//import edu.southwestern.tasks.megaman.MegaManRenderUtil;
//import edu.southwestern.util.MiscUtil;



public class MegaManStateTest {

	@Test
	public void test() throws IOException {
		Parameters.initializeParameterCollections(new String[] { "io:false", "netio:false", "recurrency:false"
				, "megaManAStarJumpHeight:4" });
		
		
		
		
		/**
		 * Below is a level that has the following layout:
		 * 						----------------
		 * 						----------------
		 * 						------#---------
		 * 						------#---------
		 * 						------#---------
		 * 						------#---------
		 * 						-----##---------
		 * 						----------------
		 * 						-P----#---------
		 * 						############--Z-
		 * 						-----------#####
		 * 						----------------
		 * 						----------------
		 * 						----------------
		 * 
		 * Make sure MegaMan can not slide here
		 */
		List<List<Integer>> level0 = MegaManVGLCUtil.convertMegamanVGLCtoListOfLists(MegaManVGLCUtil.MEGAMAN_MMLV_PATH+"MegaManStateTestHitsHead.txt"); 
		MegaManState start0 = new MegaManState(level0);
		Search<MegaManAction,MegaManState> search0 = new AStarSearch<>(MegaManState.orbHeuristic);
		//HashSet<MegaManState> mostRecentVisited = null;
		ArrayList<MegaManAction> actionSequence0 = null;
		try {
			//tries to find a solution path to solve the level, tries as many time as specified by the last int parameter 
			//represented by red x's in the visualization 
			//System.out.println(Parameters.parameters.integerParameter("megaManAStarJumpHeight"));
			actionSequence0 = ((AStarSearch<MegaManAction, MegaManState>) search0).search(start0, true, 10000000);
		} catch(Exception e) {
			System.out.println("failed search");
			e.printStackTrace();
		}
		if(actionSequence0 != null)
			for(MegaManAction a : actionSequence0) {
				System.out.println(a.getMove().toString());
			}
		assertTrue(actionSequence0==null);
		
		/**
		 * Below is a level that has the following layout:
		 * 						----------------
		 * 						----------------
		 * 						------#---------
		 * 						------#---------
		 * 						------#---------
		 * 						------#---------
		 * 						------#---------
		 * 						----------------
		 * 						-P----#---------
		 * 						############--Z-
		 * 						-----------#####
		 * 						----------------
		 * 						----------------
		 * 						----------------
		 * 
		 * Make sure MegaMan can not slide here
		 */
		List<List<Integer>> level1 = MegaManVGLCUtil.convertMegamanVGLCtoListOfLists(MegaManVGLCUtil.MEGAMAN_MMLV_PATH+"MegaManStateTest.txt"); 
		MegaManState start1 = new MegaManState(level1);
		System.out.println(MegaManState.orbHeuristic.h(start1));
		Search<MegaManAction,MegaManState> search1 = new AStarSearch<>(MegaManState.orbHeuristic);
		//HashSet<MegaManState> mostRecentVisited = null;
		ArrayList<MegaManAction> actionSequence1 = null;
		try {
			//tries to find a solution path to solve the level, tries as many time as specified by the last int parameter 
			//represented by red x's in the visualization 
			//System.out.println(Parameters.parameters.integerParameter("megaManAStarJumpHeight"));
			actionSequence1 = ((AStarSearch<MegaManAction, MegaManState>) search1).search(start1, true, 10000000);
		} catch(Exception e) {
			System.out.println("failed search");
			e.printStackTrace();
		}
		if(actionSequence1 != null)
			for(MegaManAction a : actionSequence1) {
				System.out.println(a.getMove().toString());
			}
		assertTrue(actionSequence1==null);
		//Testing the heuristic
		assertEquals(MegaManState.orbHeuristic.h(start1),13.0,.0000001);
		
		
		
		/**
		 * Below is a level that has the following layout:
		 * 						----------------
		 * 						----------------
		 * 						------#---------
		 * 						------#---------
		 * 						------#---------
		 * 						------#---------
		 * 						------#---------
		 * 						------#---------
		 * 						-P--------------
		 * 						############--Z-
		 * 						-----------#####
		 * 						----------------
		 * 						----------------
		 * 						----------------
		 * 
		 * Make sure MegaMan can slide here
		 */
		
		List<List<Integer>> level2 = MegaManVGLCUtil.convertMegamanVGLCtoListOfLists(MegaManVGLCUtil.MEGAMAN_MMLV_PATH+"MegaManStateTestTrue.txt"); 
		MegaManVGLCUtil.printLevel(level2);
		MegaManState start2 = new MegaManState(level2);
		Search<MegaManAction,MegaManState> search2 = new AStarSearch<>(MegaManState.orbHeuristic);
		//HashSet<MegaManState> mostRecentVisited = null;
		ArrayList<MegaManAction> actionSequence2 = null;
		try {
			//tries to find a solution path to solve the level, tries as many time as specified by the last int parameter 
			//represented by red x's in the visualization 
			//System.out.println(Parameters.parameters.integerParameter("megaManAStarJumpHeight"));
			actionSequence2 = ((AStarSearch<MegaManAction, MegaManState>) search2).search(start2, true, 10000000);
		} catch(Exception e) {
			System.out.println("failed search");
			e.printStackTrace();
		}
//		assertTrue(actionSequence2 != null);
		if(actionSequence2 != null)
			for(MegaManAction a : actionSequence2) {
				System.out.println(a.getMove().toString());
			}
		//System.out.println(actionSequence);
		Iterator<MegaManAction> itr2 = actionSequence2.iterator();
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr2.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr2.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr2.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr2.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr2.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr2.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr2.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr2.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr2.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr2.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr2.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr2.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr2.next());
		assertFalse(itr2.hasNext());
		
		assertEquals(MegaManState.orbHeuristic.h(start2),13.0,.0000001);

		
		//Level format, should fail
//		----------------
//		----------------
//		------#---------
//		------#---------
//		------#---------
//		------#---------
//		------#---------
//		------########--
//		-P-----------#--
//		############-#--
//		-----------#-#
//		-----------#Z#--
//		-----------###--
//		----------------
		List<List<Integer>> level3 = MegaManVGLCUtil.convertMegamanVGLCtoListOfLists(MegaManVGLCUtil.MEGAMAN_MMLV_PATH+"MegaManStateTestFalling.txt"); 
		MegaManState start3 = new MegaManState(level3);
		Search<MegaManAction,MegaManState> search3 = new AStarSearch<>(MegaManState.orbHeuristic);
		//HashSet<MegaManState> mostRecentVisited = null;
		ArrayList<MegaManAction> actionSequence3 = null;
		try {
			//tries to find a solution path to solve the level, tries as many time as specified by the last int parameter 
			//represented by red x's in the visualization 
			//System.out.println(Parameters.parameters.integerParameter("megaManAStarJumpHeight"));
			actionSequence3 = ((AStarSearch<MegaManAction, MegaManState>) search3).search(start3, true, 10000000);
		} catch(Exception e) {
			System.out.println("failed search");
			e.printStackTrace();
		}
		if(actionSequence3 != null)
			for(MegaManAction a : actionSequence3) {
				System.out.println(a.getMove().toString());
			}
		assertTrue(actionSequence3==null);
		
		assertEquals(MegaManState.orbHeuristic.h(start3),11.0,.0000001);
		
		
		//Level format, should pass
//		----------------
//		----------------
//		------#---------
//		------#---------
//		------#---------
//		------#---------
//		------#---------
//		------########--
//		-P-----------#--
//		###########--#--
//		-----------#-#
//		-----------#Z#--
//		-----------###--
//		----------------
		
		
		List<List<Integer>> level4 = MegaManVGLCUtil.convertMegamanVGLCtoListOfLists(MegaManVGLCUtil.MEGAMAN_MMLV_PATH+"MegaManStateTestFallingTrue.txt"); 
		MegaManVGLCUtil.printLevel(level4);
		MegaManState start4 = new MegaManState(level4);
		Search<MegaManAction,MegaManState> search4 = new AStarSearch<>(MegaManState.orbHeuristic);
		//Used for debugging, specifically for image creation
		//HashSet<MegaManState> mostRecentVisited = null;
		ArrayList<MegaManAction> actionSequence4 = null;
		try {
			//tries to find a solution path to solve the level, tries as many time as specified by the last int parameter 
			//represented by red x's in the visualization 
			//System.out.println(Parameters.parameters.integerParameter("megaManAStarJumpHeight"));
			actionSequence4 = ((AStarSearch<MegaManAction, MegaManState>) search4).search(start4, true, 10000000);
		} catch(Exception e) {
			System.out.println("failed search");
			e.printStackTrace();
		}
		//Also used for debugging, with previous Hashset
		//mostRecentVisited = ((AStarSearch<MegaManAction, MegaManState>) search4).getVisited();
		
//		assertTrue(actionSequence2 != null);
		if(actionSequence4 != null)
			for(MegaManAction a : actionSequence4) {
				System.out.println(a.getMove().toString());
			}
		//System.out.println(actionSequence);
		
		
		
		Iterator<MegaManAction> itr3 = actionSequence4.iterator();
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr3.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr3.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr3.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr3.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr3.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr3.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr3.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr3.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr3.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr3.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr3.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr3.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr3.next());
		
		assertEquals(MegaManState.orbHeuristic.h(start4),11.0,.0000001);

		assertFalse(itr3.hasNext());
		
		
		
		/**
		 * Below is a level that has the following layout:
		 * 						----------------
		 * 						----------------
		 * 						------#---------
		 * 						------#---------
		 * 						------#---------
		 * 						------#---------
		 * 						-----#----------
		 * 						----------------
		 * 						-P----#---------
		 * 						############--Z-
		 * 						-----------#####
		 * 						----------------
		 * 						----------------
		 * 						----------------
		 * 
		 * Make sure MegaMan can not jump-duck here
		 */
		List<List<Integer>> level5 = MegaManVGLCUtil.convertMegamanVGLCtoListOfLists(MegaManVGLCUtil.MEGAMAN_MMLV_PATH+"MegaManStateTestJumpDuck.txt"); 
		MegaManState start5 = new MegaManState(level5);
		Search<MegaManAction,MegaManState> search5 = new AStarSearch<>(MegaManState.orbHeuristic);
		//HashSet<MegaManState> mostRecentVisited = null;
		ArrayList<MegaManAction> actionSequence5 = null;
		try {
			//tries to find a solution path to solve the level, tries as many time as specified by the last int parameter 
			//represented by red x's in the visualization 
			//System.out.println(Parameters.parameters.integerParameter("megaManAStarJumpHeight"));
			actionSequence5 = ((AStarSearch<MegaManAction, MegaManState>) search5).search(start5, true, 10000000);
		} catch(Exception e) {
			System.out.println("failed search");
			e.printStackTrace();
		}
		if(actionSequence5 != null)
			for(MegaManAction a : actionSequence5) {
				System.out.println(a.getMove().toString());
			}
		assertTrue(actionSequence5==null);
		assertEquals(MegaManState.orbHeuristic.h(start5),13.0,.0000001);
		
		
		
		
		
		List<List<Integer>> level6 = MegaManVGLCUtil.convertMegamanVGLCtoListOfLists(MegaManVGLCUtil.MEGAMAN_MMLV_PATH+"MegaManStateTestJumpOnEnemies.txt"); 
		MegaManState start6 = new MegaManState(level6);
		Search<MegaManAction,MegaManState> search6 = new AStarSearch<>(MegaManState.orbHeuristic);
		//HashSet<MegaManState> mostRecentVisited = null;
		ArrayList<MegaManAction> actionSequence6 = null;
		try {
			//tries to find a solution path to solve the level, tries as many time as specified by the last int parameter 
			//represented by red x's in the visualization 
			//System.out.println(Parameters.parameters.integerParameter("megaManAStarJumpHeight"));
			actionSequence6 = ((AStarSearch<MegaManAction, MegaManState>) search6).search(start6, true, 10000000);
		} catch(Exception e) {
			System.out.println("failed search");
			e.printStackTrace();
		}
		if(actionSequence6 != null)
			for(MegaManAction a : actionSequence6) {
				System.out.println(a.getMove().toString());
			}
		assertTrue(actionSequence6==null);
		assertEquals(MegaManState.orbHeuristic.h(start6),13.0,.0000001);
		
		
		
//		-----------|##--
//		-----------|##--
//		-------|--####--
//		-------|----##--
//		------------##--
//		-------|######--
//		P------|--####--
//		##-----|--####--
//		-#-----|--------
//		-------|--------
//		-------|--------
//		----------------
//		---------------Z
//		--------########
		
		
		List<List<Integer>> level7 = MegaManVGLCUtil.convertMegamanVGLCtoListOfLists(MegaManVGLCUtil.MEGAMAN_MMLV_PATH+"MegaManStateTestOddLadderPlacementWhy.txt"); 
		MegaManState start7 = new MegaManState(level7);
		Search<MegaManAction,MegaManState> search7 = new AStarSearch<>(MegaManState.orbHeuristic);
		//HashSet<MegaManState> mostRecentVisited = null;
		ArrayList<MegaManAction> actionSequence7 = null;
		try {
			//tries to find a solution path to solve the level, tries as many time as specified by the last int parameter 
			//represented by red x's in the visualization 
			//System.out.println(Parameters.parameters.integerParameter("megaManAStarJumpHeight"));
			actionSequence7 = ((AStarSearch<MegaManAction, MegaManState>) search7).search(start7, true, 10000000);
		} catch(Exception e) {
			System.out.println("failed search");
			e.printStackTrace();
		}
		if(actionSequence7 != null)
			for(MegaManAction a : actionSequence7) {
				System.out.println(a.getMove().toString());
			}
		assertTrue(actionSequence7==null);
		assertEquals(MegaManState.orbHeuristic.h(start7),15.0,.0000001);
		
		
		
		
		
//		-----------|##--
//		-----------|##--
//		-------|--####--
//		-------|----##--
//		##----------##--
//		------|######---
//		P-----|--####---
//		##----|--####---
//		-#----|---------
//		------|---------
//		------|---------
//		----------------
//		---------------Z
//		--------########
		
		List<List<Integer>> level8 = MegaManVGLCUtil.convertMegamanVGLCtoListOfLists(MegaManVGLCUtil.MEGAMAN_MMLV_PATH+"MegaManStateTestProperJumpingWithSomethingAboveMegaMan.txt");
		//MegaManVGLCUtil.printLevel(level8);
		MegaManState start8 = new MegaManState(level8);
		Search<MegaManAction,MegaManState> search8 = new AStarSearch<>(MegaManState.orbHeuristic);
		//HashSet<MegaManState> mostRecentVisited = null;
		ArrayList<MegaManAction> actionSequence8 = null;
		try {
			//tries to find a solution path to solve the level, tries as many time as specified by the last int parameter 
			//represented by red x's in the visualization 
			//System.out.println(Parameters.parameters.integerParameter("megaManAStarJumpHeight"));
			actionSequence8 = ((AStarSearch<MegaManAction, MegaManState>) search8).search(start8, true, 10000000);
		} catch(Exception e) {
			System.out.println("failed search");
			e.printStackTrace();
		}
		if(actionSequence8 != null)
			for(MegaManAction a : actionSequence8) {
				System.out.println(a.getMove().toString());
			}
		assertTrue(actionSequence8!=null);
			

		
		Iterator<MegaManAction> itr8 = actionSequence8.iterator();
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr8.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr8.next());

		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr8.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr8.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr8.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr8.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr8.next());

		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr8.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr8.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr8.next());

		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr8.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr8.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr8.next());

		assertEquals(new MegaManAction(MegaManAction.MOVE.LEFT), itr8.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr8.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr8.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr8.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr8.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr8.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr8.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr8.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr8.next());
		assertFalse(itr8.hasNext());
		assertEquals(MegaManState.orbHeuristic.h(start8),15.0,.0000001);
		

		
		
		
//		----------------
//		----------------
//		----------------
//		----------------
//		----------------
//		########--------
//		-----|#---------
//		-----|#---------
//		-P----#---------
//		############--Z-
//		-----------#####
//		----------------
//		----------------
//		----------------
		
		
		List<List<Integer>> level9 = MegaManVGLCUtil.convertMegamanVGLCtoListOfLists(MegaManVGLCUtil.MEGAMAN_MMLV_PATH+"MegaManOddLadderUpBug.txt"); 
		MegaManState start9 = new MegaManState(level9);
		Search<MegaManAction,MegaManState> search9 = new AStarSearch<>(MegaManState.orbHeuristic);
		//HashSet<MegaManState> mostRecentVisited = null;
		ArrayList<MegaManAction> actionSequence9 = null;
		try {
			//tries to find a solution path to solve the level, tries as many time as specified by the last int parameter 
			//represented by red x's in the visualization 
			//System.out.println(Parameters.parameters.integerParameter("megaManAStarJumpHeight"));
			actionSequence9 = ((AStarSearch<MegaManAction, MegaManState>) search9).search(start9, true, 10000000);
		} catch(Exception e) {
			System.out.println("failed search");
			e.printStackTrace();
		}
		if(actionSequence9 != null)
			for(MegaManAction a : actionSequence9) {
				System.out.println(a.getMove().toString());
			}
		assertTrue(actionSequence9==null);
		assertEquals(MegaManState.orbHeuristic.h(start9),13.0,.0000001);
		
		
//		----------------
//		----------------
//		------####------
//		--####----------
//		----------------
//		#####-##--------
//		-----|#---------
//		-----|#---------
//		-P----#---------
//		############--Z-
//		-----------#####
//		----------------
//		----------------
//		----------------

		List<List<Integer>> level10 = MegaManVGLCUtil.convertMegamanVGLCtoListOfLists(MegaManVGLCUtil.MEGAMAN_MMLV_PATH+"MegaManStateTestNoClimbAndSlide.txt"); 
		MegaManState start10 = new MegaManState(level10);
		Search<MegaManAction,MegaManState> search10 = new AStarSearch<>(MegaManState.orbHeuristic);
		//HashSet<MegaManState> mostRecentVisited = null;
		ArrayList<MegaManAction> actionSequence10 = null;
		try {
			//tries to find a solution path to solve the level, tries as many time as specified by the last int parameter 
			//represented by red x's in the visualization 
			//System.out.println(Parameters.parameters.integerParameter("megaManAStarJumpHeight"));
			actionSequence10 = ((AStarSearch<MegaManAction, MegaManState>) search10).search(start10, true, 10000000);
		} catch(Exception e) {
			System.out.println("failed search");
			e.printStackTrace();
		}
		if(actionSequence10 != null)
			for(MegaManAction a : actionSequence10) {
				System.out.println(a.getMove().toString());
			}
		assertTrue(actionSequence10==null);
		assertEquals(MegaManState.orbHeuristic.h(start10),13.0,.0000001);
		
		
		
//		----------------
//		----------------
//		----------------
//		----------------
//		----------------
//		#####-##--------
//		-----|#---------
//		---##|#---------
//		-P----#---------
//		############--Z-
//		-----------#####
//		----------------
//		----------------
//		----------------
//		
		List<List<Integer>> level11 = MegaManVGLCUtil.convertMegamanVGLCtoListOfLists(MegaManVGLCUtil.MEGAMAN_MMLV_PATH+"MegaManStateTestNoSlideAndCatchLadder.txt"); 
		MegaManState start11 = new MegaManState(level11);
		Search<MegaManAction,MegaManState> search11 = new AStarSearch<>(MegaManState.orbHeuristic);
		//HashSet<MegaManState> mostRecentVisited = null;
		ArrayList<MegaManAction> actionSequence11 = null;
		try {
			//tries to find a solution path to solve the level, tries as many time as specified by the last int parameter 
			//represented by red x's in the visualization 
			//System.out.println(Parameters.parameters.integerParameter("megaManAStarJumpHeight"));
			actionSequence11 = ((AStarSearch<MegaManAction, MegaManState>) search11).search(start11, true, 11000000);
		} catch(Exception e) {
			System.out.println("failed search");
			e.printStackTrace();
		}
		if(actionSequence11 != null)
			for(MegaManAction a : actionSequence11) {
				System.out.println(a.getMove().toString());
			}
		assertTrue(actionSequence11==null);
		assertEquals(MegaManState.orbHeuristic.h(start11),13.0,.0000001);
		
		
		
//		----------------
//		----------------
//		------####------
//		--####----------
//		----------------
//		----------------
//		#####B###B######
//		------#---------
//		-P----#---------
//		############--Z-
//		-----------#####
//		----------------
//		----------------
//		----------------
		
		List<List<Integer>> level12 = MegaManVGLCUtil.convertMegamanVGLCtoListOfLists(MegaManVGLCUtil.MEGAMAN_MMLV_PATH+"MegaManCannotJumpThroughBreakableBlocks.txt"); 
		MegaManState start12 = new MegaManState(level12);
		Search<MegaManAction,MegaManState> search12 = new AStarSearch<>(MegaManState.orbHeuristic);
		//HashSet<MegaManState> mostRecentVisited = null;
		ArrayList<MegaManAction> actionSequence12 = null;
		try {
			//tries to find a solution path to solve the level, tries as many time as specified by the last int parameter 
			//represented by red x's in the visualization 
			//System.out.println(Parameters.parameters.integerParameter("megaManAStarJumpHeight"));
			actionSequence12 = ((AStarSearch<MegaManAction, MegaManState>) search12).search(start12, true, 12000000);
		} catch(Exception e) {
			System.out.println("failed search");
			e.printStackTrace();
		}
		if(actionSequence12 != null)
			for(MegaManAction a : actionSequence12) {
				System.out.println(a.getMove().toString());
			}
		assertTrue(actionSequence12==null);
		assertEquals(MegaManState.orbHeuristic.h(start12),13.0,.0000001);
		//MegaManCannotJumpThroughBreakableBlocks
		
		
		
//		----------------
//		----------------
//		------####------
//		--####----------
//		-----|#---------
//		-----|#---------
//		-----|----------
//		-----|#---------
//		-P---|#---------
//		############--Z-
//		-----------#####
//		----------------
//		----------------
//		----------------
		
		
		
		List<List<Integer>> level13 = MegaManVGLCUtil.convertMegamanVGLCtoListOfLists(MegaManVGLCUtil.MEGAMAN_MMLV_PATH+"MegaManCannotSlideFromLadder.txt"); 
		MegaManState start13 = new MegaManState(level13);
		Search<MegaManAction,MegaManState> search13 = new AStarSearch<>(MegaManState.orbHeuristic);
		//HashSet<MegaManState> mostRecentVisited = null;
		ArrayList<MegaManAction> actionSequence13 = null;
		try {
			//tries to find a solution path to solve the level, tries as many time as specified by the last int parameter 
			//represented by red x's in the visualization 
			//System.out.println(Parameters.parameters.integerParameter("megaManAStarJumpHeight"));
			actionSequence13 = ((AStarSearch<MegaManAction, MegaManState>) search13).search(start13, true, 13000000);
		} catch(Exception e) {
			System.out.println("failed search");
			e.printStackTrace();
		}
		if(actionSequence13 != null)
			for(MegaManAction a : actionSequence13) {
				System.out.println(a.getMove().toString());
			}
		assertTrue(actionSequence13==null);
		assertEquals(MegaManState.orbHeuristic.h(start13),13.0,.0000001);
		
		
		
		List<List<Integer>> level14 = MegaManVGLCUtil.convertMegamanVGLCtoListOfLists(MegaManVGLCUtil.MEGAMAN_MMLV_PATH+"MegaManStateTestNoHazardSlidingBug.txt"); 
		MegaManState start14 = new MegaManState(level14);
		Search<MegaManAction,MegaManState> search14 = new AStarSearch<>(MegaManState.orbHeuristic);
		//HashSet<MegaManState> mostRecentVisited = null;
		ArrayList<MegaManAction> actionSequence14 = null;
		try {
			//tries to find a solution path to solve the level, tries as many time as specified by the last int parameter 
			//represented by red x's in the visualization 
			//System.out.println(Parameters.parameters.integerParameter("megaManAStarJumpHeight"));
			actionSequence14 = ((AStarSearch<MegaManAction, MegaManState>) search14).search(start14, true, 14000000);
		} catch(Exception e) {
			System.out.println("failed search");
			e.printStackTrace();
		}
		if(actionSequence14 != null)
			for(MegaManAction a : actionSequence14) {
				System.out.println(a.getMove().toString());
			}
		assertTrue(actionSequence14==null);
		assertEquals(MegaManState.orbHeuristic.h(start14),15.0,.0000001);
		
		//MegaManStateTestNoHazardSlidingBug.txt
//		MegaManCannotSlideFromLadder
		//MegaManStateTestNoSlideAndCatchLadder.txt
//		RIGHT
//		RIGHT
//		JUMP
//		RIGHT
//		RIGHT
//		RIGHT
//		RIGHT
//		RIGHT
//		RIGHT
//		RIGHT
//		RIGHT
//		RIGHT
		//
//		List<List<Integer>> level = MegaManVGLCUtil.convertMegamanVGLCtoListOfLists(MegaManVGLCUtil.MEGAMAN_LEVEL_PATH+"megaman_1_"+1+".txt"); //converts to JSON
//		//MegaManVGLCUtil.printLevel(level);
//		MegaManState start = new MegaManState(level);
//		Search<MegaManAction,MegaManState> search = new AStarSearch<>(MegaManState.manhattanToOrb);
////		HashSet<MegaManState> mostRecentVisited = null;
//		ArrayList<MegaManAction> actionSequence = null;
//		try {
//			//tries to find a solution path to solve the level, tries as many time as specified by the last int parameter 
//			//represented by red x's in the visualization 
//			//System.out.println(Parameters.parameters.integerParameter("megaManAStarJumpHeight"));
//			actionSequence = ((AStarSearch<MegaManAction, MegaManState>) search).search(start, true, 10000000);
//		} catch(Exception e) {
//			System.out.println("failed search");
//			e.printStackTrace();
//		}
//		//get all of the visited states, all of the x's are in this set but the white ones are not part of solution path 
//			for(MegaManAction a : actionSequence)
//				System.out.println(a.getMove().toString());
//		//System.out.println(actionSequence);
//		Iterator<MegaManAction> itr = actionSequence.iterator();
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.LEFT), itr.next());
//
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//
//
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//		
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.LEFT), itr.next());
//		
//		
//		
//		
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//	
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//		
//		
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//
//		
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//
//		assertEquals(new MegaManAction(MegaManAction.MOVE.LEFT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//
//
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.LEFT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//
//		assertEquals(new MegaManAction(MegaManAction.MOVE.LEFT), itr.next());
//		
//		
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//		
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//
//
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//
//		
//		assertEquals(new MegaManAction(MegaManAction.MOVE.LEFT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//
//		
//		assertEquals(new MegaManAction(MegaManAction.MOVE.LEFT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.UP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.UP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.UP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.UP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.UP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.UP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.LEFT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.LEFT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.LEFT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.LEFT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.LEFT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.LEFT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.LEFT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//
//		assertEquals(new MegaManAction(MegaManAction.MOVE.LEFT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.LEFT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.LEFT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.LEFT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.LEFT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.LEFT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.UP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.UP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		
//		
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.LEFT), itr.next());
//
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.LEFT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.LEFT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//
//		assertEquals(new MegaManAction(MegaManAction.MOVE.UP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.UP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.UP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.UP), itr.next());
//
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//
//
//		
//		assertEquals(new MegaManAction(MegaManAction.MOVE.LEFT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.UP), itr.next());
//
//		assertEquals(new MegaManAction(MegaManAction.MOVE.UP), itr.next());
//
//		assertEquals(new MegaManAction(MegaManAction.MOVE.LEFT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.LEFT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.LEFT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.LEFT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.LEFT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.LEFT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.LEFT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.LEFT), itr.next());
//
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.LEFT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.LEFT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.LEFT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.LEFT), itr.next());
//
//		assertEquals(new MegaManAction(MegaManAction.MOVE.UP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.UP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.UP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.UP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.UP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.UP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.UP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.UP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.UP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.UP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.UP), itr.next());
//
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//
//		assertEquals(new MegaManAction(MegaManAction.MOVE.LEFT), itr.next());
//
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//
//		
//		assertEquals(new MegaManAction(MegaManAction.MOVE.LEFT), itr.next());
//
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//
//
//		assertEquals(new MegaManAction(MegaManAction.MOVE.UP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.UP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.UP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.UP), itr.next());
//		
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		
//		
//		assertEquals(new MegaManAction(MegaManAction.MOVE.LEFT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.LEFT), itr.next());
//
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//
//		assertEquals(new MegaManAction(MegaManAction.MOVE.LEFT), itr.next());
//
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		
//		
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		
//
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
//
//
//		
//		
//		
//		
//		
//		
//		
//		
//		
//		
//
//
//		assertFalse(itr.hasNext());
	
	}

}


//FOR TROUBLESHOOTING:
//Will generate an image with the solution path. Also will wait for key press to continue the code to ensure correct spot is looked at.
//For this to work, like 6 things need to be imported, all of them are with the imports, commented out

//BufferedImage level4Image = MegaManState.vizualizePath(level4,mostRecentVisited,actionSequence4,start4);
//MegaManRenderUtil.displayBufferedImage(level4, level4Image);
//MiscUtil.waitForReadStringAndEnterKeyPress();
