package pii.marioagent.agents;

import pii.marioagent.Main;
import pii.marioagent.agents.meta.Recorder;
import pii.marioagent.environnement.ForwardModel;
import pii.marioagent.environnement.RandomAction;

public class GenerateAgent extends BaseAgent {

    private final RandomAction random;
    private final Recorder rec;

    public GenerateAgent(RandomAction randomAction, Recorder actionRecorder) {
        this.random = randomAction;
        this.rec = actionRecorder;
    }

    @Override
    protected boolean[] feed(ForwardModel model) {
        // if no current action or previous action is finished, ask for a new random one
        if (!this.hasCurrent()) {
            this.setCurrent(this.random.nextAction());

            // record inputs
            if (this.rec != null) this.rec.feedAction(this.getCurrent(), model, "generate");
        }

        // consume the current action
        return this.getCurrent().consume();
    }

    @Override
    protected AgentSettings getSettings() {
        return Main.SETTINGS.generateAgentSettings;
    }

    @Override
    public String getAgentName() {
        return super.getAgentName() + " [Generate]";
    }

}
