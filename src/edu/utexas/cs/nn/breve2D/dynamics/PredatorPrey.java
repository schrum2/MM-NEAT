package edu.utexas.cs.nn.breve2D.dynamics;

/**
 *
 * @author Jacob Schrum
 */
public class PredatorPrey extends MultitaskDynamics {

    public PredatorPrey() {
        super(new Breve2DDynamics[]{new PlayerPredatorMonsterPrey(), new PlayerPreyMonsterPredator()});
    }

    @Override
    public int numInputSensors() {
        return 31; //41;
    }
}
