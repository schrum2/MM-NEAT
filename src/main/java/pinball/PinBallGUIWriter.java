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

/**
 * Class for loading up a PinBall domain and writing an image of it to disk.
 * Change the filenames in <code>main</code> to change the input and output
 * files.
 * 
 * @author George Konidaris (gdk at cs dot umass dot edu)
 */
public class PinBallGUIWriter extends JFrame  
{
	/**
	 * Main.
	 * 
	 * @param args		command-line arguments: ignored
	 */
	public static void main(String[] args) 
	{
		PinBallGUIWriter g = new PinBallGUIWriter("../pinball-single.cfg",  "../domain.gif");
		g.setVisible(true);
	}
	
	/**
	 * Constructor.
	 * 
	 * @param fname		domain configuration file
	 * @param outname	output image file name
	 */
	public PinBallGUIWriter(String fname, String outname)
	{
		setSize(500, 500);
        setTitle("PinBall Domain");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        Toolkit toolkit = getToolkit();
        Dimension size = toolkit.getScreenSize();
        setLocation(size.width/2 - getWidth()/2, 
		size.height/2 - getHeight()/2);
        
        pball = new PinBall(fname);
        
        canvas = new PinBallCanvas(pball, outname);
        add(canvas);
        
	}
	 
	/**
	 * Paint the main component.
	 */
	public void paint(Graphics g)
	{
		super.paint(g);
	}
	
	private static final long serialVersionUID = 1L;
	PinBall pball;
	PinBallCanvas canvas;
}
