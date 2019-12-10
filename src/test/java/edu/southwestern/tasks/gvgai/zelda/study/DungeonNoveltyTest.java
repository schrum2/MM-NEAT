package edu.southwestern.tasks.gvgai.zelda.study;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.gvgai.zelda.ZeldaVGLCUtil;
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

	@Test
	public void testVerifyConsistencyAcrossRepresentations() {
		// Remove level 4-1 because we artificially added an extra room
		String[] names = new String[] {"tloz1_1_flip", "tloz2_1_flip", "tloz3_1_flip", /**"tloz4_1_flip",**/ "tloz5_1_flip", "tloz6_1_flip", "tloz7_1_flip", "tloz8_1_flip", "tloz9_1_flip"};
		for(String name: names) {
			Dungeon dungeon = LoadOriginalDungeon.loadOriginalDungeon(name);
			double result1 = DungeonNovelty.averageDungeonNovelty(dungeon);		

			String file = name+".txt";
			List<List<List<Integer>>> roomList = ZeldaVGLCUtil.convertZeldaLevelFileVGLCtoListOfRooms(ZeldaVGLCUtil.ZELDA_LEVEL_PATH+file);
			double result2 = DungeonNovelty.averageRoomNovelty(roomList);		
			// Small epsilon allowed because different addition orders cause floating point discrepancies
			System.out.println(name);
			assertEquals(result1, result2, 0.00000001);
		}
	}

	@Test
	public void verifyRoomConsistencyAcrossRepresentations() {

		// Remove level 4-1 because we artificially added an extra room
		// TODO: Crashes at level 3 currently, seemingly because of movable blocks
		String[] names = new String[] {"tloz1_1_flip", "tloz2_1_flip", "tloz3_1_flip", /**"tloz4_1_flip",**/ "tloz5_1_flip", "tloz6_1_flip", "tloz7_1_flip", "tloz8_1_flip", "tloz9_1_flip"};
		for(String name: names) {

			// Make sure room calculations are the same no matter how they are loaded
			Dungeon dungeon = LoadOriginalDungeon.loadOriginalDungeon(name);

			List<Node> nodes = dungeon.getNodes();
			double[] result1 = DungeonNovelty.roomNovelties(nodes);	

			String file = name+".txt";
			List<List<List<Integer>>> roomList = ZeldaVGLCUtil.convertZeldaLevelFileVGLCtoListOfRooms(ZeldaVGLCUtil.ZELDA_LEVEL_PATH+file);
			double[] result2 = DungeonNovelty.roomNovelties(roomList);		

			// These two different loading methods put the rooms in different orders, so manual reorganization is needed
			Arrays.sort(result1);
			Arrays.sort(result2);

			assertEquals(result1.length, result2.length);
			for(int i = 0; i < result1.length; i++) {
				System.out.println(result1[i] + "\t" + result2[i]); 
			}
			System.out.println(name);
			for(int i = 0; i < result1.length; i++) {
				System.out.println(i);
				// Small epsilon allowed because different addition orders cause floating point discrepancies
				assertEquals(result1[i],result2[i],0.00000001);
			}
		}
	}

}
