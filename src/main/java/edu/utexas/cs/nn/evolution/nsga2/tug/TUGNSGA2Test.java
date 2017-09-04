package edu.utexas.cs.nn.evolution.nsga2.tug;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Test;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.evolution.genotypes.RealValuedGenotype;
import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.scores.MultiObjectiveScore;
import edu.utexas.cs.nn.scores.Score;
import edu.utexas.cs.nn.tasks.mspacman.MsPacManTask;
import edu.utexas.cs.nn.util.PopulationUtil;
import edu.utexas.cs.nn.util.datastructures.ArrayUtil;

public class TUGNSGA2Test {



	@After
	public void tearDown() throws Exception {
		MMNEAT.clearClasses();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void test() {
		Parameters.initializeParameterCollections(new String[] { "io:false", "netio:false", "tugKeepsParetoFront:false" });
		MMNEAT.loadClasses();
		MMNEAT.task = new MsPacManTask();
		TUGNSGA2 ea = new TUGNSGA2<ArrayList<Double>>();

		ArrayList<Score<ArrayList<Double>>> scores = new ArrayList<Score<ArrayList<Double>>>();

		ArrayList<Long> layer0 = new ArrayList<Long>();
		RealValuedGenotype g0_10 = new RealValuedGenotype(new double[] { 0, 10 });
		scores.add(new MultiObjectiveScore<ArrayList<Double>>(g0_10, new double[] { 0, 10 }, null));
		layer0.add(scores.get(0).individual.getId());
		RealValuedGenotype g5_5 = new RealValuedGenotype(new double[] { 5, 5 });
		scores.add(new MultiObjectiveScore<ArrayList<Double>>(g5_5, new double[] { 5, 5 }, null));
		layer0.add(scores.get(1).individual.getId());
		RealValuedGenotype g10_0 = new RealValuedGenotype(new double[] { 10, 0 });
		scores.add(new MultiObjectiveScore<ArrayList<Double>>(g10_0, new double[] { 10, 0 }, null));
		layer0.add(scores.get(2).individual.getId());
		RealValuedGenotype g2_6 = new RealValuedGenotype(new double[] { 2, 6 });
		scores.add(new MultiObjectiveScore<ArrayList<Double>>(g2_6, new double[] { 2, 6 }, null));
		layer0.add(scores.get(3).individual.getId());

		ArrayList<Long> layer1 = new ArrayList<Long>();
		RealValuedGenotype g0_4 = new RealValuedGenotype(new double[] { 0, 4 });
		scores.add(new MultiObjectiveScore<ArrayList<Double>>(g0_4, new double[] { 0, 4 }, null));
		layer1.add(scores.get(4).individual.getId());
		RealValuedGenotype g3_3 = new RealValuedGenotype(new double[] { 3, 3 });
		scores.add(new MultiObjectiveScore<ArrayList<Double>>(g3_3, new double[] { 3, 3 }, null));
		layer1.add(scores.get(5).individual.getId());
		RealValuedGenotype g4_0 = new RealValuedGenotype(new double[] { 4, 0 });
		scores.add(new MultiObjectiveScore<ArrayList<Double>>(g4_0, new double[] { 4, 0 }, null));
		layer1.add(scores.get(6).individual.getId());

		RealValuedGenotype g1_1 = new RealValuedGenotype(new double[] { 1, 1 });
		scores.add(new MultiObjectiveScore<ArrayList<Double>>(g1_1, new double[] { 1, 1 }, null));

		//		System.out.println("Low goals should behave just like NSGA2");
		// ea.goals = new double[2];
		// System.out.println("Select 1: " + ea.selection(1, scores) + ", Use: "
		// + Arrays.toString(((TUGNSGA2) ea).useObjective));
		// ea.goals = new double[2];
		// System.out.println("Select 2: " + ea.selection(2, scores) + ", Use: "
		// + Arrays.toString(((TUGNSGA2) ea).useObjective));
		// ea.goals = new double[2];
		// System.out.println("Select 3: " + ea.selection(3, scores) + ", Use: "
		// + Arrays.toString(((TUGNSGA2) ea).useObjective));

		ea.goals = new double[2];
		ArrayList<Genotype<ArrayList<Double>>> result0 = ea.selection(4, scores);
		// System.out.println("Select 4: " + result0 + ", Use: " +
		// Arrays.toString(((TUGNSGA2) ea).useObjective));

		assertTrue("FAILED 0 " + layer0 + " AND " + result0, ArrayUtil.setEquality(PopulationUtil.getGenotypeIds(result0), layer0));


		// ea.goals = new double[2];
		// System.out.println("Select 5: " + ea.selection(5, scores) + ", Use: "
		// + Arrays.toString(((TUGNSGA2) ea).useObjective));
		// ea.goals = new double[2];
		// System.out.println("Select 6: " + ea.selection(6, scores) + ", Use: "
		// + Arrays.toString(((TUGNSGA2) ea).useObjective));

		ea.goals = new double[2];
		ArrayList<Genotype<ArrayList<Double>>> result1 = ea.selection(7, scores);
		// System.out.println("Select 7: " + result1 + ", Use: " +
		// Arrays.toString(((TUGNSGA2) ea).useObjective));

		layer1.addAll(layer0);
		assertTrue("FAILED 1 " + layer1 + " AND " + result1, ArrayUtil.setEquality(PopulationUtil.getGenotypeIds(result1), layer1));

		ea.useObjective = new boolean[] { true, false };
		//		System.out.println("TUG towards objective 0");
		ea.goals = new double[] { 20, 0 };
		ArrayList<Genotype<ArrayList<Double>>> result2 = ea.selection(1, scores);
		ArrayList<Long> best = new ArrayList<Long>();
		best.add(g10_0.getId());
		assertTrue("FAILED 2 " + best + " AND " + result2, ArrayUtil.setEquality(PopulationUtil.getGenotypeIds(result2), best));
		// System.out.println("Select 1: " + ea.selection(1, scores) + ", Use: "
		// + Arrays.toString(((TUGNSGA2) ea).useObjective));

		ea.goals = new double[] { 20, 0 };
		ArrayList<Genotype<ArrayList<Double>>> result3 = ea.selection(2, scores);
		best.add(g5_5.getId());
		assertTrue("FAILED 3 " + best + " AND " + result3, ArrayUtil.setEquality(PopulationUtil.getGenotypeIds(result3), best));
		// System.out.println("Select 2: " + ea.selection(2, scores) + ", Use: "
		// + Arrays.toString(((TUGNSGA2) ea).useObjective));

		ea.goals = new double[] { 20, 0 };
		ArrayList<Genotype<ArrayList<Double>>> result4 = ea.selection(3, scores);
		best.add(g4_0.getId());
		assertTrue("FAILED 4 " + best + " AND " + result4, ArrayUtil.setEquality(PopulationUtil.getGenotypeIds(result4), best));
		// System.out.println("Select 3: " + ea.selection(3, scores) + ", Use: "
		// + Arrays.toString(((TUGNSGA2) ea).useObjective));

		ea.goals = new double[] { 20, 0 };
		ArrayList<Genotype<ArrayList<Double>>> result5 = ea.selection(4, scores);
		best.add(g3_3.getId());
		assertTrue("FAILED 5 " + best + " AND " + result5, ArrayUtil.setEquality(PopulationUtil.getGenotypeIds(result5), best));
		// System.out.println("Select 4: " + ea.selection(4, scores) + ", Use: "
		// + Arrays.toString(((TUGNSGA2) ea).useObjective));

		ea.goals = new double[] { 20, 0 };
		ArrayList<Genotype<ArrayList<Double>>> result6 = ea.selection(5, scores);
		best.add(g2_6.getId());
		assertTrue("FAILED 6 " + best + " AND " + result6, ArrayUtil.setEquality(PopulationUtil.getGenotypeIds(result6), best));
		// System.out.println("Select 5: " + ea.selection(5, scores) + ", Use: "
		// + Arrays.toString(((TUGNSGA2) ea).useObjective));

		ea.goals = new double[] { 20, 0 };
		ArrayList<Genotype<ArrayList<Double>>> result6a = ea.selection(6, scores);
		best.add(g1_1.getId());
		assertTrue("FAILED 6a " + best + " AND " + result6a, ArrayUtil.setEquality(PopulationUtil.getGenotypeIds(result6a), best));
		// System.out.println("Select 6: " + ea.selection(6, scores) + ", Use: "
		// + Arrays.toString(((TUGNSGA2) ea).useObjective));
		// ea.goals = new double[]{20, 0};
		// System.out.println("Select 7: " + ea.selection(7, scores) + ", Use: "
		// + Arrays.toString(((TUGNSGA2) ea).useObjective));

		ea.useObjective = new boolean[] { false, true };
		//		System.out.println("TUG towards objective 1");
		ea.goals = new double[] { 0, 20 };
		ArrayList<Genotype<ArrayList<Double>>> result2b = ea.selection(1, scores);
		best = new ArrayList<Long>();
		best.add(g0_10.getId());
		assertTrue("FAILED 2b " + best + " AND " + result2b, ArrayUtil.setEquality(PopulationUtil.getGenotypeIds(result2b), best));

		ea.goals = new double[] { 0, 20 };
		ArrayList<Genotype<ArrayList<Double>>> result3b = ea.selection(2, scores);
		best.add(g2_6.getId());
		assertTrue("FAILED 3b " + best + " AND " + result3b, ArrayUtil.setEquality(PopulationUtil.getGenotypeIds(result3b), best));

		ea.goals = new double[] { 0, 20 };
		ArrayList<Genotype<ArrayList<Double>>> result4b = ea.selection(3, scores);
		best.add(g5_5.getId());
		assertTrue("FAILED 4b " + best + " AND " + result4b, ArrayUtil.setEquality(PopulationUtil.getGenotypeIds(result4b), best));

		ea.goals = new double[] { 0, 20 };
		ArrayList<Genotype<ArrayList<Double>>> result5b = ea.selection(4, scores);
		best.add(g0_4.getId());
		assertTrue("FAILED 5b " + best + " AND " + result5b, ArrayUtil.setEquality(PopulationUtil.getGenotypeIds(result5b), best));

		ea.goals = new double[] { 0, 20 };
		ArrayList<Genotype<ArrayList<Double>>> result6b = ea.selection(5, scores);
		best.add(g3_3.getId());
		assertTrue("FAILED 6b " + best + " AND " + result6b, ArrayUtil.setEquality(PopulationUtil.getGenotypeIds(result6b), best));

		ea.goals = new double[] { 0, 20 };
		ArrayList<Genotype<ArrayList<Double>>> result7b = ea.selection(6, scores);
		best.add(g1_1.getId());
		assertTrue("FAILED 7b " + best + " AND " + result7b, ArrayUtil.setEquality(PopulationUtil.getGenotypeIds(result7b), best));

		Parameters.parameters.setBoolean("tugKeepsParetoFront", true);
		CommonConstants.tugKeepsParetoFront = true;

		//		System.out.println("Low goals should behave just like NSGA2, even with tugKeepsParetoFront on");

		ea.useObjective = new boolean[] { true, true };
		ArrayList<Genotype<ArrayList<Double>>> result7 = ea.selection(4, scores);
		ea.goals = new double[2];
		assertTrue("FAILED 7 " + layer0 + " AND " + result7, ArrayUtil.setEquality(PopulationUtil.getGenotypeIds(result7), layer0));

		ea.goals = new double[2];
		ArrayList<Genotype<ArrayList<Double>>> result8 = ea.selection(7, scores);
		assertTrue("FAILED 8 " + layer1 + " AND " + result8, ArrayUtil.setEquality(PopulationUtil.getGenotypeIds(result8), layer1));

		// ea.goals = new double[2];
		// System.out.println("Select 1: " + ea.selection(1, scores) + ", Use: "
		// + Arrays.toString(((TUGNSGA2) ea).useObjective));
		// ea.goals = new double[2];
		// System.out.println("Select 2: " + ea.selection(2, scores) + ", Use: "
		// + Arrays.toString(((TUGNSGA2) ea).useObjective));
		// ea.goals = new double[2];
		// System.out.println("Select 3: " + ea.selection(3, scores) + ", Use: "
		// + Arrays.toString(((TUGNSGA2) ea).useObjective));
		// ea.goals = new double[2];
		// System.out.println("Select 4: " + ea.selection(4, scores) + ", Use: "
		// + Arrays.toString(((TUGNSGA2) ea).useObjective));
		// ea.goals = new double[2];
		// System.out.println("Select 5: " + ea.selection(5, scores) + ", Use: "
		// + Arrays.toString(((TUGNSGA2) ea).useObjective));
		// ea.goals = new double[2];
		// System.out.println("Select 6: " + ea.selection(6, scores) + ", Use: "
		// + Arrays.toString(((TUGNSGA2) ea).useObjective));
		// ea.goals = new double[2];
		// System.out.println("Select 7: " + ea.selection(7, scores) + ", Use: "
		// + Arrays.toString(((TUGNSGA2) ea).useObjective));

		ea.useObjective = new boolean[] { true, false };
//		System.out.println("TUG towards objective 0, but favor Pareto front first");

		ArrayList<Genotype<ArrayList<Double>>> result9 = ea.selection(4, scores);
		ea.goals = new double[] { 20, 0 };
		assertTrue("FAILED 9 " + layer0 + " AND " + result9, ArrayUtil.setEquality(PopulationUtil.getGenotypeIds(result9), layer0));

		ArrayList<Genotype<ArrayList<Double>>> result10 = ea.selection(5, scores);
		ea.goals = new double[] { 20, 0 };
		layer0.add(g4_0.getId());
		assertTrue("FAILED 10 " + layer0 + " AND " + result10, ArrayUtil.setEquality(PopulationUtil.getGenotypeIds(result10), layer0));

		ArrayList<Genotype<ArrayList<Double>>> result11 = ea.selection(6, scores);
		ea.goals = new double[] { 20, 0 };
		layer0.add(g3_3.getId());
		assertTrue("FAILED 11 " + layer0 + " AND " + result11, ArrayUtil.setEquality(PopulationUtil.getGenotypeIds(result11), layer0));

		ArrayList<Genotype<ArrayList<Double>>> result12 = ea.selection(7, scores);
		ea.goals = new double[] { 20, 0 };
		layer0.add(g1_1.getId());
		assertTrue("FAILED 12 " + layer0 + " AND " + result12, ArrayUtil.setEquality(PopulationUtil.getGenotypeIds(result12), layer0));

		// System.out.println("Select 1: " + ea.selection(1, scores) + ", Use: "
		// + Arrays.toString(((TUGNSGA2) ea).useObjective));
		// ea.goals = new double[]{20, 0};
		// System.out.println("Select 2: " + ea.selection(2, scores) + ", Use: "
		// + Arrays.toString(((TUGNSGA2) ea).useObjective));
		// ea.goals = new double[]{20, 0};
		// System.out.println("Select 3: " + ea.selection(3, scores) + ", Use: "
		// + Arrays.toString(((TUGNSGA2) ea).useObjective));
		// ea.goals = new double[]{20, 0};
		// System.out.println("Select 4: " + ea.selection(4, scores) + ", Use: "
		// + Arrays.toString(((TUGNSGA2) ea).useObjective));
		// ea.goals = new double[]{20, 0};
		// System.out.println("Select 5: " + ea.selection(5, scores) + ", Use: "
		// + Arrays.toString(((TUGNSGA2) ea).useObjective));
		// ea.goals = new double[]{20, 0};
		// System.out.println("Select 6: " + ea.selection(6, scores) + ", Use: "
		// + Arrays.toString(((TUGNSGA2) ea).useObjective));
		// ea.goals = new double[]{20, 0};
		// System.out.println("Select 7: " + ea.selection(7, scores) + ", Use: "
		// + Arrays.toString(((TUGNSGA2) ea).useObjective));

		scores = new ArrayList<Score<ArrayList<Double>>>();

		layer0 = new ArrayList<Long>();
		RealValuedGenotype x1000_11 = new RealValuedGenotype(new double[] { 1000, 11 });
		scores.add(new MultiObjectiveScore<ArrayList<Double>>(x1000_11, new double[] { 1000, 11 }, null));
		layer0.add(scores.get(0).individual.getId());

		layer1 = new ArrayList<Long>();
		RealValuedGenotype x0_10 = new RealValuedGenotype(new double[] { 0, 10 });
		scores.add(new MultiObjectiveScore<ArrayList<Double>>(x0_10, new double[] { 0, 10 }, null));
		layer1.add(scores.get(1).individual.getId());
		RealValuedGenotype x5_7 = new RealValuedGenotype(new double[] { 5, 7 });
		scores.add(new MultiObjectiveScore<ArrayList<Double>>(x5_7, new double[] { 5, 7 }, null));
		layer1.add(scores.get(2).individual.getId());
		RealValuedGenotype x50_5 = new RealValuedGenotype(new double[] { 50, 5 });
		scores.add(new MultiObjectiveScore<ArrayList<Double>>(x50_5, new double[] { 50, 5 }, null));
		layer1.add(scores.get(3).individual.getId());
		RealValuedGenotype x100_2 = new RealValuedGenotype(new double[] { 100, 2 });
		scores.add(new MultiObjectiveScore<ArrayList<Double>>(x100_2, new double[] { 100, 2 }, null));
		layer1.add(scores.get(4).individual.getId());
		RealValuedGenotype x500_0 = new RealValuedGenotype(new double[] { 500, 0 });
		scores.add(new MultiObjectiveScore<ArrayList<Double>>(x500_0, new double[] { 500, 0 }, null));
		layer1.add(scores.get(5).individual.getId());

		ArrayList<Long> layer2 = new ArrayList<Long>();
		RealValuedGenotype x10_5 = new RealValuedGenotype(new double[] { 10, 5 });
		scores.add(new MultiObjectiveScore<ArrayList<Double>>(x10_5, new double[] { 10, 5 }, null));
		layer2.add(scores.get(6).individual.getId());
		RealValuedGenotype x11_4 = new RealValuedGenotype(new double[] { 11, 4 });
		scores.add(new MultiObjectiveScore<ArrayList<Double>>(x11_4, new double[] { 11, 4 }, null));
		layer2.add(scores.get(7).individual.getId());
		RealValuedGenotype x40_3 = new RealValuedGenotype(new double[] { 40, 3 });
		scores.add(new MultiObjectiveScore<ArrayList<Double>>(x40_3, new double[] { 40, 3 }, null));
		layer2.add(scores.get(8).individual.getId());
		RealValuedGenotype x75_1 = new RealValuedGenotype(new double[] { 75, 1 });
		scores.add(new MultiObjectiveScore<ArrayList<Double>>(x75_1, new double[] { 75, 1 }, null));
		layer2.add(scores.get(9).individual.getId());

		ArrayList<Long> layer3 = new ArrayList<Long>();
		RealValuedGenotype x40_2 = new RealValuedGenotype(new double[] { 40, 2 });
		scores.add(new MultiObjectiveScore<ArrayList<Double>>(x40_2, new double[] { 40, 2 }, null));
		layer3.add(scores.get(10).individual.getId());
		RealValuedGenotype x50_1 = new RealValuedGenotype(new double[] { 50, 1 });
		scores.add(new MultiObjectiveScore<ArrayList<Double>>(x50_1, new double[] { 50, 1 }, null));
		layer3.add(scores.get(11).individual.getId());

		ArrayList<Long> layer4 = new ArrayList<Long>();
		RealValuedGenotype x10_2 = new RealValuedGenotype(new double[] { 10, 2 });
		scores.add(new MultiObjectiveScore<ArrayList<Double>>(x10_2, new double[] { 10, 2 }, null));
		layer4.add(scores.get(12).individual.getId());
		RealValuedGenotype x40_1 = new RealValuedGenotype(new double[] { 40, 1 });
		scores.add(new MultiObjectiveScore<ArrayList<Double>>(x40_1, new double[] { 40, 1 }, null));
		layer4.add(scores.get(13).individual.getId());

		ArrayList<Long> layer5 = new ArrayList<Long>();
		RealValuedGenotype x5_2 = new RealValuedGenotype(new double[] { 5, 2 });
		scores.add(new MultiObjectiveScore<ArrayList<Double>>(x5_2, new double[] { 5, 2 }, null));
		layer5.add(scores.get(14).individual.getId());
		RealValuedGenotype x10_1 = new RealValuedGenotype(new double[] { 10, 1 });
		scores.add(new MultiObjectiveScore<ArrayList<Double>>(x10_1, new double[] { 10, 1 }, null));
		layer5.add(scores.get(15).individual.getId());

		/// Start testing ///////////////////////

		Parameters.parameters.setBoolean("tugKeepsParetoFront", false);
		CommonConstants.tugKeepsParetoFront = false;

//		System.out.println("Using a different set of points");
//		System.out.println("Low goals should behave just like NSGA2");

		ea.useObjective = new boolean[] { true, true };
		ArrayList<Genotype<ArrayList<Double>>> result13 = ea.selection(1, scores);
		ea.goals = new double[2];
		assertTrue("FAILED 13 " + layer0 + " AND " + result13, ArrayUtil.setEquality(PopulationUtil.getGenotypeIds(result13), layer0));

		layer1.addAll(layer0);

		ea.goals = new double[2];
		ArrayList<Genotype<ArrayList<Double>>> result14 = ea.selection(6, scores);
		assertTrue("FAILED 14 " + layer1 + " AND " + result14, ArrayUtil.setEquality(PopulationUtil.getGenotypeIds(result14), layer1));

		layer2.addAll(layer1);

		ea.goals = new double[2];
		ArrayList<Genotype<ArrayList<Double>>> result15 = ea.selection(10, scores);
		assertTrue("FAILED 15 " + layer2 + " AND " + result15, ArrayUtil.setEquality(PopulationUtil.getGenotypeIds(result15), layer2));

		layer3.addAll(layer2);

		ea.goals = new double[2];
		ArrayList<Genotype<ArrayList<Double>>> result16 = ea.selection(12, scores);
		assertTrue("FAILED 16 " + layer3 + " AND " + result16, ArrayUtil.setEquality(PopulationUtil.getGenotypeIds(result16), layer3));

		layer4.addAll(layer3);

		ea.goals = new double[2];
		ArrayList<Genotype<ArrayList<Double>>> result17 = ea.selection(14, scores);
		assertTrue("FAILED 17 " + layer4 + " AND " + result17, ArrayUtil.setEquality(PopulationUtil.getGenotypeIds(result17), layer4));

		layer5.addAll(layer4);

		ea.goals = new double[2];
		ArrayList<Genotype<ArrayList<Double>>> result18 = ea.selection(16, scores);
		assertTrue("FAILED 18 " + layer5 + " AND " + result18, ArrayUtil.setEquality(PopulationUtil.getGenotypeIds(result18), layer5));

		// Now change the goals

		ea.useObjective = new boolean[] { false, true };
//		System.out.println("TUG towards objective 1");
		ea.goals = new double[] { 0, 20 };
		ArrayList<Genotype<ArrayList<Double>>> result19 = ea.selection(1, scores);
		best = new ArrayList<Long>();
		best.add(x1000_11.getId());
		assertTrue("FAILED 19 " + best + " AND " + result19, ArrayUtil.setEquality(PopulationUtil.getGenotypeIds(result19), best));

		ea.goals = new double[] { 0, 20 };
		ArrayList<Genotype<ArrayList<Double>>> result20 = ea.selection(2, scores);
		best.add(x0_10.getId());
		assertTrue("FAILED 20 " + best + " AND " + result20, ArrayUtil.setEquality(PopulationUtil.getGenotypeIds(result20), best));

		ea.goals = new double[] { 0, 20 };
		ArrayList<Genotype<ArrayList<Double>>> result21 = ea.selection(3, scores);
		best.add(x5_7.getId());
		assertTrue("FAILED 21 " + best + " AND " + result21, ArrayUtil.setEquality(PopulationUtil.getGenotypeIds(result21), best));

		ea.goals = new double[] { 0, 20 };
		ArrayList<Genotype<ArrayList<Double>>> result22 = ea.selection(5, scores);
		best.add(x10_5.getId());
		best.add(x50_5.getId());
		assertTrue("FAILED 22 " + best + " AND " + result22, ArrayUtil.setEquality(PopulationUtil.getGenotypeIds(result22), best));

		ea.goals = new double[] { 0, 20 };
		ArrayList<Genotype<ArrayList<Double>>> result23 = ea.selection(6, scores);
		best.add(x11_4.getId());
		assertTrue("FAILED 23 " + best + " AND " + result23, ArrayUtil.setEquality(PopulationUtil.getGenotypeIds(result23), best));

		ea.goals = new double[] { 0, 20 };
		ArrayList<Genotype<ArrayList<Double>>> result24 = ea.selection(7, scores);
		best.add(x40_3.getId());
		assertTrue("FAILED 24 " + best + " AND " + result24, ArrayUtil.setEquality(PopulationUtil.getGenotypeIds(result24), best));

		ea.goals = new double[] { 0, 20 };
		ArrayList<Genotype<ArrayList<Double>>> result25 = ea.selection(11, scores);
		best.add(x5_2.getId());
		best.add(x10_2.getId());
		best.add(x40_2.getId());
		best.add(x100_2.getId());
		assertTrue("FAILED 25 " + best + " AND " + result25, ArrayUtil.setEquality(PopulationUtil.getGenotypeIds(result25), best));
	}

}
