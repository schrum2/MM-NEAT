package gvgai.ontology.sprites.producer;

import gvgai.core.content.SpriteContent;
import gvgai.core.game.Game;
import gvgai.core.vgdl.VGDLRegistry;
import gvgai.core.vgdl.VGDLSprite;
import gvgai.ontology.Types;
import gvgai.tools.Direction;
import gvgai.tools.Vector2d;

import java.awt.*;
import java.util.ArrayList;

/**
 * Spawns all stypes at once.
 */

public class SpawnPointMultiSprite extends SpriteProducer
{
    public double prob;
    public int total;
    public int counter;
    public Direction spawnorientation;

    private int start;

    public String stypes;
    private ArrayList<Integer> itypes;

    public SpawnPointMultiSprite(){}

    public SpawnPointMultiSprite(Vector2d position, Dimension size, SpriteContent cnt)
    {
        //Init the sprite
        this.init(position, size);

        //Specific class default parameter values.
        loadDefaults();

        //Parse the arguments.
        this.parseParameters(cnt);

        int notItypesArray[] = VGDLRegistry.GetInstance().explode(stypes);
        itypes = new ArrayList<>();
        for(Integer it : notItypesArray)
            itypes.add(it);
    }

    protected void loadDefaults()
    {
        super.loadDefaults();
        prob = 1.0;
        total = 0;
        start = -1;
        color = Types.BLACK;
        cooldown = 1;
        is_static = true;
        spawnorientation = Types.DNONE;
        is_oriented = true;
        orientation = Types.DRIGHT.copy();
        is_npc = true;
    }


    public void postProcess()
    {
        super.postProcess();
        is_stochastic = (prob > 0 && prob < 1);
        counter = 0;
    }

    public void update(Game game)
    {   if(start == -1)
            start = game.getGameTick();

        float rollDie = game.getRandomGenerator().nextFloat();
        if(((start+game.getGameTick()) % cooldown == 0) && rollDie < prob)
        {
            for (int itype: itypes) {
                VGDLSprite newSprite = game.addSprite(itype, this.getPosition());
                if (newSprite != null) {
                    counter++;

                    //We set the orientation given by default it this was passed.
                    if (!(spawnorientation.equals(Types.DNONE)))
                        newSprite.orientation = spawnorientation.copy();
                        //If no orientation given, we set the one from the spawner.
                    else if (newSprite.orientation.equals(Types.DNONE))
                        newSprite.orientation = this.orientation.copy();
                }
            }
        }

        super.update(game);

        if(total > 0 && counter >= total)
        {
            //boolean variable set to false to indicate the sprite was not transformed
            game.killSprite(this, false);
        }
    }

    public VGDLSprite copy()
    {
        SpawnPointMultiSprite newSprite = new SpawnPointMultiSprite();
        this.copyTo(newSprite);
        return newSprite;
    }

    public void copyTo(VGDLSprite target)
    {
        SpawnPointMultiSprite targetSprite = (SpawnPointMultiSprite) target;

        targetSprite.itypes = new ArrayList<>();
        for(Integer it : this.itypes)
            targetSprite.itypes.add(it);

        targetSprite.prob = this.prob;
        targetSprite.total = this.total;
        targetSprite.counter = this.counter;
        targetSprite.spawnorientation = this.spawnorientation.copy();
        targetSprite.start = this.start;
        
        super.copyTo(targetSprite);
    }
}
