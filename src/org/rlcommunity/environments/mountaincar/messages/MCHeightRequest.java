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

import java.util.Vector;

import org.rlcommunity.rlglue.codec.RLGlue;
import rlVizLib.messaging.AbstractMessage;
import rlVizLib.messaging.GenericMessage;
import rlVizLib.messaging.MessageUser;
import rlVizLib.messaging.MessageValueType;
import rlVizLib.messaging.NotAnRLVizMessageException;
import rlVizLib.messaging.environment.EnvMessageType;
import rlVizLib.messaging.environment.EnvironmentMessages;

public class MCHeightRequest extends EnvironmentMessages {

    Vector<Double> queryPositions = null;

    public MCHeightRequest(GenericMessage theMessageObject) {
        super(theMessageObject);
    }

    public static MCHeightResponse Execute(Vector<Double> queryPositions) {
        StringBuffer queryPosBuffer = new StringBuffer();

        queryPosBuffer.append(queryPositions.size());
        queryPosBuffer.append(":");

        for (int i = 0; i < queryPositions.size(); i++) {
            queryPosBuffer.append(queryPositions.get(i));
            queryPosBuffer.append(":");
        }


        String theRequest = AbstractMessage.makeMessage(
                MessageUser.kEnv.id(),
                MessageUser.kBenchmark.id(),
                EnvMessageType.kEnvCustom.id(),
                MessageValueType.kStringList.id(),
                "GETHEIGHTS:" + queryPosBuffer.toString());

        String responseMessage = RLGlue.RL_env_message(theRequest);

        MCHeightResponse theResponse;
        try {
            theResponse = new MCHeightResponse(responseMessage);
        } catch (NotAnRLVizMessageException e) {
            System.err.println("In MCStateRequest, the response was not RL-Viz compatible");
            theResponse = null;
        }

        return theResponse;

    }

    public Vector<Double> getQueryPositions() {
        return queryPositions;
    }
}
