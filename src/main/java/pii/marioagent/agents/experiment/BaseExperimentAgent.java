package pii.marioagent.agents.experiment;

import pii.marioagent.Main;
import pii.marioagent.agents.BaseAgent;
import pii.marioagent.agents.meta.Recorder;
import pii.marioagent.environnement.Action;
import pii.marioagent.environnement.Description;
import pii.marioagent.environnement.ForwardModel;
import pii.marioagent.environnement.RandomAction;
import pii.marioagent.environnement.utils.TilePos;

/**
 * "Random" agent, uses random number generators to create and adapt Description / Action.
 */
public abstract class BaseExperimentAgent extends BaseAgent {

    protected final RandomAction random;
    private final Recorder rec;

    private boolean expStarted;

    public BaseExperimentAgent(RandomAction randomAction, Recorder actionRecorder) {
        this.random = randomAction;
        this.rec = actionRecorder;
    }

    protected Description newDescription(int[][] grid, TilePos preferredLocation, Action action) {
        return new Description(grid, preferredLocation, 0, 0, action, "", "");
    }

    protected abstract String getExpName();
    protected abstract Description[] getExpInspiration(ForwardModel model);
    protected abstract Description getExpResult(ForwardModel model, Description... from);
    protected void finishExp(ForwardModel model) { ; }

    @Override
    protected final boolean[] feed(ForwardModel model) {
        // if no current action or previous action is finished, ask for a new one
        if (!this.hasCurrent()) {
            if (!this.expStarted) {
                // the new one may be inspired by multiple ones
                Description[] parent = this.getExpInspiration(model);
                Description description = this.getExpResult(model, parent);

                this.setCurrent(description.getAction());
                // record inputs
                if (this.rec != null) this.rec.feedDescription(description, this.getExpName(), parent);

                this.expStarted = true;
            } else {
                this.finishExp(model);
                return null;
            }
        }

        // consume the current action
        return this.getCurrent().consume();
    }

    @Override
    protected AgentSettings getSettings() {
        return Main.SETTINGS.experimentAgentSettings;
    }

    @Override
    public final String getAgentName() {
        return super.getAgentName() + " [Experiment: " + this.getExpName() + "]";
    }

}
