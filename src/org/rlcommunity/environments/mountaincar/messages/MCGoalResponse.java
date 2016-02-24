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

import rlVizLib.messaging.AbstractMessage;
import rlVizLib.messaging.AbstractResponse;
import rlVizLib.messaging.GenericMessage;
import rlVizLib.messaging.MessageUser;
import rlVizLib.messaging.MessageValueType;
import rlVizLib.messaging.NotAnRLVizMessageException;
import rlVizLib.messaging.environment.EnvMessageType;

public class MCGoalResponse extends AbstractResponse {

    double goalPosition;

    public MCGoalResponse(double goal) {
        this.goalPosition = goal;
    }

    public MCGoalResponse(String responseMessage) throws NotAnRLVizMessageException {
        GenericMessage theGenericResponse = new GenericMessage(responseMessage);
        String thePayLoadString = theGenericResponse.getPayLoad();
        StringTokenizer stateTokenizer = new StringTokenizer(thePayLoadString, ":");
        goalPosition = Double.parseDouble(stateTokenizer.nextToken());
    }

    public double getGoalPosition() {
        return this.goalPosition;
    }

    @Override
    public String makeStringResponse() {
        StringBuffer goalBuffer = new StringBuffer();

        goalBuffer.append(goalPosition);
        goalBuffer.append(":");


        String theResponse = AbstractMessage.makeMessage(
                MessageUser.kBenchmark.id(),
                MessageUser.kEnv.id(),
                EnvMessageType.kEnvResponse.id(),
                MessageValueType.kStringList.id(),
                goalBuffer.toString());


        return theResponse;
    }
}
