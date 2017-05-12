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

/**
 * A 2D point
 * 
 * @author George Konidaris (gdk at cs dot umass dot edu)
 */
public class Point 
{
	/**
	 * Construct a point.
	 * 
	 * @param xx	X coordinate
	 * @param yy	Y coordinate
	 */
	public Point(double xx, double yy)
	{
		x = xx;
		y = yy;
	}
	
	/**
	 * Get the point's X coordinate.
	 * 
	 * @return		X coordinate
	 */
	public double getX() 
	{ 
		return x; 
	}
	
	/**
	 * Get the point's Y coordinate.
	 * 
	 * @return		Y coordinate
	 */
	public double getY() 
	{ 
		return y; 
	}

	/**
	 * Get the distance from this point to another.
	 * 
	 * @param t		other point
	 * @return		distance from this point to <code>t</code>
	 */
	public double distanceTo(Point t)
	{
		double dd = Math.sqrt(Math.pow((t.x - x), 2) + Math.pow((t.y - y), 2));
		return dd;
	}
	
	/**
	 * Compute a new point that is the difference between this point and another.
	 * 
	 * @param b		the point to subtract
	 * @return		the new point
	 */
	public Point minus(Point b)
	{
		return new Point(this.x - b.x, this.y - b.y);
	}
	
	/**
	 * Compute the dot product of this and a given point.
	 * 
	 * @param b		the other point
	 * @return		the dot product between this point and <code>b</code>
	 */
	public double dot(Point b)
	{
		return (this.x*b.x) + (this.y*b.y);
	}
	
	/**
	 * Compute a new point that is a constant multiple of this point.
	 * 
	 * @param d		multiple
	 * @return		the new point
	 */
	public Point times(double d)
	{
		return new Point(x*d, y*d);
	}
	
	/**
	 * Compute a new point that is this point plus another.
	 * 
	 * @param b		the other point
	 * @return		the new point
	 */
	public Point add(Point b)
	{
		return new Point(x + b.x, y + b.y);
	}
	
	/**
	 * Add another point to this one.
	 * 
	 * @param b		the other point
	 * @return		this point (after addition)
	 */
	public Point addTo(Point b)
	{
		x += b.x;
		y += b.y;
		
		return this;
	}
	
	/**
	 * Return the length of the vector corresponding to this point.
	 * 
	 * @return	the size of this point
	 */
	public double size()
	{
		return Math.sqrt(this.dot(this));
	}
	
	/**
	 * Compute a normalized version of this point.
	 * 
	 * @return		the normalized point
	 */
	public Point normalize()
	{
		double nrm = Math.sqrt(this.dot(this));
		return new Point(x/nrm, y/nrm);
	}
	
	/**
	 * Compute the angle between this point and another.
	 * 
	 * @param p		the other point
	 * @return		the angle between this point and <code>p</code>
	 */
	public double angleBetween(Point p)
	{
		double res = Math.atan2(x, y) - Math.atan2(p.getX(), p.getY());
		if(res < 0) res = res + (Math.PI*2.0);
		
		return res;
	}
	
	protected double x, y;
}
