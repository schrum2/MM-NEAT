package icecreamyou.LodeRunner;
/**
 * Coins are Gold with a value of 1.
 *
 * This is not a very useful class by itself; one might question why use Coin
 * instead of simply Gold with value 1. The answer is that this implementation
 * is future-proof; one can imagine coins having extra behavior that their
 * "heavier" counterparts like Treasure might not. 
 */
public class Coin extends Gold {

	public static final String TITLE = "Coin";
	public static final String NAME = "coin";
	@Override
	public String title() {
		return TITLE;
	}
	@Override
	public String name() {
		return NAME;
	}

	public Coin(int x, int y) {
		super(x, y);
	}

	@Override
	public int getValue() {
		// @see http://xkcd.com/221/
		return 1;
	}

}
