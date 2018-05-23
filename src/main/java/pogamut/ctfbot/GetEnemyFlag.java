package pogamut.ctfbot;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;

public class GetEnemyFlag extends Goal {


	public GetEnemyFlag(CTFBot bot) {
		super(bot);
	}

	@Override
	public void perform() {

		if (bot.getEnemyFlag() != null) {
			UnrealId holderId = bot.getEnemyFlag().getHolder();

			if (bot.getInfo().getId().equals(holderId)) {
				bot.goTo(bot.getOurFlagBase());
				bot.getLog().info("goTo ourFlagBase");
			} else {
				if (bot.getCTF().isEnemyFlagHome()) {
					bot.getLog().info("goTo enemyFlagBase, flag is at enemy base");
					bot.goTo(bot.getEnemyFlagBase());
				} else {
					Location target = bot.getEnemyFlag().getLocation();
					if (target == null) {
						target = bot.getEnemyFlagBase().getLocation();
						bot.getLog().info("goTo enemyFlagBase");
					} else {
						bot.getLog().info("goTo enemyEnemyFlag");
					}
	
					bot.goTo(target);
				}
			}
		} else {
			bot.getLog().info("goTo enemyFlagBase null");
			bot.goTo(bot.getEnemyFlagBase());
		}

		bot.updateFight();
	}

	@Override
	public double getPriority() {

		if (bot.getEnemyFlag() != null &&
				bot.getInfo().getId().equals(bot.getEnemyFlag().getHolder())) {
			return 50d;
		} else {
			return 10d;
		}
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
	}
}
