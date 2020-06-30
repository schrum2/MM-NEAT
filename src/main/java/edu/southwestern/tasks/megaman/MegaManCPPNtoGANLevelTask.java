package edu.southwestern.tasks.megaman;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.networks.Network;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.mario.gan.GANProcess;
import edu.southwestern.tasks.megaman.gan.MegaManGANUtil;
import edu.southwestern.util.PythonUtil;
import edu.southwestern.util.datastructures.ArrayUtil;

public class MegaManCPPNtoGANLevelTask<T extends Network> extends MegaManLevelTask<T>{
	public static GANProcess ganProcessHorizontal = null;
	public static GANProcess ganProcessUp = null;
	public static GANProcess ganProcessDown = null;
	
	public MegaManCPPNtoGANLevelTask(){
		super();
		
		PythonUtil.setPythonProgram();
		//super();
		//if(Parameters.parameters.booleanParameter("useThreeGANsMegaMan")) {
			//GANProcess.terminateGANProcess();
			ganProcessHorizontal = MegaManGANUtil.initializeGAN("MegaManGANHorizontalModel");
			ganProcessDown = MegaManGANUtil.initializeGAN("MegaManGANDownModel");
			ganProcessUp = MegaManGANUtil.initializeGAN("MegaManGANUpModel");
	//		ganProcessDown = new GANProcess(GANProcess.PYTHON_BASE_PATH+"MegaManGAN"+ File.separator + Parameters.parameters.stringParameter("MegaManGANDownModel"), 
	//		Parameters.parameters.integerParameter("GANInputSize"), 
	//		/*Parameters.parameters.stringParameter("MegaManGANModel").startsWith("HORIZONTALONLYMegaManAllLevel") ? */MegaManGANUtil.MEGA_MAN_ALL_TERRAIN /*: MegaManGANUtil.MEGA_MAN_FIRST_LEVEL_ALL_TILES*/,
	//		GANProcess.MEGA_MAN_OUT_WIDTH, GANProcess.MEGA_MAN_OUT_HEIGHT);
			MegaManGANUtil.startGAN(ganProcessUp);
			MegaManGANUtil.startGAN(ganProcessDown);
			MegaManGANUtil.startGAN(ganProcessHorizontal);
	}
	@Override
	public List<List<Integer>> getMegaManLevelListRepresentationFromGenotype(Genotype<T> individual) {
		List<List<Integer>> level = MegaManCPPNtoGANUtil.cppnToMegaManLevel(ganProcessHorizontal, ganProcessDown, ganProcessUp, individual.getPhenotype(), Parameters.parameters.integerParameter("megaManGANLevelChunks"), ArrayUtil.doubleOnes(MegaManCPPNtoGANLevelBreederTask.SENSOR_LABELS.length));

		return level;
	}
	
	public static void main(String[] args) {
		try {

			MMNEAT.main(new String[]{"runNumber:8","randomSeed:8","watch:true","trials:1","mu:10","base:megamancppntogan",
					"log:MegaManCPPNtoGAN-DistPercent","saveTo:DistPercent","megaManGANLevelChunks:10",
					"megaManAllowsSimpleAStarPath:true", "megaManAllowsConnectivity:true",
					"MegaManGANUpModel:VERTICALONLYUPUniqueEnemiesMegaManAllLevelsBut7With30TileTypes_5_Epoch5000.pth", 
					"MegaManGANDownModel:VERTICALONLYDOWNUniqueEnemiesMegaManAllLevelsBut7With30TileTypes_5_Epoch5000.pth", 
					"MegaManGANHorizontalModel:HORIZONTALONLYUniqueEnemiesMegaManAllLevelsBut7With30TileTypes_5_Epoch5000.pth",
					"maxGens:500","io:true","netio:true","GANInputSize:5","mating:true","fs:false",
					"task:edu.southwestern.tasks.megaman.MegaManCPPNtoGANLevelTask","cleanOldNetworks:false",
					"allowMultipleFunctions:true","ftype:0","netChangeActivationRate:0.3","cleanFrequency:-1",
					"simplifiedInteractiveInterface:false","recurrency:false","saveAllChampions:true",
					"cleanOldNetworks:false","includeFullSigmoidFunction:true","includeFullGaussFunction:true",
					"includeCosineFunction:true","includeGaussFunction:false","includeIdFunction:true",
					"includeTriangleWaveFunction:true","includeSquareWaveFunction:true","includeFullSawtoothFunction:true",
					"includeSigmoidFunction:false","includeAbsValFunction:false","includeSawtoothFunction:false"});
		} catch (FileNotFoundException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}
	@Override
	public HashMap<String, Integer> findMiscSegments(List<List<Integer>> level) {
		// TODO Auto-generated method stub
		

		return MegaManCPPNtoGANUtil.findMiscSegments(level);
	}

}
