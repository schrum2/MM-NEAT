/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package oldpacman.controllers;

import java.util.EnumMap;

import oldpacman.game.Constants;

/**
 *
 * @author Jacob
 */
public abstract class NewGhostController extends Controller<EnumMap<Constants.GHOST, Constants.MOVE>> {

	public void reset() {
		super.threadRevive();
	}
}
