package edu.southwestern.networks.dl4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.zoo.PretrainedType;
import org.deeplearning4j.zoo.ZooModel;
import org.deeplearning4j.zoo.ZooType;
import org.deeplearning4j.zoo.model.ResNet50;
import org.deeplearning4j.zoo.model.VGG16;
import org.deeplearning4j.zoo.model.VGG19;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;

import edu.southwestern.util.graphics.ImageNetClassification;

public abstract class AllZooModelImageNetModels implements TensorNetwork {

	// All models
	static Map<String,ComputationGraph> imageNetModels = null; // null until initialized
	
	public AllZooModelImageNetModels() {
		initAllImageNets(); 
	}
	
	/**
	 * Apply each model to the input and save all outputs in a hash map along with the model name as a key
	 * (assumes initAllImageNets() has already been called)
	 * @param input Array containing image of appropriate size for ImageNet
	 * @return Map of model names to prediction scores for input image
	 */
	public static Map<String, INDArray> runAllModels(INDArray input) {
		assert imageNetModels != null;
		Map<String, INDArray> result = new HashMap<String, INDArray>();
		for(String modelName : imageNetModels.keySet()) {
			ComputationGraph model = imageNetModels.get(modelName);
			INDArray nextScores = model.output(input)[0].getRow(0);
			result.put(modelName, nextScores);
		}
		return result;
	}
	
	/**
	 * Names of all initialized ImageNet models (assumes initAllImageNets() has already been called)
	 * @return
	 */
	public static Set<String> getModelNames() {
		assert imageNetModels != null;
		return imageNetModels.keySet();
	}
	
	/**
	 * Initiates all known ImageNet models
	 */
	public static void initAllImageNets() {
		if(imageNetModels == null) {
			imageNetModels = new HashMap<String,ComputationGraph>();
			// This used to be possible in the previous DL4J
			//@SuppressWarnings("rawtypes")
			//Map<ZooType, ZooModel> models = ModelSelector.select(ZooType.CNN);
			
			// Have to select them manually now
			@SuppressWarnings("rawtypes")
			Map<ZooType, ZooModel> models = new HashMap<ZooType, ZooModel>();
			models.put(ZooType.CNN, VGG16.builder().numClasses(ImageNetClassification.NUM_IMAGE_NET_CLASSES).build());
			models.put(ZooType.CNN, VGG19.builder().numClasses(ImageNetClassification.NUM_IMAGE_NET_CLASSES).build());
			models.put(ZooType.CNN, ResNet50.builder().numClasses(ImageNetClassification.NUM_IMAGE_NET_CLASSES).build());
			
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
	
	/**
	 * flush all models
	 */
	public void flush() {
		for(ComputationGraph model : imageNetModels.values()) {
			model.rnnClearPreviousState();
		}
	}
	
	@Override
	public void fit(INDArray input, INDArray targets) {
		throw new UnsupportedOperationException("Don't change pre-trained ImageNet models");
	}

	@Override
	public void fit(DataSet batch) {
		throw new UnsupportedOperationException("Don't change pre-trained ImageNet models");
	}
}
