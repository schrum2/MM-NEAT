package edu.southwestern.tasks;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.genotypes.HyperNEATCPPNforDL4JGenotype;
import edu.southwestern.evolution.genotypes.TWEANNGenotype;
import edu.southwestern.evolution.metaheuristics.Metaheuristic;
import edu.southwestern.evolution.mulambda.MuLambda;
import edu.southwestern.log.EvalLog;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.scores.Score;
import edu.southwestern.tasks.mspacman.MsPacManTask;
import edu.southwestern.util.PopulationUtil;
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.file.FileUtilities;
import edu.southwestern.util.file.Serialization;
import edu.southwestern.util.graphics.DrawingPanel;

/**
 * A task for which an individual's fitness depends only on itself. In other
 * words, the genotype is evaluated in isolation, without interacting with any
 * other members of the population.
 *
 * @author Jacob Schrum
 * @param <T>
 *            Phenotype of evolved agent
 */
public abstract class LonerTask<T> implements SinglePopulationTask<T> {

	/**
	 * Since agents are evaluated in isolation, it is possible to parallelize
	 * their evaluation. This thread class enables parallel evaluation, and
	 * returns the results of evaluation.
	 *
	 */
	public class EvaluationThread implements Callable<Score<T>> {

		private final Genotype<T> genotype;
		private final LonerTask<T> task;

		/**
		 * a constructor for creating an evaluation thread
		 * 
		 * @param task
		 * @param g
		 */
		public EvaluationThread(LonerTask<T> task, Genotype<T> g) {
			this.genotype = g;
			this.task = task;
		}

		/**
		 * Creates a graphical representation of this task if requested and
		 * finds the fitness score for the genotype
		 * 
		 * @return score the fitness score of the agent of this task based on
		 *         evaluation
		 */
		@SuppressWarnings("unchecked")
		@Override
		public Score<T> call() {
			// Before any evaluation happens
			preEval();
			//System.out.println("preEval done on gen " + MMNEAT.ea.currentGeneration());
			
			Pair<DrawingPanel, DrawingPanel> drawPanels = CommonTaskUtil.getDrawingPanels(genotype);
			
			DrawingPanel panel = drawPanels.t1;
			DrawingPanel cppnPanel = drawPanels.t2;
			// Output a report about the specific evals
			if (CommonConstants.evalReport) {
				MMNEAT.evalReport = new EvalLog("Eval-Net" + genotype.getId());
			}
			long before = System.currentTimeMillis();
			// finds the score based on evaluation of the task's genotype
			Score<T> score = task.evaluate(genotype);
			long after = System.currentTimeMillis();
			// if there is an evalReport, save it
			if (MMNEAT.evalReport != null) {
				if (CommonConstants.recordPacman) {
					// Copy the eval report
					CopyOption[] options = new CopyOption[] { StandardCopyOption.REPLACE_EXISTING,
							StandardCopyOption.COPY_ATTRIBUTES };
					try {
						Files.copy(Paths.get(MMNEAT.evalReport.getFilePath()), Paths.get(MsPacManTask.saveFilePrefix
								+ Parameters.parameters.stringParameter("pacmanSaveFile") + ".eval"), options);
					} catch (IOException ex) {
						System.out.println("Could not save eval report");
						System.exit(1);
					}
				}
				MMNEAT.evalReport.close();
			}
			score.totalEvalTime = (after - before);
			// May need a Reentrant lock on this, if it is still used
			for (Metaheuristic<T> m : MMNEAT.metaheuristics) {
				m.augmentScore(score);
			}
			// print fitness score and genotype information then dispose the
			// panel, releasing system resources
			if (panel != null) {
				TWEANNGenotype genotype = score.individual instanceof HyperNEATCPPNforDL4JGenotype ? ((HyperNEATCPPNforDL4JGenotype) score.individual).getCPPN() : (TWEANNGenotype) score.individual;
				System.out.println("Module Usage: " + Arrays.toString(genotype.getModuleUsage()));
				System.out.println("Fitness: " + score.toString());
				panel.dispose();
			} 
			if(cppnPanel != null) {
				cppnPanel.dispose();
			}
			return score;
		}
	}

	private final boolean parallel;
	private final int threads;

	/**
	 * constructor for a LonerTask based upon command line specified evaluation
	 * and thread parameters
	 */
	public LonerTask() {
		this.parallel = Parameters.parameters.booleanParameter("parallelEvaluations");
		this.threads = Parameters.parameters.integerParameter("threads");
	}

	/**
	 * a method to evaluate one genotype
	 * 
	 * @param genotype
	 *            to evaluate
	 * @return the fitness score of the genotype
	 */
	public Score<T> evaluateOne(Genotype<T> genotype) {
		return new EvaluationThread(this, genotype).call();
	}
	
	/**
	 * Code that can be executed before each evaluation starts
	 */
	public void preEval() {
		// Do nothing by default
	}

	/**
	 * evaluate all of the genotypes in the population
	 * 
	 * @param population
	 *            the population
	 * @return scores a list of the fitness scores of the population
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<Score<T>> evaluateAll(ArrayList<Genotype<T>> population) {
		// a list of the fitness scores of the population
		ArrayList<Score<T>> scores = new ArrayList<Score<T>>(population.size());

		ExecutorService poolExecutor = null;
		ArrayList<Future<Score<T>>> futures = null;
		ArrayList<EvaluationThread> calls = new ArrayList<EvaluationThread>(population.size());

		// get each genotype for the population and add an EvaluationThread for
		// it to the calls list
		for (int i = 0; i < population.size(); i++) {
			Genotype<T> genotype = population.get(i);
			EvaluationThread callable = new EvaluationThread(this, genotype);
			calls.add(callable);
		}

		if (parallel) {
			poolExecutor = Executors.newFixedThreadPool(threads);
			futures = new ArrayList<Future<Score<T>>>(population.size());
			for (int i = 0; i < population.size(); i++) {
				Future<Score<T>> future = poolExecutor.submit(calls.get(i));
				futures.add(future);
			}
		}

		// General tracking of best in each objective
		double[] bestObjectives = minScores();
		Genotype<T>[] bestGenotypes = new Genotype[bestObjectives.length];
		Score<T>[] bestScores = new Score[bestObjectives.length];

		// some pac man variables that only apply if pac man is being used to
		// save the best pac man later
		int maxPacManScore = 0;
		Genotype<T> bestPacMan = null;
		Score<T> bestScoreSet = null;
		boolean trackBestPacManScore = CommonConstants.netio && this instanceof MsPacManTask
				&& MMNEAT.ea instanceof MuLambda && ((MuLambda<T>) MMNEAT.ea).evaluatingParents;
		for (int i = 0; i < population.size(); i++) {
			try {
				Score<T> s = parallel ? futures.get(i).get() : calls.get(i).call();
				// Specific to Ms Pac-Man
				if (trackBestPacManScore) {
					int gameScore = (int) s.otherStats[0]; // Game Score is always first
					if (gameScore >= maxPacManScore) {
						bestPacMan = s.individual;
						maxPacManScore = gameScore;
						bestScoreSet = s;
					}
				}
				// Best in each objective
				for (int j = 0; j < bestObjectives.length; j++) {
					double objectiveScore = s.scores[j];
                    // i == 0 saves first member of the population as the tentative best until a better individual is found
					if (i == 0 || objectiveScore >= bestObjectives[j]) {
                        // update best individual in objective j
						bestGenotypes[j] = s.individual;
						bestObjectives[j] = objectiveScore;
						bestScores[j] = s;
					}
				}
				scores.add(s);
			} catch (InterruptedException | ExecutionException ex) {
				ex.printStackTrace();
				System.exit(1);
			}
		}

		if (bestPacMan != null) {
			// Save best pacman
			String bestPacManDir = FileUtilities.getSaveDirectory() + "/bestPacMan";
			File bestDir = new File(bestPacManDir);
			// Delete old contents/team
			if (bestDir.exists()) {
				FileUtilities.deleteDirectoryContents(bestDir);
			} else {
				bestDir.mkdir();
			}
			Serialization.save(bestPacMan, bestPacManDir + "/bestPacMan");
			// System.out.println("Saved best Ms. Pac-Man agent with score of "+maxPacManScore);
			FileUtilities.simpleFileWrite(bestPacManDir + "/score.txt", bestScoreSet.toString());
		}

		if (CommonConstants.netio) {
			PopulationUtil.saveBestOfCurrentGen(bestObjectives, bestGenotypes, bestScores);
		}

		if (parallel) {
			poolExecutor.shutdown();
		}

		return scores;
	}

	/**
	 * defines the evaluate method to be implemented elsewhere
	 * 
	 * @param individual
	 *            whose genotype will be evaluated
	 * @return the fitness score of the individual
	 */
	public abstract Score<T> evaluate(Genotype<T> individual);

	/**
	 * Default objective mins of 0.
	 */
	@Override
	public double[] minScores() {
		return new double[this.numObjectives()];
	}

	/**
	 * Number of scores other than objectives that are tracked
	 * 
	 * @return default of 0 can be overridden
	 */
	public int numOtherScores() {
		return 0;
	}

	/**
	 * Return domain-specific behavior vector. Don't need to define if it won't
	 * be used, hence the default definition of null. A behavior vector is a
	 * collection of numbers that somehow characterizes the behavior of the
	 * agent in the domain.
	 *
	 * @return behavior vector
	 */
	public ArrayList<Double> getBehaviorVector() {
		return null;
	}

	/**
	 * Default to empty
	 */
    @Override
	public void finalCleanup() {
	}
}
