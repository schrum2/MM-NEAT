package edu.southwestern.tasks.loderunner;

import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.NoisyLonerTask;
import edu.southwestern.tasks.gvgai.zelda.dungeon.Dungeon;
import edu.southwestern.tasks.loderunner.astar.LodeRunnerState;
import edu.southwestern.tasks.loderunner.astar.LodeRunnerState.LodeRunnerAction;
import edu.southwestern.util.MiscUtil;
import edu.southwestern.util.datastructures.ArrayUtil;
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.search.AStarSearch;
import edu.southwestern.util.search.Search;
import icecreamyou.LodeRunner.LodeRunner;
import me.jakerg.rougelike.RougelikeApp;

/**
 * 
 * @author kdste
 *
 * @param <T>
 */
public abstract class LodeRunnerLevelTask<T> extends NoisyLonerTask<T> {
	
	private static final int numFitnessFunctions = 2; 

	/**
	 * Registers all fitness functions that are used, rn only one is used for lode runner 
	 */
	public LodeRunnerLevelTask() {
		MMNEAT.registerFitnessFunction("DistanceToFarthestGold"); 
		MMNEAT.registerFitnessFunction("amountOfLevelCovered"); //connectivity fitness function 
	}

	/**
	 * @return The number of fitness functions 
	 */
	@Override
	public int numObjectives() {
		return numFitnessFunctions; //only one fitness function right now 
	}
	
	/**
	 * Different level generators use the genotype to generate a level in different ways
	 * @param individual Genotype 
	 * @return List of lists of integers corresponding to tile types
	 */
	public abstract List<List<Integer>> getLodeRunnerLevelListRepresentationFromGenotype(Genotype<T> individual);

	@Override
	public double getTimeStamp() {
		return 0; //not used 
	}

	/**
	 * Does one evaluation with the A* algorithm to see if the level is beatable 
	 * @param Genotype<T> 
	 * @param Integer 
	 * @return
	 */
	@SuppressWarnings({ "unused", "unchecked" })
	@Override
	public Pair<double[], double[]> oneEval(Genotype<T> individual, int num) {
		List<Double> latentVector = (List<Double>) individual.getPhenotype(); //creates a double array for the spawn to be placed in GAN levels 
		double[] doubleArray = ArrayUtil.doubleArrayFromList(latentVector);
		ArrayList<Double> fitnesses = new ArrayList<>(numFitnessFunctions);
		List<List<Integer>> level = getLodeRunnerLevelListRepresentationFromGenotype(individual);
		List<Point> emptySpaces = LodeRunnerGANUtil.fillEmptyList(level);
		Random rand = new Random(Double.doubleToLongBits(doubleArray[0]));
		LodeRunnerGANUtil.setSpawn(level, emptySpaces, rand);
		LodeRunnerState start = new LodeRunnerState(level);
		Search<LodeRunnerAction,LodeRunnerState> search = new AStarSearch<>(LodeRunnerState.manhattanToFarthestGold);
		HashSet<LodeRunnerState> mostRecentVisited = null;
		ArrayList<LodeRunnerAction> actionSequence = null;
		double simpleAStarDistance = -1;
		//calculates the Distance to the farthest gold as a fitness fucntion 
		try { 
			actionSequence = ((AStarSearch<LodeRunnerAction, LodeRunnerState>) search).search(start, true, Parameters.parameters.integerParameter( "aStarSearchBudget"));
			if(actionSequence == null) {
				fitnesses.add(-1.0);
			} else {
				simpleAStarDistance = 1.0*actionSequence.size();
				fitnesses.add(simpleAStarDistance);
			}
		} catch(IllegalStateException e) {
			fitnesses.add(-1.0);
			System.out.println("failed search");
			e.printStackTrace();
		}
		mostRecentVisited = ((AStarSearch<LodeRunnerAction, LodeRunnerState>) search).getVisited();
		
		//calculates the amount of the level that was covered in the search, connectivity.
		HashSet<Point> visitedPoints = new HashSet<>();
		double amountOfLevelCovered = -1;
		for(LodeRunnerState s : mostRecentVisited) {
			visitedPoints.add(new Point(s.currentX,s.currentY));
		}
		amountOfLevelCovered = 1.0*visitedPoints.size();
		fitnesses.add(amountOfLevelCovered);
		
		
//		if(CommonConstants.watch) {
//			System.out.println("Distance to Farthest Gold" + simpleAStarDistance);
//			//System.out.println("Amount of Level Covered" + );
//			
//			try {
//				BufferedImage visualPath = LodeRunnerState.vizualizePath(level,mostRecentVisited,actionSequence,start);
//				JFrame frame = new JFrame();
//				JPanel panel = new JPanel();
//				JLabel label = new JLabel(new ImageIcon(visualPath.getScaledInstance(LodeRunnerRenderUtil.LODE_RUNNER_COLUMNS*LodeRunnerRenderUtil.LODE_RUNNER_TILE_X, 
//						LodeRunnerRenderUtil.LODE_RUNNER_ROWS*LodeRunnerRenderUtil.LODE_RUNNER_TILE_Y, Image.SCALE_FAST)));
//				panel.add(label);
//				frame.add(panel);
//				frame.pack();
//				frame.setVisible(true);
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			System.out.println("Enter 'P' to play, or just press Enter to continue");
//			String input = MiscUtil.waitForReadStringAndEnterKeyPress();
//			System.out.println("Entered \""+input+"\"");
//			//if the user entered P or p, then run
//			if(input.toLowerCase().equals("p")) {
//				SwingUtilities.invokeLater(new Runnable() {
//					public void run() {
//						new LodeRunner(level);
//					}
//				});
//				System.out.println("Press enter");
//				MiscUtil.waitForReadStringAndEnterKeyPress();
//			}
//		}
		
 		return new Pair<double[],double[]>(ArrayUtil.doubleArrayFromList(fitnesses), new double[0]);
	}

}
