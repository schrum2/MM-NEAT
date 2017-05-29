package org.rlcommunity.environments.acrobot;

import java.util.Random;

/**
 *
 * @author btanner
 */
public class AcrobotState {

    /*STATIC CONSTANTS*/
    private final static int stateSize = 4;
    private final static int numActions = 3;
    private final static double maxTheta1 = Math.PI;
    private final static double maxTheta2 = Math.PI;
    private final static double maxTheta1Dot = 4 * Math.PI;
    private final static double maxTheta2Dot = 9 * Math.PI;
    private final static double m1 = 1.0;
    private final static double m2 = 1.0;
    private final static double l1 = 1.0;
    private final static double l2 = 1.0;
    private final static double lc1 = 0.5;
    private final static double lc2 = 0.5;
    private final static double I1 = 1.0;
    private final static double I2 = 1.0;
    private final static double g = 9.8;
    private final static double dt = 0.05;
    private final static double acrobotGoalPosition = 1.0;
    /*State Variables*/
    private double theta1, theta2, theta1Dot, theta2Dot;

    private final Random ourRandomNumber;
    private boolean randomStarts; //if true then do random starts, else, start at static position
    private double transitionNoise=0.0d;

    private int lastAction=0;

    AcrobotState(boolean randomStartStates, double transitionNoise, long randomSeed) {
        this.randomStarts=randomStartStates;
        this.transitionNoise=transitionNoise;
        if(randomSeed==0){
            ourRandomNumber=new Random();
        }else{
            ourRandomNumber=new Random(randomSeed);
        }

        //Throw away the first few bits because they depend heavily on the seed.
        ourRandomNumber.nextDouble();
        ourRandomNumber.nextDouble();
    }
    
    public void reset() {
        lastAction=0;
        if (useRandomStarts()) {
            resetRandom();
        } else {
            resetBottom();
        }

    }


    int getLastAction(){
        return lastAction;
    }
    public void update(int theAction) {
        lastAction=theAction;
        double torque = theAction - 1.0d;
        double d1;
        double d2;
        double phi_2;
        double phi_1;

        double theta2_ddot;
        double theta1_ddot;

        //torque is in [-1,1]
        //We'll make noise equal to at most +/- 1
        double theNoise=transitionNoise*2.0d*(ourRandomNumber.nextDouble()-.5d);

        torque+=theNoise;

        int count = 0;
        while (!isTerminal() && count < 4) {
            count++;

            d1 = m1 * Math.pow(lc1, 2) + m2 * (Math.pow(l1, 2) + Math.pow(lc2, 2) + 2 * l1 * lc2 * Math.cos(theta2)) + I1 + I2;
            d2 = m2 * (Math.pow(lc2, 2) + l1 * lc2 * Math.cos(theta2)) + I2;

            phi_2 = m2 * lc2 * g * Math.cos(theta1 + theta2 - Math.PI / 2.0);
            phi_1 = -(m2 * l1 * lc2 * Math.pow(theta2Dot, 2) * Math.sin(theta2) - 2 * m2 * l1 * lc2 * theta1Dot * theta2Dot * Math.sin(theta2)) + (m1 * lc1 + m2 * l1) * g * Math.cos(theta1 - Math.PI / 2.0) + phi_2;

            theta2_ddot = (torque + (d2 / d1) * phi_1 - m2 * l1 * lc2 * Math.pow(theta1Dot, 2) * Math.sin(theta2) - phi_2) / (m2 * Math.pow(lc2, 2) + I2 - Math.pow(d2, 2) / d1);
            theta1_ddot = -(d2 * theta2_ddot + phi_1) / d1;

            theta1Dot += theta1_ddot * dt;
            theta2Dot += theta2_ddot * dt;

            theta1 += theta1Dot * dt;
            theta2 += theta2Dot * dt;
        }
        if (Math.abs(theta1Dot) > maxTheta1Dot) {
            theta1Dot = Math.signum(theta1Dot) * maxTheta1Dot;
        }

        if (Math.abs(theta2Dot) > maxTheta2Dot) {
            theta2Dot = Math.signum(theta2Dot) * maxTheta2Dot;
        }
        /* Put a hard constraint on the Acrobot physics, thetas MUST be in [-PI,+PI]
         * if they reach a top then angular velocity becomes zero
         */
        if (Math.abs(theta2) > Math.PI) {
            theta2 = Math.signum(theta2) * Math.PI;
            theta2Dot = 0;
        }
        if (Math.abs(theta1) > Math.PI) {
            theta1 = Math.signum(theta1) * Math.PI;
            theta1Dot = 0;
        }
    }

    public boolean isTerminal() {
        double feet_height = -(l1 * Math.cos(theta1) + l2 * Math.cos(theta2));

        //New Code
        double firstJointEndHeight = l1 * Math.cos(theta1);
        //Second Joint height (relative to first joint)
        double secondJointEndHeight = l2 * Math.sin(Math.PI / 2 - theta1 - theta2);
        feet_height = -(firstJointEndHeight + secondJointEndHeight);
        return (feet_height > acrobotGoalPosition);
    }

    public double getMaxTheta1() {
        return maxTheta1;
    }

    public double getMaxTheta2() {
        return maxTheta2;
    }

    public double getMaxTheta1Dot() {
        return maxTheta1Dot;
    }

    public double getMaxTheta2Dot() {
        return maxTheta2Dot;
    }

    int getNumActions() {
        return numActions;
    }

    boolean useRandomStarts() {
        return randomStarts;
    }

    public double getTheta1() {
        return theta1;
    }

    public double getTheta2() {
        return theta2;
    }

    public double getTheta1Dot() {
        return theta1Dot;
    }

    public double getTheta2Dot() {
        return theta2Dot;
    }

    private void resetRandom() {
        theta1 = ourRandomNumber.nextDouble() -.5d;
        theta2 = ourRandomNumber.nextDouble() -.5d;
        theta1Dot = ourRandomNumber.nextDouble() -.5d;
        theta2Dot = ourRandomNumber.nextDouble() -.5d;
      }

    private void resetBottom() {
        theta1 = theta2 = 0.0;
        theta1Dot = theta2Dot = 0.0;
    }
}