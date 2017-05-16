package gvgai.ontology.avatar.oriented;

import java.awt.Dimension;

import gvgai.core.content.SpriteContent;
import gvgai.core.game.Game;
import gvgai.core.vgdl.VGDLSprite;
import gvgai.ontology.Types;
import gvgai.ontology.avatar.MovingAvatar;
import gvgai.tools.Direction;
import gvgai.tools.Vector2d;

/**
 * Created with IntelliJ IDEA.
 * User: Diego
 * Date: 22/10/13
 * Time: 18:10
 * This is a Java port from Tom Schaul's VGDL - https://github.com/schaul/py-vgdl
 */
public class OrientedAvatar extends MovingAvatar
{
    public OrientedAvatar(){}

    public OrientedAvatar(Vector2d position, Dimension size, SpriteContent cnt)
    {
        //Init the sprite
        this.init(position, size);

        //Specific class default parameter values.
        loadDefaults();

        //Parse the arguments.
        this.parseParameters(cnt);
    }


    protected void loadDefaults()
    {
        super.loadDefaults();
        orientation = Types.DRIGHT.copy();
        draw_arrow = true;
        is_oriented = true;
        rotateInPlace = true;
    }


    /**
     * This update call is for the game tick() loop.
     * @param game current state of the game.
     */
    public void updateAvatar(Game game, boolean requestInput, boolean[] actionMask)
    {
        super.updateAvatar(game, requestInput, actionMask);

        //If the last thing the avatar did is to move (displacement), then update
        //the orientation in the direction of the move.
        if(lastMovementType == Types.MOVEMENT.MOVE)
        {
        	if (physicstype == 0){
        		Vector2d dir = lastDirection();
        		dir.normalise();
        		orientation = new Direction(dir.x, dir.y);
        	}
        }
        //Otherwise, orientation is already updated, no need to change anything.
    }


    public VGDLSprite copy()
    {
        OrientedAvatar newSprite = new OrientedAvatar();
        this.copyTo(newSprite);
        return newSprite;
    }

    public void copyTo(VGDLSprite target)
    {
        OrientedAvatar targetSprite = (OrientedAvatar) target;
        super.copyTo(targetSprite);
    }

}
