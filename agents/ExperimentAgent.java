package agents;

import org.marioai.engine.core.MarioForwardModel;

import agents.meta.Recorder;
import environnement.Action;
import environnement.RandomAction;

/**
 * "Random" agent, uses random number generators to create and adapt Description / Action.
 */
public class ExperimentAgent extends BaseAgent {

    private final RandomAction action;
    private final Recorder rec;

    private Action current;

    public ExperimentAgent(RandomAction randomAction, Recorder actionRecorder) {
        this.action = randomAction;
        this.rec = actionRecorder;
    }

    @Override
    public boolean[] feed(MarioForwardModel model) {
        // if no current action or previous action is finished, ask for a new random one
        if (this.current == null || this.current.finished()) {
            this.current = this.action.nextAction();

            // record inputs
            if (this.rec != null) this.rec.feedAction(this.current, model);
        }

        // consume the current action
        return this.current.consume();
    }

}
