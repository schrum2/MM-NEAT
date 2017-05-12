/*
 * Copyright 2009 George Konidaris
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package pinball;

/**
 * State class for PinBall.
 * 
 * @author George Konidaris (gdk at cs dot umass dot edu)
 */
public class PinBallState implements State 
{
	/**
	 * State constructor.
	 * 
	 * @param xx		X coordinate of the ball
	 * @param yy		Y coordinate of the ball
	 * @param xxdot		X velocity of the ball
	 * @param yydot		Y velocity of the ball
	 * @param p			Originating domain
	 */
	public PinBallState(double xx, double yy, double xxdot, double yydot, PinBall p)
	{
		x = xx;
		y = yy;
		xdot = xxdot;
		ydot = yydot;
		pinball = p;
	}
	
	/**
	 * Convert to an array of doubles (each value normalized).
	 * 
	 * @return  	state descriptor double array
	 */
	public double[] getDescriptor() 
	{
		double [] d = new double[4];
		d[0] = x;
		d[1] = y;
		d[2] = (xdot + 1.0)/2.0;
		d[3] = (ydot + 1.0)/2.0;
		
		return d;
	}
	
	/**
	 * Check whether or not this state is an end-of-episode state.
	 * 
	 * @return <code>true</code> if this state is an end-of-episode state, <code>false</code> otherwise
	 */
	public boolean endState()
	{
		Point center = new Point(x, y);
		Ball b = new Ball(center, pinball.getBall().getRadius());
		
		return pinball.episodeEnd(b);
	}
	
	/**
	 * Compute the Euclidean distance between this state and another.
	 * 
	 * @param s	other state
	 * @return 	distance
	 */
	public double euclideanDistance(State s)
	{
		double [] d1 = getDescriptor();
		double [] d2 = s.getDescriptor();
		
		if(d1.length != d2.length) return Double.POSITIVE_INFINITY;
		
		double d = 0.0;
		
		for(int j = 0; j < d1.length; j++)
		{
			d += (d1[j] - d2[j])*(d1[j] - d2[j]);
		}
		
		return Math.sqrt(d);
	}
	
	protected double x, y, xdot, ydot;
	protected PinBall pinball;
}
