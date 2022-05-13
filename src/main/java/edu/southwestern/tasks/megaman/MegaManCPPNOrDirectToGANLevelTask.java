package edu.southwestern.tasks.megaman;

import java.util.ArrayList;
import java.util.List;

import edu.southwestern.evolution.genotypes.CPPNOrDirectToGANGenotype;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.networks.Network;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.BoundedTask;
import edu.southwestern.tasks.interactive.megaman.MegaManCPPNtoGANLevelBreederTask;
import edu.southwestern.tasks.megaman.gan.MegaManGANUtil;
import edu.southwestern.util.datastructures.ArrayUtil;

@SuppressWarnings("rawtypes")
public class MegaManCPPNOrDirectToGANLevelTask extends MegaManLevelTask implements BoundedTask{

	@Override
	public List getMegaManLevelListRepresentationFromGenotype(Genotype individual,
			MegaManTrackSegmentType segmentTypeTracker) {
		CPPNOrDirectToGANGenotype m = (CPPNOrDirectToGANGenotype) individual;

		if(m.getFirstForm()) {
			return MegaManGANUtil.cppnToMegaManLevel(MegaManGANUtil.getMegaManGANGenerator(),(Network) individual.getPhenotype(), Parameters.parameters.integerParameter("megaManGANLevelChunks"), ArrayUtil.doubleOnes(MegaManCPPNtoGANLevelBreederTask.SENSOR_LABELS.length), segmentTypeTracker);
			//return MegaManCPPNtoGANLevelTask.getMegaManLevelListRepresentationFromGenotype(individual,segmentTypeTracker);
		}else {
			@SuppressWarnings("unchecked")
			List<Double> latentVector = (List<Double>) individual.getPhenotype();
			return MegaManGANLevelTask.getMegaManLevelListRepresentationFromStaticGenotype(MegaManGANUtil.getMegaManGANGenerator(), latentVector, Parameters.parameters.integerParameter("megaManGANLevelChunks"), segmentTypeTracker);
			//return MegaManGANLevelTask.getMegaManLevelListRepresentationFromGenotype((ArrayList<Double>)m.getPhenotype(),segmentTypeTracker);
		}
	}
	
	@Override
	public double[] getUpperBounds() {
		return MegaManGANLevelTask.getStaticUpperBounds();
	}

	@Override
	public double[] getLowerBounds() {
		return MegaManGANLevelTask.getStaticLowerBounds();
	}

}
