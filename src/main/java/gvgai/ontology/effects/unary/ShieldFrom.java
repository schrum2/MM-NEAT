package gvgai.ontology.effects.unary;

import gvgai.core.content.InteractionContent;
import gvgai.core.game.Game;
import gvgai.core.vgdl.VGDLRegistry;
import gvgai.core.vgdl.VGDLSprite;
import gvgai.ontology.effects.Effect;

/**
 * Created with IntelliJ IDEA.
 * User: Diego
 * Date: 23/10/13
 * Time: 15:21
 * This is a Java port from Tom Schaul's VGDL - https://github.com/schaul/py-vgdl
 */
public class ShieldFrom extends Effect {


    public String stype;
    public int istype;

    public String ftype;
    public long iftype;


    public ShieldFrom(InteractionContent cnt)
    {
        this.parseParameters(cnt);
        istype = VGDLRegistry.GetInstance().getRegisteredSpriteValue(stype);
        iftype = ftype.hashCode();
    }

    @Override
    public void execute(VGDLSprite sprite1, VGDLSprite sprite2, Game game)
    {
        game.addShield(sprite1.getType(), istype, iftype);
    }

}
