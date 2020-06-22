package icecreamyou.LodeRunner;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
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
	
	public static Level levelCopy;

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
		final String instructionText = 
				"Welcome to Lode Runner by Isaac Sukin. This code was further modified by\r\n" +
				"Kirby Steckel and Dr. Jacob Schrum to load levels evolved using a GAN.\r\n"+
				"\r\n" + 
				"Lode Runner is a platform game. Your job is to collect all the gold in each\r\n" + 
				"level. You automatically pick up gold when you walk over it. If you succeed in\r\n" + 
				"collecting all the gold, you beat the level.\r\n" +  
				"\r\n" + 
				"Use the A key to move left and the D key to move right. If you are in front of\r\n" + 
				"a ladder, you can use the W key to climb up it or the S key to climb down it.\r\n" + 
				"You can also step off of ladders to the left or right with A or D,\r\n" + 
				"respectively. Additionally, you can climb across bars to the left or right\r\n" + 
				"using A or D, or drop from them using S. You will not get hurt if you fall off\r\n" + 
				"platforms. \r\n" + 
				"\r\n" + 
				"Phantoms are devoted to stopping you in your quest to collect gold. They will\r\n" + 
				"kill you if they touch you. You can temporarily disable Phantoms by tricking\r\n" + 
				"them into falling into holes. Phantoms are incapacitated while they are in\r\n" + 
				"holes, and you can walk over them safely. You can dig a hole to your left or\r\n" + 
				"right by pressing Q or E, respectively. (Note that you cannot dig a hole\r\n" + 
				"directly under yourself, but you can trap yourself in holes.) Holes will\r\n" + 
				"eventually fill back in. If you are in a hole when it fills, you will die. If a\r\n" + 
				"Phantom is in a hole when it fills, it will respawn. (If you are in the way of\r\n" + 
				"where the Phantom wants to respawn, it will wait until you move.) Phantoms can\r\n" + 
				"also pick up gold coins, keeping you from finishing the level. However, they\r\n" + 
				"will drop their coins if they fall into a hole. Note that you cannot dig through steel.\r\n" + 
				"\r\n" + 
				"Additionally, you can edit the evolved level by clicking the\r\n" + 
				"\"Edit\" button on the menu at the top of the game window.\r\n" + 
				"Click an object in the panel on the right and then click in the game area to\r\n" + 
				"place it. (You can also click-and-drag to add many of the same object at once.)\r\n" + 
				"Click the Save button when you're done to save to file. If you simply want to play\r\n" + 
				"the resulting level, click \"Play Level Now\" which will let you play your modified\r\n" + 
				"level without saving.";

		// Top-level frame
		frame.setLocation(200, 150);
		//frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Initialize the save dialog
		final LevelSaveDialog saveDialog = new LevelSaveDialog(frame);
		saveDialog.pack();

		// Initialize level
		Level level = new Level(level1);
		levelCopy = new Level(level); //deep copy of the level
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
					levelCopy = new Level(level);
					reset.setText("Reset");
					edit.setEnabled(false);
				}
				else if (text.equals("Reset")) {
					stopPlaying();
					playGAN.setEnabled(false);
					lives.subtractValue(1);
					score.resetValue();
				}
			}
		});
		edit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String text = edit.getText();
				if (text.equals("Edit")) {
					GamePanel.mode = Mode.GAN;
					gamePanel.reset();
					gamePanel.useEditor();
					editor.setEnabled(true);
					for (Component c : editor.getComponents())
						c.setEnabled(true);
					reset.setText("Reset");
					reset.setEnabled(false);
					edit.setText("Save");
					status.setText("Editing "+ levelName);
					playGAN.setEnabled(true);
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
						playGAN.setEnabled(false);
					}
				}
			}
		});
		playGAN.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {	
				// Copy the edits that were made
				levelCopy = new Level(gamePanel.level);
				GamePanel.mode = Mode.GAN;
				gamePanel.reset(); 
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
