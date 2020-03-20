package pii.marioagent.agents.meta;

import pii.marioagent.environnement.Action;
import pii.marioagent.environnement.Description;
import pii.marioagent.environnement.ForwardModel;
import pii.marioagent.environnement.repository.BaseRepository;
import pii.marioagent.environnement.utils.TilePos;

public class Recorder {

    private BaseRepository rec;

    public Recorder(BaseRepository storage) {
        this.rec = storage;
    }

    /**
     * Accepts the action the agent intent to use in the situation described be the model.
     * @param action action that will be used.
     * @param model current environnement.
     */
    public void feedAction(Action action, ForwardModel model, String how, Description... from) {
        Description d = this.rec.newDescription(model.getScreenSceneObservation(), new TilePos(), action, how, from);
        this.rec.add(d);
    }

    public void feedDescription(Description description, String how, Description... from) {
        Description d = this.rec.newDescription(description.getGrid(), new TilePos(), description.getAction(), how, from);
        this.rec.add(d);
    }

}
