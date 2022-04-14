package edu.southwestern.evolution.lineage;

import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.genotypes.MLPGenotype;
import edu.southwestern.evolution.genotypes.TWEANNGenotype;
import edu.southwestern.evolution.mulambda.MuLambda;
import edu.southwestern.evolution.selectiveBreeding.SelectiveBreedingEA;
import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.networks.MLP;
import edu.southwestern.networks.Network;
import edu.southwestern.networks.NetworkTask;
import edu.southwestern.networks.TWEANN;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.scores.Score;
import edu.southwestern.tasks.LonerTask;
import edu.southwestern.tasks.interactive.picbreeder.PicbreederTask;
import edu.southwestern.util.PopulationUtil;
import edu.southwestern.util.file.Serialization;
import edu.southwestern.util.graphics.DrawingPanel;
import edu.southwestern.util.graphics.GraphicsUtil;
import edu.southwestern.util.graphics.Plot;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This complicated, clunky file is used to browse the lineage of an evolved
 * population. Networks possessed by individuals can be viewed, and the genotype
 * can be evaluated on the spot. Fitness data is also displayed.
 *
 * @author Jacob Schrum
 * @commented Lauren Gillespie
 */
public class Offspring {

	/**
	 * Actual graphics object that displays offspring information
	 * Fills the screen with separate windows of information.
	 * 
	 * @author Jacob Schrum
	 */
	public static class NetworkBrowser extends KeyAdapter {

		//public variables
		public int objectiveOfInterest;
		public int secondObjectiveOfInterest;

		//private final global variables
		private final DrawingPanel panel;
		private final DrawingPanel left;
		private final DrawingPanel leftFitness;
		private final DrawingPanel leftFront;
		private final DrawingPanel right;
		private final DrawingPanel rightFitness;
		private final DrawingPanel rightFront;
		private final DrawingPanel fitness;
		private final DrawingPanel front;
		private final DrawingPanel[] bests;
		private final DrawingPanel leftInfo;
		private final DrawingPanel info;
		private final DrawingPanel rightInfo;

		//private global variables
		private boolean showInnovationNumbers;
		private boolean showScores;
		private boolean showIds;
		private boolean viewModePreference = false;
		private int jumpIndex;
		private int position;
		private int previousPosition;
		private int viewingGen;
		private ArrayList<JumpPoint> jumpInBest;
		private Genotype<? extends Network> g;
		private JumpPoint lastJumpPoint;

		/**
		 * NetworkBrowser constructor
		 * 
		 * @param panel current individuual to be evaluated
		 * @param left mother panel
		 * @param right father panel
		 * @param fitness drawing panel that contains fitness
		 * @param leftFitness fitness of mother
		 * @param rightFitness fitness of father
		 * @param front front panel
		 * @param leftFront left panel
		 * @param rightFront right panel
		 * @param bests best offspring panel array
		 * @param leftInfo info on mother 
		 * @param info info on individual
		 * @param rightInfo info on father
		 */
		private NetworkBrowser(DrawingPanel panel, DrawingPanel left, DrawingPanel right, DrawingPanel fitness,
				DrawingPanel leftFitness, DrawingPanel rightFitness, DrawingPanel front, DrawingPanel leftFront,
				DrawingPanel rightFront, DrawingPanel[] bests, DrawingPanel leftInfo, DrawingPanel info,
				DrawingPanel rightInfo) {
			this.position = 0;
			this.previousPosition = 0;
			this.panel = panel;
			this.objectiveOfInterest = 0;
			this.secondObjectiveOfInterest = 1;
			this.lastJumpPoint = null;
			this.showInnovationNumbers = false;
			this.showScores = true;
			this.showIds = false;
			this.viewingGen = 0;
			this.jumpInBest = jumpsInBest(0);
			this.jumpIndex = -1;
			this.left = left;
			this.right = right;
			this.fitness = fitness;
			this.leftFitness = leftFitness;
			this.rightFitness = rightFitness;
			this.front = front;
			this.leftFront = leftFront;
			this.rightFront = rightFront;
			this.bests = bests;
			this.leftInfo = leftInfo;
			this.info = info;
			this.rightInfo = rightInfo;
		}

		/**
		 * Draws the initial panels and information
		 */
		public void draw() {
			Offspring o = lineage.get(position);
			System.out.println(position);
			clear();
			if (o == null) {
				System.out.println("null");
			} else {
				viewingGen = o.generation;
				for (int i = 0; i < bests.length; i++) {
					plotBestsWorsts(bests[i], viewingGen, i, o.scores, 0);
				}
				if (MMNEAT.genotype instanceof TWEANNGenotype) {
					g = o.drawTWEANN(panel, showInnovationNumbers);
				} else if (o.xmlNetwork != null) {
					g = getGenotype(o.xmlNetwork);
				}
				//draws relevant info to panels
				System.out.println(g);
				fillInfo(o, info, g);
				System.out.println("Finished fill");
				displayScores(front, viewingGen, showScores, showIds, o.offspringId, objectiveOfInterest,
						secondObjectiveOfInterest);
				System.out.println("Displayed scores");
				displayFitness(fitness, o.scores.get(0));
				System.out.println("Displayed fitness");
				guardedDraw(o.parentId1, left, showInnovationNumbers, leftFitness, leftFront, showScores, showIds,
						leftInfo, objectiveOfInterest, secondObjectiveOfInterest);
				System.out.println("Displayed parent 1");
				guardedDraw(o.parentId2, right, showInnovationNumbers, rightFitness, rightFront, showScores, showIds,
						rightInfo, objectiveOfInterest, secondObjectiveOfInterest);
				System.out.println("Displayed parent 2");
			}
		}

		/**
		 * Clears drawing panels
		 * Shouldn't theoretically
		 * be necessary but still included
		 */
		public void clear() { 
			left.clear();
			leftFitness.clear();
			leftFront.clear();
			panel.clear();
			front.clear();
			right.clear();
			rightFitness.clear();
			rightFront.clear();
			fitness.clear();
			leftInfo.clear();
			info.clear();
			rightInfo.clear();
			for (int i = 0; i < bests.length; i++) {
				bests[i].clear();
			}
		}

		/**
		 * Main window detects key presses and responds to them. All navigation
		 * options are handled here.
		 * 
		 * @param e Event for the key pressed
		 */
		@Override
		public void keyPressed(KeyEvent e) {
			int key = e.getKeyCode();
			boolean redraw = false;

			// Get help
			if (key == KeyEvent.VK_H) {
				System.out.println("H: Get help");
				System.out.println("LEFT: Go backward one step in the lineage");
				System.out.println("RIGHT: Go forward one step in the lineage");
				System.out.println("UP: Advance to next generation");
				System.out.println("DOWN: Return to previous generation");
				System.out.println("E: Run evaluation");
				System.out.println("M: Goto parent 1 (mother) of current individual");
				System.out.println("F: Goto father 2 (father) of current individual");
				System.out.println("J: Make jump point be the biggest fitness jump in ancestry of current position");
				System.out.println("P: Jump to parent for current jump point");
				System.out.println("C: Jump to child for current jump point");
				System.out.println("L: Go to the previous/last network");
				System.out.println("0-5: Set focus objective");
				System.out.println("S: Save picture of network");
				System.out.println("I: Show/hide innovation numbers");
				System.out.println("N: Show/hide numerical scores on score plot");
				System.out.println("D: Show/hide id numbers on score plot");
				System.out.println("A: Show most recent common ancestor of current generation");
				System.out.println("T: Goto top performer of current objective in current generation");
				System.out.println("B: Show scores of ancestry backwards through generations");
				System.out.println("V: Toggle viewing of preference neuron preferences during evaluation");
				System.out.println(">: Advance through biggest fitness jump between gens");
				System.out.println("<: Reverse through biggest fitness jump between gens");
			}

			// Show scores of ancestry backwards through generations
			if (key == KeyEvent.VK_B) {
				Offspring o = lineage.get(position);
				if (o == null) {
					System.out.println("null has no parents");
				} else {
					plotLineageScores(o, bests);
				}
			}

			// Run evaluation
			if (key == KeyEvent.VK_E) {
				final Offspring o = lineage.get(position);
				if (o != null && o.xmlNetwork != null) {
					// Launch a new thread in which to evaluate the genotype
					new Thread() {
						@Override
						public void run() {
							// Evaluation currently only supports TWEANNs and MLPs
							if (MMNEAT.genotype instanceof TWEANNGenotype) {
								if (viewModePreference && TWEANN.preferenceNeuronPanel == null && TWEANN.preferenceNeuron()) {
									TWEANN.preferenceNeuronPanel = new DrawingPanel(Plot.BROWSE_DIM, Plot.BROWSE_DIM, "Preference Neuron Activation");
									TWEANN.preferenceNeuronPanel.setLocation(Plot.BROWSE_DIM + Plot.EDGE, Plot.BROWSE_DIM + Plot.TOP);
								}
								// Designates this as the active drawing panel
								o.drawTWEANN(panel, showInnovationNumbers); 
								if(MMNEAT.task instanceof PicbreederTask) {
									System.out.println("Draw CPPN image");
									int imageSize = 300;
									BufferedImage bi = GraphicsUtil.imageFromCPPN((Network) g.getPhenotype(), imageSize, imageSize);
									DrawingPanel cppnImage = GraphicsUtil.drawImage(bi, "CPPN Image", imageSize, imageSize);
									cppnImage.setLocation(Plot.BROWSE_DIM * 3, Plot.BROWSE_DIM);
									System.out.println("Done drawing image");
								} else {
									// Evaluate mechanism is mostly limited to Loner Tasks
									@SuppressWarnings("unchecked")
									Score<TWEANN> s = ((LonerTask<TWEANN>) MMNEAT.task).evaluate((TWEANNGenotype) g);
									int[] moduleUsage = ((TWEANNGenotype) s.individual).getModuleUsage();
									o.modeUsage = moduleUsage;
									System.out.println("Score: " + s);
									System.out.println("Module Usage: " + Arrays.toString(moduleUsage));
								}
							} else if (MMNEAT.genotype instanceof MLPGenotype) {
								// Evaluate mechanism is limited to Loner Tasks
								@SuppressWarnings("unchecked")
								Score<MLP> s = ((LonerTask<MLP>) MMNEAT.task).evaluate((MLPGenotype) g);
								System.out.println("Score: " + s);
							} else {
								System.out.println("Evaluation only available for TWEANNs and MLPs");
							}
						}
					}.start();
				} else {
					System.out.println("No network available to evaluate");
				}
			}

			// Show most recent common ancestor of current generation
			if (key == KeyEvent.VK_A) {
				Offspring a = mostRecentCommonAncestor(viewingGen);
				if (a == null) {
					System.out.println("Could not calculate ancestor for gen: " + viewingGen);
				} else {
					changePosition((int) a.offspringId);
					System.out.println("Ancestor of gen " + viewingGen + " is " + position + " in gen " + a.generation);
					redraw = true;
				}
			}

			// Go to top performer of current objective in current generation
			if (key == KeyEvent.VK_T) {
				Offspring b = bestOfGeneration(viewingGen, objectiveOfInterest);
				if (b == null) {
					System.out.println(
							"Could not calculate best in objective " + objectiveOfInterest + " for gen " + viewingGen);
				} else {
					changePosition((int) b.offspringId);
					System.out.println(
							"Best of obj " + objectiveOfInterest + " in gen " + viewingGen + " is " + position);
					redraw = true;
				}
			}

			// Save picture of network
			if (key == KeyEvent.VK_S) {
				Offspring o = lineage.get(position);
				if (o != null && o.xmlNetwork != null) {
					String filename = "NETWORK_" + o.offspringId + "_GEN_" + o.generation + ".jpg";
					panel.save(filename);
					System.out.println(filename + " saved");
				} else {
					System.out.println("No network available to save");
				}
			}

			// Show/hide innovation numbers
			if (key == KeyEvent.VK_I) {
				showInnovationNumbers = !showInnovationNumbers;
				System.out.println((showInnovationNumbers ? "Show" : "Hide") + " innovation numbers");
				redraw = true;
			}

			// Show/hide numerical scores on score plot
			if (key == KeyEvent.VK_N) {
				showScores = !showScores;
				System.out.println((showScores ? "Show" : "Hide") + " scores numbers");
				redraw = true;
			}

			// Show/hide id numbers on score plot
			if (key == KeyEvent.VK_D) {
				showIds = !showIds;
				System.out.println((showIds ? "Show" : "Hide") + " id numbers");
				redraw = true;
			}

			// Go to next generation
			if (key == KeyEvent.VK_UP) {
				changeGeneration(true);
				redraw = true;
			}

			// Go to previous generation
			if (key == KeyEvent.VK_DOWN) {
				changeGeneration(false);
				redraw = true;
			}

			// Next network in lineage
			if (key == KeyEvent.VK_RIGHT) {
				advancePosition();
				redraw = true;
			}

			// previous network in lineage
			if (key == KeyEvent.VK_LEFT) {
				decreasePosition();
				redraw = true;
			}

			// Toggle viewing of module preferences for preference neuron
			// approaches
			if (key == KeyEvent.VK_V) {
				viewModePreference = !viewModePreference;
			}

			// Make jump point be the biggest fitness jump in ancestry of
			// current position
			if (key == KeyEvent.VK_J) {
				if (lineage.get(position) == null) {
					System.out.println("Cannot calculate jump to null");
				} else {
					// Biggest fitness jump info
					lastJumpPoint = findBiggestFitnessJump(objectiveOfInterest, position);
					System.out.println(lastJumpPoint);
				}
			}

			// Jump to parent for current jump point
			if (key == KeyEvent.VK_P) {
				if (lastJumpPoint == null) {
					System.out.println("Calculate a valid jump point first");
				} else {
					// Goto parent/predecessor of last jump point
					changePosition((int) lastJumpPoint.comparisonId);
					System.out.println("Goto parent side of jump point");
					redraw = true;
				}
			}

			// Jump to child for current jump point
			if (key == KeyEvent.VK_C) {
				if (lastJumpPoint == null) {
					System.out.println("Calculate a valid jump point first");
				} else {
					// Goto child/successor of last jump point
					changePosition((int) lastJumpPoint.individual.offspringId);
					System.out.println("Goto child side of jump point");
					redraw = true;
				}
			}

			// Go to the previous/last network
			if (key == KeyEvent.VK_L) {
				changePosition(previousPosition);
				System.out.println("Goto previous network");
				redraw = true;
			}

			// Goto parent 1 (mother)
			if (key == KeyEvent.VK_M) {
				Offspring o = lineage.get(position);
				if (o == null) {
					System.out.println("null has no parents");
				} else {
					System.out.println("Goto parent 1 of " + o.offspringId);
					changePosition((int) o.parentId1);
					redraw = true;
				}
			}

			// Goto parent 2 (father)
			if (key == KeyEvent.VK_F) {
				Offspring o = lineage.get(position);
				if (o == null) {
					System.out.println("null has no parents");
				} else {
					System.out.println("Goto parent 2 of " + o.offspringId);
					changePosition((int) o.parentId2);
					redraw = true;
				}
			}

			// Proceed to next fitness jump
			if (key == KeyEvent.VK_PERIOD) { // The > key
				System.out.println("Proceed to next fitness jump");
				changeBigJump(true);
				redraw = true;
			}

			// Proceed to previous fitness jump
			if (key == KeyEvent.VK_COMMA) { // The < key
				System.out.println("Proceed to previous fitness jump");
				changeBigJump(false);
				redraw = true;
			}

			// Objective to focus on: Shouldn't go higher than 5
			if (key == KeyEvent.VK_0) {
				objectiveFocus(0);
				redraw = true;
			} else if (key == KeyEvent.VK_1) {
				objectiveFocus(1);
				redraw = true;
			} else if (key == KeyEvent.VK_2) {
				objectiveFocus(2);
				redraw = true;
			} else if (key == KeyEvent.VK_3) {
				objectiveFocus(3);
				redraw = true;
			} else if (key == KeyEvent.VK_4) {
				objectiveFocus(4);
				redraw = true;
			} else if (key == KeyEvent.VK_5) {
				objectiveFocus(5);
				redraw = true;
			}

			if (redraw) {
				draw();
			}
		}

		public void changeBigJump(boolean forward) {
			jumpIndex += (forward ? 1 : -1);
			if (jumpIndex >= jumpInBest.size()) {
				jumpIndex = 0;
			} else if (jumpIndex < 0) {
				jumpIndex = jumpInBest.size() - 1;
			}
			this.lastJumpPoint = this.jumpInBest.get(jumpIndex);
			System.out.println(this.lastJumpPoint);
		}

		public void changeGeneration(boolean up) {
			Offspring o = lineage.get(position);
			int prev = position;
			if (o == null) {
				while (o == null || o.xmlNetwork == null) {
					if (up) {
						advancePosition();
					} else {
						decreasePosition();
					}
					o = lineage.get(position);
				}
				System.out.println((up ? "Advancing" : "Decreasing") + " to non-null generation: " + o.generation);
			} else {
				int generation = o.generation;
				while (o == null || o.xmlNetwork == null || o.generation == generation) {
					if (up) {
						advancePosition();
					} else {
						decreasePosition();
					}
					o = lineage.get(position);
				}
				System.out.println(
						(up ? "Advancing" : "Decreasing") + " from generation: " + generation + " to " + o.generation);
			}
			previousPosition = prev;
		}

		public void changePosition(int newPos) {
			previousPosition = position;
			position = newPos;
		}

		public void advancePosition() {
			previousPosition = position;
			position++;
			if (position >= lineage.size()) {
				position = 0;
			}
		}

		public void decreasePosition() {
			previousPosition = position;
			position--;
			if (position < 0) {
				position = lineage.size() - 1;
			}
		}

		private void objectiveFocus(int x) {
			System.out.println("Focus on objective: " + x);
			secondObjectiveOfInterest = objectiveOfInterest;
			objectiveOfInterest = x;
			this.jumpInBest = jumpsInBest(x);
			this.jumpIndex = 0;
		}
	}

	/**
	 * 
	 * 
	 * Actual Offspring class begins here
	 * 
	 * 
	 * 
	 */

	//public static variables
	public static ArrayList<Offspring> lineage = new ArrayList<Offspring>();
	public static ArrayList<Double> maxes = new ArrayList<Double>();
	public static ArrayList<Double> mins = new ArrayList<Double>();
	public static double[][] bestScores = null;
	public static double[][] worstScores = null;
	public static double[][] tugGoals = null;
	public static int numObjectives = 0;

	//public global variables
	public long offspringId;
	public long parentId1;
	public long parentId2;
	public int generation;
	public String xmlNetwork;
	public ArrayList<ArrayList<Double>> scores = new ArrayList<ArrayList<Double>>();
	public ArrayList<Integer> correspondingGenerations = new ArrayList<Integer>();
	public ArrayList<String> mutations = new ArrayList<String>();
	public int[] modeUsage = null; // must run eval first

	/**
	 * resets all the necessary objects so class can be called multiple times 
	 */
	public static void  reset() { 
		lineage = new ArrayList<Offspring>();
		maxes = new ArrayList<Double>();
		mins = new ArrayList<Double>();
		bestScores = null;
		worstScores = null;
		tugGoals = null;
		numObjectives = 0;
	}
	/**
	 * Default constructor 
	 * @param offspringId ID of offspring 
	 * @param generation gen #
	 */
	public Offspring(long offspringId, int generation) {
		this(offspringId, -1, -1, generation);
	}

	/**
	 * Constructor 
	 * @param offspringId ID of offspring
	 * @param parentId1 ID of first parent 
	 * @param generation gen #
	 */
	public Offspring(long offspringId, long parentId1, int generation) {
		this(offspringId, parentId1, -1, generation);
	}

	/**
	 * Constructor 
	 * @param offspringId ID of offspring
	 * @param parentId1 ID of first parent 
	 * @param parentId2 ID of second parent
	 * @param generation gen #
	 */
	public Offspring(long offspringId, long parentId1, long parentId2, int generation) {
		this.offspringId = offspringId;
		this.parentId1 = parentId1;
		this.parentId2 = parentId2;
		this.generation = generation;
	}

	/**
	 * Equals method, overridden 
	 */
	@Override
	public boolean equals(Object o) {
		if (o instanceof Offspring) {
			Offspring other = (Offspring) o;
			return other.offspringId == offspringId && other.parentId1 == parentId1 && other.parentId2 == parentId2
					&& other.generation == generation && other.xmlNetwork.equals(xmlNetwork);
		}
		return false;
	}

	/**
	 * Returns a relevant hashCode for offspring object
	 */
	@Override
	public int hashCode() {
		int hash = 7;
		hash = 83 * hash + (int) (this.offspringId ^ (this.offspringId >>> 32));
		hash = 83 * hash + (int) (this.parentId1 ^ (this.parentId1 >>> 32));
		hash = 83 * hash + (int) (this.parentId2 ^ (this.parentId2 >>> 32));
		hash = 83 * hash + this.generation;
		hash = 83 * hash + (this.xmlNetwork != null ? this.xmlNetwork.hashCode() : 0);
		return hash;
	}

	/**
	 * returns offspring as a string
	 */
	@Override
	public String toString() {
		String result = "";
		result += offspringId + " <- " + parentId1;
		if (parentId2 > -1) {
			result += " X " + parentId2;
		}
		result += ": Gen " + generation + ": ";
		for (int i = 0; i < mutations.size(); i++) {
			String mut = mutations.get(i);
			result += mut + " ";
		}
		result += "\n\t";
		for (int i = 0; i < correspondingGenerations.size(); i++) {
			result += correspondingGenerations.get(i) + ":" + scores.get(i) + " ";
		}
		return result;
	}

	/**
	 * Adds name of mutated offspring
	 * @param name name of offspring
	 */
	public void addMutation(String name) {
		mutations.add(name);
	}

	/**
	 * Adds name of mutated offspring
	 * @param offspringId ID of offspring
	 * @param name name of offspring 
	 */
	public static void addMutation(long offspringId, String name) {
		lineage.get((int) offspringId).addMutation(name);
	}

	/**
	 * Adds scores from offspring lineage
	 * @param s scores
	 * @param generation gen #
	 */
	public void addScores(ArrayList<Double> s, int generation) {
		scores.add(s);
		correspondingGenerations.add(generation);
	}

	/**
	 * Adds scores from offspring lineage
	 * @param offspringId ID of offspring
	 * @param s scores
	 * @param generation gen #
	 */
	public static void addScores(long offspringId, ArrayList<Double> s, int generation) {

		Offspring o = lineage.get((int) offspringId);
		if (o == null) {
			o = new Offspring(offspringId, -1);
			addOffspring(o);
		}
		numObjectives = s.size();
		o.addScores(s, generation);
	}

	/**
	 * Adds network to display panels
	 * @param offspringId ID of network to add
	 * @param filePrefix prefix of xml file where network info is stored
	 * @param gen gen # 
	 * @param withinGen population slot within that generation
	 */
	public static void addNetwork(long offspringId, String filePrefix, int gen, int withinGen) {
		Offspring o = lineage.get((int) offspringId);
		int slash = filePrefix.lastIndexOf("/");
		String subdir = gen == 0 ? "initial" : "gen" + gen;
		o.xmlNetwork = filePrefix.substring(0, slash) + "/" + subdir + "/" + filePrefix.substring(slash + 1) + subdir + "_" + withinGen + ".xml";
	}

	/**
	 * Adds offspring if individual has any
	 * @param o individual in question
	 */
	public static void addOffspring(Offspring o) {
		while (lineage.size() <= o.offspringId) {
			lineage.add(null); // problematic?
		}
		int index = (int) o.offspringId;
		if (lineage.get(index) != null) {
			System.out.println("Already filled!");
			System.out.println("Index " + index);
			System.out.println("Contains: " + lineage.get(index));
			System.out.println("Replace : " + o);
			System.exit(1);
		}
		lineage.set(index, o);
	}



	/**
	 * Adds information about mutations done to offspring 
	 * @param filename name of file where mutation information stored
	 * @throws FileNotFoundException if mutation file cannot be found 
	 */
	public static void addMutationInformation(String filename) throws FileNotFoundException {
		Scanner s = new Scanner(new File(filename));
		@SuppressWarnings("unused")
		int generation = 0;
		while (s.hasNextLine()) {
			String next = s.nextLine();
			if (next.startsWith("--")) {
				generation++; // What is this used for?
			} else {
				Scanner pattern = new Scanner(next);
				long offspringId = pattern.nextLong();
				// System.out.print(offspringId + " ");
				while (pattern.hasNext()) {
					String mutation = pattern.next();
					// System.out.print(mutation + " ");
					addMutation(offspringId, mutation);
				}
				// System.out.println();
				pattern.close();
			}
		}
		s.close();
	}

	/**
	 * Adds goals if TUG implemented 
	 * @param tugLog log of TUG info
	 * @param numGenerations numGenerations passed 
	 * @throws FileNotFoundException if tugLog cannot be found
	 */
	private static void addGoals(File tugLog, int numGenerations) throws FileNotFoundException {
		tugGoals = new double[maxes.size()][numGenerations];
		Scanner file = new Scanner(tugLog);
		while (file.hasNextLine()) {
			String l = file.nextLine();
			Scanner line = new Scanner(l);
			int gen = line.nextInt(); // generation
			// For each objective
			for (int i = 0; i < tugGoals.length; i++) {
				line.nextDouble(); // Drop whether objective on/off
				line.nextDouble(); // Drop avg score
				line.nextDouble(); // Drop best score
				line.nextDouble(); // Drop rwa
				tugGoals[i][gen] = line.nextDouble();
			}
			line.close();
		}
		file.close();
	}

	/**
	 * Add all scores from offspring 
	 * @param filePrefix prefix of file where score stored
	 * @param infix more information needed for file finding
	 * @param numGenerations number of generations passed
	 * @param associateNetworks whether or not to add parent netwosk as well
	 * @param networkPrefix prefix to add network under
	 * @throws FileNotFoundException if score file cannot be found
	 */
	public static void addAllScores(String filePrefix, String infix, int numGenerations, boolean associateNetworks, String networkPrefix) throws FileNotFoundException {
		for (int i = 0; i < numGenerations; i++) {
			String filename = filePrefix + infix + i + ".txt";
			Scanner s = new Scanner(new File(filename));
			while (s.hasNextLine()) {
				Scanner line = new Scanner(s.nextLine());
				int withinGen = line.nextInt();
				long offspringId = line.nextLong();
				ArrayList<Double> scores = new ArrayList<Double>();
				int scoreIndex = 0;
				while (line.hasNext()) {
					double x = line.nextDouble();
					if (scoreIndex >= maxes.size()) {
						maxes.add(-Double.MAX_VALUE);
						mins.add(Double.MAX_VALUE);
					}
					maxes.set(scoreIndex, Math.max(maxes.get(scoreIndex), x));
					mins.set(scoreIndex, Math.min(mins.get(scoreIndex), x));
					scores.add(x);
					scoreIndex++;
				}
				line.close();

				if (bestScores == null) {
					bestScores = new double[maxes.size()][numGenerations];
					worstScores = new double[maxes.size()][numGenerations];
					for (int j = 0; j < bestScores.length; j++) {
						Arrays.fill(bestScores[j], -Double.MAX_VALUE);
						Arrays.fill(worstScores[j], Double.MAX_VALUE);
					}
				}

				for (int j = 0; j < bestScores.length; j++) {
					bestScores[j][i] = Math.max(bestScores[j][i], scores.get(j));
					worstScores[j][i] = Math.min(worstScores[j][i], scores.get(j));
				}

				addScores(offspringId, scores, i);
				if (associateNetworks) {
					addNetwork(offspringId, networkPrefix, i, withinGen);
				}
			}
			s.close();
		}
	}

	/**
	 * 
	 * @param base
	 * @param saveTo
	 * @param run
	 * @param log
	 * @param loadFrom
	 * @param includeChildren
	 * @throws FileNotFoundException
	 * 
		fillInLineage("asexual", "DetDLMMR", 2, "Asexual-DetDLMMR", "DetDLMMR", true);
	 */
	public static void fillInLineage(String base, String saveTo, int run, String log, String loadFrom, boolean includeChildren) throws FileNotFoundException {
		Parameters.parameters.setBoolean("erasePWTrails", false);
		Parameters.parameters.setBoolean("watch", true);
		CommonConstants.watch = true;
		Parameters.parameters.setInteger("trials", 1);
		CommonConstants.trials = 1;
		MMNEAT.loadClasses();

		String prefix = base + "/" + saveTo + run + "/" + log + run + "_";
		String originalPrefix = base + "/" + loadFrom + run + "/" + log.replace(saveTo, loadFrom) + run + "_";
		System.out.println("Prefix: " + prefix);
		// Parameters.initializeParameterCollections(prefix + "parameters.txt");
		int numGenerations = PopulationUtil.loadLineage();
		System.out.println("---Lineage Loaded (" + numGenerations + " generations)-----------");
		addMutationInformation(originalPrefix + "Mutations_log.txt");
		System.out.println("---Mutation Information Added-----------");
		if(MMNEAT.ea instanceof MuLambda) { // Only MuLambda scheme has separate child pop
			if (includeChildren) {
				addAllScores(prefix, "child_gen", numGenerations, false, originalPrefix);
				System.out.println("---Child Scores Added-----------");
			}
		}
		addAllScores(prefix, "parents_gen", numGenerations, true, originalPrefix);
		System.out.println("---Parent Scores Added-----------");
		// Add TUG Goals?
		File tugLog = new File(prefix + "TUG_log.txt");
		if (tugLog.exists()) {
			addGoals(tugLog, numGenerations);
			System.out.println("---TUG Goals Added-----------");
		}
	}

	/**
	 * 
	 * @param generation
	 * @param parentId
	 * @param offspringId
	 * @return
	 */
	public static ArrayList<Double> fitnessDifference(int generation, long parentId, long offspringId) {
		Offspring parent = lineage.get((int) parentId);
		Offspring child = lineage.get((int) offspringId);
		int genIndex = parent.correspondingGenerations.indexOf(generation);
		if (genIndex == -1) {
			System.out.println("FAILURE!");
			System.out.println("gen: " + generation + ", parentId: " + parentId + ", offspringId: " + offspringId);
			System.out.println("parent: " + parent);
			System.out.println("child: " + child);
			System.exit(1);
		}
		ArrayList<Double> parentScores = parent.scores.get(genIndex);
		genIndex = child.correspondingGenerations.indexOf(generation);
		ArrayList<Double> childScores = child.scores.get(genIndex);
		ArrayList<Double> differences = new ArrayList<Double>();
		for (int i = 0; i < parentScores.size(); i++) {
			differences.add(childScores.get(i) - parentScores.get(i));
		}
		return differences;
	}

	/**
	 * 
	 * @param offspringId
	 * @return
	 */
	public static MutationBranch completeMutationHistory(long offspringId) {
		return completeMutationHistory(offspringId, new HashMap<Long, MutationBranch>());
	}

	/**
	 * 
	 * @param offspringId
	 * @param visited
	 * @return
	 */
	public static MutationBranch completeMutationHistory(long offspringId, HashMap<Long, MutationBranch> visited) {
		// System.out.println("completeMutationHistory("+offspringId+")");
		if (offspringId == -1) {
			return null;
		}
		if (visited.containsKey(offspringId)) {
			return visited.get(offspringId);
		}
		Offspring o = lineage.get((int) offspringId);
		long parent1 = o.parentId1;
		long parent2 = o.parentId2;
		MutationBranch left = completeMutationHistory(parent1, visited);
		MutationBranch right = parent2 == -1 ? null : completeMutationHistory(parent2, visited);
		MutationBranch branch = new MutationBranch(o, left, right);
		visited.put(offspringId, branch);
		return branch;
	}

	/**
	 * 
	 * @param objective
	 * @param endpointId
	 * @return
	 */
	public static edu.southwestern.evolution.lineage.JumpPoint findBiggestFitnessJump(int objective, long endpointId) {
		HashMap<Long, MutationBranch> map = new HashMap<Long, MutationBranch>();
		completeMutationHistory(endpointId, map);
		double jump = 0;
		Offspring individual = null;
		int whenGen = 0;
		boolean firstParent = true;
		for (MutationBranch mb : map.values()) {
			Offspring o = mb.o;
			int gen = o.correspondingGenerations.get(0);
			boolean first = true;
			if (o.parentId1 > -1) {
				ArrayList<Double> diffs = fitnessDifference(gen, o.parentId1, o.offspringId);
				double diff = diffs.get(objective);
				if (o.parentId2 > -1) {
					ArrayList<Double> diffs2 = fitnessDifference(gen, o.parentId2, o.offspringId);
					double diff2 = diffs2.get(objective);
					// Only take the worse level of improvement
					if (diff2 < diff) {
						first = false;
						diff = diff2;
					}
				}
				if (diff > jump) {
					jump = diff;
					individual = o;
					whenGen = gen;
					firstParent = first;
				}
			}
		}
		return new edu.southwestern.evolution.lineage.JumpPoint(objective, jump, individual, whenGen, firstParent);
	}

	/**
	 * 
	 * @return
	 */
	public static int lastGeneration() {
		return lineage.get(lineage.size() - 1).generation;
	}

	/**
	 * 
	 * @param objectives
	 * @return
	 */
	public static ArrayList<edu.southwestern.evolution.lineage.JumpPoint> biggestJumpsToReachFinalPopulation(int objectives) {
		ArrayList<edu.southwestern.evolution.lineage.JumpPoint> jumps = new ArrayList<edu.southwestern.evolution.lineage.JumpPoint>();
		ArrayList<Offspring> os = offspringOfGeneration(lastGeneration());
		for (int i = 0; i < os.size(); i++) {
			for (int j = 0; j < objectives; j++) {
				Offspring o = os.get(i);
				edu.southwestern.evolution.lineage.JumpPoint jump = findBiggestFitnessJump(j, o.offspringId);
				if (!jumps.contains(jump)) {
					jumps.add(jump);
				}
			}
		}
		return jumps;
	}

	/**
	 * 
	 * @param offspringId
	 * @return
	 */
	public static HashMap<Long, Offspring> allAncestors(long offspringId) {
		HashMap<Long, MutationBranch> map = new HashMap<Long, MutationBranch>();
		completeMutationHistory(offspringId, map);
		HashMap<Long, Offspring> ancestors = new HashMap<Long, Offspring>();
		for (MutationBranch mb : map.values()) {
			ancestors.put(mb.o.offspringId, mb.o);
		}
		return ancestors;
	}

	/**
	 * 
	 * @param offspringId1
	 * @param offspringId2
	 * @return
	 */
	public static Offspring mostRecentCommonAncestor(long offspringId1, long offspringId2) {
		HashMap<Long, Offspring> a1 = allAncestors(offspringId1);
		HashMap<Long, Offspring> a2 = allAncestors(offspringId2);
		int recentGen = 0;
		Offspring result = null;
		for (Offspring o1 : a1.values()) {
			for (Offspring o2 : a2.values()) {
				if (o1.equals(o2) && o1.generation > recentGen) {
					recentGen = o1.generation;
					result = o1;
				}
			}
		}
		return result;
	}

	/**
	 * Most recent ancestor of set of offspring. Needs to contain at least 2
	 *
	 * @param os
	 * @return
	 */
	public static Offspring mostRecentCommonAncestor(ArrayList<Offspring> os) {
		long offspringId1 = os.get(0).offspringId;
		long offspringId2 = os.get(1).offspringId;
		Offspring common = mostRecentCommonAncestor(offspringId1, offspringId2);
		for (int i = 2; i < os.size(); i++) {
			if (common == null) {
				return null;
			}
			Offspring next = os.get(i);
			common = mostRecentCommonAncestor(common.offspringId, next.offspringId);
		}
		return common;
	}

	/**
	 * 
	 * @param generation
	 * @return
	 */
	public static Offspring mostRecentCommonAncestor(int generation) {
		ArrayList<Offspring> os = offspringOfGeneration(generation);
		return mostRecentCommonAncestor(os);
	}

	/**
	 * 
	 * @param generation
	 * @return
	 */
	public static ArrayList<Offspring> offspringOfGeneration(int generation) {
		ArrayList<Offspring> os = new ArrayList<Offspring>();
		for (int i = 0; i < lineage.size(); i++) {
			Offspring next = lineage.get(i);
			if (next != null && next.generation == generation) {
				os.add(next);
			}
		}
		return os;
	}

	/**
	 * 
	 * @param generation
	 * @param objective
	 * @return
	 */
	public static Offspring bestOfGeneration(int generation, int objective) {
		ArrayList<Offspring> os = offspringOfGeneration(generation);
		Offspring best = null;
		double max = -Double.MAX_VALUE;
		for (Offspring o : os) {
			int index = o.correspondingGenerations.indexOf(generation);
			if (index == -1) {
				System.out.println("generation: " + generation);
				System.out.println("Problem: " + o);
				System.out.println("correspondingGenerations: " + o.correspondingGenerations);
				System.out.println("correspondingGenerations: " + o.scores);

				continue;
			}
			double score = o.scores.get(index).get(objective);
			if (score > max) {
				max = score;
				best = o;
			}
		}
		return best;
	}

	/**
	 * 
	 * @param objective
	 * @return
	 */
	public static ArrayList<edu.southwestern.evolution.lineage.JumpPoint> jumpsInBest(int objective) {
		ArrayList<edu.southwestern.evolution.lineage.JumpPoint> jumps = new ArrayList<edu.southwestern.evolution.lineage.JumpPoint>();
		// Selective breeding does not produce interesting scores
		if(!(MMNEAT.ea instanceof SelectiveBreedingEA)) {
			int generations = lastGeneration() + 1;
			Offspring previousBest = bestOfGeneration(0, objective);
			double previousScore = mins.get(objective);
			if (previousBest != null) {
				previousScore = previousBest.scores.get(previousBest.correspondingGenerations.indexOf(0)).get(objective);
			}
			for (int i = 1; i < generations; i++) {
				Offspring currentBest = bestOfGeneration(i, objective);
				if (currentBest != null) {
					double currentScore = currentBest.scores.get(currentBest.correspondingGenerations.indexOf(i))
							.get(objective);
					double diff = currentScore - previousScore;
					edu.southwestern.evolution.lineage.JumpPoint jump = new edu.southwestern.evolution.lineage.JumpPoint(objective, diff, currentBest, i, previousBest.offspringId);
					jumps.add(jump);
					previousBest = currentBest;
					previousScore = currentScore;
				}
			}
		}
		return jumps;
	}

	/**
	 * 
	 * @param objective
	 * @return
	 */
	public static edu.southwestern.evolution.lineage.JumpPoint biggestJumpInBest(int objective) {
		ArrayList<edu.southwestern.evolution.lineage.JumpPoint> jumps = jumpsInBest(objective);
		edu.southwestern.evolution.lineage.JumpPoint best = null;
		double biggest = -Double.MAX_VALUE;
		for (edu.southwestern.evolution.lineage.JumpPoint j : jumps) {
			if (j.jump > biggest) {
				best = j;
				biggest = j.jump;
			}
		}
		return best;
	}

	/**
	 * 
	 * @param generation
	 * @param objective
	 * @return
	 */
	public static int numberOfImprovedOffspring(int generation, int objective) {
		Offspring o = bestOfGeneration(generation - 1, objective);
		double oldBest = o.scores.get(o.correspondingGenerations.indexOf(generation - 1)).get(objective);
		ArrayList<Offspring> current = offspringOfGeneration(generation);
		int count = 0;
		for (Offspring candidate : current) {
			int index = candidate.correspondingGenerations.indexOf(generation);
			if (candidate.scores.get(index).get(objective) > oldBest) {
				count++;
			}
		}
		return count;
	}

	/**
	 * 
	 * @param objective
	 * @return
	 */
	public static ArrayList<Integer> numberOfImprovedOffspringByGeneration(int objective) {
		int lastGen = lastGeneration();
		ArrayList<Integer> result = new ArrayList<Integer>();
		for (int i = 1; i < lastGen; i++) {
			result.add(numberOfImprovedOffspring(i, objective));
		}
		return result;
	}

	/**
	 * 
	 * @param panel
	 * @param showInnovationNumbers
	 * @return
	 */
	public TWEANNGenotype drawTWEANN(DrawingPanel panel, boolean showInnovationNumbers) {
		System.out.println(xmlNetwork);
		System.out.println(this);
		TWEANNGenotype g = null;
		if (xmlNetwork == null || !(new File(xmlNetwork).exists())) {
			System.out.println("Unsaved network");
		} else {
			String oldTitle = panel.getFrame().getTitle();
			int end = oldTitle.contains(":") ? oldTitle.indexOf(":") : oldTitle.length();
			panel.setTitle(oldTitle.substring(0, end) + ": " + offspringId);
			g = viewTWEANN(panel, xmlNetwork, showInnovationNumbers);
		}
		return g;
	}

	/**
	 * 
	 * @param xml
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Genotype<? extends Network> getGenotype(String xml) {
		return (Genotype<? extends Network>) Serialization.load(xml);
	}

	/**
	 * 
	 * @param panel
	 * @param xml
	 * @param showInnovationNumbers
	 * @return
	 */
	public static TWEANNGenotype viewTWEANN(DrawingPanel panel, String xml, boolean showInnovationNumbers) {
		TWEANNGenotype g = (TWEANNGenotype) getGenotype(xml);
		g.getPhenotype().draw(panel, showInnovationNumbers);
		return g;
	}

	/**
	 * 
	 * @param panel
	 * @param scores
	 */
	public static void displayFitness(DrawingPanel panel, ArrayList<Double> scores) {
		Graphics g = panel.getGraphics();
		for (int i = 0; i < scores.size(); i++) {
			g.setColor(Color.red);
			int x = Plot.OFFSET;
			int y = Plot.OFFSET + (2 * i * Plot.OFFSET);
			g.fillRect(x, y, (int) (((scores.get(i) - mins.get(i)) / (maxes.get(i) - mins.get(i))) * (Plot.BROWSE_DIM - (2 * Plot.OFFSET))), Plot.OFFSET);
			g.setColor(Color.black);
			g.drawString("" + mins.get(i), 0, y);
			g.drawString("" + maxes.get(i), Plot.BROWSE_DIM - (Plot.OFFSET + Plot.OFFSET / 2), y);
			g.drawString("" + scores.get(i), x, y + Plot.OFFSET);
		}
	}

	/**
	 * 
	 * @param scores
	 * @param viewingGen
	 * @param label
	 * @param id
	 * @param currentId
	 * @param obj1
	 * @param obj2
	 */
	public static void displayScores(DrawingPanel scores, int viewingGen, boolean label, boolean id, long currentId,
			int obj1, int obj2) {
		int browseDim = Plot.BROWSE_DIM;
		int offset = Plot.OFFSET;
		int ovalDim = Plot.OVAL_DIM;

		boolean singleObjective = maxes.size() == 1;

		String oldTitle = scores.getFrame().getTitle();
		int end = oldTitle.contains(":") ? oldTitle.indexOf(":") : oldTitle.length();
		scores.setTitle(oldTitle.substring(0, end) + ": Gen " + viewingGen + " objs " + obj1 + "," + obj2);

		Graphics g = scores.getGraphics();
		g.setColor(Color.black);
		g.drawLine(Plot.OFFSET, Plot.OFFSET, Plot.OFFSET, Plot.BROWSE_DIM - Plot.OFFSET);
		g.drawLine(Plot.OFFSET, Plot.BROWSE_DIM - Plot.OFFSET, Plot.BROWSE_DIM - Plot.OFFSET, Plot.BROWSE_DIM - Plot.OFFSET);

		if (tugGoals != null) {
			double goal1 = tugGoals[obj1][viewingGen];
			double goal2 = tugGoals[obj2][viewingGen];

			g.setColor(Color.gray);
			g.drawLine(Plot.OFFSET + scale(goal1, obj1), Plot.OFFSET, Plot.OFFSET + scale(goal1, obj1), Plot.BROWSE_DIM - Plot.OFFSET);
			g.drawLine(Plot.OFFSET, Plot.OFFSET + invert(goal2, obj2), Plot.BROWSE_DIM - Plot.OFFSET, Plot.OFFSET + invert(goal2, obj2));
		}

		g.setColor(Color.black);
		g.drawString("" + maxes.get(obj1), Plot.BROWSE_DIM - Plot.OFFSET, Plot.BROWSE_DIM - Plot.OFFSET / 2);
		if (!singleObjective) {
			g.drawString("" + maxes.get(obj2), 0, offset / 2);
			g.drawString(mins.get(obj2) + "," + mins.get(obj1), 0, browseDim - offset / 2);
		} else {
			g.drawString("" + mins.get(obj1), 0, browseDim - offset / 2);
		}

		Offspring child = lineage.get((int) currentId);
		long parentId1 = child.parentId1;
		long parentId2 = child.parentId2;
		for (int i = 0; i < lineage.size(); i++) {
			Offspring o = lineage.get(i);
			if (o != null) {
				if (o.offspringId == currentId) {
					// All scores of given solution
					g.setColor(Color.CYAN);
					for (int j = 0; j < o.correspondingGenerations.size(); j++) {
						ArrayList<Double> s = o.scores.get(j);
						int x = offset + scale(s.get(obj1), obj1) - (ovalDim / 2);
						int y = singleObjective ? browseDim / 2 : offset + invert(s.get(obj2), obj2) - (ovalDim / 2);
						g.drawOval(x, y, ovalDim, ovalDim);
					}
				}
				for (int j = 0; j < o.correspondingGenerations.size(); j++) {
					Integer gen = o.correspondingGenerations.get(j);
					if (gen == viewingGen) {
						ArrayList<Double> s = o.scores.get(j);
						int x = offset + scale(s.get(obj1), obj1) - (ovalDim / 2);
						int y = singleObjective ? browseDim / 2 : offset + invert(s.get(obj2), obj2) - (ovalDim / 2);
						// System.out.println("("+s.get(0)+","+s.get(1)+")->("+x+","+y+")");
						if (o.offspringId == currentId) {
							g.setColor(Color.GREEN);
							g.fillRect(x - (ovalDim / 2), y - (ovalDim / 2), 2 * ovalDim, 2 * ovalDim);
						} else if (o.offspringId == parentId1) {
							g.setColor(Color.BLUE);
							g.fillRect(x - (ovalDim / 2), y - (ovalDim / 2), 2 * ovalDim, 2 * ovalDim);
						} else if (o.offspringId == parentId2) {
							g.setColor(Color.ORANGE);
							g.fillRect(x - (ovalDim / 2), y - (ovalDim / 2), 2 * ovalDim, 2 * ovalDim);
						} else {
							g.setColor(Color.red);
							g.drawOval(x, y, ovalDim, ovalDim);
						}
						if (label) {
							g.setColor(Color.black);
							g.drawString(s.toString(), x, y);
						}
						if (id) {
							g.setColor(Color.blue);
							g.drawString("" + o.offspringId, x, y + ovalDim);
						}
					}
				}
			}
		}
	}

	/**
	 * 
	 * @param x
	 * @param index
	 * @return
	 */
	private static int scale(double x, int index) {
		return GraphicsUtil.scale(x, maxes.get(index) - mins.get(index), mins.get(index));
	}

	/**
	 * 
	 * @param y
	 * @param index
	 * @return
	 */
	private static int invert(double y, int index) {
		return (Plot.BROWSE_DIM - (2 * Plot.OFFSET)) - scale(y, index);
	}

	/**
	 * 
	 */
	public static void browse() {
		System.out.println("Browse");
		int fitnessHeight = (maxes.size() + 3) * Plot.OFFSET;
		int height = Plot.BROWSE_DIM + Plot.TOP;

		int browseDim = Plot.BROWSE_DIM;
		int edge = Plot.EDGE;
		int top = Plot.TOP;
		DrawingPanel left = new DrawingPanel(browseDim, browseDim, "Parent 1");
		DrawingPanel leftFitness = new DrawingPanel(browseDim, fitnessHeight, "Parent 1 Fitness");
		leftFitness.setLocation(0, height);
		DrawingPanel leftFront = new DrawingPanel(browseDim, browseDim, "Objective scores");
		leftFront.setLocation(0, height + fitnessHeight + top);
		DrawingPanel leftInfo = new DrawingPanel(browseDim, browseDim, "Parent 1 Info");
		leftInfo.setLocation(0, height + fitnessHeight + top + height);

		DrawingPanel panel = new DrawingPanel(browseDim, browseDim, "Loaded Network");
		panel.setLocation(browseDim + edge, 0);
		DrawingPanel fitness = new DrawingPanel(browseDim, fitnessHeight, "Fitness");
		fitness.setLocation(browseDim + edge, height);
		DrawingPanel front = new DrawingPanel(browseDim, browseDim, "Objective scores");
		front.setLocation(browseDim + edge, height + fitnessHeight + top);
		DrawingPanel info = new DrawingPanel(browseDim, (int) (browseDim * 3.5), "Individual Info");
		info.setLocation(browseDim + edge, height + fitnessHeight + top + height);

		DrawingPanel right = new DrawingPanel(browseDim, browseDim, "Parent 2");
		right.setLocation(2 * (edge + browseDim), 0);
		DrawingPanel rightFitness = new DrawingPanel(browseDim, fitnessHeight, "Parent 2 Fitness");
		rightFitness.setLocation(2 * (edge + browseDim), height);
		DrawingPanel rightFront = new DrawingPanel(browseDim, browseDim, "Objective scores");
		rightFront.setLocation(2 * (edge + browseDim), height + fitnessHeight + top);
		DrawingPanel rightInfo = new DrawingPanel(browseDim, browseDim, "Parent 2 Info");
		rightInfo.setLocation(2 * (edge + browseDim), height + fitnessHeight + top + height);

		DrawingPanel[] bests = new DrawingPanel[maxes.size()];
		for (int i = 0; i < bests.length; i++) {
			bests[i] = new DrawingPanel(browseDim, browseDim, "Best and Worst in Objective " + i);
			bests[i].setLocation(3 * (edge + browseDim), i * height);
		}

		panel.getFrame().addKeyListener(new NetworkBrowser(panel, left, right, fitness, leftFitness, rightFitness,
				front, leftFront, rightFront, bests, leftInfo, info, rightInfo));

		while (true){ // Keep console open 
			try {
				Thread.sleep(100);
			} catch (InterruptedException ex) {
				Logger.getLogger(Offspring.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

	/**
	 * 
	 * @param offspringId
	 * @param panel
	 * @param showInnovationNumbers
	 * @param fitness
	 * @param front
	 * @param showScores
	 * @param showIds
	 * @param info
	 * @param obj1
	 * @param obj2
	 */
	public static void guardedDraw(long offspringId, DrawingPanel panel, boolean showInnovationNumbers,
			DrawingPanel fitness, DrawingPanel front, boolean showScores, boolean showIds, DrawingPanel info, int obj1,
			int obj2) {
		if (offspringId != -1) {
			Offspring o = lineage.get((int) offspringId);
			fillInfo(o, info);
			if (o != null) {
				if (MMNEAT.genotype instanceof TWEANNGenotype) {
					o.drawTWEANN(panel, showInnovationNumbers);
				}
				displayFitness(fitness, o.scores.get(0));
				displayScores(front, o.generation, showScores, showIds, offspringId, obj1, obj2);
			}
		}
	}

	/**
	 * 
	 * @param o
	 * @param info
	 */
	public static void fillInfo(Offspring o, DrawingPanel info) {
		fillInfo(o, info, null);
	}

	/**
	 * 
	 * @param o
	 * @param info
	 * @param ng
	 */
	public static void fillInfo(Offspring o, DrawingPanel info, Genotype<? extends Network> ng) {
		int offset = Plot.OFFSET;

		Graphics g = info.getGraphics();
		g.setColor(Color.black);
		g.drawString("File: " + o.xmlNetwork, offset, offset);
		g.drawString("Id: " + o.offspringId, offset, 2 * offset);
		g.drawString("Parent 1 Id: " + o.parentId1, offset, 3 * offset);
		g.drawString("Parent 2 Id: " + o.parentId2, offset, 4 * offset);
		g.drawString("Gen: " + o.generation, offset, 5 * offset);
		g.drawString("Mutations: " + o.mutations, offset, 6 * offset);
		g.drawString("Mode Usage: " + Arrays.toString(o.modeUsage), offset, 7 * offset);
		g.drawString("Scores: " + o.scores, offset, 8 * offset);

		fillInputs(info, ng, 9);
	}

	/**
	 * 
	 * @param ng
	 */
	public static void fillInputs(Genotype<? extends Network> ng) {
		DrawingPanel info;
		if (TWEANN.inputPanel == null) {
			info = new DrawingPanel(Plot.BROWSE_DIM, (int) (Plot.BROWSE_DIM * 3.5), "Individual Info");
		} else {
			info = TWEANN.inputPanel;
			info.clear();
		}
		fillInputs(info, ng);
	}

	/**
	 * 
	 * @param info
	 * @param ng
	 */
	public static void fillInputs(DrawingPanel info, Genotype<? extends Network> ng) {
		fillInputs(info, ng, 1);
	}
	//
	public static int inputOffset = 9;

	/**
	 * 
	 * @param info
	 * @param ng
	 * @param startY
	 */
	public static void fillInputs(DrawingPanel info, Genotype<? extends Network> ng, int startY) {
		int offset = Plot.OFFSET;
		inputOffset = startY;
		Graphics g = info.getGraphics();
		TWEANNGenotype geno = null;
		if (ng instanceof TWEANNGenotype) {
			geno = (TWEANNGenotype) ng;
		}

		if (geno != null) {
			boolean[] inputsUsage = geno.inputUsageProfile();
			if (MMNEAT.task instanceof NetworkTask) {
				NetworkTask task = (NetworkTask) MMNEAT.task;
				String[] labels = task.sensorLabels();

				g.setFont(g.getFont().deriveFont(1));
				int i;
				for (i = 0; i < inputsUsage.length; i++) {
					assert labels[i] != null : "Label " + i + " is null!";
					g.setColor(inputsUsage[i] ? Color.black : Color.red);
					g.drawString(labels[i], offset, (int) ((startY + (i * 0.5)) * offset));
				}

				g.setColor(Color.ORANGE);
				g.drawString("OUTPUTS", offset, (int) ((startY + (i * 0.5)) * offset));
				i++;

				labels = task.outputLabels();
				g.setColor(Color.black);
				for (int j = 0; j < labels.length; j++, i++) {
					g.drawString(labels[j], offset, (int) ((startY + (i * 0.5)) * offset));
				}

				TWEANN.inputPanel = info;
			}
		}
	}

	/**
	 * Should this be in GraphicsUtil or Plot.java?
	 * 
	 * @param panel
	 * @param gen
	 * @param objective
	 * @param scores
	 * @param focus
	 */
	public static void plotBestsWorsts(DrawingPanel panel, int gen, int objective, ArrayList<ArrayList<Double>> scores,
			int focus) {
		int offset = Plot.OFFSET;
		int browseDim = Plot.BROWSE_DIM;
		int ovalDim = Plot.OVAL_DIM;

		Graphics g = panel.getGraphics();
		g.setColor(Color.black);
		// x/y-axes?
		g.drawLine(offset, offset, offset, browseDim - offset);
		g.drawLine(offset, browseDim - offset, browseDim - offset, browseDim - offset);
		double max = maxes.get(objective);
		double min = mins.get(objective);
		double maxRange = Math.max(max, max - min);
		double lowerMin = Math.min(0, min);
		for (int i = 0; i < bestScores[objective].length; i++) {
			g.setColor(Color.blue);
			g.fillRect(offset + GraphicsUtil.scale(i, bestScores[objective].length * 1.0, 0),
					offset + GraphicsUtil.invert(bestScores[objective][i], maxRange, lowerMin), 1, 1);
			g.setColor(Color.magenta);
			g.fillRect(offset + GraphicsUtil.scale(i, worstScores[objective].length * 1.0, 0),
					offset + GraphicsUtil.invert(worstScores[objective][i], maxRange, lowerMin), 1, 1);
		}
		g.setColor(Color.black);
		// Writing labels on axes
		g.drawString("" + max, offset / 2, offset / 2);
		g.drawString("" + lowerMin, offset / 2, browseDim - (offset / 2));

		g.setColor(Color.green);
		g.fillRect(offset + GraphicsUtil.scale(gen, bestScores[objective].length * 1.0, 0),
				offset + GraphicsUtil.invert(scores.get(focus).get(objective), maxRange, lowerMin), ovalDim, ovalDim);

		g.setColor(Color.cyan);
		for (int i = 0; i < scores.size(); i++) {
			if (i != focus) {
				g.drawOval(offset + GraphicsUtil.scale(gen, bestScores[objective].length * 1.0, 0),
						offset + GraphicsUtil.invert(scores.get(i).get(objective), maxRange, lowerMin), ovalDim, ovalDim);
			}
		}

		if (tugGoals != null) {
			for (int i = 0; i < tugGoals[objective].length; i++) {
				g.setColor(Color.green);
				g.fillRect(offset + GraphicsUtil.scale(i, bestScores[objective].length * 1.0, 0),
						offset + GraphicsUtil.invert(tugGoals[objective][i], maxRange, lowerMin), 1, 1);
			}
		}
	}

	/**
	 * 
	 * @param offspring
	 * @param bests
	 */
	public static void plotLineageScores(Offspring offspring, DrawingPanel[] bests) {
		for (int i = 0; i < maxes.size(); i++) {
			Offspring o = offspring;
			while (o != null) {
				double offspringScore = o.scores.get(0).get(i);

				int parent1Pos = (int) o.parentId1;
				if (parent1Pos > -1) {
					Offspring parent1 = lineage.get(parent1Pos);
					double parent1Score = parent1.scores.get(0).get(i);

					double bestScore = parent1Score;
					int bestPos = parent1Pos;
					Offspring bestParent = parent1;

					int parent2Pos = (int) o.parentId2;
					if (parent2Pos > -1) {
						Offspring parent2 = lineage.get(parent2Pos);
						double parent2Score = parent2.scores.get(0).get(i);
						if (parent2Score > bestScore) {
							bestScore = parent2Score;
							bestPos = parent2Pos;
							bestParent = parent2;
						}
					}
					drawScoreSegment(offspringScore, o.correspondingGenerations.get(0), bestScore,
							bestParent.correspondingGenerations.get(0), bests[i], i);

					o = lineage.get(bestPos);
				} else {
					o = null;
				}
			}
		}
	}

	/**
	 * 
	 * @param offspringScore
	 * @param g1
	 * @param bestScore
	 * @param g2
	 * @param drawingPanel
	 * @param objective
	 */
	private static void drawScoreSegment(double offspringScore, int g1, double bestScore, int g2,
			DrawingPanel drawingPanel, int objective) {
		int offset = Plot.OFFSET;

		Graphics2D g = drawingPanel.getGraphics();
		g.setColor(Color.red);
		double max = maxes.get(objective);
		double min = mins.get(objective);
		double maxRange = Math.max(max, max - min);
		double lowerMin = Math.min(0, min);

		int x1 = offset + GraphicsUtil.scale(g1, bestScores[objective].length * 1.0, 0);
		int y1 = offset + GraphicsUtil.invert(offspringScore, maxRange, lowerMin);
		int x2 = offset + GraphicsUtil.scale(g2, bestScores[objective].length * 1.0, 0);
		int y2 = offset + GraphicsUtil.invert(bestScore, maxRange, lowerMin);

		g.drawLine(x1, y1, x2, y2);
	}

	public static void main(String[] args) throws FileNotFoundException {
		// fillInLineage("mspacman/DetDelMMR2/MsPacMan-DetDelMMR2_");
		// fillInLineage("asexual/NoInDetDelLeastMMR1/Asexual-NoInDetDelLeastMMR1_");
		// fillInLineage("breve/Test0/Asexual-Test0_");
		// fillInLineage("brevePred/Pred0/Breve-Pred0_");
		// fillInLineage("brevePP/PP0/Breve-PP0_");
		// fillInLineage("brevePP/NonDetPP0/Breve-NonDetPP0_");
		// fillInLineage("brevePP/NonDetDLMMRPP0/Breve-NonDetDLMMRPP0_");
		// fillInLineage("mspacman/DetControl0/MsPacMan-DetControl0_");
		// fillInLineage("mspacman/DetDLMMR0/MsPacMan-DetDLMMR0_");
		fillInLineage("asexual", "DetDLMMR", 2, "Asexual-DetDLMMR", "DetDLMMR", true);

		// System.out.println(fitnessDifference(99, 1346, 1370));
		// System.out.println(completeMutationHistory(49));
		// System.out.println(findBiggestFitnessJump(0, 1370));
		// System.out.println(findBiggestFitnessJump(1, 1370));
		// JumpPoint jump = findBiggestFitnessJump(1, 1370);
		// System.out.println("From mutations: " + jump.individual.mutations);
		// ArrayList<JumpPoint> jumps = biggestJumpsToReachFinalPopulation(2);
		// System.out.println("Most Recent Ancestor: " +
		// mostRecentCommonAncestor(99));
		// ArrayList<JumpPoint> jumps = jumpsInBest(0);
		// for (int i = 0; i < jumps.size(); i++) {
		// System.out.println(jumps.get(i));
		// }
		// System.out.println("----------------------------------");
		// jumps = jumpsInBest(1);
		// for (int i = 0; i < jumps.size(); i++) {
		// System.out.println(jumps.get(i));
		// }
		// System.out.println(biggestJumpInBest(0));
		// System.out.println(biggestJumpInBest(1));
		// System.out.println(numberOfImprovedOffspringByGeneration(0));
		// System.out.println(numberOfImprovedOffspringByGeneration(1));
		// viewTWEANN("mspacman/DetDelLeastMMR22/gen340/MsPacMan-DetDelLeastMMR22_gen340_15.xml");
		// lineage.get(800).drawTWEANN();

		browse();
	}
}
