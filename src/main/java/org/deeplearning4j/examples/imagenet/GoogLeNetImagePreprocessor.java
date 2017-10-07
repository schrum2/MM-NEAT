package org.deeplearning4j.examples.imagenet;

import lombok.extern.slf4j.Slf4j;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.ops.impl.broadcast.BroadcastAddOp;
import org.nd4j.linalg.api.ops.impl.broadcast.BroadcastSubOp;
import org.nd4j.linalg.dataset.api.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.serializer.NormalizerType;
import org.nd4j.linalg.factory.Nd4j;

/**
 * Based on VGGImagePreprocessor, but assumes image is in RGB order.
 */
@Slf4j
public class GoogLeNetImagePreprocessor implements DataNormalization {

	// VGG using BGR
    //public static final INDArray GOOGLENET_MEAN_OFFSET_BGR = Nd4j.create(new double[] {123.68, 116.779, 103.939});
	// GoogLeNet uses RGB
    public static final INDArray GOOGLENET_MEAN_OFFSET_BGR = Nd4j.create(new double[] {103.939, 116.779, 123.68});
	
    /**
     * Fit a dataset (only compute
     * based on the statistics from this dataset0
     *
     * @param dataSet the dataset to compute on
     */
    @Override
    public void fit(DataSet dataSet) {

    }

    /**
     * Iterates over a dataset
     * accumulating statistics for normalization
     *
     * @param iterator the iterator to use for
     *                 collecting statistics.
     */
    @Override
    public void fit(DataSetIterator iterator) {

    }

    @Override
    public void preProcess(DataSet toPreProcess) {
        INDArray features = toPreProcess.getFeatures();
        this.preProcess(features);
    }

    public void preProcess(INDArray features) {
        Nd4j.getExecutioner().execAndReturn(new BroadcastSubOp(features.dup(), GOOGLENET_MEAN_OFFSET_BGR, features, 1));
    }

    /**
     * Transform the data
     * @param toPreProcess the dataset to transform
     */
    @Override
    public void transform(DataSet toPreProcess) {
        this.preProcess(toPreProcess);
    }

    @Override
    public void transform(INDArray features) {
        this.preProcess(features);
    }

    @Override
    public void transform(INDArray features, INDArray featuresMask) {
        transform(features);
    }

    @Override
    public void transformLabel(INDArray label) {
        //No op
    }

    @Override
    public void transformLabel(INDArray labels, INDArray labelsMask) {
        transformLabel(labels);
    }

    @Override
    public void revert(DataSet toRevert) {
        revertFeatures(toRevert.getFeatures());
    }

    @Override
    public NormalizerType getType() {
        return NormalizerType.IMAGE_VGG16;
    }

    @Override
    public void revertFeatures(INDArray features) {
        Nd4j.getExecutioner().execAndReturn(new BroadcastAddOp(features.dup(), GOOGLENET_MEAN_OFFSET_BGR, features, 1));
    }

    @Override
    public void revertFeatures(INDArray features, INDArray featuresMask) {
        revertFeatures(features);
    }

    @Override
    public void revertLabels(INDArray labels) {
        //No op
    }

    @Override
    public void revertLabels(INDArray labels, INDArray labelsMask) {
        revertLabels(labels);
    }

    @Override
    public void fitLabel(boolean fitLabels) {
        if (fitLabels) {
//            log.warn("Labels fitting not currently supported for ImagePreProcessingScaler. Labels will not be modified");
        }
    }

    @Override
    public boolean isFitLabel() {
        return false;
    }
}
