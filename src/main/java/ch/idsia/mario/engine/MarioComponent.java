package ch.idsia.mario.engine;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.image.VolatileImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

import ch.idsia.ai.agents.Agent;
import ch.idsia.ai.agents.human.CheaterKeyboardAgent;
import ch.idsia.mario.engine.level.Level;
import ch.idsia.mario.engine.sprites.Mario;
import ch.idsia.mario.environments.Environment;
import ch.idsia.tools.EvaluationInfo;
import ch.idsia.tools.GameViewer;
import ch.idsia.tools.tcp.ServerAgent;
import edu.southwestern.parameters.Parameters;


public class MarioComponent extends JComponent implements Runnable, /*KeyListener,*/ FocusListener, Environment {
	private static final long serialVersionUID = 790878775993203817L;
	public static final int TICKS_PER_SECOND = 24;

	private boolean running = false;
	private int width, height;
	private GraphicsConfiguration graphicsConfiguration;
	private Scene scene;
	@SuppressWarnings("unused")
	private boolean focused = false;

	int frame;
	int delay;
	Thread animator;

	private int ZLevelEnemies = 1;
	private int ZLevelScene = 1;

	public void setGameViewer(GameViewer gameViewer) {
		this.gameViewer = gameViewer;
	}

	private GameViewer gameViewer = null;

	private Agent agent = null;
	private Agent agent2 = null; // Player 2
	private CheaterKeyboardAgent cheatAgent = null;

	private KeyAdapter prevHumanKeyBoardAgent;
	public Mario mario = null;
	public Mario luigi = null; // Player 2
	private LevelScene levelScene = null;

	// Added to make agent pause before starting
	public static int startDelay = 0; //5000;

	public MarioComponent(int width, int height) {
		adjustFPS();

		this.setFocusable(true);
		this.setEnabled(true);
		this.width = width;
		this.height = height;

		Dimension size = new Dimension(width, height);

		setPreferredSize(size);
		setMinimumSize(size);
		setMaximumSize(size);

		setFocusable(true);

		if (this.cheatAgent == null)
		{
			this.cheatAgent = new CheaterKeyboardAgent();
			this.addKeyListener(cheatAgent);
		}        

		GlobalOptions.registerMarioComponent(this);
	}

	public void adjustFPS() {
		int fps = GlobalOptions.FPS;
		delay = (fps > 0) ? (fps >= GlobalOptions.InfiniteFPS) ? 0 : (1000 / fps) : 100;
		//        System.out.println("Delay: " + delay);
	}

	public void paint(Graphics g) {
	}

	public void update(Graphics g) {
	}

	public void init() {
		graphicsConfiguration = getGraphicsConfiguration();
		//        if (graphicsConfiguration != null) {
		Art.init(graphicsConfiguration);
		//        }
	}

	public void start() {
		if (!running) {
			running = true;
			animator = new Thread(this, "Game Thread");
			animator.start();
		}
	}

	public void stop() {
		running = false;
	}

	public void run() {

	}

	public EvaluationInfo run1(int currentTrial, int totalNumberOfTrials) {
		running = true;
		adjustFPS();
		EvaluationInfo evaluationInfo = new EvaluationInfo();

		VolatileImage image = null;
		Graphics g = null;
		Graphics og = null;

		image = createVolatileImage(320, 240);
		g = getGraphics();
		og = image.getGraphics();

		if (!GlobalOptions.VisualizationOn) {
			String msgClick = "Vizualization is not available";
			drawString(og, msgClick, 160 - msgClick.length() * 4, 110, 1);
			drawString(og, msgClick, 160 - msgClick.length() * 4, 110, 7);
		}

		addFocusListener(this);

		// Remember the starting time
		long start = System.currentTimeMillis();
		long tm = System.currentTimeMillis();
		long tick = tm;
		int marioStatus = Mario.STATUS_RUNNING;

		mario = ((LevelScene) scene).mario;
		mario.resetCoins();
		if(agent2 != null) {
			luigi = ((LevelScene) scene).luigi;
			luigi.resetCoins();
		}
		int jumpActionsPerformed = 0; // Added for MarioGAN fitness
		int totalActionsPerfomed = 0;

		// Added to track if Mario can't or is not progressing
		float marioProgress = mario.x;
		int stepsWithoutProgress = 0;

		while (/*Thread.currentThread() == animator*/ running) {
			// Display the next frame of animation.
			//                repaint();
			scene.tick();
			if (gameViewer != null && gameViewer.getContinuousUpdatesState())
				gameViewer.tick();

			float alpha = 0;

			//            og.setColor(Color.RED);
			if (GlobalOptions.VisualizationOn) {
				og.fillRect(0, 0, 320, 240);
				scene.render(og, alpha);
			}

			if (agent instanceof ServerAgent && !((ServerAgent) agent).isAvailable()) {
				System.err.println("Agent became unavailable. Simulation Stopped");
				running = false;
				break;
			}

			boolean[] action = agent.getAction(this/*DummyEnvironment*/);
			boolean[] action2 = agent2 == null ? null : agent2.getAction(this/*DummyEnvironment*/);
			if (action != null)
			{
				// These next two loops were taken from the Games Benchmark branch of MarioGAN.
				// They provide proper tracking of jumps and actions.
				for (int i = 0; i < Environment.numberOfButtons; ++i){
					if (action[i])
					{
						++totalActionsPerfomed;
						break;
					}
				}
				for (int i = 0; i < Environment.numberOfButtons; ++i){
					if (action[i])
					{
						if(i==Mario.KEY_JUMP){
							jumpActionsPerformed++;
						}
					}
				}
			}
			else
			{
				//System.err.println("Null Action received. Skipping simulation...");
				//stop();
				mario.die();
			}


			//Apply action;
			long diff = System.currentTimeMillis() - start;
			//System.out.println(diff + " " + tm + " " + System.currentTimeMillis() + " " + startDelay);
			if(diff >= startDelay) { // Agent pauses a bit before starting
				mario.keys = action;
			}
			mario.cheatKeys = cheatAgent.getAction(null);
			if(agent2 != null) {
				luigi.keys = action2;
			}

			if (GlobalOptions.VisualizationOn) {

				String msg = "Agent: " + agent.getName();
				LevelScene.drawStringDropShadow(og, msg, 0, 7, 5);

				msg = "Selected Actions: ";
				LevelScene.drawStringDropShadow(og, msg, 0, 8, 6);

				msg = "";
				if (action != null)
				{
					for (int i = 0; i < Environment.numberOfButtons; ++i)
						msg += (action[i]) ? Scene.keysStr[i] : "      ";
				}
				else
					msg = "NULL";                    
				drawString(og, msg, 6, 78, 1);

				if (!this.hasFocus() && tick / 4 % 2 == 0) {
					String msgClick = "CLICK TO PLAY";
					//                    og.setColor(Color.YELLOW);
					//                    og.drawString(msgClick, 320 + 1, 20 + 1);
					drawString(og, msgClick, 160 - msgClick.length() * 4, 110, 1);
					drawString(og, msgClick, 160 - msgClick.length() * 4, 110, 7);
				}
				og.setColor(Color.DARK_GRAY);
				LevelScene.drawStringDropShadow(og, "FPS: ", 33, 2, 7);
				LevelScene.drawStringDropShadow(og, ((GlobalOptions.FPS > 99) ? "\\infty" : GlobalOptions.FPS.toString()), 33, 3, 7);

				msg = totalNumberOfTrials == -2 ? "" : currentTrial + "(" + ((totalNumberOfTrials == -1) ? "\\infty" : totalNumberOfTrials) + ")";

				LevelScene.drawStringDropShadow(og, "Trial:", 33, 4, 7);
				LevelScene.drawStringDropShadow(og, msg, 33, 5, 7);


				if (width != 320 || height != 240) {
					g.drawImage(image, 0, 0, 640 * 2, 480 * 2, null);
				} else {
					g.drawImage(image, 0, 0, null);
				}
			} else {
				// Win or Die without renderer!! independently.
				marioStatus = ((LevelScene) scene).mario.getStatus();
				if (marioStatus != Mario.STATUS_RUNNING)
					stop();
			}
			// Delay depending on how far we are behind.
			if (delay > 0)
				try {
					tm += delay;
					Thread.sleep(Math.max(0, tm - System.currentTimeMillis()));
				} catch (InterruptedException e) {
					break;
				}
			// Advance the frame
			frame++;

			// Jacob: Added to abort evaluations that are not progressing
			if(mario.x <= marioProgress) {
				stepsWithoutProgress++;
				// Null check on Parameters makes it easier to launch Mario from a main method that bypasses MM-NEAT
				if(Parameters.parameters != null && stepsWithoutProgress > Parameters.parameters.integerParameter("marioStuckTimeout")) {
					//System.out.println("Mario dies from timeout");
					mario.die(); // Killing mario ends the evaluation
				}
			} else {
				stepsWithoutProgress = 0;
			}
			marioProgress = mario.x;
		}
		//=========
		evaluationInfo.agentType = agent.getClass().getSimpleName();
		evaluationInfo.agentName = agent.getName();
		evaluationInfo.marioStatus = mario.getStatus();
		evaluationInfo.livesLeft = mario.lives; // TODO: Also track Luigi's lives?
		evaluationInfo.lengthOfLevelPassedPhys = mario.x;
		evaluationInfo.lengthOfLevelPassedCells = mario.mapX;
		evaluationInfo.totalLengthOfLevelCells = levelScene.level.getWidthCells();
		evaluationInfo.totalLengthOfLevelPhys = levelScene.level.getWidthPhys();
		evaluationInfo.timeSpentOnLevel = levelScene.getStartTime();
		evaluationInfo.timeLeft = levelScene.getTimeLeft();
		evaluationInfo.totalTimeGiven = levelScene.getTotalTime();
		evaluationInfo.jumpActionsPerformed = jumpActionsPerformed; // Counted during play/simulation
		evaluationInfo.numberOfGainedCoins = mario.coins; // TODO: Also track Luigi's coins?
		evaluationInfo.totalActionsPerfomed = totalActionsPerfomed; // Counted during the play/simulation process
		evaluationInfo.totalFramesPerfomed = frame;
		evaluationInfo.marioMode = mario.getMode();
		evaluationInfo.killsTotal = LevelScene.killedCreaturesTotal;
		//        evaluationInfo.Memo = "Number of attempt: " + Mario.numberOfAttempts;
		if (agent instanceof ServerAgent && mario.keys != null /*this will happen if client quits unexpectedly in case of Server mode*/)
			((ServerAgent)agent).integrateEvaluationInfo(evaluationInfo);
		return evaluationInfo;
	}

	private void drawString(Graphics g, String text, int x, int y, int c) {
		char[] ch = text.toCharArray();
		for (int i = 0; i < ch.length; i++) {
			g.drawImage(Art.font[ch[i] - 32][c], x + i * 8, y, null);
		}
	}

	public void startLevel(long seed, int difficulty, int type, int levelLength, int timeLimit) {
		scene = new LevelScene(graphicsConfiguration, this, seed, difficulty, type, levelLength, timeLimit);
		levelScene = ((LevelScene) scene);
		scene.init();

		// Comment out to write level text
		//        try {
		//			levelScene.level.saveText(new PrintStream(new FileOutputStream("TestLevel.txt")));
		//		} catch (IOException e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}

	}

	/**
	 * Added so that the level could be directly specified instead of randomly generated
	 * @param level
	 * @param levelRandSeed
	 * @param levelType
	 * @param timeLimit
	 */
	public void startLevel(Level level, long levelRandSeed, int levelType, int timeLimit) {
		scene = new LevelScene(level, graphicsConfiguration, this, levelRandSeed, levelType, timeLimit);
		levelScene = ((LevelScene) scene);
		((LevelScene) scene).init(false); // Not a random level
	}

	public void levelFailed() {
		mario.lives--;
		// TODO: Also subtract Luigi's lives?
		stop();
	}

	public void focusGained(FocusEvent arg0) {
		focused = true;
	}

	public void focusLost(FocusEvent arg0) {
		focused = false;
	}

	public void levelWon() {
		stop();
	}

	public void toTitle() {
	}

	public List<String> getTextObservation(boolean Enemies, boolean LevelMap, boolean Complete, int ZLevelMap, int ZLevelEnemies) {
		if (scene instanceof LevelScene)
			return ((LevelScene) scene).LevelSceneAroundMarioASCII(Enemies, LevelMap, Complete, ZLevelMap, ZLevelEnemies);
		else {
			return new ArrayList<String>();
		}
	}

	public String getBitmapEnemiesObservation()
	{
		if (scene instanceof LevelScene)
			return ((LevelScene) scene).bitmapEnemiesObservation(1);
		else {
			//
			return new String();
		}                
	}

	public String getBitmapLevelObservation()
	{
		if (scene instanceof LevelScene)
			return ((LevelScene) scene).bitmapLevelObservation(1);
		else {
			//
			return null;
		}
	}

	// Chaning ZLevel during the game on-the-fly;
	public byte[][] getMergedObservationZ(int zLevelScene, int zLevelEnemies) {
		if (scene instanceof LevelScene)
			return ((LevelScene) scene).mergedObservation(zLevelScene, zLevelEnemies);
		return null;
	}

	public byte[][] getLevelSceneObservationZ(int zLevelScene) {
		if (scene instanceof LevelScene)
			return ((LevelScene) scene).levelSceneObservation(zLevelScene);
		return null;
	}

	public byte[][] getEnemiesObservationZ(int zLevelEnemies) {
		if (scene instanceof LevelScene)
			return ((LevelScene) scene).enemiesObservation(zLevelEnemies);
		return null;
	}

	public int getKillsTotal() {
		return LevelScene.killedCreaturesTotal;
	}

	public int getKillsByFire() {
		return LevelScene.killedCreaturesByFireBall;
	}

	public int getKillsByStomp() {
		return LevelScene.killedCreaturesByStomp;
	}

	public int getKillsByShell() {
		return LevelScene.killedCreaturesByShell;
	}

	public byte[][] getCompleteObservation() {
		if (scene instanceof LevelScene)
			return ((LevelScene) scene).mergedObservation(this.ZLevelScene, this.ZLevelEnemies);
		return null;
	}

	public byte[][] getEnemiesObservation() {
		if (scene instanceof LevelScene)
			return ((LevelScene) scene).enemiesObservation(this.ZLevelEnemies);
		return null;
	}

	public byte[][] getLevelSceneObservation() {
		if (scene instanceof LevelScene)
			return ((LevelScene) scene).levelSceneObservation(this.ZLevelScene);
		return null;
	}

	public boolean isMarioOnGround() {
		return mario.isOnGround();
	}

	public boolean mayMarioJump() {
		return mario.mayJump();
	}

	public void setAgent(Agent agent) {
		this.agent = agent;
		if (agent instanceof KeyAdapter) {
			if (prevHumanKeyBoardAgent != null)
				this.removeKeyListener(prevHumanKeyBoardAgent);
			this.prevHumanKeyBoardAgent = (KeyAdapter) agent;
			this.addKeyListener(prevHumanKeyBoardAgent);
		}
	}

	/**
	 * Schrum: I added this to allow for setting the second "Luigi" agent.
	 * @param agent Controller for Luigi
	 */
	public void setAgent2(Agent agent) {
		this.agent2 = agent;
		if (agent2 instanceof KeyAdapter) {
			if (prevHumanKeyBoardAgent != null)
				this.removeKeyListener(prevHumanKeyBoardAgent);
			this.prevHumanKeyBoardAgent = (KeyAdapter) agent;
			this.addKeyListener(prevHumanKeyBoardAgent);
		}
	}    

	/**
	 * This seems to be used exclusively for debugging purposes.
	 * Can make Mario invincible to test out the level. It
	 * also makes Luigi invulnerable.
	 * @param invulnerable
	 */
	public void setMarioInvulnerable(boolean invulnerable) {
		Mario.isMarioInvulnerable = invulnerable;
	}

	public void setPaused(boolean paused) {
		levelScene.paused = paused;
	}

	public void setZLevelEnemies(int ZLevelEnemies) {
		this.ZLevelEnemies = ZLevelEnemies;
	}

	public void setZLevelScene(int ZLevelScene) {
		this.ZLevelScene = ZLevelScene;
	}

	public float[] getMarioFloatPos()
	{
		return new float[]{this.mario.x, this.mario.y};
	}

	public float[] getEnemiesFloatPos()
	{
		if (scene instanceof LevelScene)
			return ((LevelScene) scene).enemiesFloatPos();
		return null;
	}

	public int getMarioMode()
	{
		return mario.getMode();
	}

	public boolean isMarioCarrying()
	{
		return mario.carried != null;
	}
}