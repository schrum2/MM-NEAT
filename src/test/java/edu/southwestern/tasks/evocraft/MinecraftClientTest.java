package edu.southwestern.tasks.evocraft;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.BlockType;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.tasks.evocraft.MinecraftClient.Orientation;

public class MinecraftClientTest {

	@Test
	public void testBlockEquality() {
		Block slimeDown = new Block(new MinecraftCoordinates(0,1,2), BlockType.SLIME, Orientation.DOWN);
		Block slimeNorth = new Block(new MinecraftCoordinates(0,1,2), BlockType.SLIME, Orientation.NORTH);
		
		assertEquals(slimeDown,slimeNorth);
	
		Block pistonDown = new Block(new MinecraftCoordinates(0,1,2), BlockType.PISTON, Orientation.DOWN);
		Block pistonNorth = new Block(new MinecraftCoordinates(0,1,2), BlockType.PISTON, Orientation.NORTH);
		
		assertNotEquals(pistonDown,pistonNorth);
	
		
		assertNotEquals(pistonDown,slimeDown);
	}


	@Test
	public void testBlockHashcode() {
		Block slimeDown = new Block(new MinecraftCoordinates(0,1,2), BlockType.SLIME, Orientation.DOWN);
		Block slimeNorth = new Block(new MinecraftCoordinates(0,1,2), BlockType.SLIME, Orientation.NORTH);
		
		assertEquals(slimeDown.hashCode(),slimeNorth.hashCode());
	
		Block pistonDown = new Block(new MinecraftCoordinates(0,1,2), BlockType.PISTON, Orientation.DOWN);
		Block pistonNorth = new Block(new MinecraftCoordinates(0,1,2), BlockType.PISTON, Orientation.NORTH);
		
		assertNotEquals(pistonDown.hashCode(),pistonNorth.hashCode());
	
		
		assertNotEquals(pistonDown.hashCode(),slimeDown.hashCode());
	}
}
