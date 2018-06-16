package pogamut.ctfbot;

public abstract class Goal implements IGoal {

	protected CTFBot bot;

	public Goal(CTFBot bot) {
		this.bot = bot;
	}

	/**
	 * Reverse ordering, greater numbers first, lesser later
	 */
	@Override
	public int compareTo(IGoal arg0) {
		if (getPriority() == ((IGoal) arg0).getPriority())
			return 0;
		else if ((getPriority()) > ((IGoal) arg0).getPriority())
			return -1;
		else
			return 1;
	}
}
