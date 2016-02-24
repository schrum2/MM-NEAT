/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.ut2004.sensors.blocks;

import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;

/**
 *
 * @author Jacob Schrum
 */
public class SelfAwarenessBlock implements UT2004SensorBlock {

    public void prepareBlock(UT2004BotModuleController bot) {
    }

    public int incorporateSensors(UT2004BotModuleController bot, int in, double[] inputs) {

        inputs[in++] = bot.getInfo().getArmor() / 100.0;
        inputs[in++] = bot.getInfo().getHealth() / 100.0;
        //inputs[in++] = bot.getInfo().isHealthy() ? 1 : 0;
        inputs[in++] = bot.getInfo().isTouchingGround() ? 1 : 0;
        inputs[in++] = bot.getSenses().isBeingDamaged() ? 1 : 0;
        inputs[in++] = bot.getSenses().isBumping() ? 1 : 0;
        //inputs[in++] = bot.getSenses().isBumpingPlayer() ? 1 : 0;
        inputs[in++] = bot.getSenses().isCausingDamage() ? 1 : 0;
        inputs[in++] = bot.getSenses().isColliding() ? 1 : 0;
        inputs[in++] = bot.getSenses().isFallEdge() ? 1 : 0;
        //inputs[in++] = bot.getSenses().isShot() ? 1 : 0;

        return in;
    }

    public int incorporateLabels(int in, String[] labels) {
        labels[in++] = "Armor";
        labels[in++] = "Health";
        //labels[in++] = "Healthy?";
        labels[in++] = "Touching Ground?";
        labels[in++] = "Being Damaged?";
        labels[in++] = "Bumping?";
        //labels[in++] = "Bumping Player?";
        labels[in++] = "Causing Damage?";
        labels[in++] = "Colliding?";
        labels[in++] = "Fall Edge?";
        //labels[in++] = "Shot?";

        return in;
    }

    public int numberOfSensors() {
        return 8;
    }
}
