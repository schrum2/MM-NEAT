package edu.utexas.cs.nn.util.util2D;

/**
 *
 * @author He_Deceives
 */
public interface ILocated2D {

    public Tuple2D getPosition();

    public double distance(ILocated2D other);

    public double getX();

    public double getY();
}
