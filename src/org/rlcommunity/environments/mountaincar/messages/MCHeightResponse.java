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
import java.util.Vector;

import rlVizLib.messaging.AbstractMessage;
import rlVizLib.messaging.AbstractResponse;
import rlVizLib.messaging.GenericMessage;
import rlVizLib.messaging.MessageUser;
import rlVizLib.messaging.MessageValueType;
import rlVizLib.messaging.NotAnRLVizMessageException;
import rlVizLib.messaging.environment.EnvMessageType;

public class MCHeightResponse extends AbstractResponse {

    Vector<Double> theHeights = null;

    public MCHeightResponse(Vector<Double> theHeights) {
        this.theHeights = theHeights;
    }

    public MCHeightResponse(String responseMessage) throws NotAnRLVizMessageException {
        theHeights = new Vector<Double>();
        GenericMessage theGenericResponse = new GenericMessage(responseMessage);

        String thePayLoadString = theGenericResponse.getPayLoad();

        StringTokenizer stateTokenizer = new StringTokenizer(thePayLoadString, ":");

        int numHeights = Integer.parseInt(stateTokenizer.nextToken());

        for (int i = 0; i < numHeights; i++) {
            theHeights.add(new Double(Double.parseDouble(stateTokenizer.nextToken())));
        }
    }

    @Override
    public String makeStringResponse() {
        StringBuffer heightsBuffer = new StringBuffer();

        heightsBuffer.append(theHeights.size());
        heightsBuffer.append(":");

        for (int i = 0; i < theHeights.size(); i++) {
            heightsBuffer.append(theHeights.get(i));
            heightsBuffer.append(":");
        }


        String theResponse = AbstractMessage.makeMessage(
                MessageUser.kBenchmark.id(),
                MessageUser.kEnv.id(),
                EnvMessageType.kEnvResponse.id(),
                MessageValueType.kStringList.id(),
                heightsBuffer.toString());


        return theResponse;
    }

    public Vector<Double> getHeights() {
        return theHeights;
    }
};