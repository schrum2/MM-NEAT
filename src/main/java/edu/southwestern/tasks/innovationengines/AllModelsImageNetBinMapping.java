package edu.southwestern.tasks.innovationengines;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import edu.southwestern.evolution.mapelites.BinLabels;
import edu.southwestern.networks.Network;
import edu.southwestern.networks.dl4j.AllZooModelImageNetModels;
import edu.southwestern.util.graphics.ImageNetClassification;

/**
 * Multiple copies of all ImageNet classes, prefixed by the DNN that classified the image
 * @author Jacob Schrum
 */
public class AllModelsImageNetBinMapping<T extends Network> implements BinLabels {

	// Compute at construction and save for later
	List<String> cachedLabels;
	
	public AllModelsImageNetBinMapping() {
		AllZooModelImageNetModels.initAllImageNets(); // Initialize if not already done
		Set<String> modelNames = AllZooModelImageNetModels.getModelNames();
		List<String> classLabels = ImageNetClassification.getImageNetLabels();
		cachedLabels = new ArrayList<>(ImageNetClassification.NUM_IMAGE_NET_CLASSES * modelNames.size());
		// For each model, create a bin for all ImageNet class labels
		for(String model: modelNames) {
			// Model names are full class paths. This drops all packages and leaves only the class name
			String shortName = model.substring(model.lastIndexOf('.') + 1);
			for(String label: classLabels) {
				cachedLabels.add(shortName + "-" + label);
			}
		}
	}
	
	/**
	 * All 1000 ImageNet labels
	 */
	@Override
	public List<String> binLabels() {
		return cachedLabels;
	}

	@Override
	public int oneDimensionalIndex(int[] multi) {
		return multi[0]; // 1D archive
	}

	@Override
	public String[] dimensions() {
		return new String[] {"Model", "Image Category"}; // TODO Be tested
	}
	
	@Override
	public int[] dimensionSizes() {
		return new int[] {3, 1000};
	}

	@Override
	public int oneDimensionalIndex(HashMap<String, Object> keys) {
		throw new UnsupportedOperationException("One image can exist in all bins, so selecting one index with a HashMap is not supported");
	}

	@Override
	public int[] multiDimensionalIndices(HashMap<String, Object> keys) {
		throw new UnsupportedOperationException("One image can exist in all bins, so selecting one index with a HashMap is not supported");
	}

	@Override
	public boolean discard(HashMap<String, Object> behaviorMap) {
		return false;
	}

	@Override
	public boolean isOutsideRestrictedRange(int[] multi) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String lowerRestrictedBounds() {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public String upperRestrictedBounds() {
		// TODO Auto-generated method stub
		return "";
	}
}
