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
 * Generic state interface. 
 *
 * @author George Konidaris (gdk at cs dot umass dot edu)
 */
public interface State 
{
	/**
	 * Return a double array state descriptor.
	 * All values should be between 0 and 1.
	 * 
	 * @return the descriptor
	 */
	public double[] getDescriptor();
	
	/**
	 * Determine whether or not the state is an end-of-episode state.
	 * 
	 * @return <code>true</code> if the state is an end-of-episode state, <code>false</code> otherwise
	 */
	public boolean endState();
	
	/**
	 * Compute the Euclidean distance between this and another state.
	 * 
	 * @param s		the other state
	 * @return		the distance between this state and <code>s</code>
	 */
	public double euclideanDistance(State s);
}
