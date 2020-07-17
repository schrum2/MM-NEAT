package edu.southwestern.tasks.megaman.astar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.megaman.MegaManVGLCUtil;
import edu.southwestern.tasks.megaman.astar.MegaManState.MegaManAction;
import edu.southwestern.util.search.AStarSearch;
import edu.southwestern.util.search.Search;

public class MegaManStateTest {

	@Test
	public void test() {
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
		Search<MegaManAction,MegaManState> search1 = new AStarSearch<>(MegaManState.manhattanToOrb);
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
		Search<MegaManAction,MegaManState> search2 = new AStarSearch<>(MegaManState.manhattanToOrb);
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
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr2.next());
		assertFalse(itr2.hasNext());

		
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
		Search<MegaManAction,MegaManState> search3 = new AStarSearch<>(MegaManState.manhattanToOrb);
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
		Search<MegaManAction,MegaManState> search4 = new AStarSearch<>(MegaManState.manhattanToOrb);
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
		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr3.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr3.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr3.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr3.next());
		assertFalse(itr3.hasNext());
		
		
		
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
