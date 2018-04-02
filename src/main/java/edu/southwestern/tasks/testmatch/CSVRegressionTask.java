package edu.southwestern.tasks.testmatch;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import edu.southwestern.networks.Network;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.file.CSVFileUtilities;

/**
 * Evolve a model for a supervised regression problem using a CSV file of training data.
 * 
 * Currently only supports regression ... might support classification later.
 * 
 * @author Jacob Schrum
 *
 * @param <T>
 */
public class CSVRegressionTask<T extends Network> extends MatchDataTask<T> {

	private ArrayList<String> inputLabels;
	private ArrayList<Pair<double[], double[]>> trainingData;
	private String targetColumn;
	
	public CSVRegressionTask() {
		String csv = Parameters.parameters.stringParameter("csvInputFile");
		targetColumn = Parameters.parameters.stringParameter("regressionTargetColumn");		
		// The target column will be excluded from the list of columns used for input
		ArrayList<String> target = new ArrayList<>(1);
		target.add(targetColumn);

		File csvFile = new File(csv);
		try {
			inputLabels = CSVFileUtilities.columnHeaders(csvFile, target);
			trainingData = CSVFileUtilities.inputOutputPairs(csvFile, targetColumn);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("Fatal Error: No CSV file: " + csv);
			System.exit(1);
		}
	}
	
	@Override
	public String[] sensorLabels() {
		return inputLabels.toArray(new String[inputLabels.size()]);
	}

	@Override
	public String[] outputLabels() {
		return new String[] {targetColumn};
	}

	@Override
	public int numInputs() {
		return inputLabels.size();
	}

	@Override
	public int numOutputs() {
		return 1; // Assume regression with one target
	}

	@Override
	public ArrayList<Pair<double[], double[]>> getTrainingPairs() {
		return this.trainingData;
	}

}
