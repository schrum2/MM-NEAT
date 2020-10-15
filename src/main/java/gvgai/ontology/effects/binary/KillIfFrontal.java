package gvgai.ontology.effects.binary;

import gvgai.core.content.InteractionContent;
import gvgai.core.game.Game;
import gvgai.core.vgdl.VGDLSprite;
import gvgai.ontology.Types;
import gvgai.ontology.effects.Effect;
import gvgai.tools.Direction;
import gvgai.tools.Vector2d;

/**
 * Created with IntelliJ IDEA.
 * User: Diego
 * Date: 04/11/13
 * Time: 15:57
 * This is a Java port from Tom Schaul's VGDL - https://github.com/schaul/py-vgdl
 */
public class KillIfFrontal extends Effect
{

    public KillIfFrontal(InteractionContent cnt)
    {
        is_kill_effect = true;
        this.parseParameters(cnt);
    }

    @Override
    public void execute(VGDLSprite sprite1, VGDLSprite sprite2, Game game)
    {
        //Kills the sprite, only if they are going in opposite directions or sprite1 is static.
        Vector2d firstV = sprite1.lastDirection();
        Vector2d otherV = sprite2.lastDirection();

        firstV.normalise();
        otherV.normalise();

        //If the sum of the two vectors (normalized) is (0.0), directions are opposite.
        Direction sumDir = new Direction(firstV.x + otherV.x, firstV.y + otherV.y);
        Direction firstDir = new Direction(firstV.x, firstV.y);

        applyScore=false;
        if( firstDir.equals(Types.DNONE) || (sumDir.equals(Types.DNONE)))
        {
            applyScore=true;
            //boolean variable set to false to indicate the sprite was not transformed
            game.killSprite(sprite1, false);
        }

    }
}
