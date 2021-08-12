package fr.inria.optimization.cmaes.fitness;

/**
 * Shifted sphere function for optimization,
 * moving the center of the function from (0, 0)
 * to (2.048, 2.048).
 * 
 * @author Maxx Batterton
 *
 */
public class ShiftedSphereFunction extends AbstractObjectiveFunction {
	// Shifted same amount as pyribs example
	private static final double SPHERE_SHIFT = 2.048; // (5.12 * 0.4)
	
	@Override
    public double valueOf (double[] x) {
        double res = 0;
        for (int i = 0; i < x.length; ++i)
            res += (x[i] - SPHERE_SHIFT) * (x[i] - SPHERE_SHIFT);
        return res;
    }
	
    public boolean isFeasible(double[] x) {
    	return true; // Always
    }

}
