package gvgai.ontology.effects.unary;

import gvgai.core.content.InteractionContent;
import gvgai.core.game.Game;
import gvgai.core.vgdl.VGDLRegistry;
import gvgai.core.vgdl.VGDLSprite;
import gvgai.tools.Utils;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Diego
 * Date: 23/10/13
 * Time: 15:21
 * This is a Java port from Tom Schaul's VGDL - https://github.com/schaul/py-vgdl
 */
public class TransformToRandomChild extends TransformTo {

    public TransformToRandomChild(InteractionContent cnt)
    {
        super(cnt);
        itype = VGDLRegistry.GetInstance().getRegisteredSpriteValue(stype);
    }

    @Override
    public void execute(VGDLSprite sprite1, VGDLSprite sprite2, Game game)
    {
        ArrayList<Integer> subtypes = game.getSubTypes(itype);
        if (!subtypes.isEmpty()) {
            int[] types = new int[subtypes.size()-1];
            int j = -1;
            for (Integer i : subtypes) {
                if (i != itype) {
                    types[++j] = i;
                }
            }

            VGDLSprite newSprite = game.addSprite(Utils.choice(types, game.getRandomGenerator()), sprite1.getPosition());
            transformTo(newSprite, sprite1, sprite2, game);
        }
    }

    @Override
    public ArrayList<String> getEffectSprites(){
    	ArrayList<String> result = new ArrayList<String>();
    	if(stype!=null) result.add(stype);
    	
    	return result;
    }
}
