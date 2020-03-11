package pii.marioagent.environnement.repository;

import pii.marioagent.agents.ExperimentAgent.TaskType;
import pii.marioagent.environnement.Action;
import pii.marioagent.environnement.Description;
import pii.marioagent.environnement.utils.TilePos;

public abstract class BaseRepository {

    /**
     * Get the n-th element. Should be null if n is not a valid id.
     * @param n id of the element to get.
     * @return the element or null.
     */
    protected abstract Description getNth(int n);

    public abstract void add(Description... des);
    public abstract void remove(Description... des);

    /**
     * Should return the total count of descriptions present in the repository.
     * @return current number of descriptions.
     */
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

    /**
     * Used to create a new Description object that won't interfere with any other by its tag.
     * <p> The new Description has the provided one as parent.
     * <p> Note: it is not expected to add the new object to the repository (check with re-implementation if any).
     * @param grid Description's grid.
     * @param location Description's preferred location.
     * @param action Description's action.
     * @param how the method that was used to generate this description.
     * @param from the parent Description or null (in which case how is expected to be TaskType.GENERATE).
     * @return a uniquely tagged new Description object.
     */
    public Description newDescription(int[][] grid, TilePos location, Action action, TaskType how, Description from) {
        return new Description(grid, location, 0, 0, action, BaseRepository.newDescriptionTag(), how, from);
    }

    private static int descriptionTagCounter = -1;
    protected static final String newDescriptionTag() {
        return Integer.toHexString(++BaseRepository.descriptionTagCounter);
    }

    private static int actionTagCounter = -1;
    protected static final String newActionTag() {
        return Integer.toHexString(++BaseRepository.actionTagCounter);
    }

}
