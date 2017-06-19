package boardGame.heuristics;

import boardGame.TwoDimensionalBoardGameState;

public class StaticOthelloWPCHeuristic<T extends TwoDimensionalBoardGameState> extends WeightedPieceCounterHeuristic<T> {



	private static final double[][] WEIGHTS = new double[][]{{ 1.00,-0.25, 0.10, 0.05, 0.05, 0.10,-0.25, 1.00},
															 {-0.25,-0.25, 0.01, 0.01, 0.01, 0.01,-0.25,-0.25},
															 { 0.10, 0.01, 0.05, 0.02, 0.02, 0.05, 0.01, 0.10},
															 { 0.05, 0.01, 0.02, 0.01, 0.01, 0.02, 0.01, 0.05},
															 { 0.05, 0.01, 0.02, 0.01, 0.01, 0.02, 0.01, 0.05},
															 { 0.10, 0.01, 0.05, 0.02, 0.02, 0.05, 0.01, 0.10},
															 {-0.25,-0.25, 0.01, 0.01, 0.01, 0.01,-0.25,-0.25},
															 { 1.00,-0.25, 0.10, 0.05, 0.05, 0.10,-0.25, 1.00}};
															 
	/**
	 * Weights for Othello Heuristic from:
	 * 
	 * Temporal Difference Learning Versus Co-Evolution for
	 * Acquiring Othello Position Evaluation
	 * 
	 * (Simon M. Lucas, Thomas P. Runarsson)
	 * 
	 * 
	 * http://citeseerx.ist.psu.edu/viewdoc/download;jsessionid=10AB4B0966FEE51BE133255498065C42?doi=10.1.1.580.8400&rep=rep1&type=pdf											 
	 */
	public StaticOthelloWPCHeuristic() {
		super(WEIGHTS);
	}
	
}
