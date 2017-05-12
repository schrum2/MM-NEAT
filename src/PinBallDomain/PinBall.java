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
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.StringTokenizer;
import java.util.Random;

/**
 * Primary PinBall domain class.
 * This class can be mostly be dealt with directly, 
 * example code for an agent selecting actions randomly is in
 * <code>main</code>.
 * 
 * @author George Konidaris (gdk at cs dot umass dot edu)
 */
public class PinBall 
{
	/**
	 * Main. Example random agent.
	 * 
	 * @param args	command-line arguments: ignored
	 */
	public static void main(String args[])
	{
		PinBall p = new PinBall("../pinball.cfg");

		// Randomly pick actions until the episode ends.
		do
		{
			State s = p.getState();
			int action = rand.nextInt(p.getNumActions());
			double rew = p.step(action);
			State sprime = p.getState();
			
			double [] d1 = s.getDescriptor();
			double [] d2 = sprime.getDescriptor();
			
			// Output the (s, a, r, s) pair. 
			// (just output x and y for state, ignore xdot and ydot)
			System.out.println(d1[0] + " " + d1[1] + " " + action + " " + rew + " " + d2[0] + " " + d2[1]);
				
			s.getDescriptor();
		}
		while(!p.episodeEnd());
	}
	
	/**
	 * Constructor: load the domain from a configuration file.
	 * 
	 * @param configfile	the configuration file name.
	 */
	public PinBall(String configfile)
	{
		start_states = new ArrayList<PinBallState>();
		
		obstacles = new ArrayList<Obstacle>();
		loadFromFile(configfile);
	}
	
	/**
	 * Constructor: load the domain from a file, with a specified initial
	 * ball position.
	 * 
	 * @param configfile	configuration file name
	 * @param b				ball
	 */
	public PinBall(String configfile, Ball b)
	{
		start_states = new ArrayList<PinBallState>();
		
		obstacles = new ArrayList<Obstacle>();
		loadFromFile(configfile);
		
		ball = b;
	}
	
	/**
	 * Constructor: create an empty PinBall domain.
	 * 
	 */
	public PinBall()
	{
		// Create an empty table
		ball = new Ball(new Point(0.1, 0.1), 0.1);
		target = new Target(new Point(0.9, 0.9), 0.1);
		obstacles = new ArrayList<Obstacle>();
		
		start_states = new ArrayList<PinBallState>();
		start_states.add(getState());
	}
	
	/**
	 * Write the current domain configuration to a file.
	 * 
	 * @param configfile	the configuration input file
	 */
	public void writeToFile(String configfile)
	{
		try
		{
			FileWriter f = new FileWriter(new File(configfile));
			ball.write(f);
			target.write(f);
			
			f.write("start ");
			for(PinBallState st : start_states)
			{
				f.write(st.x + " " + st.y + " ");
			}
			f.write("\n\n");
			
			for(Obstacle o : obstacles)
			{
				o.write(f);
			}
			
			f.close();
		}
		catch(java.io.IOException e) {};
	}

	/**
	 * Load the domain from a file.
	 * 
	 * @param configfile	the file name
	 */
	public void loadFromFile(String configfile)
	{
		try
		{
			BufferedReader inputStream = new BufferedReader(new FileReader(configfile));
			String line = inputStream.readLine();
			
			while(line != null)
			{
				if(Ball.matchTag(line))
				{
					ball = Ball.create(line);
				}
				else if(Target.matchTag(line))
				{
					target = Target.create(line);
				}
				else if(PolygonObstacle.matchTag(line))
				{
					PolygonObstacle po = PolygonObstacle.create(line);
					obstacles.add(po);
				}
				else if(line.startsWith("start"))
				{
					// Read in a list of starting positions.
					StringTokenizer toks = new StringTokenizer(line);
					toks.nextToken();
					
					while(toks.hasMoreTokens())
					{
						double xx = Double.parseDouble(toks.nextToken());
						double yy = Double.parseDouble(toks.nextToken());
						
						PinBallState ss = new PinBallState(xx, yy, 0, 0, this);
						start_states.add(ss);
					}
				}
				
				line = inputStream.readLine();
			}
			
			if((ball == null) || (target == null))
			{
				System.out.println("Ball or target not loaded in " + configfile);
				System.exit(1);
			}
			
			resetBall();
		}
		catch(java.io.IOException e)
		{
			System.out.println("ERROR reading input file " + configfile + "!");
			System.exit(1);
		}	
	}
	
	/**
	 * Reset the domain state to one of the starting states. The starting state
	 * is selected randomly from those given in the configuration file.
	 */
	protected void resetBall()
	{
		int nstarts = start_states.size();
		int pos = rand.nextInt(nstarts);
			
		double xx = start_states.get(pos).x;
		double yy = start_states.get(pos).y;
		
		ball.setPosition(xx, yy);
	}
	
	/**
	 * Get a list of the obstacles.
	 * 
	 * @return	an ArrayList of Obstacles.
	 */
	public ArrayList<Obstacle> getObstacles()
	{
		return obstacles;
	}
	
	/**
	 * Get the ball object.
	 * 
	 * @return	the ball
	 */
	public Ball getBall()
	{
		return ball;
	}
	
	/**
	 * Get the target object.
	 * 
	 * @return	the target
	 */
	public Target getTarget()
	{
		return target;
	}
	
	/**
	 * Get current state.
	 * 
	 * @return	the current domain state.
	 */
	public PinBallState getState()
	{
		return new PinBallState(ball.getX(), ball.getY(), ball.getXDot(), ball.getYDot(), this);
	}
	
	/**
	 * Given an action, advance the domain one step and return a reward.
	 * 
	 * @param act	the action
	 * @return	the resulting reward
	 */
	public double step(int act)
	{	
		intercept = null;
		
		for(int j = 0; j < 20; j++)
		{
			if(j == 0)
			{
				double xc = 0.0;
				double yc = 0.0;
			
				if(act == ACC_X) xc = 1.0;
				if(act == DEC_X) xc = -1.0;
				if(act == ACC_Y) yc = 1.0;
				if(act == DEC_Y) yc = -1.0;
			
				ball.addImpulse(xc, yc);
			}
		
			ball.step();
			
			int collisions = 0;
			double dx = 0;
			double dy = 0;
		
			for(Obstacle o : obstacles)
			{
				if(o.collision(ball))
				{
					double [] d = o.collisionEffect(ball);
					dx += d[0];
					dy += d[1];
					collisions++;
					intercept = o.getIntercept();
				}
			}
		
			if(collisions == 1)
			{
				ball.setVelocities(dx, dy);
				
				if(j == 19)
				{
					ball.step();
				}
			}
			else if(collisions > 1)
			{
				ball.setVelocities(-ball.getXDot(), -ball.getYDot());
			}
				
			if(episodeEnd()) return END_EPISODE;
		}

		ball.addDrag();
		
		checkBounds();
		
		if(act == NONE) return STEP_PENALTY;
		return THRUST_PENALTY;
	}
	
	/**
	 * Make sure the ball doesn't go out of bounds.
	 */
	protected void checkBounds()
	{
		// Just in case there's a collision failure, make sure
		// it's not catastrophic.
		Point p = ball.getCenter();
		
		if(p.getX() > 1.0)
		{
			ball.setPosition(0.95, ball.getY());
		}
		else if(p.getX() < 0.0)
		{
			ball.setPosition(0.05, ball.getY());
		}
		
		p = ball.getCenter();
		
		if(p.getY() > 1.0)
		{
			ball.setPosition(ball.getX(), 0.95);
		}
		else if(p.getY() < 0.0)
		{
			ball.setPosition(ball.getX(), 0.05);
		}
	}
	
	/**
	 * Check for end of episode.
	 * 
	 * @return	<code>true</code> if the episode is over, <code>false</code> otherwise
	 */
	public boolean episodeEnd()
	{
		return target.collision(ball);
	}
	
	/**
	 * Check if the episode would be over, given a ball position.
	 * 
	 * @param b	the ball
	 * @return	<code>true</code> if the episode would be over, <code>false</code> otherwise
	 */
	public boolean episodeEnd(Ball b)
	{
		return target.collision(b);
	}
	
	/**
	 * Get the list of start positions for the ball.
	 * 
	 * @return	an ArrayList of start states.
	 */
	public ArrayList<PinBallState> getStartStates()
	{
		return start_states;
	}
	
	/**
	 * Add an obstacle to the domain.
	 * 
	 * @param o	the new obstacle
	 */
	public void addObstacle(Obstacle o)
	{
		obstacles.add(o);
	}

	/**
	 * Removes the obstacle at a given position.
	 * 
	 * @param pos	position of the obstacle to remove
	 */
	public void deleteObstacle(int pos)
	{
		obstacles.remove(pos);
	}
	
	/**
	 * Get the position of the last intercept.
	 * 
	 * @return	the Point position of the last intercept
	 */
	public Point getIntercept()
	{
		return intercept;
	}
	
	/**
	 * Get the number of actions available in the domain
	 * 
	 * @return	5
	 */
	public int getNumActions()
	{
		return 5;
	}
	
	ArrayList<Obstacle> obstacles;
	Ball ball;
	Target target;

	protected ArrayList<PinBallState> start_states;
	
	public static final int ACC_X = 0, ACC_Y = 1, DEC_X = 2, DEC_Y = 3, NONE = 4;
	public static final double STEP_PENALTY = -1;
	public static final double THRUST_PENALTY = -5;
	public static final double END_EPISODE = 10000;
	
	protected Point intercept;
	
	protected static Random rand = new Random();
}
