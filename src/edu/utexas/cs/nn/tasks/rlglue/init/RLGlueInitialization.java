package edu.utexas.cs.nn.tasks.rlglue.init;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.tasks.rlglue.RLGlueEnvironment;
import edu.utexas.cs.nn.tasks.rlglue.featureextractors.FeatureExtractor;
import edu.utexas.cs.nn.util.ClassCreation;

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
