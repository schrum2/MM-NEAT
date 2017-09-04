package org.rlcommunity.environments.cartpole.visualizer;

import org.rlcommunity.environments.cartpole.messages.CartpoleTrackRequest;
import org.rlcommunity.environments.cartpole.messages.CartpoleTrackResponse;
import rlVizLib.general.TinyGlue;
import rlVizLib.visualization.AbstractVisualizer;
import org.rlcommunity.rlglue.codec.types.Observation;
import rlVizLib.visualization.GenericScoreComponent;
import rlVizLib.visualization.SelfUpdatingVizComponent;
import rlVizLib.visualization.interfaces.GlueStateProvider;

public class CartPoleVisualizer extends AbstractVisualizer implements GlueStateProvider {

	private TinyGlue theGlueState = null;
	private CartpoleTrackResponse trackResponse = null;
	@SuppressWarnings("unused")
	private int lastStateUpdateTimeStep = -1;

	/**
	 * Creates a new Cart Pile Visualizer
	 *
	 * @param theGlueState
	 *            Global glue state object
	 */
	public CartPoleVisualizer(TinyGlue theGlueState) {
		super();
		this.theGlueState = theGlueState;

		SelfUpdatingVizComponent theTrackVisualizer = new CartPoleTrackComponent(this);
		SelfUpdatingVizComponent theCartVisualizer = new CartPoleCartComponent(this);

		SelfUpdatingVizComponent scoreComponent = new GenericScoreComponent(this);

		super.addVizComponentAtPositionWithSize(theTrackVisualizer, 0, 0, 1.0, 1.0);
		super.addVizComponentAtPositionWithSize(theCartVisualizer, 0, 0, 1.0, 1.0);
		super.addVizComponentAtPositionWithSize(scoreComponent, 0, 0, 1.0, 1.0);
	}

	public void checkCoreData() {
		if (trackResponse == null) {
			trackResponse = CartpoleTrackRequest.Execute();
		}
	}

	public double getLeftCartBound() {
		checkCoreData();
		return trackResponse.getLeftGoal();
	}

	public double getRightCartBound() {
		checkCoreData();
		return trackResponse.getRightGoal();
	}

	public double currentXPos() {
		Observation lastObservation = theGlueState.getLastObservation();
		if (lastObservation != null) {
			return lastObservation.doubleArray[0];
		} else {
			return 0.0f;
		}
	}

	public double getMinAngle() {
		checkCoreData();
		return trackResponse.getMinAngle() - 2.0 * Math.PI / 4.0;
	}

	public double getMaxAngle() {
		checkCoreData();
		return trackResponse.getMaxAngle() - 2.0 * Math.PI / 4.0;
	}

	public double getAngle() {
		Observation lastObservation = theGlueState.getLastObservation();
		if (lastObservation != null) {
			return lastObservation.doubleArray[2] - 2.0 * Math.PI / 4.0;
		} else {
			return 0.0f;
		}
	}

	public TinyGlue getTheGlueState() {
		return theGlueState;
	}

	public String getName() {
		return "Cart-Pole 1.0 (DEV)";
	}
}
