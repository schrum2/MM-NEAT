package edu.southwestern.tasks.megaman;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import edu.southwestern.networks.Network;
import edu.southwestern.tasks.mario.gan.GANProcess;
import edu.southwestern.tasks.megaman.astar.MegaManState;
import edu.southwestern.tasks.megaman.gan.MegaManGANUtil;
import edu.southwestern.util.stats.StatisticsUtilities;

public class MegaManCPPNtoGANUtil {
	public static GANProcess ganProcessHorizontal = null;
	public static GANProcess ganProcessVertical = null;
	public static GANProcess ganProcessUp = null;
	public static GANProcess ganProcessDown = null;
	MegaManCPPNtoGANUtil(){
		ganProcessHorizontal = MegaManGANUtil.initializeGAN("MegaManGANHorizontalModel");
		ganProcessDown= MegaManGANUtil.initializeGAN("MegaManGANDownModel");
		ganProcessUp = MegaManGANUtil.initializeGAN("MegaManGANUpModel");
//		ganProcessDown = new GANProcess(GANProcess.PYTHON_BASE_PATH+"MegaManGAN"+ File.separator + Parameters.parameters.stringParameter("MegaManGANDownModel"), 
//		Parameters.parameters.integerParameter("GANInputSize"), 
//		/*Parameters.parameters.stringParameter("MegaManGANModel").startsWith("HORIZONTALONLYMegaManAllLevel") ? */MegaManGANUtil.MEGA_MAN_ALL_TERRAIN /*: MegaManGANUtil.MEGA_MAN_FIRST_LEVEL_ALL_TILES*/,
//		GANProcess.MEGA_MAN_OUT_WIDTH, GANProcess.MEGA_MAN_OUT_HEIGHT);
		MegaManGANUtil.startGAN(ganProcessUp);
		MegaManGANUtil.startGAN(ganProcessVertical);
		MegaManGANUtil.startGAN(ganProcessHorizontal);
	}
	public enum Direction {UP, DOWN, HORIZONTAL};
	public static List<List<Integer>> cppnToMegaManLevel(Network cppn, int chunks, double[] inputMultipliers){
		int x = 0;
		int y = 0;
		int xpref  = 0;
		int ypref = 1;
		int biaspref = 2;
		List<List<List<Integer>>> levelInListHorizontal;
		List<List<List<Integer>>> levelInListUp;
		List<List<List<Integer>>> levelInListDown;
		List<List<Integer>> oneLevel;
//		levelInListHorizontal = MegaManGANUtil.getLevelListRepresentationFromGAN(ganProcessHorizontal, latentVector);
//		levelInListUp = MegaManGANUtil.getLevelListRepresentationFromGAN(ganProcessUp, latentVector);
//		levelInListDown = MegaManGANUtil.getLevelListRepresentationFromGAN(ganProcessDown, latentVector);
		Direction d;
		double[] startfull = cppn.process(new double[] {inputMultipliers[xpref] * x/chunks, inputMultipliers[ypref]*y/chunks,  inputMultipliers[biaspref] * 1.0});
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
			d = Direction.UP;
			y++;
			levelInListUp = MegaManGANUtil.getLevelListRepresentationFromGAN(ganProcessUp, startlatentVector);
			oneLevel = levelInListUp.get(0);
		}
		else if (startdirection == MegaManCPPNtoGANLevelBreederTask.DOWN_PREFERENCE) {
			d = Direction.DOWN;
			y--;
			levelInListDown = MegaManGANUtil.getLevelListRepresentationFromGAN(ganProcessDown, startlatentVector);
			oneLevel = levelInListDown.get(0);
		}
		else {
			d = Direction.HORIZONTAL;
			x++;
			levelInListHorizontal = MegaManGANUtil.getLevelListRepresentationFromGAN(ganProcessHorizontal, startlatentVector);
			oneLevel = levelInListHorizontal.get(0);
		}
		MegaManGANUtil.placeSpawn(oneLevel);
		if(chunks==1) {
			MegaManGANUtil.placeOrb(oneLevel);
		}
		List<Integer> nullLine = new ArrayList<Integer>(16);
		for(int i=0;i<MegaManGANUtil.MEGA_MAN_LEVEL_WIDTH;i++) {
			nullLine.add(MegaManState.MEGA_MAN_TILE_NULL);
		}
		Point previousMove = new Point(0,0);
		for(int level = 1; level <= chunks; level++) {
			
			double[] full = cppn.process(new double[] {inputMultipliers[xpref] * x/chunks, inputMultipliers[ypref]*y/chunks,  inputMultipliers[biaspref] * 1.0});
			double[] latentVector = new double[full.length-3];
			for(int i = 3;i<full.length;i++) {
				latentVector[i-3]=full[i];
			}
			double[] outputs = new double[3];
			for(int i =0;i<3;i++) {
				outputs[i]=full[i];
			}
			int direction = StatisticsUtilities.argmax(outputs);
			double[] backup = new double[3];
			for(int i = 0;i<3;i++) {
				if(i!=direction) {
					backup[i]=outputs[i];
				}
			}
			int bkp = StatisticsUtilities.argmax(backup);
			boolean needBackup = true;
			
			
			if(direction == MegaManCPPNtoGANLevelBreederTask.UP_PREFERENCE&&!d.equals(Direction.DOWN)) {
				d = Direction.UP;
				y++;
				levelInListUp = MegaManGANUtil.getLevelListRepresentationFromGAN(ganProcessUp, latentVector);
				if(level==chunks-1) {
					MegaManGANUtil.placeOrb(levelInListUp.get(level));
				}
				needBackup=false;
				MegaManGANUtil.placeUp(levelInListUp, previousMove, oneLevel, level);
				//wasRight = true;
				//previousMove=new Point((int) previousMove.getX()+MegaManGANUtil.MEGA_MAN_LEVEL_WIDTH,(int) previousMove.getY());

			}
			else if (direction == MegaManCPPNtoGANLevelBreederTask.DOWN_PREFERENCE&&!d.equals(Direction.UP)) {
				d = Direction.DOWN;
				y--;
				levelInListDown = MegaManGANUtil.getLevelListRepresentationFromGAN(ganProcessDown, latentVector);
				if(level==chunks-1) {
					MegaManGANUtil.placeOrb(levelInListDown.get(level));
				}
				needBackup=false;
				MegaManGANUtil.placeDown(levelInListDown, previousMove, oneLevel, level);
				previousMove=new Point((int) previousMove.getX(),(int) previousMove.getY()+MegaManGANUtil.MEGA_MAN_LEVEL_HEIGHT);

			}
			else if(direction==MegaManCPPNtoGANLevelBreederTask.HORIZONTAL_PREFERENCE){
				d = Direction.HORIZONTAL;
				x++;
				levelInListHorizontal = MegaManGANUtil.getLevelListRepresentationFromGAN(ganProcessHorizontal, latentVector);
				if(level==chunks-1) {
					MegaManGANUtil.placeOrb(levelInListHorizontal.get(level));
				}
				needBackup=false;
				
				
				MegaManGANUtil.placeRight(levelInListHorizontal, previousMove, oneLevel, nullLine, level);
				//wasRight = true;
				previousMove=new Point((int) previousMove.getX()+MegaManGANUtil.MEGA_MAN_LEVEL_WIDTH,(int) previousMove.getY());
			}
			
			
			if(needBackup) {
				if(bkp == MegaManCPPNtoGANLevelBreederTask.UP_PREFERENCE) {
					d = Direction.UP;
					y++;
					levelInListUp = MegaManGANUtil.getLevelListRepresentationFromGAN(ganProcessUp, startlatentVector);
					if(level==chunks-1) {
						MegaManGANUtil.placeOrb(levelInListUp.get(level));
					}
					MegaManGANUtil.placeUp(levelInListUp, previousMove, oneLevel, level);


				}
				else if (bkp == MegaManCPPNtoGANLevelBreederTask.DOWN_PREFERENCE) {
					d = Direction.DOWN;
					y--;
					levelInListDown = MegaManGANUtil.getLevelListRepresentationFromGAN(ganProcessDown, startlatentVector);
					if(level==chunks-1) {
						MegaManGANUtil.placeOrb(levelInListDown.get(level));
					}
					MegaManGANUtil.placeDown(levelInListDown, previousMove, oneLevel, level);
					previousMove=new Point((int) previousMove.getX(),(int) previousMove.getY()+MegaManGANUtil.MEGA_MAN_LEVEL_HEIGHT);
				}
				else {
					d = Direction.HORIZONTAL;
					x++;
					levelInListHorizontal = MegaManGANUtil.getLevelListRepresentationFromGAN(ganProcessHorizontal, startlatentVector);
					if(level==chunks-1) {
						MegaManGANUtil.placeOrb(levelInListHorizontal.get(level));
					}
					MegaManGANUtil.placeRight(levelInListHorizontal, previousMove, oneLevel, nullLine, level);
					previousMove=new Point((int) previousMove.getX()+MegaManGANUtil.MEGA_MAN_LEVEL_WIDTH,(int) previousMove.getY());
				}

			}
		}
		return oneLevel;
	}
}
