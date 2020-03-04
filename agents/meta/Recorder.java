package agents.meta;

import java.util.ArrayList;

import environnement.Action;
import environnement.Description;
import environnement.ForwardModel;
import environnement.utils.TilePos;

public class Recorder {

    private ArrayList<Description> records;

    public Recorder() {
        this.records = new ArrayList<>();
    }

    /**
     * Accepts the action the agent intent to use in the situation described be the model.
     * @param action action that will be used.
     * @param model current environnement.
     */
    public void feedAction(Action action, ForwardModel model) {
        Description d = new Description(model.getScreenSceneObservation(), new TilePos(), 0, 0, action);
        this.records.add(d);
    }

    public Description[] getRecords() {
        return this.records.toArray(new Description[this.records.size()]);
    }

}
