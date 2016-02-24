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
package org.rlcommunity.environments.tetris.messages;

import java.util.StringTokenizer;

import rlVizLib.messaging.AbstractResponse;
import rlVizLib.messaging.GenericMessage;
import rlVizLib.messaging.MessageUser;
import rlVizLib.messaging.MessageValueType;
import rlVizLib.messaging.NotAnRLVizMessageException;
import rlVizLib.messaging.environment.EnvMessageType;

public class TetrisWorldResponse extends AbstractResponse {

    private int world_width = 0;
    private int world_height = 0;

    public TetrisWorldResponse(int width, int height) {
        this.world_width = width;
        this.world_height = height;
    }

    public TetrisWorldResponse(String responseMessage) throws NotAnRLVizMessageException {

        GenericMessage theGenericResponse;
        theGenericResponse = new GenericMessage(responseMessage);


        String thePayLoadString = theGenericResponse.getPayLoad();

        StringTokenizer stateTokenizer = new StringTokenizer(thePayLoadString, ":");
        this.world_width = Integer.parseInt(stateTokenizer.nextToken());
        this.world_height = Integer.parseInt(stateTokenizer.nextToken());
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

        theResponseBuffer.append(this.world_width);
        theResponseBuffer.append(":");
        theResponseBuffer.append(this.world_height);


        return theResponseBuffer.toString();
    }

    public int getWidth() {
        return this.world_width;
    }

    public int getHeight() {
        return this.world_height;
    }
}
