package fr.enib.mirrorbot4;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Move;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.SetCrouch;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import java.util.ArrayList;

public class MirrorModule
{
	private UT2004BotModuleController ctrl;
	private boolean active;
	
	private UnrealId mirrorSubject;
	
	private ArrayList<MirrorFrame> mirrorFrames;
	
	private boolean needCancelation;
	
	private int violenceMeter;
	
	private long mirroringTime;
	private long mirroringStartTime;
	private long noSeeTime;
	private long lastSaw;
	
	private ArrayList<UnrealId> usedList;
	
	private long lastMovement;
	
	public MirrorModule(UT2004BotModuleController c)
	{
		ctrl = c;
		active = false;
		mirrorSubject = null;
		
		mirrorFrames = new ArrayList<MirrorFrame>();
		
		needCancelation = false;
		
		violenceMeter = 0;
		
		mirroringStartTime = 0l;
		mirroringTime = 8136l; //mirror for 10 seconds
		lastSaw = 0l;
		noSeeTime = 2982l; // cancel if not seen target for ? seconds
		
		usedList = new ArrayList<UnrealId>();
		
		lastMovement = System.currentTimeMillis();
	}
	
	public boolean hasTarget()
	{
		return (mirrorSubject != null);
	}
	
	public void setTarget(UnrealId id)
	{
		mirrorSubject = id;
		violenceMeter = 0;
		mirrorFrames.clear();
		lastMovement = System.currentTimeMillis();
		
		if (mirrorSubject != null)
		{
			usedList.add(mirrorSubject);
			if (usedList.size() > 3)
			{
				usedList.remove(0);
			}
		}
	}
	
	public void setActive(boolean a)
	{
		active = a;
		if (active)
		{
			mirroringStartTime = System.currentTimeMillis();
		}
	}
	
	public boolean isActive()
	{
		return active;
	}
	
	public void observe()
	{
		if (!active) return;
		
		if (mirrorSubject == null) return;
		
		Player subject = ctrl.getPlayers().getVisiblePlayer(mirrorSubject);
		
		if (subject != null)
		{
			lastSaw = System.currentTimeMillis();
		}
		
		//create a motion frame
		if (subject != null)
		{
			MirrorFrame mf = new MirrorFrame(ctrl, subject, System.currentTimeMillis());
			mirrorFrames.add(mf);
			if (mf.getViolence())
			{
				violenceMeter++;
			}
		}
		
		if ((System.currentTimeMillis() > mirroringStartTime+mirroringTime) || (violenceMeter > 7) || (System.currentTimeMillis() > lastSaw+noSeeTime))
		{
			setActive(false);
			setTarget(null);
		}
	}
	
	public ArrayList<UnrealId> getUsed()
	{
		return usedList;
	}
	
	public void act(double dt)
	{
		if (!active) return;
		
		if (mirrorSubject == null) return;
		
		Player subject = ctrl.getPlayers().getVisiblePlayer(mirrorSubject);
		
		if (mirrorFrames.size() > 0)
		{
			Location currentFrameLocation = mirrorFrames.get(0).getLocation();
			
			if (mirrorFrames.get(0).getTimeStamp()+115 < System.currentTimeMillis())
			{
				mirrorFrames.get(0).execute(dt);
				if (mirrorFrames.get(0).getMovement()) lastMovement = System.currentTimeMillis();
				mirrorFrames.remove(0);
				needCancelation = true;
			}
			
			if (System.currentTimeMillis() > lastMovement+842+((long)(Math.random()*1000)))
			{
				Player p = ctrl.getPlayers().getVisiblePlayer(mirrorSubject);
				//move a bit, if staying still...
				Move m = new Move();
				m.setFirstLocation(currentFrameLocation.sub(ctrl.getInfo().getLocation()));
				if (p != null)
				{
					m.setFocusLocation(p.getLocation());
				}
				else
				{
					m.setFocusLocation(currentFrameLocation);
				}
				ctrl.getAct().act(m);
				lastMovement = System.currentTimeMillis();
			}
		}
		else
		{
			if (needCancelation)
			{
				ctrl.getAct().act(new SetCrouch().setCrouch(false));
				ctrl.getShoot().stopShooting();
				needCancelation = false;
			}
		}
	}
}
