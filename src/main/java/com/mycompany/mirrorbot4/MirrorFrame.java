package com.mycompany.mirrorbot4;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.base3d.worldview.object.Rotation;
import cz.cuni.amis.pogamut.base3d.worldview.object.Velocity;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric.Weapon;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.UT2004ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Move;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.SetCrouch;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import java.util.Map;

public class MirrorFrame
{
	private UT2004BotModuleController ctrl;
	
	private Location subjectLocation;
	private Velocity subjectVelocity;
	private Rotation subjectRotation;
	private boolean subjectJumping;
	private boolean subjectCrouching;
	private int subjectShooting;
	private String subjectWeapon;
	
	private long timeStamp;
	
	private boolean subjectViolent;
	
	
	
	public MirrorFrame(UT2004BotModuleController c, Player p, long time)
	{
		ctrl = c;
		
		subjectLocation = p.getLocation();
		subjectVelocity = p.getVelocity();
		subjectRotation = p.getRotation();
		subjectJumping = (p.getVelocity().asLocation().getZ() > ctrl.getInfo().getJumpZBoost()/2.0);
		subjectCrouching = p.isCrouched();
		subjectShooting = p.getFiring();
		subjectWeapon = p.getWeapon();
		
		timeStamp = time;
		
		subjectViolent = false;
		
		//violence test
		Weapon usedWeapon = getWeaponFromString(subjectWeapon);
		boolean dangerousWeapon = true;
		if (usedWeapon != null)
		{
			if (usedWeapon.getType().equals(UT2004ItemType.LINK_GUN))
			{
				dangerousWeapon = false;
			}
			
			if ((usedWeapon.getType().equals(UT2004ItemType.SHIELD_GUN)) && (subjectLocation.sub(ctrl.getInfo().getLocation()).getLength() > 300))
			{
				dangerousWeapon = false;
			}
		}
		if ((subjectShooting > 0) && (dangerousWeapon))
		{
			Location locDiff = ctrl.getInfo().getLocation().sub(subjectLocation).getNormalized();
			Location sRot = subjectRotation.toLocation().getNormalized();
			//sRot.setTo(sRot.getX(), sRot.getY(), -sRot.getZ());
			sRot.setX(sRot.getX());
			sRot.setY(sRot.getY());
			sRot.setZ(-sRot.getZ());
			double dotprod = sRot.dot(locDiff);
			if (dotprod > 0.95)
			{
				subjectViolent = true;
			}
		}
	}
	
	public Location getLocation()
	{
		return subjectLocation;
	}
	
	public boolean getMovement()
	{
		return (subjectVelocity.asLocation().getLength() > 0.0);
	}
	
	public boolean getViolence()
	{
		return subjectViolent;
	}
	
	public long getTimeStamp()
	{
		return timeStamp;
	}
	
	public void execute(double dt)
	{
		//calc rotation
		Location locVector = subjectLocation.sub(ctrl.getInfo().getLocation()).getNormalized();
		Location sRot = subjectRotation.toLocation().getNormalized();
		//sRot.setTo(sRot.getX(), sRot.getY(), -sRot.getZ());
		sRot.setX(sRot.getX());
		sRot.setY(sRot.getY());
		sRot.setZ(sRot.getZ());
		double dotprod = sRot.dot(locVector); //rotation vector projected on diff vector
		Location turnTo = ctrl.getInfo().getLocation().add(sRot.add(locVector.scale(-2.0*dotprod)).scale(ctrl.getInfo().getBaseSpeed()));
		
		//move
		if (subjectVelocity.asLocation().getLength() > 0.0)
		{
			Move m = new Move();
			m.setFirstLocation(ctrl.getInfo().getLocation().add(subjectVelocity.asLocation().scale(dt)));
			m.setFocusLocation(turnTo);
			ctrl.getAct().act(m);
		}
		else
		{
			ctrl.getMove().stopMovement();
			ctrl.getMove().turnTo(turnTo);
		}
		
		if (subjectJumping)
		{
			ctrl.getMove().generalJump(false, 0.5, ctrl.getInfo().getJumpZBoost());
		}
		
		if (subjectCrouching)
		{
			ctrl.getAct().act(new SetCrouch().setCrouch(true));
		}
		else
		{
			ctrl.getAct().act(new SetCrouch().setCrouch(false));
		}
		
		Weapon weapon = getWeaponFromString(subjectWeapon);
		
		if (subjectShooting > 0)
		{
			if (weapon != null)
			{
				ctrl.getShoot().shoot(weapon, ((subjectShooting == 1)?true:false), turnTo);
			}
			else
			{
				ctrl.getShoot().shootWithMode((subjectShooting == 1)?false:true);
			}
		}
		else
		{
			ctrl.getShoot().stopShooting();
		}
	}
	
	private Weapon getWeaponFromString(String wString)
	{
		Map<ItemType, Weapon> weapons = ctrl.getWeaponry().getLoadedWeapons();
		for (Map.Entry<ItemType,Weapon> entry : weapons.entrySet())
		{
			ItemType key=entry.getKey();
			Weapon weapon=entry.getValue();
			
			if (weapon.toString().contains(wString))
			{
				return weapon;
			}
		}
		
		return null;
	}
}
