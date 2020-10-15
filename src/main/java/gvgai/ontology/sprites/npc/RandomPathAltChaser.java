package gvgai.ontology.sprites.npc;

import gvgai.core.content.SpriteContent;
import gvgai.core.game.Game;
import gvgai.core.vgdl.VGDLSprite;
import gvgai.ontology.Types;
import gvgai.tools.Direction;
import gvgai.tools.Utils;
import gvgai.tools.Vector2d;

import java.awt.*;

/**
 * Created by Diego on 24/02/14.
 */
public class RandomPathAltChaser extends PathAltChaser{

    public double epsilon;

    public RandomPathAltChaser(){}

    public RandomPathAltChaser(Vector2d position, Dimension size, SpriteContent cnt)
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
        epsilon = 0.0;
    }

    public void postProcess()
    {
        super.postProcess();
    }

    public void update(Game game)
    {
        double roll = game.getRandomGenerator().nextDouble();
        if(roll < epsilon)
        {
            //do a sampleRandom move.
            super.updatePassive();
            Direction act = (Direction) Utils.choice(Types.DBASEDIRS, game.getRandomGenerator());
            this.physics.activeMovement(this, act, this.speed);
        }else
        {
            super.update(game);
        }
    }

    public VGDLSprite copy()
    {
        RandomPathAltChaser newSprite = new RandomPathAltChaser();
        this.copyTo(newSprite);
        return newSprite;
    }

    public void copyTo(VGDLSprite target)
    {
        RandomPathAltChaser targetSprite = (RandomPathAltChaser) target;
        targetSprite.epsilon = this.epsilon;
        super.copyTo(targetSprite);
    }

}
