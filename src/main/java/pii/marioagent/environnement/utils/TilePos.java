package pii.marioagent.environnement.utils;

/**
 * A basic (x, y) int-vector.
 */
public class TilePos {

    public int x;
    public int y;

    public TilePos(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public TilePos() {
        this(0, 0);
    }

    @Override
    public String toString() {
        return "TilePos(" + this.x + ", " + this.y + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof TilePos) {
            TilePos p = (TilePos) o;
            return p.x == this.x && p.y == this.y;
        }
        return false;
    }

    /**
     * Returns the square of the euclidean distance of 2 tiles.
     * @param a tile 1.
     * @param b tile 2.
     * @return distance.
     */
    public static float distanceSq(TilePos a, TilePos b) {
        float u = a.x - b.x, v = a.y - b.y;
        return u * u + v * v;
    }

    /**
     * Returns the euclidean distance of 2 tiles.
     * <p>Calls {@link Math.sqrt}.
     * Prefer using {@see TilePos.distanceSq} when possible.
     * @param a tile 1.
     * @param b tile 2.
     * @return distance.
     */
    public static float distance(TilePos a, TilePos b) {
        return (float) Math.sqrt(TilePos.distanceSq(a, b));
    }

}
