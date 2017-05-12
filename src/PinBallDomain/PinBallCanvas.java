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
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * Extended JPanel that displays a PinBall domain world. 
 * This class is used by <code>PinBallGUI</code>, <code>PinBallGUIEdit</code>,
 * <code>PinBallGUIReplay</code> and <code>PinBallGuiWriter</code>.
 * <p>
 * Note: if you wish to display a trajectory showing all ball positions at the same
 * time, set <code>draw_all</code> to <code>true</code>.
 * 
 * @author George Konidaris (gdk at cs dot umass dot edu)
 */
public class PinBallCanvas extends JPanel 
{	
	/**
	 * Constructor. (no output file)
	 * 
	 * @param p	PinBall domain instance.
	 */
	public PinBallCanvas(PinBall p)
	{
		setPreferredSize(new Dimension(500, 500));
		pinball = p;
		        
        filename = null;
	}
	
	/**
	 * Constructor: including output file. 
	 * 
	 * @param p		PinBall domain output.
	 * @param fname	Filename to write domain image to (once off).
	 */
	public PinBallCanvas(PinBall p, String fname)
	{
		setPreferredSize(new Dimension(500, 500));
		pinball = p;
		filename = fname;
	}
	
	/**
	 * Pass in an ArrayList of "new" polygons.
	 * 
	 * @param p	ArrayList of new polygons.
	 */
	public void setNewPolygons(ArrayList<Polygon> p)
	{
		new_polygons = p;
	}
	
	/**
	 * Set the "current" polygon.
	 * 
	 * @param pp	the current polygon
	 */
	public void setCurrPoly(Polygon pp)
	{
		currPoly = pp;
	}
	
	/**
	 * Return the polygon "clicked on" at a point.
	 * 
	 * @param x		X coordinate
	 * @param y		Y coordinate
	 * @return		polygon index (-1 if no hits)
	 */
	public int getClickedPolygon(int x, int y)
	{
		for(int j = 0; j < polygons.size(); j++)
		{
			if(polygons.get(j).contains(x, y)) return j;
		}
		
		return -1;
	}
	
	/**
	 * Get the polygon at a particular position.
	 * 
	 * @param pos	the polygon position
	 * @return		the polygon at position <code>pos</code>
	 */
	public Polygon getPolygon(int pos)
	{
		return polygons.get(pos);
	}
	
	/**
	 * Update the internal PinBall instance. 
	 * 
	 * @param pb	the new PinBall instance
	 */
	public void updatePinBall(PinBall pb)
	{
		pinball = pb;
		createPolygons();
	}
	
	/**
	 * Create the drawable polygons. 
	 */
	protected void createPolygons()
	{
		polygons = new ArrayList<Polygon>();
		ArrayList<Obstacle> obs = pinball.getObstacles();
	    
		Rectangle r = getBounds();
		
	    for(Obstacle o : obs)
	    {
	    	PolygonObstacle p = (PolygonObstacle)o;
	    	ArrayList<Point> points = p.getPoints();
	    	
	    	int[] xp = new int[points.size()];
	    	int[] yp = new int[points.size()];
	    	
	    	for(int j = 0; j < points.size(); j++)
	    	{
	    		xp[j] = (int)(points.get(j).getX() * r.getWidth());
	    		yp[j] = (int)(points.get(j).getY() * r.getHeight());
	    	}
	    	
	    	Polygon P = new Polygon(xp, yp, points.size());
	    	polygons.add(P);
	    }
	}
	
	/**
	 * Paint the canvas: draw the obstacles, ball and target.
	 * 
	 * @param g		Graphics object.
	 */
	public void paintComponent(Graphics g) 
	{
	    super.paintComponent(g);
	    
	    if(polygons == null)
	    	createPolygons();
	    
	    Rectangle r = getBounds();
	    Graphics2D g2d = (Graphics2D)g;
		   	
	    Ball b = pinball.getBall();
	    Ellipse2D.Double e = new Ellipse2D.Double((b.getX() - b.getRadius())*r.getWidth(), 
	    										  (b.getY() - b.getRadius())*r.getHeight(),
	    										  2*b.getRadius()*r.getWidth(), 
	    										  2*b.getRadius()*r.getHeight());
	    
	    Color col = Color.blue;	    
	    
	    GradientPaint gradient_b = 
			new GradientPaint((int)((b.getX() + 3*b.getRadius())*r.getWidth()), 
							  (int)((b.getY() + 3*b.getRadius())*r.getHeight()), 
							  Color.white, 
							  (int)((b.getX() - b.getRadius())*r.getWidth()),
							  (int)((b.getY() - b.getRadius())*r.getHeight()),
							  col, true);
		
	    
	    if(!draw_all)
	    {
	    	g2d.setPaint(gradient_b);
	    	g2d.fill(e);
	    }
	    else
	    {
	    	ellipses.add(e);
	    	paints.add(gradient_b);
	    	
	    	for(int j = 0; j < ellipses.size(); j++)
	    	{
	    		g2d.setPaint(paints.get(j));
	    		g2d.fill(ellipses.get(j));
	    	}
	    }
	    
	    Target t = pinball.getTarget();
	    e = new Ellipse2D.Double((t.getX() - t.getRadius())*r.getWidth(),
	    						 (t.getY() - t.getRadius())*r.getHeight(),
	    						 2*t.getRadius()*r.getWidth(),
	    						 2*t.getRadius()*r.getHeight());	
	    
	    GradientPaint gradient_r = 
			new GradientPaint((int)((t.getX() + 3*t.getRadius())*r.getWidth()), 
							  (int)((t.getY() + 3*t.getRadius())*r.getHeight()), 
							  Color.white, 
							  (int)((t.getX() - t.getRadius())*r.getWidth()),
							  (int)((t.getY() - t.getRadius())*r.getHeight()),
							  Color.red, true);

	    	g2d.setPaint(gradient_r);
	    	g2d.fill(e);  
	    
	    g2d.setPaint(Color.black);
	    
	    for(Polygon p : polygons)
	    {
	    	g2d.setColor(Color.darkGray);
	    	g2d.fill(p);
	    	g2d.setColor(Color.black);
	    	g2d.draw(p);
	    }
	    
	    if(currPoly != null)
	    {
	    	g2d.setColor(Color.black);
	    	g2d.draw(currPoly);
	    }
	    
	    if(new_polygons != null)
	    {
	    	for(Polygon pp : new_polygons)
	    	{
	    		g2d.setColor(Color.green);
	    		g2d.fill(pp);
	    		g2d.setColor(Color.black);
	    		g2d.draw(pp);
	    	}
	    }
	    	    
	    if(filename != null)
	    {
	    	BufferedImage save = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
			
	    	String ff = filename;
	    	filename = null;
	    	
	        Graphics2D g2 = save.createGraphics();
	        paintComponent(g2);
	        g2.dispose();
	        
	        //write to file!
	        try {
	        ImageIO.write(save, "JPG", new File(ff));
	        } catch (Exception eee) {
	        	System.out.println("Yikes! Error writing frame.");
	        };
	    }
	  }
	
	/**
	 * Write the current image to a file.
	 * 
	 * @param fname 		filename
	 */
	void writeToFile(String fname)
	{
		BufferedImage save = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
		
    	String ff = fname;
    	
        Graphics2D g2 = save.createGraphics();
        paintComponent(g2);
        g2.dispose();
        
        //write to file!
        try 
        {
        	ImageIO.write(save, "JPG", new File(ff));
        } 
        catch (Exception eee) 
        {
        	System.out.println("Yikes! Error writing frame.");
        };
	}
	
	private static final long serialVersionUID = 1L;
	ArrayList<Polygon> polygons;
	ArrayList<Polygon> new_polygons = null;
	
	Polygon currPoly = null;
	boolean draw_all = false;
	
	PinBall pinball;
	String filename;
	
	ArrayList<Ellipse2D.Double> ellipses = new ArrayList<Ellipse2D.Double>();
	ArrayList<GradientPaint> paints = new ArrayList<GradientPaint>();
}
