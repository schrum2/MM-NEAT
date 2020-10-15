package gvgai.ontology.effects.unary;

import gvgai.core.content.InteractionContent;
import gvgai.core.game.Game;
import gvgai.core.vgdl.VGDLSprite;
import gvgai.ontology.effects.Effect;

/**
 * Created with IntelliJ IDEA.
 * User: Diego
 * Date: 04/11/13
 * Time: 15:57
 * This is a Java port from Tom Schaul's VGDL - https://github.com/schaul/py-vgdl
 */
public class AddHealthPointsToMax extends Effect
{
    public int value; //healthpoints added from sprite1
    public boolean killSecond = false;

    public AddHealthPointsToMax(InteractionContent cnt)
    {
        value = 1;
        this.parseParameters(cnt);
    }

    @Override
    public void execute(VGDLSprite sprite1, VGDLSprite sprite2, Game game)
    {
        applyScore = true;
        if(sprite1.healthPoints + value < sprite1.limitHealthPoints) {
            sprite1.healthPoints += value;
        } else {
            sprite1.healthPoints = sprite1.limitHealthPoints;
        }

        if (sprite1.healthPoints > sprite1.maxHealthPoints)
            sprite1.maxHealthPoints = sprite1.healthPoints;

        if(killSecond && sprite2 != null)
            //boolean variable set to false to indicate the sprite was not transformed
            game.killSprite(sprite2, false);
    }
}
