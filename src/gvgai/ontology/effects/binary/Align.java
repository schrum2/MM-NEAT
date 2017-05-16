package gvgai.ontology.effects.binary;

import gvgai.core.content.InteractionContent;
import gvgai.core.game.Game;
import gvgai.core.logging.Logger;
import gvgai.core.logging.Message;
import gvgai.core.vgdl.VGDLSprite;
import gvgai.ontology.effects.Effect;

import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: Diego
 * Date: 04/11/13
 * Time: 15:56
 * This is a Java port from Tom Schaul's VGDL - https://github.com/schaul/py-vgdl
 */
public class Align extends Effect
{
    public boolean orient = true;

    public Align(InteractionContent cnt)
    {
        this.parseParameters(cnt);
        setStochastic();
    }

    @Override
    public void execute(VGDLSprite sprite1, VGDLSprite sprite2, Game game)
    {
        if(sprite1 == null || sprite2 == null){
            Logger.getInstance().addMessage(new Message(Message.WARNING, "Neither 1st not 2nd sprite can be EOS with Align interaction."));
            return;
        }
        if (orient) {
            sprite1.orientation = sprite2.orientation.copy();
        }
        sprite1.rect = new Rectangle(sprite2.rect.x, sprite2.rect.y,
                sprite1.rect.width, sprite1.rect.height);
    }
}
