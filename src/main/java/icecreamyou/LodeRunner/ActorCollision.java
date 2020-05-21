package icecreamyou.LodeRunner;
/**
 * Objects that have special spatial interactions with players beyond ordinary intersection.
 */
public interface ActorCollision {

	/**
	 * Determines whether the Actor a is close enough to this to interact with it.
	 */
	public boolean actorIsOn(Actor a);

}
