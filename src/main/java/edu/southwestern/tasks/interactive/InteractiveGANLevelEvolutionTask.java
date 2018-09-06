package edu.southwestern.tasks.interactive;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.scores.Score;
import edu.southwestern.util.datastructures.Pair;

/**
 * Use a GAN to evolve levels for some game.
 * @author Jacob Schrum
 *
 */
public abstract class InteractiveGANLevelEvolutionTask extends InteractiveEvolutionTask<ArrayList<Double>> {

	// Should exceed any of the CPPN inputs or other interface buttons
	public static final int PLAY_BUTTON_INDEX = -20; 
	private static final int FILE_LOADER_BUTTON_INDEX = -21;

	/**
	 * Do domain specific GAN settings
	 */
	public abstract void configureGAN();

	/**
	 * Return the String parameter label that has the file name of the GAN model
	 * @return
	 */
	public abstract String getGANModelParameterName();
	
	public InteractiveGANLevelEvolutionTask() throws IllegalAccessException {
		super(false); // false indicates that we are NOT evolving CPPNs
		configureGAN();

		JButton fileLoadButton = new JButton();
		fileLoadButton.setText("SelectGANModel");
		fileLoadButton.setName("" + FILE_LOADER_BUTTON_INDEX);
		fileLoadButton.addActionListener(this);

		if(!Parameters.parameters.booleanParameter("simplifiedInteractiveInterface")) {
			top.add(fileLoadButton);
		}

		//Construction of button that lets user plays the level
		JButton play = new JButton("Play");
		// Name is first available numeric label after the input disablers
		play.setName("" + PLAY_BUTTON_INDEX);
		play.addActionListener(this);
		top.add(play);
	}

	@Override
	public String[] sensorLabels() {
		return new String[0]; // Not a network task, so there are no sensor labels
	}

	@Override
	public String[] outputLabels() {
		return new String[0]; // Not a network task, so there are no output labels
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
			ps.println(Parameters.parameters.stringParameter(getGANModelParameterName()));
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

	/**
	 * Responds to a button to actually play a selected level
	 */
	protected boolean respondToClick(int itemID) {
		boolean undo = super.respondToClick(itemID);
		if(undo) return true; // Click must have been a bad activation checkbox choice. Skip rest
		// Human plays level
		if(itemID == PLAY_BUTTON_INDEX && selectedItems.size() > 0) {
			ArrayList<Double> phenotype = scores.get(selectedItems.get(selectedItems.size() - 1)).individual.getPhenotype();
			playLevel(phenotype);
		}
		if(itemID == FILE_LOADER_BUTTON_INDEX) {
			JFileChooser chooser = new JFileChooser();//used to get new file
			chooser.setApproveButtonText("Open");
			FileNameExtensionFilter filter = new FileNameExtensionFilter("GAN Model", "pth");
			chooser.setFileFilter(filter);
			// This is where all the GANs are stored (only allowable spot)
			chooser.setCurrentDirectory(new File(getGANModelDirectory()));
			int returnVal = chooser.showOpenDialog(frame);
			if(returnVal == JFileChooser.APPROVE_OPTION) {//if the user decides to save the image
				String model = chooser.getSelectedFile().getName();
				Parameters.parameters.setString(getGANModelParameterName(), model);
				Pair<Integer, Integer> lengths = resetAndReLaunchGAN(model);
				resizeGenotypeVectors(lengths.t1, lengths.t2);
			}
			resetButtons(true);
		}

		return false; // no undo: every thing is fine
	}

	/**
	 * Given the name of the GAN to load, terminate the current GAN and reconfigure before
	 * launcing a new one. Returns a pair containing both the old latent vector length
	 * and the net latent vector length for the chosen model.
	 * @param model
	 * @return
	 */
	public abstract Pair<Integer, Integer> resetAndReLaunchGAN(String model);
	
	/**
	 * Where are GAN models for this particulay domain saved?
	 * @return
	 */
	public abstract String getGANModelDirectory();

	/**
	 * Play a level generated by the GAN
	 * @param phenotype Latent vector as array list
	 */
	public abstract void playLevel(ArrayList<Double> phenotype);

	/**
	 * Resize the vectors as a result of slider changes or changing the GAN model.
	 * Some similarity is attempted despite the transformation, but this should mostly
	 * be used before much evolution occurs.
	 * 
	 * @param oldLength
	 * @param newLength
	 */
	public void resizeGenotypeVectors(int oldLength, int newLength) {
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
}
