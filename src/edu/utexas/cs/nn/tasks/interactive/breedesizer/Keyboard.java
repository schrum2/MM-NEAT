package edu.utexas.cs.nn.tasks.interactive.breedesizer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import edu.utexas.cs.nn.util.graphics.DrawingPanel;

public class Keyboard extends JFrame implements MouseListener{

	public static final double C3 = 130.81;
	public static final double CSHARP3 = 138.59;
	public static final double D3 = 146.83;
	public static final double DSHARP3 = 155.56;
	public static final double E3 = 164.81;
	public static final double F3 = 174.61;
	public static final double FSHARP3 = 185.00;
	public static final double G3 = 196.00;
	public static final double GSHARP3 = 207.65;
	public static final double A3 = 220.00;
	public static final double ASHARP3 = 233.08;
	public static final double B3 = 246.94;
	public static final double C4 = 261.63;
	public static final double CSHARP4 = 277.18;
	public static final double D4 = 293.66;
	public static final double DSHARP4 = 311.13;
	public static final double E4 = 329.63;
	public static final double F4 = 349.23;
	public static final double FSHARP4 = 369.99;
	public static final double G4 = 392.00;
	public static final double GSHARP4 = 415.30;
	public static final double A4 = 440.00;
	public static final double ASHARP4 = 466.16;
	public static final double B4 = 493.88;
	public static final double C5 = 523.25;

	public static final double[] keyboard = new double[]{C3, CSHARP3, D3, DSHARP3, E3, F3, FSHARP3, G3, GSHARP3, A3, ASHARP3, B3, C4, CSHARP4, D4, DSHARP4, E4, F4, FSHARP4, G4, GSHARP4, A4, ASHARP4, B4, C5};
	public static final String[] preNote = {"C3","D3","E3","F3","G3","A3","B3","C4","D4","E4","F4","G4","A4","B4","C5"};
	public static final String[] preExtendedNote = {"C3","C#3","D3","D#3","E3","F3","F#3","G3","G#3","A3","A#3","B3", "C4","C#4","D4","D#4","E4","F4","F#4","G4","G#4","A4","A#4","B4", "C5"};

	@SuppressWarnings("serial")
	class DrawPane extends JPanel{
		public void paintComponent(Graphics g){
			//draw on g here e.g.
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, 600, 200);
			g.setColor(Color.BLACK);
			for(int i = 0; i < 1200; i += 40) {
				g.drawLine(i, 0, i, 200);
			}
			boolean[] blackKeyPresent = new boolean[]{true,true,false,true,true,true,false};
			for(int i = 0; 30+i*40 < 1200; i++) {
				if(blackKeyPresent[i % blackKeyPresent.length])	
					g.fillRect(30 + i*40, 0, 20, 100);
			}
			
//			for(int i = 30; i < 600; i += 40) {
//				if(!(i == 110 || i == 270 || i == 400 || i == 650))
//					g.fillRect(i, 0, 20, 100);
//			}
		}
	}

	public Keyboard() {
		this(600,200,"Keyboard");

	}

	public Keyboard(int width, int height, String title) {
		super("Keyboard");
		//super(width,height,title);
		setContentPane(new DrawPane());
		//JFrame frame = new JFrame("Keyboard");
		//JPanel keyPanel = new JPanel();

		List<String> note = Arrays.asList(preNote);

		List<String> extendedNote = Arrays.asList(preExtendedNote);

		//	    for (String currentNote : note) {
		//	        JButton key = new JButton(currentNote);
		//	        key.addActionListener(this);
		//	        keyPanel.add(key);
		//	    }
		//	    
		//	    for (String currentNote : extendedNote) {
		//	        JButton key = new JButton(currentNote);
		//	        key.addActionListener(this);
		//	        keyPanel.add(key);
		//	    }
		//	    
		//	    frame.add(keyPanel);

		this.setSize(width,height);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		this.addMouseListener(this);


	}
	//	
	//	@Override
	//	public void actionPerformed(ActionEvent e) {
	//		Object source = e.getSource();
	//	    if (source instanceof JButton) {
	//	        JButton but = (JButton) source;
	//	        String note = but.getText();
	//	              
	//	    }		
	//	}

	@Override
	public void mouseClicked(MouseEvent e) {
		System.out.println(e.getPoint());

	}

	// Not used
	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	public static void main(String[] args) {
		Keyboard gui = new Keyboard();	  
		System.out.println(preNote.length);
		System.out.println(preExtendedNote.length);
	}


}
