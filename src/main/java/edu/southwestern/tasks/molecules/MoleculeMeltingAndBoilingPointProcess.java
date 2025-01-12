package edu.southwestern.tasks.molecules;

import java.io.IOException;

import edu.southwestern.evolution.genotypes.SMILESStringGenotype;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.util.datastructures.Pair;

/**
 * Takes a SMILES string and determines both its melting and boiling points.
 * @author schrum2
 *
 */
public class MoleculeMeltingAndBoilingPointProcess extends MoleculeProcess {

	private static MoleculeMeltingAndBoilingPointProcess meltingBoilingProcess;
	private static final boolean DEBUG = true;
	public static final double BAD_RESULT = 999.0;
	
	private static synchronized MoleculeMeltingAndBoilingPointProcess getMeltingBoilingPointProcess() {
		if(meltingBoilingProcess == null) {
			meltingBoilingProcess = new MoleculeMeltingAndBoilingPointProcess();
			meltingBoilingProcess.start();			
		}
		return meltingBoilingProcess;
	}
	
	public static void terminateMelingBoilingPointProcess() {
		if(meltingBoilingProcess != null) {
			try {
				meltingBoilingProcess.commSend("-1"); // graceful exit
			} catch (IOException e) {
				meltingBoilingProcess.process.destroy();
			}
			meltingBoilingProcess = null;
		}
	}
	
	private MoleculeMeltingAndBoilingPointProcess() {
		super("SMILESMeltingAndBoilingPoint.exe");
	}

	/**
	 * For a given SMILES String (stored in a genotype) compute the melting point
	 * and boiling point and return in a pair: first the melting point, then the 
	 * boiling point.
	 * @param smiles Genotype with a SMILES string
	 * @return Pair of melting point followed by boiling point
	 */
	public static synchronized Pair<Double,Double> smilesMeltingAndBoilingPoints(SMILESStringGenotype smiles) {
		MoleculeMeltingAndBoilingPointProcess process = getMeltingBoilingPointProcess();
		String smilesString = smiles.getPhenotype();
		if(DEBUG) System.out.println("MMB:"+smilesString);
		try {
			process.commSend(""+smilesString.length());
			process.commSend(smilesString);	
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("MoleculeMeltingAndBoilingPointProcess failed");
			System.exit(1);
		}
		if(DEBUG) System.out.println("MMB:about to receive");
		String meltingResult = process.commRecv();
		if(DEBUG) System.out.println("MMB:"+"meltingResult:"+meltingResult);
		if(meltingResult.trim().contains(" ")) {
			System.out.println("The melting point process returned bad results");
			System.out.println("meltingResult = "+meltingResult);
			// Need more error information here
			System.exit(1);
		}
		double melting = meltingResult.trim().equals("X") ? BAD_RESULT : Double.parseDouble(meltingResult);
		String boilingResult = process.commRecv();
		if(DEBUG) System.out.println("MMB:"+"boilingResult:"+boilingResult);
		if(boilingResult.trim().contains(" ")) {
			System.out.println("The boiling point process returned bad results");
			System.out.println("boilingResult = "+boilingResult);
			// Need more error information here
			System.exit(1);
		}
		double boiling = boilingResult.trim().equals("X") ? BAD_RESULT : Double.parseDouble(boilingResult);
		if(melting >= boiling) {
			// This is not physically possible, and indicates a case where the calculation model is inaccurate
			melting = BAD_RESULT;
			boiling = BAD_RESULT;
		}
		Pair<Double, Double> pair = new Pair<Double,Double>(melting, boiling);
		if(DEBUG) System.out.println("MMB:"+"pair:"+pair);
		return pair;
	}
	
	public static void main(String[] args) {
		Parameters.initializeParameterCollections(new String[0]);
		String[] examples = {"C-C-C(=C)-O", "C-C-N-C(=C)-O", "C(#C)-C-N-C(=C)-O", "C-C(-C)-N-C(-C)-O", "N-C-N=C-C-O", "C1-C-N-C(=C1)-N"};
		
		for(String exampleSMILES : examples) {
			System.out.println(exampleSMILES);
			SMILESStringGenotype smiles = new SMILESStringGenotype(exampleSMILES);
			Pair<Double, Double> pair = MoleculeMeltingAndBoilingPointProcess.smilesMeltingAndBoilingPoints(smiles);
			System.out.println(smiles.getPhenotype() + "\nmelting point: "+pair.t1+"\nboiling point: "+pair.t2);
		}
		
		MoleculeMeltingAndBoilingPointProcess.terminateMelingBoilingPointProcess();
	}
	
	
}
