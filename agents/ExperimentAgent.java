package agents;

import agents.meta.Recorder;
import environnement.Action;
import environnement.Description;
import environnement.ForwardModel;
import environnement.RandomAction;
import environnement.repository.BaseRepository;

/**
 * "Random" agent, uses random number generators to create and adapt Description / Action.
 */
public class ExperimentAgent extends BaseAgent {

    private final RandomAction action;
    private final Recorder rec;

    private final TaskType task;
    private final BaseRepository prov;

    public ExperimentAgent(RandomAction randomAction, Recorder actionRecorder) {
        this.rec = actionRecorder;
        this.task = TaskType.GENERATE;

        this.action = randomAction;
        this.prov = null;
    }

    public ExperimentAgent(RandomAction randomAction, BaseRepository descriptionProvider, Recorder actionRecorder, TaskType task) {
        this.rec = actionRecorder;
        this.task = task;

        this.action = randomAction;
        this.prov = descriptionProvider;
    }

    public enum TaskType {
        GENERATE,
        X_ACTION, // variation in action
        X_DESCRIPTION // variation in description
    }

    private Action alterAction(Description d) {
        Action action = d.getAction();

        int before = (int) (Math.random() * 5);
        int after = (int) (Math.random() * 5);

        int start = 0 + (int) (Math.random() * 5);
        int end = action.length - 1 - (int) (Math.random() * 5);

        boolean[][] inputs = new boolean[before + (end - start + 1) + after][];

        // same from `start` to `end` (offset by `before`)
        for (int k = 0; k < end; k++) {
            boolean[] frame = action.consume();
            if (start - 1 < k)
                inputs[before + k] = frame;
        }

        // ends with `after` new inputs
        for (int k = before + (end - start + 1); k < before + (end - start + 1) + after; k--)
            inputs[k] = this.action.nextInputs(inputs[k - 1]);

        // starts with `before` new inputs
        for (int k = before; 0 < k; k--)
            inputs[k - 1] = this.action.nextInputs(inputs[k]);

        return new Action(inputs);
    }

    @Override
    public boolean[] feed(ForwardModel model) {
        // if no current action or previous action is finished, ask for a new random one
        if (!this.hasCurrent()) {
            if (this.task == TaskType.GENERATE)
                this.setCurrent(this.action.nextAction());
            else if (this.task == TaskType.X_ACTION)
                this.setCurrent(this.alterAction(this.prov.getAny()));

            // record inputs
            if (this.rec != null) this.rec.feedAction(this.getCurrent(), model);
        }

        // consume the current action
        return this.getCurrent().consume();
    }

    @Override
    public String getAgentName() {
        return super.getAgentName() + " [Experiment:" + this.task + "]";
    }

}
