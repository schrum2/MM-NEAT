package edu.southwestern.networks.dl4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.zoo.ModelSelector;
import org.deeplearning4j.zoo.PretrainedType;
import org.deeplearning4j.zoo.ZooModel;
import org.deeplearning4j.zoo.ZooType;
import org.nd4j.linalg.api.ndarray.INDArray;

public class AllZooModelImageNetModels implements TensorNetwork {

	// All models
	private Map<String,ComputationGraph> imageNetModels;
	
	public AllZooModelImageNetModels() {
		initAllImageNets(); 
	}
	
	/**
	 * For the given inputs, apply all models, add up the scores, and
	 * then average them.
	 */
	@Override
	public INDArray output(INDArray input) {
		Iterator<String> itr = imageNetModels.keySet().iterator();
		ComputationGraph model = imageNetModels.get(itr.next());
		INDArray scores = model.output(input)[0]; // First set of scores
		while(itr.hasNext()) {
			model = imageNetModels.get(itr.next());
			INDArray nextScores = model.output(input)[0];
			// Add up the scores
			scores = scores.add(nextScores);
		}
		// scores is now the element-wise sum
		return scores.div(imageNetModels.size()); // Return element-wise average
	}
	
	/**
	 * Initiates all known ImageNet models
	 */
	private void initAllImageNets() {
		if(imageNetModels == null) {
			imageNetModels = new HashMap<String,ComputationGraph>();
			@SuppressWarnings("rawtypes")
			Map<ZooType, ZooModel> models = ModelSelector.select(ZooType.CNN);
			for (@SuppressWarnings("rawtypes") Map.Entry<ZooType, ZooModel> entry : models.entrySet()) {
				@SuppressWarnings("rawtypes")
				ZooModel zooModel = entry.getValue();
				if(zooModel.pretrainedAvailable(PretrainedType.IMAGENET)) {
					try {
						ComputationGraph model = (ComputationGraph) zooModel.initPretrained(PretrainedType.IMAGENET);
						String name = zooModel.getClass().getName();
						imageNetModels.put(name,model);
						System.out.println("Initialized " + name);
					} catch (IOException e) {
						System.out.println("Failed to load pre-trained ImageNet models");
						e.printStackTrace();
						System.exit(1);
					}
				}
			}
		}
	}
	

}
