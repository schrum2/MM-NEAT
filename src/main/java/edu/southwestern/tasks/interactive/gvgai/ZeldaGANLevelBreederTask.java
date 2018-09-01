package edu.southwestern.tasks.interactive.gvgai;

import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;

import javax.swing.JButton;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.gvgai.GVGAIUtil;
import edu.southwestern.tasks.gvgai.GVGAIUtil.GameBundle;
import edu.southwestern.tasks.gvgai.zelda.ZeldaGANUtil;
import edu.southwestern.tasks.interactive.InteractiveEvolutionTask;
import edu.southwestern.tasks.mario.gan.GANProcess;
import edu.southwestern.util.datastructures.ArrayUtil;
import gvgai.core.game.BasicGame;
import gvgai.core.game.Game;
import gvgai.core.vgdl.VGDLFactory;
import gvgai.core.vgdl.VGDLParser;
import gvgai.core.vgdl.VGDLRegistry;
import gvgai.tracks.singlePlayer.tools.human.Agent;

public class ZeldaGANLevelBreederTask extends InteractiveEvolutionTask<ArrayList<Double>> {
	// Should exceed any of the CPPN inputs or other interface buttons
	public static final int PLAY_BUTTON_INDEX = -20; 
	
	private static final String gameFile = "zelda";
	private static final String fullGameFile = LevelBreederTask.GAMES_PATH + gameFile + ".txt";
	
	public ZeldaGANLevelBreederTask() throws IllegalAccessException {
		super(false);

		GANProcess.type = GANProcess.GAN_TYPE.ZELDA;
		VGDLFactory.GetInstance().init();
		VGDLRegistry.GetInstance().init();
				
		//Construction of button that lets user plays the level
		JButton play = new JButton("Play");
		// Name is first available numeric label after the input disablers
		play.setName("" + PLAY_BUTTON_INDEX);
		play.addActionListener(this);
		top.add(play);
		top.setLayout(new FlowLayout());
	}

	@Override
	public String[] sensorLabels() {
		return new String[0]; // Not used for latent variable evolution
	}

	@Override
	public String[] outputLabels() {
		return new String[0]; // Not used for latent variable evolution
	}

	@Override
	protected String getWindowTitle() {
		return "ZeldaGAN Level Breeder";
	}

	@Override
	protected void save(String file, int i) {
		ArrayList<Double> latentVector = scores.get(i).individual.getPhenotype();
		
		/**
		 * Rather than save a text representation of the level, I simply save
		 * the latent vector and the model name, which are sufficient to
		 * recreate any level
		 */
		
		try {
			PrintStream ps = new PrintStream(new File(file));
			// Write String array to text file 
			ps.println(Parameters.parameters.stringParameter("zeldaGANModel"));
			ps.println(latentVector);
			ps.close();
		} catch (FileNotFoundException e) {
			System.out.println("Could not save file: " + file);
			e.printStackTrace();
			return;
		}

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
		Game game = new VGDLParser().parseGame(fullGameFile); // Initialize the game	

		return new GameBundle(game, level, agent, seed, 0);
	}
	
	/**
	 * Disallow image caching since this only applies to CPPNs
	 */
	@Override
	protected BufferedImage getButtonImage(boolean checkCache, ArrayList<Double> phenotype, int width, int height, double[] inputMultipliers) {
		// Setting checkCache to false makes sure that the phenotype is not cast to a TWEANN in an attempt to acquire its ID
		return super.getButtonImage(false, phenotype, width, height, inputMultipliers);
	}

	@Override
	protected BufferedImage getButtonImage(ArrayList<Double> phenotype, int width, int height, double[] inputMultipliers) {
		GameBundle bundle = setUpGameWithLevelFromLatentVector(phenotype);
		BufferedImage levelImage = GVGAIUtil.getLevelImage(((BasicGame) bundle.game), bundle.level, (Agent) bundle.agent, width, height, bundle.randomSeed);
		return levelImage;
	}
		
	/**
	 * Responds to a button to actually play a selected level
	 */
	protected boolean respondToClick(int itemID) {
		boolean undo = super.respondToClick(itemID);
		if(undo) return true; // Click must have been a bad activation checkbox choice. Skip rest
		// Human plays level
		if(itemID == PLAY_BUTTON_INDEX && selectedItems.size() > 0) {
			ArrayList<Double> cppn = scores.get(selectedItems.get(selectedItems.size() - 1)).individual.getPhenotype();
			GameBundle bundle = setUpGameWithLevelFromLatentVector(cppn);
			// Must launch game in own thread, or won't animate or listen for events
			new Thread() {
				public void run() {
					// True is to watch the game being played
					GVGAIUtil.runOneGame(bundle, true);
					
				}
			}.start();
			System.out.println("Launched");
		}
		return false; // no undo: every thing is fine
	}

	@Override
	protected void additionalButtonClickAction(int scoreIndex, Genotype<ArrayList<Double>> individual) {
		// TODO Auto-generated method stub
	}

	@Override
	protected String getFileType() {
		return "Text File";
	}

	@Override
	protected String getFileExtension() {
		return "txt";
	}

	@Override
	public int numCPPNInputs() {
		throw new UnsupportedOperationException("There are no CPPNs, and therefore no inputs");
	}

	@Override
	public int numCPPNOutputs() {
		throw new UnsupportedOperationException("There are no CPPNs, and therefore no outputs");
	}

	public static void main(String[] args) {
		try {
			MMNEAT.main(new String[]{"runNumber:0","randomSeed:1","trials:1","mu:16","maxGens:500","io:false","netio:false","GANInputSize:10","mating:true","fs:false","task:edu.southwestern.tasks.interactive.gvgai.ZeldaGANLevelBreederTask","genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype","watch:false","cleanFrequency:-1","simplifiedInteractiveInterface:false","saveAllChampions:true","cleanOldNetworks:false","ea:edu.southwestern.evolution.selectiveBreeding.SelectiveBreedingEA","imageWidth:2000","imageHeight:2000","imageSize:200"});
		} catch (FileNotFoundException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

}
