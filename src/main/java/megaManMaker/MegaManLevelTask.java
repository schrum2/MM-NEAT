package megaManMaker;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.NoisyLonerTask;
import edu.southwestern.util.MiscUtil;
import edu.southwestern.util.datastructures.ArrayUtil;
import edu.southwestern.util.datastructures.Pair;
import megaManMaker.MegaManState.MegaManAction;

public abstract class MegaManLevelTask<T> extends NoisyLonerTask<T> {
	private static int numFitnessFunctions = 0; 
	private static final int numOtherScores = 2;

	MegaManLevelTask(){
		this(true);
	}
	protected MegaManLevelTask(boolean register) {
		if(register) {
			if(Parameters.parameters.booleanParameter("megaManAllowsSimpleAStarPath")) {
				MMNEAT.registerFitnessFunction("simpleAStarDistance");
				numFitnessFunctions++;
			}
			if(Parameters.parameters.booleanParameter("megaManAllowsConnectivity")) {
				MMNEAT.registerFitnessFunction("numOfPositionsVisited"); //connectivity
				numFitnessFunctions++;
			}
//			if(Parameters.parameters.booleanParameter("megaManDistinctScreenFitness")){
//				MMNEAT.registerFitnessFunction("numDistinctScreens"); //distinct screens
//				numFitnessFunctions++;
//			}
			//registers the other things to be tracked that are not fitness functions, to be put in the otherScores array 
			MMNEAT.registerFitnessFunction("simpleAStarDistance",false);
			MMNEAT.registerFitnessFunction("percentConnected", false);
		}
	}
	@Override
	public int numObjectives() {
		// TODO Auto-generated method stub
		return numFitnessFunctions;
	}
	@Override
	public int numOtherScores() {
		// TODO Auto-generated method stub
		return numOtherScores;
	}
	@Override
	public double getTimeStamp() {
		return 0; //not used
	}

	@Override
	public Pair<double[], double[]> oneEval(Genotype<T> individual, int num) {
		
		List<List<Integer>> level = getMegaManLevelListRepresentationFromGenotype(individual); //gets a level 
		//double psuedoRandomSeed = getRandomSeedForSpawnPoint(individual); //creates the seed to be passed into the Random instance 
		long genotypeId = individual.getId();
		
		return evaluateOneLevel(level, genotypeId);
	}

	private Pair<double[], double[]> evaluateOneLevel(List<List<Integer>> level, long genotypeId) {
		// TODO Auto-generated method stub
		
		ArrayList<Double> fitnesses = new ArrayList<>(numFitnessFunctions); //initializes the fitness function array 
		HashSet<MegaManState> mostRecentVisited = MegaManLevelAnalysisUtil.performAStarSearchAndCalculateAStarDistance(level).t1;
		ArrayList<MegaManAction> actionSequence = MegaManLevelAnalysisUtil.performAStarSearchAndCalculateAStarDistance(level).t2;
		MegaManState start = MegaManLevelAnalysisUtil.performAStarSearchAndCalculateAStarDistance(level).t3; //gets start state for search 
		double simpleAStarDistance = MegaManLevelAnalysisUtil.performAStarSearchAndCalculateAStarDistance(level).t4;
		//calculates the amount of the level that was covered in the search, connectivity.
		double precentConnected = MegaManLevelAnalysisUtil.caluclateConnectivity(mostRecentVisited)/MegaManLevelAnalysisUtil.findTotalTiles(level);
		//adds the fitness functions being used to the fitness array list
		if(Parameters.parameters.booleanParameter("MegaManAllowsSimpleAStarPath")) {
			fitnesses.add(simpleAStarDistance);
		}
		if(Parameters.parameters.booleanParameter("MegaManAllowsConnectivity")) {
			fitnesses.add(precentConnected);
		}
		
		double[] otherScores = new double[] {simpleAStarDistance,precentConnected};
		
		
		
		if(CommonConstants.watch) {
			//prints values that are calculated above for debugging 
			System.out.println("Simple A* Distance to Farthest Gold " + simpleAStarDistance);
			System.out.println("Number of Positions Visited " + precentConnected);
			try {
				//displays the rendered solution path in a window 
				BufferedImage visualPath = MegaManState.vizualizePath(level,mostRecentVisited,actionSequence,start);
				JFrame frame = new JFrame();
				JPanel panel = new JPanel();
				JLabel label = new JLabel(new ImageIcon(visualPath.getScaledInstance(1600,900, Image.SCALE_FAST)));
				panel.add(label);
				frame.add(panel);
				frame.pack();
				frame.setVisible(true);
			} catch (IOException e) {
				System.out.println("Could not display image");
				//e.printStackTrace();
			}
			//Gives you the option to play the level by pressing p, or skipping by pressing enter, after the visualization is displayed 
			System.out.println("Enter 'P' to play, or just press Enter to continue");
			String input = MiscUtil.waitForReadStringAndEnterKeyPress();
			System.out.println("Entered \""+input+"\"");
			//if the user entered P or p, then run
			if(input.toLowerCase().equals("p")) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						File mmlvFilePath = new File("MegaManMakerPath.txt"); //file containing the path
//						if(selectedItems.size() != 1) {
//							JOptionPane.showMessageDialog(null, "Save exactly one level to play.");
//							return; // Nothing to explore
//						}
						
						Scanner scan;
						//When the button is pushed, ask for the name input
						try {
							scan = new Scanner(mmlvFilePath);
							
							
							String mmlvPath = scan.nextLine();
							scan.close();
							
							Runtime runTime = Runtime.getRuntime();
							@SuppressWarnings("unused")
							Process process = runTime.exec(mmlvPath);
							
							
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							//e.printStackTrace();
							JFrame frame = new JFrame("");
							frame.setLocationRelativeTo(null);
							frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
							frame.setVisible(true);
							String errorMessage = "You need to create a local text file in the MMNEAT directory called \n MegaManMakePath.txt which contains the path to where MegaManMaker.exe is stored on your device";
							JOptionPane.showMessageDialog(frame, errorMessage);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
				System.out.println("Press enter");
				MiscUtil.waitForReadStringAndEnterKeyPress();
			}
		}
		return new Pair<double[],double[]>(ArrayUtil.doubleArrayFromList(fitnesses), otherScores);
	}
	/**
	 * Extract real-valued latent vector from genotype and then send to GAN to get a MegaMan level
	 */
	public abstract List<List<Integer>> getMegaManLevelListRepresentationFromGenotype(Genotype<T> individual);


}
