/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rlcommunity.environments.puddleworld;

import java.util.Vector;

/**
 *
 * @author btanner
 */
public class PuddleGen {

    public static Vector<Puddle> makePuddles() {
        Vector<Puddle> thePuddles = new Vector<Puddle>();
        thePuddles.add(new Puddle(.1, .75, .45, .75, .1));
        thePuddles.add(new Puddle(.45, .4, .45, .8, .1));
        return thePuddles;
    }
}
