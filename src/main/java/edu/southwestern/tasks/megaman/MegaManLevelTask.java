package edu.southwestern.tasks.megaman;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
import edu.southwestern.evolution.genotypes.CPPNOrDirectToGANGenotype;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.mapelites.Archive;
import edu.southwestern.evolution.mapelites.MAPElites;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.scores.Score;
import edu.southwestern.tasks.NoisyLonerTask;
import edu.southwestern.tasks.gvgai.zelda.dungeon.DungeonUtil;
import edu.southwestern.tasks.megaman.astar.MegaManState;
import edu.southwestern.tasks.megaman.astar.MegaManState.MegaManAction;
import edu.southwestern.tasks.zelda.ZeldaMAPElitesWallWaterRoomsBinLabels;
import edu.southwestern.util.MiscUtil;
import edu.southwestern.util.datastructures.ArrayUtil;
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.graphics.GraphicsUtil;

public abstract class MegaManLevelTask<T> extends NoisyLonerTask<T> {
	private static int numFitnessFunctions = 0; 
	private static final int numOtherScores = 10;

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
//			if(Parameters.parameters.booleanParameter("megaManAllowsNumDistinctSegments")){
//				MMNEAT.registerFitnessFunction("numDistinctScreens"); //distinct screens
//				numFitnessFunctions++;
//			}
			//registers the other things to be tracked that are not fitness functions, to be put in the otherScores array 
			MMNEAT.registerFitnessFunction("simpleAStarDistance",false);
			MMNEAT.registerFitnessFunction("percentConnected", false);
			MMNEAT.registerFitnessFunction("numEnemies",false);
			MMNEAT.registerFitnessFunction("numFlyingEnemies", false);
			MMNEAT.registerFitnessFunction("numGroundEnemies",false);
			MMNEAT.registerFitnessFunction("numWallEnemies", false);
			MMNEAT.registerFitnessFunction("numHorizontalSegments",false);
			MMNEAT.registerFitnessFunction("numUpSegments", false);
			MMNEAT.registerFitnessFunction("numDownSegments",false);
			MMNEAT.registerFitnessFunction("numCornerSegments", false);
			MMNEAT.registerFitnessFunction("numDistinctSegments", false);		}
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

	@SuppressWarnings("unchecked")
	private Pair<double[], double[]> evaluateOneLevel(List<List<Integer>> level, long genotypeId) {
		// TODO Auto-generated method stub
		ArrayList<Double> behaviorVector = null; // Filled in later
		ArrayList<Double> fitnesses = new ArrayList<>(numFitnessFunctions); //initializes the fitness function array 
		HashSet<MegaManState> mostRecentVisited = MegaManLevelAnalysisUtil.performAStarSearchAndCalculateAStarDistance(level).t1;
		ArrayList<MegaManAction> actionSequence = MegaManLevelAnalysisUtil.performAStarSearchAndCalculateAStarDistance(level).t2;
		MegaManState start = MegaManLevelAnalysisUtil.performAStarSearchAndCalculateAStarDistance(level).t3; //gets start state for search 
		double simpleAStarDistance = MegaManLevelAnalysisUtil.performAStarSearchAndCalculateAStarDistance(level).t4;
		//calculates the amount of the level that was covered in the search, connectivity.
		double precentConnected = MegaManLevelAnalysisUtil.caluclateConnectivity(mostRecentVisited)/MegaManLevelAnalysisUtil.findTotalPassableTiles(level);
		HashMap<String, Integer> k = MegaManLevelAnalysisUtil.findMiscEnemies(level);
		double numEnemies = k.get("numEnemies");
		double numWallEnemies = k.get("numWallEnemies");
		double numGroundEnemies = k.get("numGroundEnemies");
		double numFlyingEnemies = k.get("numFlyingEnemies");
		
		
		HashMap<String,Integer> l = findMiscSegments(level);
		double numHorizontalSegments = l.get("numHorizontal");
		double numUpSegments = l.get("numUp");
		double numDownSegments = l.get("numDown");
		double numCornerSegments = l.get("numCorner");
		double numDistinctSegments = l.get("numDistinctSegments");
//				l.get("numCorners");


		//adds the fitness functions being used to the fitness array list
		if(Parameters.parameters.booleanParameter("megaManAllowsSimpleAStarPath")) {
			fitnesses.add(simpleAStarDistance);
		}
		if(Parameters.parameters.booleanParameter("megaManAllowsConnectivity")) {
			fitnesses.add(precentConnected);
		}
		
		double[] otherScores = new double[] {simpleAStarDistance,precentConnected, numEnemies, numWallEnemies, numGroundEnemies, numFlyingEnemies, numHorizontalSegments, numUpSegments, numDownSegments, numCornerSegments, numDistinctSegments};
		
		
		
		if(CommonConstants.watch) {
			//prints values that are calculated above for debugging 
			System.out.println("Simple A* Distance Orb " + simpleAStarDistance);
			System.out.println("Percent of Positions Visited " + precentConnected);
			System.out.println("Number of Enemies " + numEnemies);
			System.out.println("Number of Wall Enemies " + numWallEnemies);
			System.out.println("Number of Ground Enemies " + numGroundEnemies);
			System.out.println("Number of Flying Enemies " + numFlyingEnemies);
			System.out.println("Number of Horizontal Segments " + numHorizontalSegments);
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
					screenx = 1800;
					screeny = 950*level.size()/level.get(0).size();
				}else {
					screeny = 950;
					screenx = 1800*level.get(0).size()/level.size();
				}
				JLabel label = new JLabel(new ImageIcon(visualPath.getScaledInstance(screenx,screeny, Image.SCALE_FAST)));
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
						File mmlvFilePath = new File("MegaManMakerLevelPath.txt"); //file containing the path
						
						Scanner scan;
						//When the button is pushed, ask for the name input
						try {
							scan = new Scanner(mmlvFilePath);
							//scan.next();
							String mmlvPath = scan.nextLine();
							System.out.println(mmlvPath);
							String mmlvFileName = JOptionPane.showInputDialog(null, "What do you want to name your level?");
							//System.out.println("pane showed up");
							//File mmlvFileFromEvolution = new File(mmlvPath+mmlvFileName+".mmlv"); //creates file inside user's MegaManLevelPath
							System.out.println(mmlvPath+mmlvFileName+".mmlv");
							@SuppressWarnings("unused")
							File mmlvFile; //creates file inside MMNEAT
							scan.close();
							//ArrayList<Double> phenotype = scores.get(selectedItems.get(selectedItems.size() - 1)).individual.getPhenotype();
							//double[] doubleArray = ArrayUtil.doubleArrayFromList(phenotype);
							//List<List<Integer>> level = levelListRepresentation(doubleArray);
							//int levelNumber = 2020;
							mmlvFile = MegaManVGLCUtil.convertMegaManLevelToMMLV(level, mmlvFileName, mmlvPath);
							//Files.copy(mmlvFile, mmlvFileFromEvolution); //copies over
							//System.out.println("File vopied");
							//mmlvFile.delete(); //deletes MMNEAT file
							JFrame frame = new JFrame("");
							frame.setLocationRelativeTo(null);
							frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
							frame.setVisible(true);
							JOptionPane.showMessageDialog(frame, "Level saved to: "+mmlvPath);
							
							
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							//e.printStackTrace();
							JFrame frame = new JFrame("");
							frame.setLocationRelativeTo(null);
							frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
							frame.setVisible(true);
							String errorMessage = "You need to create a local text file in the MMNEAT directory called \n MegaManMakerLevelPath.txt which contains the path to where MegaManMaker stores levels on your device. \n It will likely look like this: C:\\Users\\[Insert User Name]\\AppData\\Local\\MegaMaker\\Levels\\";
							JOptionPane.showMessageDialog(frame, errorMessage);
						}
//						MegaManGANLevelBreederTask.saveLevel();
						File mmlvFilePath1 = new File("MegaManMakerPath.txt"); //file containing the path
//						if(selectedItems.size() != 1) {
//							JOptionPane.showMessageDialog(null, "Save exactly one level to play.");
//							return; // Nothing to explore
//						}
						
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
						
						MiscUtil.waitForReadStringAndEnterKeyPress();

						
					}
				});
				System.out.println("Press enter");
				MiscUtil.waitForReadStringAndEnterKeyPress();
			}
			if(MMNEAT.ea instanceof MAPElites) {
				final int BINS_PER_DIMENSION = Parameters.parameters.integerParameter("megaManGANLevelChunks");
				double binScore = simpleAStarDistance;
				int binIndex = 0;
				if(((MAPElites<T>) MMNEAT.ea).getBinLabelsClass() instanceof MegaManMAPElitesDistinctVerticalAndConnectivityBinLabels) {
					int maxNumSegments = BINS_PER_DIMENSION * BINS_PER_DIMENSION;
					
					
					int indexConnected = (int) precentConnected*MegaManMAPElitesDistinctVerticalAndConnectivityBinLabels.TILE_GROUPS;
					int numVertical = (int) (numUpSegments+numDownSegments);
//					int numDistinctSegments;
					double[] archiveArray = new double[(maxNumSegments+1)*(maxNumSegments+1)*(MegaManMAPElitesDistinctVerticalAndConnectivityBinLabels.TILE_GROUPS)];
					Arrays.fill(archiveArray, Double.NEGATIVE_INFINITY); // Worst score in all dimensions
//					binIndex = (dim1*BINS_PER_DIMENSION + dim2)*BINS_PER_DIMENSION + dim3;
					binIndex =(numVertical*BINS_PER_DIMENSION+ (int) numDistinctSegments)*BINS_PER_DIMENSION+indexConnected*MegaManMAPElitesDistinctVerticalAndConnectivityBinLabels.TILE_GROUPS;
					archiveArray[binIndex] = binScore; // Percent rooms traversed

					System.out.println("["+numDistinctSegments+"]["+numVertical+"]["+indexConnected+"] = "+binScore);

					behaviorVector = ArrayUtil.doubleVectorFromArray(archiveArray);
				}
				
				if(CommonConstants.netio) {
					System.out.println("Save archive images");
					Archive<T> archive = ((MAPElites<T>) MMNEAT.ea).getArchive();
					List<String> binLabels = archive.getBinMapping().binLabels();

					// Index in flattened bin array
					Score<T> elite = archive.getElite(binIndex);
					// If the bin is empty, or the candidate is better than the elite for that bin's score
					if(elite == null || binScore > elite.behaviorVector.get(binIndex)) {
						// CHANGE!
//						BufferedImage imagePath = DungeonUtil.imageOfDungeon(dungeon, mostRecentVisited, solutionPath);
//						BufferedImage imagePlain = DungeonUtil.imageOfDungeon(dungeon, null, null);
						BufferedImage levelImage = null;
						try {
							BufferedImage levelSolution = MegaManState.vizualizePath(level,mostRecentVisited,actionSequence,start);
							BufferedImage[] images = MegaManRenderUtil.loadImagesForASTAR(MegaManRenderUtil.MEGA_MAN_TILE_PATH);
							levelImage = MegaManRenderUtil.getBufferedImageWithRelativeRendering(level, images);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						//sets the fileName, binPath, and fullName
						String fileName = String.format("%7.5f", binScore) +"-"+ genotypeId + ".png";
//						if(individual instanceof CPPNOrDirectToGANGenotype) {
//							CPPNOrDirectToGANGenotype temp = (CPPNOrDirectToGANGenotype) individual;
//							if(temp.getFirstForm()) fileName = "CPPN-" + fileName;
//							else fileName = "Direct-" + fileName;
//						}
						String binPath = archive.getArchiveDirectory() + File.separator + binLabels.get(binIndex);
						String fullName = binPath + "-" + fileName;
						System.out.println(fullName);
						GraphicsUtil.saveImage(levelImage, fullName);	
						fileName = String.format("%7.5f", binScore) +"-"+ genotypeId + "-solution.png";
						fullName = binPath + "-" + fileName;
						System.out.println(fullName);
						GraphicsUtil.saveImage(levelImage, fullName);	
					}
				}
			}
		}
		return new Pair<double[],double[]>(ArrayUtil.doubleArrayFromList(fitnesses), otherScores);
	}
	/**
	 * Extract real-valued latent vector from genotype and then send to GAN to get a MegaMan level
	 */
	public abstract List<List<Integer>> getMegaManLevelListRepresentationFromGenotype(Genotype<T> individual);
	public abstract HashMap<String, Integer> findMiscSegments(List<List<Integer>> level);


}
