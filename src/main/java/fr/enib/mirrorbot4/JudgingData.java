package fr.enib.mirrorbot4;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric.Weapon;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.UT2004ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PlayerDamaged;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class JudgingData
{
	private UT2004BotModuleController ctrl;
	
	private HashMap<UnrealId, ArrayList<UnrealId> > robotVoteChart;
	private HashMap<UnrealId, ArrayList<UnrealId> > humanVoteChart;
	
	private HashMap<UnrealId, Boolean> wasVotedByMe;
	
	private long lastSimTime;
	
	public JudgingData(UT2004BotModuleController c)
	{
		ctrl = c;
		
		robotVoteChart = new HashMap<UnrealId, ArrayList<UnrealId> >();
		humanVoteChart = new HashMap<UnrealId, ArrayList<UnrealId> >();
		
		wasVotedByMe = new HashMap<UnrealId, Boolean>();
		
		lastSimTime = 0l;
	}
	
	public void addData(Player p)
	{
		Location prot = p.getRotation().toLocation();
		Location ploc = p.getLocation();
		
		Weapon usedWeapon = getWeaponFromString(p.getWeapon());
		boolean votingWeapon = false;
		if (usedWeapon != null)
		{
			if (usedWeapon.getType().equals(UT2004ItemType.LINK_GUN))
			{
				votingWeapon = true;
			}
		}
		
		if ((p.getFiring() > 0) && votingWeapon)
		{
			//check who is shooting and what
			
			//check myself for fire
			Location me = ctrl.getInfo().getLocation().sub(ploc); //vector from shooter to me
			me = new Location(me.getX(), me.getY(), -me.getZ());
			double dotShot = me.dot(prot) / (me.getLength()*prot.getLength()); //how much focus on me
			//System.out.println("dot: "+dotShot);
			
			if (dotShot > 0.98) //shooting at me ! omg omg
			{
				addVote(p.getId(), ctrl.getInfo().getId(), p.getFiring());
			}
			else
			{
				//phew... let's see who's he shooting at
				
				Map<UnrealId, Player> players = ctrl.getPlayers().getVisiblePlayers();

				for (Map.Entry<UnrealId,Player> entry : players.entrySet())
				{
					UnrealId key=entry.getKey();
					Player player=entry.getValue();
					
					if (player == null) continue;

					Location target = player.getLocation().sub(p.getLocation());
					target = new Location(target.getX(), target.getY(), -target.getZ());

					if (target.dot(prot.getNormalized()) > 0.98*target.getLength()) //98% towards target
					{
						addVote(p.getId(), player.getId(), p.getFiring());
					}
				}
			}
		}
	}
	
	public void setVotedByMe(UnrealId player, PlayerDamaged pd)
	{
		if (pd == null) return;
		if (pd.getSimTime() == lastSimTime) return;
		
		if ((pd.getDamage() == 0) && (pd.getId().equals(player)))
		{
			wasVotedByMe.put(player, true);
		}
		
		lastSimTime = pd.getSimTime();
	}
	
	public boolean needsToBeVotedByMe(UnrealId player)
	{
		if (wasVotedByMe.containsKey(player))
		{
			if (!wasVotedByMe.get(player)) return true;
		}
		
		return false;
	}
	
	public int getRatingFor(UnrealId player) //0=tie , 1=robot , 2=human
	{
		int robotScore = 0;
		int humanScore = 0;
		
		if (robotVoteChart.containsKey(player))
		{
			robotScore = robotVoteChart.get(player).size();
		}
		
		if (humanVoteChart.containsKey(player))
		{
			humanScore = humanVoteChart.get(player).size();
		}
		
		if (robotScore == humanScore) return 0; //tie
		
		if (robotScore > humanScore) return 1; //robot
		
		return 2; //human
	}
	
	private void addVote(UnrealId judge, UnrealId victim, int vote)
	{
		//default vote for robot
		HashMap<UnrealId, ArrayList<UnrealId> > yesChart = robotVoteChart;
		HashMap<UnrealId, ArrayList<UnrealId> > nooChart = humanVoteChart;
		
		if (vote > 1) //vote for human
		{
			yesChart = humanVoteChart;
			nooChart = robotVoteChart;
		}
		
		if (yesChart.containsKey(victim))
		{
			if (!yesChart.get(victim).contains(judge))
			{
				yesChart.get(victim).add(judge);
				
				//set voted by me False
				wasVotedByMe.put(victim, false);
			}
		}
		else
		{
			ArrayList<UnrealId> newList = new ArrayList<UnrealId>();
			newList.add(judge);
			yesChart.put(victim, newList);
			
			//set voted by me False
			wasVotedByMe.put(victim, false);
		}
		
		if (nooChart.containsKey(victim))
		{
			if (nooChart.get(victim).contains(judge))
			{
				nooChart.get(victim).remove(judge);
			}
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
