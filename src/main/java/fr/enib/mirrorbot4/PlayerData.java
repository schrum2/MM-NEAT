package fr.enib.mirrorbot4;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric.Weapon;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.UT2004ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PlayerData{
	@SuppressWarnings("rawtypes")
	private UT2004BotModuleController ctrl;
	private TargetData targetData;
	private JudgingData judgingData;
	private HashMap<UnrealId, Integer> mirrorCandidates;
	
	public PlayerData(@SuppressWarnings("rawtypes") UT2004BotModuleController c){
		ctrl = c;
		targetData = new TargetData(ctrl);
		judgingData = new JudgingData(ctrl);
		mirrorCandidates = new HashMap<UnrealId, Integer>();
	}
	
	public TargetData getTargetData(){
		return targetData;
	}
	
	public JudgingData getJudgingData(){
		return judgingData;
	}
	
	public void addData(Player p){
		if (p == null) return;
		
		targetData.addTarget(p);
		judgingData.addData(p);
		
		Location prot = p.getRotation().toLocation();
		Location ploc = p.getLocation();
		
		Weapon usedWeapon = getWeaponFromString(p.getWeapon());
		boolean dangerousWeapon = true;
		if (usedWeapon != null){
			if (usedWeapon.getType().equals(UT2004ItemType.LINK_GUN)){
				dangerousWeapon = false;
			}
			
			if ((usedWeapon.getType().equals(UT2004ItemType.SHIELD_GUN)) && (ploc.sub(ctrl.getInfo().getLocation()).getLength() > 300)){
				dangerousWeapon = false;
			}
		}
		
		if ((p.getFiring() > 0) && dangerousWeapon){
			//check who is shooting and what
			
			//check myself for fire
			Location me = ctrl.getInfo().getLocation().sub(ploc); //vector from shooter to me
			me = new Location(me.getX(), me.getY(), -me.getZ());
			double dotShot = me.dot(prot) / (me.getLength()*prot.getLength()); //how much focus on me
			//System.out.println("dot: "+dotShot);
			
			if (dotShot > 0.98){ //shooting at me ! omg omg
				//System.out.println("Halp ! player "+p.getId()+" is shooting at me :(");
				targetData.addUrgency(p);
				mirrorCandidates.remove(p.getId());
			}else{
				//phew... let's see who's he shooting at
				
				Map<UnrealId, Player> players = ctrl.getPlayers().getVisiblePlayers();

				for (Map.Entry<UnrealId,Player> entry : players.entrySet()){
					// Unused: schrum: 6/7/18
					//UnrealId key=entry.getKey();
					Player player=entry.getValue();

					Location target = player.getLocation().sub(p.getLocation());
					target = new Location(target.getX(), target.getY(), -target.getZ());

					//System.out.println("Dot: "+target.dot(prot.getNormalized())+" Len:"+target.getLength());

					if (target.dot(prot.getNormalized()) > 0.98*target.getLength()){ //99% towards target
						//System.out.println("Player "+p.getId()+" is shooting to the same semispace where "+player.getId()+" is located.");
						targetData.addTarget(player); //add player (again), so it will be more likely to be picked == KILL THE WEAK !
					}else{
						//System.out.println("Player "+p.getId()+" is not shooting at "+player.getId()+".");
					}
				}
			}
		}else{
			//not shooting or nondangerous weapon
			
			Location me = ctrl.getInfo().getLocation().sub(ploc); //vector from shooter to me
			me = new Location(me.getX(), me.getY(), -me.getZ());
			double dotShot = me.dot(prot) / (me.getLength()*prot.getLength()); //how much focus on me
			
			if (dotShot > 0.9 && (ploc.sub(ctrl.getInfo().getLocation()).getLength() < 2000)){ //looking at me
				if (mirrorCandidates.containsKey(p.getId())){
					mirrorCandidates.put(p.getId(), (mirrorCandidates.get(p.getId())+1));
				}else{
					mirrorCandidates.put(p.getId(), 1);
				}
			}
		}
	}
	
	private Weapon getWeaponFromString(String wString){
		Map<ItemType, Weapon> weapons = ctrl.getWeaponry().getLoadedWeapons();
		for (Map.Entry<ItemType,Weapon> entry : weapons.entrySet()){
			// Unused: schrum: 6/7/18
			//ItemType key=entry.getKey();
			Weapon weapon=entry.getValue();			
			if (weapon.toString().contains(wString)){
				return weapon;
			}
		}
		return null;
	}
	
	public UnrealId getGoodMirrorCandidate(ArrayList<UnrealId> forbiddenList){
		UnrealId candidate = null;
		int cvotes = 0;		
		for (UnrealId key : mirrorCandidates.keySet()){
			Player p = ctrl.getPlayers().getVisiblePlayer(key);
			if (p == null) continue;			
			if (forbiddenList.contains(p.getId())) continue;
			int tmpvotes = mirrorCandidates.get(key);
			if (tmpvotes >= 5){
				if ((candidate == null) || (tmpvotes > cvotes)){
					candidate = key;
					cvotes = tmpvotes;
				}
			}
		}
		return candidate;
	}
	
	public void clearMirrorCandidates(){
		mirrorCandidates.clear();
	}
}
