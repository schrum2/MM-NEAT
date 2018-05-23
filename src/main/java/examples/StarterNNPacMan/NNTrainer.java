package examples.StarterNNPacMan;

import examples.StarterNNPacMan.examples.NNPacMan;
import org.jenetics.DoubleChromosome;
import org.jenetics.DoubleGene;
import org.jenetics.Genotype;
import org.jenetics.NumericGene;
import org.jenetics.engine.Engine;
import org.jenetics.engine.EvolutionResult;
import org.jenetics.engine.limit;
import org.jenetics.util.Factory;

import java.io.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Created by piers on 13/10/16.
 */
public class NNTrainer {


    public static void main(String[] args) throws FileNotFoundException {

        // Need to be able to make these things
        NeuralPacMan pacman = new NNPacMan(null);

        // GA Parameters
        int populationSize = 100;

        // Decide the shape of the networks
        int inputLength = pacman.getInputLength();
        int outputLength = pacman.getOutputLength();
        int numberOfLayers = pacman.getNumberOfHiddenLayers() + 2;
        int neuronsPerHiddenLayer = pacman.getNeuronsPerHiddenLayer();

        String info = String.format("%s, %d, %d, %d, %d", pacman.getClass().getCanonicalName(), inputLength, outputLength, numberOfLayers, neuronsPerHiddenLayer);
        System.out.println(info);

        // numberOfWeightsNeeded
        NeuralNet sample = new NeuralNet(inputLength, outputLength, numberOfLayers, neuronsPerHiddenLayer);
        sample.createNet();
        int weightsTotal = sample.getNumberOfWeights();
        System.out.println(weightsTotal);

        final Factory<Genotype<DoubleGene>> gtf = Genotype.of(
                DoubleChromosome.of(0, 1, weightsTotal)
        );

        // Don't want all the output from Executor in the GA
        PrintStream out = System.out;

        // Jenetics configuration now
        final Engine<DoubleGene, Double> engine = Engine
                .builder(pacman::getFitnessFunctionJenetics, gtf)
                .populationSize(populationSize)
                .build();


        final Genotype<DoubleGene> result = engine.stream()
                .limit(limit.byExecutionTime(Duration.ofMinutes(120)))
//                .limit(100)
                .peek(x -> {
                    System.setOut(out);
                    System.out.println("Generation: " + x.getGeneration());
                    System.out.println("Best Fitness: " + x.getBestFitness());
                })
                .collect(EvolutionResult.toBestGenotype());

        saveModel(
                result,
                new int[]{
                        pacman.getInputLength(),
                        pacman.getOutputLength(),
                        pacman.getNumberOfHiddenLayers() + 2,
                        pacman.getNeuronsPerHiddenLayer()},
                "results/model.txt"
        );
    }


    public static void saveModel(Genotype<DoubleGene> model, int[] params, String filename) throws FileNotFoundException{
        ArrayList<Double> weights = model.getChromosome().stream()
                .map(NumericGene::doubleValue)
                .collect(Collectors.toCollection(ArrayList::new));
        NeuralNet net = new NeuralNet(params[0], params[1], params[2], params[3]);
        net.createNet();
        net.setWeights(weights);
        NeuralNet.saveToFile(filename, net);
    }
}

class SilentStream extends PrintStream {
    public SilentStream(OutputStream outputStream) {
        super(outputStream);
    }

    public SilentStream(OutputStream outputStream, boolean b) {
        super(outputStream, b);
    }

    public SilentStream(OutputStream outputStream, boolean b, String s) throws UnsupportedEncodingException {
        super(outputStream, b, s);
    }

    public SilentStream(String s) throws FileNotFoundException {
        super(s);
    }

    public SilentStream(String s, String s1) throws FileNotFoundException, UnsupportedEncodingException {
        super(s, s1);
    }

    public SilentStream(File file) throws FileNotFoundException {
        super(file);
    }

    public SilentStream(File file, String s) throws FileNotFoundException, UnsupportedEncodingException {
        super(file, s);
    }

    @Override
    public void println(String s) {

    }

    @Override
    public void println(Object o) {

    }
}
