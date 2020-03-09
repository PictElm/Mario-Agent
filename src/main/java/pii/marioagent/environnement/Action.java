package pii.marioagent.environnement;

import org.marioai.engine.helper.MarioActions;

/**
 * Contains a list of button to press.
 * <p>Each frame is as follow: { Left, Right, Down, Speed, Jump }.
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

    public Action() {
        this(new boolean[0][5]);
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
        return this.length - 1 < this.current;
    }

    public void reset() {
        this.current = 0;
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
            sep = " ";
        }

        return r.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Action) {
            Action p = (Action) o;
            if (p.length != this.length)
                return false;
            for (int i = 0; i < this.length; i++) {
                if (p.inputs[i].length != this.inputs[i].length)
                    return false;
                for (int j = 0; j < this.inputs[i].length; j++)
                    if (p.inputs[i][j] != this.inputs[i][j])
                        return false;
            }
            return true;
        }
        return false;
    }

    /**
     * Used to create a new Action with inputs from a string of space-separated bit-fields list.
     * @param c space-separated list of inputs.
     */
    private static boolean[][] parseInput(String c) {
        boolean[][] r;

        if (c.equals("")) return new boolean[0][5];

        int nbInputs = MarioActions.numberOfActions();

        String[] raw = c.split(" ");
        r = new boolean[raw.length][nbInputs];

        for (int frame = 0; frame < raw.length; frame++) {
            int pressed = Integer.parseInt(raw[frame]);
            for (int button = 0; button < nbInputs; button++)
                r[frame][button] = ((1 << button) & pressed) != 0;
        }

        return r;
    }

}
