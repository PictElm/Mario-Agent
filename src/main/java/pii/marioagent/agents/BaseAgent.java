package pii.marioagent.agents;

import org.marioai.engine.core.MarioAgent;
import org.marioai.engine.core.MarioForwardModel;
import org.marioai.engine.core.MarioTimer;

import pii.marioagent.environnement.Action;
import pii.marioagent.environnement.ForwardModel;

public abstract class BaseAgent implements MarioAgent {

    private Action currentAction;

    protected Action getCurrent() {
        return this.currentAction;
    }

    protected void setCurrent(Action a) {
        a.reset();
        this.currentAction = a;
    }

    protected boolean hasCurrent() {
        return this.currentAction != null && !this.currentAction.finished();
    }

    /**
     * Ask the agent for input given the game state.
     * @param model access to game state.
     * @return inputs to perform as a boolean array (0: Left, Right, Down, Speed, Jump).
     */
    public abstract boolean[] feed(ForwardModel model);

    @Override
    public void initialize(MarioForwardModel model, MarioTimer timer) {
        ;//System.out.println("BaseAgent.initialize (name: " + this.getAgentName() + " - " + this.getClass() + ")");
    }

    @Override
    public boolean[] getActions(MarioForwardModel model, MarioTimer timer) {
        return this.feed(new ForwardModel(model));
    }

    @Override
    public String getAgentName() {
        return "D/A";
    }

}
