package boardGame;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;

/**
 * Viewer for the BoardGameTask
 * 
 * @author johnso17
 */
public class BoardGameViewer extends JFrame{

	private static final long serialVersionUID = 1L;
	
	public BoardGameViewer(BoardGame bGame){
		setSize(500, 500);
        setTitle(bGame.getName());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        Toolkit toolkit = getToolkit();
        Dimension size = toolkit.getScreenSize();
        setLocation(size.width/2 - getWidth()/2, 
		size.height/2 - getHeight()/2);
        
        //pball = pinball;
        //canvas = new PinBallCanvas(pball);
        //add(canvas);
        //canvas.setVisible(true);
	}
	
}
