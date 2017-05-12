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
import java.util.ArrayList;

/**
 * PinBall domain GUI configuration file editor.
 * This program allows a user to edit a configuration file via a GUI interface.
 * Editor the filename in <code>main</code> to change the name of the config file
 * to edit. The program only works with polygon obstacles (which are the only types
 * currently implemented). 
 * <p>
 * The program has four modes:<br>
 * <UL>
 * <LI> MOVE (press 'm'): move polygons around by grabbing them and dragging them.
 * <LI> ADJUST (press 'a'): move individual points by grabbing and dragging them (click inside the polygon
 * very close to the point.
 * <LI> NEW (press 'n'): create new polygons. Right click is an ordinary point, left click is the final point.
 * <LI> DELETE (press 'd'): delete polygons (by clicking on them).
 * </UL>
 * <p>
 * Finally, remember to save (press Shift-S) your changes. 
 * 
 * @author George Konidaris (gdk at cs dot umass dot edu)
 */
public class PinBallGUIEdit extends JFrame implements KeyListener, ActionListener, MouseListener, MouseMotionListener 
{

	/**
	 * Main. 
	 * 
	 * @param args		command-line arguments: ignored. 
	 */
	public static void main(String[] args) 
	{
		PinBallGUIEdit g = new PinBallGUIEdit("../pinball-work.cfg");
		g.setVisible(true);
	}
	
	/**
	 * Constructor.
	 * 
	 * @param fname	config file name to load. 
	 */
	public PinBallGUIEdit(String fname)
	{
		setSize(500, 550);
        setTitle("PinBall Domain");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        Toolkit toolkit = getToolkit();
        Dimension size = toolkit.getScreenSize();
        setLocation(size.width/2 - getWidth()/2, 
		size.height/2 - getHeight()/2);
        
        pball = new PinBall(fname);
        filename = fname;
        
        canvas = new PinBallCanvas(pball);
        canvas.setNewPolygons(new_polygons);
        add(canvas, BorderLayout.NORTH);
        
        ControlPanel = new JPanel();
        ControlPanel.setLayout(new GridLayout(1, 3));
        
        add(ControlPanel, BorderLayout.SOUTH);
        
        XYOutput = new JLabel();
        ControlPanel.add(XYOutput);
        
        Output = new JLabel();
        ControlPanel.add(Output);
        
        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
        canvas.addKeyListener(this);
        
        this.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
        canvas.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
	}
	  
	 /**
	  * Handle typed keys: switch modes.
	  * 
	  * @param e		KeyEvent descriptor
	  */
	 public void keyTyped(KeyEvent e) 
	 {
		 int old_mode = mode;
		 
		 if(e.getKeyChar() == 'n')
		 {
			 mode = NEW;
			 Output.setText("NEW mode.");
		 }
		 else if(e.getKeyChar() == 'd')
		 {
			 mode = DELETE;
			 Output.setText("DELETE mode.");
		 }
		 else if(e.getKeyChar() == 'm')
		 {
			 poly_dragged = -1;
			 mode = MOVE;
			 Output.setText("MOVE mode.");
		 }
		 else if(e.getKeyChar() == 'a')
		 {
			 poly_dragged = -1;
			 point_dragged = -1;
			 mode = ADJUST;
			 Output.setText("ADJUST mode.");
		 }
		 
		 if((old_mode == NEW) && (mode != NEW))
		 {
			 // Add in new polygons to the internal pinball object.
			 for(Polygon pp : new_polygons)
			 {
				 ArrayList<Point> poly_points = new ArrayList<Point>();
				 
				 for(int j = 0; j < pp.npoints; j++)
				 {
					 Point pz = new Point((double)pp.xpoints[j] / (double)canvas.getWidth(), (double)pp.ypoints[j] / (double)canvas.getHeight());
					 poly_points.add(pz);
				 }
				 
				 PolygonObstacle po = new PolygonObstacle(poly_points);
				 pball.addObstacle(po);
			 }
			 
			 // Clear out new polygon variables
			 new_polygons.clear();
			 thisPoly = null;
			 computeCurrPoly();
			 
			 // Update and repaint
			 canvas.updatePinBall(pball);	 
			 repaint();
		 }
		 
		 // Save
		 if(e.getKeyChar() == 'S')
		 {
			 // Add new polygons into the internal pinball object. 
			 for(Polygon pp : new_polygons)
			 {
				 ArrayList<Point> poly_points = new ArrayList<Point>();
				 
				 for(int j = 0; j < pp.npoints; j++)
				 {
					 Point pz = new Point((double)pp.xpoints[j] / (double)canvas.getWidth(), (double)pp.ypoints[j] / (double)canvas.getHeight());
					 poly_points.add(pz);
				 }
				 
				 PolygonObstacle po = new PolygonObstacle(poly_points);
				 pball.addObstacle(po);
			 }
			 
			 // Clear out new polygon variables.
			 new_polygons.clear();
			 thisPoly = null;
			 computeCurrPoly();
			 
			 // Save internal pinball object
			 pball.writeToFile(filename);
			 Output.setText("Saved.");
			 
			 // Update canvas
			 canvas.updatePinBall(pball);
			 repaint();
		 }
	 }

	 /**
	  * Required method for KeyListener. Empty.
	  */
	 public void keyPressed(KeyEvent e) 
	 {
		
	 }
	
	 /**
	  * Required method for KeyListener. Empty. 
	  */
	 public void keyReleased(KeyEvent e) 
	 {
		 
	 }
	
	/**
	 * Repaint the canvas when an action is performed. 
	 */
	public void actionPerformed(ActionEvent e) 
	{
      repaint();
	}

	/**
	 * Handle mouse click events.
	 */
	public void mouseClicked(MouseEvent e)
	{
		mousex = e.getX();
		mousey = e.getY() - 24;
				
		if(mode == NEW)
		{
			if(e.getButton() == MouseEvent.BUTTON1)
			{
				// Left button.
				// Add point to polygon.
				if(thisPoly == null)
				{
					thisPoly = new Polygon();
					Output.setText("New polygon.");
				}
			
				thisPoly.addPoint(mousex, mousey);
				computeCurrPoly();
				repaint();
			}
			else
			{
				// Right or middle button.
				// Final point in polygon.
				thisPoly.addPoint(mousex, mousey);
				
				if(thisPoly.npoints > 1)
					new_polygons.add(thisPoly);
				
				thisPoly = null;
			
				Output.setText("Polygon added.");
				computeCurrPoly();
				repaint();
			}
		}
		else if(mode == DELETE)
		{
			int pos = canvas.getClickedPolygon(mousex, mousey);
			if(pos != -1)
			{
				pball.deleteObstacle(pos);
				canvas.updatePinBall(pball);
				repaint();
			}
		}
	}

	/**
	 * Required method for MouseMotionListener. Empty.
	 */
	public void mouseEntered(MouseEvent e)
	{
		
	}

	/**
	 * Required method for MouseMotionListener. Empty. 
	 */
	public void mouseExited(MouseEvent e)
	{
		
	}

	/**
	 * MousePressed event handler. 
	 */
	public void mousePressed(MouseEvent e)
	{
		if(mode == MOVE)
		{
			int pos = canvas.getClickedPolygon(mousex, mousey);
			if(pos != -1)
			{
				poly_dragged = pos;
				poly_dragged_x = mousex;
				poly_dragged_y = mousey;
			}
		}
		if(mode == ADJUST)
		{
			// First, find the relevant polygon.
			int pos = canvas.getClickedPolygon(mousex, mousey);
			
			// If there is one, find the closest point to the click.
			if(pos != -1)
			{
				Point clickPoint = new Point(mousex, mousey);
				
				Polygon p = canvas.getPolygon(pos);
				double ddist = canvas.getHeight() + canvas.getWidth();
				int mpos = -1;
				
				for(int j = 0; j < p.npoints; j++)
				{
					Point p1 = new Point(p.xpoints[j], p.ypoints[j]);
					
					double dd = p1.distanceTo(clickPoint);
					if(dd < ddist)
					{
						ddist = dd;
						mpos = j;
					}
				}
				
				// If the closest point is within a small radius, select it. 
				if((mpos != -1) && (ddist < 16))
				{
					poly_dragged = pos;
					point_dragged = mpos;
					
					poly_dragged_x = mousex;
					poly_dragged_y = mousey;
				}
			}
		}
	}

	/**
	 * Handle MouseReleased events.
	 */
	public void mouseReleased(MouseEvent e)
	{
		if(mode == MOVE)
		{
			poly_dragged = -1;
		}
		else if(mode == ADJUST)
		{
			poly_dragged = -1;
			point_dragged = -1;
		}
	}
	
	/**
	 * Handle mouse moved events.
	 */
	public void mouseMoved( MouseEvent event )
	{
		mousex = event.getX();
		mousey = event.getY() - 24;
		
		String S = "[ " + mousex + ", " + mousey + "]";
		XYOutput.setText(S);
		computeCurrPoly();
		repaint();
	}

	/**
	 * Handle mouse dragging events.
	 */
	public void mouseDragged(MouseEvent e)
	{
		mousex = e.getX();
		mousey = e.getY() - 24;
		
		if(mode == MOVE)
		{
			if(poly_dragged == -1) return;
			
			double xdiff = (double)(mousex - poly_dragged_x) / canvas.getWidth();
			double ydiff = (double)(mousey - poly_dragged_y) / canvas.getHeight();
			
			Point diff = new Point(xdiff, ydiff);
			
			PolygonObstacle po = (PolygonObstacle)pball.getObstacles().get(poly_dragged);
			for(Point pp : po.getPoints())
				pp.addTo(diff);
			
			poly_dragged_x = mousex;
			poly_dragged_y = mousey;
			
			canvas.updatePinBall(pball);
			
			repaint();
		}
		if(mode == ADJUST)
		{
			if(poly_dragged == -1) return;
			
			double xdiff = (double)(mousex - poly_dragged_x) / canvas.getWidth();
			double ydiff = (double)(mousey - poly_dragged_y) / canvas.getHeight();
			
			Point diff = new Point(xdiff, ydiff);
			
			PolygonObstacle po = (PolygonObstacle)pball.getObstacles().get(poly_dragged);
			po.getPoints().get(point_dragged).addTo(diff);
			
			poly_dragged_x = mousex;
			poly_dragged_y = mousey;
			
			canvas.updatePinBall(pball);
			
			repaint();
		}
	}
	
	/**
	 * Compute a temporary polygon for drawing on the canvas from the current
	 * new polygon variables. 
	 */
	public void computeCurrPoly()
	{
		if(thisPoly == null)
		{
			drawPoly = null;
		}
		else
		{
			if(drawPoly == null)
				drawPoly = new Polygon();
			else if(drawPoly.npoints != thisPoly.npoints + 1)
			{
				drawPoly = new Polygon();
				for(int j = 0; j < thisPoly.npoints; j++)
					drawPoly.addPoint(thisPoly.xpoints[j], thisPoly.ypoints[j]);
				
				drawPoly.addPoint(mousex, mousey);
			}
			else
			{
				drawPoly.xpoints[drawPoly.npoints - 1] = mousex;
				drawPoly.ypoints[drawPoly.npoints - 1] = mousey;
			}
		}
		
		canvas.setCurrPoly(drawPoly);
	}
	
	private static final long serialVersionUID = 1L;
	PinBall pball;
	PinBallCanvas canvas;
	JPanel ControlPanel;
	JLabel XYOutput;
	JLabel Output;
	
	ArrayList<Polygon> new_polygons = new ArrayList<Polygon>();
	Polygon thisPoly = null;
	Polygon drawPoly = null;
	
	String filename;
	
	int poly_dragged = -1;
	int point_dragged = -1;
	int poly_dragged_x = 0, poly_dragged_y = 0;
	int mousex = 0, mousey = 0;
	
	public static final int NEW = 0, MOVE = 1, DELETE = 2, ADJUST = 3;
	protected int mode = NEW;
}
