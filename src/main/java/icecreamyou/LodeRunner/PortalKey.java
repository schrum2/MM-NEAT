package icecreamyou.LodeRunner;
/**
 * PortalKeys unlock portals.
 */
public class PortalKey extends Key {
	
	public static final String TITLE = "Portal key";
	public static final String NAME = "portalKey";
	@Override
	public String title() {
		return TITLE;
	}
	@Override
	public String name() {
		return NAME;
	}

	public PortalKey(int x, int y) {
		super(x, y);
	}

}
