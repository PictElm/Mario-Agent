package pii.marioagent.agents;

import java.util.LinkedList;
import java.util.Queue;

import pii.marioagent.Main;
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
     * Local weight for a found description in the environnement (at fit) accounting for its preferred location.
     * @param d
     * @param fit
     * @return
     */
    protected static float localWeight(Description d, TilePos fit) {
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
            TilePos[] fittingPos = next.findInScene(scene);
            // if it's able to, complete the `to` list and re-evaluate weight with fit position
            if (0 < fittingPos.length) {
                // find the best location for the description
                int localBest = 0;
                for (int k = 1; k < fittingPos.length; k++)
                    if (UseAgent.localWeight(next, fittingPos[localBest]) < UseAgent.localWeight(next, fittingPos[k]))
                        localBest = k;

                to[fitted] = next;
                at[fitted] = fittingPos[localBest];
                r[fitted++] = UseAgent.localWeight(next, fittingPos[localBest]);
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
     * <p> Pick is stored in this.current.
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
        return Main.SETTINGS.userAgentSettings;
    }

    @Override
    public String getAgentName() {
        return super.getAgentName() + " [Use:" + this.prov.count() + "]";
    }

}
