package edu.southwestern.tasks.megaman.astar;

import static org.junit.Assert.*;

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
		List<List<Integer>> level = MegaManVGLCUtil.convertMegamanVGLCtoListOfLists(MegaManVGLCUtil.MEGAMAN_LEVEL_PATH+"megaman_1_"+1+".txt"); //converts to JSON
		//MegaManVGLCUtil.printLevel(level);
		MegaManState start = new MegaManState(level);
		Search<MegaManAction,MegaManState> search = new AStarSearch<>(MegaManState.manhattanToOrb);
		//HashSet<MegaManState> mostRecentVisited = null;
		ArrayList<MegaManAction> actionSequence = null;
		try {
			//tries to find a solution path to solve the level, tries as many time as specified by the last int parameter 
			//represented by red x's in the visualization 
			//System.out.println(Parameters.parameters.integerParameter("megaManAStarJumpHeight"));
			actionSequence = ((AStarSearch<MegaManAction, MegaManState>) search).search(start, true, 10000000);
		} catch(Exception e) {
			System.out.println("failed search");
			e.printStackTrace();
		}
		//get all of the visited states, all of the x's are in this set but the white ones are not part of solution path 
		//mostRecentVisited = ((AStarSearch<MegaManAction, MegaManState>) search).getVisited();
		
		
		if(actionSequence != null)
			for(MegaManAction a : actionSequence)
				System.out.println(a.getMove().toString());
		//System.out.println(actionSequence);
		Iterator<MegaManAction> itr = actionSequence.iterator();
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());

		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());

		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());

		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.LEFT), itr.next());

		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.LEFT), itr.next());

		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());

		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());

		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
		
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.LEFT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());

		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
		
		
		
		
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
		
		
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.LEFT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());

		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());

		assertEquals(new MegaManAction(MegaManAction.MOVE.LEFT), itr.next());


		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());

		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
		
		
		assertEquals(new MegaManAction(MegaManAction.MOVE.LEFT), itr.next());
		
		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());

		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());

		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.LEFT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());


		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.LEFT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());

		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());

		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());

		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());

		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());

		
		assertEquals(new MegaManAction(MegaManAction.MOVE.LEFT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());

		
		assertEquals(new MegaManAction(MegaManAction.MOVE.LEFT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.UP), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.UP), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.UP), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.UP), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.UP), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.UP), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.LEFT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.LEFT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.LEFT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.LEFT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.LEFT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.LEFT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.LEFT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());

		assertEquals(new MegaManAction(MegaManAction.MOVE.LEFT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.LEFT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.LEFT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.LEFT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.LEFT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.LEFT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.UP), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.UP), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());

		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		
		
		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());

		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());

		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());

		assertEquals(new MegaManAction(MegaManAction.MOVE.LEFT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());

		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());

		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		
		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());

		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.LEFT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		
		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.LEFT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
		
		
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.UP), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.UP), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.UP), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.UP), itr.next());
		
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());

		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());

		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		
		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());

		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());

		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());

		
		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());

		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());

		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());

		assertEquals(new MegaManAction(MegaManAction.MOVE.UP), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.UP), itr.next());

		assertEquals(new MegaManAction(MegaManAction.MOVE.LEFT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.LEFT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.LEFT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.LEFT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.LEFT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.LEFT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.LEFT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.LEFT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());

		assertEquals(new MegaManAction(MegaManAction.MOVE.LEFT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.LEFT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.LEFT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.LEFT), itr.next());

		assertEquals(new MegaManAction(MegaManAction.MOVE.UP), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.UP), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.UP), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.UP), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.UP), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.UP), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.UP), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.UP), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.UP), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.UP), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.UP), itr.next());
		
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());

		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.LEFT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		
		
		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());

		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());

		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.LEFT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());

		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());

		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());

		assertEquals(new MegaManAction(MegaManAction.MOVE.UP), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.UP), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.UP), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.UP), itr.next());


		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());

		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());

		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());

		assertEquals(new MegaManAction(MegaManAction.MOVE.LEFT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		

		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.JUMP), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());
		assertEquals(new MegaManAction(MegaManAction.MOVE.RIGHT), itr.next());


		assertFalse(itr.hasNext());
	
	}

}
