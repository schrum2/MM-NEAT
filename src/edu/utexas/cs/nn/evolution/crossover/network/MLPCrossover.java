/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.evolution.crossover.network;

import edu.utexas.cs.nn.evolution.crossover.ArrayCrossover;
import edu.utexas.cs.nn.evolution.crossover.Crossover;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.evolution.genotypes.MLPGenotype;
import edu.utexas.cs.nn.evolution.genotypes.RealValuedGenotype;
import edu.utexas.cs.nn.networks.MLP;
import java.util.ArrayList;

/**
 *
 * @author Jacob Schrum
 */
public class MLPCrossover extends Crossover<MLP> {

    private final ArrayCrossover<Double> vc;

    public MLPCrossover() {
        vc = new ArrayCrossover<Double>();
    }

    @Override
    public Genotype<MLP> crossover(Genotype<MLP> toModify, Genotype<MLP> toReturn) {
        // Get MLP weights
        double[][] modFirst = toModify.getPhenotype().firstConnectionLayer;
        double[][] modSecond = toModify.getPhenotype().secondConnectionLayer;
        double[][] retFirst = toReturn.getPhenotype().firstConnectionLayer;
        double[][] retSecond = toReturn.getPhenotype().secondConnectionLayer;

        int inputs = modFirst.length;
        int hidden = modFirst[0].length;
        int outputs = modSecond[0].length;

        //System.out.println("In:"+inputs+",Hidden:"+hidden+",Out:"+outputs);
        ArrayList<Double> mod = new ArrayList<Double>(inputs * hidden);
        ArrayList<Double> ret = new ArrayList<Double>(inputs * hidden);

        // Put weights in one linear array
        for (int i = 0; i < inputs; i++) {
            for (int j = 0; j < hidden; j++) {
                mod.add(modFirst[i][j]);
                ret.add(retFirst[i][j]);
            }
        }

        for (int i = 0; i < hidden; i++) {
            for (int j = 0; j < outputs; j++) {
                mod.add(modSecond[i][j]);
                ret.add(retSecond[i][j]);
            }
        }

        //System.out.println("Mod:"+mod.size()+",Ret:"+ret.size());

        // Cross
        RealValuedGenotype modG = new RealValuedGenotype(mod);
        RealValuedGenotype retG = new RealValuedGenotype(ret);
        Genotype<ArrayList<Double>> resultG = vc.crossover(modG, retG);

        double[][] rModFirst = new double[inputs][hidden];
        double[][] rModSecond = new double[hidden][outputs];
        double[][] rRetFirst = new double[inputs][hidden];
        double[][] rRetSecond = new double[hidden][outputs];

        // Put weights back in layered structure
        int pos = 0;
        for (int i = 0; i < inputs; i++) {
            for (int j = 0; j < hidden; j++) {
                rModFirst[i][j] = modG.getPhenotype().get(pos);
                rRetFirst[i][j] = resultG.getPhenotype().get(pos);
                pos++;
            }
        }

        for (int i = 0; i < hidden; i++) {
            for (int j = 0; j < outputs; j++) {
                rModSecond[i][j] = modG.getPhenotype().get(pos);
                rRetSecond[i][j] = resultG.getPhenotype().get(pos);
                pos++;
            }
        }

        ((MLPGenotype) toModify).firstConnectionLayer = rModFirst;
        ((MLPGenotype) toModify).secondConnectionLayer = rModSecond;
        return new MLPGenotype(new MLP(rRetFirst, rRetSecond));
    }
}
