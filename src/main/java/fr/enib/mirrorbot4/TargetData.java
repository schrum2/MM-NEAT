package fr.enib.mirrorbot4;

import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import java.util.HashMap;

public class TargetData{
	UT2004BotModuleController ctrl;
	
	private HashMap<UnrealId, Integer> blackList;
	private HashMap<UnrealId, Integer> oldList;
	private UnrealId archEnemy;
	private long archTime;
	private long clearTime;
	private long archThresh;
	private long clearThresh;
	
	public TargetData(UT2004BotModuleController c){
		ctrl = c;
		
		blackList = new HashMap<UnrealId, Integer>();
		oldList = new HashMap<UnrealId, Integer>();
		archEnemy = null;
		archTime = 0;
		clearTime = 0;
		archThresh = 8211;
		clearThresh = 2142;
	}
	
	public boolean amTargeted(){
		if ((archEnemy != null) && (archTime+archThresh > System.currentTimeMillis())){
			return true;
		}
		return false;
	}
	
	public void addUrgency(Player p){
		if ((archEnemy == null) || (archTime + archThresh < System.currentTimeMillis())) { //if no enemy or 4.211 seconds passed
			archEnemy = p.getId();
			archTime = System.currentTimeMillis();
		}else{
			if (p.getId().equals(archEnemy)){
				archTime = System.currentTimeMillis(); //refresh time
			}else if (ctrl.getPlayers().getVisiblePlayer(archEnemy) == null){
				archEnemy = null;
				addUrgency(p);
			}
		}
	}
	
	public void clearTargets(){
		if (clearTime + clearThresh < System.currentTimeMillis()){
			oldList.clear();
			clearTime = System.currentTimeMillis();
		}		
		blackList.clear();
	}
	
	public void addTarget(Player p)	{
		if (archEnemy != null)		{
			if (p.getId().equals(archEnemy)) addUrgency(p);
		}
		if (blackList.containsKey(p.getId())){
			blackList.put(p.getId(), (blackList.get(p.getId())+1));
		}else{
			blackList.put(p.getId(), 1);
		}
		
		//add to oldlist too
		if (oldList.containsKey(p.getId())){
			oldList.put(p.getId(), (oldList.get(p.getId())+1));
		}else{
			oldList.put(p.getId(), 1);
		}
	}
	
	public UnrealId getTarget(){
		if ((archEnemy != null) && (archTime+archThresh > System.currentTimeMillis())){
			return archEnemy;
		}
		
		UnrealId bestId = null;
		int bestTarget = 0;
		int tmpTarget;
		
		for (UnrealId key : blackList.keySet()){
			tmpTarget = blackList.get(key);
			if (oldList.containsKey(key)) tmpTarget += oldList.get(key);
			if (tmpTarget > bestTarget){
				bestId = key;
				bestTarget = tmpTarget;
			}
		}		
		return bestId;
	}
}
