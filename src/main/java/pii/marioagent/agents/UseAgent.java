package pii.marioagent.agents;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import pii.marioagent.agents.meta.Statistics;
import pii.marioagent.environnement.Action;
import pii.marioagent.environnement.Description;
import pii.marioagent.environnement.ForwardModel;
import pii.marioagent.environnement.repository.BaseRepository;
import pii.marioagent.environnement.utils.TilePos;

/**
 * "Trained" agent, relies on the Description provided by the BaseRepository given to constructor.
 */
public class UseAgent extends BaseAgent {

    protected final BaseRepository prov;
    protected final Statistics stat;

    public UseAgent(BaseRepository descriptionProvider, Statistics statReport) {
        this.prov = descriptionProvider;
        this.stat = statReport;
    }

    public UseAgent(BaseRepository descriptionProvider) {
        this(descriptionProvider, null);
    }

    /**
     * Tries to find a position at which the description's grid is a sub-matrix of the scene.
     * @param d description which grid is to be found.
     * @param scene a grid to search into.
     * @return a TilePos at the position or null if not found.
     */
    protected static TilePos[] fitDescription(Description d, int[][] scene) {
        ArrayList<TilePos> r = new ArrayList<>();

        for (int x = 0; x < scene.length - d.width + 1; x++) {
            for (int y = 0; y < scene[x].length - d.height + 1; y++) {
                int i = 0;
                int j = 0;
                while (scene[x + i][y + j] == d.getAt(i, j) || d.getAt(i, j) < 0) {
                    if (d.height - 1 < ++j) {
                        if (d.width - 1 < ++i) {
                            r.add(new TilePos(x, y));
                            break;
                        }
                        j = 0;
                    }
                }
            }
        }

        return r.toArray(new TilePos[r.size()]);
    }

    protected static float reWeight(Description d, TilePos fit) {
        return d.getWeight() / (TilePos.distanceSq(d.getPreferredLocation(), fit) + 1);
    }

    /**
     * Attempts to describe the given scene using as many description element as the length of to.
     * Result of the description are stored in the given parameters.
     * @param scene scene to describe.
     * @param to list to store results into.
     * @param r list to store reWeighted values (or null).
     * @param at list to store found locations (or null).
     * @return true if it was able to describe the environnement.
     */
    protected boolean describeEnvironnement(int[][] scene, Description[] to, float[] r, TilePos[] at) {
        if (r == null) r = new float[to.length];
        if (at == null) at = new TilePos[to.length];

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
            TilePos[] fittingPos = UseAgent.fitDescription(next, scene);
            // if it's able to, complete the `to` list and re-evaluate weight with fit position
            if (0 < fittingPos.length) {
                // find the best location for the description
                int localBest = 0;
                for (int k = 1; k < fittingPos.length; k++)
                    if (UseAgent.reWeight(next, fittingPos[localBest]) < UseAgent.reWeight(next, fittingPos[k]))
                        localBest = k;

                to[fitted] = next;
                at[fitted] = fittingPos[localBest];
                r[fitted++] = UseAgent.reWeight(next, fittingPos[localBest]);
            }

        // until enough descriptions are found to match requirement
        } while (fitted < to.length);

        return 0 < fitted;
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
    protected void findNewAction(ForwardModel model) {
        // query the 3 first fitting descriptions, result in `result`
        Description[] result = new Description[3];
        float[] weighted = new float[result.length];
        TilePos[] foundAt = new TilePos[result.length];
        boolean wasAbleTo = this.describeEnvironnement(model.getScreenSceneObservation(), result, weighted, foundAt);

        if (wasAbleTo) {
            // find the most weighted one
            int max = 0;
            for (int k = 0; k < result.length; k++) {
                if (result[k] != null) {
                    if (weighted[max] < weighted[k])
                        max = k;
                    result[k].incOccurences();
                }
            }

            // set it as the current action and report choice for statistics
            this.setCurrent(result[max].getAction());
            if (this.stat != null) this.stat.choiceReport(result, foundAt, max);

        } else {
            // if no fitting description was found, do nothing
            this.setCurrent(new Action("0")); // FIXME: will result in doing so endlessly
            if (this.stat != null) this.stat.statusReport(Statistics.Status.DEAD_END);
        }
    }

    @Override
    protected boolean[] feed(ForwardModel model) {
        // if no current action or previous action is finished, find a new one
        if (!this.hasCurrent())
            this.findNewAction(model);

        // report progress for statistics
        if (this.stat != null) this.stat.progressReport(model);

        // consume the current action
        return this.getCurrent().consume();
    }

    @Override
    protected AgentSettings getSettings() {
        return new AgentSettings(20);
    }

    @Override
    public String getAgentName() {
        return super.getAgentName() + " [Use:" + this.prov.count() + "]";
    }

}
