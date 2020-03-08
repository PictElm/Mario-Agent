package pii.marioagent.environnement;

import org.marioai.engine.core.MarioForwardModel;

/**
 * A basic wrapper around the MarioForwardModel class.
 */
public class ForwardModel {

    private MarioForwardModel base;

    public ForwardModel(MarioForwardModel model) {
        this.base = model;
    }

    /**
     * @see MarioForwardModel.getScreenSceneObservation()
     * @return a grid of 1 for walls and 0 for non-walls.
     */
    public int[][] getScreenSceneObservation() {
        int[][] screen = this.base.getScreenSceneObservation();
        int[][] r = new int[screen.length][screen[0].length];

        for (int i = 0; i < screen.length; i++)
            for (int j = 0; j < screen[i].length; j++)
                r[i][j] = screen[i][j] == 0 ? 0 : 1;

        return r;
    }

    /**
     * @see MarioForwardModel.getCompletionPercentage()
     * @return percentage of distance,between 0 and 1.
     */
    public float getCompletionPercentage() {
        return this.base.getCompletionPercentage();
    }

}
