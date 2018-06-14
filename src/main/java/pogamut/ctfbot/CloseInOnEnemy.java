package pogamut.ctfbot;

import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;

public class CloseInOnEnemy extends Goal {

	protected boolean runningToPlayer = false;

	public CloseInOnEnemy(CTFBot bot) {
		super(bot);
	}

	@Override
	public void perform() {

		bot.updateFight();

		Player enemy = bot.getEnemy();
		int decentDistance = Math.round(bot.getRandom().nextFloat() * 250) + 200;
		if (enemy != null && bot.getInfo().getDistance(enemy) > decentDistance
				&& !runningToPlayer) {

			bot.goTo(enemy);
			runningToPlayer = true;
		}

	}

	@Override
	public double getPriority() {
		Player player = bot.getPlayers().getNearestVisibleEnemy();

		if (player == null)
			return 0;

		double distance = bot.getInfo().getDistance(player) / 50d;
		return 10d - distance;
	}

	@Override
	public boolean hasFailed() {
		return false;
	}

	@Override
	public boolean hasFinished() {
		return false;
	}

	@Override
	public void abandon() {
		bot.getNavigation().stopNavigation();
		runningToPlayer = false;
		return;
	}

}
