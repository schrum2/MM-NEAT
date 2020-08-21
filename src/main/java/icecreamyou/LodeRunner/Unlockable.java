package icecreamyou.LodeRunner;
/**
 * Unlockables can be unlocked by keys.
 */
public interface Unlockable {

	/**
	 * Determines whether the given Key will unlock this.
	 */
	public boolean isUnlockedBy(Key key);
	
	/**
	 * Whether this is locked or unlocked.
	 */
	public boolean isLocked();
	
	/**
	 * Set this to either locked or unlocked.
	 */
	public void setLocked(boolean locked);
	
}
