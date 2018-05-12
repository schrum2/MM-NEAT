package edu.southwestern.util.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import edu.southwestern.util.datastructures.Pair;

public class CSVFileUtilities {
	
	/**
	 * Take a CSV file and strip out the column headers, which should be on the first line
	 * @param csv Text file with comma-separated fields that starts with column headers
	 * @param toExclude Specific known column labels to exclude from result
	 * @return ArrayList of non-excluded column headers (typical to exclude target column for regression problems)
	 * @throws FileNotFoundException
	 */
	public static ArrayList<String> columnHeaders(File csv, ArrayList<String> toExclude) throws FileNotFoundException {
		Scanner read = new Scanner(csv);
		ArrayList<String> result = new ArrayList<String>();
		// All headers should be on first line
		Scanner line = new Scanner(read.nextLine());
		line.useDelimiter(","); // Because columns are comma separated
		read.close();
		// Each token on line
		while(line.hasNext()) {
			String header = line.next();
			if(!toExclude.contains(header)) {
				result.add(header);
			}
		}
		line.close();
		return result;
	}

	/**
	 * Extract all data from the CSV file and treat it as a collection of input-output pairs.
	 * Each row contains several input values, but the column with the label targetColumn is excluded as
	 * input and instead becomes the lone output target for the row.
	 * 
	 * @param csvFile CSV file with only numeric data
	 * @param targetColumn Column header to use as target
	 * @return Collection of input-output pairs from file
	 * @throws FileNotFoundException
	 */
	public static ArrayList<Pair<double[], double[]>> inputOutputPairs(File csvFile, String targetColumn) throws FileNotFoundException {
		Scanner csv = new Scanner(csvFile);
		// First line contains column headers
		Scanner headerScanner = new Scanner(csv.nextLine());
		headerScanner.useDelimiter(",");
		boolean targetFound = false; // make sure target column is one of the headers
		int targetPosition = 0;
		int inputCount = 0;
		while(headerScanner.hasNext()) {
			String next = headerScanner.next();
			if(next.equals(targetColumn)) {
				targetFound = true; // verify that header was found
			} else {
				// Maybe target column is in next position
				if(!targetFound) targetPosition++;
				// One more input field was found
				inputCount++;
			}
		}
		headerScanner.close();
		if(!targetFound) {
			csv.close();
			throw new IllegalStateException("Target column \"" + targetColumn + "\" was not one of the headers of \"" + csvFile.getName() + "\"");
		}
		
		// Used to compute variance
		double[] sums = new double[inputCount];
		double targetSum = 0;
		double[] sumInputsSquared = new double[inputCount];
		double sumTargetsSquared = 0;
		
		// the rest of the CSV file contains the data
		ArrayList<Pair<double[], double[]>> data = new ArrayList<>();
		while(csv.hasNextLine()) { // Each line is a single training example
			double[] inputs = new double[inputCount];
			double[] target = new double[1]; // Assuming a single regression target
			Scanner row = new Scanner(csv.nextLine());
			row.useDelimiter(","); // values are comma separated
			int index = 0; // in inputs array
			for(int column = 0; row.hasNext(); column++) { // column is location in file's row
				// Assuming all data is pre-processed numeric data
				double datum = row.nextDouble();
				if(column == targetPosition) { // NN output target
					// Used for scaling later
					targetSum += datum; 
					sumTargetsSquared += datum*datum;
					
					target[0] = datum;
				} else { // NN input value
					// Used for scaling later
					sums[index] += datum; 
					sumInputsSquared[index] += datum*datum;
					
					inputs[index++] = datum;
				}
			}
			row.close(); // close row scanner
			data.add(new Pair<>(inputs,target));
		}
		csv.close(); // Close file scanner
		
		
		// TODO: Fix: This code doesn't scale values in the way I thought it would. 
		//       Simply removing for now -Schrum
		
		// Calculate variances to divide the inputs and targets. This will scale
		// those values, assuring that they are all in the range [-1,1]
//		double[] inputVariances = new double[inputCount];
//		for(int i = 0; i < inputVariances.length; i++) {
//			// Computational formula for sum of squares and variance
//			double inputSS = sumInputsSquared[i] - (sums[i]*sums[i] / data.size());
//			inputVariances[i] = inputSS / data.size();
//		}		
//		// Computational formula for sum of squares and variance
//		double targetSS = sumTargetsSquared - (targetSum*targetSum / data.size());
//		double targetVariance = targetSS / data.size(); // Population variance: should it be sample variance instead?
		
		// TODO: Fix: Also does not scale as expected. However, some kind of scaling is likely needed
		
		// Now scale all inputs and outputs
//		for(Pair<double[],double[]> example : data) { // Each training example
//			for(int i = 0; i < example.t1.length; i++) { // scale each input
//				example.t1[i] /= inputVariances[i];
//			}
//			example.t2[0] /= targetVariance; // There is only one output to scale
//		}
		
		return data;
	}

}
