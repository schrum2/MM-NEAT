package pogamut.ctfbot;

import java.util.LinkedList;
import java.util.Set;

import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;

public class GetItems extends Goal {

	protected Item item;
	protected LinkedList<Item> itemsToRunAround;

	public GetItems(CTFBot bot) {
		super(bot);
		item = null;
	}

	@Override
	public void perform() {

		boolean atLocation = false;

		if (item != null) {
			atLocation = bot.getInfo().atLocation(item);
			if (atLocation) {
				bot.getLog().info("Abandoning item.");
				bot.getTaboo().add(item);
				item = null;
			}
		}

		if (item == null
				|| atLocation
				|| !bot.getItems().getVisibleItems()
						.containsKey(item.getId())) {

			Set<Item> items = bot.getTaboo().filter(itemsToRunAround);
			
			if (items.size() != 0) {
				item = items.iterator().next();
				bot.getLog().severe(String.format("Chosen item: %s", item));
				if (!bot.goTo(item)) {
					bot.getLog().info("Failed goTo");
					bot.getTaboo().add(item);
				}
			} else {
				bot.getLog().severe("Taboo clear");
				bot.getTaboo().clear();
			}
		} else {
			bot.getLog().severe("Item is ok");
			bot.goTo(item);
		}

		bot.updateFight();
	}

	@Override
	public double getPriority() {
		itemsToRunAround = new LinkedList<Item>(bot.getItems()
				.getVisibleItems()
				.values());

		return 8 + bot.getItems().getVisibleItems().size();
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
		bot.getLog().info("abandoning GetItems");
		item = null;
		return;
	}

	public Item getItem() {
		return item;
	}
}
