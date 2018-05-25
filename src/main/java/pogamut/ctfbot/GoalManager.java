package pogamut.ctfbot;

import java.util.Collections;
import java.util.LinkedList;

import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;

public class GoalManager {
	protected final LinkedList<IGoal> goals = new LinkedList<IGoal>();
	protected IGoal currentGoal = null;
	protected UT2004Bot bot;

	public GoalManager(UT2004Bot bot) {
		this.bot = bot;
	}

	public boolean addGoal(IGoal goal) {
		if (!goals.contains(goal)) {
			goals.add(goal);
			return true;
		} else {
			return false;
		}
	}

	public IGoal executeBestGoal() {

		Collections.sort(goals);

		IGoal next_goal = goals.peekFirst();
		if (next_goal != currentGoal && currentGoal != null) {

			currentGoal.abandon();
		}
		
		currentGoal = next_goal;


		bot.getLog().severe(
				String.format(
						"Chosen goal pri %.2f: %s",
						currentGoal.getPriority(),
						currentGoal.toString()));

		currentGoal.perform();

		return currentGoal;
	}

	public IGoal getCurrentGoal() {
		return currentGoal;
	}

	public void abandonAllGoals() {
		for (IGoal goal : goals) {
			goal.abandon();
		}
	}
}
