package gvgai.ontology.effects.unary;

import gvgai.core.content.InteractionContent;
import gvgai.core.game.Game;
import gvgai.core.vgdl.VGDLRegistry;
import gvgai.core.vgdl.VGDLSprite;
import gvgai.ontology.effects.Effect;
import gvgai.tools.Vector2d;

import java.util.ArrayList;

public class SpawnBehind extends Effect {

    public String stype;
    public int itype;

    public SpawnBehind(InteractionContent cnt)
    {
        this.parseParameters(cnt);
        itype = VGDLRegistry.GetInstance().getRegisteredSpriteValue(stype);
    }

    @Override
    public void execute(VGDLSprite sprite1, VGDLSprite sprite2, Game game)
    {
        if(game.getRandomGenerator().nextDouble() >= prob) return;
        Vector2d lastPos = sprite2.getLastPosition();
        if (lastPos != null) {
            game.addSprite(itype, lastPos);
        }
    }
    
    @Override
    public ArrayList<String> getEffectSprites(){
    	ArrayList<String> result = new ArrayList<String>();
    	if(stype!=null) result.add(stype);
    	
    	return result;
    }
}
