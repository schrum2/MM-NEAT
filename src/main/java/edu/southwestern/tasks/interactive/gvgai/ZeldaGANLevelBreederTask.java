package edu.southwestern.tasks.interactive.gvgai;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.gvgai.GVGAIUtil;
import edu.southwestern.tasks.gvgai.GVGAIUtil.GameBundle;
import edu.southwestern.tasks.gvgai.zelda.ZeldaGANUtil;
import edu.southwestern.tasks.interactive.InteractiveGANLevelEvolutionTask;
import edu.southwestern.tasks.mario.gan.GANProcess;
import edu.southwestern.util.datastructures.ArrayUtil;
import edu.southwestern.util.datastructures.Pair;
import gvgai.core.game.BasicGame;
import gvgai.core.game.Game;
import gvgai.core.vgdl.VGDLFactory;
import gvgai.core.vgdl.VGDLParser;
import gvgai.core.vgdl.VGDLRegistry;
import gvgai.tracks.singlePlayer.tools.human.Agent;

/**
 * Evolve Zelda rooms using a GAN
 * 
 * @author Jacob Schrum
 */
public class ZeldaGANLevelBreederTask extends InteractiveGANLevelEvolutionTask {

	private static final String GAME_FILE = "zelda";
	private static final String FULL_GAME_FILE = LevelBreederTask.GAMES_PATH + GAME_FILE + ".txt";

	public ZeldaGANLevelBreederTask() throws IllegalAccessException {
		super();
		VGDLFactory.GetInstance().init();
		VGDLRegistry.GetInstance().init();
	}

	@Override
	protected String getWindowTitle() {
		return "ZeldaGAN Level Breeder";
	}

	/**
	 * Take the latent vector and use the ZeldaGAN to create a level,
	 * and then a GameBundle used for playing the game.
	 * @param phenotype Latent vector
	 * @return GameBundle for playing GVG-AI game
	 */
	public GameBundle setUpGameWithLevelFromLatentVector(ArrayList<Double> phenotype) {
		double[] latentVector = ArrayUtil.doubleArrayFromList(phenotype);
		String[] level = ZeldaGANUtil.generateGVGAILevelFromGAN(latentVector, new Point(8,8));
		int seed = 0; // TODO: Use parameter?
		Agent agent = new Agent();
		agent.setup(null, seed, true); // null = no log, true = human 
		Game game = new VGDLParser().parseGame(FULL_GAME_FILE); // Initialize the game	

		return new GameBundle(game, level, agent, seed, 0);
	}

	@Override
	protected BufferedImage getButtonImage(ArrayList<Double> phenotype, int width, int height, double[] inputMultipliers) {
		GameBundle bundle = setUpGameWithLevelFromLatentVector(phenotype);
		BufferedImage levelImage = GVGAIUtil.getLevelImage(((BasicGame) bundle.game), bundle.level, (Agent) bundle.agent, width, height, bundle.randomSeed);
		return levelImage;
	}

	@Override
	public void configureGAN() {
		GANProcess.type = GANProcess.GAN_TYPE.ZELDA;
	}

	@Override
	public String getGANModelParameterName() {
		return "zeldaGANModel";
	}

	@Override
	public Pair<Integer, Integer> resetAndReLaunchGAN(String model) {
		int oldLength = GANProcess.latentVectorLength(); // for old model
		// Need to parse the model name to find out the latent vector size
		String dropDataSource = model.substring(model.indexOf("_")+1);
		String dropEpochs = dropDataSource.substring(dropDataSource.indexOf("_")+1);
		String latentSize = dropEpochs.substring(0,dropEpochs.indexOf("."));
		int size = Integer.parseInt(latentSize);
		Parameters.parameters.setInteger("GANInputSize", size);

		GANProcess.terminateGANProcess();
		// Because Python process was terminated, latentVectorLength will reinitialize with the new params
		int newLength = GANProcess.latentVectorLength(); // new model
		return new Pair<>(oldLength, newLength);
	}

	@Override
	public String getGANModelDirectory() {
		return "src"+File.separator+"main"+File.separator+"python"+File.separator+"GAN"+File.separator+"ZeldaGAN";
	}

	@Override
	public void playLevel(ArrayList<Double> phenotype) {
		GameBundle bundle = setUpGameWithLevelFromLatentVector(phenotype);
		// Must launch game in own thread, or won't animate or listen for events
		new Thread() {
			public void run() {
				// True is to watch the game being played
				GVGAIUtil.runOneGame(bundle, true);
			}
		}.start();
	}

	public static void main(String[] args) {
		try {
			MMNEAT.main(new String[]{"runNumber:0","randomSeed:1","trials:1","mu:16","zeldaGANModel:ZeldaDungeonsAll_5000_10.pth","maxGens:500","io:false","netio:false","GANInputSize:10","mating:true","fs:false","task:edu.southwestern.tasks.interactive.gvgai.ZeldaGANLevelBreederTask","genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype","watch:false","cleanFrequency:-1","simplifiedInteractiveInterface:false","saveAllChampions:true","cleanOldNetworks:false","ea:edu.southwestern.evolution.selectiveBreeding.SelectiveBreedingEA","imageWidth:2000","imageHeight:2000","imageSize:200"});
		} catch (FileNotFoundException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}


}
