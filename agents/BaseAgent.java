package agents;

import org.marioai.engine.core.MarioAgent;
import org.marioai.engine.core.MarioForwardModel;
import org.marioai.engine.core.MarioTimer;

public abstract class BaseAgent implements MarioAgent {

    /**
     * Ask the agent for input given the game state.
     * @param model access to game state.
     * @return inputs to perform as a boolean array (0: Left, Right, Down, Speed, Jump).
     */
    public abstract boolean[] feed(MarioForwardModel model);

    public void initialize(MarioForwardModel model, MarioTimer timer) {
        System.out.println("BaseAgent.initialize (name: " + this.getAgentName() + " - class: " + this.getClass() + ")");
    }

    public boolean[] getActions(MarioForwardModel model, MarioTimer timer) {
        return this.feed(model);
    }

    public String getAgentName() {
        return "DA";
    }

}
