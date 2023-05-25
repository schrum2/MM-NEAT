package edu.southwestern.tasks.evocraft;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.ProcessBuilder.Redirect;
import java.util.ArrayList;
import java.util.List;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.BlockType;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.tasks.mario.gan.Comm;
import edu.southwestern.util.PythonUtil;
import edu.southwestern.util.datastructures.Triple;
/**
 * MinecraftClient creates A client if one doesn't exist communicates with Python API as a Java interface 
 * to place blocks in their proper positions while also formating the area properly
 * @author raffertyt
 *
 */
public class MinecraftClient extends Comm {

	public static final int MAX_Y_COORDINATE = 255;

	private static final int MAX_CLEAR_WITHOUT_LOOP = 1500000;

	public static final int GROUND_LEVEL = 4;

	public static final int BUFFER = 10;

	private static MinecraftClient client = null;

	public static final String PYTHON_BASE_PATH = "." + File.separator + "src" + File.separator + "main" + File.separator + "python" + File.separator + "EvoCraft" + File.separator;
	// Python script to interact with a Minecraft server on the localhost
	public static final String CLIENT_PATH = PYTHON_BASE_PATH + "ServerSendReceive.py";
	
	//RELATED TO MOVED CLEAR FUNCTIONS FROM getCenterOfMassBeforeAndAfter in ChangeCenterOfMass
	// Nowhere near where anything else is being evaluated
	public static final MinecraftCoordinates SPECIAL_CORNER = new MinecraftCoordinates(-500, 100, 500);
	public static final int SPECIAL_CORNER_BUFFER = 20;

	public MinecraftClient() {
		super();
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
	/**
	 * Creates new client if one does not exist returns client if it already exists
	 * @return client MinecraftClient();
	 */
	public static MinecraftClient getMinecraftClient() {
		if(client == null) {
			PythonUtil.setPythonProgram();
			client = new MinecraftClient();
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
	/**
	 * Launches client script and checks to see if builder can build
	 */
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
	/**
	 * destroys Client process and sets it equal to null
	 */
	public static void terminateClientScriptProcess() {
		if(client != null) {
			client.process.destroy();
			client.process = null;
		}
	}

	//// Modeling the data and commands used to communicate with Minecraft ////

	// Block orientation. Compare with src\main\python\EvoCraft\minecraft_pb2.py
	public enum Orientation {
		NORTH,	//0
		WEST,	//1
		SOUTH,	//2
		EAST,	//3
		UP,		//4
		DOWN	//5
	}

	// Block type. Compare with src\main\python\EvoCraft\minecraft_pb2.py to find index of block
	// MM-NEAT\src\main\python\EvoCraft>python ServerSendReceive.py
	// spawnBlocks x y z type# orientation#
	public enum BlockType {
		ACACIA_DOOR, 		// 0
		ACACIA_FENCE,		// 1
		ACACIA_FENCE_GATE,	// 2
		ACACIA_STAIRS,		// 3
		ACTIVATOR_RAIL,		// 4
		AIR,				// 5
		ANVIL,				// 6
		BARRIER,			// 7
		BEACON,				// 8
		BED,				// 9
		BEDROCK,			// 10
		BEETROOTS,			// 11
		BIRCH_DOOR,			// 12
		BIRCH_FENCE,		// 13
		BIRCH_FENCE_GATE,	// 14
		BIRCH_STAIRS,		// 15
		BLACK_GLAZED_TERRACOTTA,	// 16
		BLACK_SHULKER_BOX,	// 17
		BLUE_GLAZED_TERRACOTTA,		// 18
		BLUE_SHULKER_BOX,	// 19
		BONE_BLOCK,			// 20
		BOOKSHELF,			// 21
		BREWING_STAND,		// 22
		BRICK_BLOCK,		// 23
		BRICK_STAIRS,		// 24
		BROWN_GLAZED_TERRACOTTA,	// 25
		BROWN_MUSHROOM,		// 26
		BROWN_MUSHROOM_BLOCK,	// 27
		BROWN_SHULKER_BOX,	// 28
		CACTUS,				// 29
		CAKE,				// 30
		CARPET,				// 31
		CARROTS,			// 32
		CAULDRON,			// 33
		CHAIN_COMMAND_BLOCK,	// 34
		CHEST,				// 35
		CHORUS_FLOWER,		// 36
		CHORUS_PLANT,		// 37
		CLAY,				// 38
		COAL_BLOCK,			// 39
		COAL_ORE,			// 40
		COBBLESTONE,		// 41
		COBBLESTONE_WALL,	// 42
		COCOA,				// 43
		COMMAND_BLOCK,		// 44
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

	/**
	 * Coordinates for blocks in Minecraft are int-based: (x,y,z)
	 * @author schrum2
	 *
	 */
	public static class MinecraftCoordinates extends Triple<Integer,Integer,Integer> {
		/**
		 * sets Integers within this MinecraftCoordinates equal to size
		 * @param size value of each coordinate (x, y, and z)
		 */
		public MinecraftCoordinates(int size) {
			this(size,size,size);
		}
		/**
		 * constructs a new coordinate with X, Y and Z values
		 * @param x specific coordinate for x 
		 * @param y specific coordinate for y
		 * @param z specific coordinate for z
		 */
		public MinecraftCoordinates(int x, int y, int z) {
			super(x, y, z);
		}
		/**
		 * gets proper coordinates from original
		 * @param original
		 */
		public MinecraftCoordinates(MinecraftCoordinates original) { // copy constructor
			super(original.x(), original.y(), original.z());
		}
		/**
		 * 
		 * @return t1 which is the proper coordinate for x
		 */
		public int x() { return t1; }
		/**
		 * 
		 * @return t1 which is the proper coordinate for y
		 */
		public int y() { return t2; }
		/**
		 * 
		 * @return t1 which is the proper coordinate for z
		 */
		public int z() { return t3; }
		
		/**
		 * Add two vectors together (component-wise)
		 * @param other Coordinates to add to this one.
		 * @return Result of adding the vectors
		 */
		public MinecraftCoordinates add(MinecraftCoordinates other) {
			return new MinecraftCoordinates(x() + other.x(), y() + other.y(), z() + other.z());
		}
		
		/**
		 * subtract two vectors together (component-wise)
		 * @param other Coordinates to subtract from this one.
		 * @return Result of subtracting the vectors
		 */
		public MinecraftCoordinates sub(MinecraftCoordinates other) {
			return new MinecraftCoordinates(x() - other.x(), y() - other.y(), z() - other.z());
		}
		
		/**
		 * multiply two vectors together (component-wise)
		 * @param other Coordinates to multiplying to this one.
		 * @return Product of the vectors
		 */
		public MinecraftCoordinates mult(MinecraftCoordinates other) {
			return new MinecraftCoordinates(x() * other.x(), y() * other.y(), z() * other.z());
		}

		/**
		 * Subtract the same quantity from each coordinate
		 * @param amount Amount to subtract
		 * @return Resulting coordinates
		 */
		public MinecraftCoordinates sub(int amount) {
			return this.sub(new MinecraftCoordinates(amount,amount,amount));
		}

		/**
		 * Add the same quantity to each coordinate
		 * @param amount Amount to add
		 * @return Resulting coordinates
		 */
		public MinecraftCoordinates add(int amount) {
			return this.add(new MinecraftCoordinates(amount,amount,amount));
		}
	}
	
	/**
	 * A Minecraft Block has position, type, and orientation,
	 * though the EvoCraft interface does not read orientation
	 * information, so that may be null sometimes.
	 * 
	 * @author schrum2
	 *
	 */
	public static class Block {
		MinecraftCoordinates position;
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
		public Block(MinecraftCoordinates pos, BlockType type, Orientation orientation) {
			this(pos.x(), pos.y(), pos.z(), type, orientation);
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			if(type != null && orientationMatters(type)) {
				result = prime * result + ((orientation == null) ? 0 : orientation.hashCode());
			}
			result = prime * result + ((position == null) ? 0 : position.hashCode());
			result = prime * result + ((type == null) ? 0 : type.hashCode());
			return result;
		}

		/**
		 * Identifies blocks where orientation affects placement in world,
		 * but only works for machine block set.
		 * 
		 * @param type Block Type
		 * @return if orientation changes its placement
		 */
		public boolean orientationMatters(BlockType type) {
			switch(type) {
			case PISTON:
			case STICKY_PISTON:
			case OBSERVER:
				return true;
			default:
				return false;
			}
		}
		/**
		 * uses values from orientationMatters compared with object to narrow down flying machines that are identical.
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (!(obj instanceof Block)) {
				return false;
			}
			Block other = (Block) obj;
			if(type != null && orientationMatters(type)) {
				if (orientation != other.orientation) {
					return false;
				}
			}
			if (position == null) {
				if (other.position != null) {
					return false;
				}
			} else if (!position.equals(other.position)) {
				return false;
			}
			if (type != other.type) {
				return false;
			}
			return true;
		}

		/**
		 * New block at given coordinates with given type.
		 * @param x x-coordinate
		 * @param y y-coordinate (up and down). Minimum value allowed is 0.
		 * @param z z-coordinate
		 * @param type From BlockType enum above
		 * @param orientation From Orientation enum above
		 */
		public Block(int x, int y, int z, BlockType type, Orientation orientation) {
			position = new MinecraftCoordinates(x,y,z);
			this.type = type;
			this.orientation = orientation;
		}
		
		/**
		 * New block at given coordinates with type specified as index within enum.
		 * @param x x-coordinate
		 * @param y y-coordinate (up and down). Minimum value allowed is 0.
		 * @param z z-coordinate
		 * @param typeIndex index in enum values of the desired block type
		 */
		public Block(int x, int y, int z, int typeIndex) {
			// Meant for use with readCube, which does not provide orientation
			this(x,y,z,BlockType.values()[typeIndex],null);
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
		
		/**
		 * Display block information
		 */
		public String toString() {
			String name = type.name();
			String pos = position.toString();
			return name + " at " + pos + " oriented " + (orientation == null ? "null" : orientation.name());
		}
		

	}

	/**
	 * Spawn the given list of Block objects in the Minecraft server
	 * @param blocks List of Blocks to spawn
	 */
	public synchronized void spawnBlocks(List<Block> blocks) {
		// Only spawns a block if there are blocks to spawn
		if(blocks.size()>0) {
			StringBuilder sb = new StringBuilder();
			sb.append("spawnBlocks ");
			for(Block b: blocks) {
				checkBlockBounds(b.x(),b.y(),b.z());
				sb.append(b.x() + " " + b.y() + " " + b.z() + " " + b.type() + " " + b.orientation() + " ");
			}
		
			try
			{
				commSend(sb.toString());
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Minecraft failed on command "+sb);
				System.exit(1);
			}
		}
	}
	
	/**
	 * Clears an area and verifies that it is clear
	 * called if you need to make sure it is clear
	 * @param corner corner that the shape is occupying
	 */
	public static void clearAndVerify(MinecraftCoordinates corner) {
		boolean empty = false;
		int clearAttempt = 0;
		do {
			clearAreaAroundCorner(corner);
			empty = areaAroundCornerEmpty(corner);
			if(!empty) System.out.println("Cleared "+(++clearAttempt)+" times: empty?: "+empty);
		} while(!empty);
	}
	
	/**
	 * Make sure the special area for double-checking flying shapes is really clear
	 */
	public static void clearAreaAroundSpecialCorner() {
		clearAreaAroundCorner(SPECIAL_CORNER);
	}
	/**
	 * body of code for clearAreaAroundSpecialCorner used above
	 * @param corner
	 */
	public static void clearAreaAroundCorner(MinecraftCoordinates corner) {
		MinecraftCoordinates lower = corner.sub(SPECIAL_CORNER_BUFFER);
		MinecraftCoordinates upper = corner.add(MinecraftUtilClass.getRanges().add(SPECIAL_CORNER_BUFFER));
		getMinecraftClient().clearCube(lower, upper, BlockType.AIR);
		List<Block> errorCheck = null;
		assert areaAroundCornerEmpty(corner) : "Area not empty after clearing! "+errorCheck;
	}
	/**
	 * Checks if the area around a corner is empty
	 * @param corner the corner coordinates being checked
	 * @return boolean if space is empty or not
	 */
	public static boolean areaAroundCornerEmpty(MinecraftCoordinates corner) {
		MinecraftCoordinates lower = corner.sub(SPECIAL_CORNER_BUFFER);
		MinecraftCoordinates upper = corner.add(MinecraftUtilClass.getRanges().add(SPECIAL_CORNER_BUFFER));
		List<Block> errorCheck = MinecraftUtilClass.filterOutBlock(getMinecraftClient().readCube(lower, upper), BlockType.AIR);
//		if(!errorCheck.isEmpty()) {
//			System.out.println("NOT EMPTY at corner "+corner+"\n"+errorCheck);
//			MiscUtil.waitForReadStringAndEnterKeyPress();
//		}
		return errorCheck.isEmpty();
	}
	
	/**
	 * clears cubes by replacing all cubes with log and then replacing with air
	 * log chosen arbitrarily
	 * this forcefully replaces all blocks that might be misinterpreted as air into log and then replaces them with air
	 * uses fillCube to fill the space
	 * 
	 * @param min Minimal coordinates in each dimension (each min coordinate must be <= max coordinate)
	 * @param max Maximal coordinates in each dimension
	 */
	public synchronized void clearCube(MinecraftCoordinates min, MinecraftCoordinates max, BlockType type) {
		fillCube(min, max, BlockType.LOG);
		fillCube(min, max, type);
	}
	
	/**
	 * Fill all space in the specified range with the provided type. The
	 * rectangular prism defined in the world spawns from the
	 * (xmin,ymin,zmin) coordinates to the (xmax,ymax,zmax) coordinates.
	 * 
	 * first fills with log to clear out any blocks incorrectly registered as air, then replaces with the desired type
	 * uses fillCube to fill the space
	 * 
	 * @param xmin Minimal x coordinate. xmin <= xmax
	 * @param ymin Minimal y coordinate. ymin <= ymax
	 * @param zmin Minimal z coordinate. zmin <= zmax
	 * @param xmax Maximal x coordinate
	 * @param ymax Maximal y coordinate
	 * @param zmax Maximal z coordinate
	 * @param type Type to fill the space with
	 */
	public synchronized void clearCube(int xmin, int ymin, int zmin, int xmax, int ymax, int zmax, BlockType type) {
		fillCube(xmin, ymin, zmin, xmax, ymax, zmax, BlockType.LOG);
		fillCube(xmin, ymin, zmin, xmax, ymax, zmax, type);
	}
	
	/**
	 * Fill all space in the specified range with the provided type. The
	 * rectangular prism defined in the world spawns from the min coordinates to the max coordinates.
	 * 
	 * @param min Minimal coordinates in each dimension (each min coordinate must be <= max coordinate)
	 * @param max Maximal coordinates in each dimension
	 * @param type Type to fill the space with
	 */
	public synchronized void fillCube(MinecraftCoordinates min, MinecraftCoordinates max, BlockType type) {
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
	public synchronized void fillCube(int xmin, int ymin, int zmin, int xmax, int ymax, int zmax, BlockType type) {
		checkBlockBounds(xmin, ymin, zmin, xmax, ymax, zmax);
		String message = "fillCube "+xmin+" "+ymin+" "+zmin+" "+xmax+" "+ymax+" "+zmax+" "+type.ordinal()+" ";
		
		try {
			commSend(message);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Minecraft failed on command "+message);
			System.exit(1);
		}
	}
	
	/**
	 * Over loaded method, retrieves list of a singular block at a specified coordinate
	 * 
	 * @param pos Singular coordinate to read in from
	 * @return List of Blocks at the singular coordinate
	 */
	public synchronized ArrayList<Block> readCube(MinecraftCoordinates pos) {
		return readCube(pos.x(), pos.y(), pos.z(), pos.x(), pos.y(), pos.z());
	}
	
	/**
	 * Retrieve list of all Blocks (types and positions) found in the given region.
	 * Note that when blocks are read, their orientation is not known.
	 * 
	 * @param min Minimal coordinates in each dimension (each min coordinate must be <= max coordinate)
	 * @param max Maximal coordinates in each dimension
	 * @return List of Blocks between the min and max coordinates (inclusive)
	 */
	public synchronized ArrayList<Block> readCube(MinecraftCoordinates min, MinecraftCoordinates max) {
		assert min.x() <= max.x() && min.y() <= max.y() && min.z() <= max.z(): "Min should be less than max in each coordinate: min = "+min+ ", max = "+max; 
		return readCube(min.x(), min.y(), min.z(), max.x(), max.y(), max.z());
	}
	
	/**
	 * Retrieve list of all Blocks (types and positions) found in the given region.
	 * Note that when blocks are read, their orientation is not known.
	 * 
	 * @param xmin Minimal x coordinate. xmin <= xmax
	 * @param ymin Minimal y coordinate. ymin <= ymax
	 * @param zmin Minimal z coordinate. zmin <= zmax
	 * @param xmax Maximal x coordinate
	 * @param ymax Maximal y coordinate
	 * @param zmax Maximal z coordinate
	 * @return List of Blocks between the min and max coordinates (inclusive)
	 */
	public synchronized ArrayList<Block> readCube(int xmin, int ymin, int zmin, int xmax, int ymax, int zmax) {
		String message = "readCube "+xmin+" "+ymin+" "+zmin+" "+xmax+" "+ymax+" "+zmax+" ";
		checkBlockBounds(xmin, ymin, zmin, xmax, ymax, zmax);
		try {
			commSend(message);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Minecraft failed on command "+message);
			System.exit(1);
		}
		String response = commRecv();
		if(response == null) {
			System.out.println("Python process for interacting with the Minecraft server is not responding.");
			System.out.println("This likely means the Minecraft server was overwhelmed. Try increasing the \"minecraftClearSleepTimer\"");
			throw new NullPointerException("Response not received from readCube. Server is probably overwhelmed and crashed.");
		}
		String[] tokens = response.split(" ");
		ArrayList<Block> result = new ArrayList<Block>(tokens.length / 4);
		// Each Block is 4 numbers: x y z type
		for(int i = 0; i < tokens.length; i += 4) {
			Block b = new Block(Integer.parseInt(tokens[i]),Integer.parseInt(tokens[i+1]),Integer.parseInt(tokens[i+2]),Integer.parseInt(tokens[i+3]));
			result.add(b);
		}
		return result;
	}
	
	/**
	 * Clear a large enough space in the world for a population of shapes.
	 * 
	 * @param start Start coordinates where shapes are generated
	 * @param ranges Size of each shape space
	 * @param numShapes Number of generated shapes
	 * @param buffer Buffer distance between shapes
	 */
	public void clearSpaceForShapes(MinecraftCoordinates start, MinecraftCoordinates ranges, int numShapes, int buffer) {
		clearSpaceForShapes(start, ranges, numShapes, buffer, true);
	}
	
	/**(
	 * Clear a large enough space in the world for up to a population of shapes. This
	 * is a separate method that is called by a method of the same name, however, this 
	 * method allows for toggling whether or not the y is set at ground level
	 * 
	 * @param start Start coordinates where shapes are generated
	 * @param ranges Size of each shape space
	 * @param numShapes Number of generated shapes
	 * @param buffer Buffer distance between shapes
	 * @param stopAtGround Whether or not the y axis is set at ground level
	 */
	public void clearSpaceForShapes(MinecraftCoordinates start, MinecraftCoordinates ranges, int numShapes, int buffer, boolean stopAtGround) {
		MinecraftCoordinates groundStart = new MinecraftCoordinates(start.x()-buffer, stopAtGround ? GROUND_LEVEL : start.y()-buffer, start.z()-buffer);
		//System.out.println("Starts:"+groundStart);
		MinecraftCoordinates end = new MinecraftCoordinates(start.x() + numShapes*(ranges.x() + Parameters.parameters.integerParameter("spaceBetweenMinecraftShapes")) + buffer, start.y() + ranges.y() + buffer, start.z() + (int)(ranges.z()*Math.sqrt(numShapes)) + buffer);
		//System.out.println("ENDS:"+end);
		
		// If cleared space isn't very large, just clear that space
		int clearSize = (end.x()-groundStart.x())*(end.y()-groundStart.y())*(end.z()-groundStart.z());
		if( clearSize<=MAX_CLEAR_WITHOUT_LOOP) {
			clearCube(groundStart, end, BlockType.AIR); // Calls clear cube, which checks coordinates
		}else {
			int counter=50000000; // Don't need gradual clear messages for small clear sizes
			// Otherwise, clears out large block sections one at a time to ensure the server isn't overloaded
			int fillSize = Parameters.parameters.integerParameter("minecraftClearDimension");
			System.out.println("*WARNING* The size that needs to be cleared out is over "+MAX_CLEAR_WITHOUT_LOOP+" blocks, this may take a while to clear");
			System.out.println("Size neededing to be cleared: "+clearSize+" blocks"); // Prints to warn user
			System.out.println("From "+groundStart+" to "+end);
			for(int x=groundStart.x();x<=end.x();x+=fillSize) {
				for(int z=groundStart.z();z<=end.z();z+=fillSize) {
					for(int y=GROUND_LEVEL;y<=end.y();y+=fillSize) {
						//System.out.println("clearing "+counter);
						counter++;
						clearCube(x,y,z,x+fillSize,y+fillSize,z+fillSize, BlockType.AIR);
						//System.out.println(x+fillSize+" "+(y+fillSize)+" "+(z+fillSize));
						if((x+fillSize)*(y+fillSize)*(z+fillSize)>counter) {
							System.out.println("Blocks cleared = "+((x+fillSize)*(y+fillSize)*(z+fillSize)));
							counter+=50000000; // Gradual prints to let the user know it's still running
						}
						try {
							Thread.sleep(Parameters.parameters.integerParameter("minecraftClearSleepTimer"));
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}	
			System.out.println("Clearing done");
		}
	}
	
	/**
	 * Represent a list of Minecraft blocks as a 3D array where the given corner is the
	 * start of the 3D array, but potentially with padding added.
	 * 
	 * @param corner Location in Minecraft where shape was generated (minimal coordinates)
	 * @param blocks List of blocks generated at that position
	 * @param padding How much empty space to have around the shape
	 * @return 3D array with block types organized as they appear in the world
	 */
	public static int[][][] blockListTo3DArray(MinecraftCoordinates corner, List<Block> blocks, int padding) {
		assert corner != null : "Corner is null!";
		int[][][] shape= new int[Parameters.parameters.integerParameter("minecraftXRange")+2*padding][Parameters.parameters.integerParameter("minecraftYRange")+2*padding][Parameters.parameters.integerParameter("minecraftZRange")+2*padding];
		// Initialize everything in the 3D array to be air
		for(int i = 0; i < shape.length; i++) {
			for(int j = 0; j < shape[i].length; j++) {
				for(int k = 0; k < shape[i][j].length; k++) {
					shape[i][j][k] = BlockType.AIR.ordinal();
				}
			}
		}
		// Places the blocks from the list into the 3D array in the right spot
		for(Block b : blocks) {
			assert b != null : "Block b null in "+blocks;
			shape[b.x() - corner.x() + padding][b.y() - corner.y() + padding][b.z() - corner.z() + padding] = b.type();
		}
		return shape;
	}
	
	/**
	 * Overloaded method. Calls method of same name, but without min and max, just 
	 * checking if current coordinates are legal
	 * @param x x coordinate
	 * @param y y coordinate
	 * @param z z coordinate
	 */
	public void checkBlockBounds(int x, int y, int z) {
		checkBlockBounds(x,y,z,x,y,z);
	}
	
	/**
	 * Checks the x,y, and z coordinates to ensure they are within bounds
	 * and won't crash the world by placing something out of bounds
	 * 
	 * @param xmin Minimal x coordinate. xmin <= xmax
	 * @param ymin Minimal y coordinate. ymin <= ymax
	 * @param zmin Minimal z coordinate. zmin <= zmax
	 * @param xmax Maximal x coordinate
	 * @param ymax Maximal y coordinate
	 * @param zmax Maximal z coordinate
	 */
	public void checkBlockBounds(int xmin, int ymin, int zmin, int xmax, int ymax, int zmax) {
		// Max and min based on not being able to place blocks after coordinate 29999983. Added a buffer to it
		if(xmin < -29999960 || xmin > 29999960 || xmax < -29999960 || xmax > 29999960 ||
		   ymin < 0 || ymin > MAX_Y_COORDINATE || ymax < 0 || ymax > MAX_Y_COORDINATE|| 
		   zmin < -29999960 || zmin > 29999960 || zmax < -29999960 || zmax > 29999960) {
			System.out.println("This version of Minecraft only allows blocks to be generated with x-coordinates between -29999983 and 29999983.");
			System.out.println("y-coordinates between 0 and 255,");
			System.out.println("and z-coordinates between -29999983 and 29999983, all inclusive.");
			System.out.println("Therefore, cannot generate in this range: ("+xmin+", "+ymin+", "+zmin+"), ("+xmax+", "+ymax+", "+zmax+")");
			throw new IllegalArgumentException("This version of Minecraft only allows blocks to be generated with y-coordinates between 0 and 255 inclusive.\nTherefore, cannot generate in this range: "+xmin+", "+ymin+", "+zmin+", "+xmax+", "+ymax+", "+zmax);
		}
	}
}
