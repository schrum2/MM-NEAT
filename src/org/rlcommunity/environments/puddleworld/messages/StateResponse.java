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
package org.rlcommunity.environments.puddleworld.messages;

import java.awt.geom.Point2D;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import java.util.logging.Level;
import java.util.logging.Logger;
import rlVizLib.messaging.AbstractResponse;
import rlVizLib.messaging.BinaryPayload;
import rlVizLib.messaging.GenericMessage;
import rlVizLib.messaging.MessageUser;
import rlVizLib.messaging.MessageValueType;
import rlVizLib.messaging.NotAnRLVizMessageException;
import rlVizLib.messaging.environment.EnvMessageType;

public class StateResponse extends AbstractResponse {

    Point2D Position = new Point2D.Double();

    public StateResponse(String responseMessage) throws NotAnRLVizMessageException {
        try {
            GenericMessage theGenericResponse = new GenericMessage(responseMessage);
            String thePayLoadString = theGenericResponse.getPayLoad();
            DataInputStream in = BinaryPayload.getInputStreamFromPayload(thePayLoadString);
            double x = in.readDouble();
            double y = in.readDouble();
            Position = new Point2D.Double(x, y);
        } catch (Exception ex) {
            Logger.getLogger(StateResponse.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public StateResponse(Point2D agentPosition) {
        this.Position.setLocation(agentPosition);
    }

    @Override
    public String toString() {
        String theResponse = "StateResponse: not implemented ";
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

        BinaryPayload theStatePayload = new BinaryPayload();
        DataOutputStream theOutputStream = theStatePayload.getOutputStream();
        try {
            theOutputStream.writeDouble(Position.getX());
            theOutputStream.writeDouble(Position.getY());
        } catch (IOException ex) {
            Logger.getLogger(StateResponse.class.getName()).log(Level.SEVERE, null, ex);
        }
        theResponseBuffer.append(theStatePayload.getAsEncodedString());
        return theResponseBuffer.toString();
    }

    public Point2D getPosition() {
        return Position;
    }
};
