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
	 * from the human subject study conducted in 2020 (published in GECCO 2021). Note that 
	 * this command assumes the availability of saved level data from the study, stored in 
	 * the location specified by the basePath variable.
	 * 
	 * @param args Empty array ... just use default parameters
	 * @throws FileNotFoundException 
	 * @throws Exception
	 */
	public static void main(String[] args) throws FileNotFoundException {
		game = GAME.MEGA_MAN;
		final String basePath = "data/EvolvedMegaManLevels2020/AStarDistAndConnectivity";
		// To suppress output from file loading
		PrintStream original = System.out;
		int rows = getRows();
		int columns = getColumns();
		int numLevels = 10; // Num original VGLC levels (levels in Mega Man 1)
		List<List<List<Integer>>> allVGLCSegments = new ArrayList<>();
		List<List<List<Integer>>> allOneGANSegments = new ArrayList<>();
		List<List<List<Integer>>> allSevenGANSegments = new ArrayList<>();
		List<List<List<Integer>>> allSevenGANLLSegments = new ArrayList<>();
		List<List<List<Integer>>> allSevenGANLRSegments = new ArrayList<>();
		List<List<List<Integer>>> allSevenGANULSegments = new ArrayList<>();
		List<List<List<Integer>>> allSevenGANURSegments = new ArrayList<>();
		List<List<List<Integer>>> allSevenGANCornerSegments = new ArrayList<>();

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
		double vglcMin = 0;
		double vglcMax = 0;
		for(int i = 1;i<=10;i++) {
			double novelty = originalNovelties.get(name+i);
			System.out.println(novelty);
			vglcStream.println(novelty);
			vglcLevelAverage += novelty;
			if(vglcMin==0||novelty<vglcMin) vglcMin = novelty;
			if(novelty>vglcMax) vglcMax = novelty;
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
		double oneGANMin = 0;
		double oneGANMax = 0;
		PrintStream graphGrammarStream = new PrintStream(new File("MegaMan-OneGAN.csv"));
		double oneGANAverage = 0;
		for(int i = 0; i < 30; i++) {
			double novelty = oneGANNovelties.get(name+i);
			System.out.println(novelty);
			graphGrammarStream.println(novelty);
			oneGANAverage += novelty;
			if(oneGANMin==0||novelty<oneGANMin) oneGANMin = novelty;
			if(novelty>oneGANMax) oneGANMax = novelty;
		}
		graphGrammarStream.close();
		// Average novelty of Graph Grammar dungeons from study
		oneGANAverage /= 30;
		
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
			MegaManVGLCUtil.upAndDownTrainingData(sevenGANlevel);

			allSevenGANSegments.addAll(segmentList); // Collect all rooms for final comparison at the end
			sevenGANNovelties.put(name+i, averageSegmentNovelty(segmentList));
		}
		
		allSevenGANLLSegments = MegaManVGLCUtil.getLL();
		allSevenGANLRSegments = MegaManVGLCUtil.getLR();
		allSevenGANULSegments = MegaManVGLCUtil.getUL();
		allSevenGANURSegments = MegaManVGLCUtil.getUR();
		
		allSevenGANCornerSegments.addAll(allSevenGANLLSegments);
		allSevenGANCornerSegments.addAll(allSevenGANLRSegments);
		allSevenGANCornerSegments.addAll(allSevenGANURSegments);
		allSevenGANCornerSegments.addAll(allSevenGANULSegments);
		
		
		
		// Resume outputting text
		System.setOut(original);
		

		System.out.println("Novelty of SevenGAN Levels");
		double sevenGANMin = 0;
		double sevenGANMax = 0;
		PrintStream sevenGANStream = new PrintStream(new File("MegaMan-SevenGAN.csv"));
		double sevenGANAverage = 0;
		for(int i = 0; i < 30; i++) {
			double novelty = sevenGANNovelties.get(name+i);
			System.out.println(novelty);
			sevenGANStream.println(novelty);
			sevenGANAverage += novelty;
			if(sevenGANMin==0||novelty<sevenGANMin) sevenGANMin = novelty;
			if(novelty>sevenGANMax) sevenGANMax = novelty;
		}
		sevenGANStream.close();
		// Average novelty of Graph GAN dungeons from study
		sevenGANAverage /= 30;
	
		System.out.println();
		System.out.println("VGLC Average: "+vglcLevelAverage);
		System.out.println("OneGAN Average: "+oneGANAverage);
		System.out.println("SevenGAN Average: "+sevenGANAverage);
		System.out.println("VGLC Min: "+vglcMin);
		System.out.println("OneGAN Min: "+oneGANMin);
		System.out.println("SevenGAN Min: "+sevenGANMin);
		System.out.println("VGLC Max: "+vglcMax);
		System.out.println("OneGAN Max: "+oneGANMax);
		System.out.println("SevenGAN Max: "+sevenGANMax);
		
		HashSet<List<List<Integer>>> noDuplicatesSet = new HashSet<>(allVGLCSegments);
		List<List<List<Integer>>> noDuplicatesList = new LinkedList<>();
		noDuplicatesList.addAll(noDuplicatesSet);
		
		double[] originalRoomsNoveltySet = segmentNovelties(noDuplicatesList);
		PrintStream originalPS = new PrintStream(new File("VGLCSegmentsSet.csv"));
		for(Double d : originalRoomsNoveltySet) {
			originalPS.println(d);
		}
		originalPS.close();

		double[] originalRoomsNoveltyAll = segmentNovelties(allVGLCSegments);
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

		double[] oneGANSegmentNoveltySet = segmentNovelties(noDuplicatesList);
		PrintStream graphPS = new PrintStream(new File("OneGANSegmentsSet.csv"));
		for(Double d : oneGANSegmentNoveltySet) {
			graphPS.println(d);
		}
		graphPS.close();

		double[] oneGANsegmentsNoveltyAll = segmentNovelties(allOneGANSegments);
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

		double[] sevenGANSegmentsNoveltySet = segmentNovelties(noDuplicatesList);
		PrintStream ganPS = new PrintStream(new File("SevenGANSegmentsSet.csv"));
		for(Double d : sevenGANSegmentsNoveltySet) {
			ganPS.println(d);
		}
		ganPS.close();

		double[] sevenGANSegmentsNoveltyAll = segmentNovelties(allSevenGANSegments);
		ganPS = new PrintStream(new File("SevenGANSegmentsAll.csv"));
		for(Double d : sevenGANSegmentsNoveltyAll) {
			ganPS.println(d);
		}
		ganPS.close();

		
		System.out.println(noDuplicatesList.size());
		System.out.println("Average Set of SevenGAN Segments: " + LevelNovelty.averageSegmentNovelty(noDuplicatesList));
		System.out.println(allSevenGANSegments.size());
		System.out.println("Average All SevenGAN Segments: " + LevelNovelty.averageSegmentNovelty(allSevenGANSegments));
		
		
		
		
		System.out.println();
		
		
		
		
		
		
		
		
		noDuplicatesSet = new HashSet<>(allSevenGANLLSegments);
		noDuplicatesList = new LinkedList<>();
		noDuplicatesList.addAll(noDuplicatesSet);

		double[] sevenGANLLSegmentsNoveltySet = segmentNovelties(noDuplicatesList);
		PrintStream ganLLPS = new PrintStream(new File("SevenGANLLSegmentsSet.csv"));
		for(Double d : sevenGANLLSegmentsNoveltySet) {
			ganLLPS.println(d);
		}
		ganLLPS.close();

		double[] sevenGANLLSegmentsNoveltyAll = segmentNovelties(allSevenGANLLSegments);
		ganLLPS = new PrintStream(new File("SevenGANLLSegmentsAll.csv"));
		for(Double d : sevenGANLLSegmentsNoveltyAll) {
			ganLLPS.println(d);
		}
		ganLLPS.close();

		System.out.println("Num Segments LL: "+allSevenGANLLSegments.size());
		System.out.println("Num Distinct Segments LL: "+noDuplicatesList.size());
		
		System.out.println("Average All SevenGANLL Segments: " + LevelNovelty.averageSegmentNovelty(allSevenGANLLSegments));
		System.out.println("Average Set of SevenGANLL Segments: " + LevelNovelty.averageSegmentNovelty(noDuplicatesList));
		
		
		System.out.println();
		
		
		
		
		
		
		
		
		
		noDuplicatesSet = new HashSet<>(allSevenGANLRSegments);
		noDuplicatesList = new LinkedList<>();
		noDuplicatesList.addAll(noDuplicatesSet);

		double[] sevenGANLRSegmentsNoveltySet = segmentNovelties(noDuplicatesList);
		PrintStream GANLRPS = new PrintStream(new File("SevenGANLRSegmentsSet.csv"));
		for(Double d : sevenGANLRSegmentsNoveltySet) {
			GANLRPS.println(d);
		}
		GANLRPS.close();

		double[] sevenGANLRSegmentsNoveltyAll = segmentNovelties(allSevenGANLRSegments);
		GANLRPS = new PrintStream(new File("SevenGANLRSegmentsAll.csv"));
		for(Double d : sevenGANLRSegmentsNoveltyAll) {
			GANLRPS.println(d);
		}
		GANLRPS.close();

		System.out.println("Num Segments LR: "+allSevenGANLRSegments.size());
		System.out.println("Num Distinct Segments LR: "+noDuplicatesList.size());
		
		
		System.out.println("Average All SevenGANLR Segments: " + LevelNovelty.averageSegmentNovelty(allSevenGANLRSegments));
		System.out.println("Average Set of SevenGANLR Segments: " + LevelNovelty.averageSegmentNovelty(noDuplicatesList));
		
		System.out.println();
		
		
		
		
		
		
		
		
		
		noDuplicatesSet = new HashSet<>(allSevenGANURSegments);
		noDuplicatesList = new LinkedList<>();
		noDuplicatesList.addAll(noDuplicatesSet);

		double[] sevenGANURSegmentsNoveltySet = segmentNovelties(noDuplicatesList);
		PrintStream GANURPS = new PrintStream(new File("SevenGANURSegmentsSet.csv"));
		for(Double d : sevenGANURSegmentsNoveltySet) {
			GANURPS.println(d);
		}
		GANURPS.close();

		double[] sevenGANURSegmentsNoveltyAll = segmentNovelties(allSevenGANURSegments);
		GANURPS = new PrintStream(new File("SevenGANURSegmentsAll.csv"));
		for(Double d : sevenGANURSegmentsNoveltyAll) {
			GANURPS.println(d);
		}
		GANURPS.close();

		System.out.println("Num Segments UR: "+allSevenGANURSegments.size());
		System.out.println("Num Distinct Segments UR: "+noDuplicatesList.size());
		
		
		System.out.println("Average All SevenGANUR Segments: " + LevelNovelty.averageSegmentNovelty(allSevenGANURSegments));
		System.out.println("Average Set of SevenGANUR Segments: " + LevelNovelty.averageSegmentNovelty(noDuplicatesList));
		System.out.println();
		
		
		
		
		noDuplicatesSet = new HashSet<>(allSevenGANULSegments);
		noDuplicatesList = new LinkedList<>();
		noDuplicatesList.addAll(noDuplicatesSet);

		double[] sevenGANULSegmentsNoveltySet = segmentNovelties(noDuplicatesList);
		PrintStream GANULPS = new PrintStream(new File("SevenGANULSegmentsSet.csv"));
		for(Double d : sevenGANULSegmentsNoveltySet) {
			GANULPS.println(d);
		}
		GANULPS.close();

		double[] sevenGANULSegmentsNoveltyAll = segmentNovelties(allSevenGANULSegments);
		GANULPS = new PrintStream(new File("SevenGANULSegmentsAll.csv"));
		for(Double d : sevenGANULSegmentsNoveltyAll) {
			GANULPS.println(d);
		}
		GANULPS.close();

		System.out.println("Num Segments UL: "+allSevenGANULSegments.size());
		System.out.println("Num Distinct Segments UL: "+noDuplicatesList.size());
		
		
		System.out.println("Average All SevenGANUL Segments: " + LevelNovelty.averageSegmentNovelty(allSevenGANULSegments));
		System.out.println("Average Set of SevenGANUL Segments: " + LevelNovelty.averageSegmentNovelty(noDuplicatesList));
		
		System.out.println();
		noDuplicatesSet = new HashSet<>(allSevenGANCornerSegments);
		noDuplicatesList = new LinkedList<>();
		noDuplicatesList.addAll(noDuplicatesSet);

		double[] sevenGANCornerSegmentsNoveltySet = segmentNovelties(noDuplicatesList);
		PrintStream GANCornerPS = new PrintStream(new File("SevenGANCornerSegmentsSet.csv"));
		for(Double d : sevenGANCornerSegmentsNoveltySet) {
			GANCornerPS.println(d);
		}
		GANCornerPS.close();

		double[] sevenGANCornerSegmentsNoveltyAll = segmentNovelties(allSevenGANCornerSegments);
		GANCornerPS = new PrintStream(new File("SevenGANCornerSegmentsAll.csv"));
		for(Double d : sevenGANCornerSegmentsNoveltyAll) {
			GANCornerPS.println(d);
		}
		GANCornerPS.close();

		System.out.println("Num Corner Segments: "+allSevenGANCornerSegments.size());
		System.out.println("Num Distinct Corner Segments: "+noDuplicatesList.size());
		
		
		System.out.println("Average All SevenGANCorner Segments: " + LevelNovelty.averageSegmentNovelty(allSevenGANCornerSegments));
		System.out.println("Average Set of SevenGANCorner Segments: " + LevelNovelty.averageSegmentNovelty(noDuplicatesList));
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		String path = basePath + "SevenGAN" + 1 + "SolutionPath.txt";
		File file = new File(path);
		List<List<Integer>> ganLevel = MegaManVGLCUtil.convertLevelFromIntText(file);
		List<List<List<Integer>>> segmentList = partitionSegments(ganLevel, rows, columns);
		System.out.println(getAverageSolutionPathPercent(segmentList));
		
		path = basePath + "OneGAN" + 8 + "SolutionPath.txt";
		file = new File(path);
		ganLevel = MegaManVGLCUtil.convertLevelFromIntText(file);
		segmentList = partitionSegments(ganLevel, rows, columns);
		System.out.println(getAverageSolutionPathPercent(segmentList));
		
		
		path = basePath + "OneGAN" + 15 + "SolutionPath.txt";
		file = new File(path);
		ganLevel = MegaManVGLCUtil.convertLevelFromIntText(file);
		segmentList = partitionSegments(ganLevel, rows, columns);
		System.out.println(getAverageSolutionPathPercent(segmentList));
		
		
		
	}
}
