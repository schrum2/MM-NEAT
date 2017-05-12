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

/**
 * An interface to represent obstacles.
 * 
 * @author George Konidaris (gdk at cs dot umass dot edu)
 */
public interface Obstacle 
{
	/**
	 * Return the effect of a collision with the ball.
	 * 
	 * @param b		the ball
	 * @return		changes to the ball's X and Y velocities
	 */
	public double[] collisionEffect(Ball b);
	
	/**
	 * Determine whether a collision with the ball has occurred. 
	 * 
	 * @param b		the ball
	 * @return		<code>true</code> if there is a collision, <code>false</code> otherwise
	 */
	public boolean collision(Ball b);
	
	/**
	 * Get the intercept position with the ball.
	 * This should be called <I>after</I> <code>collision</code> and <code>collisionEffect</code>.
	 * 
	 * @return		the point of collision
	 */
	public Point getIntercept();
	
	/**
	 * Determine whether a point is inside an obstacle.
	 * 
	 * @param p		the point to test
	 * @return		<code>true</code> if <code>p</code> is inside the obstacle, <code>false</code> otherwise
	 */
	public boolean inside(Point p);
	
	/**
	 * Write this object to a file.
	 * 
	 * @param f			the file
	 * @throws IOException
	 */
	public void write(FileWriter f) throws IOException;
	
	//public abstract static boolean matchesTag(String tag);
	//public abstract static Obstacle create(String configline);
}
