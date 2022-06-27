package edu.southwestern.tasks.mario;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;

import ch.idsia.ai.agents.Agent;
import ch.idsia.mario.engine.level.Level;
import ch.idsia.tools.CmdLineOptions;
import ch.idsia.tools.EvaluationInfo;
import ch.idsia.tools.EvaluationOptions;
import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.GenerationalEA;
import edu.southwestern.evolution.genotypes.CPPNOrDirectToGANGenotype;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.mapelites.Archive;
import edu.southwestern.evolution.mapelites.generalmappings.KLDivergenceBinLabels;
import edu.southwestern.evolution.mapelites.generalmappings.LatentVariablePartitionSumBinLabels;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.scores.Score;
import edu.southwestern.tasks.NoisyLonerTask;
import edu.southwestern.tasks.export.JsonLevelGenerationTask;
import edu.southwestern.tasks.interactive.mario.MarioCPPNtoGANLevelBreederTask;
import edu.southwestern.tasks.interactive.mario.MarioLevelBreederTask;
import edu.southwestern.tasks.mario.gan.GANProcess;
import edu.southwestern.tasks.mario.level.LevelParser;
import edu.southwestern.tasks.mario.level.MarioLevelUtil;
import edu.southwestern.tasks.mario.level.MarioState;
import edu.southwestern.tasks.mario.level.MarioState.MarioAction;
import edu.southwestern.tasks.mario.level.OldLevelParser;
import edu.southwestern.tasks.megaman.LevelNovelty;
import edu.southwestern.util.ClassCreation;
import edu.southwestern.util.datastructures.ArrayUtil;
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.file.FileUtilities;
import edu.southwestern.util.graphics.GraphicsUtil;
import edu.southwestern.util.random.RandomNumbers;
import edu.southwestern.util.search.AStarSearch;
import edu.southwestern.util.search.Search;


/**
 * 
 * Evolve Mario levels using an agent,
 * like the Mario A* Agent, as a means of evaluating.
 * Levels can be generated by CPPNs or a GAN, but this is
 * done in child classes.
 * 
 * @author Jacob Schrum
 *
 * @param <T>
 */
public abstract class MarioLevelTask<T> extends NoisyLonerTask<T> implements JsonLevelGenerationTask<T> {	

	public static final int SEGMENT_WIDTH_IN_BLOCKS = 28; // GAN training window
	private static final int PIXEL_BLOCK_WIDTH = 16; // Is this right?

	private Agent agent;
	private int numFitnessFunctions;
	private boolean fitnessRequiresSimulation;
	private boolean segmentFitness;
	private ArrayList<List<Integer>> targetLevel = null;
	private int[][][] klDivLevels;
	
	private boolean initialized = false; // become true on first evaluation

	public static final int DECORATION_FREQUENCY_STAT_INDEX = 0;
	public static final int LENIENCY_STAT_INDEX = 1;
	public static final int NEGATIVE_SPACE_STAT_INDEX = 2;
	public static final int NUM_SEGMENT_STATS = 3;

	// Calculated in oneEval, so it can be passed on the getBehaviorVector
	private double fitnessSaveThreshold = Parameters.parameters.doubleParameter("fitnessSaveThreshold");
	
	public MarioLevelTask() {
		LevelNovelty.setGame("mario"); //In case Novelty binning is used with MAP Elites
		
		// Replace this with a command line parameter
		try {
			agent = (Agent) ClassCreation.createObject("marioLevelAgent");
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			System.out.println("Could not instantiate Mario agent");
			System.exit(1);
		}

		// FitnessfitnessRequiresSimulation
		numFitnessFunctions = 0;
		fitnessRequiresSimulation = false; // Until proven otherwise
		segmentFitness = false;
		if(Parameters.parameters.booleanParameter("marioProgressPlusJumpsFitness")) {
			// First maximize progress through the level.
			// If the level is cleared, then maximize the duration of the
			// level, which will indicate that it is challenging.
			MMNEAT.registerFitnessFunction("ProgressPlusJumps");
			fitnessRequiresSimulation = true;
			numFitnessFunctions++;
		} 
		if(Parameters.parameters.booleanParameter("marioProgressPlusTimeFitness")) {
			// Levels that take longer must be harder
			MMNEAT.registerFitnessFunction("ProgressPlusTime");
			fitnessRequiresSimulation = true;
			numFitnessFunctions++;
		}
		if(Parameters.parameters.booleanParameter("marioLevelMatchFitness")) {
			MMNEAT.registerFitnessFunction("LevelMatch");
			numFitnessFunctions++;
			// Load level representation from file here
			String levelFileName = Parameters.parameters.stringParameter("marioTargetLevel"); // Does not have a default value yet
			targetLevel = MarioLevelUtil.listLevelFromVGLCFile(levelFileName);

			// View whole dungeon layout
			Level level = Parameters.parameters.booleanParameter("marioGANUsesOriginalEncoding") ? OldLevelParser.createLevelJson(targetLevel) : LevelParser.createLevelJson(targetLevel);			
			BufferedImage image = MarioLevelUtil.getLevelImage(level);
			String saveDir = FileUtilities.getSaveDirectory();
			GraphicsUtil.saveImage(image, saveDir + File.separator + "Target.png");

		}
		// Encourages an alternating pattern of Vanessa's objectives
		if(Parameters.parameters.booleanParameter("marioLevelAlternatingLeniency")) {
			MMNEAT.registerFitnessFunction("AlternatingLeniency");
			segmentFitness = true;
			numFitnessFunctions++;
		}
		if(Parameters.parameters.booleanParameter("marioLevelAlternatingNegativeSpace")) {
			MMNEAT.registerFitnessFunction("AlternatingNegativeSpace");
			segmentFitness = true;
			numFitnessFunctions++;			
		}
		if(Parameters.parameters.booleanParameter("marioLevelAlternatingDecoration")) {
			MMNEAT.registerFitnessFunction("AlternatingDecorationFrequency");
			segmentFitness = true;
			numFitnessFunctions++;
		}
		// Encourages an periodic pattern of Vanessa's objectives
		if(Parameters.parameters.booleanParameter("marioLevelPeriodicLeniency")) {
			MMNEAT.registerFitnessFunction("PeriodicLeniency");
			segmentFitness = true;
			numFitnessFunctions++;
		}
		if(Parameters.parameters.booleanParameter("marioLevelPeriodicNegativeSpace")) {
			MMNEAT.registerFitnessFunction("PeriodicNegativeSpace");
			segmentFitness = true;
			numFitnessFunctions++;			
		}
		if(Parameters.parameters.booleanParameter("marioLevelPeriodicDecoration")) {
			MMNEAT.registerFitnessFunction("PeriodicDecorationFrequency");
			segmentFitness = true;
			numFitnessFunctions++;
		}

		// Encourages a symmetric pattern of Vanessa's objectives
		if(Parameters.parameters.booleanParameter("marioLevelSymmetricLeniency")) {
			MMNEAT.registerFitnessFunction("SymmetricLeniency");
			segmentFitness = true;
			numFitnessFunctions++;			
		}
		if(Parameters.parameters.booleanParameter("marioLevelSymmetricNegativeSpace")) {
			MMNEAT.registerFitnessFunction("SymmetricNegativeSpace");
			segmentFitness = true;
			numFitnessFunctions++;						
		}
		if(Parameters.parameters.booleanParameter("marioLevelSymmetricDecoration")) {
			MMNEAT.registerFitnessFunction("SymmetricDecorationFrequency");
			segmentFitness = true;
			numFitnessFunctions++;			
		}

		if(Parameters.parameters.booleanParameter("marioSimpleAStarDistance")) {
			MMNEAT.registerFitnessFunction("SimpleA*Distance");
			numFitnessFunctions++;			
		}

		if(Parameters.parameters.booleanParameter("marioRandomFitness")) {
			MMNEAT.registerFitnessFunction("Random");
			numFitnessFunctions++;
		}
		if(Parameters.parameters.booleanParameter("marioDistinctSegmentFitness")) {
			MMNEAT.registerFitnessFunction("Random");
			numFitnessFunctions++;
		}
		if(numFitnessFunctions == 0) throw new IllegalStateException("At least one fitness function required to evolve Mario levels");
		// Other scores
		MMNEAT.registerFitnessFunction("Distance", false);
		MMNEAT.registerFitnessFunction("PercentDistance", false);
		MMNEAT.registerFitnessFunction("Time", false);
		MMNEAT.registerFitnessFunction("Jumps", false);
		for(int i=0; i<Parameters.parameters.integerParameter("marioGANLevelChunks"); i++){
			MMNEAT.registerFitnessFunction("DecorationFrequency-"+i,false);
			MMNEAT.registerFitnessFunction("Leniency-"+i,false);
			MMNEAT.registerFitnessFunction("NegativeSpace-"+i,false);
		}
	}

	private void setupKLDivLevelsForComparison() {
		if (MMNEAT.usingDiversityBinningScheme && MMNEAT.getArchiveBinLabelsClass() instanceof KLDivergenceBinLabels) {
			System.out.println("Instance of MAP Elites using KL Divergence Bin Labels");
			String level1FileName = Parameters.parameters.stringParameter("mapElitesKLDivLevel1"); 
			String level2FileName = Parameters.parameters.stringParameter("mapElitesKLDivLevel2"); 
			ArrayList<List<Integer>> level1List = MarioLevelUtil.listLevelFromVGLCFile(level1FileName);
			ArrayList<List<Integer>> level2List = MarioLevelUtil.listLevelFromVGLCFile(level2FileName);
			int[][] level1Array = ArrayUtil.int2DArrayFromListOfLists(level1List);
			int[][] level2Array = ArrayUtil.int2DArrayFromListOfLists(level2List);
			klDivLevels = new int[][][] {level1Array, level2Array};
		}
	}

	@Override
	public int numObjectives() {
		return numFitnessFunctions;  
	}

	public int numOtherScores() {
		return 4 + Parameters.parameters.integerParameter("marioGANLevelChunks") * 3; // Distance, Percentage, Time, and Jumps 
		//plus (decorationFrequency, leniency, negativeSpace) per level segment
	}

	@Override
	public double getTimeStamp() {
		return 0; // Not used
	}

	/**
	 * Different level generators use the genotype to generate a level in different ways
	 * @param individual Genotype 
	 * @return List of lists of integers corresponding to tile types
	 */
	public abstract ArrayList<List<Integer>> getMarioLevelListRepresentationFromGenotype(Genotype<T> individual);

	/**
	 * Different level generators generate levels of different lengths
	 * @param info 
	 * @return
	 */
	public abstract double totalPassableDistance(EvaluationInfo info);
	
	@Override
	public Pair<double[], double[]> oneEval(Genotype<T> individual, int num, HashMap<String, Object> behaviorMap) {
		if(!initialized) {
			setupKLDivLevelsForComparison();
			initialized = true;
		}
		List<List<Integer>> oneLevel = getMarioLevelListRepresentationFromGenotype(individual);
		// 0 is because the pseudo random seed is not used
		return evaluateOneLevel(oneLevel, 0, individual, behaviorMap);
	}

	/**
	 * Note that psuedoRandomSeed is completely ignored here
	 */
	public Pair<double[], double[]> evaluateOneLevel(List<List<Integer>> oneLevel, double psuedoRandomSeed, Genotype<T> individual, HashMap<String,Object> behaviorMap) {
		EvaluationInfo info = null;
		BufferedImage levelImage = null;		
		Level level = Parameters.parameters.booleanParameter("marioGANUsesOriginalEncoding") ? OldLevelParser.createLevelJson(oneLevel) : LevelParser.createLevelJson(oneLevel);			
		if(fitnessRequiresSimulation || CommonConstants.watch) {
			agent.reset(); // Get ready to play a new level
			EvaluationOptions options = new CmdLineOptions(new String[]{});
			options.setAgent(agent);
			options.setLevel(level);
			options.setMaxFPS(!(agent instanceof ch.idsia.ai.agents.human.HumanKeyboardAgent)); // Run fast when not playing
			options.setVisualization(CommonConstants.watch);

			List<EvaluationInfo> infos = MarioLevelUtil.agentPlaysLevel(options);
			// For now, assume a single evaluation
			info = infos.get(0);
		}
		
		if(MMNEAT.usingDiversityBinningScheme || CommonConstants.watch) {
			// View whole dungeon layout
			levelImage = MarioLevelUtil.getLevelImage(level);
			if(segmentFitness) { // Draw lines dividing the segments 
				Graphics2D g = (Graphics2D) levelImage.getGraphics();
				g.setColor(Color.MAGENTA);
				g.setStroke(new BasicStroke(4)); // Thicker line
				for(int i = 1; i < Parameters.parameters.integerParameter("marioGANLevelChunks"); i++) {
					g.drawLine(i*PIXEL_BLOCK_WIDTH*SEGMENT_WIDTH_IN_BLOCKS, 0, i*PIXEL_BLOCK_WIDTH*SEGMENT_WIDTH_IN_BLOCKS, levelImage.getHeight());
				}
			}
			
			// MAP Elites images get saved later, in a different directory
			if(!(MMNEAT.usingDiversityBinningScheme)) {
				String saveDir = FileUtilities.getSaveDirectory();
				int currentGen = ((GenerationalEA) MMNEAT.ea).currentGeneration();
				GraphicsUtil.saveImage(levelImage, saveDir + File.separator + (currentGen == 0 ? "initial" : "gen"+ currentGen) + File.separator + "MarioLevel"+individual.getId()+".png");
			}
		}


		double distancePassed = info == null ? 0 : info.lengthOfLevelPassedPhys;
		double percentLevelPassed = info == null ? 0 : distancePassed / totalPassableDistance(info);
		double time = info == null ? 0 : info.timeSpentOnLevel;
		double jumps = info == null ? 0 : info.jumpActionsPerformed;
		int numDistinctSegments = -1; // If not used, value will be -1
		
		double[] otherScores = new double[] {distancePassed, percentLevelPassed, time, jumps};
		
		//put each segment into a HashSet to see if it's  distinct
		HashSet<List<List<Integer>>> k = new HashSet<List<List<Integer>>>();
        List<List<List<Integer>>> levelWithParsedSegments = MarioLevelUtil.getSegmentsFromLevel(oneLevel, SEGMENT_WIDTH_IN_BLOCKS);
        ArrayList<double[]> lastLevelSegmentStats = null;
        double[] lastLevelCompleteStats = LevelParser.getWholeLevelStats(oneLevel);
        //int numSegments = 0;
        if(levelWithParsedSegments != null) {
        	// The concept of segments really only applies to GAN-generated levels, but not levels from MarioCPPNLevelTask
        	for(List<List<Integer>> segment : levelWithParsedSegments) {
        		k.add(segment);
        		//numSegments++;
        	}
        	numDistinctSegments = k.size();

        	// Adds Vanessa's Mario stats: Decoration Frequency, Leniency, Negative Space
        	lastLevelSegmentStats = Parameters.parameters.booleanParameter("marioGANUsesOriginalEncoding") ? OldLevelParser.getLevelStats(oneLevel, SEGMENT_WIDTH_IN_BLOCKS) : LevelParser.getLevelStats(oneLevel, SEGMENT_WIDTH_IN_BLOCKS);
        	for(double[] stats:lastLevelSegmentStats){
        		otherScores = ArrayUtils.addAll(otherScores, stats);
        	}
        }

		ArrayList<Double> fitnesses = new ArrayList<>(numFitnessFunctions);
		if(Parameters.parameters.booleanParameter("marioProgressPlusJumpsFitness")) {
			if(percentLevelPassed < 1.0) {
				fitnesses.add(percentLevelPassed);
			} else { // Level beaten
				fitnesses.add(1.0+jumps);
			}
		} 
		if(Parameters.parameters.booleanParameter("marioProgressPlusTimeFitness")) {
			if(percentLevelPassed < 1.0) {
				fitnesses.add(percentLevelPassed);
			} else { // Level beaten
				fitnesses.add(1.0+time);
			}
		}
		if(Parameters.parameters.booleanParameter("marioLevelMatchFitness")) {
			int diffCount = 0;

			if(oneLevel.size() != targetLevel.size()) {
				System.out.println("Target");
				System.out.println(targetLevel);
				System.out.println("Evolved");
				System.out.println(oneLevel);
				throw new IllegalStateException("Target level and evolved level are not even the same height.");
			}

			// This will hold the target level, except that every location of conflict with the evolved level will
			// be replaced with the blank passable background tile
			ArrayList<List<Integer>> targetDiff = new ArrayList<>();

			// TODO
			// Should this calculation include or eliminate the starting and ending regions we add to Mario levels?
			Iterator<List<Integer>> evolveIterator = oneLevel.iterator();
			Iterator<List<Integer>> targetIterator = targetLevel.iterator();
			while(evolveIterator.hasNext() && targetIterator.hasNext()) {
				Iterator<Integer> evolveRow = evolveIterator.next().iterator();
				Iterator<Integer> targetRow = targetIterator.next().iterator();
				List<Integer> diffRow = new ArrayList<>(targetLevel.get(0).size()); // For visualizing differences
				while(evolveRow.hasNext() && targetRow.hasNext()) {
					Integer nextInTarget = targetRow.next();
					if(!evolveRow.next().equals(nextInTarget)) {
						diffCount++;
						diffRow.add(-100); // An illegal tile. Indicates a conflict
					} else {
						diffRow.add(nextInTarget);
					}
				}
				targetDiff.add(diffRow);
			}
			// More differences = worse fitness
			fitnesses.add(-1.0*diffCount);

			if(CommonConstants.watch) {
				// View whole level layout
				Level diffLevel = Parameters.parameters.booleanParameter("marioGANUsesOriginalEncoding") ? OldLevelParser.createLevelJson(targetDiff) : LevelParser.createLevelJson(targetDiff);			
				BufferedImage image = MarioLevelUtil.getLevelImage(diffLevel);
				String saveDir = FileUtilities.getSaveDirectory();
				int currentGen = ((GenerationalEA) MMNEAT.ea).currentGeneration();
				GraphicsUtil.saveImage(image, saveDir + File.separator + (currentGen == 0 ? "initial" : "gen"+ currentGen) + File.separator + "MarioLevel"+individual.getId()+"TargetDiff.png");
			}
		}

		// Encourages an alternating pattern of Vanessa's objectives
		if(Parameters.parameters.booleanParameter("marioLevelAlternatingLeniency")) {
			fitnesses.add(alternatingStatScore(lastLevelSegmentStats, LENIENCY_STAT_INDEX));
		}
		if(Parameters.parameters.booleanParameter("marioLevelAlternatingNegativeSpace")) {
			fitnesses.add(alternatingStatScore(lastLevelSegmentStats, NEGATIVE_SPACE_STAT_INDEX));
		}
		if(Parameters.parameters.booleanParameter("marioLevelAlternatingDecoration")) {
			fitnesses.add(alternatingStatScore(lastLevelSegmentStats, DECORATION_FREQUENCY_STAT_INDEX));
		}

		// Encourages a periodic pattern of Vanessa's objectives
		if(Parameters.parameters.booleanParameter("marioLevelPeriodicLeniency")) {
			fitnesses.add(periodicStatScore(lastLevelSegmentStats, LENIENCY_STAT_INDEX));
		}
		if(Parameters.parameters.booleanParameter("marioLevelPeriodicNegativeSpace")) {
			fitnesses.add(periodicStatScore(lastLevelSegmentStats, NEGATIVE_SPACE_STAT_INDEX));
		}
		if(Parameters.parameters.booleanParameter("marioLevelPeriodicDecoration")) {
			fitnesses.add(periodicStatScore(lastLevelSegmentStats, DECORATION_FREQUENCY_STAT_INDEX));
		}

		// Encourages a symmetric pattern of Vanessa's objectives
		if(Parameters.parameters.booleanParameter("marioLevelSymmetricLeniency")) {
			fitnesses.add(symmetricStatScore(lastLevelSegmentStats, LENIENCY_STAT_INDEX));
		}
		if(Parameters.parameters.booleanParameter("marioLevelSymmetricNegativeSpace")) {
			fitnesses.add(symmetricStatScore(lastLevelSegmentStats, NEGATIVE_SPACE_STAT_INDEX));
		}
		if(Parameters.parameters.booleanParameter("marioLevelSymmetricDecoration")) {
			fitnesses.add(symmetricStatScore(lastLevelSegmentStats, DECORATION_FREQUENCY_STAT_INDEX));
		}

		double simpleAStarDistance = -1;
		if(Parameters.parameters.booleanParameter("marioSimpleAStarDistance")) {
			MarioState start = new MarioState(MarioState.preprocessLevel(oneLevel));
			Search<MarioAction,MarioState> search = new AStarSearch<>(MarioState.moveRight);
			HashSet<MarioState> mostRecentVisited = null;
			ArrayList<MarioAction> actionSequence = null;
			try{
				// TODO: Somehow extract/recreate the sequence of locations from the search
				
				actionSequence = ((AStarSearch<MarioAction, MarioState>) search).search(start, true, Parameters.parameters.integerParameter("aStarSearchBudget"));
				if(actionSequence == null) {
					fitnesses.add(-1.0); // failed search 				
				} else {
					simpleAStarDistance = 1.0*actionSequence.size(); // For MAP Elites bin later
					fitnesses.add(1.0*actionSequence.size()); // maximize length of solution
				}
			} catch(IllegalStateException e) {
				// Sometimes this exception occurs from A*. Not sure why, but we can take this to mean the level has a problem and deserves bad fitness.
				fitnesses.add(-1.0); // failed search 				
			} finally {
				mostRecentVisited = ((AStarSearch<MarioAction, MarioState>) search).getVisited();
			}

			if(MMNEAT.usingDiversityBinningScheme || (CommonConstants.netio && CommonConstants.watch)) {
				// Add X marks to the original level image, which should exist if since watch saved it above
				if(mostRecentVisited != null) {
					Graphics2D g = (Graphics2D) levelImage.getGraphics();
					g.setColor(Color.BLUE);
					g.setStroke(new BasicStroke(4)); // Thicker line
					for(MarioState s : mostRecentVisited) {
						int x = s.marioX - LevelParser.BUFFER_WIDTH;
						int y = s.marioY;
						g.drawLine(x*PIXEL_BLOCK_WIDTH, y*PIXEL_BLOCK_WIDTH, (x+1)*PIXEL_BLOCK_WIDTH, (y+1)*PIXEL_BLOCK_WIDTH);
						g.drawLine((x+1)*PIXEL_BLOCK_WIDTH, y*PIXEL_BLOCK_WIDTH, x*PIXEL_BLOCK_WIDTH, (y+1)*PIXEL_BLOCK_WIDTH);
					}
					
					if(actionSequence != null) {
						MarioState current = start;
						g.setColor(Color.RED);
						for(MarioAction a : actionSequence) {
							int x = current.marioX - LevelParser.BUFFER_WIDTH;
							int y = current.marioY;
							g.drawLine(x*PIXEL_BLOCK_WIDTH, y*PIXEL_BLOCK_WIDTH, (x+1)*PIXEL_BLOCK_WIDTH, (y+1)*PIXEL_BLOCK_WIDTH);
							g.drawLine((x+1)*PIXEL_BLOCK_WIDTH, y*PIXEL_BLOCK_WIDTH, x*PIXEL_BLOCK_WIDTH, (y+1)*PIXEL_BLOCK_WIDTH);
							current = (MarioState) current.getSuccessor(a);
						}
					}
				}

				if(!(MMNEAT.usingDiversityBinningScheme)) {
					// View level with path
					String saveDir = FileUtilities.getSaveDirectory();
					int currentGen = ((GenerationalEA) MMNEAT.ea).currentGeneration();
					GraphicsUtil.saveImage(levelImage, saveDir + File.separator + (currentGen == 0 ? "initial" : "gen"+ currentGen) + File.separator + "MarioLevel"+individual.getId()+"SolutionPath.png");
				}
			}
		}

		if(Parameters.parameters.booleanParameter("marioRandomFitness")) {
			fitnesses.add(RandomNumbers.fullSmallRand());
		}
		if(Parameters.parameters.booleanParameter("marioDistinctSegmentFitness")) {
			fitnesses.add(new Double(numDistinctSegments));
		}
		// Could conceivably also be used for behavioral diversity instead of map elites, but this would be a weird behavior vector from a BD perspective
		if(MMNEAT.usingDiversityBinningScheme) { // (MMNEAT.ea instanceof MAPElites) -> (MMNEAT.usingDiversityBinningScheme)
			
			assert Parameters.parameters.booleanParameter("marioSimpleAStarDistance") : "Bin score will be -1 everywhere if you don't calculate the A* distance. Set marioSimpleAStarDistance:true";
			double binScore = simpleAStarDistance;
			behaviorMap.put("binScore", binScore); // Quality Score!
			
			// All possible behavior characterization information
			behaviorMap.put("Level Stats",lastLevelSegmentStats);
			behaviorMap.put("Complete Stats", lastLevelCompleteStats);
			behaviorMap.put("Distinct Segments",numDistinctSegments);
			// It would be slightly more efficient to use levelWithParsedSegments here, but then Mario Novelty
			// calculation would require distinct code in comparison with Mega Man, which makes the code a mess.
			behaviorMap.put("Level", oneLevel); // Used to calculate Level Novelty
			if (MMNEAT.getArchiveBinLabelsClass() instanceof KLDivergenceBinLabels) { 
				int[][] oneLevelAs2DArray = ArrayUtil.int2DArrayFromListOfLists(oneLevel);
				behaviorMap.put("2D Level", oneLevelAs2DArray);
				behaviorMap.put("Comparison Levels", klDivLevels);
			} else if (MMNEAT.getArchiveBinLabelsClass() instanceof LatentVariablePartitionSumBinLabels) {
				@SuppressWarnings("unchecked")
				ArrayList<Double> rawVector = (ArrayList<Double>) individual.getPhenotype();
				double[] latentVector = ArrayUtil.doubleArrayFromList(rawVector);
				behaviorMap.put("Solution Vector", latentVector);
			}			
			int dim1D = MMNEAT.getArchiveBinLabelsClass().oneDimensionalIndex(behaviorMap);
			behaviorMap.put("dim1D", dim1D); // Save so it does not need to be computed again
			saveMAPElitesImages(individual, levelImage, dim1D, binScore);
		}
		return new Pair<double[],double[]>(ArrayUtil.doubleArrayFromList(fitnesses), otherScores);
		
	}

	/**
	 * sets the bins and saves MAPElites images to archive
	 * @param individual the genotype
	 * @param levelImage the buffered image of the level
	 * @param dim1D 1D index in bin labels
	 * @param binScore the bin score
	 */
	private void saveMAPElitesImages(Genotype<T> individual, BufferedImage levelImage, int dim1D, double binScore) {
		// Saving map elites bin images	
		if(CommonConstants.netio) {
			//System.out.println("Save archive images");
			@SuppressWarnings("unchecked")
			Archive<T> archive = MMNEAT.getArchive();
			List<String> binLabels = archive.getBinMapping().binLabels();

			// Index in flattened bin array
			Score<T> elite = archive.getElite(dim1D);
			// If the bin is empty, or the candidate is better than the elite for that bin's score
			if(elite == null || binScore > elite.behaviorIndexScore()) {
				if(binScore > fitnessSaveThreshold) {
					assert individual != null : "null individual";
					String fileName = String.format("%7.5f", binScore) + "_" + individual.getId() + ".png";
					if(individual instanceof CPPNOrDirectToGANGenotype) {
						CPPNOrDirectToGANGenotype temp = (CPPNOrDirectToGANGenotype) individual;
						if(temp.getFirstForm()) fileName = "CPPN-" + fileName;
						else fileName = "Direct-" + fileName;
					}
					String binPath = archive.getArchiveDirectory() + File.separator + binLabels.get(dim1D);
					String fullName = binPath + "_" + fileName;
					System.out.println(fullName);
					GraphicsUtil.saveImage(levelImage, fullName);
				}
			}
		}
	}

	public static double sumStatScore(ArrayList<double[]> levelStats, int statIndex) {
		double total = 0;
		for(int i = 0; i < levelStats.size(); i++) {
			total += levelStats.get(i)[statIndex];
		}
		return total;
	}

	public static double periodicStatScore(ArrayList<double[]> levelStats, int statIndex) {
		double evenTotal = 0;
		// even differences
		for(int i = 2; i < levelStats.size(); i += 2) {
			// Differences between even segments
			evenTotal += Math.abs(levelStats.get(i-2)[statIndex] - levelStats.get(i)[statIndex]);
		}
		double oddTotal = 0;
		// odd differences
		for(int i = 3; i < levelStats.size(); i += 2) {
			// Differences between odd segments
			oddTotal += Math.abs(levelStats.get(i-2)[statIndex] - levelStats.get(i)[statIndex]);
		}
		// Negative because differences are discouraged
		return - (evenTotal + oddTotal);
	}

	public static double symmetricStatScore(ArrayList<double[]> levelStats, int statIndex) {
		double total = 0;
		for(int i = 0; i < levelStats.size()/2; i++) {
			// Diff between symmetric segments
			total += Math.abs(levelStats.get(i)[statIndex] - levelStats.get(levelStats.size()-1-i)[statIndex]);
		}
		return - total; // Negative: Max symmetry means minimal difference in symmetric segments
	}

	public static double alternatingStatScore(ArrayList<double[]> levelStats, int statIndex) {
		double total = 0;
		for(int i = 1; i < levelStats.size(); i++) {
			// Differences between adjacent segments
			total += Math.abs(levelStats.get(i-1)[statIndex] - levelStats.get(i)[statIndex]);
		}
		return total;
	}
	
	@Override
	public void postConstructionInitialization() {
		GANProcess.type = GANProcess.GAN_TYPE.MARIO;
		if(MMNEAT.task instanceof MarioCPPNtoGANLevelTask|| MMNEAT.task instanceof MarioCPPNOrDirectToGANLevelTask) {
			// Evolving CPPNs that create latent vectors that are sent to a GAN
			MMNEAT.setNNInputParameters(MarioCPPNtoGANLevelBreederTask.UPDATED_INPUTS.length, GANProcess.latentVectorLength());
		} else {
			// This line only matters for the CPPN version of the task, but doesn't hurt the GAN version, which does not evolve networks
			MMNEAT.setNNInputParameters(MarioLevelBreederTask.INPUTS.length, MarioLevelBreederTask.OUTPUTS.length);
		}
	}
	
	public static void main(String[] args) throws FileNotFoundException, NoSuchMethodException {
		int runNum = 24;
		//MMNEAT.main(("runNumber:"+runNum+" randomSeed:"+runNum+" base:mariolevelskldiv log:MarioLevelsKLDiv-test saveTo:test marioGANLevelChunks:5 marioGANUsesOriginalEncoding:false marioGANModel:Mario1_Overworld_5_Epoch5000.pth GANInputSize:5 trials:1 lambda:50 mu:50 maxGens:5000 io:true netio:true genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype mating:true fs:false task:edu.southwestern.tasks.mario.MarioGANLevelTask cleanFrequency:-1 saveAllChampions:true cleanOldNetworks:false logTWEANNData:false logMutationAndLineage:false marioStuckTimeout:20 watch:false marioProgressPlusJumpsFitness:false marioRandomFitness:false marioSimpleAStarDistance:true ea:edu.southwestern.evolution.mapelites.MAPElites experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment mapElitesBinLabels:edu.southwestern.evolution.mapelites.generalmappings.KLDivergenceBinLabels steadyStateIndividualsPerGeneration:100 aStarSearchBudget:100000 mapElitesKLDivLevel1:data\\VGLC\\SuperMarioBrosNewEncoding\\overworld\\mario-8-1.txt mapElitesKLDivLevel2:data\\VGLC\\SuperMarioBrosNewEncoding\\overworld\\mario-3-1.txt klDivBinDimension:100 klDivMaxValue:0.3").split(" "));
		//MMNEAT.main(("runNumber:"+runNum+" randomSeed:"+runNum+" base:mariolevelskldiv log:MarioLevelsKLDiv-testCMAME saveTo:testCMAME marioGANLevelChunks:5 marioGANUsesOriginalEncoding:false marioGANModel:Mario1_Overworld_5_Epoch5000.pth GANInputSize:5 trials:1 lambda:50 mu:10 maxGens:5000 io:true netio:true genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype mating:true fs:false task:edu.southwestern.tasks.mario.MarioGANLevelTask cleanFrequency:-1 saveAllChampions:true cleanOldNetworks:false logTWEANNData:false logMutationAndLineage:false marioStuckTimeout:20 watch:false marioProgressPlusJumpsFitness:false marioRandomFitness:false marioSimpleAStarDistance:true ea:edu.southwestern.evolution.mapelites.CMAME experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment mapElitesBinLabels:edu.southwestern.evolution.mapelites.generalmappings.KLDivergenceBinLabels steadyStateIndividualsPerGeneration:100 aStarSearchBudget:100000 mapElitesKLDivLevel1:data\\VGLC\\SuperMarioBrosNewEncoding\\overworld\\mario-8-1.txt mapElitesKLDivLevel2:data\\VGLC\\SuperMarioBrosNewEncoding\\overworld\\mario-3-1.txt klDivBinDimension:100 klDivMaxValue:0.3 numImprovementEmitters:3 numOptimizingEmitters:0").split(" "));
		//MMNEAT.main(("runNumber:"+runNum+" randomSeed:"+runNum+" base:mariolevelsnsga2 log:MarioLevelNSGA2-PseudoArchive saveTo:PseudoArchive trackPseudoArchive:true netio:true marioGANLevelChunks:5 marioGANUsesOriginalEncoding:false marioGANModel:Mario1_Overworld_5_Epoch5000.pth GANInputSize:5 trials:1 lambda:50 mu:10 maxGens:200 genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype mating:true fs:false task:edu.southwestern.tasks.mario.MarioGANLevelTask cleanFrequency:-1 saveAllChampions:true cleanOldNetworks:false logTWEANNData:false logMutationAndLineage:false marioStuckTimeout:20 watch:false marioProgressPlusJumpsFitness:false marioRandomFitness:false marioSimpleAStarDistance:true mapElitesBinLabels:edu.southwestern.evolution.mapelites.generalmappings.KLDivergenceBinLabels steadyStateIndividualsPerGeneration:100 aStarSearchBudget:100000 mapElitesKLDivLevel1:data\\\\VGLC\\\\SuperMarioBrosNewEncoding\\\\overworld\\\\mario-8-1.txt mapElitesKLDivLevel2:data\\\\VGLC\\\\SuperMarioBrosNewEncoding\\\\overworld\\\\mario-3-1.txt klDivBinDimension:100 klDivMaxValue:0.3").split(" "));
		MMNEAT.main(("runNumber:"+runNum+" randomSeed:"+runNum+" base:mariolevelsdecoratensleniency log:MarioLevelsDecorateNSLeniency-CPPN2GAN saveTo:CPPN2GAN marioGANLevelChunks:10 marioGANUsesOriginalEncoding:false marioGANModel:Mario1_Overworld_5_Epoch5000.pth GANInputSize:5 trials:1 mu:100 maxGens:100000 io:true netio:true mating:true fs:false task:edu.southwestern.tasks.mario.MarioCPPNtoGANLevelTask allowMultipleFunctions:true ftype:0 netChangeActivationRate:0.3 cleanFrequency:-1 recurrency:false saveAllChampions:true cleanOldNetworks:false logTWEANNData:false logMutationAndLineage:false marioStuckTimeout:20 watch:false marioProgressPlusJumpsFitness:false marioRandomFitness:false marioSimpleAStarDistance:true ea:edu.southwestern.evolution.mapelites.MAPElites experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment mapElitesBinLabels:edu.southwestern.tasks.mario.MarioMAPElitesDecorNSAndLeniencyBinLabels steadyStateIndividualsPerGeneration:100 aStarSearchBudget:100000 includeCosineFunction:true includeIdFunction:true").split(" "));
	}

}
