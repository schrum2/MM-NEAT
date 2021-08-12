package edu.southwestern.tasks.megaman;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
//import edu.southwestern.evolution.GenerationalEA;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.mapelites.Archive;
import edu.southwestern.evolution.mapelites.generalmappings.*;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.scores.Score;
import edu.southwestern.tasks.NoisyLonerTask;
import edu.southwestern.tasks.export.JsonLevelGenerationTask;
import edu.southwestern.tasks.megaman.astar.MegaManState;
import edu.southwestern.tasks.megaman.astar.MegaManState.MegaManAction;
import edu.southwestern.util.MiscUtil;
import edu.southwestern.util.datastructures.ArrayUtil;
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.datastructures.Quad;
import edu.southwestern.util.file.FileUtilities;
import edu.southwestern.util.graphics.GraphicsUtil;

/**
 * This class is responsible for being the backbone behind Direct2GAN and CPPN2GAN
 * Registers fitness functions and determines the score of each level generated
 * 
 * @author Benjamin Capps	
 *
 *
 */
public abstract class MegaManLevelTask<T> extends NoisyLonerTask<T> implements JsonLevelGenerationTask<T>{
	private int numFitnessFunctions = 0; 
	private static final int NUM_OTHER_SCORES = 12;

	private double fitnessSaveThreshold = Parameters.parameters.doubleParameter("fitnessSaveThreshold");
	
	public MegaManLevelTask(){
		this(true);
	}
	
	protected MegaManLevelTask(boolean register) {
		LevelNovelty.setGame("mega_man"); // If Novelty-based MAP Elites binning is used
		if(register) {
			numFitnessFunctions = 0;
			if(Parameters.parameters.booleanParameter("megaManAllowsSimpleAStarPath")) {
				MMNEAT.registerFitnessFunction("simpleAStarDistance");
				numFitnessFunctions++;
			}
			if(Parameters.parameters.booleanParameter("megaManAllowsConnectivity")) {
				MMNEAT.registerFitnessFunction("numOfPositionsVisited"); //connectivity
				numFitnessFunctions++;
			}
			if(Parameters.parameters.booleanParameter("megaManMaximizeEnemies")) {
				MMNEAT.registerFitnessFunction("numEnemies"); //connectivity
				numFitnessFunctions++;
			}
			//megaManMaximizeEnemies
			//registers the other things to be tracked that are not fitness functions, to be put in the otherScores array 
			MMNEAT.registerFitnessFunction("simpleAStarDistance",false);
			MMNEAT.registerFitnessFunction("percentConnected", false);
			MMNEAT.registerFitnessFunction("numEnemies",false);
			MMNEAT.registerFitnessFunction("numFlyingEnemies", false);
			MMNEAT.registerFitnessFunction("numGroundEnemies",false);
			MMNEAT.registerFitnessFunction("numWallEnemies", false);
			MMNEAT.registerFitnessFunction("numRightSegments",false);
			MMNEAT.registerFitnessFunction("numLeftSegments",false);
			MMNEAT.registerFitnessFunction("numUpSegments", false);
			MMNEAT.registerFitnessFunction("numDownSegments",false);
			MMNEAT.registerFitnessFunction("numCornerSegments", false);
			MMNEAT.registerFitnessFunction("numDistinctSegments", false);		}
	}
	@Override
	/**
	 * gets the number of objective fitness functions
	 */
	public int numObjectives() {
		return numFitnessFunctions;
	}
	@Override
	/**
	 * gets the number of other scores (scores that do not affect the levels' fitness)
	 */
	public int numOtherScores() {
		return NUM_OTHER_SCORES;
	}
	@Override
	public double getTimeStamp() {
		return 0; //not used
	}

	@Override
	public Pair<double[], double[]> oneEval(Genotype<T> individual, int num, HashMap<String,Object> behaviorCharacteristics) {
		MegaManTrackSegmentType segmentCount = new MegaManTrackSegmentType();
		// Passing this parameter inside the hash map instead of as a normal parameter is confusing, 
		// but allows this class to conform to the JsonLevelGenerationTask easily.
		behaviorCharacteristics.put("segmentTracker",segmentCount); 
		List<List<Integer>> level = getMegaManLevelListRepresentationFromGenotype(individual, segmentCount); //gets a level 
		// 0 represents the ignored pseudo-random seed
		return evaluateOneLevel(level, 0, individual, behaviorCharacteristics);
	}

	/**
	 * Evaluate the given List of Lists of Integers representation of the level
	 * @param level List of lists of integers (each represents a tile)
	 * @param psuedoRandomSeed Ignored
	 * @param individual Genotype, but mainly used for the ID
	 * @param behaviorMap Map that will store behavior information, but also MUST contain a MegaManTrackSegmentType
	 *                    as a sneakily passed parameter with the key "segmentTracker"
	 * @return Pair of fitness and other scores
	 */
	public Pair<double[], double[]> evaluateOneLevel(List<List<Integer>> level, double psuedoRandomSeed, Genotype<T> individual, HashMap<String,Object> behaviorCharacteristics) {
		if(!behaviorCharacteristics.containsKey("segmentTracker")) behaviorCharacteristics.put("segmentTracker", new MegaManTrackSegmentType());
		MegaManTrackSegmentType segmentCount = (MegaManTrackSegmentType) behaviorCharacteristics.get("segmentTracker");
		long genotypeId = individual.getId();
		ArrayList<Double> fitnesses = new ArrayList<>(numFitnessFunctions); //initializes the fitness function array 
		Quad<HashSet<MegaManState>, ArrayList<MegaManAction>, MegaManState, Double> aStarResults = MegaManLevelAnalysisUtil.performAStarSearchAndCalculateAStarDistance(level);
		HashSet<MegaManState> mostRecentVisited = aStarResults.t1;
		ArrayList<MegaManAction> actionSequence = aStarResults.t2;
		MegaManState start = aStarResults.t3; //gets start state for search 
		double simpleAStarDistance = aStarResults.t4;
		//calculates the amount of the level that was covered in the search, connectivity.
		double precentConnected = MegaManLevelAnalysisUtil.caluclateConnectivity(mostRecentVisited)/MegaManLevelAnalysisUtil.findTotalPassableTiles(level);
		
		HashMap<String, Integer> miscEnemyInfo = MegaManLevelAnalysisUtil.findMiscEnemies(level);
		double numEnemies = miscEnemyInfo.get("numEnemies");
		double numWallEnemies = miscEnemyInfo.get("numWallEnemies");
		double numGroundEnemies = miscEnemyInfo.get("numGroundEnemies");
		double numFlyingEnemies = miscEnemyInfo.get("numFlyingEnemies");
		HashMap<String,Integer> miscChunkInfo = segmentCount.findMiscSegments();
		double numRightSegments = miscChunkInfo.get("numRight");
		double numLeftSegments = miscChunkInfo.get("numLeft");
		double numUpSegments = miscChunkInfo.get("numUp");
		double numDownSegments = miscChunkInfo.get("numDown");
		double numCornerSegments = miscChunkInfo.get("numCorner");
		double numDistinctSegments = miscChunkInfo.get("numDistinctSegments");


		//adds the fitness functions being used to the fitness array list
		if(Parameters.parameters.booleanParameter("megaManAllowsSimpleAStarPath")) {
			fitnesses.add(simpleAStarDistance);
		}
		if(Parameters.parameters.booleanParameter("megaManAllowsConnectivity")) {
			fitnesses.add(precentConnected);
		}
		if(Parameters.parameters.booleanParameter("megaManMaximizeEnemies")) {
			fitnesses.add(numEnemies);
		}
		
		double[] otherScores = new double[] {simpleAStarDistance,precentConnected, numEnemies, numWallEnemies, numGroundEnemies, numFlyingEnemies, numRightSegments, numLeftSegments, numUpSegments, numDownSegments, numCornerSegments, numDistinctSegments};
		
		if(CommonConstants.watch) {
			//prints values that are calculated above for debugging 
			System.out.println("Simple A* Distance Orb " + simpleAStarDistance);
			System.out.println("Percent of Positions Visited " + precentConnected);
			System.out.println("Number of Enemies " + numEnemies);
			System.out.println("Number of Wall Enemies " + numWallEnemies);
			System.out.println("Number of Ground Enemies " + numGroundEnemies);
			System.out.println("Number of Flying Enemies " + numFlyingEnemies);
			System.out.println("Number of Right Segments " + numRightSegments);
			System.out.println("Number of Left Segments " + numLeftSegments);
			System.out.println("Number of Up Segments " + numUpSegments);
			System.out.println("Number of Down Segments " + numDownSegments);
			System.out.println("Number of Corner Segments " + numCornerSegments);
			System.out.println("Number of Distinct Segments " + numDistinctSegments);	
			try {
				//displays the rendered solution path in a window 
				BufferedImage visualPath = MegaManState.vizualizePath(level,mostRecentVisited,actionSequence,start);
				JFrame frame = new JFrame();
				JPanel panel = new JPanel();
				int screenx;
				int screeny;
				if(level.get(0).size()>level.size()) {
					screenx = MegaManRenderUtil.MEGA_MAN_RENDER_X; 
					screeny = MegaManRenderUtil.MEGA_MAN_RENDER_Y*level.size()/level.get(0).size();
				}else {
					screeny = MegaManRenderUtil.MEGA_MAN_RENDER_Y;
					screenx = MegaManRenderUtil.MEGA_MAN_RENDER_X*level.get(0).size()/level.size();
				}
				JLabel label = new JLabel(new ImageIcon(visualPath.getScaledInstance(screenx,screeny, Image.SCALE_FAST)));
				panel.add(label);
				frame.add(panel);
				frame.pack();
				frame.setVisible(true);
				String saveDir = FileUtilities.getSaveDirectory(); //save directory
				//int currentGen = MMNEAT.ea instanceof GenerationalEA ? ((GenerationalEA) MMNEAT.ea).currentGeneration() : -1;
				//saves image
				File tempFile = new File(saveDir+".png");
				boolean exists = tempFile.exists();
				File textFile = new File(saveDir+".txt");
				boolean exists1 = textFile.exists();
				if(Parameters.parameters.booleanParameter("io")&&!exists) GraphicsUtil.saveImage(visualPath, saveDir + ".png");
				if(Parameters.parameters.booleanParameter("io")&&!exists1) {
					FileWriter writer = new FileWriter(saveDir+".txt"); //text file containing the List<List<Integer>> level
					for(int i = 0 ; i < level.size();i++) {
						for(int j = 0;j<level.get(0).size(); j++) {
							writer.write(level.get(i).get(j).toString());
							writer.write(" ");
						}
						writer.write("\n");
					}
					writer.close();
					
				}
				
			} catch (IOException e) {
				System.out.println("Could not display image");
			}
			
//			BufferedImage levelImage = null;
//			try {
//				BufferedImage[] images = MegaManRenderUtil.loadImagesForASTAR(MegaManRenderUtil.MEGA_MAN_TILE_PATH);
//				levelImage = MegaManRenderUtil.createBufferedImage(level, MegaManRenderUtil.renderedImageWidth(level.get(0).size()), MegaManRenderUtil.renderedImageHeight(level.size()), images);
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
			
			//Gives you the option to play the level by pressing p, or skipping by pressing enter, after the visualization is displayed 
			System.out.println("Enter 'P' to play, or just press Enter to continue");
			String input = MiscUtil.waitForReadStringAndEnterKeyPress();
			System.out.println("Entered \""+input+"\"");
			//if the user entered P or p, then run
			if(input.toLowerCase().equals("p")) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						File mmlvFilePath = new File("MegaManMakerLevelPath.txt"); //file containing the path
						
						Scanner scan;
						//When the button is pushed, ask for the name input
						try {
							scan = new Scanner(mmlvFilePath);
							//scan.next();
							String mmlvPath = scan.nextLine();
							System.out.println(mmlvPath);
							String mmlvFileName = JOptionPane.showInputDialog(null, "What do you want to name your level?");
							System.out.println(mmlvPath+mmlvFileName+".mmlv");
							@SuppressWarnings("unused")
							File mmlvFile; //creates file inside MMNEAT
							scan.close();
							mmlvFile = MegaManVGLCUtil.convertMegaManLevelToMMLV(level, mmlvFileName, mmlvPath);
							JFrame frame = new JFrame("");
							frame.setLocationRelativeTo(null);
							frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
							frame.setVisible(true);
							JOptionPane.showMessageDialog(frame, "Level saved to: "+mmlvPath);
							
							
						} catch (FileNotFoundException e) {
							JFrame frame = new JFrame("");
							frame.setLocationRelativeTo(null);
							frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
							frame.setVisible(true);
							String errorMessage = "You need to create a local text file in the MMNEAT directory called \n MegaManMakerLevelPath.txt which contains the path to where MegaManMaker stores levels on your device. \n It will likely look like this: C:\\Users\\[Insert User Name]\\AppData\\Local\\MegaMaker\\Levels\\";
							JOptionPane.showMessageDialog(frame, errorMessage);
						}
						File mmlvFilePath1 = new File("MegaManMakerPath.txt"); //file containing the path
						Scanner scan1;
						//When the button is pushed, ask for the name input
						try {
							scan1 = new Scanner(mmlvFilePath1);
							
							
							String mmlvPath = scan1.nextLine();
							System.out.println(mmlvPath);
							scan1.close();
							
							Runtime runTime = Runtime.getRuntime();
							@SuppressWarnings("unused")
							Process process = runTime.exec(mmlvPath);
							
							
						} catch (FileNotFoundException e) {
							JFrame frame = new JFrame("");
							frame.setLocationRelativeTo(null);
							frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
							frame.setVisible(true);
							String errorMessage = "You need to create a local text file in the MMNEAT directory called \n MegaManMakePath.txt which contains the path to where MegaManMaker.exe is stored on your device";
							JOptionPane.showMessageDialog(frame, errorMessage);
						} catch (IOException e) {
							e.printStackTrace();
						}
						
						MiscUtil.waitForReadStringAndEnterKeyPress();

						
					}
				});
				System.out.println("Press enter");
				MiscUtil.waitForReadStringAndEnterKeyPress();
			}
			
		}
		if(MMNEAT.usingDiversityBinningScheme) {
			assert precentConnected <= 1;
			
			behaviorCharacteristics.put("Connectivity", precentConnected);
			behaviorCharacteristics.put("Vertical Segments", numUpSegments+numDownSegments);
			behaviorCharacteristics.put("Distinct Segments", numDistinctSegments);
			behaviorCharacteristics.put("Level", level); // Used to calculate Level Novelty
			// Takes some effort to compute, so only compute if needed.
			if (MMNEAT.getArchiveBinLabelsClass() instanceof LatentVariablePartitionSumBinLabels) {
				@SuppressWarnings("unchecked")
				ArrayList<Double> rawVector = (ArrayList<Double>) individual.getPhenotype();
				double[] latentVector = ArrayUtil.doubleArrayFromList(rawVector);
				behaviorCharacteristics.put("Solution Vector", latentVector);
			}
			
			double binScore = simpleAStarDistance;
			if(Parameters.parameters.booleanParameter("megaManAllowsAStarConnectivityCombo")) {
				// Distance is 0 if unsolvable, but the percent connected will be positive (between 0 and 1) giving some
				// indication of how easy it is to navigate the level.
				binScore = Math.max(precentConnected, simpleAStarDistance); 
			}
			behaviorCharacteristics.put("binScore", binScore); // Quality measure!
			
			int dim1D = MMNEAT.getArchiveBinLabelsClass().oneDimensionalIndex(behaviorCharacteristics);
			behaviorCharacteristics.put("dim1D", dim1D); // Save so it does not need to be computed again
			
			if(CommonConstants.netio) {
				System.out.println("Save archive images");
				@SuppressWarnings("unchecked")
				Archive<T> archive = MMNEAT.getArchive();
				List<String> binLabels = archive.getBinMapping().binLabels();

				// Index in flattened bin array
				Score<T> elite = archive.getElite(dim1D);
				// If the bin is empty, or the candidate is better than the elite for that bin's score
				if(elite == null || binScore > elite.behaviorIndexScore()) {
					if(binScore > fitnessSaveThreshold) {
						BufferedImage levelImage = null;
						BufferedImage levelSolution = null;
						try {
							levelSolution = MegaManState.vizualizePath(level,mostRecentVisited,actionSequence,start);
							BufferedImage[] images = MegaManRenderUtil.loadImagesForASTAR(MegaManRenderUtil.MEGA_MAN_TILE_PATH);
							levelImage = MegaManRenderUtil.createBufferedImage(level, MegaManRenderUtil.renderedImageWidth(level.get(0).size()), MegaManRenderUtil.renderedImageHeight(level.size()), images);
						} catch (IOException e) {
							e.printStackTrace();
						}
						
						//sets the fileName, binPath, and fullName
						String fileName = String.format("%7.5f", binScore) +"-"+ genotypeId + ".png";
						//FOR CPPNThenDirect2GAN
	//					if(individual instanceof CPPNOrDirectToGANGenotype) {
	//						CPPNOrDirectToGANGenotype temp = (CPPNOrDirectToGANGenotype) individual;
	//						if(temp.getFirstForm()) fileName = "CPPN-" + fileName;
	//						else fileName = "Direct-" + fileName;
	//					}
						String binPath = archive.getArchiveDirectory() + File.separator + binLabels.get(dim1D);
						String fullName = binPath + "-" + fileName;
						System.out.println(fullName);
						GraphicsUtil.saveImage(levelImage, fullName);	
						fileName = String.format("%7.5f", binScore) +"-"+ genotypeId + "-solution.png";
						fullName = binPath + "-" + fileName;
						System.out.println(fullName);
						GraphicsUtil.saveImage(levelSolution, fullName);	
					}
				}
			}
		}
		return new Pair<double[],double[]>(ArrayUtil.doubleArrayFromList(fitnesses), otherScores);
	}
	/**
	 * Extract real-valued latent vector from genotype and then send to GAN to get a MegaMan level
	 */
	public abstract List<List<Integer>> getMegaManLevelListRepresentationFromGenotype(Genotype<T> individual, MegaManTrackSegmentType segmentCount);
	
	
	public static void main(String[] args) throws FileNotFoundException, NoSuchMethodException {
		// Test comparison that isn't working
		MMNEAT.main("runNumber:0 parallelEvaluations:true threads:10 base:megamanmultigancomparebins log:MegaManMultiGANCompareBins-LineNoveltyVerticalAndConnectivity saveTo:LineNoveltyVerticalAndConnectivity trials:1 experiment:edu.southwestern.experiment.post.CompareMAPElitesBinningSchemeExperiment mapElitesBinLabels:edu.southwestern.tasks.megaman.MegaManMAPElitesDistinctVerticalAndConnectivityBinLabels logLock:true io:false".split(" "));
	}
	

}
