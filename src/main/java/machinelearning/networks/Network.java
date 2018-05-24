package machinelearning.networks;

import machinelearning.evolution.evolvables.Evolvable;

/**
* Created by IntelliJ IDEA.
* User: julian
* Date: Oct 30, 2008
* Time: 4:53:17 PM
*/
public interface Network extends Evolvable, FunctionApproximator {

    @Override
   public double[] propagate(double[] doubles);

    @Override
   public void reset();

   //public double[] calculateDerivatives(double[] outputError);

   public void changeWeights (double[] weightChanges);

   public double[] getWeightsArray();

   public void setWeightsArray(double[] weights);

   public void randomise();

    @Override
   public int getNumberOfInputs();

   public int getNumberOfWeights ();

    @Override
   public int getNumberOfOutputs();

}