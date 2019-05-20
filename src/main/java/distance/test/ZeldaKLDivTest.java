package distance.test;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import distance.convolution.ConvNTuple;
import distance.kl.KLDiv;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.gvgai.GVGAIUtil;
import edu.southwestern.tasks.gvgai.GVGAIUtil.GameBundle;
import edu.southwestern.tasks.gvgai.zelda.ZeldaGANUtil;
import edu.southwestern.tasks.interactive.gvgai.LevelBreederTask;
import edu.southwestern.tasks.mario.gan.GANProcess;
import edu.southwestern.util.datastructures.ArrayUtil;
import edu.southwestern.util.graphics.DrawingPanel;
import edu.southwestern.util.random.RandomNumbers;
import gvgai.core.game.BasicGame;
import gvgai.core.game.Game;
import gvgai.core.vgdl.VGDLFactory;
import gvgai.core.vgdl.VGDLParser;
import gvgai.core.vgdl.VGDLRegistry;
import gvgai.tracks.singlePlayer.tools.human.Agent;

public class ZeldaKLDivTest {

	private static final String GAME_FILE = "zelda";
	private static final String FULL_GAME_FILE = LevelBreederTask.GAMES_PATH + GAME_FILE + ".txt";
	
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
		double[] latentVector1 = RandomNumbers.randomArray(size);
		//String[] level1 = generateGVGAILevelFromGAN(latentVector1, new Point(8,8));
		double[] latentVector2 = RandomNumbers.randomArray(size);
		//String[] level2 = generateGVGAILevelFromGAN(latentVector2, new Point(8,8));
		
		ArrayList<Double> phenotype1 = new ArrayList<>();
		for(double x: latentVector1) {
			phenotype1.add(x);
		}
		ArrayList<Double> phenotype2 = new ArrayList<>();
		for(double x: latentVector2) {
			phenotype2.add(x);
		}
		
		drawLevel(phenotype1, "Level 1");
		drawLevel(phenotype2, "Level 2").setLocation(300, 0);
		
		int[][] level1 = getArrayLevel(phenotype1);
		printLevel(level1);
		System.out.println("-----------------");
		int[][] level2 = getArrayLevel(phenotype2);
		printLevel(level2);
		
	    final int KL_FILTER_WIDTH = 2;
	    final int KL_FILTER_HEIGHT = 2;
	    final int KL_STRIDE = 1;
		
		ConvNTuple c1 = KLDivTest.getConvNTuple(level1, KL_FILTER_WIDTH, KL_FILTER_HEIGHT, KL_STRIDE);
		ConvNTuple c2 = KLDivTest.getConvNTuple(level2, KL_FILTER_WIDTH, KL_FILTER_HEIGHT, KL_STRIDE);

		double klDiv = KLDiv.klDiv(c1.sampleDis, c2.sampleDis);
		String result = "KL Div: " + String.format("%10.6f", klDiv);
		System.out.println(result);

		klDiv = KLDiv.klDiv(c2.sampleDis, c1.sampleDis);
		result = "KL Div: " + String.format("%10.6f", klDiv);
		System.out.println(result);
		
		klDiv = KLDiv.klDivSymmetric(c1.sampleDis, c2.sampleDis);
		result = "Symmetric KL Div: " + String.format("%10.6f", klDiv);
		System.out.println(result);

		
//		for(String line : level) {
//			System.out.println(line);
//		}
//		
//		Agent agent = new Agent();
//		agent.setup(null, 0, true); // null = no log, true = human 
//
//		Game toPlay = new VGDLParser().parseGame(game_file); // Initialize the game
//		GVGAIUtil.runOneGame(toPlay, level, true, agent, seed, playerID);

	}

	/**
	 * @param phenotype
	 */
	private static DrawingPanel drawLevel(ArrayList<Double> phenotype, String label) {
		int picSize = 300;
		GameBundle bundle = setUpGameWithLevelFromLatentVector(phenotype);
		BufferedImage levelImage = GVGAIUtil.getLevelImage(((BasicGame) bundle.game), bundle.level, (Agent) bundle.agent, picSize, picSize, bundle.randomSeed);
		DrawingPanel dp1 = new DrawingPanel(picSize,picSize,label);
		dp1.image.setData(levelImage.getData());
		return dp1;
	}

	/**
	 * @param level
	 */
	private static void printLevel(int[][] level) {
		for(int i = 0; i < level.length; i++) {
			for(int j = 0; j < level[i].length; j++) {
				System.out.print(level[i][j]);
			}
			System.out.println();
		}
		System.out.println();
	}
	
	// Only for testing
	public static int[][] getArrayLevel(ArrayList<Double> phenotype) {
		double[] doubleArray = ArrayUtil.doubleArrayFromList(phenotype);
		List<List<Integer>> oneLevel = levelListRepresentation(doubleArray);
		int[][] level = new int[oneLevel.size()][oneLevel.get(0).size()];
		// Convert form lists to 2D array
		for(int row = 0; row < oneLevel.size(); row++) {
			//System.out.println(oneLevel.get(row));
			for(int col = 0; col < oneLevel.get(0).size(); col++) {
				level[row][col] = oneLevel.get(row).get(col);
			}
		}
		return level;
	}
	
	public static List<List<Integer>> levelListRepresentation(double[] latentVector) {
		return ZeldaGANUtil.generateRoomListRepresentationFromGAN(latentVector);
	}

	public static GameBundle setUpGameWithLevelFromLatentVector(ArrayList<Double> phenotype) {
		double[] latentVector = ArrayUtil.doubleArrayFromList(phenotype);
		String[] level = ZeldaGANUtil.generateGVGAILevelFromGAN(latentVector, new Point(8,8));
		int seed = 0; // TODO: Use parameter?
		Agent agent = new Agent();
		agent.setup(null, seed, true); // null = no log, true = human 
		Game game = new VGDLParser().parseGame(FULL_GAME_FILE); // Initialize the game	

		return new GameBundle(game, level, agent, seed, 0);
	}
}
