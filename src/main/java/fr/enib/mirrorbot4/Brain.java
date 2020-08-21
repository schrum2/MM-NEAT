package fr.enib.mirrorbot4;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cz.cuni.amis.pogamut.base.agent.navigation.PathExecutorState;
import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.base3d.worldview.object.Rotation;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.UT2004ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Move;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.SetCrouch;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BotDamaged;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;


/**
 * 
 * @author Mihai Polceanu
 */
public class Brain{
	@SuppressWarnings("rawtypes")
	private UT2004BotModuleController ctrl;

	private MirrorModule mirrorModule;
	private RayData rayData;
	private PlayerData playerData;
	private AimData aim;
	private ShootingData shooting;

	private Rotation lastNoiseRotation;
	private long noiseExpiry;

	private long lastRandomAim;

	private long lastHitSimTime;

	private Location lastArchLocation;
	private long lastSeenArch;
	private UnrealId archId;
	private UnrealId usingLastArchLocationFor;

	private long rayCastingTimeTrigger;
	private long rayCastingTimeExpiry;

	private ArrayList<Item> lastPickedItems;

	//private boolean waitingForPathComputation;
	private long waitForPathComputationStart;
	private long maxWaitForPathComputation;

	private int mirrorDamageMeter;

	private int lastBehavior;

	private int STOPPEDcounter;
	private int maxSTOPPED;
	private boolean useSTOPPEDtrigger;

	/**
	 * Initializes the bot into the server
	 * @param c (controller for the bot)
	 * @param rd (data from the ray traces)
	 */
	public Brain(@SuppressWarnings("rawtypes") UT2004BotModuleController c, RayData rd){
		ctrl = c;
		rayData = rd;

		mirrorModule = new MirrorModule(ctrl);
		playerData = new PlayerData(ctrl);
		aim = new AimData(ctrl);
		shooting = new ShootingData(ctrl, playerData.getJudgingData());

		lastNoiseRotation = new Rotation(0.0, 0.0, 0.0);
		noiseExpiry = 0l;

		lastRandomAim = 0l;

		lastHitSimTime = 0l;

		lastArchLocation = null;
		lastSeenArch = 0l;
		archId = null;
		usingLastArchLocationFor = null;

		rayCastingTimeTrigger = 0l;
		rayCastingTimeExpiry = 4213l;

		lastPickedItems = new ArrayList<Item>();

		//waitingForPathComputation = false;
		waitForPathComputationStart = 0l;
		maxWaitForPathComputation = 1024l;

		mirrorDamageMeter = 0;

		lastBehavior = 0;

		STOPPEDcounter = 0;
		maxSTOPPED = 40;
		useSTOPPEDtrigger = false;
	}

	public void deathClean(){
		noiseExpiry = 0l;
		lastRandomAim = 0l;
		lastHitSimTime = 0l;

		lastArchLocation = null;
		lastSeenArch = 0l;
		archId = null;
		usingLastArchLocationFor = null;

		rayCastingTimeTrigger = 0l;

		lastPickedItems.clear();

		waitForPathComputationStart = 0l;

		mirrorDamageMeter = 0;

		lastBehavior = 0;

		STOPPEDcounter = 0;
		useSTOPPEDtrigger = false;
	}

	public void execute(double dt){
		//gather player and map info
		observePlayers();

		if (mirrorModule.isActive()){
			STOPPEDcounter = 0;
			//System.out.println(ctrl.getInfo().getName()+": Mirror behavior");

			if (lastBehavior != 1){
				//clear stuff
				ctrl.getAct().act(new SetCrouch().setCrouch(false));
				ctrl.getShoot().stopShooting();
			}
			lastBehavior = 1;

			mirrorModule.observe();
			mirrorModule.act(dt);
			playerData.clearMirrorCandidates();

			BotDamaged bdsm = ctrl.getSenses().getLastDamage();
			if (bdsm != null){
				if ((bdsm.getDamage()>0) && (!bdsm.isCausedByWorld())){
					if (bdsm.getSimTime() != lastHitSimTime){
						lastHitSimTime = bdsm.getSimTime();
						mirrorDamageMeter++;
					}
				}
			}

			if (mirrorDamageMeter > 7){
				mirrorModule.setActive(false);
				mirrorModule.setTarget(null);
			}
		}
		else{
			if (useSTOPPEDtrigger && (ctrl.getNavigation().getPathExecutor().inState(PathExecutorState.STOPPED))) STOPPEDcounter++;
			//System.out.println(ctrl.getInfo().getName()+": Normal behavior");

			//System.out.println("STOPPEDcounter : "+STOPPEDcounter);

			if (lastBehavior != 2){
				//clear stuff
				ctrl.getAct().act(new SetCrouch().setCrouch(false));
				ctrl.getShoot().stopShooting();
			}
			lastBehavior = 2;

			mirrorDamageMeter = 0;
			doBehavior(dt);

			if (STOPPEDcounter > maxSTOPPED){
				STOPPEDcounter = 0;
				rayCastingTimeTrigger = System.currentTimeMillis()+2*rayCastingTimeExpiry;
				//System.out.println("Running STOPPED procedure with rays");
			}
		}
	}

	private void doBehavior(double dt){
		// --------------------------- NORMAL MOVEMENT + AIM -----//

		Player p = ctrl.getPlayers().getVisiblePlayer(playerData.getTargetData().getTarget());

		if (p != null){
			archId = p.getId();
			lastArchLocation = p.getLocation();
			lastSeenArch = System.currentTimeMillis();

			Location futurePlayerLocation = p.getLocation().add(p.getVelocity().asLocation().scale(dt));
			aim.setFocus(futurePlayerLocation.sub(ctrl.getInfo().getLocation()));
		}
		else{
			List<ILocated> currentPath = ctrl.getNavigation().getCurrentPathCopy();
			int pathIndex = ctrl.getNavigation().getPathExecutor().getPathElementIndex();
			if ((currentPath != null) && (pathIndex >= 0) && (pathIndex < currentPath.size())){
				//System.out.println("setting current target as focus: "+currentPath.get(pathIndex).getLocation());
				if (currentPath.size() > pathIndex+1){
					double distToTarget = currentPath.get(pathIndex).getLocation().sub(ctrl.getInfo().getLocation()).getLength();
					double interpRange = 1000.0;
					if (distToTarget > interpRange) distToTarget = interpRange;
					Location sight = currentPath.get(pathIndex).getLocation().add(currentPath.get(pathIndex+1).getLocation().sub(currentPath.get(pathIndex).getLocation()).scale(1.0-distToTarget/interpRange));
					aim.setFocus(sight.sub(ctrl.getInfo().getLocation()));
				}
				else{
					aim.setFocus(currentPath.get(pathIndex).getLocation().sub(ctrl.getInfo().getLocation()));
				}
			}
			else{
				//Random direction
				Location random = new Location(Math.random()*100.0-50.0, Math.random()*100.0-50.0, Math.random()*20.0-10.0).getNormalized();
				if (System.currentTimeMillis() > lastRandomAim+741){
					double dotRand = random.dot(ctrl.getInfo().getVelocity().asLocation().getNormalized());

					//System.out.println("dotRand: "+dotRand);
					if ((dotRand < 0.5) && (ctrl.getInfo().getVelocity().asLocation().getLength() > 0.0)){
						random = random.add(ctrl.getInfo().getVelocity().asLocation().getNormalized().scale((0.5-dotRand)*2));
					}
					aim.setFocus(random);
					lastRandomAim = System.currentTimeMillis();
				}
			}

			// -- noise detection -- //

			Rotation noise = ctrl.getSenses().getNoiseRotation();

			if (noise != null){
				if (!noise.equals(lastNoiseRotation)){
					lastNoiseRotation = noise;
					noiseExpiry = System.currentTimeMillis()+1525;
				}
				if (System.currentTimeMillis() < noiseExpiry){
					aim.setFocus(noise.toLocation());
					//////ctrl.getMove().stopMovement(); //SUPERBUG.... crashes navigator. leaving it here for later notice... don't ever use this again
				}
			}

			// -- hit detection -- //

			BotDamaged bdsm = ctrl.getSenses().getLastDamage();

			if (bdsm != null){
				if (!bdsm.isCausedByWorld())
				{
					if (bdsm.getSimTime() != lastHitSimTime)
					{
						aim.setFocus(ctrl.getInfo().getRotation().toLocation().scale(-1.0).getNormalized());
						lastHitSimTime = bdsm.getSimTime();
					}
				}
			}
		}

		if (System.currentTimeMillis() > (waitForPathComputationStart+maxWaitForPathComputation)){
			if (!ctrl.getNavigation().isNavigating()){
				usingLastArchLocationFor = null;				
				if (System.currentTimeMillis() < (rayCastingTimeTrigger+rayCastingTimeExpiry)){
					STOPPEDcounter = 0;
					if (p == null){
						Location normalRayDir = rayData.getNormalDirection(dt);
						//normalRayDir.setTo(normalRayDir.getX(), normalRayDir.getY(), 0.0);
						normalRayDir.setX(normalRayDir.getX());
						normalRayDir.setY(normalRayDir.getY());
						normalRayDir.setZ(normalRayDir.getZ());
						aim.setFocus(normalRayDir);
					}

					Location rayLoc = ctrl.getInfo().getLocation().add(rayData.getNormalDirection(dt).scale(ctrl.getInfo().getBaseSpeed()));
					if (rayLoc.getLength() == 0.0){
						if (ctrl.getInfo().getVelocity().asLocation().getLength() > 0.0){
							rayLoc = ctrl.getInfo().getLocation().add(ctrl.getInfo().getVelocity().asLocation());
						}
						else{
							rayLoc = ctrl.getInfo().getLocation().add(new Location(Math.random()*ctrl.getInfo().getBaseSpeed(),Math.random()*ctrl.getInfo().getBaseSpeed(),0.0));
						}
					}

					Move m = new Move();
					m.setFirstLocation(rayLoc);
					m.setFocusLocation(aim.getFocusLocation());
					ctrl.getAct().act(m);

					//System.out.println(ctrl.getInfo().getName()+": using ray navigation");
				}
				else{
					waitForPathComputationStart = System.currentTimeMillis();

					if ((lastArchLocation != null) && (lastArchLocation.sub(ctrl.getInfo().getLocation()).getLength() < 200)){
						archId = null;
						lastArchLocation = null;
					}
					double distanceToArch = 0.0;
					if ((System.currentTimeMillis() < lastSeenArch+20000) && (lastArchLocation != null)){
						distanceToArch = lastArchLocation.sub(ctrl.getInfo().getLocation()).getLength();
					}
					Location targetMoveLocation = null;
					Item item = null;
					if ((item == null) && (distanceToArch < 2000)){
						item = getClosestImportantItemTo(ctrl.getInfo().getLocation(), 1700);
					}
					if ((item == null) && (distanceToArch < 2000)){
						item = getClosestItemTo(ctrl.getInfo().getLocation(), 1000);
					}
					if (item == null){
						if ((System.currentTimeMillis() < lastSeenArch+20000) && (lastArchLocation != null)){
							item = getClosestItemTo(lastArchLocation, 1000);
							if (item != null){
								if (archId != null){
									usingLastArchLocationFor = archId;
								}
							}
						}
					}

					if (item != null){
						addLastPickedItem(item);
						if (item.getNavPoint() != null){
							targetMoveLocation = item.getLocation();
						}
					}

					if (targetMoveLocation == null){
						if ((System.currentTimeMillis() < lastSeenArch+20000) && (lastArchLocation != null)){
							targetMoveLocation = lastArchLocation;
							if (archId != null){
								usingLastArchLocationFor = archId;
							}
						}
					}

					if (targetMoveLocation == null){
						item = getClosestItemTo(ctrl.getInfo().getLocation());
						if (item != null){
							addLastPickedItem(item);
							if (item.getNavPoint() != null){
								targetMoveLocation = item.getLocation();
							}
						}
					}

					if (targetMoveLocation == null){
						rayCastingTimeTrigger = System.currentTimeMillis();
						//System.out.println(ctrl.getInfo().getName()+": navpoint not found");
					}
					else{
						ctrl.getNavigation().navigate(targetMoveLocation);
						ctrl.getNavigation().setFocus(aim.getFocusLocation());
						//System.out.println(ctrl.getInfo().getName()+": initiating path navigation");
					}
				}
			}
			else{
				if ((usingLastArchLocationFor != null) && (archId != null)){
					Player visiblePlayer = ctrl.getPlayers().getVisiblePlayer(usingLastArchLocationFor);
					if (visiblePlayer != null){
						if (visiblePlayer.getLocation().sub(ctrl.getInfo().getLocation()).getLength() < 700){
							ctrl.getNavigation().stopNavigation();
							//System.out.println("Cancelling navigation, due to sight of old arch enemy");
							usingLastArchLocationFor = null;

							Move m = new Move();
							m.setFirstLocation(ctrl.getInfo().getLocation().add(rayData.getNormalDirection(dt).scale(ctrl.getInfo().getBaseSpeed())));
							m.setFocusLocation(aim.getFocusLocation());
							ctrl.getAct().act(m);
						}
					}
				}

				ctrl.getNavigation().setFocus(aim.getFocusLocation());

				//System.out.println(ctrl.getInfo().getName()+": executing path navigation");
			}
		}

		// --------------------------- SHOOTING -----------//

		//aim+shoot
		shooting.step(ctrl.getPlayers().getVisiblePlayer(playerData.getTargetData().getTarget()), dt);

		//change behavior if needed
		UnrealId mirrorTarget = playerData.getGoodMirrorCandidate(mirrorModule.getUsed());
		if (mirrorTarget != null){
			mirrorModule.setTarget(mirrorTarget);
			mirrorModule.setActive(true);
			if (ctrl.getNavigation().isNavigating()){
				ctrl.getNavigation().stopNavigation();
			}
		}
	}

	private void observePlayers(){
		Map<UnrealId, Player> players = ctrl.getPlayers().getVisiblePlayers();

		playerData.getTargetData().clearTargets(); //get a clear image before doing any shooting
		int myTeam = ctrl.getInfo().getTeam(); // Added to allow team play
		for (Entry<UnrealId,Player> entry : players.entrySet()){
			// Wasn't used: schrum: 6/7/18
			//UnrealId key=entry.getKey();
			Player player=entry.getValue();
			// Restriction added to prevent MirrorBot from attacking teammates
			int otherTeam = player.getTeam();
			if(otherTeam != myTeam) // Only add enemies
				playerData.addData(player);
		}
	}

	public void pathExecutorStateChange(PathExecutorState flag){
		useSTOPPEDtrigger = false; //it will stay true if it's really stopped

		if (flag.equals(PathExecutorState.PATH_COMPUTATION_FAILED)){
			rayCastingTimeTrigger = System.currentTimeMillis()+2*rayCastingTimeExpiry;
			//System.out.println("PATH_COMPUTATION_FAILED");
		}
		if (flag.equals(PathExecutorState.TARGET_REACHED)){
			//System.out.println("TARGET_REACHED");
		}
		if (flag.equals(PathExecutorState.STUCK)){
			rayCastingTimeTrigger = System.currentTimeMillis();
			//System.out.println("STUCK");
			ctrl.getNavigation().stopNavigation();
		}
		if (flag.equals(PathExecutorState.STOPPED)){
			//System.out.println("STOPPED");
			ctrl.getNavigation().stopNavigation();
			useSTOPPEDtrigger = true;
		}
		else{
			STOPPEDcounter = 0;
		}

		if (flag.equals(PathExecutorState.PATH_COMPUTED)){
			if (ctrl.getNavigation().getCurrentPathDirect().size() > 2){
				Location loc = ctrl.getNavigation().getCurrentPathDirect().get(0).getLocation();				
				double zdist = loc.getZ()-ctrl.getInfo().getLocation().getZ();
				if (Math.abs(zdist) < 40){
					ctrl.getNavigation().getCurrentPathDirect().remove(0);
				}
			}
			else if (ctrl.getNavigation().getCurrentPathDirect().size() == 0){
				rayCastingTimeTrigger = System.currentTimeMillis();
			}
			//System.out.println("PATH_COMPUTED");
		}
		//waitingForPathComputation = false;
	}

	public Item getClosestItemTo(Location loc){
		return getClosestItemTo(loc, Double.MAX_VALUE);
	}

	public Item getClosestItemTo(Location loc, double radius){
		Item item = null;
		double minDist = -1.0;
		Map<UnrealId, Item> allItems = ctrl.getItems().getAllItems();
		for (Entry<UnrealId,Item> entry : allItems.entrySet()){
			// Unused: schrum: 6/7/18
			//UnrealId key=entry.getKey();
			Item crtIt=entry.getValue();

			if (crtIt == null) continue;
			if (crtIt.getNavPoint() != null){
				if (!crtIt.getNavPoint().isItemSpawned()) continue;
			}
			if (!lastPickedItems.contains(crtIt)){
				double dist = crtIt.getLocation().sub(loc).getLength();
				if (dist > radius) continue;

				if ((crtIt.getType().equals(UT2004ItemType.SUPER_HEALTH_PACK)) ||
						(crtIt.getType().equals(UT2004ItemType.SUPER_SHIELD_PACK))){
					dist = 0.0; //make most desirable !
				}
				if ((item == null) || (dist < minDist)){
					item = crtIt;
					minDist = dist;
				}
			}
		}
		return item;
	}

	public Item getClosestImportantItemTo(Location loc, double radius){
		Item item = null;
		double minDist = -1.0;
		Map<UnrealId, Item> allItems = ctrl.getItems().getAllItems();
		for (Entry<UnrealId,Item> entry : allItems.entrySet()){
			// Unused: schrum: 6/7/18
			//UnrealId key=entry.getKey();
			Item crtIt=entry.getValue();

			if (crtIt == null) continue;
			if (crtIt.getNavPoint() != null){
				if (!crtIt.getNavPoint().isItemSpawned()) continue;
			}

			if (
					(crtIt.getType().equals(UT2004ItemType.SUPER_HEALTH_PACK)) ||
					(crtIt.getType().equals(UT2004ItemType.SUPER_SHIELD_PACK)) ||
					(crtIt.getType().equals(UT2004ItemType.HEALTH_PACK) && (ctrl.getInfo().getHealth() < ctrl.getGame().getMaxHealth())) ||
					(crtIt.getType().equals(UT2004ItemType.SHIELD_PACK)) ||
					(crtIt.getType().equals(UT2004ItemType.U_DAMAGE_PACK)) ||
					(crtIt.getType().equals(UT2004ItemType.MINI_HEALTH_PACK)) ||
					(
							(ctrl.getWeaponry().getLoadedRangedWeapons().size() < 2) &&
							(
									crtIt.getType().equals(UT2004ItemType.ASSAULT_RIFLE) ||
									crtIt.getType().equals(UT2004ItemType.BIO_RIFLE) ||
									crtIt.getType().equals(UT2004ItemType.FLAK_CANNON) ||
									crtIt.getType().equals(UT2004ItemType.LIGHTNING_GUN) ||
									crtIt.getType().equals(UT2004ItemType.MINIGUN) ||
									crtIt.getType().equals(UT2004ItemType.ROCKET_LAUNCHER) ||
									crtIt.getType().equals(UT2004ItemType.SHOCK_RIFLE) ||
									crtIt.getType().equals(UT2004ItemType.SNIPER_RIFLE)
									)
							)
					){
				if (!lastPickedItems.contains(crtIt)){
					double dist = crtIt.getLocation().sub(loc).getLength();
					if (dist > radius) continue;
					if ((crtIt.getType().equals(UT2004ItemType.SUPER_HEALTH_PACK)) ||
							(crtIt.getType().equals(UT2004ItemType.SUPER_SHIELD_PACK))){
						dist = 0.0; //make most desirable !
					}
					if ((item == null) || (dist < minDist)){
						item = crtIt;
						minDist = dist;
					}
				}
			}
		}	
		return item;
	}

	public Item getClosestSuperImportantItemTo(Location loc, double radius){
		Item item = null;
		double minDist = -1.0;
		Map<UnrealId, Item> allItems = ctrl.getItems().getAllItems();
		for (Entry<UnrealId,Item> entry : allItems.entrySet()){
			// Unused: schrum 6/7/18
			//UnrealId key=entry.getKey();
			Item crtIt=entry.getValue();
			if (crtIt == null) continue;
			if (crtIt.getNavPoint() != null){
				if (!crtIt.getNavPoint().isItemSpawned()) continue;
			}

			if ((crtIt.getType().equals(UT2004ItemType.SUPER_HEALTH_PACK)) ||(crtIt.getType().equals(UT2004ItemType.SUPER_SHIELD_PACK))){
				double dist = crtIt.getLocation().sub(loc).getLength();
				if (dist > radius) continue;

				if ((item == null) || (dist < minDist)){
					item = crtIt;
					minDist = dist;
				}
			}
		}

		return item;
	}

	public void addLastPickedItem(Item item){
		lastPickedItems.add(item);

		int maxSize = ctrl.getItems().getAllItems().values().size();
		if (maxSize > 20) maxSize = 20;
		if (lastPickedItems.size() > maxSize){
			lastPickedItems.remove(0);
		}
	}
}
