package edu.southwestern.util.stats;

/**
 * Finds the entry that is the most distinct
 * 
 * @author Gabby Gonzalez
 *
 */
public class MostExtreme implements Statistic {

	@Override
	public double stat(double[] xs) {
		double[] extremes = new double[xs.length];
		for(int i = 0; i < xs.length; i++){
			double[] diff = new double[xs.length];
			for(int j = 0; j < xs.length; j++){
				if(i != j){
					diff[i] = Math.abs(xs[i] - xs[j]);
				}
			}
			extremes[i] = StatisticsUtilities.maximum(diff);			
		}
		return StatisticsUtilities.maximum(extremes);
	}

}
