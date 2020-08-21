package edu.southwestern.tasks.interactive.mario;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ch.idsia.ai.agents.Agent;
import ch.idsia.ai.agents.human.HumanKeyboardAgent;
import ch.idsia.mario.engine.level.Level;
import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.interactive.InteractiveGANLevelEvolutionTask;
import edu.southwestern.tasks.mario.gan.GANProcess;
import edu.southwestern.tasks.mario.gan.MarioGANUtil;
import edu.southwestern.tasks.mario.level.MarioLevelUtil;
import edu.southwestern.util.datastructures.ArrayUtil;
import edu.southwestern.util.datastructures.Pair;

/**
 * Interactively evolve Mario levels
 * in the latent space of a GAN.
 * 
 * @author Jacob
 *
 * @param <T>
 */
public class MarioGANLevelBreederTask extends InteractiveGANLevelEvolutionTask {

	public static final int LEVEL_MIN_CHUNKS = 1;
	public static final int LEVEL_MAX_CHUNKS = 10;

	private boolean initializationComplete = false;
	protected JSlider levelChunksSlider; // Allows for changing levelWidth

	public MarioGANLevelBreederTask() throws IllegalAccessException {
		super();

		//Construction of JSlider to determine number of latent vector level chunks
		levelChunksSlider = new JSlider(JSlider.HORIZONTAL, LEVEL_MIN_CHUNKS, LEVEL_MAX_CHUNKS, Parameters.parameters.integerParameter("marioGANLevelChunks"));
		levelChunksSlider.setToolTipText("Determines the number of distinct latent vectors that are sent to the GAN to create level chunks which are patched together into a single level.");
		levelChunksSlider.setMinorTickSpacing(1);
		levelChunksSlider.setPaintTicks(true);
		Hashtable<Integer,JLabel> labels = new Hashtable<>();
		JLabel shorter = new JLabel("Shorter Level");
		JLabel longer = new JLabel("Longer Level");
		if(Parameters.parameters.booleanParameter("bigInteractiveButtons")) {
			shorter.setFont(new Font("Arial", Font.PLAIN, 23));
			longer.setFont(new Font("Arial", Font.PLAIN, 23));
		}
		labels.put(LEVEL_MIN_CHUNKS, shorter);
		labels.put(LEVEL_MAX_CHUNKS, longer);
		levelChunksSlider.setLabelTable(labels);
		levelChunksSlider.setPaintLabels(true);
		levelChunksSlider.setPreferredSize(new Dimension((int)(200 * (Parameters.parameters.booleanParameter("bigInteractiveButtons") ? 1.4 : 1)), 40 * (Parameters.parameters.booleanParameter("bigInteractiveButtons") ? 2 : 1)));

		/**
		 * Changed level width picture previews
		 */
		levelChunksSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if(!initializationComplete) return;
				// get value
				JSlider source = (JSlider)e.getSource();
				if(!source.getValueIsAdjusting()) {

					int oldValue = Parameters.parameters.integerParameter("marioGANLevelChunks");
					int newValue = (int) source.getValue();
					Parameters.parameters.setInteger("marioGANLevelChunks", newValue);

					if(oldValue != newValue) {
						int oldLength = oldValue * GANProcess.latentVectorLength();
						int newLength = newValue * GANProcess.latentVectorLength();
						resizeGenotypeVectors(oldLength, newLength);
						// reset buttons
						resetButtons(true);
					}
				}
			}
		});

		if(!Parameters.parameters.booleanParameter("simplifiedInteractiveInterface")) {
			top.add(levelChunksSlider);	
		}

		initializationComplete = true;
	}

	@Override
	protected String getWindowTitle() {
		return "MarioGAN Level Breeder";
	}

	@Override
	protected BufferedImage getButtonImage(ArrayList<Double> phenotype, int width, int height, double[] inputMultipliers) {
		double[] doubleArray = ArrayUtil.doubleArrayFromList(phenotype);
		Level level = MarioGANUtil.generateLevelFromGAN(doubleArray);
		BufferedImage image = MarioLevelUtil.getLevelImage(level);
		return image;
	}

	@Override
	public void configureGAN() {
		GANProcess.type = GANProcess.GAN_TYPE.MARIO;
	}

	@Override
	public String getGANModelParameterName() {
		return "marioGANModel";
	}

	@Override
	public Pair<Integer, Integer> resetAndReLaunchGAN(String model) {
		return staticResetAndReLaunchGAN(model);
	}
	
	public static Pair<Integer, Integer> staticResetAndReLaunchGAN(String model) {
		int marioGANLevelChunks = Parameters.parameters.integerParameter("marioGANLevelChunks");
		int oldLength = marioGANLevelChunks * GANProcess.latentVectorLength(); // for old model
		if(model.equals("GECCO2018GAN_World1-1_32_Epoch5000.pth")) {
			Parameters.parameters.setInteger("GANInputSize", 32); // Default latent vector size
			Parameters.parameters.setBoolean("marioGANUsesOriginalEncoding", true);
		} else if(model.startsWith("GECCOEncoding")) { // Old encoding, but created after GECCO
			// Need to parse the model name to find out the latent vector size
			String dropEncodingLabel = model.substring(model.indexOf("_")+1);
			String dropDataSource = dropEncodingLabel.substring(model.indexOf("_")+1);
			String dropType = dropDataSource.substring(dropDataSource.indexOf("_")+1);
			String latentSize = dropType.substring(0,dropType.indexOf("_"));
			int size = Integer.parseInt(latentSize);
			Parameters.parameters.setInteger("GANInputSize", size);
			Parameters.parameters.setBoolean("marioGANUsesOriginalEncoding", true);
		} else {
			// Need to parse the model name to find out the latent vector size
			String dropDataSource = model.substring(model.indexOf("_")+1);
			String dropType = dropDataSource.substring(dropDataSource.indexOf("_")+1);
			String latentSize = dropType.substring(0,dropType.indexOf("_"));
			int size = Integer.parseInt(latentSize);
			Parameters.parameters.setInteger("GANInputSize", size);
			Parameters.parameters.setBoolean("marioGANUsesOriginalEncoding", false);
		}
		GANProcess.terminateGANProcess();
		// Because Python process was terminated, latentVectorLength will reinitialize with the new params
		int newLength = marioGANLevelChunks * GANProcess.latentVectorLength(); // new model
		return new Pair<>(oldLength,newLength);
	}

	@Override
	public String getGANModelDirectory() {
		return "src"+File.separator+"main"+File.separator+"python"+File.separator+"GAN"+File.separator+"MarioGAN";
	}

	@Override
	public void playLevel(ArrayList<Double> phenotype) {
		double[] doubleArray = ArrayUtil.doubleArrayFromList(phenotype);
		Level level = MarioGANUtil.generateLevelFromGAN(doubleArray);
		Agent agent = new HumanKeyboardAgent();
		// Must launch game in own thread, or won't animate or listen for events
		new Thread() {
			public void run() {
				MarioLevelUtil.agentPlaysLevel(level, agent);
			}
		}.start();
	}

	@Override
	protected void save(String file, int i) {
		ArrayList<Double> latentVector = scores.get(i).individual.getPhenotype();
		double[] doubleArray = ArrayUtil.doubleArrayFromList(latentVector);
		ArrayList<List<Integer>> levelList = MarioGANUtil.generateLevelListRepresentationFromGAN(doubleArray);
		String[] level = MarioGANUtil.generateTextLevel(levelList); // Generate the level from 2D integers

		// Prepare text file
		try {
			PrintStream ps = new PrintStream(new File(file));
//			ps.println(Parameters.parameters.stringParameter("marioGANModel"));
			// Write String array to text file 
			for(String row : level) {
				ps.println(row);
			}
			ps.close();
		} catch (FileNotFoundException e) {
			System.out.println("Could not save file: " + file);
			e.printStackTrace();
			return;
		}
	}
	
	public static void main(String[] args) {
		try {
			MMNEAT.main(new String[]{"runNumber:0","randomSeed:1","bigInteractiveButtons:true","drawMarioOverlayText:false","showKLOptions:false","trials:1","mu:16","maxGens:500","io:false","netio:false","mating:true","fs:false","task:edu.southwestern.tasks.interactive.mario.MarioGANLevelBreederTask","watch:true","cleanFrequency:-1","genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype","simplifiedInteractiveInterface:false","saveAllChampions:true","ea:edu.southwestern.evolution.selectiveBreeding.SelectiveBreedingEA","imageWidth:2000","imageHeight:2000","imageSize:200"});
		} catch (FileNotFoundException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<List<Integer>> levelListRepresentation(double[] latentVector) {
		return MarioGANUtil.generateLevelListRepresentationFromGAN(latentVector);
	}

}
