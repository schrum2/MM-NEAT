package edu.southwestern.evolution.mapelites.generalmappings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.southwestern.evolution.mapelites.BaseBinLabels;

/**
 * Combines two sets of bin labels into a new
 * 2D archive. No matter what the dimensions of
 * the original labels were, they are combined
 * into a 2D archive.
 */
public class CombinationBinLabels extends BaseBinLabels {

	private BaseBinLabels first;
	private BaseBinLabels second;

	private int firstSize = -1;
	private int secondSize = -1;
	
	private List<String> labels = null;

	public CombinationBinLabels(BaseBinLabels first, BaseBinLabels second) {
		this.first = first;
		this.second = second;
	}
	
	@Override
	public List<String> binLabels() {
		if(labels == null) {
			List<String> firstLabels = first.binLabels();
			List<String> secondLabels = second.binLabels();
			
			firstSize = firstLabels.size();
			secondSize = secondLabels.size();
			
			labels = new ArrayList<>(firstSize * secondSize);
			for(int i = 0; i < firstSize; i++) {
				for(int j = 0; j < secondSize; j++) {
					labels.add(firstLabels.get(i)+"_"+secondLabels.get(j));
				}
			}
		}
		return labels;
	}

	@Override
	public int oneDimensionalIndex(int[] multi) {
		int firstIndex = multi[0];
		int secondIndex = multi[1];
		return secondIndex + firstIndex*secondSize;
	}

	@Override
	public int[] multiDimensionalIndices(HashMap<String, Object> keys) {
		int[] firstMulti = first.multiDimensionalIndices(keys);
		int firstIndex = first.oneDimensionalIndex(firstMulti);
		
		int[] secondMulti = second.multiDimensionalIndices(keys);
		int secondIndex = second.oneDimensionalIndex(secondMulti);

		return new int[] {firstIndex, secondIndex};
	}

	@Override
	public String[] dimensions() {
		return new String[] {first.getClass().getSimpleName()+"", second.getClass().getSimpleName()+""};
	}

	@Override
	public int[] dimensionSizes() {
		return new int[] {firstSize, secondSize};
	}

}
