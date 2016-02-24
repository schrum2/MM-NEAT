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

import org.rlcommunity.rlglue.codec.RLGlue;
import rlVizLib.messaging.AbstractMessage;
import rlVizLib.messaging.GenericMessage;
import rlVizLib.messaging.MessageUser;
import rlVizLib.messaging.MessageValueType;
import rlVizLib.messaging.NotAnRLVizMessageException;
import rlVizLib.messaging.environment.EnvMessageType;
import rlVizLib.messaging.environment.EnvironmentMessages;

public class StateRequest extends EnvironmentMessages {

    public StateRequest(GenericMessage theMessageObject) {
        super(theMessageObject);
    }

    public static StateResponse Execute() {
        String theRequest = AbstractMessage.makeMessage(
                MessageUser.kEnv.id(),
                MessageUser.kBenchmark.id(),
                EnvMessageType.kEnvCustom.id(),
                MessageValueType.kString.id(),
                "GETPWSTATE");

        String responseMessage = RLGlue.RL_env_message(theRequest);

        StateResponse theResponse;
        try {
            theResponse = new StateResponse(responseMessage);
        } catch (NotAnRLVizMessageException e) {
            System.err.println("In ".getClass().getName() + ", the response was not RL-Viz compatible");
            theResponse = null;
        }

        return theResponse;

    }
}
