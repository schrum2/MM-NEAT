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
 * The goal in the PinBallDomain.
 * 
 * @author George Konidaris (gdk at cs dot umass dot edu)
 */
public class Target implements Obstacle
{
	/**
	 * Create a new target.
	 * 
	 * @param P		center
	 * @param rad	radius
	 */
	public Target(Point P, double rad)
	{
		x = P.getX();
		y = P.getY();
		radius = rad;
	}
	
	/**
	 *	Compute collision effect with the ball. 
	 *
	 * @param b		the ball
	 * @return		the effect: (0, 0) since the target is absorbing
	 */
	public double[] collisionEffect(Ball b)
	{
		double [] d = {0, 0};
		return d;
	}
	
	/**
	 * Get the center of the target.
	 * 
	 * @return	the center point
	 */
	public Point getCenter()
	{
		return new Point(x, y);
	}
	
	/**
	 *  Determine whether a collision takes place with the ball.
	 *  
	 *  @param b	the ball
	 *  @return		<code>true</code> if there has been a collision, <code>false</code> otherwise
	 */
	public boolean collision(Ball b)
	{
		if(b.getCenter().distanceTo(getCenter()) < radius)
				return true;
		return false;
	}
	
	/**
	 * Determine whether a point is inside the target.
	 * 
	 * @param p		point to check
	 * @return 		<code>true</code> if <code>p</code> lies within the target, <code>false</code> otherwise.
	 */
	public boolean inside(Point p)
	{
		if(getCenter().distanceTo(p) < radius) return true;
		return false;
	}
	
	/**
	 * Determine whether a line of the config file refers to the target.
	 * 
	 * @param line		the line of config file text
	 * @return			<code>true</code> if the line matches, <code>false</code> if it doesn't
	 */
	public static boolean matchTag(String line)
	{
		if(line.startsWith("target")) return true;
		return false;
	}
	
	/**
	 * Create a new target given a line of config file text.
	 * 
	 * @param line		the config file line
	 * @return			a newly created Target object
	 */
	public static Target create(String line)
	{
		StringTokenizer toks = new StringTokenizer(line);
		toks.nextToken();
		
		double xx = Double.parseDouble(toks.nextToken());
		double yy = Double.parseDouble(toks.nextToken());
		double rad = Double.parseDouble(toks.nextToken());
		
		return new Target(new Point(xx, yy), rad);
	}
	
	/**
	 * Write the target object to a file.
	 * 
	 * @param f		the file
	 */
	public void write(FileWriter f) throws IOException
	{
		f.write("target " + x + " " + y + " " + radius + "\n");
	}
	
	/**
	 * Get a point of intersection.
	 * 
	 * @return		<code>null</code>, always
	 */
	public Point getIntercept()
	{
		return null;
	}
	
	/**
	 * Get the target's X coordinate.
	 * 
	 * @return the X coordinate
	 */
	public double getX() 
	{ 
		return x; 
	}

	/**
	 * Get the target's Y coordinate.
	 * 
	 * @return the Y coordinate
	 */
	public double getY() 
	{
		return y; 
	}
	
	/**
	 * Get the target's radius.
	 * 
	 * @return the radius
	 */
	public double getRadius()
	{ 
		return radius; 
	}
	
	protected double x, y, radius;	
}
