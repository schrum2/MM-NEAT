package com.aqwis.models;

import java.awt.image.BufferedImage;
import java.util.Random;
import java.util.stream.DoubleStream;

public abstract class WFCModel {
    protected boolean[][][] wave;
    protected boolean[][] changes;
    protected double[] stationary;

    protected int FMX, FMY, T, limit;
    protected Random random;
    protected boolean periodic;

    double[] logProb;
    double logT;

    protected abstract Boolean propagate();

    private Boolean observe() {
        double min = 1E+3;
        double sum, mainSum, logSum, noise, entropy;

        int argminx = -1;
        int argminy = -1;
        int amount;

        for (int x = 0; x < FMX; x++) {
            for (int y = 0; y < FMY; y++) {
                if (onBoundary(x, y)) {
                    continue;
                }

                amount = 0;
                sum = 0;

                for (int t = 0; t < T; t++) {
                    if (wave[x][y][t]) {
                        amount++;
                        sum += stationary[t];
                    }
                }

                if (sum == 0) {
                    return false;
                }

                noise = 1E-6 * random.nextDouble();

                if (amount == 1) {
                    entropy = 0;
                } else if (amount == T) {
                    entropy = logT;
                } else {
                    mainSum = 0;
                    logSum = Math.log(sum);

                    for (int t = 0; t < T; t++) {
                        if (wave[x][y][t]) {
                            mainSum += stationary[t] * logProb[t];
                        }
                    }

                    entropy = logSum - mainSum/sum;
                }

                if (entropy > 0 && entropy+noise < min) {
                    min = entropy + noise;
                    argminx = x;
                    argminy = y;
                }
            }
        }

        if (argminx == -1 && argminy == -1) {
            return true;
        }

        double[] distribution = new double[T];
        for (int t = 0; t < T; t++) {
            distribution[t] = wave[argminx][argminy][t] ? stationary[t]: 0;
        }

        int r = randomChoice(distribution, random.nextDouble());
        for (int t = 0; t < T; t++) {
            wave[argminx][argminy][t] = t == r;
        }

        changes[argminx][argminy] = true;

        return null;
    }

    public boolean run(int seed, int limit) {
        logT = Math.log(T);
        logProb = new double[T];

        for (int t = 0; t < T; t++) {
            logProb[t] = Math.log(stationary[t]);
        }

        clear();

        random = new Random(seed);
        for (int l = 0; l < limit || limit == 0; l++) {
            Boolean result = observe();
            if (result != null) {
                return result;
            }
            while (propagate()) {}
        }

        return true;
    }

    protected void clear() {
        for (int x = 0; x < FMX; x++) {
            for (int y = 0; y < FMY; y++) {
                for (int t = 0; t < T; t++) {
                    wave[x][y][t] = true;
                }
                changes[x][y] = false;
            }
        }
    }

    protected abstract boolean onBoundary(int x, int y);
    public abstract BufferedImage graphics();

    public static int randomChoice(double[] a, double r)
    {
        double sum = DoubleStream.of(a).sum();

        if (sum == 0)
        {
            for (int j = 0; j < a.length; j++) {
                a[j] = 1;
            }
            sum = a.length;
        }

        for (int j = 0; j < a.length; j++) {
            a[j] /= sum;
        }

        int i = 0;
        double x = 0;

        while (i < a.length) {
            x += a[i];
            if (r <= x) return i;
            i++;
        }

        return 0;
    }
}
