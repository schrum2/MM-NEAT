package gvgai.ontology.effects.unary;

import gvgai.core.content.InteractionContent;
import gvgai.core.game.Game;
import gvgai.core.vgdl.VGDLRegistry;
import gvgai.core.vgdl.VGDLSprite;
import gvgai.ontology.effects.Effect;

import java.util.ArrayList;

/**
 * Created by Diego on 18/02/14.
 */
public class Spawn extends Effect {

    public String stype;
    public int itype;

    public Spawn(InteractionContent cnt)
    {
        this.parseParameters(cnt);
        itype = VGDLRegistry.GetInstance().getRegisteredSpriteValue(stype);
    }

    @Override
    public void execute(VGDLSprite sprite1, VGDLSprite sprite2, Game game)
    {
        if(game.getRandomGenerator().nextDouble() >= prob) return;
        game.addSprite(itype, sprite1.getPosition());
    }
    
    @Override
    public ArrayList<String> getEffectSprites(){
    	ArrayList<String> result = new ArrayList<String>();
    	if(stype!=null) result.add(stype);
    	
    	return result;
    }
}
