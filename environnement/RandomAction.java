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

    /**
     * Create a random action generator.
     * @param nbFrames exact amount of frames in an action.
     */
    public RandomAction(int nbFrames) {
        this(nbFrames, nbFrames + 1);
    }

    /**
     * Used to generate the first button combination of the sequence.
     * @param nbInputs number of button that can be pressed.
     * @return a random list of boolean.
     */
    protected boolean[] nextInputs(int nbInputs) {
        boolean[] r = new boolean[nbInputs];

        int pressed = super.next(nbInputs);
        for (int button = 0; button < nbInputs; button++)
            r[button] = ((1 << button) & pressed) != 0;

        return r;
    }

    /**
     * Used to generate a logical followup to the given input.
     * @see RandomAction.nextAction()
     * @param input previous frame's inputs.
     * @return a random (biased) list of boolean.
     */
    public boolean[] nextInputs(boolean[] input) {
        boolean[] r = new boolean[input.length];

        for (int button = 0; button < input.length; button++)
            r[button] = input[button] ^ (super.nextFloat() < .05);

        // right and left cannot be pressed at the same time
        if (r[0] && r[1])
            r[super.next(1)] = false;

        return r;
    }

    /**
     * Returns a new list of inputs following restrictions:
     * <ul>
     *   <li>a button is more likely to keep its state from one frame the next
     *   <li>right and left cannot be pressed at the same time
     * </ul>
     * @return a random Action.
     */
    public Action nextAction() {
        int nbInputs = MarioActions.numberOfActions();
        int nbFrames = this.minNbFrames + super.nextInt(this.maxNbFrames - this.minNbFrames);
        boolean[][] inputs = new boolean[nbFrames][nbInputs];

        inputs[0] = this.nextInputs(nbInputs);
        for (int frame = 1; frame < nbFrames; frame++)
            inputs[frame] = this.nextInputs(inputs[frame - 1]);

        return new Action(inputs);
    }

}
