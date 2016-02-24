package org.rlcommunity.environments.cartpole.messages;

import java.util.StringTokenizer;

import rlVizLib.messaging.AbstractResponse;
import rlVizLib.messaging.GenericMessage;
import rlVizLib.messaging.MessageUser;
import rlVizLib.messaging.MessageValueType;
import rlVizLib.messaging.NotAnRLVizMessageException;
import rlVizLib.messaging.environment.EnvMessageType;

public class CartpoleTrackResponse extends AbstractResponse {

    double leftGoalPosition = 0.0;
    double rightGoalPosition = 0.0;
    double minAngle = 0.0;
    double maxAngle = 0.0;

    public CartpoleTrackResponse(double leftGoal, double rightGoal, double minTheta, double maxTheta) {
        leftGoalPosition = leftGoal;
        rightGoalPosition = rightGoal;
        minAngle = minTheta;
        maxAngle = maxTheta;
    }

    public CartpoleTrackResponse(String responseMessage) throws NotAnRLVizMessageException {

        GenericMessage theGenericResponse;
        theGenericResponse = new GenericMessage(responseMessage);


        String thePayLoadString = theGenericResponse.getPayLoad();

        StringTokenizer stateTokenizer = new StringTokenizer(thePayLoadString, ":");
        leftGoalPosition = Double.parseDouble(stateTokenizer.nextToken());
        rightGoalPosition = Double.parseDouble(stateTokenizer.nextToken());
        minAngle = Double.parseDouble(stateTokenizer.nextToken());
        maxAngle = Double.parseDouble(stateTokenizer.nextToken());

    }

    @Override
    public String makeStringResponse() {

        StringBuffer theResponseBuffer = new StringBuffer();
        theResponseBuffer.append("TO=");
        theResponseBuffer.append(MessageUser.kBenchmark.id());
        theResponseBuffer.append(" FROM=");
        theResponseBuffer.append(MessageUser.kEnv.id());
        theResponseBuffer.append(" CMD=");
        theResponseBuffer.append(EnvMessageType.kEnvResponse.id());
        theResponseBuffer.append(" VALTYPE=");
        theResponseBuffer.append(MessageValueType.kStringList.id());
        theResponseBuffer.append(" VALS=");

        theResponseBuffer.append(leftGoalPosition);
        theResponseBuffer.append(":");
        theResponseBuffer.append(rightGoalPosition);
        theResponseBuffer.append(":");
        theResponseBuffer.append(minAngle);
        theResponseBuffer.append(":");
        theResponseBuffer.append(maxAngle);

        return theResponseBuffer.toString();
    }

    public double getLeftGoal() {
        return leftGoalPosition;
    }

    public double getRightGoal() {
        return rightGoalPosition;
    }

    public double getMinAngle() {
        return minAngle;
    }

    public double getMaxAngle() {
        return maxAngle;
    }
}
