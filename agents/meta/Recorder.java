package agents.meta;

import environnement.Action;
import environnement.Description;
import environnement.ForwardModel;
import environnement.repository.BaseRepository;
import environnement.utils.TilePos;

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
    public void feedAction(Action action, ForwardModel model) {
        Description d = this.rec.newDescription(model.getScreenSceneObservation(), new TilePos(), action);
        this.rec.add(d);
    }

}
