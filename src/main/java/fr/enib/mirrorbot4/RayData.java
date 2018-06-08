package fr.enib.mirrorbot4;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.base3d.worldview.object.Velocity;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.RemoveRay;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.AutoTraceRay;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;
import cz.cuni.amis.pogamut.ut2004.utils.UnrealUtils;
import cz.cuni.amis.utils.flag.FlagListener;
import java.util.ArrayList;
import javax.vecmath.Vector3d;

/**
 * interprets data from the ray traces
 * @author Mihai Polceanu
 */
public class RayData{
	private UT2004BotModuleController ctrl;
	
	private ArrayList<AutoTraceRay> rayList;
	private ArrayList<AutoTraceRay> downRayList;
	private ArrayList<AutoTraceRay> upRayList;
    private int nrRays = 16;
	private int nrDownRays = 8;
	private int nrUpRays = 0;
	private double timeInZone = 0.0;
	
	/**
	 * sets up the class and controller
	 * @param c (controller for the bot)
	 */
	public RayData(UT2004BotModuleController c){
		ctrl = c;
		
		//------------- initialize rayCasting data -----------------------------
		
		rayList = new ArrayList<AutoTraceRay>();
		downRayList = new ArrayList<AutoTraceRay>();
		upRayList = new ArrayList<AutoTraceRay>();
		//int rayLength = (int) (UnrealUtils.CHARACTER_COLLISION_RADIUS * 10);
		
		// settings for the rays
		boolean fastTrace = true;			// perform only fast trace == we just need true/false information
		boolean floorCorrection = false;	// provide floor-angle correction for the ray (when the bot is running on the skewed floor, the ray gets rotated to match the skew)
		boolean traceActor = false;			// whether the ray should collid with other actors == bots/players as well

		// 1. remove all previous rays, each bot starts by default with three
		// rays, for educational purposes we will set them manually
		ctrl.getAct().act(new RemoveRay("All"));

		for (int i=0; i<nrRays; ++i){
			double vx = Math.cos((Math.PI*2.0*((double)i))/((double)nrRays));
			double vy = Math.sin((Math.PI*2.0*((double)i))/((double)nrRays));
			double vz = -0.1; //((i%2==0)?(-0.05):(0.05));
			double len = Math.sqrt(vx*vx + vy*vy + vz*vz);
			vx /= len;
			vy /= len;
			vz /= len;
			Vector3d vec = new Vector3d(vx, vy, vz);
			ctrl.getRaycasting().createRay("ray"+Integer.toString(i), vec, (int)(UnrealUtils.CHARACTER_COLLISION_RADIUS * 10), fastTrace, ((i%2==0)?floorCorrection:!floorCorrection), traceActor);
		}
		
		for (int i=0; i<nrDownRays; ++i){
			double vx = Math.cos((Math.PI*2.0*((double)i))/((double)nrDownRays));
			double vy = Math.sin((Math.PI*2.0*((double)i))/((double)nrDownRays));
			double vz = -1.0;
			double len = Math.sqrt(vx*vx + vy*vy + vz*vz);
			vx /= len;
			vy /= len;
			vz /= len;
			Vector3d vec = new Vector3d(vx, vy, vz);
			ctrl.getRaycasting().createRay("downray"+Integer.toString(i), vec, (int)(UnrealUtils.CHARACTER_COLLISION_RADIUS * 10), fastTrace, floorCorrection, traceActor);
		}

		for (int i=0; i<nrUpRays; ++i){
			double vx = Math.cos((Math.PI*2.0*((double)i))/((double)nrUpRays));
			double vy = Math.sin((Math.PI*2.0*((double)i))/((double)nrUpRays));
			double vz = 0.5;
			double len = Math.sqrt(vx*vx + vy*vy + vz*vz);
			vx /= len;
			vy /= len;
			vz /= len;
			Vector3d vec = new Vector3d(vx, vy, vz);
			ctrl.getRaycasting().createRay("upray"+Integer.toString(i), vec, (int)(UnrealUtils.CHARACTER_COLLISION_RADIUS * 10), fastTrace, floorCorrection, traceActor);
		}
		
		// register listener called when all rays are set up in the UT engine
		ctrl.getRaycasting().getAllRaysInitialized().addListener(new FlagListener<Boolean>(){
			@Override
			public void flagChanged(Boolean changedValue){
				for (int i=0; i<nrRays; ++i){
					rayList.add(ctrl.getRaycasting().getRay("ray"+Integer.toString(i)));
				}				
				for (int i=0; i<nrDownRays; ++i){
					downRayList.add(ctrl.getRaycasting().getRay("downray"+Integer.toString(i)));
				}				
				for (int i=0; i<nrUpRays; ++i){
					upRayList.add(ctrl.getRaycasting().getRay("upray"+Integer.toString(i)));
				}
			}
		});

		ctrl.getRaycasting().endRayInitSequence();

		//ctrl.getConfig().setDrawTraceLines(true); //TODO : COMMENT THIS LINE
		ctrl.getConfig().setAutoTrace(true);
	}
	
	/**
	 * @return returns the bot controller
	 */
	public UT2004BotModuleController getCtrl(){
		return ctrl;
	}
	
	/**
	 * @param dt (not sure what this does)
	 * @return returns the normal direction
	 */
	public Location getNormalDirection(double dt){
		if (!ctrl.getRaycasting().getAllRaysInitialized().getFlag()){
			System.out.println("RAYCASTING ERROR: rays not initialized properly");
            return ctrl.getInfo().getLocation();
        }
		
		Location moveDir = ctrl.getInfo().getVelocity().asLocation();
		Velocity zoneVel = ctrl.getInfo().getCurrentZoneVelocity();
		if ((zoneVel != null) && (zoneVel.asLocation().getLength() > 0.0)){
			if (Math.sin(timeInZone*Math.PI/20.0) >= 0.0) timeInZone += dt;
			else timeInZone += dt/2.0;
			if (Math.sin(timeInZone*Math.PI/20.0) >= 0.0) { //start by opposing direction for ~20secs, then let loose for another 40
				if (moveDir.getLength() > 0.0){
					Location vel = zoneVel.asLocation().getNormalized();
					double dotprod = moveDir.dot(vel);
					if (dotprod > 0.0){ //if going in the direction of the waves, try to turn around
						moveDir = moveDir.add(vel.scale(-2*dotprod));
					}
				}
				else{
					moveDir = ctrl.getInfo().getCurrentZoneVelocity().asLocation().scale(-1.0);
				}
				//System.out.println("Water velocity = "+ctrl.getInfo().getCurrentZoneVelocity());
			}
		}else{
			timeInZone = 0.0;
		}
		
		if (ctrl instanceof MirrorBot4)	{
			Item item = ((MirrorBot4)(ctrl)).getBrain().getClosestSuperImportantItemTo(ctrl.getInfo().getLocation(), 1000);
			if ((item != null) && (item.getLocation() != null)){
				Location impLoc = item.getLocation().sub(ctrl.getInfo().getLocation());
				if (impLoc.getZ() < 200) moveDir = impLoc;
			}
		}
		
		if (moveDir.getLength() > 0.0) {
			moveDir = moveDir.getNormalized();
		}else{
			moveDir = new Location(Math.random()-0.5, Math.random()-0.5, 0.0);
			moveDir = moveDir.getNormalized();
		}
		
		Location result = new Location(0,0,0);
		for (int i=0; i<rayList.size(); ++i){
			if (!rayList.get(i).isResult()){
				Location myRot = ctrl.getInfo().getRotation().toLocation().getNormalized();

				double dxy=Math.sqrt(myRot.getX()*myRot.getX() + myRot.getY()*myRot.getY());
				double yawOut=((dxy!=0) ? Math.atan2(myRot.getY(),myRot.getX()) : 0.0);
				//double pitchOut=((dxy!=0)||(myRot.getZ()!=0) ? -Math.atan2(myRot.getZ(),dxy) : 0.0);

				double vx = Math.cos(yawOut+(Math.PI*2.0*((double)i))/((double)nrRays));
				double vy = Math.sin(yawOut+(Math.PI*2.0*((double)i))/((double)nrRays));
				double vz = 0.0;

				Location rayDir = new Location(vx, vy, vz);

				double dot = moveDir.dot(rayDir);
				if (dot < -1.0) dot = -1.0;
				else if (dot > 1.0) dot = 1.0;

				dot = (1.0+dot)/2.0;

				if (dot < 0.1) dot = 0.1;
				rayDir = rayDir.scale(dot);
				result = result.add(rayDir);
			}
		}
		if (result.getLength() > 0.0) return (result.getNormalized());
		return moveDir;
	}
	
	public Location getDownDirection(){
		if (!ctrl.getRaycasting().getAllRaysInitialized().getFlag()){
			System.out.println("RAYCASTING ERROR: rays not initialized properly");
            return ctrl.getInfo().getLocation();
        }
		
		Location moveDir = ctrl.getInfo().getVelocity().asLocation();
		if (moveDir.getLength() > 0.0){
			moveDir = moveDir.getNormalized();
		}else{
			moveDir = new Location(Math.random()-0.5, Math.random()-0.5, 0.0);
			moveDir = moveDir.getNormalized();
		}
		
		Location result = new Location(0,0,0);
		for (int i=0; i<downRayList.size(); ++i){
			if (!downRayList.get(i).isResult()){
				Location myRot = ctrl.getInfo().getRotation().toLocation().getNormalized();

				double dxy=Math.sqrt(myRot.getX()*myRot.getX() + myRot.getY()*myRot.getY());
				double yawOut=((dxy!=0) ? Math.atan2(myRot.getY(),myRot.getX()) : 0.0);
				//double pitchOut=((dxy!=0)||(myRot.getZ()!=0) ? -Math.atan2(myRot.getZ(),dxy) : 0.0);

				double vx = Math.cos(yawOut+(Math.PI*2.0*((double)i))/((double)nrDownRays));
				double vy = Math.sin(yawOut+(Math.PI*2.0*((double)i))/((double)nrDownRays));
				double vz = 0.0;

				Location rayDir = new Location(vx, vy, vz);

				double dot = moveDir.dot(rayDir);
				if (dot < -1.0) dot = -1.0;
				else if (dot > 1.0) dot = 1.0;

				dot = (1.0+dot)/2.0;

				if (dot < 0.1) dot = 0.1;
				rayDir = rayDir.scale(-dot);
				result = result.add(rayDir);
			}
		}
		
		if (result.getLength() > 0.0) return (result.getNormalized());
		return result;
	}
	/*
	public Location getUpDirection(){
		if (!ctrl.getRaycasting().getAllRaysInitialized().getFlag()){
			System.out.println("RAYCASTING ERROR: rays not initialized properly");
            return ctrl.getInfo().getLocation();
        }
		
		Location moveDir = ctrl.getInfo().getVelocity().asLocation();
		if (moveDir.getLength() > 0.0){
			moveDir = moveDir.getNormalized();
		}
		else{
			moveDir = new Location(Math.random()-0.5, Math.random()-0.5, 0.0);
			moveDir = moveDir.getNormalized();
		}
		
		Location result = new Location(0,0,0);
		for (int i=0; i<upRayList.size(); ++i){
			if (upRayList.get(i).isResult()){
				Location myRot = ctrl.getInfo().getRotation().toLocation().getNormalized();

				double dxy=Math.sqrt(myRot.getX()*myRot.getX() + myRot.getY()*myRot.getY());
				double yawOut=((dxy!=0) ? Math.atan2(myRot.getY(),myRot.getX()) : 0.0);
				//double pitchOut=((dxy!=0)||(myRot.getZ()!=0) ? -Math.atan2(myRot.getZ(),dxy) : 0.0);

				double vx = Math.cos(yawOut+(Math.PI*2.0*((double)i))/((double)nrUpRays));
				double vy = Math.sin(yawOut+(Math.PI*2.0*((double)i))/((double)nrUpRays));
				double vz = 0.0;

				Location rayDir = new Location(vx, vy, vz);

				double dot = moveDir.dot(rayDir);
				if (dot < -1.0) dot = -1.0;
				else if (dot > 1.0) dot = 1.0;
				dot = (1.0+dot)/2.0;
				if (dot < 0.1) dot = 0.1;
				rayDir = rayDir.scale(-dot); /////// probably not suitable for up
				result = result.add(rayDir);
			}
		}
		
		return (result.getNormalized());
	}
	*/
}
