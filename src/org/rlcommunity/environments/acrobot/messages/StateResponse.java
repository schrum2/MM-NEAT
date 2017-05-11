/*
Copyright 2007 Brian Tanner
http://rl-library.googlecode.com/
brian@tannerpages.com
http://brian.tannerpages.com
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package org.rlcommunity.environments.acrobot.messages;

import java.util.StringTokenizer;

import rlVizLib.messaging.AbstractResponse;
import rlVizLib.messaging.GenericMessage;
import rlVizLib.messaging.MessageUser;
import rlVizLib.messaging.MessageValueType;
import rlVizLib.messaging.NotAnRLVizMessageException;
import rlVizLib.messaging.environment.EnvMessageType;

public class StateResponse extends AbstractResponse {

    private double theta1;
    private double theta2;
    private double theta1dot;
    private double theta2dot;
    private int theAction;

    public StateResponse(int action, double theta1, double theta2, double theta1dot, double theta2dot) {
        this.theta1 = theta1;
        this.theta2 = theta2;
        this.theta1dot = theta1dot;
        this.theta2dot = theta2dot;
        this.theAction = action;
    }

    public StateResponse(String responseMessage) throws NotAnRLVizMessageException {

        GenericMessage theGenericResponse = new GenericMessage(responseMessage);

        String thePayLoadString = theGenericResponse.getPayLoad();

        StringTokenizer stateTokenizer = new StringTokenizer(thePayLoadString, ":");

        theAction = Integer.parseInt(stateTokenizer.nextToken());
        theta1 = Double.parseDouble(stateTokenizer.nextToken());
        theta2 = Double.parseDouble(stateTokenizer.nextToken());
        theta1dot = Double.parseDouble(stateTokenizer.nextToken());
        theta2dot = Double.parseDouble(stateTokenizer.nextToken());
    }

    @Override
    public String toString() {
        String theResponse = this.getClass().getName()+": not implemented ";
        return theResponse;
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

        theResponseBuffer.append(theAction);
        theResponseBuffer.append(":");
        theResponseBuffer.append(theta1);
        theResponseBuffer.append(":");
        theResponseBuffer.append(theta2);
        theResponseBuffer.append(":");
        theResponseBuffer.append(theta1dot);
        theResponseBuffer.append(":");
        theResponseBuffer.append(theta2dot);

        return theResponseBuffer.toString();
    }

    public double getTheta1() {
        return theta1;
    }

    public double getTheta2() {
        return theta2;
    }

    public double getTheta1Dot() {
        return theta1dot;
    }

    public int getAction() {
        return theAction;
    }

    public double getTheta2Dot() {
        return theta2dot;
    }
};