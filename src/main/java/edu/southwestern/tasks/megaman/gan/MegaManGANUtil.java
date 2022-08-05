package edu.southwestern.tasks.megaman.gan;

import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import edu.southwestern.networks.Network;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.mario.gan.GANProcess;
import edu.southwestern.tasks.mario.gan.reader.JsonReader;
import edu.southwestern.tasks.megaman.MegaManTrackSegmentType;
import edu.southwestern.tasks.megaman.MegaManVGLCUtil;
import edu.southwestern.tasks.megaman.astar.MegaManState;
import edu.southwestern.tasks.megaman.levelgenerators.MegaManGANGenerator;
import edu.southwestern.util.datastructures.Pair;

public class MegaManGANUtil {
	
	//Static MegaManGenerator across all files
	private static MegaManGANGenerator megaManGenerator;
	
	public static final int MEGA_MAN_ALL_TERRAIN = 7; //number of tiles in MegaMan
	public static final int MEGA_MAN_ONE_ENEMY = 12; //number of tiles in MegaMan
	public static final int MEGA_MAN_LEVEL_WIDTH = 16;
	public static final int MEGA_MAN_LEVEL_HEIGHT = 14;

	/**
	 * Initializes a GAN of the type specified
	 * @param modelType The type of model the GAN will be
	 * @return newGAN A new GANProcess
	 */
	public static GANProcess initializeGAN(String modelType) {
		GANProcess newGAN = new GANProcess(GANProcess.PYTHON_BASE_PATH+"MegaManGAN"+ File.separator + Parameters.parameters.stringParameter(modelType), 
				Parameters.parameters.integerParameter("GANInputSize"), 
				Parameters.parameters.stringParameter(modelType).contains("With7Tile") ? MegaManGANUtil.MEGA_MAN_ALL_TERRAIN : MegaManGANUtil.MEGA_MAN_ONE_ENEMY,
				GANProcess.MEGA_MAN_OUT_WIDTH, GANProcess.MEGA_MAN_OUT_HEIGHT);
		return newGAN;
	}
	
	/**
	 * Gets megaManGenerator
	 * @return megaManGenerator The MegaManGANGenerator
	 */
	public static MegaManGANGenerator getMegaManGANGenerator() {
		return megaManGenerator;
	}
	
	/**
	 * sets megaManGenerator to the newMegaManGenerator
	 * @param newMegaManGenerator new MegaManGANGenerator to set megaManGenerator to
	 */
	public static void setMegaManGANGenerator(MegaManGANGenerator newMegaManGenerator) {
		megaManGenerator = newMegaManGenerator;
	}
	
	/**
	 * Places enemies in proper locations after the level has processed
	 * @param level The MegaMan level
	 */
	public static void postProcessingPlaceProperEnemies(List<List<Integer>> level) {
		for(int y=0;y<level.size();y++) {
			for(int x=0;x<level.get(0).size();x++) {
				if(level.get(y).get(x)==MegaManVGLCUtil.ONE_ENEMY_GROUND_ENEMY) {
					if((x>0&&level.get(y).get(x-1)==MegaManVGLCUtil.ONE_ENEMY_SOLID)||(x+1<level.get(0).size()&&level.get(y).get(x+1)==MegaManVGLCUtil.ONE_ENEMY_SOLID)) {
						level.get(y).set(x, MegaManVGLCUtil.ONE_ENEMY_WALL_ENEMY);
					}else if((y>0&&level.get(y-1).get(x)==MegaManVGLCUtil.ONE_ENEMY_SOLID)||(y+1<level.size()&&level.get(y+1).get(x)==MegaManVGLCUtil.ONE_ENEMY_SOLID)) {
						level.get(y).set(x, MegaManVGLCUtil.ONE_ENEMY_GROUND_ENEMY);
					}else {
						level.get(y).set(x, MegaManVGLCUtil.ONE_ENEMY_FLYING_ENEMY);
					}
				}
			}
		}
	}
	
	/**
	 * Starts the GAN process
	 * @param gan GANProcess Specific GAN model to use as a generator
	 */
	public static void startGAN(GANProcess gan) {
		gan.start();
		String response = "";
		while(!response.equals("READY")) {
			response = gan.commRecv();
		}
	}
	/**
	 * Gets a set of all of the levels from the latent vector 
	 * @param gan GANProcess Specific GAN model to use as a generator
	 * @param latentVector Array of doubles to store chunks
	 * @return Set of all the levels
	 */
	public static List<List<List<Integer>>> getLevelListRepresentationFromGAN(GANProcess gan, double[] latentVector){
		
		latentVector = GANProcess.mapArrayToOne(latentVector); // Range restrict the values
		int chunk_length = Integer.valueOf(gan.GANDim);
		String levelString = "";
		for(int i = 0; i < latentVector.length; i+=chunk_length){
			double[] chunk = Arrays.copyOfRange(latentVector, i, i+chunk_length); // Generate a level from the vector
			String oneLevelChunk;
			synchronized(gan) { // Make sure GAN response corresponds to message
				// Brackets required since generator.py expects of list of multiple levels, though only one is being sent here
				try {
					gan.commSend("[" + Arrays.toString(chunk) + "]");
				} catch (IOException e) {
					e.printStackTrace();
					System.exit(1); // Cannot continue without the GAN process
				}
				oneLevelChunk = gan.commRecv(); // Response to command just sent
			}
			levelString = levelString + ", " + oneLevelChunk;  
		}
		// These two lines remove the , from the first append to an empty string
		levelString = levelString.replaceFirst(",", "");
		levelString = levelString.replaceFirst(" ", "");
		levelString = "["+levelString+"]"; // Make a bundle of several levels
		// Create one level from all
		List<List<List<Integer>>> allLevels = JsonReader.JsonToInt(levelString);
		// This list contains several separate levels. The following code
		// merges the levels by appending adjacent rows
		List<List<Integer>> oneLevel = new ArrayList<List<Integer>>();
		// Create the appropriate number of rows in the array
		for(@SuppressWarnings("unused") List<Integer> row : allLevels.get(0)) { // Look at first level (assume all are same size)
			oneLevel.add(new ArrayList<Integer>()); // Empty row
		}
		// Now fill up the rows, one level at a time
		for(List<List<Integer>> aLevel : allLevels) {
			int index = 0;
			for(List<Integer> row : aLevel) { // Loot at each row
				oneLevel.get(index++).addAll(row);
			}	
		}
		return allLevels;
	}

	/**
	 * Gets one level from the list of levels, chooses the first one in the list  
	 * @param latentVector Array of doubles to store chunks
	 * @return oneLevel A single level, the first one in the list
	 */
	public static List<List<Integer>> generateOneLevelListRepresentationFromGANHorizontal(double[] latentVector) {
		// Since only one model is needed, using the standard getGANProcess
		List<List<List<Integer>>> levelInList = getLevelListRepresentationFromGAN(GANProcess.getGANProcess(), latentVector);
		List<List<Integer>> oneLevel = levelInList.get(0); // gets first level in the set 
		for(int level = 1;level<levelInList.size();level++) {
			for(int i = 0;i<oneLevel.size();i++) {
				oneLevel.get(i).addAll(levelInList.get(level).get(i));
			}
		}
		return oneLevel;
	}
	
	/**
	 * Generates level segments from GAN, but stitches them together vertically rather than horizontally
	 * @param latentVector Array of doubles to store chunks
	 * @return oneLevel A single level
	 */
	public static List<List<Integer>> generateOneLevelListRepresentationFromGANVertical(double[] latentVector) {
		// Since only one model is needed, using the standard getGANProcess
		List<List<List<Integer>>> levelInList = getLevelListRepresentationFromGAN(GANProcess.getGANProcess(), latentVector);
		List<List<Integer>> oneLevel = levelInList.get(0); // gets first level in the set 
		for(int level = 1;level<levelInList.size();level++) {
			oneLevel.addAll(levelInList.get(level));
		}
		return oneLevel;
	}

	/**
	 * Places the spawn point for MegaMan
	 * @param level The level
	 */
	public static void placeSpawn(List<List<Integer>> level) {
		boolean placed = false;
		//Preliminary loop to add spawn
		for(int x = 0;x<level.get(0).size();x++) {
			for(int y = 0;y<level.size();y++) {
				if(y-2>=0&&(level.get(y).get(x)==1||level.get(y).get(x)==2)&&(level.get(y-1).get(x)==0||level.get(y-1).get(x)==10)&&(level.get(y-2).get(x)==0||level.get(y-1).get(x)==10)) {
					level.get(y-1).set(x, 8);
					placed = true;
					break;
				}
			}
			//If the spawn is placed through the first loop, it stops,
			//otherwise, runs through second loop
			if(placed) break;	
		}
		for(int i = 0; i<level.get(0).size();i++) {
			if(!placed) {
				level.get(level.size()-1).set(0, 1);
				level.get(level.size()-2).set(0, 8);
				level.get(level.size()-3).set(0, 0);
				placed = true;
			}
		}

	}
	
	/**
	 * Places the orb for MegaMan on the right side of the specified segment
	 * @param level The segment
	 */
	public static void placeOrbRight(List<List<Integer>> level) {
		boolean placed = false;
		//Preliminary loop to add orb, goes right to left, then calls method to 
		//loop through Y's until a location is found
		for(int x = level.get(0).size()-1;x>=0; x--) {
			placed = placeOrblLoopForYValues(level, placed, x);
			
			//If it the orb is placed, the method stops
			if(placed) break;
		}
		//If no location was found, this method is called to make sure one is placed
		placeOrbForced(level, placed);
	}
	
	/**
	 * Places the orb for MegaMan on the left side of the specified segment
	 * @param level The segment
	 */
	public static void placeOrbLeft(List<List<Integer>> level) {
		boolean placed = false;
		//Preliminary loop to add orb, goes right to left, then calls method to 
		//loop through Y's until a location is found
		for(int x =0;x<=level.get(0).size()-1; x++) {
			placed = placeOrblLoopForYValues(level, placed, x);
			
			//If it the orb is placed, the method stops
			if(placed) break;
		}
		//If no location was found, this method is called to make sure one is placed
		placeOrbForced(level, placed);
	}
	
	/**
	 * Helper for placeOrb methods. Inner loop goes from bottom to top of the
	 * segment to find a suitable location for the orb. Once it is found, the
	 * orb is place. This loop controls the Y's in the segment
	 * @param level The segment
	 * @param placed Boolean flag to track if the orb was placed
	 * @param x Specified value of X the loop should run for
	 * @return placed Whether or not the orb was placed
	 */
	private static boolean placeOrblLoopForYValues(List<List<Integer>> level, boolean placed, int x) {
		for(int y = level.size()-1; y>=0;y--) { //Bottom to top
			if(y-2>=0&&(level.get(y).get(x)==2||level.get(y).get(x)==1||level.get(y).get(x)==5)&&(level.get(y-1).get(x)==0||level.get(y-1).get(x)==10)) {
				level.get(y-1).set(x, 7);
				level.get(y-2).set(x,  MegaManVGLCUtil.ONE_ENEMY_AIR);
				placed=true;
				break; //Break to ensure only one orb is placed
			}
		}
		return placed;
	}
	
	/**
	 * If no place was suitable within the segment, this loop modifies the 
	 * segment to find a suitable location.
	 * @param level The segment
	 * @param placed placed Boolean flag to track if the orb was placed
	 */
	private static void placeOrbForced(List<List<Integer>> level, boolean placed) {
		for(int i = 0; i<level.get(0).size();i++) {
			if(!placed) {
				level.get(level.size()-1).set(0, MegaManVGLCUtil.ONE_ENEMY_SOLID);
				level.get(level.size()-2).set(0, MegaManVGLCUtil.ONE_ENEMY_ORB);
				level.get(level.size()-3).set(0, MegaManVGLCUtil.ONE_ENEMY_AIR);
				placed = true;
			}
		}
	}

	

	/**
	 * Gets all of the sgement's data and adds it to a latent vector
	 * @param width Number of segments
	 * @param segmentLength The length of one segment
	 * @param wholeVector The entire vector from realValuedGenotype
	 * @return result Portion of vector corresponding to one segment
	 */
	public static double[] latentVectorAndMiscDataForPosition(int width,int segmentLength, double[] wholeVector) { 
		int startIndex = segmentLength*(width);
		double[] result = new double[segmentLength]; //resets size of result to be the same length
		System.arraycopy(wholeVector, startIndex, result, 0, segmentLength);
		return result;
	}
	
	/**
	 * Kick off for longVectorOrCPPNToMegaManLevel. For Long Vectors to 
	 * MegaMan Level, has nulls in place to ensure Long Vector version
	 * @param megaManGANGenerator the megaManGANGenerator
	 * @param wholeVector The whole latent vector
	 * @param chunks The number of chunks
	 * @param segmentTypeTracker Type tracker of type MegaManTrackSegmentType
	 * @return call to longVectorOrCPPNToMegaManLevel
	 */
	public static List<List<Integer>> longVectorToMegaManLevel(MegaManGANGenerator megaManGANGenerator, double[] wholeVector, int chunks, MegaManTrackSegmentType segmentTypeTracker){
		return longVectorOrCPPNToMegaManLevel(megaManGANGenerator, null, wholeVector, chunks, segmentTypeTracker, null);
	}	

	/**
	 * Kick off for longVectorOrCPPNToMegaManLevel. For CPPN to 
	 * MegaMan Level, has nulls in place to ensure CPPN version
	 * @param megaManGANGenerator the megaManGANGenerator
	 * @param cppn the CPPN
	 * @param chunks The number of chunks
	 * @param inputMultipliers Array of Doubles
	 * @param segmentTypeTracker Type tracker of type MegaManTrackSegmentType
	 * @return call to longVectorOrCPPNToMegaManLevel
	 */
	public static List<List<Integer>> cppnToMegaManLevel(MegaManGANGenerator megaManGANGenerator, Network cppn,  int chunks, double[] inputMultipliers, MegaManTrackSegmentType segmentTypeTracker){
		return longVectorOrCPPNToMegaManLevel(megaManGANGenerator,cppn,null,chunks, segmentTypeTracker, inputMultipliers);
	}	
	
	/**
	 * Takes in info from either a long Vector or a CPPN and converts it to a 
	 * MegaMan level. 
	 * @param megaManGANGenerator the megaManGANGenerator
	 * @param cppn the CPPN
	 * @param wholeVector The whole latent vector
	 * @param chunks The number of chunks
	 * @param segmentTypeTracker Type tracker of type MegaManTrackSegmentType
	 * @param inputMultipliers Array of Doubles
	 * @return the MegaMan level 
	 */
	public static List<List<Integer>> longVectorOrCPPNToMegaManLevel(MegaManGANGenerator megaManGANGenerator, Network cppn, double[] wholeVector, int chunks, MegaManTrackSegmentType segmentTypeTracker, double[] inputMultipliers){
		
		Pair<List<List<Integer>>, double[]> levelAndWholeLatentVector = longVectorOrCPPNtoMegaManLevelAndVector(
				megaManGANGenerator, cppn, wholeVector, chunks, segmentTypeTracker, inputMultipliers);
		
		postProcessingPlaceProperEnemies(levelAndWholeLatentVector.t1);
		return levelAndWholeLatentVector.t1; //t1 refers to the level in the pair
		
	}
	
	// For CPPN2GAN
	public static final int XPREF  = 0;
	public static final int YPREF = 1;
	public static final int BIASPREF = 2;	
	
	/**
	 * Converts either a long vector or a CPPN into a pair containing
	 * both a MegaMan level, and also the whole latent vector. Helper
	 * method of longVectorOrCPPNToMegaManLevel. Using this method, you
	 * can return the pair, which gives you access to both the level 
	 * and the whole latent vector
	 * @param megaManGANGenerator the megaManGANGenerator
	 * @param cppn the CPPN
	 * @param wholeVector The whole latent vector
	 * @param chunks The number of chunks
	 * @param segmentTypeTracker Type tracker of type MegaManTrackSegmentType
	 * @param inputMultipliers Array of Doubles
	 * @return levelAndWholeLatentVector A pair containing both the level and the latent vector
	 */
	public static Pair<List<List<Integer>>, double[]> longVectorOrCPPNtoMegaManLevelAndVector(
			MegaManGANGenerator megaManGANGenerator, Network cppn, double[] wholeVector, int chunks,
			MegaManTrackSegmentType segmentTypeTracker, double[] inputMultipliers) {
		HashSet<Point> previousPoints = new HashSet<>();
		Point currentPoint  = new Point(0,0);
		Point previousPoint = null;
		Point placementPoint = currentPoint;
		List<List<Integer>> level = new ArrayList<>();
		List<List<Integer>> segment = new ArrayList<>();
		HashSet<List<List<Integer>>> distinct = new HashSet<>();
		
		//Computes the length of one segment, which is the GAN input size and the number of aux. variables
		int oneSegmentLength = Parameters.parameters.integerParameter("GANInputSize")+MegaManGANGenerator.numberOfAuxiliaryVariables();
		
		// Level is being generated based on the CPPN. Store the latent vectors into one
		// big vector as the components are generated.
		if(cppn != null && wholeVector == null) {
			wholeVector = new double[chunks * oneSegmentLength];
		}
		
		for(int i = 0;i<chunks;i++) {
			assert currentPoint != null : "START: i="+i;
			double[] oneSegmentData = cppn == null ?
					latentVectorAndMiscDataForPosition(i, oneSegmentLength, wholeVector) :
					cppn.process(new double[] {
								inputMultipliers[XPREF] * currentPoint.x/(1.0*chunks),
								inputMultipliers[YPREF] * currentPoint.y/(1.0*chunks),
								inputMultipliers[BIASPREF] * 1.0});
			
			// If CPPN is generating level, copy each segment of variables into one big vector
			if(cppn != null && wholeVector == null) {
				System.arraycopy(oneSegmentData, 0, wholeVector, i*oneSegmentLength, oneSegmentLength);
			}
			Pair<List<List<Integer>>, Point> segmentAndPoint = megaManGANGenerator.generateSegmentFromVariables(oneSegmentData, previousPoint, previousPoints, currentPoint);
			if(segmentAndPoint==null) {
				break; //NEEDS TO BE FIXED!! ORB WILL NOT BE PLACED
			}
			segment = segmentAndPoint.t1;
			segmentTypeTracker.findSegmentData(megaManGANGenerator.getSegmentType(), segment, distinct);

			// If it is the last segment, place the orb
			if(i==chunks-1) {	
				// If the last segment is to the left, place on left side 
				if(previousPoint.x-currentPoint.x==1) placeOrbLeft(segment);
				
				//Otherwise, place the orb on the right side
				else placeOrbRight(segment);
			}
			previousPoint = currentPoint; // backup previous
			currentPoint = segmentAndPoint.t2;
			
			
			placementPoint = placeMegaManSegment(level, segment,  currentPoint, previousPoint, placementPoint);

			assert currentPoint != null : "Iteration "+i+", \n"+segment+"\npreviousPoint = "+previousPoint;
		}
		
		// t1 is the level, t2 is the Whole vector
		Pair<List<List<Integer>>, double[]> levelAndWholeLatentVector = new Pair<>(level,wholeVector);
		return levelAndWholeLatentVector;
	}

	/**
	 * Places the MegaMan segment based on where the current 
	 * points are and 
	 * @param level
	 * @param segment
	 * @param current
	 * @param prev
	 * @param placementPoint
	 * @return
	 */
	public static Point placeMegaManSegment(List<List<Integer>> level,List<List<Integer>> segment, 
			Point current, Point prev, Point placementPoint) {
		if(level.isEmpty()) { // First segment
			for(List<Integer> row : segment) {
				ArrayList<Integer> newRow = new ArrayList<>(row.size());
				newRow.addAll(row);
				level.add(newRow);
			}
			placeSpawn(level);
			
			
			// Schrum:  I'm  really not sure what belongs here, of it this deserves its own case
			placementPoint = findInitialPlacementPoint(prev, current, placementPoint);
		} else if(current.equals(new Point(prev.x+1, prev.y))) {
			placeRightSegment(level, segment, placementPoint);
			placementPoint = new Point(placementPoint.x+MEGA_MAN_LEVEL_WIDTH, placementPoint.y);
		} else if(current.equals(new Point(prev.x, prev.y+1))) {
			placeDownSegment(level, segment, placementPoint);
			placementPoint = new Point(placementPoint.x, placementPoint.y+MEGA_MAN_LEVEL_HEIGHT);
		} else if(current.equals(new Point(prev.x, prev.y-1))) {
			placeUpSegment(level, segment, placementPoint);
			placementPoint = new Point(placementPoint.x, placementPoint.y-MEGA_MAN_LEVEL_HEIGHT);
		} else if(current.equals(new Point(prev.x-1, prev.y))) {
			placeLeftSegment(level, segment, placementPoint);
			placementPoint = new Point(placementPoint.x-MEGA_MAN_LEVEL_WIDTH, placementPoint.y);
		}
		return placementPoint;
	}
	
	/**
	 * Seems to scale the coordinates of segments within the grid to the coordinates of tiles within
	 * the grid of tiles when all segments are batched together.
	 * 
	 * @param prev SHOULD NOT EXIST!? Gets immediately overwritten ... probably safe to remove as parameter
	 * @param current Current Point in the grid coordinate system
	 * @param placementPoint Point corresponding to current in the tile coordinate system
	 * @return Point in the tile coordinate system where new level segment should be placed (upper left corner?)
	 */
	private static Point findInitialPlacementPoint(Point prev, Point current, Point placementPoint) {
		prev = new Point(0,0);
		if(current.equals(new Point(prev.x+1, prev.y))) {
			placementPoint = new Point(placementPoint.x+MEGA_MAN_LEVEL_WIDTH, placementPoint.y);
		} else if(current.equals(new Point(prev.x, prev.y+1))) {
			placementPoint = new Point(placementPoint.x, placementPoint.y+MEGA_MAN_LEVEL_HEIGHT);
		} else if(current.equals(new Point(prev.x, prev.y-1))) {
			placementPoint = new Point(placementPoint.x, placementPoint.y-MEGA_MAN_LEVEL_HEIGHT);
		} else if(current.equals(new Point(prev.x-1, prev.y))) {
			placementPoint = new Point(placementPoint.x-MEGA_MAN_LEVEL_WIDTH, placementPoint.y);
		}
		return placementPoint;
	}
	
	
	private static void placeLeftSegment(List<List<Integer>> level, List<List<Integer>> segment, Point placementPoint) {
		if(placementPoint.x<0) { //add null lines to left
			List<Integer> nullLine = new ArrayList<>();
			for(int i = 0;i<segment.get(0).size();i++) {
				nullLine.add(MegaManVGLCUtil.ONE_ENEMY_NULL);
			}
				for(int i = 0;i<level.size();i++) { //add null to all spaces to the right TODO possibly change
					level.get(i).addAll(0,nullLine);
				
			}
			
			placementPoint.x += MEGA_MAN_LEVEL_WIDTH;
		}
		if(placementPoint.x+MEGA_MAN_LEVEL_WIDTH>level.get(0).size()) {
			List<Integer> nullLine = new ArrayList<>();
			for(int i = 0;i<segment.get(0).size();i++) {
				nullLine.add(MegaManVGLCUtil.ONE_ENEMY_NULL);
			}
			for(int i = 0;i<level.size();i++) { //add null to all spaces to the right TODO possibly change
				level.get(i).addAll(nullLine);
			}
			
		
		}
		if(placementPoint.y>=level.size()) {
			List<List<Integer>> nullScreen = new ArrayList<>();
			for(int i = 0;i<MEGA_MAN_LEVEL_HEIGHT;i++) {
				List<Integer> nullLines = new ArrayList<Integer>();
					for(int j = 0;j<level.get(0).size();j++) {
						nullLines.add(MegaManState.MEGA_MAN_TILE_NULL);
				}
				
				nullScreen.add(nullLines);
			}
			if(level.size()==0) {
				
			}
			level.addAll(level.size(), nullScreen);
		}
		
		if(placementPoint.y<0) {
			List<List<Integer>> nullScreen = new ArrayList<>();
			for(int i = 0;i<MEGA_MAN_LEVEL_HEIGHT;i++) {
				List<Integer> nullLines = new ArrayList<Integer>();
					for(int j = 0;j<level.get(0).size();j++) {
						nullLines.add(MegaManState.MEGA_MAN_TILE_NULL);
					
				}
				nullScreen.add(nullLines);
			}
			level.addAll(0, nullScreen);
			placementPoint.y+=MEGA_MAN_LEVEL_HEIGHT;
		}
		
		for(int x = placementPoint.x; x < placementPoint.x+MEGA_MAN_LEVEL_WIDTH;x++) {
			for(int y = placementPoint.y;y<placementPoint.y+MEGA_MAN_LEVEL_HEIGHT;y++) {
				level.get(y).set(x, segment.get(y-placementPoint.y).get(x - placementPoint.x));
			}
		}
		
	}

	private static void placeRightSegment(List<List<Integer>> level, List<List<Integer>> segment, Point placementPoint) {
		if(placementPoint.x+MEGA_MAN_LEVEL_WIDTH>level.get(0).size()) {
			List<Integer> nullLine = new ArrayList<>();
			for(int i = 0;i<segment.get(0).size();i++) {
				nullLine.add(MegaManVGLCUtil.ONE_ENEMY_NULL);
			}
				for(int i = 0;i<level.size();i++) { //add null to all spaces to the right TODO possibly change
					level.get(i).addAll(nullLine);
				}
		}
		
		if(placementPoint.x<0) { //add null lines to left
			List<Integer> nullLine = new ArrayList<>();
			for(int i = 0;i<segment.get(0).size();i++) {
				nullLine.add(MegaManVGLCUtil.ONE_ENEMY_NULL);
			}
				for(int i = 0;i<level.size();i++) { //add null to all spaces to the right TODO possibly change
					level.get(i).addAll(0,nullLine);
				
			}
			
			placementPoint.x += MEGA_MAN_LEVEL_WIDTH;
		}
		if(placementPoint.y>=level.size()) {
			List<List<Integer>> nullScreen = new ArrayList<>();
			for(int i = 0;i<MEGA_MAN_LEVEL_HEIGHT;i++) {
				List<Integer> nullLines = new ArrayList<Integer>();
				
					for(int j = 0;j<level.get(0).size();j++) {
						nullLines.add(MegaManState.MEGA_MAN_TILE_NULL);
					}
				
				nullScreen.add(nullLines);
			}
			if(level.size()==0) {
				
			}
			level.addAll(level.size(), nullScreen);
		}
		
		if(placementPoint.y<0) {
			List<List<Integer>> nullScreen = new ArrayList<>();
			for(int i = 0;i<MEGA_MAN_LEVEL_HEIGHT;i++) {
				List<Integer> nullLines = new ArrayList<Integer>();
					for(int j = 0;j<level.get(0).size();j++) {
						nullLines.add(MegaManState.MEGA_MAN_TILE_NULL);
				}
				nullScreen.add(nullLines);
			}
			level.addAll(0, nullScreen);
			placementPoint.y+=MEGA_MAN_LEVEL_HEIGHT;
		}
		for(int x = placementPoint.x; x < placementPoint.x+MEGA_MAN_LEVEL_WIDTH;x++) {
			for(int y = placementPoint.y;y<placementPoint.y+MEGA_MAN_LEVEL_HEIGHT;y++) {
				level.get(y).set(x, segment.get(y-placementPoint.y).get(x - placementPoint.x));
			}
		}

		
	}

	private static void placeUpSegment(List<List<Integer>> level, List<List<Integer>> segment, Point placementPoint) {
		if(placementPoint.y<0) {
			List<List<Integer>> nullScreen = new ArrayList<>();
			for(int i = 0;i<MEGA_MAN_LEVEL_HEIGHT;i++) {
				List<Integer> nullLines = new ArrayList<Integer>();
					for(int j = 0;j<level.get(0).size();j++) {
						nullLines.add(MegaManState.MEGA_MAN_TILE_NULL);
					}
				
				nullScreen.add(nullLines);
			}
			level.addAll(0, nullScreen);
			placementPoint.y+=MEGA_MAN_LEVEL_HEIGHT;
		}
		if(placementPoint.x<0) { //add null lines to left
			List<Integer> nullLine = new ArrayList<>();
			for(int i = 0;i<segment.get(0).size();i++) {
				nullLine.add(MegaManVGLCUtil.ONE_ENEMY_NULL);
			}
				for(int i = 0;i<level.size();i++) { //add null to all spaces to the right TODO possibly change
					level.get(i).addAll(0,nullLine);
			}
			
			placementPoint.x += MEGA_MAN_LEVEL_WIDTH;
		}
		
		if(placementPoint.x>=level.get(0).size()) {
			List<Integer> nullLine = new ArrayList<>();
			for(int i = 0;i<segment.get(0).size();i++) {
				nullLine.add(MegaManVGLCUtil.ONE_ENEMY_NULL);
			}

				for(int i = 0;i<level.size();i++) { //add null to all spaces to the right TODO possibly change
					level.get(i).addAll(nullLine);
				}
			
			
		
		}
		if(placementPoint.y+MEGA_MAN_LEVEL_HEIGHT>level.size()) {
			List<List<Integer>> nullScreen = new ArrayList<>();
			for(int i = 0;i<MEGA_MAN_LEVEL_HEIGHT;i++) {
				List<Integer> nullLines = new ArrayList<Integer>();
				
					for(int j = 0;j<level.get(0).size();j++) {
						nullLines.add(MegaManState.MEGA_MAN_TILE_NULL);
					}

				
				nullScreen.add(nullLines);
			}
			if(level.size()==0) {
				
			}
			level.addAll(level.size(), nullScreen);
		}
		for(int x = placementPoint.x; x < placementPoint.x+MEGA_MAN_LEVEL_WIDTH;x++) {
			for(int y = placementPoint.y;y<placementPoint.y+MEGA_MAN_LEVEL_HEIGHT;y++) {
				level.get(y).set(x, segment.get(y-placementPoint.y).get(x - placementPoint.x));
			}
		}
		
		
	}

	private static void placeDownSegment(List<List<Integer>> level, List<List<Integer>> segment, Point placementPoint) {
		if(placementPoint.y+MEGA_MAN_LEVEL_HEIGHT>level.size()) {
			List<List<Integer>> nullScreen = new ArrayList<>();
			for(int i = 0;i<MEGA_MAN_LEVEL_HEIGHT;i++) {
				List<Integer> nullLines = new ArrayList<Integer>();

					for(int j = 0;j<level.get(0).size();j++) {
						nullLines.add(MegaManState.MEGA_MAN_TILE_NULL);
					}
				
				
				nullScreen.add(nullLines);
			}
			if(level.size()==0) {
				
			}
			level.addAll(level.size(), nullScreen);
		}
		if(placementPoint.x<0) { //add null lines to left
			List<Integer> nullLine = new ArrayList<>();
			for(int i = 0;i<segment.get(0).size();i++) {
				nullLine.add(MegaManVGLCUtil.ONE_ENEMY_NULL);
			}
		
				for(int i = 0;i<level.size();i++) { //add null to all spaces to the right TODO possibly change
					level.get(i).addAll(0,nullLine);
				
			}
			
			placementPoint.x += MEGA_MAN_LEVEL_WIDTH;
		}
		if(placementPoint.x>=level.get(0).size()) {
			List<Integer> nullLine = new ArrayList<>();
			for(int i = 0;i<segment.get(0).size();i++) {
				nullLine.add(MegaManVGLCUtil.ONE_ENEMY_NULL);
			}
				for(int i = 0;i<level.size();i++) { //add null to all spaces to the right TODO possibly change
					level.get(i).addAll(nullLine);
				}
			
		
		
		}
		if(placementPoint.y<0) {
			List<List<Integer>> nullScreen = new ArrayList<>();
			for(int i = 0;i<MEGA_MAN_LEVEL_HEIGHT;i++) {
				List<Integer> nullLines = new ArrayList<Integer>();
			
					for(int j = 0;j<level.get(0).size();j++) {
						nullLines.add(MegaManState.MEGA_MAN_TILE_NULL);
					}
				
				nullScreen.add(nullLines);
			}
			level.addAll(0, nullScreen);
			placementPoint.y+=MEGA_MAN_LEVEL_HEIGHT;
		}
		
		for(int x = placementPoint.x; x < placementPoint.x+MEGA_MAN_LEVEL_WIDTH;x++) {
			for(int y = placementPoint.y; y < placementPoint.y+MEGA_MAN_LEVEL_HEIGHT;y++) {
				level.get(y).set(x, segment.get(y-placementPoint.y).get(x - placementPoint.x));
			}
		}
		
	}
}
