package edu.southwestern.tasks.gvgai.zelda;

import java.awt.Point;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.gvgai.GVGAIUtil;
import edu.southwestern.tasks.mario.gan.GANProcess;
import edu.southwestern.tasks.mario.gan.reader.JsonReader;
import edu.southwestern.util.random.RandomNumbers;
import gvgai.core.game.Game;
import gvgai.core.vgdl.VGDLFactory;
import gvgai.core.vgdl.VGDLParser;
import gvgai.core.vgdl.VGDLRegistry;
import gvgai.tracks.singlePlayer.tools.human.Agent;

public class ZeldaGANUtil {

	/**
	 * Generate a Zelda room with the GAN, and then convert it to a String representation
	 * that GVG-AI can turn into a level to play.
	 * @param latentVector Vector that generates level
	 * @param startLocation Where to place Zelda avatar in the level
	 * @return String representation
	 */
	public static String[] generateGVGAILevelFromGAN(double[] latentVector, Point startLocation) {
		List<List<Integer>> room = generateRoomListRepresentationFromGAN(latentVector);
		return ZeldaVGLCUtil.convertZeldaRoomListtoGVGAI(room, startLocation);
	}

	/**
	 * Get one room in list form from a latent vector using the GAN.
	 * The GANProcess type must be set to ZELDA before executing this method.
	 * @param latentVector Latent vector to generate room
	 * @return One room in list form
	 */
	private static List<List<Integer>> generateRoomListRepresentationFromGAN(double[] latentVector) {
		assert GANProcess.type.equals(GANProcess.GAN_TYPE.ZELDA);
		latentVector = GANProcess.mapArrayToOne(latentVector); // Range restrict the values
		// Generate room from vector
		try {
        	GANProcess.getGANProcess().commSend("[" + Arrays.toString(latentVector) + "]");
        } catch (IOException e) {
        	e.printStackTrace();
        	System.exit(1); // Cannot continue without the GAN process
        }
        String oneRoom = GANProcess.getGANProcess().commRecv(); // Response to command just sent
        oneRoom = "["+oneRoom+"]"; // Wrap room in another json array
        // Create one room in a list
        List<List<List<Integer>>> roomInList = JsonReader.JsonToInt(oneRoom);
		return roomInList.get(0); // Only contains one room
	}
	
	
	/**
	 * For quick tests
	 * @param args
	 */
	public static void main(String[] args) {
		int size = 10;
		Parameters.initializeParameterCollections(new String[] {"GANInputSize:"+size,"zeldaGANModel:ZeldaDungeon02_5000_10.pth"});
		//MMNEAT.loadClasses();
		
		VGDLFactory.GetInstance().init();
		VGDLRegistry.GetInstance().init();

		String game = "zelda";
		String gamesPath = "data/gvgai/examples/gridphysics/";
		String game_file = gamesPath + game + ".txt";
		int playerID = 0;
		int seed = 0;

		GANProcess.type = GANProcess.GAN_TYPE.ZELDA;
		double[] latentVector = RandomNumbers.randomArray(size);
		String[] level = generateGVGAILevelFromGAN(latentVector, new Point(8,8));
		
		for(String line : level) {
			System.out.println(line);
		}
		
		Agent agent = new Agent();
		agent.setup(null, 0, true); // null = no log, true = human 

		Game toPlay = new VGDLParser().parseGame(game_file); // Initialize the game
		GVGAIUtil.runOneGame(toPlay, level, true, agent, seed, playerID);

	}
}
