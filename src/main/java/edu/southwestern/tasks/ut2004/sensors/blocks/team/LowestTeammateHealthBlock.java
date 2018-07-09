package edu.southwestern.tasks.ut2004.sensors.blocks.team;

import java.util.HashMap;

import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import edu.southwestern.tasks.ut2004.sensors.AcceptsTeamHealthLevels;
import edu.southwestern.tasks.ut2004.sensors.blocks.UT2004SensorBlock;

public class LowestTeammateHealthBlock implements UT2004SensorBlock, AcceptsTeamHealthLevels {
	public static final double MAX_POSSIBLE_HEALTH = 199; //if player runs over helath pickups, their maximum overheal is 199, I have no idea why it isn't 200
	public static final double SPAWN_HEALTH = 100;
	HashMap<String,Double> friendsHealth;

	public LowestTeammateHealthBlock() {
	}
	
	@Override
	public void prepareBlock(UT2004BotModuleController bot) {
		// TODO Auto-generated method stub
	}
	
	/**
	 * looks through the health of all the bot's friends, and returns the lowest value
	 * @param bot (bot whose friends to look at)
	 * @return returns the minimum health of all teammates
	 */
	public double getLowestHealth(UT2004BotModuleController bot) {
		double minHealth = MAX_POSSIBLE_HEALTH;
		String botName = bot.getBot().getName();
		for(String s: friendsHealth.keySet()) {
			double friendHealth = friendsHealth.get(s);
			if(s.equals(botName)){
				continue;
			}
			minHealth = (Math.min(friendHealth, minHealth));
		}
		double minHealthToReturn = minHealth/MAX_POSSIBLE_HEALTH; //divide by MAX_POSSIBLE_HEALTH to scale because all the sensor values have to be in -1 to 1
		return minHealthToReturn;
	}

	@Override
	public int incorporateSensors(UT2004BotModuleController bot, int in, double[] inputs) {
		// TODO Auto-generated method stub
		//for()
		friendsHealth.put(bot.getBot().getName(), (double) bot.getInfo().getSelf().getHealth());
		inputs[in++] = getLowestHealth(bot);
		return in;
	}

	@Override
	public int incorporateLabels(int in, String[] labels) {
		// TODO Auto-generated method stub
		labels[in++] = "Lowest teammate health";
		return in;
	}

	@Override
	public int numberOfSensors() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public void giveTeamHealthLevels(HashMap<String, Double> healths) {
		this.friendsHealth = healths;		
	}

}
