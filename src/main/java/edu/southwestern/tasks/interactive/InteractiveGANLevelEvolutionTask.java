package edu.southwestern.tasks.interactive;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import distance.convolution.ConvNTuple;
import distance.kl.KLDiv;
import distance.test.KLDivTest;
import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.scores.Score;
import edu.southwestern.util.datastructures.ArrayUtil;
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
	private static final int VECTOR_EXPLORER_BUTTON_INDEX = -22;
	private static final int KL_DIV_BUTTON_INDEX = -23;
	private static final int INTERPOLATE_BUTTON_INDEX = -24;
	
	private static final int SLIDER_RANGE = 100; // Latent vector sliders (divide by this to get vector value)
	
    JLabel globalKLDivLabel1;
    JLabel globalKLDivLabel2;
    JLabel globalKLDivSymLabel;
    
    // Used by the interpolate button
    private ArrayList<Double> interpolatedPhenotype = null;
    
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
		
		JButton vectorExplorerButton = new JButton();
		vectorExplorerButton.setText("ExploreLatentSpace");
		vectorExplorerButton.setName("" + VECTOR_EXPLORER_BUTTON_INDEX);
		vectorExplorerButton.addActionListener(this);

		JButton interpolationButton = new JButton();
		interpolationButton.setText("Interpolate");
		interpolationButton.setName("" + INTERPOLATE_BUTTON_INDEX);
		interpolationButton.addActionListener(this);

		
		// Jacob: 2019-01-15
		// I'm removing the KL Div button because the latent space explorer already provides
		// this information in a better interface
		/**
		JButton klDivButton = new JButton();
		klDivButton.setText("KLDiv");
		klDivButton.setName("" + KL_DIV_BUTTON_INDEX);
		klDivButton.addActionListener(this);
		*/
		
		JSlider widthFilterSlider = klDivSlider("receptiveFieldWidth",1,6,"KL filter width");
		JSlider heightFilterSlider = klDivSlider("receptiveFieldHeight",1,6,"KL filter height");
		JSlider strideFilterSlider = klDivSlider("stride",1,6,"KL filter stride");
		
		if(!Parameters.parameters.booleanParameter("simplifiedInteractiveInterface")) {
			top.add(fileLoadButton);
			top.add(vectorExplorerButton);
			//top.add(klDivButton);
			top.add(interpolationButton);
			
			JPanel klSliders = new JPanel();
			klSliders.setLayout(new GridLayout(3,1));
			
			klSliders.add(widthFilterSlider);
			klSliders.add(heightFilterSlider);
			klSliders.add(strideFilterSlider);
			
			top.add(klSliders);
		}

		//Construction of button that lets user plays the level
		JButton play = new JButton("Play");
		// Name is first available numeric label after the input disablers
		play.setName("" + PLAY_BUTTON_INDEX);
		play.addActionListener(this);
		top.add(play);
	}

	/**
	 * @param paramLabel
	 * @return
	 */
	private JSlider klDivSlider(String paramLabel, int min, int max, String name) {
		JSlider filterSlider = new JSlider(JSlider.HORIZONTAL, min, max, Parameters.parameters.integerParameter(paramLabel));

		Hashtable<Integer,JLabel> labels = new Hashtable<>();

		filterSlider.setMinorTickSpacing(1);
		filterSlider.setPaintTicks(true);
		labels.put((min+max)/2, new JLabel(name));
		filterSlider.setLabelTable(labels);
		filterSlider.setPaintLabels(true);
		filterSlider.setPreferredSize(new Dimension(200, 40));

		filterSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider)e.getSource();
				Parameters.parameters.setInteger(paramLabel, source.getValue());
			}
			
		});
		return filterSlider;
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
	@SuppressWarnings("unchecked")
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
		
		// This button doesn't exist any more, since the latent space explorere handles it
		if(itemID == KL_DIV_BUTTON_INDEX) {
			// Compare every selected level with every other selected level
			for(Integer i : selectedItems) {
				for(Integer j : selectedItems) {
					System.out.println(klDivResults(i, j));
					System.out.println(klDivSymmetricResults(i,j));
				}
			}
		}
		if(itemID == VECTOR_EXPLORER_BUTTON_INDEX) {
			if(selectedItems.size() == 0) return false; // Nothing to explore
			
			JFrame explorer = new JFrame("Explore Latent Space");
			
			int itemToExplore = selectedItems.size() - 1;
			boolean compareTwo = selectedItems.size() > 1;
			// In case two levels are being compared, stack them:
			// There are three rows: one for each level, and one for KL Div info.
			if(compareTwo) explorer.getContentPane().setLayout(new GridLayout(2,1));
			
			addLevelToExploreToFrame(itemToExplore, explorer, compareTwo);
			
			// If there are at least two items, compare the last two:
			if(compareTwo) {
				System.out.println("Will compare two levels in explorer");
				addLevelToExploreToFrame(selectedItems.size() - 2, explorer, compareTwo);
			}
		}
		if(itemID == INTERPOLATE_BUTTON_INDEX) {
			if(selectedItems.size() < 2) {
				System.out.println("Select two items to interpolate between");
				return false; // Can only interpolate between two
			}
			
			JFrame explorer = new JFrame("Interpolate Between Vectors");
			explorer.getContentPane().setLayout(new GridLayout(1,3));
			
			final int leftItem = selectedItems.size() - 1;
			final int rightItem = selectedItems.size() - 2;
			
			final ArrayList<Double> leftPhenotype = scores.get(selectedItems.get(leftItem)).individual.getPhenotype();
			final ArrayList<Double> rightPhenotype = scores.get(selectedItems.get(rightItem)).individual.getPhenotype();
			
			// The interpolated result starts as the left level/vector
			interpolatedPhenotype = (ArrayList<Double>) leftPhenotype.clone();			
			final JLabel interpolatedImageLabel = getLevelImageLabel(2*picSize, interpolatedPhenotype);		
			
			// Show one level on the left
			final JLabel leftImageLabel = getLevelImageLabel(leftItem, picSize);
			final JLabel rightImageLabel = getLevelImageLabel(rightItem, picSize);
			
			// Add left image now. Right image added below.
			explorer.getContentPane().add(leftImageLabel);
			
			// In between is the level interpolated between
			JPanel interpolatedLevel = new JPanel();
			interpolatedLevel.setLayout(new BoxLayout(interpolatedLevel, BoxLayout.Y_AXIS));
			
			// Slider starts at 0 which is the left vector
			JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, SLIDER_RANGE, 0);
			slider.setMinorTickSpacing(1);
			slider.setPaintTicks(true);
			Hashtable<Integer,JLabel> labels = new Hashtable<>();
			labels.put(0, new JLabel("Left"));
			labels.put(SLIDER_RANGE, new JLabel("Right"));
			slider.setLabelTable(labels);
			slider.setPaintLabels(true);
			slider.setPreferredSize(new Dimension(200, 40));

			/**
			 * Changed level with picture previews
			 */
			slider.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					// get value
					JSlider source = (JSlider)e.getSource();
					if(!source.getValueIsAdjusting()) {
						int newValue = (int) source.getValue();
						double scaledValue = (1.0 * newValue) / SLIDER_RANGE;

						// Loop through the interpolated phenotype and set each position based on slider
						for(int i = 0; i < interpolatedPhenotype.size(); i++) {
							double left = leftPhenotype.get(i);
							double right = rightPhenotype.get(i);
							// Value between left and right
							double interpolated = left + scaledValue*(right - left);
							interpolatedPhenotype.set(i, interpolated);
						}
						
						// Update image
						ImageIcon img = getLevelImageIcon(2*picSize, interpolatedPhenotype);
						interpolatedImageLabel.setIcon(img);
					}
				}
			});

			interpolatedLevel.add(new JLabel("   ")); // Create some space
			
			// First the slider for interpolating
			interpolatedLevel.add(slider);
			
			// Then the image of the level
			interpolatedLevel.add(interpolatedImageLabel);

			interpolatedLevel.add(new JLabel("   ")); // Create some space
			
			JPanel buttons = new JPanel();
			
			JButton repalceLeft = new JButton("ReplaceLeft");
			repalceLeft.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// Set each position in left phenotype to interpolated phenotype value
					for(int i = 0; i < interpolatedPhenotype.size(); i++) {
						leftPhenotype.set(i, interpolatedPhenotype.get(i));
						ImageIcon img = getLevelImageIcon(picSize, leftPhenotype);
						leftImageLabel.setIcon(img);
						resetButton(scores.get(selectedItems.get(leftItem)).individual, selectedItems.get(leftItem));
					}
				}
			});
			
			JButton repalceRight = new JButton("ReplaceRight");
			repalceRight.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// Set each position in right phenotype to interpolated phenotype value
					for(int i = 0; i < interpolatedPhenotype.size(); i++) {
						rightPhenotype.set(i, interpolatedPhenotype.get(i));
						ImageIcon img = getLevelImageIcon(picSize, rightPhenotype);
						rightImageLabel.setIcon(img);
						resetButton(scores.get(selectedItems.get(rightItem)).individual, selectedItems.get(rightItem));
					}
				}
			});
			
			// Play the modified level
			JButton play = new JButton("Play");
			play.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					playLevel(interpolatedPhenotype);
				}
			});
			
			buttons.add(repalceLeft);
			buttons.add(play);
			buttons.add(repalceRight);
			
			// Then the option to play the interpolated level
			interpolatedLevel.add(buttons);

			// Place interface in middle
			explorer.getContentPane().add(interpolatedLevel);

			// Other level on the right
			explorer.getContentPane().add(rightImageLabel);
			
			explorer.pack();
			explorer.setVisible(true);

		}

		return false; // no undo: every thing is fine
	}

	/**
	 * 
	 * @param itemIndex Index in population
	 * @param picSize Size of image
	 * @return
	 */
	private JLabel getLevelImageLabel(int itemIndex, int picSize) {
		int leftPopulationIndex = selectedItems.get(itemIndex);
		ArrayList<Double> leftPhenotype = scores.get(leftPopulationIndex).individual.getPhenotype();
		// Image of level
		return getLevelImageLabel(picSize, leftPhenotype);
	}

	/**
	 * @param picSize Size of image
	 * @param phenotype Latent vector
	 * @return
	 */
	public JLabel getLevelImageLabel(int picSize, ArrayList<Double> phenotype) {
		ImageIcon img = getLevelImageIcon(picSize, phenotype);
		JLabel leftImageLabel = new JLabel(img);
		return leftImageLabel;
	}

	/**
	 * Get the ImageIcon to put on a JLabel
	 * @param picSize Image size
	 * @param phenotype latent vector
	 * @return
	 */
	public ImageIcon getLevelImageIcon(int picSize, ArrayList<Double> phenotype) {
		BufferedImage leftLevel = getButtonImage(false, phenotype, picSize,picSize, inputMultipliers);
		ImageIcon img = new ImageIcon(leftLevel.getScaledInstance(picSize,picSize,Image.SCALE_DEFAULT));
		return img;
	}

	/**
	 * Adds a view of a level and all sliders for tweaking it to a given JFrame. Might
	 * behave weirdly if called more than twice on any frame, but works for one or two.
	 * 
	 * @param itemToExplore Index in the population
	 * @param explorer The Frame to add to
	 * @param compareTwo Whether or not the last two members of the group of selected items
	 * 					are being compared in terms of KL Div
	 */
	public void addLevelToExploreToFrame(int itemToExplore, JFrame explorer, boolean compareTwo) {
		final int populationIndex = selectedItems.get(itemToExplore);
		final boolean compare = compareTwo;
		ArrayList<Double> phenotype = scores.get(populationIndex).individual.getPhenotype();
		// Image of level
		final JLabel imageLabel = getLevelImageLabel(2*picSize, phenotype);
		
		JPanel bothKLDivStrings = new JPanel();
		bothKLDivStrings.setLayout(new GridLayout(3,1));
		
		// Only allow one copy of each label to be visible
		if(globalKLDivLabel1 != null) globalKLDivLabel1.setText("");
		if(globalKLDivLabel2 != null) globalKLDivLabel2.setText("");
		if(globalKLDivSymLabel != null) globalKLDivSymLabel.setText("");
		// The hard-coded assumption here is that we always compare the last two items selected
		// Compare in both orders since KL Div not symmetric
		globalKLDivLabel1 = new JLabel(compare ? klDivResults(selectedItems.get(selectedItems.size() - 1), selectedItems.get(selectedItems.size() - 2)) : "");
		globalKLDivLabel2 = new JLabel(compare ? klDivResults(selectedItems.get(selectedItems.size() - 2), selectedItems.get(selectedItems.size() - 1)) : "");
		globalKLDivSymLabel = new JLabel(compare ? klDivSymmetricResults(selectedItems.get(selectedItems.size() - 2), selectedItems.get(selectedItems.size() - 1)) : "");
		bothKLDivStrings.add(globalKLDivLabel1);
		bothKLDivStrings.add(globalKLDivLabel2);
		bothKLDivStrings.add(globalKLDivSymLabel);
		
		JPanel vectorSliders = new JPanel();
		vectorSliders.setLayout(new GridLayout(10, phenotype.size() / 10));
		// Add a slider for each latent vector variable
		for(int i = 0; i < phenotype.size(); i++) {
			JSlider vectorValue = new JSlider(JSlider.HORIZONTAL, 0, SLIDER_RANGE, (int)(SLIDER_RANGE*phenotype.get(i)));
			vectorValue.setMinorTickSpacing(1);
			vectorValue.setPaintTicks(true);
			Hashtable<Integer,JLabel> labels = new Hashtable<>();
			labels.put(0, new JLabel("0.0"));
			labels.put(SLIDER_RANGE, new JLabel("1.0"));
			vectorValue.setLabelTable(labels);
			vectorValue.setPaintLabels(true);
			vectorValue.setPreferredSize(new Dimension(200, 40));

			/**
			 * Changed level width picture previews
			 */
			final int latentVariableIndex = i;
			vectorValue.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					// get value
					JSlider source = (JSlider)e.getSource();
					if(!source.getValueIsAdjusting()) {
						int newValue = (int) source.getValue();
						double scaledValue = (1.0 * newValue) / SLIDER_RANGE;
						// Actually change the value of the phenotype in the population
						phenotype.set(latentVariableIndex, scaledValue);
						// Update image
						ImageIcon img = getLevelImageIcon(2*picSize, phenotype); 
						imageLabel.setIcon(img);
						// Genotype references the phenotype, so it is changed by the modifications above
						resetButton(scores.get(populationIndex).individual, populationIndex);
						
						// If there is another level in the frame to compare against, then update KL Div calculations
						if(compare) {
							// Do both comparisons since KL Div is not symmetric
							globalKLDivLabel1.setText(klDivResults(selectedItems.get(selectedItems.size() - 1), selectedItems.get(selectedItems.size() - 2)));
							globalKLDivLabel2.setText(klDivResults(selectedItems.get(selectedItems.size() - 2), selectedItems.get(selectedItems.size() - 1)));
							globalKLDivSymLabel.setText(klDivSymmetricResults(selectedItems.get(selectedItems.size() - 2), selectedItems.get(selectedItems.size() - 1)));
						}
					}
				}
			});

			vectorSliders.add(vectorValue);
		}

		// Play the modified level
		JButton play = new JButton("Play");
		// Population index of last clicked level
		play.setName(""+populationIndex);
		play.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String name = ((JButton) e.getSource()).getName();
				int populationIndex = Integer.parseInt(name);
				ArrayList<Double> phenotype = scores.get(populationIndex).individual.getPhenotype();
				playLevel(phenotype);
			}
		});

		JPanel main = new JPanel();
		main.add(vectorSliders);
		main.add(imageLabel);
		main.add(play);
		main.add(bothKLDivStrings);
		explorer.getContentPane().add(main);
		
		explorer.pack();
		explorer.setVisible(true);
	}

	/**
	 * Return antisymmetric KL Div results in a String
	 * @param popIndex1 First index in population
	 * @param popIndex2 Second index in population
	 * @return String with results of KL div comparison
	 */
	public String klDivResults(int popIndex1, int popIndex2) {
		Genotype<ArrayList<Double>> genotype1 = scores.get(popIndex1).individual;
		Genotype<ArrayList<Double>> genotype2 = scores.get(popIndex2).individual;
		
		ArrayList<Double> phenotype1 = genotype1.getPhenotype();
		ArrayList<Double> phenotype2 = genotype2.getPhenotype();
		
		int[][] level1 = getArrayLevel(phenotype1);
		int[][] level2 = getArrayLevel(phenotype2);
		
		ConvNTuple c1 = KLDivTest.getConvNTuple(level1, Parameters.parameters.integerParameter("receptiveFieldWidth"), Parameters.parameters.integerParameter("receptiveFieldHeight"), Parameters.parameters.integerParameter("stride"));
		ConvNTuple c2 = KLDivTest.getConvNTuple(level2, Parameters.parameters.integerParameter("receptiveFieldWidth"), Parameters.parameters.integerParameter("receptiveFieldHeight"), Parameters.parameters.integerParameter("stride"));

		double klDiv = KLDiv.klDiv(c1.sampleDis, c2.sampleDis);
		String result = "KL Div: " + genotype1.getId() + " to " + genotype2.getId() + ": " + String.format("%10.6f", klDiv);
		return result;
	}

	/**
	 * Return symmetric KL Div results in a String
	 * @param popIndex1 First index in population
	 * @param popIndex2 Second index in population
	 * @return String with results of KL div comparison
	 */
	public String klDivSymmetricResults(int popIndex1, int popIndex2) {
		Genotype<ArrayList<Double>> genotype1 = scores.get(popIndex1).individual;
		Genotype<ArrayList<Double>> genotype2 = scores.get(popIndex2).individual;
		
		ArrayList<Double> phenotype1 = genotype1.getPhenotype();
		ArrayList<Double> phenotype2 = genotype2.getPhenotype();
		
		int[][] level1 = getArrayLevel(phenotype1);
		int[][] level2 = getArrayLevel(phenotype2);
		
		ConvNTuple c1 = KLDivTest.getConvNTuple(level1, Parameters.parameters.integerParameter("receptiveFieldWidth"), Parameters.parameters.integerParameter("receptiveFieldHeight"), Parameters.parameters.integerParameter("stride"));
		ConvNTuple c2 = KLDivTest.getConvNTuple(level2, Parameters.parameters.integerParameter("receptiveFieldWidth"), Parameters.parameters.integerParameter("receptiveFieldHeight"), Parameters.parameters.integerParameter("stride"));

		double klDiv = KLDiv.klDivSymmetric(c1.sampleDis, c2.sampleDis);
		String result = "Symmetric KL Div: " + genotype1.getId() + " to " + genotype2.getId() + ": " + String.format("%10.6f", klDiv);
		return result;
	}

	/**
	 * Return a representation of the level as a 2D array of ints where each int represents
	 * a different tile type.
	 * @param phenotype GAN latent vector
	 * @return 2D int tile representation
	 */
	public int[][] getArrayLevel(ArrayList<Double> phenotype) {
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

	/**
	 * Use GAN to take latent vector and create a 2D list of lists that represents
	 * the layout of the level. Importantly, the list representation uses unique
	 * integers for each tile type in a range of 0 to max
	 * @param latentVector
	 * @return
	 */
	public abstract List<List<Integer>> levelListRepresentation(double[] latentVector);

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
