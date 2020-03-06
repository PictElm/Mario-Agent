package environnement.repository;

import environnement.Action;
import environnement.Description;
import environnement.utils.TilePos;

public abstract class BaseRepository {

    /**
     * Get the n-th element. Should be null if n is not a valid id.
     * @param n id of the element to get.
     * @return the element or null.
     */
    protected abstract Description getNth(int n);
    public abstract Description getAny();

    public abstract void add(Description d);
    public abstract void remove(Description d);

    public abstract int count();

    /**
     * Get the n first element after skipping skip element.
     * <p> If the end of the repository is reached, the returned list is null-padded to be of length n.
     * @param n number of element to return.
     * @param skip number of element to skip.
     * @return a list of descriptions of len n.
     */
    public Description[] getFirst(int n, int skip) {
        Description[] r = new Description[n];

        for (int k = 0; k < n; k++)
            r[k] = this.getNth(k + skip);

        return r;
    }

    /**
     * Get the n first element.
     * <p> If the end of the repository is reached, the returned list is null-padded to be of length n.
     * @param n number of element to return.
     * @return a list of descriptions of len n.
     */
    public Description[] getFirst(int n) {
        return this.getFirst(n, 0);
    }

    public Description newDescription(int[][] grid, TilePos location, Action action) {
        return new Description(grid, location, 0, 0, action, BaseRepository.newDescriptionTag());
    }

    private static int descriptionTagCounter = 0;
    protected static String newDescriptionTag() {
        char c = 26 * 26 < BaseRepository.descriptionTagCounter ? 'a' : 'A';
        return "" + (char) (c + (int) (BaseRepository.descriptionTagCounter / 26)) + (char) (c + (int) (BaseRepository.descriptionTagCounter % 26));
    }

    private static int actionTagCounter = 0;
    protected static String newActionTag() {
        char c = 26 * 26 < BaseRepository.actionTagCounter ? 'a' : 'A';
        return "" + (char) (c + (int) (BaseRepository.actionTagCounter / 26)) + (char) (c + (int) (BaseRepository.actionTagCounter % 26));
    }

}
