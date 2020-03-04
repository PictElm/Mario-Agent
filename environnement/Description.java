package environnement;

import environnement.utils.TilePos;

/**
 * Describes a part of the environnement.
 */
public class Description {

    public final int width;
    public final int height;
    private final int grid[][];

    private final TilePos preferredLocation;

    private final float weight;
    private int occurences;

    private final Action action;

    public Description(int[][] grid, TilePos preferred, float weight, int occurences, Action action) {
        this.width = grid.length;
        this.height = 0 < grid.length ? grid[0].length : 0;
        this.grid = grid;

        this.preferredLocation = preferred;

        this.weight = weight;
        this.occurences = occurences;

        this.action = action;
    }

    public Description(String gridStr, int prefX, int prefY, float weight, int occurences, String actionStr) {
        this(Description.parseGrid(gridStr), new TilePos(prefX, prefY), weight, occurences, new Action(actionStr));
    }

    public int getAt(int x, int y) {
        return this.grid[x][y];
    }

    public Action getAction() {
        return this.action;
    }

    public TilePos getPreferredLocation() {
        return this.preferredLocation;
    }

    public float getWeight() {
        return this.weight;
    }

    public int getOccurences() {
        return this.occurences;
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
