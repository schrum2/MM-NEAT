package edu.southwestern.tasks.rlglue.init;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.tasks.rlglue.RLGlueEnvironment;
import edu.southwestern.tasks.rlglue.featureextractors.FeatureExtractor;
import edu.southwestern.util.ClassCreation;

public class RLGlueInitialization {

	public static void setupRLGlue() throws NoSuchMethodException {
		// RL-Glue environment, if RL-Glue is being used
		MMNEAT.rlGlueEnvironment = (RLGlueEnvironment) ClassCreation.createObject("rlGlueEnvironment");
		if (MMNEAT.rlGlueEnvironment != null) {
			System.out.println("Define RL-Glue Task Spec");
			MMNEAT.tso = MMNEAT.rlGlueEnvironment.makeTaskSpec();
			MMNEAT.rlGlueExtractor = (FeatureExtractor) ClassCreation.createObject("rlGlueExtractor");
		}
	}
}
