package gvgai.ontology.effects.unary;

import gvgai.core.content.InteractionContent;
import gvgai.core.game.Game;
import gvgai.core.vgdl.VGDLSprite;
import gvgai.ontology.Types;
import gvgai.ontology.effects.Effect;
import gvgai.tools.Direction;
import gvgai.tools.Utils;

/**
 * Created with IntelliJ IDEA.
 * User: Diego
 * Date: 03/12/13
 * Time: 16:17
 * This is a Java port from Tom Schaul's VGDL - https://github.com/schaul/py-vgdl
 */
public class FlipDirection extends Effect
{
    public FlipDirection(InteractionContent cnt)
    {
        is_stochastic = true;
        this.parseParameters(cnt);
    }

    @Override
    public void execute(VGDLSprite sprite1, VGDLSprite sprite2, Game game)
    {
        sprite1.orientation = (Direction) Utils.choice(Types.DBASEDIRS, game.getRandomGenerator());
    }
}
