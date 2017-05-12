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

package PinBallDomain;

import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;

/**
 * Object to represent the ball for the PinBall domain.
 * 
 * @author George Konidaris (gdk at cs dot umass dot edu)
 */
public class Ball 
{
	/**
	 * Constructs a new ball given a point and radius.
	 * X and Y velocities are set to zero.  
	 * 
	 * @param p		desired X and Y co-ordinates
	 * @param rad	desired radius
	 */
	public Ball(Point p, double rad)
	{
		x = p.getX();
		y = p.getY();
		xdot = 0;
		ydot = 0;
		radius = rad;
	}
	
	/**
	 * Obtain the ball's radius.
	 * 
	 * @return the radius
	 */
	public double getRadius() 
	{ 
		return radius; 
	}
	
	/**
	 * Obtain the ball's X co-ordinate
	 * 
	 * @return 		the ball's X co-ordinate
	 */
	public double getX() 
	{ 
		return x; 
	}
	
	/**
	 * Obtain the ball's Y co-ordinate
	 * 
	 * @return		the ball's Y co-ordinate
	 */
	public double getY() 
	{ 
		return y; 
	}
	
	/**
	 * Obtain the ball's X velocity.
	 * 
	 * @return		the ball's X velocity.
	 */
	public double getXDot() 
	{ 
		return xdot; 
	}
	
	/**
	 * Obtain the ball's Y velocity.
	 * 
	 * @return		the ball's Y velocity.
	 */
	public double getYDot() 
	{ 
		return ydot; 
	}
	
	/**
	 * Determines whether a config file line refers to the ball.
	 * 
	 * @param line		the text line from the config file
	 * @return			<code>true</code> if the line refers to a ball, <code>false</code> otherwise
	 */
	public static boolean matchTag(String line)
	{
		if(line.startsWith("ball")) return true;
		return false;
	}
	
	/**
	 * Creates a ball object from a line in a config file.
	 * The line should first have been matched, and is of format:
	 * "ball <i>radius</i>".
	 * <p>
	 * The line specifies the ball's radius, but not it's position,
	 * because it may have multiple potential starting positions.
	 * 
	 * @param line		the line
	 * @see				Ball
	 * @return			a new Ball object 
	 */
	public static Ball create(String line)
	{
		StringTokenizer toks = new StringTokenizer(line);
		toks.nextToken();
	
		double rad = Double.parseDouble(toks.nextToken());
		
		return new Ball(new Point(0, 0), rad);
	}
	
	/**
	 * Writes the ball to a config file.
	 * 
	 * @param f				file to be written to.
	 * @throws IOException
	 */
	public void write(FileWriter f) throws IOException
	{
		f.write("ball " + radius + "\n");
	}
	
	/**
	 * Moves the ball one step forward, in the direction of <code>xdot</code> and <code>ydot</code>.
	 * 
	 */
	public void step()
	{
		x += (xdot*radius/20.0);
		y += (ydot*radius/20.0);
	}
	
	/**
	 * Applies drag to the ball.
	 * 
	 */
	public void addDrag()
	{
		xdot = DRAG*xdot;
		ydot = DRAG*ydot;
	}
	
	/**
	 * Return the ball's speed.
	 * 
	 * @return 		speed
	 */
	public double getVelocity()
	{
		Point p = new Point(xdot, ydot);
		return Math.sqrt(p.dot(p));
	}
	
	/**
	 * Add a velocity impulse to the ball.
	 * 
	 * @param tox	impulse to add to <code>xdot</code>
	 * @param toy	impulse to add to <code>ydot</code>
	 */
	public void addImpulse(double tox, double toy)
	{
		xdot += (tox / 5.0);	
		ydot += (toy / 5.0);
		
		xdot = clip(xdot, -1.0, 1.0);
		ydot = clip(ydot, -1.0, 1.0);
	}
	
	/**
	 * Helper function to clip a variable.
	 * 
	 * @param d			value to clip
	 * @param low		lower bound
	 * @param high		upper bound
	 * @return
	 */
	protected double clip(double d, double low, double high)
	{
		if(d > high) d = high;
		if(d < low) d = low;
		
		return d;
	}
	
	/**
	 * Explicity set the ball's velocities.
	 * 
	 * @param dx	X velocity
	 * @param dy	Y velocity
	 */
	public void setVelocities(double dx, double dy)
	{
		xdot = dx;
		ydot = dy;
	}
	
	/**
	 * Set the ball's position.
	 * This also halts the ball (sets its X and Y velocities to zero).
	 * 
	 * @param xx		X position
	 * @param yy		Y position
	 */
	public void setPosition(double xx, double yy)
	{
		x = xx;
		y = yy;
		
		// Clear velocities.
		xdot = 0;
		ydot = 0;
	}
	
	/**
	 * Obtain the center point of the ball.
	 * 
	 * @return 	the center point
	 * @see 	Point
	 */
	public Point getCenter()
	{
		return new Point(x, y);
	}

	/**
	 * Drag coefficient
	 */
	public static final double DRAG = 0.995;
	
	double x, y;
	double xdot, ydot;
	double radius;
}
