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
	public static Pair<Double,Double> smilesMeltingAndBoilingPoints(SMILESStringGenotype smiles) {
		MoleculeMeltingAndBoilingPointProcess process = getMeltingBoilingPointProcess();
		String smilesString = smiles.getPhenotype();

		try {
			process.commSend(""+smilesString.length());
			process.commSend(smilesString);	
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("MoleculeMeltingAndBoilingPointProcess failed");
			System.exit(1);
		}
		double melting = Double.parseDouble(process.commRecv());
		double boiling = Double.parseDouble(process.commRecv());
		assert melting < boiling;
		return new Pair<Double,Double>(melting, boiling);
	}
	
	public static void main(String[] args) {
		Parameters.initializeParameterCollections(new String[0]);
		String[] examples = {"C-C-C(=C)-O", "C-C-N-C(=C)-O", "C(#C)-C-N-C(=C)-O", "C-C(-C)-N-C(-C)-O", "N-C-N=C-C-O", "C1-C-N-C(=C1)-N"};
		
		for(String exampleSMILES : examples) {
			SMILESStringGenotype smiles = new SMILESStringGenotype(exampleSMILES);
			Pair<Double, Double> pair = MoleculeMeltingAndBoilingPointProcess.smilesMeltingAndBoilingPoints(smiles);
			System.out.println(smiles.getPhenotype() + "\nmelting point: "+pair.t1+"\nboiling point: "+pair.t2);
		}
		
		MoleculeMeltingAndBoilingPointProcess.terminateMelingBoilingPointProcess();
	}
}
