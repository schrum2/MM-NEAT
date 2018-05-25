package fr.enib.mirrorbot4;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.event.WorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.base.utils.math.DistanceUtils;
import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.AgentInfo;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.UT2004AgentInfo;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.AbstractUT2004PathNavigator;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.loquenavigator.LoqueNavigator;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.loquenavigator.LoqueRunner;
import cz.cuni.amis.pogamut.ut2004.bot.command.AdvancedLocomotion;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Mover;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPointNeighbourLink;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Self;
import cz.cuni.amis.pogamut.ut2004.utils.LinkFlag;
import cz.cuni.amis.pogamut.ut2004.utils.UnrealUtils;

/**
 * Responsible for navigation to location.
 *
 * <p>This class navigates the agent along path. Silently handles all casual
 * trouble with preparing next nodes, running along current nodes, switching
 * between nodes at appropriate distances, etc. In other words, give me a
 * destination and a path and you'll be there in no time.</p>
 *
 * <h4>Preparing ahead</h4>
 *
 * Nodes in the path are being prepared ahead, even before they are actually
 * needed. The agent decides ahead, looks at the next nodes while still running
 * to current ones, etc.
 *
 * <h4>Reachability checks</h4>
 *
 * Whenever the agent switches to the next node, reachcheck request is made to
 * the engine. The navigation routine then informs the {@link LoqueRunner}
 * beneath about possible troubles along the way.
 *
 * <h4>Movers</h4>
 *
 * This class was originally supposed to contain handy (and fully working)
 * navigation routines, including safe navigation along movers. However, the
 * pogamut platform is not quite ready for movers yet. Especial when it comes
 * to mover frames and correct mover links.
 *
 * <p>Thus, we rely completely on navigation points. Since the mover navigation
 * points (LiftCenter ones) always travel with the associated mover, we do not
 * try to look for movers at all. We simply compare navigation point location
 * to agent's location and wait or move accordingly.</p>
 *
 * <h4>Future</h4>
 *
 * The bot could check from time to time, whether the target destination he is
 * traveling to is not an empty pickup spot, since the memory is now capable of
 * reporting empty pickups, when they are visible. The only pitfall to this is
 * the way the agent might get <i>trapped</i> between two not-so-far-away items,
 * each of them empty. The more players playe the same map, the bigger is the
 * chance of pickup emptyness. The agent should implement a <i>fadeing memory
 * of which items are empty</i> before this can be put safely into logic.
 *
 * @author Juraj Simlovic [jsimlo@matfyz.cz]
 * @author Jimmy
 */
public class MyNavigator<PATH_ELEMENT extends ILocated> extends AbstractUT2004PathNavigator<PATH_ELEMENT>
{
    /**
     * Current navigation destination.
     */
    private Location navigDestination = null;

    /**
     * Current stage of the navigation.
     */
    private MyNavigator.Stage navigStage = MyNavigator.Stage.COMPLETED;

    /**
     * Current focus of the bot, if null, provide default focus.
     * <p><p>
     * Filled at the beginning of the {@link LoqueNavigator#navigate(ILocated, int)}.
     */
	private ILocated focus = null;
	
	/**
	 * {@link Self} listener.
	 */
	private class SelfListener implements IWorldObjectEventListener<Self, WorldObjectUpdatedEvent<Self>>
	{
		private IWorldView worldView;

		/**
		 * Constructor. Registers itself on the given WorldView object.
		 * @param worldView WorldView object to listent to.
		 */
		public SelfListener(IWorldView worldView)
		{
			this.worldView = worldView;
			worldView.addObjectListener(Self.class, WorldObjectUpdatedEvent.class, this);
		}

		@Override
		public void notify(WorldObjectUpdatedEvent<Self> event) {
			self = event.getObject();			
		}
	}
	
	/** {@link Self} listener */
	private MyNavigator.SelfListener selfListener;

    /*========================================================================*/

    /**
     * Distance, which is considered as close enough for considering the bot to be AT LOCATION/NAV POINT.
     * 
     * If greater than 50, navigation will failed on DM-Flux2 when navigating between health vials in one of map corers.
     */
    public static final int CLOSE_ENOUGH = 50;

    /*========================================================================*/
    
	@Override
	protected void navigate(ILocated focus, int pathElementIndex) {
		if (log != null && log.isLoggable(Level.FINE)) log.fine("Current stage: " + navigStage);
		this.focus = focus;
		switch (navigStage = keepNavigating()) {
		case AWAITING_MOVER:
		case RIDING_MOVER:
			setBotWaiting(true);
			break;
		case TELEPORT:
		case NAVIGATING:
		case REACHING:
			setBotWaiting(false);
			break;
		
		case TIMEOUT:
		case CRASHED:
		case CANCELED:
			if (log != null && log.isLoggable(Level.WARNING)) log.warning("Navigation " + navigStage);
			executor.stuck();
			return;
		
		case COMPLETED:
			executor.targetReached();
			break;
		}
		if (log != null && log.isLoggable(Level.FINE)) log.fine("Next stage: " + navigStage);
	}
	
	@Override
	public void reset() {
		// reinitialize the navigator's values
		
		navigCurrentLocation = null;
		navigCurrentNode = null;
        navigCurrentLink = null;
		navigDestination = null;
		navigIterator = null;
		navigLastLocation = null;
		navigLastNode = null;
		navigNextLocation = null;
		navigNextNode = null;
		navigNextLocationOffset = 0;
		navigStage = MyNavigator.Stage.COMPLETED;
		setBotWaiting(false);
		
		resetNavigMoverVariables();
	}

	// J. Schrum removed this override to make compatible with Pogamut 3.7.0
	//@Override
	public void newPath(List<PATH_ELEMENT> path) {
		// prepare for running along new path
		reset();
		
		// 1) obtain the destination
		Location dest = path.get(path.size()-1).getLocation();
				
		// 2) init the navigation
		initPathNavigation(dest, path);		
	}
	
	public NavPointNeighbourLink getCurrentLink() {
		return navigCurrentLink;
	}
	
	/*========================================================================*/
	
	/**
     * Initializes direct navigation to the specified destination.
     * @param dest Destination of the navigation.
     * @param timeout Maximum timeout of the navigation. Use 0 to auto-timeout.
     */
    protected void initDirectNavigation (Location dest)
    {
        // calculate destination distance
        int distance = (int) memory.getLocation().getDistance(dest);
        // init the navigation
        if (log != null && log.isLoggable(Level.FINE)) log.fine (
            "LoqueNavigator.initDirectNavigation(): initializing direct navigation"
            + ", distance " + distance
        );
        // init direct navigation
        initDirectly (dest);
    }
	
	/*========================================================================*/

    /**
     * Initializes navigation to the specified destination along specified path.
     * @param dest Destination of the navigation.
     * @param path Navigation path to the destination.
     */
    protected void initPathNavigation(Location dest, List<PATH_ELEMENT> path)
    {
        // init the navigation
        if (log != null && log.isLoggable(Level.FINE)) 
        	log.fine (
        			"LoqueNavigator.initPathNavigation(): initializing path navigation"
        			+ ", nodes " + path.size ()
        	);
        // init path navigation
        if (!initAlongPath(dest, path))
        {
            // do it directly then..
            initDirectNavigation(dest);
        }
    }

    /*========================================================================*/

    /**
     * Navigates with the current navigation request.
     * @return Stage of the navigation progress.
     */
    protected MyNavigator.Stage keepNavigating ()
    {
        // is there any point in navigating further?
        if (navigStage.terminated)
            return navigStage;
      
        if (log != null && log.isLoggable(Level.FINE)) {
        	if (navigLastNode != null) {
        		log.fine("LoqueNavigator.keepNavigating(): navigating from " + navigLastNode.getId().getStringId() + navigLastNode.getLocation() );    	
        	} else
        	if (navigLastLocation != null) {
        		log.fine("LoqueNavigator.keepNavigating(): navigating from " + navigLastLocation + " (navpoint is unknown)" );
        	}
        	if (navigCurrentNode != null) {
        		log.fine("LoqueNavigator.keepNavigating(): navigating to   " + navigCurrentNode.getId().getStringId() + navigCurrentNode.getLocation() );
        	} else
        	if (navigCurrentLocation != null) {
        		log.fine("LoqueNavigator.keepNavigating(): navigating to       " + navigCurrentLocation + " (navpoint is unknown)" );
        	}
        	if (navigLastLocation != null && navigCurrentLocation != null) {
        		log.fine("LoqueNavigator.keepNavigating(): distance in-between " + navigCurrentLocation.getDistance(navigLastLocation));
        	}
        }
        
        // try to navigate
        switch (navigStage)
        {
            case REACHING:
                navigStage = navigDirectly();
                break;
            default:
                navigStage = navigAlongPath();
                break;
        }

        // return the stage
        if (log != null && log.isLoggable(Level.FINEST)) log.finest ("Navigator.keepNavigating(): navigation stage " + navigStage);
        return navigStage;
    }

    /*========================================================================*/

    /**
     * Initializes direct navigation to given destination.
     * 
     * @param dest Destination of the navigation.
     * @return Next stage of the navigation progress.
     */
    private MyNavigator.Stage initDirectly(Location dest)
    {
        // setup navigation info
        navigDestination = dest;
        // init runner
        runner.reset();
        // reset navigation stage
        return navigStage = MyNavigator.Stage.REACHING;
    }

    /**
     * Tries to navigate the agent directly to the navig destination.
     * @return Next stage of the navigation progress.
     */
    private MyNavigator.Stage navigDirectly ()
    {    	
        // get the distance from the target
        int distance = (int) memory.getLocation().getDistance(navigDestination);

        // are we there yet?
        if (distance <= CLOSE_ENOUGH)
        {
            if (log != null && log.isLoggable(Level.FINE)) log.fine ("LoqueNavigator.navigDirectly(): destination close enough: " + distance);
            return MyNavigator.Stage.COMPLETED;
        }

        // run to that location..
        if (!runner.runToLocation (navigLastLocation, navigDestination, null, (focus == null ? navigDestination : focus), null, true))
        {
            if (log != null && log.isLoggable(Level.FINE)) log.fine ("LoqueNavigator.navigDirectly(): direct navigation failed");
            return MyNavigator.Stage.CRASHED;
        }

        // well, we're still running
        if (log != null && log.isLoggable(Level.FINEST)) log.finer ("LoqueNavigator.navigDirectly(): traveling directly, distance = " + distance);
        return navigStage;
    }

    /*========================================================================*/

    /**
     * Iterator through navigation path.
     */
    private Iterator<PATH_ELEMENT> navigIterator = null;
    
    /**
     * How many path elements we have iterated over before selecting the current {@link LoqueNavigator#navigNextLocation}.
     */
    private int navigNextLocationOffset = 0;

    /**
     * Last location in the path (the one the agent already reached).
     */
    private Location navigLastLocation = null;
    
    /**
     * If {@link LoqueNavigator#navigLastLocation} is a {@link NavPoint} or has NavPoint near by, its instance
     * is written here (null otherwise).
     */
    private NavPoint navigLastNode = null;

    /**
     * Current node in the path (the one the agent is running to).
     */
    private Location navigCurrentLocation = null;
    
    /**
     * If {@link LoqueNavigator#navigCurrentLocation} is a {@link NavPoint} or has NavPoint near by,
     * its instance is written here (null otherwise).
     */
    private NavPoint navigCurrentNode = null;

    /**
     * If moving between two NavPoints {@link NavPoint} the object {@link NeighbourLink} holding infomation
     * about the link (if any) will be stored here (null otherwise).
     */
    private NavPointNeighbourLink navigCurrentLink = null;

    /**
     * Next node in the path (the one being prepared).
     */
    private Location navigNextLocation = null;
    
    /**
     * If {@link LoqueNavigator#navigNextLocation} is a {@link NavPoint} or has NavPoint near by,
     * its instance is written here (null otherwise).
     */
    private NavPoint navigNextNode = null;
    
    /**
     * Returns {@link NavPoint} instance for a given location. If there is no navpoint in the vicinity of {@link LoqueNavigator#CLOSE_ENOUGH}
     * null is returned.
     * 
     * @param location
     * @return
     */
    protected NavPoint getNavPoint(ILocated location) {
    	if (location instanceof NavPoint) return (NavPoint) location;
    	NavPoint np = DistanceUtils.getNearest(main.getWorldView().getAll(NavPoint.class).values(), location);
    	if (np.getLocation().getDistance(location.getLocation()) < CLOSE_ENOUGH) return np;
    	return null;
    }

    /**
     * Initializes navigation along path.
     * @param dest Destination of the navigation.
     * @param path Path of the navigation.
     * @return True, if the navigation is successfuly initialized.
     */
    private boolean initAlongPath(Location dest, List<PATH_ELEMENT> path)
    {
        // setup navigation info
        navigDestination = dest;
        navigIterator = path.iterator();
        // reset current node
        navigCurrentLocation = bot.getLocation();
        navigCurrentNode = DistanceUtils.getNearest(bot.getWorldView().getAll(NavPoint.class).values(), bot.getLocation(), 40);
        // prepare next node
        prepareNextNode();
        // reset navigation stage
        navigStage = MyNavigator.Stage.NAVIGATING;
        // reset node navigation info
        return switchToNextNode ();
    }

    /**
     * Tries to navigate the agent safely along the navigation path.
     * @return Next stage of the navigation progress.
     */
    private MyNavigator.Stage navigAlongPath()
    {
        // get the distance from the destination
        int totalDistance = (int) memory.getLocation().getDistance(navigDestination);

        // are we there yet?
        if (totalDistance <= CLOSE_ENOUGH)
        {
            if (log != null && log.isLoggable(Level.FINEST)) log.finest ("Navigator.navigAlongPath(): destination close enough: " + totalDistance);
            return MyNavigator.Stage.COMPLETED;
        }
        
        // navigate
        if (navigStage.mover) {
        	return navigThroughMover();
        } else
        if (navigStage.teleport) {
        	return navigThroughTeleport();
        } else {
            return navigToCurrentNode(true); // USE FOCUS, normal navigation
        }
    }

    /*========================================================================*/

    /**
     * Prepares next navigation node in path.
     * <p><p>
     * If necessary just recalls {@link LoqueNavigator#prepareNextNodeTeleporter()}.
     */
    private void prepareNextNode ()
    {
    	if (navigCurrentNode != null && navigCurrentNode.isTeleporter()) {
    		// current node is a teleporter! ...
    		prepareNextNodeTeleporter();
    		return;
    	}
    	
        // retreive the next node, if there are any left
        // note: there might be null nodes along the path!
    	ILocated located = null;
        navigNextLocation = null;
        navigNextLocationOffset = 0;
        while ((located == null) && navigIterator.hasNext ())
        {
            // get next node in the path
        	located = navigIterator.next();
        	navigNextLocationOffset += 1;
        	if (located == null) {
        		continue;            
        	}
        }

        // did we get the next node?
        if (located == null) {
        	navigNextLocationOffset = 0;
        	return;
        }
        
        if (executor.getPathElementIndex() + navigNextLocationOffset >= executor.getPath().size()) {
        	navigNextLocationOffset = 0; // WTF?
        }
       
        // obtain next location
        navigNextLocation = located.getLocation();
        // obtain navpoint instance for a given location
        navigNextNode = getNavPoint(located);
    }
    
    /**
     * Prepares next node in the path assuming the currently pursued node is a teleporter.
     */
    private void prepareNextNodeTeleporter() {
    	// Retrieve the next node, if there are any left
        // note: there might be null nodes along the path!
    	ILocated located = null;
        navigNextLocation = null;
        navigNextLocationOffset = 0;
        boolean nextTeleporterFound = false;
        while ((located == null) && navigIterator.hasNext ())
        {
            // get next node in the path
        	located = navigIterator.next();
        	navigNextLocationOffset += 1;
        	if (located == null) {
        		continue;            
        	}
        	navigNextNode = getNavPoint(located);
        	if (navigNextNode != null && navigNextNode.isTeleporter()) {
        		// next node is 
        		if (!nextTeleporterFound) {
        			// ignore first teleporter as it is the other end of the teleporter we're currently trying to enter
        			located = null;
        		}
        		nextTeleporterFound = true;
        	} else {
        		break;
        	}
        }
        
        // did we get the next node?
        if (located == null) {
        	navigNextLocationOffset = 0;
        	return;
        }
        
        if (executor.getPathElementIndex() + navigNextLocationOffset >= executor.getPath().size()) {
        	navigNextLocationOffset = 0; // WTF?
        }
       
        // obtain next location
        navigNextLocation = located.getLocation();
        // obtain navpoint instance for a given location
        navigNextNode = getNavPoint(located);        
    }

    /**
     * Initializes next navigation node in path.
     * @return True, if the navigation node is successfully switched.
     */
    private boolean switchToNextNode ()
    {    	
    	if (log != null && log.isLoggable(Level.FINER)) log.finer ("Navigator.switchToNextNode(): switching!");
    	
        // move the current node into last node
        navigLastLocation = navigCurrentLocation;
        navigLastNode = navigCurrentNode;

        // get the next prepared node
        if (null == (navigCurrentLocation = navigNextLocation))
        {
            // no nodes left there..
            if (log != null && log.isLoggable(Level.FINER)) log.finer ("Navigator.switchToNextNode(): no nodes left");
            navigCurrentNode = null;
            return false;
        }
        // rewrite the navpoint as well
        navigCurrentNode = navigNextNode;

        // store current NavPoint link
        navigCurrentLink = getNavPointsLink(navigLastNode, navigCurrentNode);
        
        if (navigCurrentLink == null) {
        	getNavPointsLink(navigLastNode, navigCurrentNode);
        	if (log.isLoggable(Level.INFO)) {
        		log.info("No link information...");
        	}
        }
        
        // ensure that the last node is not null
        if (navigLastLocation == null) {
            navigLastLocation = bot.getLocation();
            navigLastNode = navigCurrentNode;
        }

        // get next node distance
        int localDistance = (int) memory.getLocation().getDistance(navigCurrentLocation.getLocation());

        if (navigCurrentNode == null) {
        	// we do not have extra information about the location we're going to reach
        	runner.reset();
        	if (log != null && log.isLoggable(Level.FINE)) 
        		log.fine (
                    "LoqueNavigator.switchToNextNode(): switch to next location " + navigCurrentLocation
                    + ", distance " + localDistance
                    + ", mover " + navigStage.mover
                );
        } else {
        	// is this next node a teleporter?
        	if (navigCurrentNode.isTeleporter()) {
        		navigStage = MyNavigator.Stage.TeleporterStage();
        	} else
	        // is this next node a mover?
	        if (navigCurrentNode.isLiftCenter())
	        {
	            // setup mover sequence
	            navigStage = MyNavigator.Stage.FirstMoverStage();	            
	            resetNavigMoverVariables();
	            
	            // AREN'T WE ALREADY ON THE LIFT CENTER?
	            if (memory.getLocation().getDistance(navigCurrentNode.getLocation()) < CLOSE_ENOUGH) {
	            	// YES WE ARE!
	            	navigStage = navigStage.next();
	            }	            
	        } else
	        // are we still moving on mover?
	        if (navigStage.mover)
	        {
	        	navigStage = navigStage.next();
	            // init the runner
	            runner.reset();
	        } else
	        if (navigStage.teleport) {
	        	navigStage = navigStage.next();
	            // init the runner
	            runner.reset();
	        } else
	        // no movers & teleports
	        {
	            // init the runner
	            runner.reset();
	        }
	
	        // switch to next node
	        if (log != null && log.isLoggable(Level.FINE)) 
	        	log.fine (
		            "LoqueNavigator.switchToNextNode(): switch to next node " + navigCurrentNode.getId().getStringId()
		            + ", distance " + localDistance
		            + ", reachable " + isReachable(navigCurrentNode)
		            + ", mover " + navigStage.mover
		        );
        }

        // tell the executor that we have moved in the path to the next element
        if (executor.getPathElementIndex() < 0) {
        	executor.switchToAnotherPathElement(0);
        } else {
        	if (navigNextLocationOffset > 0) {
        		executor.switchToAnotherPathElement(executor.getPathElementIndex()+navigNextLocationOffset);
        	} else {
        		executor.switchToAnotherPathElement(executor.getPathElementIndex());
        	}        	
        }
        navigNextLocationOffset = 0;
        
        prepareNextNode();
        
        if (localDistance < 20) {
        	return switchToNextNode();
        }
        
        return true;
    }
	
    protected boolean isReachable(NavPoint node) {
    	if (node == null) return true;
		int hDistance = (int) memory.getLocation().getDistance2D(node.getLocation());
		int vDistance = (int) node.getLocation().getDistanceZ(memory.getLocation());
		double angle; 
		if (hDistance == 0) {
			angle = vDistance == 0 ? 0 : (vDistance > 0 ? Math.PI/2 : -Math.PI/2);
		} else {
			angle = Math.atan(vDistance / hDistance);
		}
		//return Math.abs(vDistance) < 30 && Math.abs(angle) < Math.PI / 4;
		//return (vDistance < 30) && (angle < Math.PI / 4);
		
		Location downRay = getRunner().getRayData().getDownDirection();
		Location normalRay = getRunner().getRayData().getNormalDirection(0.2); //estimated dt
		
		Location vel = memory.getVelocity().asLocation();
		
		//if (!(vel.getLength() > 0.0))
		//{
		//	Location dir = node.getLocation().sub(memory.getLocation()).getNormalized();
		//	vel = dir;
		//}
		//else
		
		if (vel.getLength() > 0.0)
		{
			vel = vel.getNormalized();
		}
		
		boolean reachable = true;
		if ((vel.getLength() > 0.0) && (downRay.getLength() > 0.0))
		{
			double dot = downRay.dot(vel);
			if (dot < -0.4)
			{
				reachable = false;
			}
		}
		
		if ((vel.getLength() > 0.0) && (normalRay.getLength() > 0.0))
		{
			double dot = normalRay.dot(vel);
			if (dot < -0.1)
			{
				reachable = false;
			}
		}
		
		if (angle > Math.PI/3)
		{
			reachable = false;
		}
		
		return reachable;
	}
	
	//
    // NAVIG MOVER VARIABLES
    //
    
    private int navigMoverRideUpCount;

	private int navigMoverRideDownCount;

	private Boolean navigMoverIsRidingUp;

	private Boolean navigMoverIsRidingDown;
	
	private void resetNavigMoverVariables() {
    	navigMoverIsRidingUp = null;
    	navigMoverIsRidingDown = null;
        navigMoverRideUpCount = 0;
        navigMoverRideDownCount = 0;
	}
	
	private void checkMoverMovement(Mover mover) {
		// ASSUMING THAT MOVER IS ALWAYS ... riding fully UP, riding fully DOWN (or vice versa) and passing all possible exits
		if (mover.getVelocity().z > 0) {
			// mover is riding UP
			if (navigMoverIsRidingUp == null) {
				navigMoverIsRidingUp = true;
				navigMoverIsRidingDown = false;
				navigMoverRideUpCount = 1;
				navigMoverRideDownCount = 0;
			} else
			if (navigMoverIsRidingDown) {
				navigMoverIsRidingUp = true;
				navigMoverIsRidingDown = false;
				++navigMoverRideUpCount;
			}
		} else 
		if (mover.getVelocity().z < 0) {
			// mover is riding DOWN
			if (navigMoverIsRidingDown == null) {
				navigMoverIsRidingUp = false;
				navigMoverIsRidingDown = true;
				navigMoverRideUpCount = 0;
				navigMoverRideDownCount = 1;
			} else 
			if (navigMoverIsRidingUp) {
				navigMoverIsRidingUp = false;
				navigMoverIsRidingDown = true;
				++navigMoverRideDownCount;
			}
		}
	}

	/*========================================================================*/

    /**
     * Gets the link with movement information between two navigation points. Holds
     * information about how we can traverse from the start to the end navigation
     * point.
     * 
     * @return NavPointNeighbourLink or null
     */
    private NavPointNeighbourLink getNavPointsLink(NavPoint start, NavPoint end) {
        if (start == null) {
            //if start NavPoint is not provided, we try to find some
            NavPoint tmp = getNavPoint(memory.getLocation());
            if (tmp != null)
                start = tmp;
            else
                return null;
        }
        if (end == null)
            return null;

        if (end.getIncomingEdges().containsKey(start.getId()))
            return end.getIncomingEdges().get(start.getId());
        
        return null;
    }

    /*========================================================================*/

    /**
     * Tries to navigate the agent safely to the current navigation node.
     * @return Next stage of the navigation progress.
     */
    private MyNavigator.Stage navigToCurrentNode (boolean useFocus)
    {
    	if (navigCurrentNode != null) {
    		// update location of the current place we're reaching ... it might be Mover after all
    		navigCurrentLocation = navigCurrentNode.getLocation();
       	}
    	if (navigNextNode != null) {
    		// update location of the next place we're reaching ... it might be Mover after all
    		navigNextLocation = navigNextNode.getLocation();
    	}
    	
        // get the distance from the current node
        int localDistance = (int) memory.getLocation().getDistance(navigCurrentLocation.getLocation());
        // get the distance from the current node (neglecting jumps)
        int localDistance2 = (int) memory.getLocation().getDistance(
            Location.add(navigCurrentLocation.getLocation(), new Location (0,0,100))
        );
        int distanceZ = (int) memory.getLocation().getDistanceZ(navigCurrentLocation);

        // where are we going to run to
        Location firstLocation = navigCurrentLocation.getLocation();
        // where we're going to continue
		/*
        Location secondLocation = (navigNextNode != null ?
        		 						(navigNextNode.isLiftCenter() || navigNextNode.isLiftExit() ? 
        		 								null // let navigThroughMover() to handle these cases with care!
        		 							:	navigNextNode.getLocation())
        		 						: navigNextLocation);
        */
		Location secondLocation = (navigNextNode != null ? navigNextNode.getLocation() : navigNextLocation);
		
		// and what are we going to look at
        ILocated focus = (this.focus == null || !useFocus ?
        						((navigNextLocation == null) ? firstLocation : navigNextLocation.getLocation())
        						:
        						this.focus
        				 );

        // run to the current node..
        if (!runner.runToLocation (navigLastLocation, firstLocation, secondLocation, focus, navigCurrentLink, (navigCurrentNode == null ? true : isReachable(navigCurrentNode)))) {
            if (log != null && log.isLoggable(Level.FINE)) log.fine ("LoqueNavigator.navigToCurrentNode(): navigation to current node failed");
            return MyNavigator.Stage.CRASHED;
        }

        // we're still running
        if (log != null && log.isLoggable(Level.FINEST)) log.finest ("LoqueNavigator.navigToCurrentNode(): traveling to current node, distance = " + localDistance);

        int testDistance = 200; // default constant suitable for default running 
        if (navigCurrentNode != null && (navigCurrentNode.isLiftCenter() || navigCurrentNode.isLiftExit())) {
        	// if we should get to lift exit or the lift center, we must use more accurate constants
        	testDistance = 150;
        }
        if (navigCurrentLink != null && (navigCurrentLink.getFlags() & LinkFlag.JUMP.get()) != 0) {
        	// we need to jump to reach the destination ... do not switch based on localDistance2
        	localDistance2 = 10000;
        }
        
        if (navigCurrentLocation != null && navigCurrentLocation.equals(executor.getPath().get(executor.getPath().size()-1))
        	|| (!navigIterator.hasNext() && (navigNextLocation == null || navigCurrentLocation == navigNextLocation))) {
        	// if we're going to the LAST POINT ... be sure to get to the exact location in order to ensure pick up of the item
        	testDistance = 2 * ((int) UnrealUtils.CHARACTER_COLLISION_RADIUS);
        }
            
        // are we close enough to switch to the next node? (mind the distanceZ particularly!)
        if ( distanceZ < 40 && ((localDistance < testDistance) || (localDistance2 < testDistance) ))
        {
            // switch navigation to the next node
            if (!switchToNextNode ())
            {
                // switch to the direct navigation
                if (log != null && log.isLoggable(Level.FINE)) log.fine("Navigator.navigToCurrentNode(): switching to direct navigation");
                return initDirectly(navigDestination);
            }
        }

        // well, we're still running
        return navigStage;
    }

    /*========================================================================*/

    /**
     * Tries to navigate the agent safely along mover navigation nodes.
     *
     * <h4>Pogamut troubles</h4>
     *
     * Since the engine does not send enough reasonable info about movers and
     * their frames, the agent relies completely and only on the associated
     * navigation points. Fortunatelly, LiftCenter navigation points move with
     * movers.
     *
     * <p>Well, do not get too excited. Pogamut seems to update the position of
     * LiftCenter navpoint from time to time, but it's not frequent enough for
     * correct and precise reactions while leaving lifts.</p>
     *
     * @return Next stage of the navigation progress.
     */
    private MyNavigator.Stage navigThroughMover ()
    {
    	MyNavigator.Stage stage = navigStage;
    	        
        if (navigCurrentNode == null) {
        	if (log != null && log.isLoggable(Level.WARNING)) log.warning("LoqueNavigator.navigThroughMover("+stage+"): can't navigate through the mover without the navpoint instance (navigCurrentNode == null)");
        	return MyNavigator.Stage.CRASHED;
        }
        
        Mover mover = (Mover) bot.getWorldView().get(navigCurrentNode.getMover());
        if (mover == null) {
        	if (log != null && log.isLoggable(Level.WARNING)) log.warning("LoqueNavigator.navigThroughMover("+stage+"): can't navigate through the mover as current node does not represent a mover (moverId == null): " + navigCurrentNode);
        	return MyNavigator.Stage.CRASHED;
        }
        
        // update navigCurrentLocation as the mover might have moved
        navigCurrentLocation = navigCurrentNode.getLocation();
        
        if (navigNextNode != null) {
        	// update navigNextLocation as the mover might have moved
        	navigNextLocation = navigNextNode.getLocation();
        }
        
        // get horizontal distance from the mover center node ... always POSITIVE
        int hDistance = (int) memory.getLocation().getDistance2D(navigCurrentLocation.getLocation());
        // get vertical distance from the mover center node ... +/- ... negative -> mover is below us, positive -> mover is above us
        int zDistance = (int) navigCurrentLocation.getLocation().getDistanceZ(memory.getLocation());
        // whether mover is riding UP
        boolean moverRidingUp = mover.getVelocity().z > 0;
        // whether mover is riding DOWN
		boolean moverRidingDown = mover.getVelocity().z < 0;
		// whether mover is standing still
		boolean moverStandingStill = Math.abs(mover.getVelocity().z) < Location.DISTANCE_ZERO;
		
		System.out.println(">>>>>>>>>> navigThroughMover <<<<<<<<<<");
        
    	if (navigStage == MyNavigator.Stage.AWAITING_MOVER)
		{
			System.out.println(">>>>> AWAITING");
    		// Aren't we under the mover?
    		//if (zDistance > 50 || !moverStandingStill)
			//if (moverRidingDown)
			System.out.println(">> zDistance: "+zDistance+" (zboost is "+memory.getJumpZBoost()+")");
			if (zDistance > memory.getJumpZBoost())
			{
    			// we're under the mover and the mover is riding up...
    			if (log != null && log.isLoggable(Level.FINER)) {
	            	log.finer (
		                "LoqueNavigator.navigThroughMover("+stage+"): we are UNDER the mover and mover is RIDING UP ... getting back to waiting position"
		                + ", zDistance " + zDistance + ", mover.velocity.z " + mover.getVelocity().z + ", mover " + (moverRidingUp ? "riding UP" : (moverRidingDown ? "riding DOWN" : moverStandingStill ? "standing STILL" : " movement unknown"))
		            );
    			}
	            // run to the last node, the one we need to be waiting on for the mover to come
	        	// WE MUST NOT USE FOCUS! Because we need to see the mover TODO: provide turning behavior, i.e., if focus is set, once in a time turn to then direction
				System.out.println("dbg1");
    			if (!runner.runToLocation(memory.getLocation(), navigLastLocation, null, /*navigCurrentLocation*/focus, null, (navigLastNode == null ? true : isReachable(navigLastNode)), 2)) //force jump
	            {
	                if (log != null && log.isLoggable(Level.FINE)) log.fine ("LoqueNavigator.navigThroughMover("+stage+"): navigation to wait-for-mover node failed");
	                return MyNavigator.Stage.CRASHED;
	            }
    			return navigStage;
    		}
    		
	        // wait for the current node to come close in both, vert and horiz
	        // the horizontal distance can be quite long.. the agent will hop on
	        // TODO: There may be problem when LiftExit is more than 400 ut units far from LiftCenter!
    		
    		if (hDistance > 400) {
    			if (log != null && log.isLoggable(Level.WARNING)) log.warning("LoqueNavigator.navigThroughMover("+stage+"): failed to get onto the mover as its 2D distance is > 400, hDistance " + hDistance + ", unsupported!");
                return MyNavigator.Stage.CRASHED;
    		}
    		/*
	        if (zDistance > 30 && moverRidingUp) // mover is riding UP and is already above us, we won't make it...
	        {
	            // run to the last node, the one we need to be waiting on for the mover to come
	        	if (log != null && log.isLoggable(Level.FINER)) {
	            	log.finer (
		                "LoqueNavigator.navigThroughMover("+stage+"): waiting for the mover to come"
		                + " | zDistance " + zDistance + ", hDistance " + hDistance + ", mover " + (moverRidingUp ? "riding UP" : (moverRidingDown ? "riding DOWN" : moverStandingStill ? "standing STILL" : " movement unknown"))
		                + ", node " + navigCurrentNode.getId().getStringId()
		            );
	        	}
	        	// WE MUST NOT USE FOCUS! Because we need to see the mover TODO: provide turning behavior, i.e., if focus is set, once in a time turn to then direction
				System.out.println("dbg2");
	            if (!runner.runToLocation(memory.getLocation(), navigLastLocation, navigLastLocation, focus, null, (navigLastNode == null ? true : isReachable(navigLastNode)), 2)) //force jump
				{
	                if (log != null && log.isLoggable(Level.FINE)) log.fine ("LoqueNavigator.navigThroughMover("+stage+"): navigation to last node failed");
	                return MyNavigator.Stage.CRASHED;
	            }
	
	            return navigStage;
	        }
	        */
	        // MOVER HAS ARRIVED (at least that what we're thinking so...)
	        if (log != null && log.isLoggable(Level.FINER)) 
		        log.finer (
		            "Navigator.navigThroughMover("+stage+"): mover arrived"		            
		            + " | zDistance " + zDistance + ", hDistance " + hDistance + ", mover " + (moverRidingUp ? "riding UP" : (moverRidingDown ? "riding DOWN" : moverStandingStill ? "standing STILL" : " movement unknown"))
		            + ", node " + navigCurrentNode.getId().getStringId()
		        );
			
			if (!moverStandingStill || moverRidingUp || moverRidingDown)
			{
				runner.stopMovement(focus);
				return navigStage;
			}
	        
	        // LET'S MOVE TO THE LIFT CENTER (do not use focus)
	        return navigToCurrentNode(true);
    	}
		else if (navigStage == MyNavigator.Stage.RIDING_MOVER)
		{
			System.out.println(">>>>> RIDING");
			
    		checkMoverMovement(mover);
    		
    		if (navigMoverRideDownCount > 3 || navigMoverRideUpCount > 3) {
    			// we're riding up & down without any effect ... failure :(
    			if (log != null && log.isLoggable(Level.FINE)) log.fine ("LoqueNavigator.navigThroughMover("+stage+"): navigation to mover exit node failed, we've rided twice up & down and there was no place suitable to exit the mover in order to get to get to " + navigCurrentNode);
	            return MyNavigator.Stage.CRASHED;
    		}
    		
    		if (hDistance > 300) {
    			if (log != null && log.isLoggable(Level.WARNING)) log.warning("LoqueNavigator.navigThroughMover("+stage+"): navigation to mover exit node failed, the node is too far, hDistance " + hDistance + " > 400, unsupported (wiered navigation graph link)");
	            return MyNavigator.Stage.CRASHED;
    		}
			
			zDistance = (int)(navigNextLocation.getZ()-memory.getLocation().getZ());
			
			System.out.println(">>>> zDistance: "+zDistance);
    		
    		// wait for the mover to ride us up/down
    		if ( zDistance > 30 )
			{
 	            // run to the last node, the one we're waiting on
    			if (log != null && log.isLoggable(Level.FINER)) 
 	            	log.finer (
 		                "LoqueNavigator.navigThroughMover("+stage+"): riding the mover"               
 		                + " | zDistance " + zDistance + ", hDistance " + hDistance + ", mover " + (moverRidingUp ? "riding UP" : (moverRidingDown ? "riding DOWN" : moverStandingStill ? "standing STILL" : " movement unknown"))
 		                + ", node " + navigCurrentNode.getId().getStringId()
 		            ); 	
    			runner.stopMovement(focus);
				/*
    			// WE MUST NOT USE FOCUS! We have to see the mover. TODO: provide turning behavior, i.e., turn to desired focus once in a time
 	            if (!runner.runToLocation(memory.getLocation(), memory.getLocation(), null, focus, null, (navigLastNode == null ? true : isReachable(navigLastNode)), 1)) //jump forbidden
 	            {
 	                if (log != null && log.isLoggable(Level.FINE)) log.fine ("LoqueNavigator.navigThroughMover("+stage+"): navigation to last node failed");
 	                return MyNavigator.Stage.CRASHED;
 	            }
 	            // and keep waiting for the mover to go to the correct position
 	            */
 	            return navigStage;
 	        }
    		
    		// MOVER HAS ARRIVED TO POSITION FOR EXIST (at least that what we're thinking so...)
	        if (log != null && log.isLoggable(Level.FINER)) 
		        log.finer (
		            "Navigator.navigThroughMover("+stage+"): exiting the mover"
		            + " | zDistance " + zDistance + ", hDistance " + hDistance + ", mover " + (moverRidingUp ? "riding UP" : (moverRidingDown ? "riding DOWN" : moverStandingStill ? "standing STILL" : " movement unknown"))
		            + ", node " + navigCurrentNode.getId().getStringId()		            
		        );
	        
	        // LET'S MOVE TO THE LIFT EXIT (do not use focus)
	        return navigToCurrentNode(true);
    	}
		else
		{
    		if (log != null && log.isLoggable(Level.WARNING)) {
    			log.warning("Navigator.navigThroughMover("+stage+"): invalid stage, neither AWAITING_MOVER nor RIDING MOVER");
    		}
    		return MyNavigator.Stage.CRASHED;
    	}

    }
    
    /*========================================================================*/
    
    /**
     * Tries to navigate the agent safely to the current navigation node.
     * @return Next stage of the navigation progress.
     */
    private MyNavigator.Stage navigThroughTeleport()
    {
    	if (navigCurrentNode != null) {
    		// update location of the current place we're reaching
    		navigCurrentLocation = navigCurrentNode.getLocation();
    	}
    	
    	if (navigNextNode != null) {
    		// update location of the Next place we're reaching
    		navigNextLocation = navigNextNode.getLocation();
    	}
    	
    	// now we have to compute whether we should switch to another navpoint
        // it has two flavours, we should switch if:
        //			1. we're too near to teleport, we should run into
        //          2. we're at the other end of the teleport, i.e., we've already got through the teleport
        
        // 1. DISTANCE TO THE TELEPORT
        // get the distance from the current node
        int localDistance1_1 = (int) memory.getLocation().getDistance(navigCurrentLocation.getLocation());
        // get the distance from the current node (neglecting jumps)
        int localDistance1_2 = (int) memory.getLocation().getDistance(
            Location.add(navigCurrentLocation.getLocation(), new Location (0,0,100))
        );        
        
        // 2. DISTANCE TO THE OTHER END OF THE TELEPORT
        // ---[[ WARNING ]]--- we're assuming that there is only ONE end of the teleport
        int localDistance2_1 = Integer.MAX_VALUE;
        int localDistance2_2 = Integer.MAX_VALUE;
        for (NavPointNeighbourLink link : navigCurrentNode.getOutgoingEdges().values()) {
        	if (link.getToNavPoint().isTeleporter()) {
        		localDistance2_1 = (int)memory.getLocation().getDistance(link.getToNavPoint().getLocation());
        		localDistance2_2 = (int) memory.getLocation().getDistance(
        	            Location.add(link.getToNavPoint().getLocation(), new Location (0,0,100))
                );        
        		break;
        	}
        }
                
        boolean switchedToNextNode = false;
        // are we close enough to switch to the OTHER END of the teleporter?
        if ( (localDistance2_1 < 200) || (localDistance2_2 < 200))
        {
        	// yes we are! we already passed the teleporter, so DO NOT APPEAR DUMB and DO NOT TRY TO RUN BACK 
            // ... better to switch navigation to the next node
            if (!switchToNextNode ())
            {
                // switch to the direct navigation
                if (log != null && log.isLoggable(Level.FINE)) log.fine ("Navigator.navigToCurrentNode(): switch to direct navigation");
                return initDirectly(navigDestination);
            }
            switchedToNextNode = true;
        }
    	
        // where are we going to run to
        Location firstLocation = navigCurrentLocation.getLocation();
        // where we're going to continue
        Location secondLocation = (navigNextNode != null && !navigNextNode.isLiftCenter() && !navigNextNode.isLiftCenter() ? 
        		                  	navigNextNode.getLocation() :
        		                  	navigNextLocation);
        // and what are we going to look at
        ILocated focus = (this.focus == null ? 
        						((navigNextLocation == null) ? firstLocation : navigNextLocation.getLocation())
        						:
        						this.focus
        				 );

        // run to the current node..
        if (!runner.runToLocation(navigLastLocation, firstLocation, secondLocation, focus, navigCurrentLink, (navigCurrentNode == null ? true : isReachable(navigCurrentNode)))) {
            if (log != null && log.isLoggable(Level.FINE)) log.fine ("LoqueNavigator.navigToCurrentNode(): navigation to current node failed");
            return MyNavigator.Stage.CRASHED;
        }

        // we're still running
        if (log != null && log.isLoggable(Level.FINEST)) log.finest ("LoqueNavigator.navigToCurrentNode(): traveling to current node");        
        
        // now as we've tested the first node ... test the second one
        if ( !switchedToNextNode && ((localDistance1_1 < 200) || (localDistance1_2 < 200)) )
        {
            // switch navigation to the next node
            if (!switchToNextNode ())
            {
                // switch to the direct navigation
                if (log != null && log.isLoggable(Level.FINE)) log.fine ("Navigator.navigToCurrentNode(): switch to direct navigation");
                return initDirectly(navigDestination);
            }
        }

        // well, we're still running
        return navigStage;
    }

    /*========================================================================*/

    /**
     * Enum of types of terminating navigation stages.
     */
    private enum TerminatingStageType {
        /** Terminating with success. */
        SUCCESS (false),
        /** Terminating with failure. */
        FAILURE (true);

        /** Whether the terminating with failure. */
        public boolean failure;

        /**
         * Constructor.
         * @param failure Whether the terminating with failure.
         */
        private TerminatingStageType (boolean failure)
        {
            this.failure = failure;
        }
    };

    /**
     * Enum of types of mover navigation stages.
     */
    private enum MoverStageType {
        /** Waiting for mover. */
        WAITING,
        /** Riding mover. */
        RIDING;
    };
    
    /**
     * Enum of types of mover navigation stages.
     */
    private enum TeleportStageType {
        /** Next navpoint is a teleport */
        GOING_THROUGH;
    };

    /**
     * All stages the navigation can come to.
     */
    public enum Stage
    {
        /**
         * Running directly to the destination.
         */
        REACHING ()
        {
            protected MyNavigator.Stage next () { return this; }
        },
        /**
         * Navigating along the path.
         */
        NAVIGATING ()
        {
            protected MyNavigator.Stage next () { return this; }
        },
        /**
         * Waiting for a mover to arrive.
         */
        AWAITING_MOVER (MyNavigator.MoverStageType.WAITING)
        {
            protected MyNavigator.Stage next () { return RIDING_MOVER; }
        },
        /**
         * Waiting for a mover to ferry.
         */
        RIDING_MOVER (MyNavigator.MoverStageType.RIDING)
        {
            protected MyNavigator.Stage next () { return NAVIGATING; }
        },
        /**
         * Navigation cancelled by outer force.
         */
        CANCELED (MyNavigator.TerminatingStageType.FAILURE)
        {
            protected MyNavigator.Stage next () { return this; }
        },
        /**
         * Navigation timeout reached.
         */
        TIMEOUT (MyNavigator.TerminatingStageType.FAILURE)
        {
            protected MyNavigator.Stage next () { return this; }
        },
        /**
         * Navigation failed because of troublesome obstacles.
         */
        CRASHED (MyNavigator.TerminatingStageType.FAILURE)
        {
            protected MyNavigator.Stage next () { return this; }
        },
        /**
         * Navigation finished successfully.
         */
        COMPLETED (MyNavigator.TerminatingStageType.SUCCESS)
        {
            protected MyNavigator.Stage next () { return this; }
        },
        /**
         * We're going through the teleport.
         */
        TELEPORT (MyNavigator.TeleportStageType.GOING_THROUGH) {
        	protected MyNavigator.Stage next() { return NAVIGATING; };
        };
        

        /*====================================================================*/

        /**
         * Running through the mover.
         */
        private boolean mover;
        /**
         * Whether the nagivation is terminated.
         */
        public boolean terminated;
        /**
         * Whether the navigation has failed.
         */
        public boolean failure;
        /**
         * We're going through the teleport.
         */
        public boolean teleport;

        /*====================================================================*/

        /**
         * Constructor: Not finished, not failed
         */
        private Stage ()
        {
            this.mover = false;
            this.teleport = false;
            this.terminated = false;
            this.failure = false;
        }

        private Stage(MyNavigator.TeleportStageType type) {
        	this.mover = false;
        	this.teleport = true;
        	this.failure = false;
        	this.terminated = false;
        }
        
        /**
         * Constructor: mover.
         * @param type Type of mover navigation stage.
         */
        private Stage (MyNavigator.MoverStageType type)
        {
            this.mover = true;
            this.teleport = false;
            this.terminated = false;
            this.failure = false;
        }

        /**
         * Constructor: terminating.
         * @param type Type of terminating navigation stage.
         */
        private Stage (MyNavigator.TerminatingStageType type)
        {
            this.mover = false;
            this.teleport = false;
            this.terminated = true;
            this.failure = type.failure;
        }

        /*====================================================================*/

        /**
         * Retreives the next step of navigation sequence the stage belongs to.
         * @return The next step of navigation sequence. Note: Some stages are
         * not part of any logical navigation sequence. In such cases, this
         * method simply returns the same stage.
         */
        protected abstract MyNavigator.Stage next ();

        /*====================================================================*/

        /**
         * Returns the first step of mover sequence.
         * @return The first step of mover sequence.
         */
        protected static MyNavigator.Stage FirstMoverStage ()
        {
            return AWAITING_MOVER;
        }
        
        /**
         * Returns the first step of the teleporter sequence.
         * @return
         */
        protected static MyNavigator.Stage TeleporterStage() {
        	return MyNavigator.Stage.TELEPORT;
        }
    }

    /*========================================================================*/

    /**
     * Default: Loque Runner.
     */
    private MyRunner runner;

    /*========================================================================*/

    /** Agent's main. */
    protected UT2004Bot main;
    /** Loque memory. */
    protected AgentInfo memory;
    /** Agent's body. */
    protected AdvancedLocomotion body;
    /** Agent's log. */
    protected Logger log;

    /*========================================================================*/

    /**
     * Constructor.
     * @param main Agent's main.
     * @param memory Loque memory.
     */
    public MyNavigator (UT2004Bot bot, Logger log)
    {
        // setup reference to agent
        this.main = bot;
        this.memory = new UT2004AgentInfo(bot);
        this.body = new AdvancedLocomotion(bot, log);
        this.log = log;

        // create runner object
        this.runner = new MyRunner(bot, memory, body, log);
        
        this.selfListener = new MyNavigator.SelfListener( bot.getWorldView() );
    }
	
	public MyRunner getRunner()
	{
		return this.runner;
	}

	////////////////////////////////////////////////////////////////////////////////
	// Methods below added by J. Schrum 5/25/18 for Pogamut 3.7.0 compatibility
	////////////////////////////////////////////////////////////////////////////////
	
	@Override
	public void newPath(List<PATH_ELEMENT> path, ILocated focus) {
		// The version of newPath already in the code did not use a focus, so this parameter is being ignored
		newPath(path);
	}

	@Override
	public void pathExtended(List<PATH_ELEMENT> path, int currentPathIndex) {
		// J. Schrum: I am unclear on what this method should do, but the bot lacked such a method,
		// so I'm hoping it does not need it.
	}

	@Override
	public Logger getLog() {
		return log;
	}
}