package gvgai.tools.pathfinder;

import java.util.ArrayList;

import gvgai.ontology.Types;
import gvgai.tools.Direction;
import gvgai.tools.Vector2d;


/**
 * Created by dperez on 13/01/16.
 */
public class Node implements Comparable<Node> {

    public double totalCost;
    public double estimatedCost;
    public Node parent;
    public Vector2d position;
    public Vector2d comingFrom;
    public int id;

    public Node(Vector2d pos)
    {
        estimatedCost = 0.0f;
        totalCost = 1.0f;
        parent = null;
        position = pos;
        id = ((int)(position.x) * 100 + (int)(position.y));
    }

    @Override
    public int compareTo(Node n) {
        if(this.estimatedCost + this.totalCost < n.estimatedCost + n.totalCost)
            return -1;
        if(this.estimatedCost + this.totalCost > n.estimatedCost + n.totalCost)
            return 1;
        return 0;
    }

    @Override
    public boolean equals(Object o)
    {
        return this.position.equals(((Node)o).position);
    }


    public void setMoveDir(Node pre) {

        //TODO: New types of actions imply a change in this method.
        Direction action = Types.DNONE;

        if(pre.position.x < this.position.x)
            action = Types.DRIGHT;
        if(pre.position.x > this.position.x)
            action = Types.DLEFT;

        if(pre.position.y < this.position.y)
            action = Types.DDOWN;
        if(pre.position.y > this.position.y)
            action = Types.DUP;

        this.comingFrom = new Vector2d(action.x(), action.y());
    }
}
