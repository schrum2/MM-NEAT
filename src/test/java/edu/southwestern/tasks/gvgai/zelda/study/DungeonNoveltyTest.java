package edu.southwestern.tasks.gvgai.zelda.study;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.gvgai.zelda.ZeldaVGLCUtil;
import edu.southwestern.tasks.gvgai.zelda.dungeon.Dungeon;
import edu.southwestern.tasks.gvgai.zelda.dungeon.Dungeon.Node;
import edu.southwestern.tasks.gvgai.zelda.dungeon.LoadOriginalDungeon;

public class DungeonNoveltyTest {

	static Dungeon originalFour;
	static List<List<List<Integer>>> listOfRooms;
	static final int NEIGHBORS = 10;
	static final double EPSILON = 0.00001;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		MMNEAT.clearClasses();
		// P now maps to 5, but was 2 when the text was developed
		Parameters.initializeParameterCollections(new String[] {"zeldaVGLCWaterPMapCode:2","randomSeed:2"});
		originalFour = LoadOriginalDungeon.loadOriginalDungeon("tloz4_1_flip");
		listOfRooms = new LinkedList<>();
		for(Node x : originalFour.getLevels().values()) {
			listOfRooms.add(x.level.getLevel());
		}
	}

	@Test
	public void testNodeToNodeNovelty() {
		// Check to see if the novelty of the same rooms are 0.0
		for(int i = 0; i < listOfRooms.size(); i++)
			assertEquals(0.0, DungeonNovelty.roomDistance(listOfRooms.get(i), listOfRooms.get(i)), EPSILON);
	}

	/**
	 * The novelty of any given dungeon should be the same every time. Load each dungeon twice and make sure average novelty is consistent.
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

	/**
	 * The loading process should not affect calculation of room novelty. This test
	 * loads the same dungeon twice and does a room by room comparison of novelty calculations.
	 */
	@Test
	public void verifyRoomConsistency() {
		// Make sure rooms are always in some order with the same novelty values
		Dungeon dungeon = LoadOriginalDungeon.loadOriginalDungeon("tloz2_1_flip");
		List<List<List<Integer>>> rooms = new LinkedList<>();
		for(Node x : dungeon.getLevels().values()) {
			rooms.add(x.level.getLevel());
		}
		double[] result1 = DungeonNovelty.roomNovelties(rooms);	
		dungeon = LoadOriginalDungeon.loadOriginalDungeon("tloz2_1_flip");
		rooms = new LinkedList<>();
		for(Node x : dungeon.getLevels().values()) {
			rooms.add(x.level.getLevel());
		}
		double[] result2 = DungeonNovelty.roomNovelties(rooms);	

		for(int i = 0; i < result1.length; i++) {
			assertEquals(result1[i],result2[i],0);
		}
	}

	/**
	 * The Dungeon representation we use to play the game and the original raw VGLC representation should lead to the same
	 * novelty calculations. Using the raw data is necessary for levels that haven't been adapted to our custom Dungeon format yet.
	 */
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

	/**
	 * Compares Dungeon format to raw VGLC format for individual rooms. However, because rooms can be loaded in different orders,
	 * this calculation can lead to small floating point discrepancies. Therefore, the individual room novelties are calculated
	 * and sorted. Then the sorted novelties are compared, with some small wiggle room in the epsilon value for comparison.
	 */
	@Test
	public void verifyRoomConsistencyAcrossRepresentations() {

		// Remove level 4-1 because we artificially added an extra room
		// TODO: Crashes at level 5 currently, seemingly because of movable blocks
		String[] names = new String[] {"tloz1_1_flip", "tloz2_1_flip", "tloz3_1_flip", /**"tloz4_1_flip",**/ "tloz5_1_flip", "tloz6_1_flip", "tloz7_1_flip", "tloz8_1_flip", "tloz9_1_flip"};
		for(String name: names) {

			// Make sure room calculations are the same no matter how they are loaded
			Dungeon dungeon = LoadOriginalDungeon.loadOriginalDungeon(name);
			List<List<List<Integer>>> rooms = new LinkedList<>();
			for(Node x : dungeon.getLevels().values()) {
				rooms.add(x.level.getLevel());
			}
			double[] result1 = DungeonNovelty.roomNovelties(rooms);	

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
