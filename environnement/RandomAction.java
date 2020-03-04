package environnement;

import java.util.Random;

import org.marioai.engine.helper.MarioActions;

/**
 * Random action generator.
 */
public class RandomAction extends Random {

    private static final long serialVersionUID = 1L;

    private final int minNbFrames;
    private final int maxNbFrames;

    /**
     * Create a random action generator from a set seed.
     * @param minNbFrames min amount of frames in an action.
     * @param maxNbFrames max amount of frames in an action.
     * @param seed the initial seed.
     */
    public RandomAction(int minNbFrames, int maxNbFrames, long seed) {
        super(seed);

        this.minNbFrames = minNbFrames;
        this.maxNbFrames = maxNbFrames;
    }

    /**
     * Create a random action generator.
     * @param minNbFrames min amount of frames in an action.
     * @param maxNbFrames max amount of frames in an action.
     */
    public RandomAction(int minNbFrames, int maxNbFrames) {
        super();

        this.minNbFrames = minNbFrames;
        this.maxNbFrames = maxNbFrames;
    }

    public RandomAction(int nbFrames) {
        this(nbFrames, nbFrames + 1);
    }

    protected boolean[] nextInputs(int nbInputs) {
        return new boolean[] { false, true, false, false, false };
        // boolean[] r = new boolean[nbInputs];

        // int pressed = super.next(nbInputs);
        // for (int button = 0; button < nbInputs; button++)
        //     r[button] = ((1 << button) & pressed) != 0;

        // return r;
    }

    protected boolean[] nextInputs(int nbInputs, boolean[] input) {
        boolean[] r = new boolean[nbInputs];

        for (int button = 0; button < nbInputs; button++)
            r[button] = input[button] ^ (super.nextFloat() < .01);

        // right and left cannot be pressed at the same time
        if (r[0] && r[1])
            r[super.next(1)] = false;

        return r;
    }

    /**
     * Returns a new list of inputs following restrictions:
     * <ul>
     * <li>coucou
     * </ul>
     * <p>coucou
     * @return a random Action.
     */
    public Action nextAction() {
        int nbInputs = MarioActions.numberOfActions();
        int nbFrames = this.minNbFrames + super.nextInt(this.maxNbFrames - this.minNbFrames);
        boolean[][] inputs = new boolean[nbFrames][nbInputs];

        inputs[0] = this.nextInputs(nbInputs);
        for (int frame = 1; frame < nbFrames; frame++)
            inputs[frame] = this.nextInputs(nbInputs, inputs[frame - 1]);

        return  new Action(inputs);
    }

}
