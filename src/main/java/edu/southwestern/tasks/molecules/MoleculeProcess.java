package edu.southwestern.tasks.molecules;

import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;

import edu.southwestern.tasks.mario.gan.Comm;
import edu.southwestern.util.PythonUtil;

/**
 * This code launches and manages an external executable's input/output in a manner similar
 * to edu.southwestern.tasks.mario.gan.GANProcess. This file is focused on managing processes
 * used in the evolution of molecules. In principle, a lot of this code should generalize
 * to any executable, but it is intended to run compiled FORTRAN programs.
 * 
 * @author schrum2
 *
 */
public class MoleculeProcess extends Comm {

	public static final String FORTRAN_BASE_PATH = "." + File.separator + "src" + File.separator + "main" + File.separator + "fortran" + File.separator + "molecules" + File.separator;
	
	public String executableName;
	
	public MoleculeProcess(String exe) {
		executableName = exe;
	}
	
	/**
	 * Fortran process running in background, ready to accept input
	 */
	@Override
	public void start() {
		try {
			launchExecutable();
			initBuffers();
			printInfoMsg(this.threadName + " has started");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Launch the compiled executable
	 */
	private void launchExecutable() {
		// Run program with model architecture and weights specified as parameters
		ProcessBuilder builder = new ProcessBuilder(FORTRAN_BASE_PATH + executableName);
		builder.redirectError(Redirect.INHERIT); // Standard error will print to console
		try {
			System.out.println(builder.command());
			this.process = builder.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// A quick test
	public static void main(String[] args) throws IOException {
		MoleculeProcess mutate = new MoleculeProcess("SMILESMutate.exe");
		mutate.start();

		String exampleSMILES = "C-C-N-C(=C)-O";
		int mutationNumber = 3;
		
		mutate.commSend("131 246"); // Two random seeds
		mutate.commSend(exampleSMILES.length()+" "+mutationNumber);    // Input string length and mutation type
		mutate.commSend(exampleSMILES);
		
		System.out.println(mutate.commRecv()); // Result length
		System.out.println(mutate.commRecv()); // Result string
		
		mutate.commSend("-1 -1"); // special inputs to terminate process
		
	}
}
