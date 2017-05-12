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
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Interactive PinBall GUI class. 
 * This class allows you to manually "play" a particular PinBall configuration
 * file. Edit the filename in <code>main</code> to change the config file that
 * it loads, and use the arrow keys to control the ball.
 * 
 * @author George D Konidaris
 */
public class PinBallGUI extends JFrame implements KeyListener, ActionListener 
{	
	/**
	 * Loads up a PinBall config file and allows the user to "play" it.
	 * 
	 * @param args		Command-line arguments: ignored.
	 */
	public static void main(String[] args) 
	{
		PinBallGUI g = new PinBallGUI("../pinball-single.cfg");
		g.setVisible(true);
	}
	
	/**
	 * Constructs a new PinBallGUI using a config file.
	 * 
	 * @param fname		the config file
	 */
	public PinBallGUI(String fname)
	{
		setSize(500, 500);
        setTitle("PinBall Domain");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        Toolkit toolkit = getToolkit();
        Dimension size = toolkit.getScreenSize();
        setLocation(size.width/2 - getWidth()/2, 
		size.height/2 - getHeight()/2);
        
        pball = new PinBall(fname);
        
        canvas = new PinBallCanvas(pball);
        add(canvas);
        
        addKeyListener(this);
        canvas.addKeyListener(this);
        
        timer = new Timer(50, this);
        timer.setInitialDelay(50);
        timer.setCoalesce(true);
        timer.start();
	}
	  
	 /**
	  * Required method for a KeyListener. Empty.
	  */
	 public void keyTyped(KeyEvent e) 
	 {
			
	 }

	 /**
	  * Handles key pressed events.
	  * 
	  * @param e	KeyEvent object
	  */
	 public void keyPressed(KeyEvent e) 
	 {
		if(e.getKeyCode() == KeyEvent.VK_UP) up_down = true;
		if(e.getKeyCode() == KeyEvent.VK_DOWN) down_down = true;
		if(e.getKeyCode() == KeyEvent.VK_LEFT) left_down = true;
		if(e.getKeyCode() == KeyEvent.VK_RIGHT) right_down = true;
	 }

	 /**
	  * Handles released key events.
	  *     
	  * @param e	KeyEvent object
	  */
	 public void keyReleased(KeyEvent e) 
	 {
		 if(e.getKeyCode() == KeyEvent.VK_UP) up_down = false;
		 if(e.getKeyCode() == KeyEvent.VK_DOWN) down_down = false;
		 if(e.getKeyCode() == KeyEvent.VK_LEFT) left_down = false;
		 if(e.getKeyCode() == KeyEvent.VK_RIGHT) right_down = false;
	 }
	
	 /**
	  * Handle action events.
	  * This function is called by the Timer, and updates the PinBall world.
	  * 
	  * @param 	e		ActionEvent descriptor
	  */
	 public void actionPerformed(ActionEvent e) 
	 {
	  timer.stop();
      int action = PinBall.NONE;
      
      if(up_down) action = PinBall.DEC_Y;
      if(down_down) action = PinBall.ACC_Y;
      if(right_down) action = PinBall.ACC_X;
      if(left_down) action = PinBall.DEC_X;
      
	  pball.step(action);
	  
	  if(!pball.episodeEnd())
	  {
		  timer.start();
	  }
      repaint();
	 }
	
	private static final long serialVersionUID = 1L;
	PinBall pball;
	PinBallCanvas canvas;
	Timer timer;
	boolean up_down, down_down, left_down, right_down;
}
