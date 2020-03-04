package environnement;

import org.marioai.engine.helper.MarioActions;

/**
 * Contains a list of actions to perform.
 */
public class Action {

    public final int length;
    private final boolean[][] inputs;

    private int current;

    public Action(boolean[][] inputs) {
        this.length = inputs.length;
        this.inputs = inputs;

        this.current = 0;
    }

    /**
     * Creates a new Action with inputs from a string of space-separated bit-fields list.
     * @param actionStr space-separated list of inputs.
     */
    public Action(String actionStr) {
        this(Action.parseInput(actionStr));
    }

    /**
     * Returns the current inputs and advance by one for next call.
     * @return inputs (0: Left, Right, Down, Speed, Jump).
     */
    public boolean[] consume() {
        return this.inputs[this.current++];
    }

    /**
     * Returns true if there is no input left to consume.
     * @return true if there is no input left to consume.
     */
    public boolean finished() {
        return this.length <= this.current;
    }

    @Override
    public String toString() {
        StringBuilder r = new StringBuilder();

        String sep = "";
        for (boolean[] bp : this.inputs) {
            int n = 0;
            for (int i = bp.length - 1; -1 < i; i--)
                n = (n << 1) + (bp[i] ? 1 : 0);

            r.append(sep).append(n);
            sep = ",";
        }

        return r.toString();
    }

    private static boolean[][] parseInput(String c) {
        boolean[][] r;

        if (c.equals("")) return new boolean[0][5];

        String[] raw = c.split(" ");
        r = new boolean[raw.length][MarioActions.numberOfActions()];

        for (int frame = 0; frame < raw.length; frame++) {
            int pressed = Integer.parseInt(raw[frame]);
            for (int button = 0; button < 4; button++)
                r[frame][button] = ((1 << button) & pressed) != 0;
        }

        return r;
    }

}
