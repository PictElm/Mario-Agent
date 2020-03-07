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

    private final RandomAction random;
    private final Recorder rec;

    private final TaskType task;
    private final BaseRepository prov;

    public ExperimentAgent(RandomAction randomAction, Recorder actionRecorder) {
        this.rec = actionRecorder;
        this.task = TaskType.GENERATE;

        this.random = randomAction;
        this.prov = null;
    }

    public ExperimentAgent(RandomAction randomAction, BaseRepository descriptionProvider, Recorder actionRecorder, TaskType task) {
        this.rec = actionRecorder;
        this.task = task;

        this.random = randomAction;
        this.prov = descriptionProvider;
    }

    public enum TaskType {
        GENERATE,
        X_ACTION, // variation in action
        X_DESCRIPTION // variation in description
    }

    private Action alterAction(Description d) {
        Action action = d.getAction();
        action.reset();

        int range = Math.min((int) (action.length / 2), 10);

        int before = this.random.nextInt(range);
        int after = this.random.nextInt(range);

        int start = 0 + this.random.nextInt(range);
        int end = action.length - 1 - this.random.nextInt(range);

        boolean[][] inputs = new boolean[before + (end - start + 1) + after][];

        // copy from `start` to `end` (offset by `before`)
        for (int k = 0; k < end + 1; k++) {
            boolean[] frame = action.consume();
            if (start - 1 < k)
                inputs[before - start + k] = frame;
        }

        // end with `after` new random inputs
        for (int k = 0; k < after; k++)
            inputs[before - start + end + k + 1] = this.random.nextInputs(inputs[before - start + end + k]);

        // start with `before` new random inputs
        for (int k = before; 0 < k; k--)
            inputs[k - 1] = this.random.nextInputs(inputs[k]);

        return new Action(inputs);
    }

    @Override
    public boolean[] feed(ForwardModel model) {
        // if no current action or previous action is finished, ask for a new random one
        if (!this.hasCurrent()) {
            if (this.task == TaskType.GENERATE)
                this.setCurrent(this.random.nextAction());
            else if (this.task == TaskType.X_ACTION)
                this.setCurrent(this.alterAction(this.prov.getFirst(1)[0]));

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
