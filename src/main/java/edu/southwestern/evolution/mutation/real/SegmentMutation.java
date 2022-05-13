package edu.southwestern.evolution.mutation.real;

import java.util.ArrayList;

import edu.southwestern.tasks.interactive.gvgai.ZeldaCPPNtoGANLevelBreederTask;
import edu.southwestern.tasks.mario.gan.GANProcess;
import edu.southwestern.parameters.Parameters;

/**
 * A common parent class for the two segment based mutation classes 
 * (SegmentCopyMutation and SegmentSwapMutation) used to eliminate repeated
 * code in the constructors. 
 * 
 * @author Alejandro Medina
 *
 */
public abstract class SegmentMutation extends RealMutation {
	protected final int segmentSize;
	protected final int segmentAmount;
	protected final int auxVariableStartLocation;
	protected final int auxVariableEndLocation;
	protected final boolean segmentSwapAuxiliaryVarialbes;
	
	protected ArrayList<Double> storedSegment;
	
	public SegmentMutation(String rateLabel) {
		super(rateLabel);
		this.segmentSize = GANProcess.evolvedSegmentLength();
		this.segmentSwapAuxiliaryVarialbes = Parameters.parameters.booleanParameter("segmentSwapAuxiliaryVarialbes");
		switch(GANProcess.type) {
		case MARIO:
			this.segmentAmount = Parameters.parameters.integerParameter("marioGANLevelChunks");
			this.auxVariableStartLocation = -1;
			this.auxVariableEndLocation = -1;
			break;
		case ZELDA:
			this.segmentAmount = Parameters.parameters.integerParameter("zeldaGANLevelWidthChunks")*Parameters.parameters.integerParameter("zeldaGANLevelHeightChunks");
			this.auxVariableStartLocation = 0;
			this.auxVariableEndLocation = ZeldaCPPNtoGANLevelBreederTask.numberOfNonLatentVariables()-1; // inclusive index 
			break;
		case MEGA_MAN:
			this.segmentAmount = Parameters.parameters.integerParameter("megaManGANLevelChunks");
			this.auxVariableStartLocation = Parameters.parameters.integerParameter("megaManAuxVarsStart");
			this.auxVariableEndLocation = Parameters.parameters.integerParameter("megaManAuxVarsEnd");
			break;
		case LODE_RUNNER:
			// swapping not possible with Lode Runner since each level is just one segment
			Parameters.parameters.setDouble(rateLabel, 0.0);
			this.segmentAmount = 1;
			this.auxVariableStartLocation = -1;
			this.auxVariableEndLocation = -1;
			break;
		default:
			throw new UnsupportedOperationException("Pick a game");
		}
		this.storedSegment = new ArrayList<Double>(this.segmentSize);
	}
}
