package edu.southwestern.tasks.megaman;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import edu.southwestern.networks.Network;
import edu.southwestern.tasks.mario.gan.GANProcess;
import edu.southwestern.tasks.megaman.gan.MegaManGANUtil;
import edu.southwestern.tasks.megaman.levelgenerators.MegaManGANGenerator;
import edu.southwestern.util.datastructures.Pair;

public class MegaManCPPNtoGANUtil {
	public static final int XPREF  = 0;
	public static final int YPREF = 1;
	public static final int BIASPREF = 2;
	public static int numHorizontal = 0;
	public static int numUp = 0;
	public static int numDown = 0;
	public static int numCorner = 0;
	public static int numDistinctSegments = 0;
	public static HashSet<List<List<Integer>>> distinct;
	public static int x = 0;
	public static int y = 0;
	public static Point previousMove;
//	public static GANProcess ganProcessHorizontal = null;
//	public static GANProcess ganProcessUp = null;
//	public static GANProcess ganProcessDown = null;
//	MegaManCPPNtoGANUtil(){
//		ganProcessHorizontal = MegaManGANUtil.initializeGAN("MegaManGANHorizontalModel");
//		ganProcessDown= MegaManGANUtil.initializeGAN("MegaManGANDownModel");
//		ganProcessUp = MegaManGANUtil.initializeGAN("MegaManGANUpModel");
//		MegaManGANUtil.startGAN(ganProcessUp);
//		MegaManGANUtil.startGAN(ganProcessDown);
//		MegaManGANUtil.startGAN(ganProcessHorizontal);
	//}
	public enum Direction {UP, DOWN, HORIZONTAL};
	public static Direction d;
	public static List<List<Integer>> cppnToMegaManLevel(MegaManGANGenerator megaManGenerator, Network cppn, int chunks, double[] inputMultipliers){

		// TODO: This method unnecessarily repeats code from MegaManGANUtil.longVectorToMegaManLevel
		//       We should refactor to avoid the repeated code
		
		HashSet<Point> previousPoints = new HashSet<>();
		Point currentPoint  = new Point(0,0);
		Point previousPoint = null;
		Point placementPoint = currentPoint;
		List<List<Integer>> level = new ArrayList<>();
		List<List<Integer>> segment = new ArrayList<>();
		for(int i = 0;i<chunks;i++) {
			
			// This line is the only differece from MegaManGANUtil.longVectorToMegaManLevel
			double[] oneSegmentData = cppn.process(new double[] {
					inputMultipliers[XPREF] * currentPoint.x/(1.0*chunks),
					inputMultipliers[YPREF] * currentPoint.y/(1.0*chunks),
					inputMultipliers[BIASPREF] * 1.0});
						
			Pair<List<List<Integer>>, Point> segmentAndPoint = megaManGenerator.generateSegmentFromVariables(oneSegmentData, previousPoint, previousPoints, currentPoint);
			segment = segmentAndPoint.t1;
			previousPoint = currentPoint; // backup previous
			currentPoint = segmentAndPoint.t2;
			if(i==chunks-1) MegaManGANUtil.placeOrb(segment);
			//placementPoint = currentPoint;
			//System.out.println("previousPoint:"+previousPoint+",current:"+currentPoint+",placementPoint:"+placementPoint);
			placementPoint = MegaManGANUtil.placeMegaManSegment(level, segment,  currentPoint, previousPoint, placementPoint);
			//System.out.println(placementPoint);
			//MegaManVGLCUtil.printLevel(level);
//			MiscUtil.waitForReadStringAndEnterKeyPress();
			
			
//			if(i==0) MegaManGANUtil.placeSpawn(level);
		}
		
		
		return level;
		
//		
//		
//		
//		numHorizontal = 0;
//		 numUp = 0;
//		 numDown = 0;
//		 numCorner = 0;
//		 numDistinctSegments = 0;
//		 distinct = new HashSet<>();
//		 x = 0;
//		 y = 0;
////		for(double k :inputMultipliers) {
////			System.out.println(k);
////		}
//		
//
//		List<List<Integer>> oneLevel;
////		levelInListHorizontal = MegaManGANUtil.getLevelListRepresentationFromGAN(ganProcessHorizontal, latentVector);
////		levelInListUp = MegaManGANUtil.getLevelListRepresentationFromGAN(ganProcessUp, latentVector);
////		levelInListDown = MegaManGANUtil.getLevelListRepresentationFromGAN(ganProcessDown, latentVector);
//		d = null;
//		//System.out.println(MegaManCPPNtoGANLevelBreederTask.staticNumCPPNOutputs());
//		double[] startfull = cppn.process(new double[] {
//				inputMultipliers[XPREF] * x/chunks,
//				inputMultipliers[YPREF]*y/chunks,
//				inputMultipliers[BIASPREF] * 1.0});
//		//System.out.println(startfull.length);
//
//		double[] startlatentVector = new double[startfull.length-3];
//		for(int i = 3;i<startfull.length;i++) {
//			startlatentVector[i-3]=startfull[i];
//		}
//		
//		List<List<List<Integer>>> levelInListHorizontal = MegaManGANUtil.getLevelListRepresentationFromGAN(ganProcessHorizontal, startlatentVector);
//		List<List<List<Integer>>> levelInListUp = MegaManGANUtil.getLevelListRepresentationFromGAN(ganProcessUp, startlatentVector);
//		List<List<List<Integer>>> levelInListDown = MegaManGANUtil.getLevelListRepresentationFromGAN(ganProcessDown, startlatentVector);
//		List<List<List<Integer>>> levelInListUpperRight; /*= getLevelListRepresentationFromGAN(upperRightGAN, latentVector);*/
//		List<List<List<Integer>>> levelInListUpperLeft;/*= getLevelListRepresentationFromGAN(upperLeftGAN, latentVector);*/
//		List<List<List<Integer>>> levelInListLowerRight; /*= getLevelListRepresentationFromGAN(lowerRightGAN, latentVector);*/
//		List<List<List<Integer>>> levelInListLowerLeft; /*= getLevelListRepresentationFromGAN(lowerLeftGAN, latentVector);*/
//		
//		double[] startoutput = new double[3];
//		for(int i =0;i<3;i++) {
//			startoutput[i]=startfull[i];
//		}
//		
//		int startdirection = StatisticsUtilities.argmax(startoutput);
//		oneLevel = placeInitialDirection(ganProcessHorizontal, startlatentVector, levelInListUp, levelInListDown,
//				startdirection);
//		distinct.add(oneLevel);
//		MegaManGANUtil.placeSpawn(oneLevel);
//		if(chunks==1) {
//			MegaManGANUtil.placeOrb(oneLevel);
//		}
//		List<Integer> nullLine = new ArrayList<Integer>(16);
//		for(int i=0;i<MegaManGANUtil.MEGA_MAN_LEVEL_WIDTH;i++) {
//			nullLine.add(MegaManState.MEGA_MAN_TILE_NULL);
//		}
//		previousMove = new Point(0,0);
//		for(int level = 1; level < chunks; level++) {
//			Direction previous = d;
//			double[] full = cppn.process(new double[] {inputMultipliers[XPREF] * x/chunks, inputMultipliers[YPREF]*y/chunks,  inputMultipliers[BIASPREF] * 1.0});
//			double[] latentVector = new double[full.length-3];
//			for(int i = 3;i<full.length;i++) {
//				latentVector[i-3]=full[i];
//			}
//			double[] outputs = new double[3];
//			for(int i =0;i<3;i++) {
//				outputs[i]=full[i];
//			}
//			int direction = StatisticsUtilities.argmax(outputs);
//			double[] backup = new double[3];
//			for(int i = 0;i<3;i++) {
//				if(i!=direction) {
//					backup[i]=outputs[i];
//				}
//			}
//			int bkp = StatisticsUtilities.argmax(backup);
//			double[] backup1 = new double[3];
//			for(int i = 0;i<3;i++) {
//				if(i!=direction&&i!=bkp) {
//					backup1[i]=outputs[i];
//				}
//			}
//			int bkp1 = StatisticsUtilities.argmax(backup1);
//			boolean needBackup = true;
//			levelInListLowerRight = MegaManGANUtil.getLevelListRepresentationFromGAN(lowerRightGAN, latentVector);
//			levelInListUp = MegaManGANUtil.getLevelListRepresentationFromGAN(ganProcessUp, latentVector);
//			levelInListUpperRight = MegaManGANUtil.getLevelListRepresentationFromGAN(upperRightGAN, latentVector);
//			levelInListDown = MegaManGANUtil.getLevelListRepresentationFromGAN(ganProcessDown, latentVector);
//			levelInListHorizontal = MegaManGANUtil.getLevelListRepresentationFromGAN(ganProcessHorizontal, latentVector);
//			levelInListUpperLeft = MegaManGANUtil.getLevelListRepresentationFromGAN(upperLeftGAN, latentVector);
//			levelInListLowerLeft = MegaManGANUtil.getLevelListRepresentationFromGAN(lowerLeftGAN, latentVector);
//			
//			needBackup = addLevelSegment(chunks, oneLevel, levelInListHorizontal, levelInListUp, levelInListDown,
//					levelInListUpperRight, levelInListUpperLeft, levelInListLowerRight, levelInListLowerLeft, nullLine,
//					level, previous, direction, needBackup);
//			
//			
//			if(needBackup) {
//				needBackup = addLevelSegment(chunks, oneLevel, levelInListHorizontal, levelInListUp, levelInListDown,
//						levelInListUpperRight, levelInListUpperLeft, levelInListLowerRight, levelInListLowerLeft, nullLine,
//						level, previous, bkp, needBackup);
//				
//			}
//			if(needBackup) {
//				needBackup = addLevelSegment(chunks, oneLevel, levelInListHorizontal, levelInListUp, levelInListDown,
//						levelInListUpperRight, levelInListUpperLeft, levelInListLowerRight, levelInListLowerLeft, nullLine,
//						level, previous, bkp1, needBackup);
//				
//			}
//			if(!d.equals(previous)) {
//				numCorner++;
//			}
//			previous = d;
//			numDistinctSegments = distinct.size();
//		}
//		
//		
////		ganProcessUp.terminate();
////		ganProcessDown.terminate();
////		ganProcessHorizontal.terminate();
//		if(!Parameters.parameters.booleanParameter("megaManUsesUniqueEnemies")) {
//			MegaManGANUtil.postProcessingPlaceProperEnemies(oneLevel);
//		}
//		return oneLevel;
	}
	public static boolean addLevelSegment(int chunks, List<List<Integer>> oneLevel,
			List<List<List<Integer>>> levelInListHorizontal, List<List<List<Integer>>> levelInListUp,
			List<List<List<Integer>>> levelInListDown, List<List<List<Integer>>> levelInListUpperRight,
			List<List<List<Integer>>> levelInListUpperLeft, List<List<List<Integer>>> levelInListLowerRight,
			List<List<List<Integer>>> levelInListLowerLeft, List<Integer> nullLine, int level, Direction previous,
			int direction, boolean needBackup) {
		if(direction == MegaManCPPNtoGANLevelBreederTask.UP_PREFERENCE&&!d.equals(Direction.DOWN)) {
			
			d = Direction.UP;
			if(level==chunks-1) {
				MegaManGANUtil.placeOrb(levelInListUp.get(0));
			}
			needBackup=false;
			if(previous.equals(Direction.UP)) {
				MegaManGANUtil.placeUpCPPN(levelInListUp, previousMove, oneLevel, 0);
				y++;
				numUp++;
			}
			if(previous.equals(Direction.HORIZONTAL)) {
				MegaManGANUtil.placeRightCPPN(levelInListLowerRight, previousMove, oneLevel, nullLine, 0);
				x++;
				distinct.add(levelInListLowerRight.get(0));
				previousMove=new Point((int) previousMove.getX()+MegaManGANUtil.MEGA_MAN_LEVEL_WIDTH,(int) previousMove.getY());

			}
		}
		else if (direction == MegaManCPPNtoGANLevelBreederTask.DOWN_PREFERENCE&&!d.equals(Direction.UP)) {
			

			d = Direction.DOWN;
			
			distinct.add(levelInListDown.get(0));
			if(level==chunks-1) {
				MegaManGANUtil.placeOrb(levelInListDown.get(0));
			}
			needBackup=false;

			if(previous.equals(Direction.DOWN)) {
				y--;
				numDown++;
				MegaManGANUtil.placeDownCPPN(levelInListDown, previousMove, oneLevel, 0);
				previousMove=new Point((int) previousMove.getX(),(int) previousMove.getY()+MegaManGANUtil.MEGA_MAN_LEVEL_HEIGHT);
			}
			if(previous.equals(Direction.HORIZONTAL)) {
				x++;
				MegaManGANUtil.placeRightCPPN(levelInListUpperRight, previousMove, oneLevel, nullLine, 0);
				distinct.add(levelInListUpperRight.get(0));

				previousMove=new Point((int) previousMove.getX()+MegaManGANUtil.MEGA_MAN_LEVEL_WIDTH,(int) previousMove.getY());

			}

		}
		else if(direction==MegaManCPPNtoGANLevelBreederTask.HORIZONTAL_PREFERENCE){
			

			d = Direction.HORIZONTAL;
//				distinct.add(levelInListHorizontal.get(0));
			if(level==chunks-1) {
				MegaManGANUtil.placeOrb(levelInListHorizontal.get(0));
			}
			needBackup=false;
			
			if(previous.equals(Direction.UP)) {
				y++;
				MegaManGANUtil.placeUpCPPN(levelInListUpperLeft, previousMove, oneLevel, 0);
				distinct.add(levelInListUpperLeft.get(0));

			}
			if(previous.equals(Direction.DOWN)) {
				y--;
				MegaManGANUtil.placeDownCPPN(levelInListLowerLeft, previousMove, oneLevel, 0);
				previousMove=new Point((int) previousMove.getX(),(int) previousMove.getY()+MegaManGANUtil.MEGA_MAN_LEVEL_HEIGHT);
				distinct.add(levelInListLowerLeft.get(0));
			}
			if(previous.equals(Direction.HORIZONTAL)) {
				numHorizontal++;
				x++;
				MegaManGANUtil.placeRightCPPN(levelInListHorizontal, previousMove, oneLevel, nullLine, 0);
				previousMove=new Point((int) previousMove.getX()+MegaManGANUtil.MEGA_MAN_LEVEL_WIDTH,(int) previousMove.getY());

			}
			
			//wasRight = true;
		}
		return needBackup;
	}
	public static List<List<Integer>> placeInitialDirection(GANProcess ganProcessHorizontal,
			double[] startlatentVector, List<List<List<Integer>>> levelInListUp,
			List<List<List<Integer>>> levelInListDown, int startdirection) {
		List<List<Integer>> oneLevel;
		List<List<List<Integer>>> levelInListHorizontal;
		if(startdirection == MegaManCPPNtoGANLevelBreederTask.UP_PREFERENCE) {
			numUp++;
			d = Direction.UP;
			//y++;
//			levelInListUp = MegaManGANUtil.getLevelListRepresentationFromGAN(ganProcessUp, startlatentVector);
			oneLevel = levelInListUp.get(0);
		}
		else if (startdirection == MegaManCPPNtoGANLevelBreederTask.DOWN_PREFERENCE) {
			numDown++;
			d = Direction.DOWN;
			//y--;
//			levelInListDown = MegaManGANUtil.getLevelListRepresentationFromGAN(ganProcessDown, startlatentVector);
			oneLevel = levelInListDown.get(0);
		}
		else {
			numHorizontal++;
			d = Direction.HORIZONTAL;
			//x++;
			levelInListHorizontal = MegaManGANUtil.getLevelListRepresentationFromGAN(ganProcessHorizontal, startlatentVector);
			oneLevel = levelInListHorizontal.get(0);
		}
		return oneLevel;
	}
	public static HashMap<String, Integer> findMiscSegments(List<List<Integer>> level){
		HashMap<String, Integer> j = new HashMap<>();
		j.put("numUp", numUp);
		j.put("numDown", numDown);
		j.put("numHorizontal", numHorizontal);
		j.put("numCorner", numCorner);
		j.put("numDistinctSegments", numDistinctSegments);
		return j;
	}
	
}
