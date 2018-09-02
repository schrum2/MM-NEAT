package edu.southwestern.tasks.interactive.mario;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import ch.idsia.ai.agents.Agent;
import ch.idsia.ai.agents.human.HumanKeyboardAgent;
import ch.idsia.mario.engine.level.Level;
import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.scores.Score;
import edu.southwestern.tasks.interactive.InteractiveEvolutionTask;
import edu.southwestern.tasks.mario.gan.GANProcess;
import edu.southwestern.tasks.mario.gan.MarioGANUtil;
import edu.southwestern.tasks.mario.level.MarioLevelUtil;
import edu.southwestern.util.datastructures.ArrayUtil;

/**
 * Interactively evolve Mario levels
 * in the latent space of a GAN.
 * 
 * @author Jacob
 *
 * @param <T>
 */
public class MarioGANLevelBreederTask extends InteractiveEvolutionTask<ArrayList<Double>> {

	// Should exceed any of the CPPN inputs or other interface buttons
	public static final int PLAY_BUTTON_INDEX = -20; 
	private static final int FILE_LOADER_BUTTON_INDEX = -21;

	public static final int LEVEL_MIN_CHUNKS = 1;
	public static final int LEVEL_MAX_CHUNKS = 10;

	private boolean initializationComplete = false;
	protected JSlider levelChunksSlider; // Allows for changing levelWidth

	public MarioGANLevelBreederTask() throws IllegalAccessException {
		super(false); // false indicates that we are NOT evolving CPPNs
		GANProcess.type = GANProcess.GAN_TYPE.MARIO;

		//Construction of JSlider to determine number of latent vector level chunks
		levelChunksSlider = new JSlider(JSlider.HORIZONTAL, LEVEL_MIN_CHUNKS, LEVEL_MAX_CHUNKS, Parameters.parameters.integerParameter("marioGANLevelChunks"));
		levelChunksSlider.setMinorTickSpacing(1);
		levelChunksSlider.setPaintTicks(true);
		Hashtable<Integer,JLabel> labels = new Hashtable<>();
		labels.put(LEVEL_MIN_CHUNKS, new JLabel("Shorter Level"));
		labels.put(LEVEL_MAX_CHUNKS, new JLabel("Longer Level"));
		levelChunksSlider.setLabelTable(labels);
		levelChunksSlider.setPaintLabels(true);
		levelChunksSlider.setPreferredSize(new Dimension(200, 40));

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

		JButton fileLoadButton = new JButton();
		fileLoadButton.setText("SelectGANModel");
		fileLoadButton.setName("" + FILE_LOADER_BUTTON_INDEX);
		fileLoadButton.addActionListener(this);

		if(!Parameters.parameters.booleanParameter("simplifiedInteractiveInterface")) {
			top.add(levelChunksSlider);	
			top.add(fileLoadButton);
		}

		//Construction of button that lets user plays the level
		JButton play = new JButton("Play");
		// Name is first available numeric label after the input disablers
		play.setName("" + PLAY_BUTTON_INDEX);
		play.addActionListener(this);
		top.add(play);
		initializationComplete = true;
	}

	@Override
	public String[] sensorLabels() {
		return new String[0];
		//throw new UnsupportedOperationException("Genotypes for MarioGAN are actually not networks, so there are no sensor labels");
	}

	@Override
	public String[] outputLabels() {
		return new String[0];
		//throw new UnsupportedOperationException("Genotypes for MarioGAN are actually not networks, so there are no output labels");
	}

	@Override
	protected String getWindowTitle() {
		return "MarioGAN Level Breeder";
	}

	@Override
	protected void save(String file, int i) {
		ArrayList<Double> latentVector = scores.get(i).individual.getPhenotype();

		/**
		 * Rather than save a text representation of the level, I simply save
		 * the latent vector and the model name, which are sufficient to
		 * recreate any level
		 */
		//		double[] doubleArray = ArrayUtil.doubleArrayFromList(latentVector);
		//		ArrayList<List<Integer>> levelList = MarioGANUtil.generateLevelListRepresentationFromGAN(doubleArray);
		//
		//		throw new UnsupportedOperationException("MarioGAN Levels cannot be saved yet");

		// Transfer list into String array
		//		String[] level = null; // TODO
		//		
		// Prepare text file
		try {
			PrintStream ps = new PrintStream(new File(file));
			// Write String array to text file 
			ps.println(Parameters.parameters.stringParameter("marioGANModel"));
			ps.println(latentVector);
			ps.close();
		} catch (FileNotFoundException e) {
			System.out.println("Could not save file: " + file);
			e.printStackTrace();
			return;
		}
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
		double[] doubleArray = ArrayUtil.doubleArrayFromList(phenotype);
		Level level = MarioGANUtil.generateLevelFromGAN(doubleArray);
		BufferedImage image = MarioLevelUtil.getLevelImage(level);
		return image;
	}

	/**
	 * Responds to a button to actually play a selected level
	 */
	protected boolean respondToClick(int itemID) {
		boolean undo = super.respondToClick(itemID);
		if(undo) return true; // Click must have been a bad activation checkbox choice. Skip rest
		// Human plays level
		if(itemID == PLAY_BUTTON_INDEX && selectedItems.size() > 0) {
			ArrayList<Double> phenotype = scores.get(selectedItems.get(selectedItems.size() - 1)).individual.getPhenotype();
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
		if(itemID == FILE_LOADER_BUTTON_INDEX) {
			JFileChooser chooser = new JFileChooser();//used to get new file
			chooser.setApproveButtonText("Open");
			FileNameExtensionFilter filter = new FileNameExtensionFilter("GAN Model", "pth");
			chooser.setFileFilter(filter);
			// This is where all the GANs are stored (only allowable spot)
			chooser.setCurrentDirectory(new File("src\\main\\python\\GAN\\MarioGAN"));
			int returnVal = chooser.showOpenDialog(frame);
			if(returnVal == JFileChooser.APPROVE_OPTION) {//if the user decides to save the image
				int marioGANLevelChunks = Parameters.parameters.integerParameter("marioGANLevelChunks");
				int oldLength = marioGANLevelChunks * GANProcess.latentVectorLength(); // for old model

				String model = chooser.getSelectedFile().getName();
				Parameters.parameters.setString("marioGANModel", model);
				if(model.equals("GECCO2018GAN_World1-1_32_Epoch5000.pth")) {
					Parameters.parameters.setInteger("GANInputSize", 32); // Default latent vector size
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
				resizeGenotypeVectors(oldLength, newLength);
			}
			// reset necessary?
			resetButtons(true);
		}

		return false; // no undo: every thing is fine
	}	

	/**
	 * Resize the vectors as a result of slider changes or changing the GAN model.
	 * Some similarity is attempted despite the transformation, but this should mostly
	 * be used before much evolution occurs.
	 * 
	 * @param oldLength
	 * @param newLength
	 */
	private void resizeGenotypeVectors(int oldLength, int newLength) {
		if(oldLength != newLength) {
			// Modify all genotypes' lengths accordingly. This means chopping off,
			// or elongating by duplicating
			for(Score<ArrayList<Double>> s : scores) {
				ArrayList<Double> oldPhenotype = s.individual.getPhenotype();
				ArrayList<Double> newPhenotype = null;
				if(newLength < oldLength) { // Get sublist
					newPhenotype = new ArrayList<>(oldPhenotype.subList(0, newLength));
				} else if(newLength > oldLength) { // Repeat copies of the original
					newPhenotype = new ArrayList<>(oldPhenotype); // Start with original
					while(newPhenotype.size() < newLength) {
						// Add a full copy (oldLength), or as much as is needed to reach the new length (difference from current size)
						newPhenotype.addAll(oldPhenotype.subList(0, Math.min(oldLength, newLength - newPhenotype.size())));
					}
				} else { // Possible when switching between different models with same latent vector length
					throw new IllegalArgumentException("Should not be possible");
				}
				s.individual = new BoundedRealValuedGenotype(newPhenotype,MMNEAT.getLowerBounds(),MMNEAT.getUpperBounds());
			}
		}
	}

	@Override
	protected void additionalButtonClickAction(int scoreIndex, Genotype<ArrayList<Double>> individual) {
		// do nothing
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
			MMNEAT.main(new String[]{"runNumber:0","randomSeed:1","trials:1","mu:16","maxGens:500","io:false","netio:false","mating:true","fs:false","task:edu.southwestern.tasks.interactive.mario.MarioGANLevelBreederTask","watch:true","cleanFrequency:-1","genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype","simplifiedInteractiveInterface:false","saveAllChampions:true","ea:edu.southwestern.evolution.selectiveBreeding.SelectiveBreedingEA","imageWidth:2000","imageHeight:2000","imageSize:200"});
		} catch (FileNotFoundException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}
}
