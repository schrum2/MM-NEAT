package me.jakerg.rougelike;

import java.awt.Point;
import java.util.Map.Entry;

import edu.southwestern.util.datastructures.Pair;

public class DungeonAi extends CreatureAi{

	public DungeonAi(Creature creature) {
		super(creature);
		// TODO Auto-generated constructor stub
	}
	
	public void onEnter(int x, int y, Tile tile) {
		if(tile.isGround()) {
			creature.x = x;
			creature.y = y;
		}
		if(tile.isExit()) {
			double cX = creature.x;
			double cY = creature.y;
			
			if(cX == 10.0 && cY == 8.0) {
				cX = 450.;
				cY = 400.;
			} else if(cX == 5.0 && cY == 15.0) {
				cX = 250.;
				cY = 700.;
			} else if(cX == 5.0 && cY == 0.0) {
				cX = 250.;
				cY = 50.;
			} else if (cX == 0. && cY == 8.) {
				cX = 50.;
				cY = 400.;
			}
			
			String exitPoint = cX + " : " + cY;
			System.out.println(exitPoint);
			
			Point p = creature.getDungeon().getNextNode(exitPoint);
			creature.getWorld().setNewTiles(new DungeonBuilder(creature.getDungeon()).getCurrentTiles());
			
			System.out.println("New point" + p);
			creature.x  = p.y;
			creature.y = p.x;
//			for(Entry<String, Pair<String, Point>> entry : creature.dungeon.getCurrentlevel().adjacency.entrySet()) {
//				System.out.println("OOF : " + entry.getKey());
//			}
		}
		System.out.println("Trying to move");
	}

}
