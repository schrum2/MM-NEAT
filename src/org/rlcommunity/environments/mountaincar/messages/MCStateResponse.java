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
package org.rlcommunity.environments.mountaincar.messages;

import java.util.StringTokenizer;

import rlVizLib.messaging.AbstractResponse;
import rlVizLib.messaging.GenericMessage;
import rlVizLib.messaging.MessageUser;
import rlVizLib.messaging.MessageValueType;
import rlVizLib.messaging.NotAnRLVizMessageException;
import rlVizLib.messaging.environment.EnvMessageType;

public class MCStateResponse extends AbstractResponse {

    double position;
    double velocity;
    double height;
    double theSlope;
    int theAction;

    public MCStateResponse(int action, double position, double velocity, double height, double slope) {
        this.position = position;
        this.velocity = velocity;
        this.height = height;
        this.theAction = action;
        this.theSlope = slope;

    }

    public MCStateResponse(String responseMessage) throws NotAnRLVizMessageException {

        GenericMessage theGenericResponse = new GenericMessage(responseMessage);

        String thePayLoadString = theGenericResponse.getPayLoad();

        StringTokenizer stateTokenizer = new StringTokenizer(thePayLoadString, ":");

        theAction = Integer.parseInt(stateTokenizer.nextToken());
        position = Double.parseDouble(stateTokenizer.nextToken());
        velocity = Double.parseDouble(stateTokenizer.nextToken());
        height = Double.parseDouble(stateTokenizer.nextToken());
        theSlope = Double.parseDouble(stateTokenizer.nextToken());
    }

    @Override
    public String toString() {
        String theResponse = "MCStateResponse: not implemented ";
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
        theResponseBuffer.append(position);
        theResponseBuffer.append(":");
        theResponseBuffer.append(velocity);
        theResponseBuffer.append(":");
        theResponseBuffer.append(height);
        theResponseBuffer.append(":");
        theResponseBuffer.append(theSlope);

        return theResponseBuffer.toString();
    }

    public double getPosition() {
        return position;
    }

    public double getVelocity() {
        return velocity;
    }

    public double getHeight() {
        return height;
    }

    public int getAction() {
        return theAction;
    }

    public double getSlope() {
        return theSlope;
    }
};