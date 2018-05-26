package fr.enib.mirrorbot4;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric.Weapon;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.UT2004ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import java.util.Map;

public class ShootingData
{
	private UT2004BotModuleController ctrl;
	
	private JudgingData judgingData;
	
	private double timePassed;
	
	public ShootingData(UT2004BotModuleController c, JudgingData jd)
	{
		ctrl = c;
		
		judgingData = jd;
		
		timePassed = 0.0;
	}
	
	public void step(Player p, double dt)
	{
		timePassed += dt;
		
		Location myRot = ctrl.getInfo().getRotation().toLocation();
		Location myLoc = ctrl.getInfo().getLocation();
		boolean isMeelee = ctrl.getWeaponry().getCurrentWeapon().getDescriptor().isMelee();
		
		if (p != null)
		{
			//ctrl.getMove().turnTo(p); //accurate turn to target
			
			Location target = p.getLocation().sub(myLoc.getLocation()); //vector from shooter to me
			target = new Location(target.getX(), target.getY(), -target.getZ());
			double dotShot = target.dot(myRot.getNormalized()) / target.getLength(); //how much focus on me
			
			double shootingThresh = 0.97;
			
			if (weaponHasSplash(ctrl.getWeaponry().getCurrentWeapon())) shootingThresh = 0.95;
			
			if (target.getLength() < 300) shootingThresh = 0.90;
			
			if (isMeelee) shootingThresh = 0.50;
			
			double distanceToTarget = p.getLocation().sub(myLoc.getLocation()).getLength();
			
			if ((dotShot > shootingThresh) && (distanceToTarget < 3500)) //facing the target, shoot !!
			{
				if ((Math.cos(timePassed*Math.PI/30) > 0.0) && (distanceToTarget < 600) && (judgingData.needsToBeVotedByMe(p.getId())) && (judgingData.getRatingFor(p.getId()) > 0))
				{
					Weapon linkGun = ctrl.getWeaponry().getWeapon(UT2004ItemType.LINK_GUN);
					ctrl.getShoot().shoot(linkGun, ((judgingData.getRatingFor(p.getId()) == 1)?true:false), p.getId());
					judgingData.setVotedByMe(p.getId(), ctrl.getSenses().getLastCausedDamage());
					//System.out.println(ctrl.getInfo().getName()+": Voting that "+p.getId()+" is a "+((judgingData.getRatingFor(p.getId()) == 1)?"bot":"human"));
				}
				else
				{
					ctrl.getShoot().shoot(ctrl.getWeaponPrefs().getWeaponPreference(p.getLocation()), p.getId());
				}
			}
			else
			{
				ctrl.getShoot().stopShooting();
			}
		}
		else
		{
			boolean foundColateralTarget = false;
			Player colateralPlayer = null;
			
			Map<UnrealId, Player> players = ctrl.getPlayers().getVisiblePlayers();
			for (Map.Entry<UnrealId,Player> entry : players.entrySet())
			{
				UnrealId key=entry.getKey();
				Player player=entry.getValue();
				
				Location target = player.getLocation().sub(myLoc.getLocation()); //vector from shooter to me
				target = new Location(target.getX(), target.getY(), -target.getZ());
				double dotShot = target.dot(myRot.getNormalized()) / target.getLength(); //how much focus on me
				
				double shootingThresh = 0.97;

				if (weaponHasSplash(ctrl.getWeaponry().getCurrentWeapon())) shootingThresh = 0.95;

				if (target.getLength() < 300) shootingThresh = 0.90;

				if (isMeelee) shootingThresh = 0.50;
				
				if (dotShot > shootingThresh)
				{
					foundColateralTarget = true;
					colateralPlayer = player;
				}
			}
			
			if (foundColateralTarget)
			{
				//ctrl.getShoot().shootWithMode(!primaryShootingMode);
				ctrl.getShoot().shoot(ctrl.getWeaponPrefs().getWeaponPreference(colateralPlayer.getLocation()), colateralPlayer.getId());
			}
			else
			{
				ctrl.getShoot().stopShooting();
			}
		}
	}
	
	private boolean weaponHasSplash(Weapon w)
	{
		if (w.getType().equals(UT2004ItemType.BIO_RIFLE)) return true;
		if (w.getType().equals(UT2004ItemType.FLAK_CANNON)) return true;
		if (w.getType().equals(UT2004ItemType.ROCKET_LAUNCHER)) return true;
		if (w.getType().equals(UT2004ItemType.ONS_AVRIL)) return true;
		
		return false;
	}
}
