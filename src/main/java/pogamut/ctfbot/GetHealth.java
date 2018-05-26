package pogamut.ctfbot;

import java.util.Set;

import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType.Category;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;

public class GetHealth extends Goal {

	protected Item health = null;

	public GetHealth(CTFBot bot) {
		super(bot);
	}

	@Override
	public void perform() {

		bot.updateFight();

		if (health == null) {
			Set<Item> healths = bot.getTaboo().filter(
					bot.getItems().getSpawnedItems(
							Category.HEALTH).values());

			double min_distance = Double.MAX_VALUE;
			Item winner = null;

			for (Item item : healths) {
				double dist = item.getLocation().getDistance(
						bot.getInfo().getLocation());
				if (dist < min_distance) {
					min_distance = dist;
					winner = item;
				}
			}
			this.health = winner;
		}
		if (health == null)
			return;

		bot.getLog().info(String.format("Found health: %s", health.toString()));

		if (bot.getEnemyFlag() == null
				|| !bot.getInfo().getId()
						.equals(bot.getEnemyFlag().getHolder())) {
			bot.goTo(health);
		}

	}

	@Override
	public double getPriority() {

		if (bot.getItems().getAllItems(Category.HEALTH).size() > 0 &&
				bot.getInfo().getHealth() < 20 && !(
				bot.getEnemyFlag() != null
						&& bot.getInfo().getId()
								.equals(bot.getEnemyFlag().getHolder())
					&& bot.getInfo().atLocation(bot.getOurFlagBase(), 5d)))
			return 100d;

		return 0d;
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
