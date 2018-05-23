package examples.StarterNNPacMan;

import org.jenetics.DoubleGene;
import org.jenetics.Genotype;
import org.jenetics.NumericGene;
import pacman.Executor;
import pacman.controllers.PacmanController;
import pacman.controllers.examples.po.POCommGhosts;
import pacman.game.util.Stats;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Created by piers on 14/10/16.
 */
public abstract class NeuralPacMan extends PacmanController{

    protected NeuralNet net;

    public NeuralPacMan(NeuralNet net) {
        this.net = net;
    }

    public abstract int getInputLength();
    public abstract int getOutputLength();
    public abstract int getNumberOfHiddenLayers();
    public abstract int getNeuronsPerHiddenLayer();

    public abstract NeuralPacMan getPacManForTraining(NeuralNet net);


    public double getFitnessFunctionJenetics(final Genotype<DoubleGene> gt){
        ArrayList<Double> weights = gt.getChromosome().stream().map(NumericGene::doubleValue).collect(Collectors.toCollection(ArrayList::new));
        NeuralNet net = new NeuralNet(
                getInputLength(),
                getOutputLength(),
                getNumberOfHiddenLayers() + 2,
                getNeuronsPerHiddenLayer()
        );

        net.createNet();
        net.setWeights(weights);

        NeuralPacMan pacman = getPacManForTraining(net);
        PrintStream out = System.out;
        System.setOut(new SilentStream(out));

        Executor executor = new Executor.Builder()
                .setTickLimit(4000)
                .build();
        Stats[] results = executor.runExperiment(pacman, new POCommGhosts(50), 15, "");
//        System.setOut(out);
        return results[0].getAverage();
    }

}
