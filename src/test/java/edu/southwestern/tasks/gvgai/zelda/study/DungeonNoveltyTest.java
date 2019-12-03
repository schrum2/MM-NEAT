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
	
	/**
	 * The novelty of any given dungeon should be the same every time.
	 */
	@Test
	public void testVerifyConsistency() {
		String[] names = new String[] {"tloz1_1_flip", "tloz2_1_flip", "tloz3_1_flip", "tloz4_1_flip", "tloz5_1_flip", "tloz6_1_flip", "tloz7_1_flip", "tloz8_1_flip", "tloz9_1_flip"};
		for(String name: names) {
			Dungeon dungeon = LoadOriginalDungeon.loadOriginalDungeon(name);
			double result1 = DungeonNovelty.averageDungeonNovelty(dungeon);		
			// Reloading the dungeon somehow subtly changes the representation leading to a very slightly different novelty calculation!
			dungeon = LoadOriginalDungeon.loadOriginalDungeon(name);
			double result2 = DungeonNovelty.averageDungeonNovelty(dungeon);		
			assertEquals(result1, result2, 0.0);
		}
	}
	
	@Test
	public void verifyRoomConsistency() {
		// Make sure rooms are always in some order with the same novelty values
		Dungeon dungeon = LoadOriginalDungeon.loadOriginalDungeon("tloz2_1_flip");
		double[] result1 = DungeonNovelty.roomNovelties(dungeon.getNodes());	
		dungeon = LoadOriginalDungeon.loadOriginalDungeon("tloz2_1_flip");
		double[] result2 = DungeonNovelty.roomNovelties(dungeon.getNodes());	

		for(int i = 0; i < result1.length; i++) {
			assertEquals(result1[i],result2[i],0);
		}
	}

}
