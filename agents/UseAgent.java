package agents;

import java.util.LinkedList;
import java.util.Queue;

import org.marioai.engine.core.MarioForwardModel;

import agents.meta.Statistics;
import environnement.Action;
import environnement.Description;
import environnement.repository.BaseRepository;
import environnement.utils.TilePos;

/**
 * "Trained" agent, relies on the Description provided by the BaseRepository given to constructor.
 */
public class UseAgent extends BaseAgent {

    protected final BaseRepository prov;
    protected final Statistics stat;

    private Action current;

    public UseAgent(BaseRepository descriptionProvider, Statistics statReport) {
        this.prov = descriptionProvider;
        this.stat = statReport;
    }

    /**
     * Tries to find a position at which the description's grid is a sub-matrix of the scene.
     * TODO: if it exists multiple times, should only focus on the most reWeight-ed one.
     * @param d description which grid is to be found.
     * @param scene a grid to search into.
     * @return a TilePos at the position or null if not found.
     */
    protected static TilePos fitDescription(Description d, int[][] scene) {
        for (int x = 0; x < scene.length - d.width + 1; x++) {
            for (int y = 0; y < scene[x].length - d.height + 1; y++) {
                int i = 0;
                int j = 0;
                while (scene[x + i][y + j] == d.getAt(i, j) || d.getAt(i, j) < 0) {
                    if (d.height - 1 < ++j) {
                        if (d.width - 1 < ++i)
                            return new TilePos(x, y);
                        j = 0;
                    }
                }
            }
        }
        return null;
    }

    protected static float reWeight(Description d, TilePos fit) {
        return d.getWeight() / (TilePos.distanceSq(d.getPreferredLocation(), fit) + 1);
    }

    /**
     * Attempts to describe the given scene using as many description element as the length of to.
     * Result are store in the given parameter to.
     * @param scene scene to describe.
     * @param to list to store results into.
     * @return reevaluated weight for corresponding description in the result.
     */
    protected float[] describeEnvironnement(int[][] scene, Description[] to) {
        float[] r = new float[to.length];

        int fitted = 0;
        int skip = 0;
        Description next = null;
        Queue<Description> buffer = new LinkedList<>();

        do {
            // keep a buffer of `to.length` descriptions at hand
            if (buffer.isEmpty() && this.prov != null) {
                for (Description item : this.prov.getFirst(to.length, skip))
                    buffer.add(item);
                skip+= to.length;
            }

            // query the next description element to use
            next = buffer.poll();
            // if none is left, break and return (@see `BaseRepository.getFirst` for null-padding)
            if (next == null) break;

            // try to fit the description to the current scene
            TilePos fit = fitDescription(next, scene);
            // if it's able to, complete the `to` list and re-evaluate weight with fit position
            if (fit != null) {
                to[fitted] = next;
                r[fitted++] = UseAgent.reWeight(next, fit);
            }

        // until enough descriptions are found to match requirement
        } while (fitted < to.length);

        // if no fitting descriptions was found, add at least an empty description (~ waiting)
        if (fitted == 0)
            to[0] = new Description("", 0, 0, ++r[0], 0, "0");

        return r;
    }

    /**
     * Finds the next 'logical' action to perform by:
     * <ul>
     *   <li> describing the current scene from the model,
     *   <li> weighting each description used,
     *   <li> picking the most valuated one.
     * </ul>
     * <p>Pick is stored in this.current.
     * @param model access to game state.
     */
    protected void findNewAction(MarioForwardModel model) {
        // query the 3 first fitting descriptions, result in `result`
        Description[] result = new Description[3];
        float[] weighted = this.describeEnvironnement(model.getScreenSceneObservation(), result);

        // find the most weighted one
        int max = 0;
        for (int k = 0; k < weighted.length; k++)
            if (weighted[max] < weighted[k])
                max = k;

        // set the current action
        this.current = result[max].getAction();

        // report choice for statistics
        if (this.stat != null) this.stat.choiceReport(result, max);
    }

    @Override
    public boolean[] feed(MarioForwardModel model) {
        // if no current action or previous action is finished, find a new one
        if (this.current == null || this.current.finished())
            this.findNewAction(model);

        // report progress for statistics
        if (this.stat != null) this.stat.progressReport(model);

        // consume the current action
        return this.current.consume();
    }

}
