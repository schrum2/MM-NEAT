package distance.convolution;

// a convolutional version of the N-Tuple

// it actually uses a standard N-Tuple, but via a convolutional expansion of the input


import distance.kl.KLDiv;
import distance.pattern.Pattern;
import distance.pattern.PatternDistribution;
import utilities.Picker;
import utilities.StatSummary;

import java.util.ArrayList;

public class ConvNTuple {

    double epsilon = 0.00001;


    public ConvNTuple() {
        // reset();
    }

    // self explanatory variables
    public int imageWidth, imageHeight;

    public int filterWidth, filterHeight;

    int stride;

    int mValues; // number of values possible in each image pixel / tile

    public PatternDistribution sampleDis;

    // store every solution ever sampled, ready to return the best one when ready
    // since the fitness estimate is always being updated, best to do all these at the end

    ArrayList<int[]> solutions;
    private Picker<int[]> picker;

    int nSamples;

    public ConvNTuple reset() {
        // avoid taking log of zero
        nSamples = 1;
        // ntMap = new HashMap<>();
        sampleDis = new PatternDistribution();
        solutions = new ArrayList<>();
        picker = new Picker<>();
        return this;
    }

    public ConvNTuple setEpsilon(double epsilon) {
        this.epsilon = epsilon;
        return this;
    }

    public ConvNTuple setImageDimensions(int imageWidth, int imageHeight) {
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        return this;
    }

    public ConvNTuple setFilterDimensions(int filterWidth, int filterHeight) {
        this.filterWidth = filterWidth;
        this.filterHeight = filterHeight;
        return this;
    }

    public ConvNTuple setStride(int stride) {
        this.stride = stride;
        return this;
    }

    public double address(int[] image, int[] index) {
        // System.out.println(image.length);
        double prod = 1;
        double addr = 0;
        for (int i : index) { //  i<tuple.length; i++) {
            // System.out.println(i);
            addr += prod * image[i];
            prod *= mValues;
        }
        return addr;
    }

    public ArrayList<int[]> indices;

    // todo: add a filter gap parameter as well to allow
    //

    public ConvNTuple makeIndices() {
        indices = new ArrayList<>();
        for (int i = 0; i <= imageWidth - filterWidth; i += stride) {
            for (int j = 0; j <= imageHeight - filterHeight; j += stride) {
                // i and j are the start points in the image
                // but we need to iterate over all the filter positions
                // for each start point we create an array of filter sample points in image vector coordinates
                // first calculate the image x,y points, then map them to the one-d vector coords
                int[] a = new int[filterWidth * filterHeight];
                int filterIndex = 0;
                for (int k = 0; k < filterWidth; k++) {
                    for (int l = 0; l < filterHeight; l++) {
                        int x = i + k;
                        int y = j + l;
                        int ix = x + imageWidth * y;
                        a[filterIndex] = ix;
                        filterIndex++;
                    }
                }
                // System.out.println(Arrays.toString(a));
                indices.add(a);
            }
        }
        // DO NOT reset the stats after making new indices
        // reset();
        // System.out.println("Made index vectors: " + indices.size());
        return this;
    }

    public ConvNTuple makeWrapAroundIndices() {
        indices = new ArrayList<>();
        // the iteration now is for i and j up to the edge of the image
        // we then calculate the indices modulo this
        for (int i = 0; i < imageWidth; i += stride) {
            for (int j = 0; j < imageHeight; j += stride) {
                // i and j are the start points in the image
                // but we need to iterate over all the filter positions
                // for each start point we create an array of filter sample points in image vector coordinates
                // first calculate the image x,y points, then map them to the one-d vector coords
                int[] a = new int[filterWidth * filterHeight];
                int filterIndex = 0;
                for (int k = 0; k < filterWidth; k++) {
                    for (int l = 0; l < filterHeight; l++) {
                        int x = (i + k) % imageWidth;
                        int y = (j + l) % imageHeight;
                        int ix = x + imageWidth * y;
                        a[filterIndex] = ix;
                        filterIndex++;
                    }
                }
                // System.out.println(Arrays.toString(a));
                indices.add(a);
            }
        }
        // do NOT reset the stats after making new indices

        // reset();
        // System.out.println("Made index vectors: " + indices.size());
        return this;
    }

    public double addressSpaceSize() {
        // return SearchSpaceUtil.size(searchSpace);
        double size = 1;
        for (int i = 0; i < filterWidth * filterHeight; i++) {
            size *= mValues;
        }
        return size;
    }


    public int[] flatten(int[][] a) {
        int n = a.length * a[0].length;
        int w = a.length;
        if (w != this.imageWidth)
            throw new RuntimeException("Image width not equal to Sample Width: " + w + " : " + imageWidth);
        int[] x = new int[n];
        for (int i = 0; i < n; i++) {
            x[i] = a[i % w][i / w];
        }
        return x;
    }

    boolean storeIndexArrays = true;

    public void addPoint(int[][] p, double value) {
        addPoint(flatten(p), value);
    }

    public void addPoint(int[] p, double value) {
        // iterate over all the indices
        // calculate an address for each one

        // System.out.println(" ADDING A POINT !!!!!!!!!!!!!!!!!!!!!!");
        for (int[] index : indices) {
            // double address = address(p, index);
            Pattern pattern = new Pattern().setPattern(p, index);
            sampleDis.add(pattern);
        }
        solutions.add(p);
        picker.add(value, p);
        nSamples++;
//        if (storeIndexArrays) {
//            addIndexArrays(p);
//        }
        // return this;
    }

    public void addIndexArrays(int[] p) {
        // iterate over all the indices
        // create an array of values for each one

        for (int[] index : indices) {
            int[] values = new int[index.length];
            for (int i = 0; i < index.length; i++) {
                values[i] = p[index[i]];
            }
            // sampleDis.addValueArray(address(p, index), values);
        }
    }

    public int nSamples() {
        return nSamples;
    }

    public int nEntries() {
        return sampleDis.tot; // .keySet().size();
    }

//    public StatSummary getStatsForceCreate(double address) {
//        // System.out.println(ntMap);
//        StatSummary ss = ntMap.get(address);
//        if (ss == null) {
//            ss = new StatSummary();
//
//            ntMap.put(address, ss);
//        }
//        return ss;
//    }


    public boolean useWeightedMean = false;

    public StatSummary getNoveltyStats(int[] x) {
        StatSummary ssTot = new StatSummary();
        for (int[] index : indices) {
            double address = address(x, index);
            StatSummary ss = sampleDis.statMap.get(address);
            if (ss != null) {
                ssTot.add(ss.n());
            } else {
                ssTot.add(0);
            }
        }
        return ssTot;
    }

    /**
     * @param x: probe image vector
     * @return quality of fit to trained distribution
     */
    // note that epsilon is the punishment for included non-observed values
    // it is the KL Divergence between the two distributions
    public double getKLDivergence(int[] x, double epsilon) {
        // create a new SampleDis for this image
        PatternDistribution qDis = new PatternDistribution();
        for (int[] index : indices) {
            // double address = address(x, index);
            Pattern pattern = new Pattern().setPattern(x, index);
            qDis.add(pattern);
        }
        // return Math.random();
        return KLDiv.klDivSymmetric(sampleDis, qDis);
        // return PatternDistribution.klDiv(sampleDis, qDis);
        // return PatternDistribution.klDiv(qDis, sampleDis);
    }

    double k = 2.0;

    public double explore(int n_i) {
        return k * Math.sqrt(Math.log(nSamples) / (epsilon + n_i));
    }

}
