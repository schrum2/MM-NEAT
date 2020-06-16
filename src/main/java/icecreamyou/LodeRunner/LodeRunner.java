package icecreamyou.LodeRunner;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;

/**
 * KNOWN BUGS
 * - Due to a collision model where players are considered "on" a surface if they are touching it,
 *   players might sometimes be unable to fall into a hole that is just one unit wide.
 * - The player can dig even when there is something else on top of the hole.
 * - Enemies should not be able to occupy the same hole (enemies in holes should act like solids).
 * - On death, subtract gold points gained on that level so far.
 */

/**
 * FUTURE FEATURES
 * - Save high scores
 * - Allow just "save" in addition to "save as"
 * - Allow deleting custom levels
 * - Allow two-player
 * - Allow switching themes
 * - Make enemies smarter (may involve multiple enemy classes)
 * - Start nextLevel on a timer
 */

/**
 * The controlling class for a new Lode Runner game.
 */
public class LodeRunner {

	public static final String FILE_PATH = "src/main/java/icecreamyou/LodeRunner/";
	private String levelName = "CAMPAIGN-1";
	public static final int INITIAL_LIVES = 3;

	// Score counters
	final ScoreLabel score = new ScoreLabel("Gold: 0", "Gold", 0);
	final ScoreLabel lives = new ScoreLabel("Lives: "+ INITIAL_LIVES, "Lives", INITIAL_LIVES);
	// Editor
	final JPanel editor = new JPanel();
	// Menu
	private final JPanel menu = new JPanel();
	final JLabel status = new JLabel(levelName);
	final JButton reset = new JButton("Play");
	final JButton createNew = new JButton("Create new level");
	final JButton edit = new JButton("Edit");
	final JButton openNew = new JButton("Open level");
	final JButton playGAN = new JButton("Play Level Now"); //added when editing a GAN Level and then removed when now editing
	// Top-level frame
	final JFrame frame = new JFrame("Lode Runner");

	public LodeRunner() {
		// Retrieve instructions.
		final String instructionText = getInstructions();

		// Top-level frame
		frame.setLocation(200, 150);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Initialize the save dialog
		final LevelSaveDialog saveDialog = new LevelSaveDialog(frame);
		saveDialog.pack();

		// Initialize level
		Level level = new Level(levelName);

		// Main playing area
		final GamePanel gamePanel = new GamePanel(level, this);
		gamePanel.addMouseListener(new MouseInputAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (gamePanel.isEditing()) {
					String key = gamePanel.getEditorKey();
					if (key != null && !key.equals("")) {
						gamePanel.addNode(
								key,
								GamePanel.getXUnitPosition(e.getX()),
								GamePanel.getYUnitPosition(e.getY())
								);
						openNew.setEnabled(false);
					}
				}
			}
		});
		frame.add(gamePanel, BorderLayout.CENTER);


		//Editor options.
		frame.add(editor, BorderLayout.EAST);
		editor.setLayout(new GridLayout(0, 2));
		addEditorButton(Bar.TITLE, 		 Bar.NAME,		 Bar.DEFAULT_IMAGE_PATH,	   editor, gamePanel);
		addEditorButton(Coin.TITLE, 	 Coin.NAME,		 Coin.DEFAULT_IMAGE_PATH,	   editor, gamePanel);
		addEditorButton(Gate.TITLE, 	 Gate.NAME,		 Gate.DEFAULT_IMAGE_PATH,	   editor, gamePanel);
		addEditorButton(GateKey.TITLE,   GateKey.NAME,	 GateKey.DEFAULT_IMAGE_PATH,   editor, gamePanel);
		addEditorButton(Diggable.TITLE,	 Diggable.NAME,	 Diggable.DEFAULT_IMAGE_PATH,  editor, gamePanel);
		addEditorButton(Hole.TITLE, 	 Hole.NAME,		 Hole.DEFAULT_IMAGE_PATH,	   editor, gamePanel);
		addEditorButton(Ladder.TITLE, 	 Ladder.NAME,	 Ladder.DEFAULT_IMAGE_PATH,	   editor, gamePanel);
		addEditorButton(Enemy.TITLE, 	 Enemy.NAME,	 Enemy.DEFAULT_IMAGE_PATH,	   editor, gamePanel);
		addEditorButton(Player.TITLE, 	 Player.NAME,	 Player.DEFAULT_IMAGE_PATH,	   editor, gamePanel);
		addEditorButton(Portal.TITLE, 	 Portal.NAME,	 Portal.DEFAULT_IMAGE_PATH,	   editor, gamePanel);
		addEditorButton(PortalKey.TITLE, PortalKey.NAME, PortalKey.DEFAULT_IMAGE_PATH, editor, gamePanel);
		addEditorButton(Slippery.TITLE,	 Slippery.NAME,	 Slippery.DEFAULT_IMAGE_PATH,  editor, gamePanel);
		addEditorButton(Solid.TITLE, 	 Solid.NAME,	 Solid.DEFAULT_IMAGE_PATH,	   editor, gamePanel);
		addEditorButton(Spikes.TITLE, 	 Spikes.NAME,	 Spikes.DEFAULT_IMAGE_PATH,	   editor, gamePanel);
		addEditorButton(Treasure.TITLE,  Treasure.NAME,	 Treasure.DEFAULT_IMAGE_PATH,  editor, gamePanel);
		addEditorButton("Erase",		 "erase",		 "eraser.png",				   editor, gamePanel);
		editor.setEnabled(false);
		for (Component c : editor.getComponents())
			c.setEnabled(false);


		// Menu
		frame.add(menu, BorderLayout.NORTH);
		menu.add(status);
		final JButton instructions = new JButton("Instructions");
		instructions.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(
						frame,
						instructionText,
						"Instructions",
						JOptionPane.PLAIN_MESSAGE
						);
			}
		});
		reset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gamePanel.reset();
				String text = reset.getText();
				if (text.equals("Play")) {
					reset.setText("Reset");
					edit.setEnabled(false);
					createNew.setEnabled(false);
					openNew.setEnabled(false);
				}
				else if (text.equals("Reset")) {
					stopPlaying();
					lives.subtractValue(1);
					score.resetValue();
				}
			}
		});
		edit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String text = edit.getText();
				if (text.equals("Edit")) {
					gamePanel.useEditor();
					editor.setEnabled(true);
					for (Component c : editor.getComponents())
						c.setEnabled(true);
					reset.setText("Reset");
					edit.setText("Save");
					status.setText("Editing "+ levelName);
					createNew.setText("Cancel");
					createNew.setEnabled(true);
					openNew.setEnabled(false);
				}
				else if (text.equals("Save")) {
					if (!gamePanel.playerOneExists()) {
						JOptionPane.showMessageDialog(frame, "You cannot save a level that has no player in it!");
						return;
					}
					saveDialog.setLocationRelativeTo(frame);
					saveDialog.setVisible(true);
					String result = saveDialog.getResult();
					if (result != null) {
						gamePanel.save(result);
						gamePanel.stopUsingEditor();
						editor.setEnabled(false);
						for (Component c : editor.getComponents())
							c.setEnabled(false);
						reset.setText("Play");
						reset.setEnabled(true);
						edit.setText("Edit");
						levelName = result;
						status.setText(levelName);
						createNew.setText("Create new level");
						createNew.setEnabled(true);
						openNew.setEnabled(true);
					}
				}
			}
		});
		createNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String text = createNew.getText();
				if (text.equals("Create new level")) {
					gamePanel.switchLevel();
					gamePanel.useEditor();
					editor.setEnabled(true);
					for (Component c : editor.getComponents())
						c.setEnabled(true);
					edit.setText("Save");
					status.setText("New level");
					reset.setText("Reset");
					reset.setEnabled(false);
					createNew.setText("Cancel");
					createNew.setEnabled(false);
					score.resetValue();
					lives.resetValue();
					openNew.setEnabled(false);
				}
				else if (text.equals("Cancel")) {
					gamePanel.reset();
					gamePanel.stopUsingEditor();
					editor.setEnabled(false);
					for (Component c : editor.getComponents())
						c.setEnabled(false);
					edit.setText("Edit");
					status.setText(levelName);
					createNew.setText("Create new level");
					reset.setText("Play");
					openNew.setEnabled(true);
				}
			}
		});
		openNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Object[] levels = getLevels();
				String result = (String)JOptionPane.showInputDialog(
						frame,
						"Choose a level to open.",
						"Open level",
						JOptionPane.PLAIN_MESSAGE,
						null,
						levels,
						levels[0]);
				if (result != null && result.length() > 0) {
					if (result.equals("CAMPAIGN")) {
						gamePanel.startCampaign();
						status.setText("CAMPAIGN");
					}
					else {
						gamePanel.switchLevel(result);
						levelName = result;
						status.setText(levelName);
					}
				}
			}
		});
		menu.add(instructions);
		menu.add(reset);
		menu.add(edit);
		menu.add(createNew);
		menu.add(openNew);
		menu.add(score);
		menu.add(lives);


		// Put the frame on the screen
		frame.pack();
		frame.setVisible(true);
	}

	/**
	 * Constructor for GAN levels that doesnt save file
	 * @param level1
	 */
	public LodeRunner(List<List<Integer>> level1) {
		// Retrieve instructions.
		final String instructionText = getInstructions();

		// Top-level frame
		frame.setLocation(200, 150);
		//frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Initialize the save dialog
		final LevelSaveDialog saveDialog = new LevelSaveDialog(frame);
		saveDialog.pack();

		// Initialize level
		Level level = new Level(level1);
		levelName = "Level From GAN";
		status.setText(levelName);

		// Main playing area
		final GamePanel gamePanel = new GamePanel(level, this);
		gamePanel.addMouseListener(new MouseInputAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (gamePanel.isEditing()) {
					String key = gamePanel.getEditorKey();
					if (key != null && !key.equals("")) {
						gamePanel.addNode(
								key,
								GamePanel.getXUnitPosition(e.getX()),
								GamePanel.getYUnitPosition(e.getY())
								);
						openNew.setEnabled(false);
					}
				}
			}
		});
		frame.add(gamePanel, BorderLayout.CENTER);


		//Editor options.
		frame.add(editor, BorderLayout.EAST);
		editor.setLayout(new GridLayout(0, 2));
		addEditorButton(Bar.TITLE, 		 Bar.NAME,		 Bar.DEFAULT_IMAGE_PATH,	   editor, gamePanel);
		addEditorButton(Coin.TITLE, 	 Coin.NAME,		 Coin.DEFAULT_IMAGE_PATH,	   editor, gamePanel);
		addEditorButton(Gate.TITLE, 	 Gate.NAME,		 Gate.DEFAULT_IMAGE_PATH,	   editor, gamePanel);
		addEditorButton(GateKey.TITLE,   GateKey.NAME,	 GateKey.DEFAULT_IMAGE_PATH,   editor, gamePanel);
		addEditorButton(Diggable.TITLE,	 Diggable.NAME,	 Diggable.DEFAULT_IMAGE_PATH,  editor, gamePanel);
		addEditorButton(Hole.TITLE, 	 Hole.NAME,		 Hole.DEFAULT_IMAGE_PATH,	   editor, gamePanel);
		addEditorButton(Ladder.TITLE, 	 Ladder.NAME,	 Ladder.DEFAULT_IMAGE_PATH,	   editor, gamePanel);
		addEditorButton(Enemy.TITLE, 	 Enemy.NAME,	 Enemy.DEFAULT_IMAGE_PATH,	   editor, gamePanel);
		addEditorButton(Player.TITLE, 	 Player.NAME,	 Player.DEFAULT_IMAGE_PATH,	   editor, gamePanel);
		addEditorButton(Portal.TITLE, 	 Portal.NAME,	 Portal.DEFAULT_IMAGE_PATH,	   editor, gamePanel);
		addEditorButton(PortalKey.TITLE, PortalKey.NAME, PortalKey.DEFAULT_IMAGE_PATH, editor, gamePanel);
		addEditorButton(Slippery.TITLE,	 Slippery.NAME,	 Slippery.DEFAULT_IMAGE_PATH,  editor, gamePanel);
		addEditorButton(Solid.TITLE, 	 Solid.NAME,	 Solid.DEFAULT_IMAGE_PATH,	   editor, gamePanel);
		addEditorButton(Spikes.TITLE, 	 Spikes.NAME,	 Spikes.DEFAULT_IMAGE_PATH,	   editor, gamePanel);
		addEditorButton(Treasure.TITLE,  Treasure.NAME,	 Treasure.DEFAULT_IMAGE_PATH,  editor, gamePanel);
		addEditorButton("Erase",		 "erase",		 "eraser.png",				   editor, gamePanel);
		editor.setEnabled(false);
		for (Component c : editor.getComponents())
			c.setEnabled(false);


		// Menu
		frame.add(menu, BorderLayout.NORTH);
		menu.add(status);
		final JButton instructions = new JButton("Instructions");
		instructions.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(
						frame,
						instructionText,
						"Instructions",
						JOptionPane.PLAIN_MESSAGE
						);
			}
		});
		reset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GamePanel.mode = Mode.GAN;
				gamePanel.reset();
				String text = reset.getText();
				if (text.equals("Play")) {
					reset.setText("Reset");
					edit.setEnabled(false);
					createNew.setEnabled(false);
					openNew.setEnabled(false);
				}
				else if (text.equals("Reset")) {
					stopPlaying();
					lives.subtractValue(1);
					score.resetValue();
				}
			}
		});
		edit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String text = edit.getText();
				if (text.equals("Edit")) {
					gamePanel.useEditor();
					editor.setEnabled(true);
					for (Component c : editor.getComponents())
						c.setEnabled(true);
					reset.setText("Reset");
					edit.setText("Save");
					status.setText("Editing "+ levelName);
					createNew.setText("Cancel");
					createNew.setEnabled(true);
					playGAN.setEnabled(true);
					openNew.setEnabled(false);
				}
				else if (text.equals("Save")) {
					if (!gamePanel.playerOneExists()) {
						JOptionPane.showMessageDialog(frame, "You cannot save a level that has no player in it!");
						return;
					}
					saveDialog.setLocationRelativeTo(frame);
					saveDialog.setVisible(true);
					String result = saveDialog.getResult();
					if (result != null) {
						gamePanel.save(result);
						gamePanel.stopUsingEditor();
						editor.setEnabled(false);
						for (Component c : editor.getComponents())
							c.setEnabled(false);
						reset.setText("Play");
						reset.setEnabled(true);
						edit.setText("Edit");
						levelName = result;
						status.setText(levelName);
						createNew.setText("Create new level");
						createNew.setEnabled(true);
						playGAN.setEnabled(false);
						openNew.setEnabled(true);
					}
				}
			}
		});
		createNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String text = createNew.getText();
				if (text.equals("Create new level")) {
					gamePanel.switchLevel();
					gamePanel.useEditor();
					editor.setEnabled(true);
					for (Component c : editor.getComponents())
						c.setEnabled(true);
					edit.setText("Save");
					status.setText("New level");
					reset.setText("Reset");
					reset.setEnabled(false);
					createNew.setText("Cancel");
					createNew.setEnabled(false);
					score.resetValue();
					lives.resetValue();
					openNew.setEnabled(false);
				}
				else if (text.equals("Cancel")) {
					gamePanel.reset();
					gamePanel.stopUsingEditor();
					editor.setEnabled(false);
					for (Component c : editor.getComponents())
						c.setEnabled(false);
					edit.setText("Edit");
					status.setText(levelName);
					createNew.setText("Create new level");
					reset.setText("Play");
					openNew.setEnabled(true);
				}
			}
		});
		openNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Object[] levels = getLevels();
				String result = (String)JOptionPane.showInputDialog(
						frame,
						"Choose a level to open.",
						"Open level",
						JOptionPane.PLAIN_MESSAGE,
						null,
						levels,
						levels[0]);
				if (result != null && result.length() > 0 && openNew.getText()=="Open level") {
					if (result.equals("CAMPAIGN")) {
						gamePanel.startCampaign();
						status.setText("CAMPAIGN");
					}
					else {
						gamePanel.switchLevel(result);
						levelName = result;
						status.setText(levelName);
					}
				}
			}

		});
		playGAN.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {	
				gamePanel.updateUI(); //updates the layout of the level to be the edited version
//				GamePanel.mode = Mode.GAN;
//				gamePanel.reset(); //calling reset always resets it to the original, so this is probably not a good way to do it 
				GamePanel.mode = Mode.MODE_PLAYING;
				status.setText(levelName); //updates name at top left to not be editing anymore 
				editor.setEnabled(false);
				for (Component c : editor.getComponents())
					c.setEnabled(false);
				reset.setText("Play");
				reset.setEnabled(true);
				createNew.setText("Create new level");
				createNew.setEnabled(true);
				edit.setText("Edit");
				edit.setEnabled(true);
				openNew.setText("Open level");
				openNew.setEnabled(true);
				playGAN.setEnabled(false);
			}

		});
		playGAN.setEnabled(false);
		menu.add(instructions);
		menu.add(reset);
		menu.add(edit);
		menu.add(createNew);
		menu.add(openNew);
		menu.add(playGAN);
		menu.add(score);
		menu.add(lives);


		// Put the frame on the screen
		frame.pack();
		frame.setVisible(true);
	}

	/**
	 * Switch the current level.
	 * Only used in GamePanel.win() if there is a nextLevel.
	 */
	void setLevelName(String name) {
		levelName = name;
	}

	/**
	 * React when the game stops, e.g. in GamePanel.lose().
	 */
	public void stopPlaying() {
		reset.setText("Play");
		edit.setEnabled(true);
		createNew.setEnabled(true);
		openNew.setEnabled(true);
	}

	/**
	 * Read in the instructions file and return it as a String.
	 */
	private static String getInstructions() {
		String instructionText = "";
		try {
			BufferedReader r = new BufferedReader(new FileReader(FILE_PATH+"Instructions.txt"));
			if (r.ready()) {
				String line = "";
				while (line != null) {
					line = r.readLine();
					if (line != null)
						instructionText += line +"\n";
				}
			}
			r.close();
			return instructionText;
		} catch (IOException e) {
			System.out.println("Error: Cannot find instructions. Message: "+ e.getMessage());
		}
		return instructionText;
	}

	/**
	 * A helper function to create an editor button with its listener and add it to the editor panel.
	 * @param caption The text to show on the button.
	 * @param key The NAME of the class that the button will create.
	 * @param imgPath A path to the image to show on the button.
	 * @param panel The editor panel to which the new button will be added.
	 * @param gamePanel The GamePanel that will keep track of the last button pressed.
	 */
	private static void addEditorButton(
			final String caption,
			final String key,
			final String imgPath,
			JPanel panel,
			final GamePanel gamePanel) {
		final JButton button = new JButton(caption, new ImageIcon(imgPath));
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				KeyColor color = KeyColor.RED;
				if (key != null && (key.equals("gate") || key.equals("gateKey"))) {
					color = (KeyColor) JOptionPane.showInputDialog(
							button,
							"Choose the color of the "+ caption,
							caption +" color",
							JOptionPane.QUESTION_MESSAGE,
							null,
							KeyColor.values(),
							color
							);
					if (color == null)
						color = KeyColor.RED;
				}
				gamePanel.setEditorKey(key, imgPath, color);
				gamePanel.grabFocus();
			}
		});
		panel.add(button);
	}

	/**
	 * Returns an array of levels that can be opened.
	 */
	static Object[] getLevels() {
		final File[] files = new File(FILE_PATH+".").listFiles();
		Set<String> levels = new HashSet<String>();
		for (File f : files) {
			if (f != null) {
				String name = f.getName();
				// We have an invariant that custom levels end in -level.txt to avoid reading Instructions.txt.
				if (name.length() > 10 && name.substring(name.length()-10).equals("-level.txt"))
					levels.add(name.substring(0, name.length()-10));
			}
		}
		levels.add("CAMPAIGN"); // Special case for the campaign.
		Object[] result = levels.toArray();
		Arrays.sort(result);
		return result;
	}

}
