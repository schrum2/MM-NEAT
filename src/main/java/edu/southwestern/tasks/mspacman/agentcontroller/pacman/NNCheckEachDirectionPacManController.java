package edu.southwestern.tasks.mspacman.agentcontroller.pacman;

import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.genotypes.TWEANNGenotype;
import edu.southwestern.evolution.lineage.Offspring;
import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.networks.Network;
import edu.southwestern.networks.TWEANN;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.mspacman.MsPacManTask;
import edu.southwestern.tasks.mspacman.data.ScentPath;
import edu.southwestern.tasks.mspacman.facades.GameFacade;
import edu.southwestern.tasks.mspacman.sensors.VariableDirectionBlockLoadedInputOutputMediator;
import edu.southwestern.tasks.mspacman.sensors.directional.VariableDirectionBlock;
import edu.southwestern.util.graphics.DrawingPanel;
import edu.southwestern.util.graphics.Plot;
import edu.southwestern.util.stats.StatisticsUtilities;
import oldpacman.Executor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Arrays;

/**
 * An extension of a directionalPacManController which checks each direction and
 * finds the preferred direction
 * 
 * @author Jacob Schrum
 */
public class NNCheckEachDirectionPacManController extends NNDirectionalPacManController {

	public static int[] totalChosenDirectionModeUsageCounts = null;
	public static int[] totalChosenDirectionJunctionModeUsageCounts = null;
	public static int[] totalChosenDirectionEdibleModeUsageCounts = null;
	public static int[] totalChosenDirectionJunctionEdibleModeUsageCounts = null;
	public static int[] totalChosenDirectionThreatModeUsageCounts = null;
	public static int[] totalChosenDirectionJunctionThreatModeUsageCounts = null;
	// Needed so each direction can have its own recurrent state
	private final Network[] directionalNetworks;
	// track when each network is actually used
	private final int[] usageCounts = new int[GameFacade.NUM_DIRS]; 
	// Mode usage when ghosts are edible
	private final int[][] edibleModeUsageCounts = new int[GameFacade.NUM_DIRS][]; 
	// Mode usage at junctions when ghosts are edible
	private final int[][] edibleJunctionModeUsageCounts = new int[GameFacade.NUM_DIRS][]; 
	// Mode usage when ghosts are threats
	private final int[][] threatModeUsageCounts = new int[GameFacade.NUM_DIRS][]; 
	// Mode usage at junctions when ghosts are threats
	private final int[][] threatJunctionModeUsageCounts = new int[GameFacade.NUM_DIRS][]; 
	// Mode usage at junctions
	private final int[][] junctionModeUsageCounts = new int[GameFacade.NUM_DIRS][]; 
	private final int[] chosenDirectionModeUsageCounts;
	private final int[] chosenJunctionDirectionModeUsageCounts;
	private final int[] chosenDirectionEdibleModeUsageCounts;
	private final int[] chosenDirectionJunctionEdibleModeUsageCounts;
	private final int[] chosenDirectionThreatModeUsageCounts;
	private final int[] chosenDirectionJunctionThreatModeUsageCounts;
	private int totalUsage = 0;
	private final boolean multitask;
	private static DrawingPanel[] panels = null;
	private final VariableDirectionBlock safe;
	private int scentMode = -1;
	private PrintStream modeFile = null;

	/**
	 * creates the controller based on genotype, the variable direction block,
	 * and various parameters
	 * 
	 * @param g,
	 *            the genotype
	 * @param safe,
	 *            the VariableDirectionBlock
	 */
	public NNCheckEachDirectionPacManController(Genotype<? extends Network> g, VariableDirectionBlock safe) {
		super(g.getPhenotype());
		this.safe = safe;
		directionalNetworks = new Network[GameFacade.NUM_DIRS];
		for (int i = 0; i < GameFacade.NUM_DIRS; i++) {
			directionalNetworks[i] = g.getPhenotype(); // Each is a different
														// copy
			edibleModeUsageCounts[i] = new int[directionalNetworks[i].numModules()];
			edibleJunctionModeUsageCounts[i] = new int[directionalNetworks[i].numModules()];
			threatModeUsageCounts[i] = new int[directionalNetworks[i].numModules()];
			threatJunctionModeUsageCounts[i] = new int[directionalNetworks[i].numModules()];
			junctionModeUsageCounts[i] = new int[directionalNetworks[i].numModules()];
		}
		chosenDirectionModeUsageCounts = new int[directionalNetworks[0].numModules()];
		chosenJunctionDirectionModeUsageCounts = new int[directionalNetworks[0].numModules()];
		chosenDirectionEdibleModeUsageCounts = new int[directionalNetworks[0].numModules()];
		chosenDirectionJunctionEdibleModeUsageCounts = new int[directionalNetworks[0].numModules()];
		chosenDirectionThreatModeUsageCounts = new int[directionalNetworks[0].numModules()];
		chosenDirectionJunctionThreatModeUsageCounts = new int[directionalNetworks[0].numModules()];
		multitask = directionalNetworks[0].isMultitask();
		
		if (CommonConstants.monitorInputs) {
			TWEANN.inputPanel.dispose();
			// Dispose of existing panels
			if (panels != null) {
				for (int i = 0; i < GameFacade.NUM_DIRS; i++) {
					panels[i].dispose();
				}
			}
			panels = new DrawingPanel[GameFacade.NUM_DIRS];
			for (int i = 0; i < GameFacade.NUM_DIRS; i++) {
				panels[i] = new DrawingPanel(Plot.BROWSE_DIM, (int) (Plot.BROWSE_DIM * 3.5), "Direction " + i);
				panels[i].setLocation(i * (Plot.BROWSE_DIM + 10), 0);
				Offspring.fillInputs(panels[i], (TWEANNGenotype) g);
			}
		}
		if (Parameters.parameters.booleanParameter("modePheremone")) {
			System.out.println("Set up scent path for modes");
			ScentPath.resetAll(directionalNetworks[0].numModules());
			this.scentMode = Parameters.parameters.integerParameter("scentMode");
			if (CommonConstants.recordPacman) {
				try {
					System.out.println("Create new mode file to record");
					modeFile = new PrintStream(new FileOutputStream(new File(MsPacManTask.saveFilePrefix
							+ Parameters.parameters.stringParameter("pacmanSaveFile") + ".modes")));
				} catch (FileNotFoundException ex) {
					System.out.println("Cannot track modes");
					System.exit(1);
				}
			}
		}
	}

	@Override
	/**
	 * finds the direction preferences and returns them in an array of doubles
	 * 
	 * @param gf,
	 *            the gameFacade
	 * @return preferences, an array of doubles for direction preferences
	 */
	public double[] getDirectionPreferences(GameFacade gf) {
		totalUsage++;
		double[] preferences = new double[GameFacade.NUM_DIRS];
		Arrays.fill(preferences, -1); // -1 is lowest possible value after
										// activation function scaling
		final int current = gf.getPacmanCurrentNodeIndex();
		final int[] neighbors = gf.neighbors(current);
		int mode = -1;
		if (multitask) {
			ms.giveGame(gf);
			mode = ms.mode();
		}
		for (int i = 0; i < neighbors.length; i++) {
			if (neighbors[i] != -1) {
				((VariableDirectionBlockLoadedInputOutputMediator) this.inputMediator).setDirection(i);
				double[] inputs = this.inputMediator.getInputs(gf, gf.getPacmanLastMoveMade());
				if (mode != -1) {
					this.directionalNetworks[i].chooseMode(mode);
				}
				if (panels != null) {
					TWEANN.inputPanel = panels[i];
				}
				usageCounts[i]++;
				double[] outputs = this.directionalNetworks[i].process(inputs);
				// Much of mode usage tracking for eval reports
				int lastMode = directionalNetworks[i].lastModule();
				if (gf.anyIsEdible()) {
					edibleModeUsageCounts[i][lastMode]++;
				} else {
					threatModeUsageCounts[i][lastMode]++;
				}
				if (gf.isJunction(current)) {
					junctionModeUsageCounts[i][lastMode]++;
					if (gf.anyIsEdible()) {
						edibleJunctionModeUsageCounts[i][lastMode]++;
					} else {
						threatJunctionModeUsageCounts[i][lastMode]++;
					}
				}
				// End eval tracking
				assert outputs.length == 1 : "Network should have a lone output for the utility of the move in the given direction";
				preferences[i] = outputs[0];
			} else {
				if (panels != null) {
					TWEANN.inputPanel = panels[i];
				}
				if (CommonConstants.checkEachFlushWalls) {
					this.directionalNetworks[i].flush(); // Nothing sensed from
															// wall
				}
			}
		}
		// Should unsafe directions be excluded?
		if (safe != null) {
			boolean anySafe = false;
			boolean[] safeDirections = new boolean[preferences.length];
			for (int i = 0; i < preferences.length; i++) {
				if (neighbors[i] != -1) {
					safe.setDirection(i);
					safeDirections[i] = safe.getValue(gf) > 0;
					anySafe = anySafe || safeDirections[i];
				}
			}
			// If any direction is safe, then only consider the safe ones.
			// Otherwise, consider all and pick the best of the bad.
			if (anySafe) {
				// System.out.println("Safe " + gf.getPacmanCurrentNodeIndex());
				for (int i = 0; i < safeDirections.length; i++) {
					if (!safeDirections[i]) {
						preferences[i] = -1;
					}
				}
			}
		}

		int chosenMode = directionalNetworks[directionFromPreferences(preferences)].lastModule();
		chosenDirectionModeUsageCounts[chosenMode]++;
		// System.out.println("Chose Mode: " + chosenMode);

		// Stop game for mode analysis, or add visualization
		if (CommonConstants.watch) {
			if (CommonConstants.stopMode == chosenMode) {
				System.out.println("Used mode " + chosenMode + " at time " + gf.getTotalTime());
				Executor.hold = true;
			}
			if (ScentPath.modeScents != null) {
				// Visit with 1 or 0 depending on whether scentMode is chosen
				if (scentMode == -1) {
					// for(int i = 0; i < ScentPath.modeScents.length; i++){
					// ScentPath.modeScents[i].visit(gf,
					// gf.getPacmanCurrentNodeIndex(), i == chosenMode ? 1.0 :
					// 0.0);
					// }
					ScentPath.modeScents[chosenMode].visit(gf, gf.getPacmanCurrentNodeIndex(), 1);
				} else {
					ScentPath.modeScents[scentMode].visit(gf, gf.getPacmanCurrentNodeIndex(),
							scentMode == chosenMode ? 1.0 : 0.0);
				}
				if (CommonConstants.recordPacman) {
					modeFile.println(chosenMode + "");
				}
			} else if (CommonConstants.recordPacman) {
				System.out.println("No scent path to record!");
				System.exit(1);
			}
		}

		if (gf.anyIsEdible()) {
			chosenDirectionEdibleModeUsageCounts[chosenMode]++;
		} else {
			chosenDirectionThreatModeUsageCounts[chosenMode]++;
		}
		if (gf.isJunction(current)) {
			chosenJunctionDirectionModeUsageCounts[chosenMode]++;
			if (gf.anyIsEdible()) {
				chosenDirectionJunctionEdibleModeUsageCounts[chosenMode]++;
			} else {
				chosenDirectionJunctionThreatModeUsageCounts[chosenMode]++;
			}
		}
		return preferences;
	}

	@Override
	/**
	 * resets all of the controller's various directional networks
	 */
	public void reset() {
		super.reset();
		for (int i = 0; i < GameFacade.NUM_DIRS; i++) {
			directionalNetworks[i].flush();
		}
	}

	@Override
	/**
	 * log various details about the controller in the evalReport
	 */
	public void logEvaluationDetails() {
		MMNEAT.evalReport.log("Network Info");
		MMNEAT.evalReport.log("\tNum Nodes: " + ((TWEANN) directionalNetworks[0]).nodes.size());
		MMNEAT.evalReport.log("\tNum Modes: " + ((TWEANN) directionalNetworks[0]).numModules());
		MMNEAT.evalReport.log("\tNum Outputs: " + ((TWEANN) directionalNetworks[0]).numOutputs());
		MMNEAT.evalReport.log("\tNeurons Per Mode: " + ((TWEANN) directionalNetworks[0]).neuronsPerModule());
		MMNEAT.evalReport.log("\tTime Steps: " + totalUsage);
		MMNEAT.evalReport
				.log("\tMode Usage For Chosen Direction Networks: " + Arrays.toString(chosenDirectionModeUsageCounts)
						+ ":" + Arrays.toString(StatisticsUtilities.distribution(chosenDirectionModeUsageCounts)));
		MMNEAT.evalReport.log("\tMode Usage At Junctions For Chosen Direction Networks: "
				+ Arrays.toString(chosenJunctionDirectionModeUsageCounts) + ":"
				+ Arrays.toString(StatisticsUtilities.distribution(chosenJunctionDirectionModeUsageCounts)));
		MMNEAT.evalReport.log("\tEdible Mode Usage For Chosen Direction Networks: "
				+ Arrays.toString(chosenDirectionEdibleModeUsageCounts) + ":"
				+ Arrays.toString(StatisticsUtilities.distribution(chosenDirectionEdibleModeUsageCounts)));
		MMNEAT.evalReport.log("\tThreat Mode Usage For Chosen Direction Networks: "
				+ Arrays.toString(chosenDirectionThreatModeUsageCounts) + ":"
				+ Arrays.toString(StatisticsUtilities.distribution(chosenDirectionThreatModeUsageCounts)));
		MMNEAT.evalReport.log("\tEdible Mode Usage At Junctions For Chosen Direction Networks: "
				+ Arrays.toString(chosenDirectionJunctionEdibleModeUsageCounts) + ":"
				+ Arrays.toString(StatisticsUtilities.distribution(chosenDirectionJunctionEdibleModeUsageCounts)));
		MMNEAT.evalReport.log("\tThreat Mode Usage At Junctions For Chosen Direction Networks: "
				+ Arrays.toString(chosenDirectionJunctionThreatModeUsageCounts) + ":"
				+ Arrays.toString(StatisticsUtilities.distribution(chosenDirectionJunctionThreatModeUsageCounts)));
		for (int i = 0; i < directionalNetworks.length; i++) {
			MMNEAT.evalReport.log("\t" + GameFacade.indexToMove(i) + " Network:");
			MMNEAT.evalReport.log("\t\tMode Usage: " + Arrays.toString(((TWEANN) directionalNetworks[i]).moduleUsage)
					+ ":"
					+ Arrays.toString(StatisticsUtilities.distribution(((TWEANN) directionalNetworks[i]).moduleUsage)));
			MMNEAT.evalReport.log("\t\tEdible Mode Usage: " + Arrays.toString(edibleModeUsageCounts[i]) + ":"
					+ Arrays.toString(StatisticsUtilities.distribution(edibleModeUsageCounts[i])));
			MMNEAT.evalReport.log("\t\tThreat Mode Usage: " + Arrays.toString(threatModeUsageCounts[i]) + ":"
					+ Arrays.toString(StatisticsUtilities.distribution(threatModeUsageCounts[i])));
			MMNEAT.evalReport.log("\t\tJunction Mode Usage: " + Arrays.toString(junctionModeUsageCounts[i]) + ":"
					+ Arrays.toString(StatisticsUtilities.distribution(junctionModeUsageCounts[i])));
			MMNEAT.evalReport.log(
					"\t\tEdible Mode Usage At Junctions: " + Arrays.toString(edibleJunctionModeUsageCounts[i]) + ":"
							+ Arrays.toString(StatisticsUtilities.distribution(edibleJunctionModeUsageCounts[i])));
			MMNEAT.evalReport.log(
					"\t\tThreat Mode Usage At Junctions: " + Arrays.toString(threatJunctionModeUsageCounts[i]) + ":"
							+ Arrays.toString(StatisticsUtilities.distribution(threatJunctionModeUsageCounts[i])));
			MMNEAT.evalReport.log("\t\tNetwork Usage: " + usageCounts[i] + ":" + ((1.0 * usageCounts[i]) / totalUsage));
			MMNEAT.evalReport.log("\t\tUnused Count: " + (totalUsage - usageCounts[i]));
		}
		MMNEAT.evalReport.log("");

		int modes = directionalNetworks[0].numModules();
		if (totalChosenDirectionModeUsageCounts == null) {
			totalChosenDirectionModeUsageCounts = new int[modes];
			totalChosenDirectionJunctionModeUsageCounts = new int[modes];
			totalChosenDirectionEdibleModeUsageCounts = new int[modes];
			totalChosenDirectionJunctionEdibleModeUsageCounts = new int[modes];
			totalChosenDirectionThreatModeUsageCounts = new int[modes];
			totalChosenDirectionJunctionThreatModeUsageCounts = new int[modes];
		}
		for (int i = 0; i < modes; i++) {
			totalChosenDirectionModeUsageCounts[i] += chosenDirectionModeUsageCounts[i];
			totalChosenDirectionJunctionModeUsageCounts[i] += chosenJunctionDirectionModeUsageCounts[i];
			totalChosenDirectionEdibleModeUsageCounts[i] += chosenDirectionEdibleModeUsageCounts[i];
			totalChosenDirectionJunctionEdibleModeUsageCounts[i] += chosenDirectionJunctionEdibleModeUsageCounts[i];
			totalChosenDirectionThreatModeUsageCounts[i] += chosenDirectionThreatModeUsageCounts[i];
			totalChosenDirectionJunctionThreatModeUsageCounts[i] += chosenDirectionJunctionThreatModeUsageCounts[i];
		}
	}
}
