package edu.southwestern.tasks.megaman;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.util.file.NullPrintStream;


public class MegaManLevelNovelty extends LevelNovelty{
	/**
	 * Perform an analysis of the novelty of of various dungeons from the original game and
	 * from the human subject study conducted in 2020. Note that this command assumes the 
	 * availability of saved level data from the study, stored in the location specified
	 * by the basePath variable.
	 * 
	 * @param args Empty array ... just use default parameters
	 * @throws FileNotFoundException 
	 * @throws Exception
	 */
	public static void main(String[] args) throws FileNotFoundException {
		game = GAME.MEGA_MAN;
		final String basePath = "megamanlevels/textLevels/AStarDistAndConnectivity";
		// To suppress output from file loading
		PrintStream original = System.out;
		int rows = getRows();
		int columns = getColumns();
		int numLevels = 10;
		List<List<List<Integer>>> allVGLCSegments = new ArrayList<>();
		List<List<List<Integer>>> allOneGANSegments = new ArrayList<>();
		List<List<List<Integer>>> allSevenGANSegments = new ArrayList<>();
		
		
		Parameters.initializeParameterCollections(args);
		HashMap<String,Double> originalNovelties = new HashMap<String,Double>();
		String name = "megaman_1_";
		for(int i = 1;i<=numLevels;i++) {			
			String file = name+i+".txt";
			List<List<Integer>> vglcLevel = MegaManVGLCUtil.convertMegamanVGLCtoListOfLists(MegaManVGLCUtil.MEGAMAN_LEVEL_PATH+file);
			List<List<List<Integer>>> segmentList = partitionSegments(vglcLevel, rows, columns);
			allVGLCSegments.addAll(segmentList); // Collect all rooms for final comparison at the end
			originalNovelties.put(name+i, averageSegmentNovelty(segmentList));		
		}
		
		// Resume outputting text
		System.setOut(original);
		
		System.out.println("Novelty of VGLC Levels");
		PrintStream vglcStream = new PrintStream(new File("MegaMan-VGLC.csv"));
		double vglcLevelAverage = 0;
		for(int i = 1;i<=10;i++) {
			double novelty = originalNovelties.get(name+i);
			System.out.println(novelty);
			vglcStream.println(novelty);
			vglcLevelAverage += novelty;
		}
		vglcStream.close();
		// Average novelty of dungeons from original game
		vglcLevelAverage /= numLevels; 

		
		// Mute output again
		System.setOut(new NullPrintStream());
		//For OneGAN
		
		name = "OneGAN";
		HashMap<String,Double> oneGANNovelties = new HashMap<String,Double>();
		for(int i = 0; i < 30; i++) {
			String path = basePath + "OneGAN" + i + ".txt";
			File file = new File(path);
			List<List<Integer>> oneGANlevel = MegaManVGLCUtil.convertLevelFromIntText(file);
			List<List<List<Integer>>> segmentList = partitionSegments(oneGANlevel, rows, columns);
			allOneGANSegments.addAll(segmentList); // Collect all rooms for final comparison at the end
			oneGANNovelties.put(name+i, averageSegmentNovelty(segmentList));			
		}
		
		// Resume outputting text
		System.setOut(original);
		
		System.out.println("Novelty of OneGAN levels");
		PrintStream graphGrammarStream = new PrintStream(new File("MegaMan-OneGAN.csv"));
		double oneGANGrammarAverage = 0;
		for(int i = 0; i < 30; i++) {
			double novelty = oneGANNovelties.get(name+i);
			System.out.println(novelty);
			graphGrammarStream.println(novelty);
			oneGANGrammarAverage += novelty;
		}
		graphGrammarStream.close();
		// Average novelty of Graph Grammar dungeons from study
		oneGANGrammarAverage /= 30;
		
		// Mute output again
		System.setOut(new NullPrintStream());
		
		name = "SevenGAN";
		//For SevenGAN
		HashMap<String,Double> sevenGANNovelties = new HashMap<String, Double>();
		for(int i = 0; i < 30; i++) {
			String path = basePath + "SevenGAN" + i + ".txt";
			File file = new File(path);
			List<List<Integer>> sevenGANlevel = MegaManVGLCUtil.convertLevelFromIntText(file);
			List<List<List<Integer>>> segmentList = partitionSegments(sevenGANlevel, rows, columns);
			allSevenGANSegments.addAll(segmentList); // Collect all rooms for final comparison at the end
			sevenGANNovelties.put(name+i, averageSegmentNovelty(segmentList));
		}
		// Resume outputting text
		System.setOut(original);
		
		System.out.println("Novelty of SevenGAN Levels");
		PrintStream sevenGANStream = new PrintStream(new File("MegaMan-SevenGAN.csv"));
		double sevenGANAverage = 0;
		for(int i = 0; i < 30; i++) {
			double novelty = sevenGANNovelties.get(name+i);
			System.out.println(novelty);
			sevenGANStream.println(novelty);
			sevenGANAverage += novelty;
		}
		sevenGANStream.close();
		// Average novelty of Graph GAN dungeons from study
		sevenGANAverage /= 30;
	
		System.out.println();
		System.out.println("VGLC Average: "+vglcLevelAverage);
		System.out.println("OneGAN Average: "+oneGANGrammarAverage);
		System.out.println("SevenGAN Average: "+sevenGANAverage);
		
		
		HashSet<List<List<Integer>>> noDuplicatesSet = new HashSet<>(allVGLCSegments);
		List<List<List<Integer>>> noDuplicatesList = new LinkedList<>();
		noDuplicatesList.addAll(noDuplicatesSet);
		
		double[] originalRoomsNoveltySet = roomNovelties(noDuplicatesList);
		PrintStream originalPS = new PrintStream(new File("VGLCSegmentsSet.csv"));
		for(Double d : originalRoomsNoveltySet) {
			originalPS.println(d);
		}
		originalPS.close();

		double[] originalRoomsNoveltyAll = roomNovelties(allVGLCSegments);
		originalPS = new PrintStream(new File("VGLCSegmentsAll.csv"));
		for(Double d : originalRoomsNoveltyAll) {
			originalPS.println(d);
		}
		originalPS.close();

		
		System.out.println(noDuplicatesList.size());
		System.out.println("Average Set of Original Segments: " + LevelNovelty.averageSegmentNovelty(noDuplicatesList));
		System.out.println(allVGLCSegments.size());
		System.out.println("Average All Original Segments: " + LevelNovelty.averageSegmentNovelty(allVGLCSegments));
		
		noDuplicatesSet = new HashSet<>(allOneGANSegments);
		noDuplicatesList = new LinkedList<>();
		noDuplicatesList.addAll(noDuplicatesSet);

		double[] oneGANSegmentNoveltySet = roomNovelties(noDuplicatesList);
		PrintStream graphPS = new PrintStream(new File("OneGANSegmentsSet.csv"));
		for(Double d : oneGANSegmentNoveltySet) {
			graphPS.println(d);
		}
		graphPS.close();

		double[] oneGANsegmentsNoveltyAll = roomNovelties(allOneGANSegments);
		graphPS = new PrintStream(new File("OneGANSegmentsAll.csv"));
		for(Double d : oneGANsegmentsNoveltyAll) {
			graphPS.println(d);
		}
		graphPS.close();

		
		System.out.println(noDuplicatesList.size());
		System.out.println("Average Set of OneGAN segments: " + LevelNovelty.averageSegmentNovelty(noDuplicatesList));
		System.out.println(allOneGANSegments.size());
		System.out.println("Average All OneGAN segments: " + LevelNovelty.averageSegmentNovelty(allOneGANSegments));

		noDuplicatesSet = new HashSet<>(allSevenGANSegments);
		noDuplicatesList = new LinkedList<>();
		noDuplicatesList.addAll(noDuplicatesSet);

		double[] sevenGANSegmentsNoveltySet = roomNovelties(noDuplicatesList);
		PrintStream ganPS = new PrintStream(new File("SevenGANSegmentsSet.csv"));
		for(Double d : sevenGANSegmentsNoveltySet) {
			ganPS.println(d);
		}
		ganPS.close();

		double[] sevenGANSegmentsNoveltyAll = roomNovelties(allSevenGANSegments);
		ganPS = new PrintStream(new File("SevenGANRoomsAll.csv"));
		for(Double d : sevenGANSegmentsNoveltyAll) {
			ganPS.println(d);
		}
		ganPS.close();

		
		System.out.println(noDuplicatesList.size());
		System.out.println("Average Set of SevenGAN Segments: " + LevelNovelty.averageSegmentNovelty(noDuplicatesList));
		System.out.println(allSevenGANSegments.size());
		System.out.println("Average All SevenGAN Segments: " + LevelNovelty.averageSegmentNovelty(allSevenGANSegments));
	}
}
