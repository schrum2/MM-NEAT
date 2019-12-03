package edu.southwestern.tasks.gvgai.zelda.study;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.gvgai.zelda.dungeon.Dungeon;
import edu.southwestern.tasks.gvgai.zelda.dungeon.Dungeon.Node;
import edu.southwestern.tasks.gvgai.zelda.dungeon.LoadOriginalDungeon;

public class DungeonNoveltyTest {
	
	static Dungeon originalFour;
	static List<Node> listOfRooms;
	static final int NEIGHBORS = 10;
	static final double EPSILON = 0.00001;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Parameters.initializeParameterCollections(new String[] {});
		originalFour = LoadOriginalDungeon.loadOriginalDungeon("tloz4_1_flip");
		listOfRooms = originalFour.getNodes();
	}

	@Test
	public void testNodeToNodeNovelty() {
		// Check to see if the novelty of the same rooms are 0.0
		for(int i = 0; i < listOfRooms.size(); i++)
			assertEquals(0.0, DungeonNovelty.roomDistance(listOfRooms.get(i), listOfRooms.get(i)), EPSILON);
	}

	@Test
	public void testDungeonNovelty() {
		fail("Not yet implemented");
	}

}
