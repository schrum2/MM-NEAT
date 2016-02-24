/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.amis.pogamut.ut2004.utils;

/**
 *
 * @author Jacob Schrum
 */
public class MyUCCWrapperConf extends UCCWrapper.UCCWrapperConf {

    protected int playerPort = -1;

    public void setPlayerPort(int gamePort) {
        this.playerPort = gamePort;
    }
}
