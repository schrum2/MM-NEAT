		}
		return nearestFriend;
	}
	
	
	/**
	 * @param teamLocations (hashmap containing the locations of teammates)
	 */
	public NavigateToNearestTeammate(UT2004BotModuleController bot, HashMap<String,Location> teamLocations) {
		super(locationOfNearestTeammate(bot));
	}
	
	@Override
	/**
	 * tells bot to follow command
	 * @return returns a string identifying which teammate the bot is following
	 */
	public String execute(@SuppressWarnings("rawtypes") UT2004BotModuleController bot) {
		//null check
		//super.execute(bot);
		if(nearestFriend == null) {
			return ("do nothing");
		}
		super.execute(bot);
		//NavigateToLocationAction(locationOfNearestTeammate(bot));
		return ("navigating to teammate: " + friend.getName());
	}
}
