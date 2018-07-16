package pogamut.ctfbot;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;

public class GetOurFlag extends Goal {

	private static final double CAPTURE_SUPPORT_DISTANCE = 200;
	protected Player enemy = null;
	Location flagLocation;

	public GetOurFlag(CTFBot bot) {
		super(bot);
	}

	@Override
	public void perform() {

		UnrealId holderId = null;
		
		if (bot.getOurFlag() != null) {

			if (bot.getOurFlag().getLocation() != null) {
				flagLocation = bot.getOurFlag().getLocation();
			}

			if (flagLocation != null) {

				enemy = bot.getPlayers().getPlayer(holderId);

				bot.getLog().info(
						String.format("FlagLocation: %s %s %.2f", flagLocation,
								bot.getInfo().getLocation(),
								bot.getInfo().getDistance(flagLocation)));
				if (enemy != null) {
					bot.goTo(enemy);

					if (enemy.isVisible()) {
						bot.updateFight(enemy);
						return;
					}
				} else {
					bot.goTo(flagLocation);
				}
			} else {
				bot.goTo(bot.getEnemyFlagBase());
			}
		}

		bot.updateFight();
	}

	@Override
	public double getPriority() {
		if (bot.getOurFlag() == null
				|| bot.getOurFlag().getState().equalsIgnoreCase("home"))
			return 0d;

		if (bot.getEnemyFlag() != null) {
			UnrealId holderId = bot.getEnemyFlag().getHolder();

			if (holderId != null) {
				Player holder = bot.getPlayers().getFriends().get(holderId);

				if (bot.getPlayers().getFriends().size() > 1) {
					if (holderId.equals(bot.getInfo().getId())
							||
							holder.getLocation().getDistance(
									bot.getInfo().getLocation())
							< CAPTURE_SUPPORT_DISTANCE) {
						return 0d;
					}
				} else {
					return 55d;
				}
			} else {
				if (bot.getPlayers().getFriends().size() > 1)
					return 0d;
				else {
					return 55d;
				}
			}
		}
		return 20d;
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
		return;
	}
}
