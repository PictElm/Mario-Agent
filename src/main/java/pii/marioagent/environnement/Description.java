package pii.marioagent.environnement;

import java.util.ArrayList;

import pii.marioagent.environnement.utils.TilePos;

/**
 * Describes a part of the environnement.
 * <p> A table of ints and the associated location it is usually found at.
 */
public class Description {

    public final String tag;

    public final int width;
    public final int height;
    private final int grid[][];

    private final TilePos preferredLocation;

    private final String how;
    private final Description[] from;

    private float weight;
    private int occurences;

    private final Action action;

    public Description(int[][] grid, TilePos preferred, float weight, int occurences, Action action, String tag, String how, Description... from) {
        this.tag = tag;

        this.width = grid.length;
        this.height = 0 < grid.length ? grid[0].length : 0;
        this.grid = grid;

        this.preferredLocation = preferred;

        this.weight = weight;
        this.occurences = occurences;

        this.action = action;

        this.how = how;
        this.from = from;
    }

    public Description(int[][] grid, TilePos preferred, float weight, String tag) {
        this(grid, preferred, weight, 0, new Action(), tag, "generate");
    }

    /**
     * Create a new Description with grid from a string of semicolon- and comma-separated ints table.
     * @see Action(String)
     * @param gridStr semicolon- and comma-separated ints table.
     * @param prefX preferred location x.
     * @param prefY preferred location y.
     * @param weight importance when found by the agent.
     * @param occurences 
     * @param actionStr space-separated list of inputs.
     */
    public Description(String gridStr, int prefX, int prefY, float weight, int occurences, String actionStr, String tag) {
        this(Description.parseGrid(gridStr), new TilePos(prefX, prefY), weight, occurences, new Action(actionStr), tag, "generate");
    }


    /**
     * Returns the value of the description's grid.
     * @param x x.
     * @param y y.
     * @return grid[x][y].
     */
    public int getAt(int x, int y) {
        return this.grid[x][y];
    }

    /**
     * Returns the entire description's grid.
     * @return grid.
     */
    public int[][] getGrid() {
        return this.grid;
    }

    /**
     * Returns the action to perform when this description is found by the agent.
     * @return the associated action.
     */
    public Action getAction() {
        return this.action;
    }

    /**
     * Return the preferred location where this is usually found by the agent.
     * @return the associated location.
     */
    public TilePos getPreferredLocation() {
        return this.preferredLocation;
    }

    /**
     * Returns the importance of this when found by the agent.
     * @return the associated weight.
     */
    public float getWeight() {
        return this.weight;
    }

    public void setWeight(float reWeight) {
        this.weight = reWeight;
    }

    /**
     * 
     * @return
     */
    public int getOccurences() {
        return this.occurences;
    }

    /**
     * 
     */
    public void incOccurences() {
        this.occurences++;
    }

    /**
     * 
     * @return
     */
    public Description[] getFrom() {
        return this.from;
    }

    /**
     * 
     * @return
     */
    public String getHow() {
        return this.how;
    }

    /**
     * Tries to find a position at which the description's grid is a sub-matrix of the scene.
     * @param scene a grid to search into.
     * @return a TilePos at the position or null if not found.
     */
    public TilePos[] findInScene(int[][] scene) {
        ArrayList<TilePos> r = new ArrayList<>();

        for (int x = 0; x < scene.length - this.width + 1; x++) {
            for (int y = 0; y < scene[x].length - this.height + 1; y++) {
                int i = 0;
                int j = 0;
                while (scene[x + i][y + j] == this.getAt(i, j) || this.getAt(i, j) < 0) {
                    if (this.height - 1 < ++j) {
                        if (this.width - 1 < ++i) {
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

    /**
     * Return a grid according to the following rules:
     * <ul>
     *   <li> if this and mate have the same value, the result here is this value
     *   <li> if this and mate have different values, or one is -1, the result here is:
     *   <ul>
     *     <li> if priority is -1, -1
     *     <li> if priority is 0, this value
     *     <li> if priority is 1, mate value
     *   </ul>
     * </ul>
     * <p> Mate should be smaller, or the same size as this in both width and height.
     * @param thisGrid the "this" grid to be used.
     * @param mateGrid the "mate" grid to be used.
     * @param offPos the offset position in accessing this grid.
     * @param priority the priority behavior for non matching values.
     * @return a new grid the size of mate.
     */
    public static int[][] cross(int[][] thisGrid, int[][] mateGrid, TilePos offPos, int priority) {
        int[][] r = new int[mateGrid.length][mateGrid[0].length];

        for (int i = 0; i < r.length; i++)
            for (int j = 0; j < r[i].length; j++) {
                int thisValue = thisGrid[i + offPos.x][j + offPos.y];
                int mateValue = mateGrid[i][j];
                int hereValue = mateValue;

                if (thisValue != mateValue || thisValue < 0 || mateValue < 0)
                    hereValue = priority < 0 ? -1 : priority == 0 ? thisValue : mateValue;

                r[i][j] = hereValue;
            }

        return r;
    }

    @Override
    public String toString() {
        StringBuilder r = new StringBuilder();

        String sep = "";
        for (int i = 0; i < grid.length; i++) {
            StringBuilder n = new StringBuilder();
            n.append(grid[i][0]);
            for (int j = 1; j < grid[i].length; j++)
                n.append(',').append(grid[i][j]);

            r.append(sep).append(n);
            sep = ";";
        }

        return r.toString();
    }

    /**
     * Used to create a new Description with grid from a string of semicolon- and comma-separated ints table.
     * @param c semicolon- and comma-separated ints table.
     */
    private static int[][] parseGrid(String c) {
        int[][] r;

        if (c.equals("")) return new int[0][0];

        String[] lines = c.split(";");
        r = new int[lines.length][];

        for (int i = 0; i < lines.length; i++) {
            String[] current = lines[i].split(",");
            r[i] = new int[current.length];

            for (int j = 0; j < current.length; j++)
                r[i][j] = Integer.parseInt(current[j]);
        }

        return r;
    }

}
