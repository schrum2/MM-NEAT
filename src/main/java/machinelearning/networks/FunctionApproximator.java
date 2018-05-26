package machinelearning.networks;

/**
* Created by Jacob Schrum
* 
 * For some reason, MLPs were not a subclass of Networks, but LSTMs were.
 * FunctionApproximator provides a simple, common superclass for both,
 * which is needed by the Behavioral Diversity Calculator
*/
public interface FunctionApproximator {

   public double[] propagate(double[] doubles);

   public int getNumberOfInputs();

   public int getNumberOfOutputs();

}