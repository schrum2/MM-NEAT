package edu.southwestern.tasks.megaman;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import edu.southwestern.networks.Network;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.mario.gan.GANProcess;
import edu.southwestern.tasks.megaman.astar.MegaManState;
import edu.southwestern.tasks.megaman.gan.MegaManGANUtil;
import edu.southwestern.util.stats.StatisticsUtilities;

public class MegaManCPPNtoGANUtil {
	public static final int XPREF  = 0;
	public static final int YPREF = 1;
	public static final int BIASPREF = 2;
	public static int numHorizontal = 0;
	public static int numUp = 0;
	public static int numDown = 0;
	public static int numCorner = 0;
	public static int numDistinctSegments = 0;

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
	public static List<List<Integer>> cppnToMegaManLevel(GANProcess ganProcessHorizontal, GANProcess ganProcessDown, GANProcess ganProcessUp, Network cppn, int chunks, double[] inputMultipliers){
		 numHorizontal = 0;
		 numUp = 0;
		 numDown = 0;
		 numCorner = 0;
		 numDistinctSegments = 0;
		 HashSet<List<List<Integer>>> distinct = new HashSet<>();
		int x = 0;
		int y = 0;
//		for(double k :inputMultipliers) {
//			System.out.println(k);
//		}
		List<List<List<Integer>>> levelInListHorizontal;
		List<List<List<Integer>>> levelInListUp;
		List<List<List<Integer>>> levelInListDown;
		List<List<Integer>> oneLevel;
//		levelInListHorizontal = MegaManGANUtil.getLevelListRepresentationFromGAN(ganProcessHorizontal, latentVector);
//		levelInListUp = MegaManGANUtil.getLevelListRepresentationFromGAN(ganProcessUp, latentVector);
//		levelInListDown = MegaManGANUtil.getLevelListRepresentationFromGAN(ganProcessDown, latentVector);
		Direction d;
		//System.out.println(MegaManCPPNtoGANLevelBreederTask.staticNumCPPNOutputs());
		double[] startfull = cppn.process(new double[] {
				inputMultipliers[XPREF] * x/chunks,
				inputMultipliers[YPREF]*y/chunks,
				inputMultipliers[BIASPREF] * 1.0});
		//System.out.println(startfull.length);

		double[] startlatentVector = new double[startfull.length-3];
		for(int i = 3;i<startfull.length;i++) {
			startlatentVector[i-3]=startfull[i];
		}
		double[] startoutput = new double[3];
		for(int i =0;i<3;i++) {
			startoutput[i]=startfull[i];
		}
		int startdirection = StatisticsUtilities.argmax(startoutput);
		if(startdirection == MegaManCPPNtoGANLevelBreederTask.UP_PREFERENCE) {
			numUp++;
			d = Direction.UP;
			//y++;
			levelInListUp = MegaManGANUtil.getLevelListRepresentationFromGAN(ganProcessUp, startlatentVector);
			oneLevel = levelInListUp.get(0);
		}
		else if (startdirection == MegaManCPPNtoGANLevelBreederTask.DOWN_PREFERENCE) {
			numDown++;
			d = Direction.DOWN;
			//y--;
			levelInListDown = MegaManGANUtil.getLevelListRepresentationFromGAN(ganProcessDown, startlatentVector);
			oneLevel = levelInListDown.get(0);
		}
		else {
			numHorizontal++;
			d = Direction.HORIZONTAL;
			//x++;
			levelInListHorizontal = MegaManGANUtil.getLevelListRepresentationFromGAN(ganProcessHorizontal, startlatentVector);
			oneLevel = levelInListHorizontal.get(0);
		}
		distinct.add(oneLevel);
		MegaManGANUtil.placeSpawn(oneLevel);
		if(chunks==1) {
			MegaManGANUtil.placeOrb(oneLevel);
		}
		List<Integer> nullLine = new ArrayList<Integer>(16);
		for(int i=0;i<MegaManGANUtil.MEGA_MAN_LEVEL_WIDTH;i++) {
			nullLine.add(MegaManState.MEGA_MAN_TILE_NULL);
		}
		Point previousMove = new Point(0,0);
		for(int level = 1; level < chunks; level++) {
			Direction previous = d;
			double[] full = cppn.process(new double[] {inputMultipliers[XPREF] * x/chunks, inputMultipliers[YPREF]*y/chunks,  inputMultipliers[BIASPREF] * 1.0});
			double[] latentVector = new double[full.length-3];
			for(int i = 3;i<full.length;i++) {
				latentVector[i-3]=full[i];
			}
			double[] outputs = new double[3];
			for(int i =0;i<3;i++) {
				outputs[i]=full[i];
				//System.out.println(outputs[i]);
			}
			int direction = StatisticsUtilities.argmax(outputs);
			//System.out.println(direction);
			//System.out.println("\n"+x+"\n"+y);
			
			//System.out.println("just finished");
			double[] backup = new double[3];
			for(int i = 0;i<3;i++) {
				if(i!=direction) {
					backup[i]=outputs[i];
				}
			}
			int bkp = StatisticsUtilities.argmax(backup);
			boolean needBackup = true;
			
			
			if(direction == MegaManCPPNtoGANLevelBreederTask.UP_PREFERENCE&&!d.equals(Direction.DOWN)) {
				numUp++;
				d = Direction.UP;
//				if(previous.equals(Direction.UP)) y++;
//				if(previous.equals(Direction.DOWN)) y--;
//				if(previous.equals(Direction.HORIZONTAL)) x++;


				levelInListUp = MegaManGANUtil.getLevelListRepresentationFromGAN(ganProcessUp, latentVector);
				distinct.add(levelInListUp.get(0));
				if(level==chunks-1) {
					MegaManGANUtil.placeOrb(levelInListUp.get(0));
				}
				needBackup=false;
				if(previous.equals(Direction.UP)) {
					MegaManGANUtil.placeUp(levelInListUp, previousMove, oneLevel, 0);
					y++;
				}
//				if(previous.equals(Direction.DOWN)) { //should never happen
//					MegaManGANUtil.placeDown(levelInListUp, previousMove, oneLevel, 0);
//					y--;
//					previousMove=new Point((int) previousMove.getX(),(int) previousMove.getY()+MegaManGANUtil.MEGA_MAN_LEVEL_HEIGHT);
//
//				}
				if(previous.equals(Direction.HORIZONTAL)) {
					MegaManGANUtil.placeRight(levelInListUp, previousMove, oneLevel, nullLine, 0);
					x++;
					previousMove=new Point((int) previousMove.getX()+MegaManGANUtil.MEGA_MAN_LEVEL_WIDTH,(int) previousMove.getY());

				}
				//wasRight = true;
				//previousMove=new Point((int) previousMove.getX()+MegaManGANUtil.MEGA_MAN_LEVEL_WIDTH,(int) previousMove.getY());

			}
			else if (direction == MegaManCPPNtoGANLevelBreederTask.DOWN_PREFERENCE&&!d.equals(Direction.UP)) {
				numDown++;

				d = Direction.DOWN;
				
				levelInListDown = MegaManGANUtil.getLevelListRepresentationFromGAN(ganProcessDown, latentVector);
				distinct.add(levelInListDown.get(0));
				if(level==chunks-1) {
					MegaManGANUtil.placeOrb(levelInListDown.get(0));
				}
				needBackup=false;
//				if(previous.equals(Direction.UP)) {
//					y++;
//					MegaManGANUtil.placeUp(levelInListDown, previousMove, oneLevel, 0);
//				}
				if(previous.equals(Direction.DOWN)) {
					y--;
					MegaManGANUtil.placeDown(levelInListDown, previousMove, oneLevel, 0);
					previousMove=new Point((int) previousMove.getX(),(int) previousMove.getY()+MegaManGANUtil.MEGA_MAN_LEVEL_HEIGHT);
				}
				if(previous.equals(Direction.HORIZONTAL)) {
					x++;
					MegaManGANUtil.placeRight(levelInListDown, previousMove, oneLevel, nullLine, 0);
					previousMove=new Point((int) previousMove.getX()+MegaManGANUtil.MEGA_MAN_LEVEL_WIDTH,(int) previousMove.getY());

				}
				//previousMove=new Point((int) previousMove.getX(),(int) previousMove.getY()+MegaManGANUtil.MEGA_MAN_LEVEL_HEIGHT);

			}
			else if(direction==MegaManCPPNtoGANLevelBreederTask.HORIZONTAL_PREFERENCE){
				numHorizontal++;

				d = Direction.HORIZONTAL;
//				if(previous.equals(Direction.UP)) y++;
//				if(previous.equals(Direction.DOWN)) y--;
//				if(previous.equals(Direction.HORIZONTAL)) x++;
				
				levelInListHorizontal = MegaManGANUtil.getLevelListRepresentationFromGAN(ganProcessHorizontal, latentVector);
				distinct.add(levelInListHorizontal.get(0));
				if(level==chunks-1) {
					MegaManGANUtil.placeOrb(levelInListHorizontal.get(0));
				}
				needBackup=false;
				
				if(previous.equals(Direction.UP)) {
					y++;
					MegaManGANUtil.placeUp(levelInListHorizontal, previousMove, oneLevel, 0);
				}
				if(previous.equals(Direction.DOWN)) {
					y--;
					MegaManGANUtil.placeDown(levelInListHorizontal, previousMove, oneLevel, 0);
					previousMove=new Point((int) previousMove.getX(),(int) previousMove.getY()+MegaManGANUtil.MEGA_MAN_LEVEL_HEIGHT);

				}
				if(previous.equals(Direction.HORIZONTAL)) {
					x++;
					MegaManGANUtil.placeRight(levelInListHorizontal, previousMove, oneLevel, nullLine, 0);
					previousMove=new Point((int) previousMove.getX()+MegaManGANUtil.MEGA_MAN_LEVEL_WIDTH,(int) previousMove.getY());

				}
				
				//wasRight = true;
			}
			
			
			if(needBackup) {
				if(bkp == MegaManCPPNtoGANLevelBreederTask.UP_PREFERENCE) {
					numUp++;
					d = Direction.UP;
//					if(previous.equals(Direction.UP)) y++;
//					if(previous.equals(Direction.DOWN)) y--;
//					if(previous.equals(Direction.HORIZONTAL)) x++;
					levelInListUp = MegaManGANUtil.getLevelListRepresentationFromGAN(ganProcessUp, startlatentVector);
					distinct.add(levelInListUp.get(0));
					if(level==chunks-1) {
						MegaManGANUtil.placeOrb(levelInListUp.get(0));
					}
					if(previous.equals(Direction.UP)) {
						y++;
						MegaManGANUtil.placeUp(levelInListUp, previousMove, oneLevel, 0);
					}
					if(previous.equals(Direction.DOWN)) {
						 y--;
						MegaManGANUtil.placeDown(levelInListUp, previousMove, oneLevel, 0);
						previousMove=new Point((int) previousMove.getX(),(int) previousMove.getY()+MegaManGANUtil.MEGA_MAN_LEVEL_HEIGHT);

					}
					if(previous.equals(Direction.HORIZONTAL)) {
						x++;
						MegaManGANUtil.placeRight(levelInListUp, previousMove, oneLevel, nullLine, 0);
						previousMove=new Point((int) previousMove.getX()+MegaManGANUtil.MEGA_MAN_LEVEL_WIDTH,(int) previousMove.getY());

					}

				}
				else if (bkp == MegaManCPPNtoGANLevelBreederTask.DOWN_PREFERENCE) {
					numDown++;
					d = Direction.DOWN;
//					if(previous.equals(Direction.UP)) y++;
//					if(previous.equals(Direction.DOWN)) y--;
//					if(previous.equals(Direction.HORIZONTAL)) x++;
					levelInListDown = MegaManGANUtil.getLevelListRepresentationFromGAN(ganProcessDown, startlatentVector);
					distinct.add(levelInListDown.get(0));
					if(level==chunks-1) {
						MegaManGANUtil.placeOrb(levelInListDown.get(0));
					}
					if(previous.equals(Direction.UP)) {
						y++;
						MegaManGANUtil.placeUp(levelInListDown, previousMove, oneLevel, 0);
					}
					if(previous.equals(Direction.DOWN)) {
						y--;
						MegaManGANUtil.placeDown(levelInListDown, previousMove, oneLevel, 0);
						previousMove=new Point((int) previousMove.getX(),(int) previousMove.getY()+MegaManGANUtil.MEGA_MAN_LEVEL_HEIGHT);

					}
					if(previous.equals(Direction.HORIZONTAL)) {
						x++;
						MegaManGANUtil.placeRight(levelInListDown, previousMove, oneLevel, nullLine, 0);
					}
				}
				else {
					numHorizontal++;
					d = Direction.HORIZONTAL;
//					if(previous.equals(Direction.UP)) y++;
//					if(previous.equals(Direction.DOWN)) y--;
//					if(previous.equals(Direction.HORIZONTAL)) x++;
					levelInListHorizontal = MegaManGANUtil.getLevelListRepresentationFromGAN(ganProcessHorizontal, startlatentVector);
					distinct.add(levelInListHorizontal.get(0));
					if(level==chunks-1) {
						MegaManGANUtil.placeOrb(levelInListHorizontal.get(0));
					}
					if(previous.equals(Direction.UP)) {
						y++;
						MegaManGANUtil.placeUp(levelInListHorizontal, previousMove, oneLevel, 0);
					}
					if(previous.equals(Direction.DOWN)) {
						y--;
						MegaManGANUtil.placeDown(levelInListHorizontal, previousMove, oneLevel, 0);
						previousMove=new Point((int) previousMove.getX(),(int) previousMove.getY()+MegaManGANUtil.MEGA_MAN_LEVEL_HEIGHT);

					}
					if(previous.equals(Direction.HORIZONTAL)) {
						x++;
						MegaManGANUtil.placeRight(levelInListHorizontal, previousMove, oneLevel, nullLine, 0);
						previousMove=new Point((int) previousMove.getX()+MegaManGANUtil.MEGA_MAN_LEVEL_WIDTH,(int) previousMove.getY());

					}
				}

			}
			if(!d.equals(previous)) {
				numCorner++;
			}
			previous = d;
			numDistinctSegments = distinct.size();
		}
		
		
//		ganProcessUp.terminate();
//		ganProcessDown.terminate();
//		ganProcessHorizontal.terminate();
		if(!Parameters.parameters.booleanParameter("megaManUsesUniqueEnemies")) {
			MegaManGANUtil.postProcessingPlaceProperEnemies(oneLevel);
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
