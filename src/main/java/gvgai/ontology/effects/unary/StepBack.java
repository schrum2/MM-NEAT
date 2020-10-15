package gvgai.ontology.effects.unary;

import gvgai.core.content.InteractionContent;
import gvgai.core.game.Game;
import gvgai.core.vgdl.VGDLSprite;
import gvgai.ontology.Types;
import gvgai.ontology.effects.Effect;
import gvgai.tools.Direction;
import gvgai.tools.Vector2d;

import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: Diego
 * Date: 23/10/13
 * Time: 15:23
 * This is a Java port from Tom Schaul's VGDL - https://github.com/schaul/py-vgdl
 */
public class StepBack extends Effect
{
    public boolean pixelPerfect;

    public StepBack(InteractionContent cnt)
    {
        pixelPerfect = false;
        this.parseParameters(cnt);
    }

    @Override
    public void execute(VGDLSprite sprite1, VGDLSprite sprite2, Game game)
    {
        if(pixelPerfect && sprite2!=null) //Sprite2 could be Null in an EOS case.
            sprite1.setRect(calculatePixelPerfect(sprite1, sprite2));
        else
            sprite1.setRect(sprite1.lastrect);
    }

}
