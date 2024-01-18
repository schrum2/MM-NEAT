package edu.southwestern.tasks.zelda;

import static org.junit.Assert.assertEquals;

import java.awt.Point;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.ContainerGenotype;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.scores.Score;
import edu.southwestern.tasks.gvgai.zelda.ZeldaVGLCUtil;
import edu.southwestern.tasks.gvgai.zelda.dungeon.Dungeon;
import edu.southwestern.tasks.gvgai.zelda.dungeon.DungeonUtil;
import edu.southwestern.tasks.gvgai.zelda.dungeon.ZeldaDungeon.Level;
import edu.southwestern.tasks.gvgai.zelda.level.ZeldaLevelUtil;
import me.jakerg.rougelike.Tile;

public class ZeldaDungeonDirectEncodingTaskTest {

	@Before
	public void setUp() throws Exception {
		MMNEAT.clearClasses();
		Parameters.parameters = null;
		MMNEAT.usingDiversityBinningScheme = false;
		// TODO: Set the initial parameters here, as in the main method of ZeldaDungeonDirectEncodingTask
		MMNEAT mmneat = new MMNEAT("runNumber:0 randomSeed:0 watch:false zeldaCPPN2GANSparseKeys:true trackPseudoArchive:false zeldaDungeonDistanceFitness:false zeldaDungeonFewRoomFitness:false zeldaDungeonTraversedRoomFitness:false zeldaPercentDungeonTraversedRoomFitness:false zeldaDungeonRandomFitness:false zeldaDungeonBackTrackRoomFitness:true trials:1 mu:10 io:false netio:false cleanOldNetworks:false zeldaGANUsesOriginalEncoding:false task:edu.southwestern.tasks.zelda.ZeldaDungeonDirectEncodingTask".split(" "));
		
		MMNEAT.loadClasses();
//		Parameters.initializeParameterCollections(new String[] { "io:false", "netio:false", "recurrency:false"});//TODO
//		MMNEAT.loadClasses();
	}
	
	@After
	public void tearDown() throws Exception {
		MMNEAT.clearClasses();
	}
	
	@Test
	public void BackTrackingFitnessTest() {
		// TODO: Copy-paste code
		ZeldaDungeonDirectEncodingTask task = new ZeldaDungeonDirectEncodingTask();
		
		@SuppressWarnings("unchecked")
		List<List<Integer>>[][] levelAsListsGrid = new List[2][2];
		ZeldaLevelUtil.makeEmptyRoom(levelAsListsGrid, 0, 0);
		ZeldaLevelUtil.makeEmptyRoom(levelAsListsGrid, 1, 0);
		ZeldaLevelUtil.makeEmptyRoom(levelAsListsGrid, 0, 1);
		ZeldaLevelUtil.makeEmptyRoom(levelAsListsGrid, 1, 1);
		int y=ZeldaVGLCUtil.ZELDA_ROOM_ROWS-1;
		
		levelAsListsGrid[0][0].get(4).set(14,Tile.DOOR.getNum());
		levelAsListsGrid[0][0].get(5).set(14,Tile.DOOR.getNum());
		levelAsListsGrid[0][0].get(6).set(14,Tile.DOOR.getNum());
		levelAsListsGrid[0][1].get(4).set(1,Tile.DOOR.getNum());
		levelAsListsGrid[0][1].get(5).set(1,Tile.DOOR.getNum());
		levelAsListsGrid[0][1].get(6).set(1,Tile.DOOR.getNum());
		levelAsListsGrid[0][0].get(y-1).set(8,Tile.LOCKED_DOOR.getNum());
		levelAsListsGrid[0][0].get(y-1).set(7,Tile.LOCKED_DOOR.getNum());
		levelAsListsGrid[1][0].get(1).set(8,Tile.LOCKED_DOOR.getNum());
		levelAsListsGrid[1][0].get(1).set(7,Tile.LOCKED_DOOR.getNum());
		levelAsListsGrid[1][0].get(4).set(14,Tile.DOOR.getNum());
		levelAsListsGrid[1][0].get(5).set(14,Tile.DOOR.getNum());
		levelAsListsGrid[1][0].get(6).set(14,Tile.DOOR.getNum());
		levelAsListsGrid[1][1].get(4).set(1,Tile.DOOR.getNum());
		levelAsListsGrid[1][1].get(5).set(1,Tile.DOOR.getNum());
		levelAsListsGrid[1][1].get(6).set(1,Tile.DOOR.getNum());
		levelAsListsGrid[1][1].get(1).set(8,Tile.DOOR.getNum());
		levelAsListsGrid[1][1].get(1).set(7,Tile.DOOR.getNum());
		levelAsListsGrid[0][1].get(y-1).set(8,Tile.DOOR.getNum());
		levelAsListsGrid[0][1].get(y-1).set(7,Tile.DOOR.getNum());
		for(int x = 0; x<ZeldaVGLCUtil.ZELDA_ROOM_COLUMNS;x++) {
			if(y>=0) {
			levelAsListsGrid[0][1].get(y).set(x,Tile.WALL.getNum());
			y--;
			}
		}
		levelAsListsGrid[0][1].get(2).set(6,Tile.WALL.getNum());
		levelAsListsGrid[0][1].get(5).set(3, Tile.KEY.getNum());
		
		
		// Look at dungeon structure
		Dungeon dungeon = task.makeDungeon(levelAsListsGrid);
		Point triforceRoom = new Point(0,1);
		Level[][] levelGrid = DungeonUtil.roomGridFromJsonGrid(levelAsListsGrid);
		levelGrid[triforceRoom.x][triforceRoom.y].placeTriforce(dungeon);
		dungeon.setGoalPoint(new Point(triforceRoom.x, triforceRoom.y));
		dungeon.setGoal("f7ecf085-4a8c-36f0-85ed-11fb6ef5a642");
		
		// Verify that fitness calculations are correct
		//ContainerGenotype
		Score<List<List<Integer>>[][]> s = task.evaluate(new ContainerGenotype<List<List<Integer>>[][]>(levelAsListsGrid));
//		assertEquals(2, NumBackTrackRooms);
		
		double numBackTrackRooms = s.scores[0];
		assertEquals(2.0, numBackTrackRooms, 0);
		// TODO: Add assertEquals checks on the backtrack value
	}
	
}
