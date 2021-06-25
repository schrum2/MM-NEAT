package edu.southwestern.tasks.gvgai.zelda;

import java.awt.Point;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.scores.Score;
import edu.southwestern.tasks.mario.gan.GANProcess;
import edu.southwestern.tasks.mario.gan.reader.JsonReader;
import edu.southwestern.util.datastructures.ArrayUtil;
import wox.serial.Easy;

public class ZeldaGANUtil {

	public static final int ZELDA_GAN_ORIGINAL_TILE_NUMBER = 4;
	public static final int ZELDA_GAN_EXPANDED_TILE_NUMBER = 6;
	public static final int ZELDA_GAN_REDUCED_TILE_NUMBER = 3;
	
	/**
	 * Generate a Zelda room with the GAN, and then convert it to a String representation
	 * that GVG-AI can turn into a level to play.
	 * @param latentVector Vector that generates level
	 * @param startLocation Where to place Zelda avatar in the level
	 * @return String representation
	 */
	public static String[] generateGVGAILevelFromGAN(double[] latentVector, Point startLocation) {
		List<List<Integer>> room = generateOneRoomListRepresentationFromGAN(latentVector);
		return ZeldaVGLCUtil.convertZeldaRoomListtoGVGAI(room, startLocation);
	}

	/**
	 * Get one room in list form from a latent vector using the GAN.
	 * The GANProcess type must be set to ZELDA before executing this method.
	 * @param latentVector Latent vector to generate room
	 * @return One room in list form
	 */
	public static List<List<Integer>> generateOneRoomListRepresentationFromGAN(double[] latentVector) {
		List<List<List<Integer>>> roomInList = getRoomListRepresentationFromGAN(latentVector);
		List<List<Integer>> result = roomInList.get(0); // Only contains one room
		if(result.size() > result.get(0).size()) {
			// If taller than wide, then rotate
			result = ArrayUtil.rotateCounterClockwise(result);
		}
		// This means the encoding will use 2 to represent water instead of enemies. Easier to fix by changing 2 to 5 here.
		if(Parameters.parameters.stringParameter("zeldaGANModel").startsWith("ZeldaDungeonsAll3Tiles")) {
			for(List<Integer> row : result) {
				ListIterator<Integer> itr = row.listIterator();
				while(itr.hasNext()) {
					Integer value = itr.next();
					if(value.equals(new Integer(2))) {	// 2 is not an enemy in this representation
						itr.set(5); // It is water, which normally has a value of 5
					}
				}
			}
		}
		return result;
	}
	
	public static List<List<List<Integer>>> getRoomListRepresentationFromGAN(double[] latentVector){
		assert GANProcess.type.equals(GANProcess.GAN_TYPE.ZELDA);
		latentVector = GANProcess.mapArrayToOne(latentVector); // Range restrict the values
		// Generate room from vector
		String oneRoom;
		synchronized(GANProcess.getGANProcess()) { // Make sure the response corresponds to the sent message
			try {
				GANProcess.getGANProcess().commSend("[" + Arrays.toString(latentVector) + "]");
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1); // Cannot continue without the GAN process
			}
			oneRoom = GANProcess.getGANProcess().commRecv(); // Response to command just sent
		}
        oneRoom = "["+oneRoom+"]"; // Wrap room in another json array
        // Create one room in a list
        List<List<List<Integer>>> roomInList = JsonReader.JsonToInt(oneRoom);
        // Height of first room is greater than width of first room, and all are same size
		if(roomInList.get(0).size() > roomInList.get(0).get(0).size()) {
			for(int i = 0; i < roomInList.size(); i++) {
				//System.out.println("HERE:"+roomInList.get(i));
				roomInList.set(i, ArrayUtil.rotateCounterClockwise(roomInList.get(i)));
			}
		}        
        return roomInList;
	}
	
	
	/**
	 * For quick tests
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {
		int size = 10;
		GANProcess.type = GANProcess.GAN_TYPE.ZELDA;
		Parameters.initializeParameterCollections(new String[] {"zeldaGANUsesOriginalEncoding:false","GANInputSize:"+size,"zeldaGANModel:ZeldaDungeonsAll3Tiles_10000_10.pth"});
		//MMNEAT.loadClasses();
		
		String path = "G:\\My Drive\\Research\\2020-GECCO-InteractiveGAN-Dagstuhl\\UserData\\zelda-vv-modl-23-01-20\\Evolve14\\bestObjectives"; // <--- Change this to the specific directory you want to check
		File dir = new File(path);
		File[] xmlGenotypes = dir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.getName().endsWith(".xml");
			}
		});
		
		for(File f: xmlGenotypes) {
			@SuppressWarnings("unchecked")
			Score<ArrayList<Double>> s = (Score<ArrayList<Double>>) Easy.load(f.getAbsolutePath());
			ArrayList<Double> list = s.individual.getPhenotype();
			double[] latent = ArrayUtil.doubleArrayFromList(list);
			List<List<List<Integer>>> listLevel = getRoomListRepresentationFromGAN(latent);
			
			// Do whatever you want with this list representation
			PrintStream ps = new PrintStream(new File(path+"\\"+f.getName()+"level.txt"));
			ps.println(listLevel);
		}
		
		GANProcess.terminateGANProcess();
	}		

	
//	public static void main(String[] args) throws FileNotFoundException {
//		
//		VGDLFactory.GetInstance().init();
//		VGDLRegistry.GetInstance().init();
//
//		String game = "zelda";
//		String gamesPath = "data/gvgai/examples/gridphysics/";
//		String game_file = gamesPath + game + ".txt";
//		int playerID = 0;
//		int seed = 0;
//
//		GANProcess.type = GANProcess.GAN_TYPE.ZELDA;
//		double[] latentVector = RandomNumbers.randomArray(size);
//		String[] level = generateGVGAILevelFromGAN(latentVector, new Point(8,8));
//		
//		for(String line : level) {
//			System.out.println(line);
//		}
//		
//		Agent agent = new Agent();
//		agent.setup(null, 0, true); // null = no log, true = human 
//
//		Game toPlay = new VGDLParser().parseGame(game_file); // Initialize the game
//		GVGAIUtil.runOneGame(toPlay, level, true, agent, seed, playerID);
//
//	}
}
