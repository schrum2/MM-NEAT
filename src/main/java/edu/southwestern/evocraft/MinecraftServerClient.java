package edu.southwestern.evocraft;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.ProcessBuilder.Redirect;
import java.util.List;

import edu.southwestern.tasks.mario.gan.Comm;
import edu.southwestern.util.PythonUtil;
import edu.southwestern.util.datastructures.Triple;

public class MinecraftServerClient extends Comm {

	private static MinecraftServerClient client = null;

	public static final String PYTHON_BASE_PATH = "." + File.separator + "src" + File.separator + "main" + File.separator + "python" + File.separator + "EvoCraft" + File.separator;
	// Python script to interact with a Minecraft server on the localhost
	public static final String CLIENT_PATH = PYTHON_BASE_PATH + "ServerSendReceive.py";

	public MinecraftServerClient() {
		super();
		// More?
	}

	@Override
	public void initBuffers() {
		//Initialize input and output
		if (this.process != null) {
			this.reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			this.writer = new PrintStream(this.process.getOutputStream());
			System.out.println("Process buffers initialized");
		} else {
			printErrorMsg("MinecraftServerUtil:initBuffers:Null process!");
		}
	}

	public MinecraftServerClient getMinecraftServerClient() {
		if(client == null) {
			PythonUtil.setPythonProgram();
			client = new MinecraftServerClient();
			client.start();
			// consume all start-up messages that are not data responses
			String response = "";
			while(!response.equals("READY")) {
				response = client.commRecv();
			}
		}
		return client;
	}

	@Override
	public void start() {
		try {
			launchClientScript();
			initBuffers();
			printInfoMsg(this.threadName + " has started");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void launchClientScript() {
		PythonUtil.checkPython();
		// Run script for communicating with Minecraft Server
		ProcessBuilder builder = new ProcessBuilder(PythonUtil.PYTHON_EXECUTABLE, CLIENT_PATH);
		builder.redirectError(Redirect.INHERIT); // Standard error will print to console
		try {
			System.out.println(builder.command());
			this.process = builder.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void terminateClientScriptProcess() {
		if(client != null) {
			client.process.destroy();
			client.process = null;
		}
	}

	//// Modeling the data and commands used to communicate with Minecraft ////

	// Block orientation. Compare with src\main\python\EvoCraft\minecraft_pb2.py
	public enum Orientation {
		NORTH,
		WEST,
		SOUTH,
		EAST,
		UP,
		DOWN
	}

	// Block type. Compare with src\main\python\EvoCraft\minecraft_pb2.py
	public enum BlockType {
		ACACIA_DOOR,
		ACACIA_FENCE,
		ACACIA_FENCE_GATE,
		ACACIA_STAIRS,
		ACTIVATOR_RAIL,
		AIR,
		ANVIL,
		BARRIER,
		BEACON,
		BED,
		BEDROCK,
		BEETROOTS,
		BIRCH_DOOR,
		BIRCH_FENCE,
		BIRCH_FENCE_GATE,
		BIRCH_STAIRS,
		BLACK_GLAZED_TERRACOTTA,
		BLACK_SHULKER_BOX,
		BLUE_GLAZED_TERRACOTTA,
		BLUE_SHULKER_BOX,
		BONE_BLOCK,
		BOOKSHELF,
		BREWING_STAND,
		BRICK_BLOCK,
		BRICK_STAIRS,
		BROWN_GLAZED_TERRACOTTA,
		BROWN_MUSHROOM,
		BROWN_MUSHROOM_BLOCK,
		BROWN_SHULKER_BOX,
		CACTUS,
		CAKE,
		CARPET,
		CARROTS,
		CAULDRON,
		CHAIN_COMMAND_BLOCK,
		CHEST,
		CHORUS_FLOWER,
		CHORUS_PLANT,
		CLAY,
		COAL_BLOCK,
		COAL_ORE,
		COBBLESTONE,
		COBBLESTONE_WALL,
		COCOA,
		COMMAND_BLOCK,
		CONCRETE,
		CONCRETE_POWDER,
		CRAFTING_TABLE,
		CYAN_GLAZED_TERRACOTTA,
		CYAN_SHULKER_BOX,
		DARK_OAK_DOOR,
		DARK_OAK_FENCE,
		DARK_OAK_FENCE_GATE,
		DARK_OAK_STAIRS,
		DAYLIGHT_DETECTOR,
		DAYLIGHT_DETECTOR_INVERTED,
		DEADBUSH,
		DETECTOR_RAIL,
		DIAMOND_BLOCK,
		DIAMOND_ORE,
		DIRT,
		DISPENSER,
		DOUBLE_PLANT,
		DOUBLE_STONE_SLAB,
		DOUBLE_STONE_SLAB2,
		DOUBLE_WOODEN_SLAB,
		DRAGON_EGG,
		DROPPER,
		EMERALD_BLOCK,
		EMERALD_ORE,
		ENCHANTING_TABLE,
		ENDER_CHEST,
		END_BRICKS,
		END_GATEWAY,
		END_PORTAL,
		END_PORTAL_FRAME,
		END_ROD,
		END_STONE,
		FARMLAND,
		FENCE,
		FENCE_GATE,
		FIRE,
		FLOWER_POT,
		FLOWING_LAVA,
		FLOWING_WATER,
		FROSTED_ICE,
		FURNACE,
		GLASS,
		GLASS_PANE,
		GLOWSTONE,
		GOLDEN_RAIL,
		GOLD_BLOCK,
		GOLD_ORE,
		GRASS,
		GRASS_PATH,
		GRAVEL,
		GRAY_GLAZED_TERRACOTTA,
		GRAY_SHULKER_BOX,
		GREEN_GLAZED_TERRACOTTA,
		GREEN_SHULKER_BOX,
		HARDENED_CLAY,
		HAY_BLOCK,
		HEAVY_WEIGHTED_PRESSURE_PLATE,
		HOPPER,
		ICE,
		IRON_BARS,
		IRON_BLOCK,
		IRON_DOOR,
		IRON_ORE,
		IRON_TRAPDOOR,
		JUKEBOX,
		JUNGLE_DOOR,
		JUNGLE_FENCE,
		JUNGLE_FENCE_GATE,
		JUNGLE_STAIRS,
		LADDER,
		LAPIS_BLOCK,
		LAPIS_ORE,
		LAVA,
		LEAVES,
		LEAVES2,
		LEVER,
		LIGHT_BLUE_GLAZED_TERRACOTTA,
		LIGHT_BLUE_SHULKER_BOX,
		LIGHT_WEIGHTED_PRESSURE_PLATE,
		LIME_GLAZED_TERRACOTTA,
		LIME_SHULKER_BOX,
		LIT_FURNACE,
		LIT_PUMPKIN,
		LIT_REDSTONE_LAMP,
		LIT_REDSTONE_ORE,
		LOG,
		LOG2,
		MAGENTA_GLAZED_TERRACOTTA,
		MAGENTA_SHULKER_BOX,
		MAGMA,
		MELON_BLOCK,
		MELON_STEM,
		MOB_SPAWNER,
		MONSTER_EGG,
		MOSSY_COBBLESTONE,
		MYCELIUM,
		NETHERRACK,
		NETHER_BRICK,
		NETHER_BRICK_FENCE,
		NETHER_BRICK_STAIRS,
		NETHER_WART,
		NETHER_WART_BLOCK,
		NOTEBLOCK,
		OAK_STAIRS,
		OBSERVER,
		OBSIDIAN,
		ORANGE_GLAZED_TERRACOTTA,
		ORANGE_SHULKER_BOX,
		PACKED_ICE,
		PINK_GLAZED_TERRACOTTA,
		PINK_SHULKER_BOX,
		PISTON,
		PISTON_EXTENSION,
		PISTON_HEAD,
		PLANKS,
		PORTAL,
		POTATOES,
		POWERED_COMPARATOR,
		POWERED_REPEATER,
		PRISMARINE,
		PUMPKIN,
		PUMPKIN_STEM,
		PURPLE_GLAZED_TERRACOTTA,
		PURPLE_SHULKER_BOX,
		PURPUR_BLOCK,
		PURPUR_DOUBLE_SLAB,
		PURPUR_PILLAR,
		PURPUR_SLAB,
		PURPUR_STAIRS,
		QUARTZ_BLOCK,
		QUARTZ_ORE,
		QUARTZ_STAIRS,
		RAIL,
		REDSTONE_BLOCK,
		REDSTONE_LAMP,
		REDSTONE_ORE,
		REDSTONE_TORCH,
		REDSTONE_WIRE,
		RED_FLOWER,
		RED_GLAZED_TERRACOTTA,
		RED_MUSHROOM,
		RED_MUSHROOM_BLOCK,
		RED_NETHER_BRICK,
		RED_SANDSTONE,
		RED_SANDSTONE_STAIRS,
		RED_SHULKER_BOX,
		REEDS,
		REPEATING_COMMAND_BLOCK,
		SAND,
		SANDSTONE,
		SANDSTONE_STAIRS,
		SAPLING,
		SEA_LANTERN,
		SILVER_GLAZED_TERRACOTTA,
		SILVER_SHULKER_BOX,
		SKULL,
		SLIME,
		SNOW,
		SNOW_LAYER,
		SOUL_SAND,
		SPONGE,
		SPRUCE_DOOR,
		SPRUCE_FENCE,
		SPRUCE_FENCE_GATE,
		SPRUCE_STAIRS,
		STAINED_GLASS,
		STAINED_GLASS_PANE,
		STAINED_HARDENED_CLAY,
		STANDING_BANNER,
		STANDING_SIGN,
		STICKY_PISTON,
		STONE,
		STONEBRICK,
		STONE_BRICK_STAIRS,
		STONE_BUTTON,
		STONE_PRESSURE_PLATE,
		STONE_SLAB,
		STONE_SLAB2,
		STONE_STAIRS,
		STRUCTURE_BLOCK,
		STRUCTURE_VOID,
		TALLGRASS,
		TNT,
		TORCH,
		TRAPDOOR,
		TRAPPED_CHEST,
		TRIPWIRE,
		TRIPWIRE_HOOK,
		UNLIT_REDSTONE_TORCH,
		UNPOWERED_COMPARATOR,
		UNPOWERED_REPEATER,
		VINE,
		WALL_BANNER,
		WALL_SIGN,
		WATER,
		WATERLILY,
		WEB,
		WHEAT,
		WHITE_GLAZED_TERRACOTTA,
		WHITE_SHULKER_BOX,
		WOODEN_BUTTON,
		WOODEN_DOOR,
		WOODEN_PRESSURE_PLATE,
		WOODEN_SLAB,
		WOOL,
		YELLOW_FLOWER,
		YELLOW_GLAZED_TERRACOTTA,
		YELLOW_SHULKER_BOX		
	}

	public static class MineCraftCoordinates extends Triple<Integer,Integer,Integer> {
		public MineCraftCoordinates(int x, int y, int z) {
			super(x, y, z);
		}
		
		public int x() { return t1; }
		public int y() { return t2; }
		public int z() { return t3; }
	}
	
	public static class Block {
		MineCraftCoordinates position;
		BlockType type;
		Orientation orientation;
		
		/**
		 * New block at given coordinates with given type.
		 * @param x x-coordinate
		 * @param y y-coordinate (up and down). Minimum value allowed is 0.
		 * @param z z-coordinate
		 * @param type From BlockType enum above
		 * @param orientation From Orientation enum above
		 */
		public Block(int x, int y, int z, BlockType type, Orientation orientation) {
			position = new MineCraftCoordinates(x,y,z);
			this.type = type;
			this.orientation = o;
		}
		
		/**
		 * New block at given coordinates with type specified as index within enum.
		 * @param x x-coordinate
		 * @param y y-coordinate (up and down). Minimum value allowed is 0.
		 * @param z z-coordinate
		 * @param typeIndex index in enum values of the desired block type
		 * @param orientationIndex index in enum values of the desired orientation
		 */
		public Block(int x, int y, int z, int typeIndex, int orientationIndex) {
			position = new MineCraftCoordinates(x,y,z);
			this.type = BlockType.values()[typeIndex];
			this.orientation = Orientation.values()[orientationIndex];
		}

		/**
		 * x-coordinate of Block
		 * @return x-coordinate
		 */
		public int x() {
			return position.x();
		}
		
		/**
		 * y-coordinate of Block
		 * @return y-coordinate
		 */
		public int y() {
			return position.y();
		}

		/**
		 * z-coordinate of Block
		 * @return z-coordinate
		 */
		public int z() {
			return position.z();
		}

		/**
		 * Get type index of the type that will be recognized by Python
		 * @return Type index/number for type
		 */
		public int type() {
			return type.ordinal();
		}
		
		/**
		 * Get orientation index of the orientation that will be recognized by Python
		 * @return index/number for orientation
		 */
		public int orientation() {
			return orientation.ordinal();
		}
	}

	/**
	 * Spawn the given list of Block objects in the Minecraft server
	 * @param blocks List of Blocks to spawn
	 */
	public void spawnBlocks(List<Block> blocks) {
		StringBuilder sb = new StringBuilder();
		sb.append("spawnBlocks ");
		for(Block b: blocks) {
			sb.append(b.x() + " " + b.y() + " " + b.z() + " " + b.type() + " " + b.orientation() + " ");
		}
		try {
			commSend(sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Minecraft failed on command "+sb);
			System.exit(1);
		}
	}
	
	/**
	 * Fill all space in the specified range with the provided type. The
	 * rectangular prism defined in the world spawns from the min coordinates to the max coordinates.
	 * 
	 * @param min Minimal coordinates in each dimension (each min coordinate must be <= max coordinate)
	 * @param max Maximal coordinates in each dimension
	 * @param type Type to fill the space with
	 */
	public void fillCube(MineCraftCoordinates min, MineCraftCoordinates max, BlockType type) {
		fillCube(min.x(), min.y(), min.z(), max.x(), max.y(), max.z(), type);
	}

	/**
	 * Fill all space in the specified range with the provided type. The
	 * rectangular prism defined in the world spawns from the
	 * (xmin,ymin,zmin) coordinates to the (xmax,ymax,zmax) coordinates.
	 * 
	 * @param xmin Minimal x coordinate. xmin <= xmax
	 * @param ymin Minimal y coordinate. ymin <= ymax
	 * @param zmin Minimal z coordinate. zmin <= zmax
	 * @param xmax Maximal x coordinate
	 * @param ymax Maximal y coordinate
	 * @param zmax Maximal z coordinate
	 * @param type Type to fill the space with
	 */
	public void fillCube(int xmin, int ymin, int zmin, int xmax, int ymax, int zmax, BlockType type) {
		String message = "fillCube "+xmin+" "+ymin+" "+zmin+" "+xmax+" "+ymax+" "+zmax+" "+type+" "+type.ordinal()+" ";
		try {
			commSend(message);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Minecraft failed on command "+message);
			System.exit(1);
		}
	}
	
	
}
