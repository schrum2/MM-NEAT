package edu.utexas.cs.nn.evolution.substrate;

import edu.utexas.cs.nn.util.datastructures.Pair;

public interface ISubstrate<T> {

public void setNeurons(Pair<T, T> inputNodes, Pair<T, T> hiddenNodes, Pair<T, T> outputNodes);

public void setCustomConnectivity(Pair<T, T> toAdd);

public void clearCustomConnectivity();

public Pair<T, T> maxDimension();

public String toString();

public String substrateType();

public boolean setNewCoordinate(Pair<T, T> toAdd);

public boolean addNewLayer(double[] location, String substrateType);
}
