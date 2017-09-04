/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pacman.controllers;

import java.util.EnumMap;
import pacman.game.Constants;

/**
 *
 * @author Jacob
 */
public abstract class NewGhostController extends Controller<EnumMap<Constants.GHOST, Constants.MOVE>> {

	public void reset() {
		super.threadRevive();
	}
}
