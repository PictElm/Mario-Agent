package pii.marioagent.agents.experiment;

import pii.marioagent.agents.meta.Recorder;
import pii.marioagent.environnement.Description;
import pii.marioagent.environnement.ForwardModel;
import pii.marioagent.environnement.RandomAction;
import pii.marioagent.environnement.utils.TilePos;

public class AlterDescriptionAgent extends BaseExperimentAgent {

    Description from;

    public AlterDescriptionAgent(RandomAction randomAction, Recorder actionRecorder, Description inspiration) {
        super(randomAction, actionRecorder);
        this.from = inspiration;
    }

    /**
     * TODO: doc
     * @param d
     * @param outNewPos
     * @return
     */
    public int[][] alterDescription(Description d, TilePos outNewPos) {
        //int range = 2;
        int range = Math.min(Math.min((int) (d.width / 2f), (int) (d.height / 2f)), 2);

        if (range == 0) return d.getGrid();

        int startI = this.random.nextInt(range);
        int endI = d.width - 1 - this.random.nextInt(range);

        int startJ = this.random.nextInt(range);
        int endJ = d.height - 1 - this.random.nextInt(range);

        int[][] grid = new int[endI - startI][endJ - startJ];

        for (int i = startI; i < endI; i++) {
            for (int j = startJ; j < endJ; j++) {
                int at = d.getAt(i, j);
                if (this.random.nextDouble() < 1d / (d.width * d.height))
                    at = at < 0 ? this.random.nextInt(2) : -1;
                grid[i - startI][j - startJ] = at;
            }
        }

        outNewPos.x = d.getPreferredLocation().x + startI;
        outNewPos.y = d.getPreferredLocation().y + startJ;
        return grid;
    }

    @Override
    protected String getExpName() {
        return "alter description";
    }

    @Override
    protected Description[] getExpInspiration(ForwardModel model) {
        return new Description[] { this.from };
    }

    @Override
    protected Description getExpResult(ForwardModel model, Description... from) {
        TilePos newPref = new TilePos();

        int[][] scene = model.getScreenSceneObservation();
        int[][] altered = this.alterDescription(from[0], newPref);

        int[][] r = altered; //Description.cross(scene, altered, newPref, 0);

        return super.newDescription(r, newPref, from[0].getAction());
    }

}
