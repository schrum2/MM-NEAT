package edu.southwestern.evocraft;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.ProcessBuilder.Redirect;

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

	public static class Block {
		Triple<Integer,Integer,Integer> position;
	}

	public void spawnBlocks(List<>) {

	}
}
