package org.rlcommunity.environments.cartpole.messages;

import org.rlcommunity.rlglue.codec.RLGlue;
import rlVizLib.messaging.AbstractMessage;
import rlVizLib.messaging.GenericMessage;
import rlVizLib.messaging.MessageUser;
import rlVizLib.messaging.MessageValueType;
import rlVizLib.messaging.NotAnRLVizMessageException;
import rlVizLib.messaging.environment.EnvMessageType;
import rlVizLib.messaging.environment.EnvironmentMessages;

public class CartpoleTrackRequest extends EnvironmentMessages {

    public CartpoleTrackRequest(GenericMessage theMessageObject) {
        super(theMessageObject);
    }

    public static CartpoleTrackResponse Execute() {
        String theRequest = AbstractMessage.makeMessage(
                MessageUser.kEnv.id(),
                MessageUser.kBenchmark.id(),
                EnvMessageType.kEnvCustom.id(),
                MessageValueType.kString.id(),
                "GETCARTPOLETRACK");

        String responseMessage = RLGlue.RL_env_message(theRequest);
        CartpoleTrackResponse theResponse;
        try {
            theResponse = new CartpoleTrackResponse(responseMessage);
        } catch (NotAnRLVizMessageException ex) {
            System.out.println("Not a valid RL Viz Message in Cartpole Track Request" + ex);
            return null;
        }
        return theResponse;
    }
}
