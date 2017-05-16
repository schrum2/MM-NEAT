package edu.utexas.cs.nn.tasks;

import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.evolution.genotypes.HyperNEATCPPNGenotype;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.evolution.lineage.Offspring;
import edu.utexas.cs.nn.evolution.metaheuristics.Metaheuristic;
import edu.utexas.cs.nn.evolution.mulambda.MuLambda;
import edu.utexas.cs.nn.evolution.ucb.UCB1Comparator;
import edu.utexas.cs.nn.log.EvalLog;
import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.networks.hyperneat.HyperNEATTask;
import edu.utexas.cs.nn.networks.hyperneat.HyperNEATUtil;
import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.scores.Score;
import edu.utexas.cs.nn.tasks.breve2D.Breve2DTask;
import edu.utexas.cs.nn.tasks.gridTorus.TorusPredPreyTask;
import edu.utexas.cs.nn.tasks.mspacman.MsPacManTask;
import edu.utexas.cs.nn.util.PopulationUtil;
import edu.utexas.cs.nn.util.file.FileUtilities;
import edu.utexas.cs.nn.util.graphics.DrawingPanel;
import edu.utexas.cs.nn.util.graphics.Plot;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.*;
import wox.serial.Easy;

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

	public static final int NETWORK_WINDOW_OFFSET = 0;

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
		public Score<T> call() {//TODO 
			DrawingPanel panel = null;
			DrawingPanel cppnPanel = null;
			@SuppressWarnings("unused")
			ArrayList<DrawingPanel> weightPanels;
			// DrawingPanel[] subPanels = null;
			if (genotype instanceof TWEANNGenotype) {
				if (CommonConstants.showNetworks) {
					panel = new DrawingPanel(TWEANN.NETWORK_VIEW_DIM, TWEANN.NETWORK_VIEW_DIM, "Evolving Network");
					panel.setLocation(NETWORK_WINDOW_OFFSET, 0);
					((TWEANNGenotype) genotype).getPhenotype().draw(panel);
					if(genotype instanceof HyperNEATCPPNGenotype) {//TODO
						HyperNEATCPPNGenotype hngt = (HyperNEATCPPNGenotype) genotype;
                                                if( Parameters.parameters.booleanParameter("showCPPN")) {
							cppnPanel = new DrawingPanel(500, 500, "Evolved CPPN");
							cppnPanel.setLocation(TWEANN.NETWORK_VIEW_DIM + NETWORK_WINDOW_OFFSET, 0);
							hngt.getCPPN().draw(cppnPanel);
						}
						if(Parameters.parameters.booleanParameter("showWeights")){
							weightPanels = HyperNEATUtil.drawWeight(hngt.getSubstrateGenotype((HyperNEATTask) task),(HyperNEATTask) task); //TODO
						}

					}
					// if(genotype instanceof HierarchicalTWEANNGenotype){
					// HierarchicalTWEANNGenotype htg =
					// (HierarchicalTWEANNGenotype) genotype;
					// ArrayList<Integer> subnetIds =
					// htg.subNetIds.getPhenotype();
					// subPanels = new DrawingPanel[subnetIds.size()];
					// for(int i = 0; i < subPanels.length; i++){
					// subPanels[i] = new DrawingPanel(TWEANN.NETWORK_VIEW_DIM,
					// TWEANN.NETWORK_VIEW_DIM, "Subnet " + i);
					// subPanels[i].setLocation(NETWORK_WINDOW_OFFSET + (i *
					// TWEANN.NETWORK_VIEW_DIM), TWEANN.NETWORK_VIEW_DIM);
					// ((TWEANNGenotype)
					// htg.getSubNetGenotype(i)).getPhenotype().draw(subPanels[i]);
					// }
					// }
				}
				if (CommonConstants.viewModePreference && TWEANN.preferenceNeuronPanel == null && TWEANN.preferenceNeuron()) {
					TWEANN.preferenceNeuronPanel = new DrawingPanel(Plot.BROWSE_DIM, Plot.BROWSE_DIM, "Preference Neuron Activation");
					TWEANN.preferenceNeuronPanel.setLocation(Plot.BROWSE_DIM + Plot.EDGE, Plot.BROWSE_DIM + Plot.TOP);
				}
				// this does not happen for TorusPredPreyTasks because the
				// "Individual Info" panel is unnecessary, as panels for each
				// evolved agents are already shown with monitorInputs with all
				// of their sensors and information
				if (CommonConstants.monitorInputs && !(MMNEAT.task instanceof TorusPredPreyTask) && !(MMNEAT.task instanceof Breve2DTask)) {
					Offspring.fillInputs((TWEANNGenotype) genotype);
				}
			}
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
						Files.copy(MMNEAT.evalReport.getFile().toPath(), Paths.get(MsPacManTask.saveFilePrefix
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
				System.out.println("Module Usage: " + Arrays.toString(((TWEANNGenotype) score.individual).getModuleUsage()));
				System.out.println("Fitness: " + score.toString());
				panel.dispose();
				
				// if (subPanels != null) {
				// for (int i = 0; i < subPanels.length; i++) {
				// subPanels[i].dispose();
				// }
				// subPanels = null;
				// }
			} if(cppnPanel != null) {
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
		boolean trackBestPacManScore = 
                                   CommonConstants.netio && this instanceof MsPacManTask
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
			Easy.save(bestPacMan, bestPacManDir + "/bestPacMan.xml");
			// System.out.println("Saved best Ms. Pac-Man agent with score of "
			// + maxPacManScore);
			FileUtilities.simpleFileWrite(bestPacManDir + "/score.txt", bestScoreSet.toString());
		}

		if (CommonConstants.netio) {
			PopulationUtil.saveBestOfCurrentGen(bestObjectives, bestGenotypes, bestScores);
		}

		if (parallel) {
			poolExecutor.shutdown();
		}

		/**
		 * If using UCB to decide who to give extra evals to, then by this point
		 * every member of the population will have been evaluated (preferably
		 * once). From here on, intelligent decisions need to be made about who
		 * to evaluate again.
		 */
		if (CommonConstants.ucb1Evaluation) {
			int evaluationBudget = Parameters.parameters.integerParameter("evaluationBudget");
			// Do an initial sort so the individual to evaluate will always
			// be at the end of the list
			int index = 0;
			double max = 0;
			for (Score<T> s : scores) {
				max = Math.max(max, s.scores[index]);
			}
			UCB1Comparator<T> ucb1 = new UCB1Comparator<T>(index, scores.size(), max);
			Collections.sort(scores, ucb1);
			int last = scores.size() - 1;
			// Perform the budgeted number of evals
			for (int i = 0; i < evaluationBudget; i++) {
				// Highest UCB is always at end, so evaluate it
				Score<T> oldScore = scores.get(last);
				// System.out.print(ucb1.ucb1(oldScore) + "::" + oldScore +
				// "->");
				EvaluationThread callable = new EvaluationThread(this, oldScore.individual);
				Score<T> newScore = oldScore.incrementalAverage(callable.call());
				// After eval, insert the individual into the correct slot in
				// sorted list
				ucb1.increaseTotal();
				// System.out.println(ucb1.ucb1(newScore) + "::" + newScore);
				ucb1.setMax(newScore.scores[index]);

				scores.set(last, newScore);
				Collections.sort(scores, ucb1);
			}
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
