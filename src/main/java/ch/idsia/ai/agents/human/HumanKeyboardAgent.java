package ch.idsia.ai.agents.human;

import ch.idsia.ai.agents.Agent;
import ch.idsia.mario.engine.sprites.Mario;
import ch.idsia.mario.environments.Environment;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy
 * Date: Mar 29, 2009
 * Time: 12:19:49 AM
 * Package: ch.idsia.ai.agents.ai;
 */
public class HumanKeyboardAgent extends KeyAdapter implements Agent
{
    List<boolean[]> history = new ArrayList<boolean[]>();
    private boolean[] Action = null;
    private String Name = "HumanKeyboardAgent";

    public HumanKeyboardAgent()
    {
        this.reset ();
//        RegisterableAgent.registerAgent(this);
    }

    /**
     * Initializes Action with a boolean array of the size of the number of buttons
     * or keys that are being used in the game
     */
    public void reset()
    {
        // Just check your keyboard. Especially arrow buttons and 'A' and 'S'!
        Action = new boolean[Environment.numberOfButtons];
    }

    /**
     * Given what is observed in the Environment by the agent, the boolean
     * array Action is returned which contains all the buttons that are being
     * pressed or not
     * 
     * @param observation What the agent observes from the Environment
     */
    public boolean[] getAction(Environment observation)
    {
        @SuppressWarnings("unused")
		float[] enemiesPos = observation.getEnemiesFloatPos();
        return Action;
    }

    /**
     * Returns the type of agent being used, in this case a Human 
     */
    public AGENT_TYPE getType() {        return AGENT_TYPE.HUMAN;    }

    /**
     * Returns the name of the agent
     */
    public String getName() {   return Name; }

    /**
     * Sets the current name of the agent to the new name
     * 
     * @param name New name for the agent
     */
    public void setName(String name) {        Name = name;    }


    /**
     * This method calls the toggleKey method to determine what action
     * to perform given a KeyEvent e
     * 
     * @param KeyEvent The key that the user presses
     */
    public void keyPressed (KeyEvent e)
    {
        toggleKey(e.getKeyCode(), true);
        //System.out.println("sdf");
    }

   /**
    * This method calls the toggleKey method to stop the action of
    * a given KeyEvent
    * 
    * @param KeyEvent The key that the user has now released
    */
    public void keyReleased (KeyEvent e)
    {
        toggleKey(e.getKeyCode(), false);
    }


   /**
    * Helper method that detects the key that is being pressed when
    * the user is playing Mario. If the left, right, down, 'A', or 'S' 
    * keys are being pressed, that means that isPressed is true and will
    * perform the corresponding actions of moving left, right, down, using the
    * power-up, or jumping respectively. If isPressed is false, that means that
    * the key is being released and will stop the action.
    * 
    * @param keyCode integer that represents the key being pressed or released
    * @param isPressed Determines whether 'A', 'S', left, right, or down keys were pressed
    */
    private void toggleKey(int keyCode, boolean isPressed)
    {
        switch (keyCode) {
            case KeyEvent.VK_LEFT:
                Action[Mario.KEY_LEFT] = isPressed;
                break;
            case KeyEvent.VK_RIGHT:
                Action[Mario.KEY_RIGHT] = isPressed;
                break;
            case KeyEvent.VK_DOWN:
                Action[Mario.KEY_DOWN] = isPressed;
                break;

            case KeyEvent.VK_S:
                Action[Mario.KEY_JUMP] = isPressed;
                break;
            case KeyEvent.VK_A:
                Action[Mario.KEY_SPEED] = isPressed;
                break;
        }
    }

    /**
     * Returns the ArrayList of boolean Arrays that contain the 
     * buttons that were pressed by the user.
     * 
     * @return history ArrayList of boolean Arrays with corresponding
     *  		button presses
     */
   public List<boolean[]> getHistory () {
       return history;
   }
}
