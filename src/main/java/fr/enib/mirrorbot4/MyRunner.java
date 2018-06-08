package fr.enib.mirrorbot4;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObject;
import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.AgentInfo;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.Players;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.Senses;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.IUT2004PathRunner;
import cz.cuni.amis.pogamut.ut2004.bot.command.AdvancedLocomotion;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Move;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPointNeighbourLink;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import cz.cuni.amis.utils.NullCheck;

/**
 * Responsible for direct running to location.
 *
 * <p>This class commands the agent directly to the given location. Silently
 * tries to resolve incidental collisions, troubling pits, obstacles, etc.
 * In other words, give me a destination and you'll be there in no time.</p>
 *
 * <h4>Precise jumper</h4>
 *
 * Most of the incident running problems and troubles can be solved by precise
 * single-jumping or double-jumping. This class calculates the best spots for
 * initiating such jumps and then follows jump sequences in order to nicely
 * jump and then land exactly as it was desired.
 *
 * <h4>Pogamut troubles</h4>
 *
 * This class was supposed to use autotrace rays to scan the space and ground
 * in from of the agent. However, results of depending on these traces were
 * much worst than jumping whenever possible. Therefore, no autotrace is being
 * used and the agent simply jumps a lot. Some human players do that as well.
 * See {@link #runToLocation } for details.
 *
 * <h4>Speed</h4>
 *
 * The agent does not ever try to run faster than with speed of <i>1.0</i> as
 * it is used by most of <i>body.runTo*()</i> methods. Anyway, speeding is not
 * available to common players (AFAIK), so why should this agent cheat?
 *
 * <h4>Focus</h4>
 *
 * This class works with destination location as well as agent focal point.
 * Since the agent can look at something else rather than the destination,
 * this running API is also suitable for engaging in combat or escaping from
 * battles.
 *
 * @author Juraj Simlovic [jsimlo@matfyz.cz]
 */
public class MyRunner implements IUT2004PathRunner {
    /**
     * Number of steps we have taken.
     */
    private int runnerStep = 0;

    /**
     * Jumping sequence of a single-jumps.
     */
    private int runnerSingleJump = 0;
    /**
     * Jumping sequence of a double-jumps.
     */
    private int runnerDoubleJump = 0;

    /**
     * Collision counter.
     */
    private int collisionCount = 0;
    
    /**
     * Collision location.
     */
    private Location collisionSpot = null;

	/*========================================================================*/
	
	/**
	 * Transform movement direction
	 */
	
	private RayData rayData;
	
	public void setRayData(RayData rd){
		rayData = rd;
	}
	
	public RayData getRayData(){
		return rayData;
	}
	
	private void movementFilter(Location firstLocation, Location secondLocation, ILocated focus){
		Location toFirstLoc = firstLocation.sub(memory.getLocation()).getNormalized();
		Location avoidance = getEnemyAvoidance();
		if (avoidance.getLength() > 0.0) toFirstLoc = toFirstLoc.add(avoidance);
		firstLocation = memory.getLocation().add(toFirstLoc.getNormalized().scale(300.0));
		bot.getAct().act(addFocus(new Move().setFirstLocation(firstLocation).setSecondLocation(secondLocation), focus));
	}
	
	@SuppressWarnings("unused")
	private void keyboardMove(Location firstLocation, Location secondLocation, ILocated focus){
		//if (firstLocation != null) firstLocation = convertToKeyboardLocation(firstLocation);
		//if (secondLocation != null) secondLocation = convertToKeyboardLocation(secondLocation);
		
		bot.getAct().act(addFocus(new Move().setFirstLocation(firstLocation).setSecondLocation(secondLocation), focus));
	}
	
	@SuppressWarnings("unused")
	private Location convertToKeyboardLocation(Location loc){
		if ((bot.getLocation() == null) || (bot.getRotation() == null)) return loc;
		Location myPos = bot.getLocation();
		Location myRot = bot.getRotation().toLocation();
		//myRot.setTo(myRot.getX(), myRot.getY(), 0.0);
		myRot.setX(myRot.getX());
		myRot.setY(myRot.getY());
		myRot.setZ(0.0);
		myRot = myRot.getNormalized();
		Location movDir = loc.sub(myPos);
		
		Location keybDir = myPos;
		if ((movDir.getLength() > 0.0) && (myRot.getLength() > 0.0)){
			Location forwardBack = myRot.scale(myRot.dot(movDir));
			Location leftRight = movDir.sub(forwardBack);
			
			if (forwardBack.getLength() < 40.0) {
				//forwardBack.setTo(0.0, 0.0, 0.0);
				forwardBack.setX(0.0);
				forwardBack.setY(0.0);
				forwardBack.setZ(0.0);
			}else forwardBack = forwardBack.getNormalized();
			
			if (leftRight.getLength() < 40.0) {
				//leftRight.setTo(0.0, 0.0, 0.0);
				leftRight.setX(0.0);
				leftRight.setY(0.0);
				leftRight.setZ(0.0);
			}
			else leftRight = leftRight.getNormalized();
			
			Location result = forwardBack.add(leftRight);
			
			if (result.getLength() > 0.0){
				result = result.getNormalized().scale(memory.getBaseSpeed()); //(movDir.getLength()/10.0);
				keybDir = keybDir.add(result);
			}
		}
		return keybDir;
	}
	
	public Location getEnemyAvoidance(){
		//avoid eye contact
		Location avoidance = new Location(0.0, 0.0, 0.0);
		Location avoidanceNoShoot = new Location(0.0, 0.0, 0.0);
		Map<UnrealId, Player> players = rayData.getCtrl().getPlayers().getVisiblePlayers();
		for (Map.Entry<UnrealId,Player> entry : players.entrySet()){
			// Unused: schrum: 6/7/19
			//UnrealId key=entry.getKey();
			Player player=entry.getValue();

			Location pLoc = player.getLocation();
			Location pRot = player.getRotation().toLocation();
			Location aimVec = pRot.getNormalized();
			//aimVec.setTo(aimVec.getX(), aimVec.getY(), -aimVec.getZ());
			aimVec.setX(aimVec.getX());
			aimVec.setY(aimVec.getY());
			aimVec.setZ(aimVec.getZ());
			Location posVec = rayData.getCtrl().getInfo().getLocation().sub(pLoc).getNormalized();
			double dotprod = posVec.dot(aimVec);
			
			if (dotprod > 0.90){
				if (player.getFiring() > 0){
					avoidance = avoidance.add(posVec.sub(aimVec.scale(dotprod)).getNormalized());
				}else{
					//aimVec.setTo(Math.random(), Math.random(), 0.0);
					aimVec.setX(Math.random());
					aimVec.setY(Math.random());
					aimVec.setZ(Math.random());
					dotprod = posVec.dot(aimVec);
					avoidanceNoShoot = avoidanceNoShoot.add(posVec.sub(aimVec.scale(dotprod)).getNormalized());
				}
			}
		}
		
		if (avoidance.getLength() > 0.0) avoidance = avoidance.getNormalized();
		if ((avoidanceNoShoot.getLength() > 0.0) && (avoidance.getLength() == 0.0)) avoidance = avoidance.add(avoidanceNoShoot.getNormalized());
		if (avoidance.getLength() > 0.0) avoidance = avoidance.getNormalized();
		
		return avoidance;
	}
	
    /*========================================================================*/

    /**
     * Initializes direct running to the given destination.
     */
    public void reset(){
        // reset working info
        runnerStep = 0;
        runnerSingleJump = 0;
        runnerDoubleJump = 0;
        collisionCount = 0;
        collisionSpot = null;
    }
	
	public void stopMovement(ILocated focus){
		rayData.getCtrl().getMove().stopMovement();
		reset();
		if (focus != null){
			rayData.getCtrl().getMove().turnTo(focus);
		}
	}

    /*========================================================================*/
    
    private Move addFocus(Move move, ILocated focus) {
    	if (focus != null) {
    		if (focus instanceof Player && ((IWorldObject)focus).getId() instanceof UnrealId) {
    			move.setFocusTarget((UnrealId)((IWorldObject)focus).getId());
    		} else {	
    			move.setFocusLocation(focus.getLocation());
    		}
    	}
    	return move;
    }
    
    /**
     * Added by J. Schrum: 5/25/18
     * For Pogamut 3.7.0 compatibility.
     * 
     * The only difference between this and the existing method with the same name below seems to be the
     * forceNoJump boolean parameter, which I think means that jumping is NOT allowed if the value is TRUE.
     * If that's the case, then the other version of runToLocation can exhibit the same behavior but with 
     * an int parameter, where 1 means jumping is forbidden and 0 means the bot can choose. 
     * 
     * TODO: Follow up to make sure this is correct (change if bot jumps too much in weird ways).
     */
	@Override
	public boolean runToLocation(Location runningFrom, Location firstLocation, Location secondLocation, ILocated focus,
			NavPointNeighbourLink navPointsLink, boolean reachable, boolean forceNoJump) {
		return runToLocation(runningFrom, firstLocation, secondLocation, focus, navPointsLink, reachable, forceNoJump ? 1 : 0);
	}
        
    /**
     * Handles running directly to the specified location.
     *
     * <h4>Pogamut troubles</h4>
     *
     * <p>Reachchecks are buggy (they ignore most of the pits). Autotrace rays
     * are buggy (they can not be used to scan the ground). Now, how's the agent
     * supposed to travel along a map full of traps, when he is all blind, his
     * guide-dogs are stupid and blind as well and his white walking stick is
     * twisted?</p>
     *
     * <p>There is only one thing certain here (besides death and taxes): No
     * navpoint is ever placed above a pit or inside map geometry. But, navpoint
     * positions are usually the only places where we know the ground is safe.
     * So, due to all this, the agent tries to jump whenever possible and still
     * suitable for landing each jump on a navpoint. This still helps overcome
     * most of the map troubles. Though it is counter-productive at times.</p>
     *
     * @param fromLocation location we're running from, may be null
     * @param firstLocation Location to which to run.
     * @param secondLocation Location where to continue (may be null).
     * @param focus Location to which to look.
     * @param reachable Whether the location is reachable.
     * @return True, if no problem occured.
     */
	public boolean runToLocation (Location fromLocation, Location firstLocation, Location secondLocation, ILocated focus, NavPointNeighbourLink navPointsLink, boolean reachable){
		return runToLocation (fromLocation, firstLocation, secondLocation, focus, navPointsLink, reachable, 0);
	}
	
	//jumptype: 0=choose, 1=forbidden, 2=forced
    public boolean runToLocation (Location fromLocation, Location firstLocation, Location secondLocation, ILocated focus, NavPointNeighbourLink navPointsLink, boolean reachable, int jumpType){

        if (log != null && log.isLoggable(Level.FINER)) {
        	log.finer("Runner.runToLocation(): runnerStep is "
	                + runnerStep + ", reachable is " + reachable + ",  navPointsLink is" + navPointsLink);            
        }
        
        // take another step
        runnerStep++;

        // wait for delayed start: this is usually used for waiting
        // in order to ensure the previous runner request completion
        if (runnerStep <= 0) {
        	// TODO: [Jimmy] what's this? It is never effective, as we're increasing runnerStep in the previous statement
            return true;
        }

        // are we just starting a new runner request? the first step should
        // always be like this, in order to gain speed/direction before jumps
        if (runnerStep <= 1){
            // start running to that location..
            movementFilter(firstLocation, secondLocation, focus);

             // This heuristics works when the bot continues movement - e.g. when the jump is in the same direction as
             // current velocity vector and we have already some speed
             if ((navPointsLink != null) && (
                    navPointsLink.isForceDoubleJump()
                || (navPointsLink.getNeededJump() != null)
                || (navPointsLink.getFlags() & 8) != 0
                )) {
                 // some jumplinks (R_Jump) are leading down, we jump only when we are going up or to approx. same level
                 if (((navPointsLink.getFlags() & 8) == 0) || (memory.getLocation().z - 100 <= firstLocation.z)) { 
                    Location direction = Location.sub(firstLocation, memory.getLocation()).getNormalized();
                    Location velocityDir = new Location(memory.getVelocity().asVector3d()).getNormalized();
                    Double result = Math.acos(direction.dot(velocityDir));
					
					if (jumpType != 1){ //if jump not forbidden
						// we will jump if our speed is reasonable and our direction differs max. 20 degrees
						// or just jump if it's forced !
						if ((jumpType==2) || (memory.getVelocity().size() > 200 && !result.isNaN() && result < (Math.PI / 9)))
							return resolveJump(firstLocation, secondLocation, focus, navPointsLink, reachable);
					}
                 }
             }
            return true;
        }

        // are we single-jumping already?
        if (runnerSingleJump > 0){
            // continue with the single-jump
            return iterateSingleJumpSequence (firstLocation, secondLocation, focus, reachable);
        }else if (runnerDoubleJump > 0){// are we double-jumping already?
            // continue with the double-jump
            return iterateDoubleJumpSequence (firstLocation, secondLocation, focus, reachable);
        }
        // collision experienced?
        if (senses.isCollidingOnce()){
            // try to resolve it
            return resolveCollision (firstLocation, secondLocation, focus, reachable);
        }
        // are we going to jump now?
        else 
        if (
            // the agent is not jumping already
            (runnerSingleJump == 0) && (runnerDoubleJump == 0)
            &&
            (
                // is the destination directly unreachable?
                !reachable
                // is there an unpleasant pit ahead?
                || (navPointsLink != null) &&
                (
                    navPointsLink.isForceDoubleJump()
                || (navPointsLink.getNeededJump() != null)
                || (navPointsLink.getFlags() & 8) != 0
                )
                // note: see pogamut notes in javadoc above
                //|| true
                // is there an unpleasant wall ahead?
                // note: see pogamut notes in javadoc above
                //|| true
                // are we going to jump just because we want to show off?
                //|| (Math.random () < .03)
            )
        ) 
        {   // try to start a jump
            return resolveJump (firstLocation, secondLocation, focus, navPointsLink, reachable);
        }

        // otherwise: just keep running to that location..
        movementFilter(firstLocation, secondLocation, focus);

        if (log != null && log.isLoggable(Level.FINER)) {
     	            log.finer("Runner.runToLocation(): issuing default move command to: " + firstLocation);
        }
        return true;
    }

    /*========================================================================*/

    /**
     * Tries to resolve collisions.
     *
     * <p>Only continuous collisions are resolved, first by a double jump, then
     * by a single-jump.</p>
     *
     * @param firstLocation Location to which to run.
     * @param secondLocation Location where to continue (may be null).
     * @param focus Location to which to look.
     * @param reachable Whether the location is reachable.
     * @return True, if no problem occured.
     */
    private boolean resolveCollision (Location firstLocation, Location secondLocation, ILocated focus, boolean reachable){
        // are we colliding at a new spot?
        if (
            // no collision yet
            (collisionSpot == null)
            // or the last collision is far away
            || (memory.getLocation().getDistance2D(collisionSpot) > 120)
        ) {
            // setup new collision spot info
        	if (log != null && log.isLoggable(Level.FINER)) { 
	            log.finer("Runner.resolveCollision(): collision at "
	            			+ (int) memory.getLocation().getDistance2D(firstLocation));
        	}
            collisionSpot = memory.getLocation();
            collisionCount = 1;
            // meanwhile: keep running to the location..
            movementFilter(firstLocation, secondLocation, focus);
            return true;
        }
        // so, we were already colliding here before..
        // try to solve the problem according to how long we're here..
        else 
        	switch (collisionCount++ % 2) {
            case 0:
                // ..first by a double jump sequnce
            	if (log != null && log.isLoggable(Level.FINER)) { 
	                log.finer("Runner.resolveCollision(): repeated collision (" + collisionCount + "):"
	                		  + " double-jumping at " + (int) memory.getLocation().getDistance2D(firstLocation));
            	}
                return initDoubleJumpSequence (firstLocation, secondLocation, focus, reachable);

            default:
                // ..then by a single-jump sequence
            	if (log != null && log.isLoggable(Level.FINER)) 
	                log.finer(
	                    "Runner.resolveCollision(): repeated collision (" + collisionCount + "):"
	                    + " single-jumping at " + (int) memory.getLocation().getDistance2D(firstLocation)
	                );
                return initSingleJumpSequence (firstLocation, secondLocation, focus, reachable);
        	}
    }

    /*========================================================================*/

    /**
     * Starts a new (single or double)-jump sequence based on the distance.
     *
     * <p>Due to inevitability of ensuring of landing on destination locations,
     * jumps may only be started, when it is appropriate. This method decides,
     * whether and which jump would be appropriate and the initiates jumping
     * sequence.</p>
     *
     * @param firstLocation Location to which to run.
     * @param secondLocation Location where to continue (may be null).
     * @param focus Location to which to look.
     * @param reachable Whether the location is reachable.
     * @return True, if no problem occured.
     */
    private boolean resolveJump (Location firstLocation, Location secondLocation, ILocated focus, NavPointNeighbourLink navPointsLink, boolean reachable){    		
        // get the distance of the target location
        int distance = (int) memory.getLocation().getDistance2D(firstLocation);
        // get the agent overall velocity
        int velocity = (int) memory.getVelocity().size();

        // cut the jumping distance of the next jump.. this is to allow to
        // jump more than once per one runner request, while ensuring that
        // the last jump will always land exactly on the destination..
        int jumpDistance = distance % 1000;
                
        // get the agent z-distance (e.g. is the destination above/below?)..     
        int zDistance = (int) firstLocation.getDistanceZ(memory.getLocation());
        
        // IF zDistance < 0 THEN WE'RE JUMPING DOWN!
        
        // adjust jumping distance for jumps into lower/higher positions
        jumpDistance += Math.min (200, Math.max (-200, zDistance));
        
        if (log != null && log.isLoggable(Level.FINER)) {
        	log.finer("Runner.resolveJump: distance = " + distance + ", velocity = " + velocity + ", jumpDistance = " + jumpDistance + ", zDistance = " + zDistance);        
        }
        
        // TODO: [Jimmy] test it!
        boolean enforceDoubleJump = false;
        if ((navPointsLink != null) 
        	 && 
        	( (navPointsLink.getNeededJump() != null || (navPointsLink.getFlags() & 8 ) != 0) 
              && (zDistance > 60 || jumpDistance > 380)
            )
           ){
        	// we should jump && the zDistance between our position and the target is more than 60units
        	// we won't ever make it with ordinary jump
        	enforceDoubleJump = true;
        	if (log != null && log.isLoggable(Level.FINEST)) {
        		log.finest("Runner.resolveJump(): double jump indicated");
        	}
        }

        // we already missed all jumping opportunities
        if (jumpDistance < 370){
            //TODO: test - michal bida
            //We force jump when needed jump is true or when jump flag is set
            if (navPointsLink != null) {
                if (navPointsLink.getNeededJump() != null || (navPointsLink.getFlags() & 8 ) != 0)
                	// TODO: [Jimmy] test it!
                	if (enforceDoubleJump) return initDoubleJumpSequence(firstLocation, secondLocation, focus, reachable);
                    return initSingleJumpSequence (firstLocation, secondLocation, focus, reachable);
            }
            
            // if it's reachable, don't worry, we'll make it no matter what
            // if it's unreachable: well, are we waiting for the next jump?
            if (reachable || (distance >= 1000)){
                // just keep running to that location..
                movementFilter(firstLocation, secondLocation, focus);
                return true;
            }
            // otherwise: we should try to solve the situation here, since
            // the destination is not reachable (i.e. there is an obstacle
            // or a pit ahead).. however, the reachability checks does not
            // work very well, and raycasting is broken too.. well, trying
            // to resolve this situation by a random choice does not work
            // either.. therefore, just keep running to that location and
            // wait for success or timeout, whatever comes first..
            movementFilter(firstLocation, secondLocation, focus);
            return true;
        }
        // this is the right space for a single-jump
        else if (jumpDistance < 470){
        	// is double jump enforced?
        	if (enforceDoubleJump) return initDoubleJumpSequence(firstLocation, secondLocation, focus, reachable);
            // start a single-jump sequences        	
            return initSingleJumpSequence (firstLocation, secondLocation, focus, reachable);
        }
        // we already missed the double-jump opportunity
        // this is the space for waiting for a single-jump
        else if (jumpDistance < 600){
        	// but if the double jump is enforced... we should try that!
        	if (enforceDoubleJump) {
        		// even though we've missed the opportunity, the ordinary jump won't help us!
        		// TODO: [Jimmy] may be we should fail?
        		return initDoubleJumpSequence(firstLocation, secondLocation, focus, reachable);
        	}

            // meanwhile: keep running to the location..
            movementFilter(firstLocation, secondLocation, focus);
            return true;
        }
        // this is the right space for double-jumping
        // but only, if we have the necessary speed
        else if ((jumpDistance < 700) && (velocity > 300)){
        	if (!enforceDoubleJump) {
        		// we do not need double jump
        		// so... don't use double jump when link is R_Jump (for that we need just single jump)
	            if (navPointsLink != null && (navPointsLink.getFlags() & 8 ) != 0) {
	                // meanwhile: keep running to the location..
	                movementFilter(firstLocation, secondLocation, focus);
	                return true;
	            }
        	}
        	// we truly need the double jump! single jump won't take us too the desired target!
	           
            // start double-jump sequence by double-jump command
            return initDoubleJumpSequence (firstLocation, secondLocation, focus, reachable);
        }
        // otherwise, wait for the right double-jump distance
        // meanwhile: keep running to the location..
        movementFilter(firstLocation, secondLocation, focus);
        return true;
    }

    /*========================================================================*/

    /**
     * Initiates new single-jump sequence.
     *
     * <p>Single-jump sequences are used to ensure that no single-jump is ever
     * turned accidentally into a semi-double-jump. Such mishaps could lead to
     * overjumping the desired landing location.</p>
     *
     * @param firstLocation Location to which to run.
     * @param secondLocation Location where to continue (may be null).
     * @param focus Location to which to look.
     * @param reachable Whether the location is reachable.
     * @return True, if no problem occured.
     */
    private boolean initSingleJumpSequence (Location firstLocation, Location secondLocation, ILocated focus, boolean reachable){
        // do not allow two jumping sequences
        if ((runnerSingleJump > 0) || (runnerDoubleJump > 0))
            throw new RuntimeException ("jumping sequence aleady started");

        if (log != null && log.isLoggable(Level.FINER)) log.finer("Runner.initSingleJumpSequence() !");
        
        // point to the destination
        movementFilter(firstLocation, secondLocation, focus);
        // issue jump command
        body.jump ();
        // and setup sequence
        runnerSingleJump = 1;
        return true;
    }

    /**
     * Follows single-jump sequence steps.
     * @param firstLocation Location to which to run.
     * @param secondLocation Location where to continue (may be null).
     * @param focus Location to which to look.
     * @param reachable Whether the location is reachable.
     * @return True, if no problem occured.
     */
    private boolean iterateSingleJumpSequence (Location firstLocation, Location secondLocation, ILocated focus, boolean reachable){
        // get the distance of the target location
        int distance = (int) memory.getLocation().getDistance2D(firstLocation);
        // get the agent vertical velocity (e.g. is the agent jumping/falling?)..
        int zVelocity = (int) memory.getVelocity().z;

        // what phase of the single-jump sequence?
        switch (runnerSingleJump){
            // the first phase: wait for the jump
            case 1:
                // did the agent started the jump already?
                if (zVelocity > 100){
                    // continue the jump sequence by waiting for a peak
                	if (log != null && log.isLoggable(Level.FINER)) {
                		log.finer("Runner.iterateSingleJumpSequence(): single-jump registered at " + distance + ", z-velo " + zVelocity);
                	}
                    runnerSingleJump++;
                }
                // meanwhile: just wait for the jump to start
                movementFilter(firstLocation, secondLocation, focus);
                return true;

            //  the last phase: finish the jump
            default:
                // did the agent started to fall already
                if (zVelocity <= 0){
                    // kill the single-jump sequence
                	if (log != null && log.isLoggable(Level.FINER)) {
                		log.finer("Runner.iterateSingleJumpSequence(): single-jump completed at " + distance + ", z-velo " + zVelocity);
                	}
                    runnerSingleJump = 0;
                }
                // meanwhile: just wait for the jump to start
                movementFilter(firstLocation, secondLocation, focus);
                return true;
        }
    }

    /*========================================================================*/

    /**
     * Initiates new double-jump sequence.
     *
     * <p>Double-jump sequences are used to ensure that the agent correctly
     * claims the double-jump boost at the jump peak.</p>
     *
     * @param firstLocation Location to which to run.
     * @param secondLocation Location where to continue (may be null).
     * @param focus Location to which to look.
     * @param reachable Whether the location is reachable.
     * @return True, if no problem occured.
     */
    private boolean initDoubleJumpSequence (Location firstLocation, Location secondLocation, ILocated focus, boolean reachable){
        // do not allow two jumping sequences
        if ((runnerSingleJump > 0) || (runnerDoubleJump > 0))
            throw new RuntimeException ("jumping sequence aleady started");

        // point to the destination
        //body.strafeTo(firstLocation, focus);

        // init double jump parameters to perform FULL DOUBLE JUMP
        boolean doubleJump = true;
        double delay = 0.39;
        double jumpZ = 680;        
                
        // if we want to jump 70 units up we use -> delay 0.39 / jumpZ 680
        double distanceZ = firstLocation.getDistanceZ(memory.getLocation());
        double distance2D = firstLocation.getDistance2D(memory.getLocation());
        if (log != null && log.isLoggable(Level.FINER)) {
        	log.finer("Runner.initDoubleJumpSequence(): disntane2D = " + distance2D + ", distanceZ = " + distanceZ);
        }
        if (distanceZ > 0) {
        	if (log != null && log.isLoggable(Level.FINER)) {
        		log.finer("Runner.initDoubleJumpSequence(): JUMPING UP! Adjusting parameters of the jump...");
        	}
        	
        	double  jumpZ_up = 680 * distanceZ / 70;
        	boolean doubleJump_up = jumpZ_up > 340;
        	
        	double  jumpZ_forward = 680;
        	boolean doubleJump_forward = true;
        	if (distance2D < 250) {
        		// single jump suffice
        		doubleJump_forward = false;
        		jumpZ_forward = 340 * distance2D / 250;
        	} else
        	if (distance2D < 350) {
        		// smaller double jump is needed
        		jumpZ_forward = 340 + 340 * (distance2D - 250) / 100;
        	}
        	
        	if (jumpZ_up > jumpZ_forward) {        		
        		jumpZ = jumpZ_up;
        		doubleJump = doubleJump_up;
        		if (log != null && log.isLoggable(Level.FINER)) log.finer("Runner.initDoubleJumpSequence(): jumping up more than jumping forward, jumpZ_up = " + jumpZ_up + " > " + jumpZ_forward + " = jumpZ_forward");
        	} else {
        		jumpZ = jumpZ_forward;
        		doubleJump = doubleJump_forward;
        		if (log != null && log.isLoggable(Level.FINER)) log.finer("Runner.initDoubleJumpSequence(): jumping forward more than jumping up, jumpZ_up = " + jumpZ_up + " < " + jumpZ_forward + " = jumpZ_forward");
        	}
        
        } else {
        	if (log != null && log.isLoggable(Level.FINER)) log.finer("Runner.initDoubleJumpSequence(): FALLING DOWN! Adjusting parameters of the jump for falling...");
        	// we're going to fall, thus we have to be careful not to overjump the target
        	
        	// if we would be just falling (without any jump) we would move in 2D about 451 units when falling 382 units down (normal gravity)
        	double distanceTravelledByFalling = 451/382 * Math.abs(distanceZ);
        	// remainind distance for which we need jumping
        	double remainingDistance2D = distance2D - distanceTravelledByFalling;
        	
        	// single jump will get us about 300 forward
        	// double jump will get us about 450 forward
        	// -- note that above two constants taking into account also a jump itself (it gets us higher so falling down will take us further),
        	//    theoretically, we should compute much more complex equation but this seems to work OK
        	
        	if (remainingDistance2D < 300) {
        		if (log != null && log.isLoggable(Level.FINER)) log.finer("Runner.initDoubleJumpSequence(): single jump suffice, distance2D = " + distance2D + ", estimated distance travelled by just falling = " + distanceTravelledByFalling + ", remaining distance 2D to jump = " + remainingDistance2D);
        		doubleJump = false;
        		jumpZ = 340 * remainingDistance2D / 300;
        	} else
        	if (remainingDistance2D < 450) {
        		if (log != null && log.isLoggable(Level.FINER)) log.finer("Runner.initDoubleJumpSequence(): smaller double jump is needed, distance2D = " + distance2D + ", estimated distance travelled by just falling = " + distanceTravelledByFalling + ", remaining distance 2D to jump = " + remainingDistance2D);
        		jumpZ = 340 + 340 * (remainingDistance2D - 220) * 150;
        	} else {
        		if (log != null && log.isLoggable(Level.FINER)) log.finer("Runner.initDoubleJumpSequence(): full double jump is needed, distance2D = " + distance2D + ", estimated distance travelled by just falling = " + distanceTravelledByFalling + ", remaining distance 2D to jump = " + remainingDistance2D);
        		// preform full DOUBLE JUMP
        	}
        }
        
        if (log != null && log.isLoggable(Level.FINER)) {
        	log.finer("Runner.initDoubleJumpSequence(): " + (doubleJump ? "double jumping, double jump delay = " + delay : "single jumping") + ", jumpZ = " + jumpZ);
        }
	        
	    // make some inferation about the doubleJump parameters 
	        
	    // issue jump command
	    body.doubleJump(delay, jumpZ);
	    // and setup sequence
	    runnerDoubleJump = 1;
	    return true;        
    }

    /**
     * Follows double-jump sequence steps.
     * @param firstLocation Location to which to run.
     * @param secondLocation Location where to continue (may be null).
     * @param focus Location to which to look.
     * @param reachable Whether the location is reachable.
     * @return True, if no problem occured.
     */
    private boolean iterateDoubleJumpSequence (Location firstLocation, Location secondLocation, ILocated focus, boolean reachable){
        // get the distance of the target location
        int distance = (int) memory.getLocation().getDistance2D(firstLocation);
        // get the agent vertical velocity (e.g. is the agent jumping/falling?)..
        int zVelocity = (int) memory.getVelocity().z;

        // what phase of the double-jump sequence?
        switch (runnerDoubleJump){
            // the first phase: wait for the jump
            case 1:
                // did the agent started the jump already?
                if (zVelocity > 100){
                    // continue the double-jump sequence by waiting for a peak
                    if (log != null && log.isLoggable(Level.FINER)) {
                    	log.finer("Runner.iterateDoubleJumpSequence(): double-jump registered at " + distance + ", z-velo " + zVelocity);
                    }
                    runnerDoubleJump++;
                }
                // meanwhile: just wait for the jump to start
                movementFilter(firstLocation, secondLocation, focus);
                return true;

            // the second phase: claim the extra boost at jump peak..
            case 2:
                // is this the awaited jump peak?
                if (zVelocity < 150){
                    // continue the double-jump sequence by a single jump boost
                    if (log != null && log.isLoggable(Level.FINER)) {
                    	log.finer("Runner.iterateDoubleJumpSequence(): double-jump boost at " + distance + ", z-velo " + zVelocity);
                    }
                    body.jump ();
                    runnerDoubleJump++;
                    return true;
                }
                // meanwhile: just wait for the jump peak
                movementFilter(firstLocation, secondLocation, focus);
                return true;

            // the last phase:  finish the double-jump
            default:
                // did the agent started to fall already
                if (zVelocity <= 0){
                    // kill the doule-jump sequence
                    runnerDoubleJump = 0;
                }
                // meanwhile: just wait for the agent to start falling
                movementFilter(firstLocation, secondLocation, focus);
                return true;
        }
    }

    /*========================================================================*/

    /** Agent's bot. */
    @SuppressWarnings("rawtypes")
	protected UT2004Bot bot;
    /** Loque memory. */
    protected AgentInfo memory;
    /** Agent's body. */
    protected AdvancedLocomotion body;
    /** Agent's log. */
    protected Logger log;
    /** Base agent's senses. */
	protected Senses senses;

    /*========================================================================*/

    /**
     * Constructor.
     * @param bot Agent's bot.
     * @param memory Loque memory.
     */
    public MyRunner (@SuppressWarnings("rawtypes") UT2004Bot bot, AgentInfo agentInfo, AdvancedLocomotion locomotion, Logger log) {
        // setup reference to agent
    	NullCheck.check(bot, "bot");
    	this.bot = bot;
        NullCheck.check(agentInfo, "agentInfo");
        this.memory = agentInfo;
        NullCheck.check(locomotion, "locomotion");
        this.body = new AdvancedLocomotion(bot, log);
        this.senses = new Senses(bot, memory, new Players(bot), log);
        this.log = log;
    }

}
