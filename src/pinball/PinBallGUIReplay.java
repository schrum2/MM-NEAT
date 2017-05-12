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
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.StringTokenizer;

/**
 * Replay PinBall trajectory from a trace.
 * This class can also optionally write the frames to file (useful for making a video). 
 * <p>
 * Edit the filenames in <code>main</code> to change the input domain configuration
 * file and trace file. 
 * <p>
 * Set the variable <code>output_frames</code> to <code>true</code> to save each frame
 * to a numbered image file for use in creating animations. The output file prefix is
 * a literal in <code>actionPerformed</code>.
 * <p>
 * Traces are text files, each line in format:<br>
 * <i>x</i> <i>y</i>
 * 
 * @author George Konidaris (gdk at cs dot umass dot edu)
 */
public class PinBallGUIReplay extends JFrame implements ActionListener 
{
	/**
	 * Main.
	 * 
	 * @param args		command line arguments: ignored
	 */
	public static void main(String[] args) 
	{
		PinBallGUIReplay g = new PinBallGUIReplay("../pinball-single.cfg", 
												  "../pinball_trace.dat");
		g.setVisible(true);
	}
	
	/**
	 * Constructor.
	 * 
	 * @param config		PinBall domain configuration file
	 * @param trace			trace file name
	 */
	public PinBallGUIReplay(String config, String trace)
	{
		setSize(500, 500);
        setTitle("PinBall Domain");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        Toolkit toolkit = getToolkit();
        Dimension size = toolkit.getScreenSize();
        setLocation(size.width/2 - getWidth()/2, 
		size.height/2 - getHeight()/2);
        
        pball = new PinBall(config);
        
        canvas = new PinBallCanvas(pball);
        add(canvas);
        
        try
        {
        	inputStream = new BufferedReader(new FileReader(trace));
        }
        catch(java.io.IOException e)
        {
        	System.out.println("Couldn't open trace.");
        	System.exit(1);
        }
        
        timer = new Timer(2, this);
        timer.setInitialDelay(100);
        timer.setCoalesce(true);
        timer.start();
	}
	

	/**
	 * Action listener, used by timer.
	 * Advance the position in the trace and perhaps write the frame to file.
	 */
	public void actionPerformed(ActionEvent e) 
	{
	  timer.stop();
      String line = null;
  
	  try
	  {
		  line = inputStream.readLine();
	  }
		catch(java.io.IOException ee) {};  
	  
	  if(line != null)
	  {
		  StringTokenizer toks = new StringTokenizer(line);
		  pos++;
		  
		  double x = Double.parseDouble(toks.nextToken());
		  double y = Double.parseDouble(toks.nextToken());
		   
		  pball.getBall().setPosition(x, y);
		  
		  String ss = "" + pos;
		  while(ss.length() < 4) ss = "0" + ss;
		  
		  if(output_frames)
			  canvas.writeToFile("../solution" + ss + ".jpg");
		  
		  timer.start();
	  }
	  else
	  {
		  try
		  {
			  inputStream.close();
		  }
		  catch(java.io.IOException eee) {};
	  }
	 
      repaint();
  }
	
	private static final long serialVersionUID = 1L;
	PinBall pball;
	PinBallCanvas canvas;
	Timer timer;
	
	protected final boolean output_frames = false;
	
	BufferedReader inputStream;
    int pos = 0;
}
