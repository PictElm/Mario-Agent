package environnement.repository;

import environnement.Description;

public abstract class BaseRepository {

    /**
     * Get the n-th element. Should be null if n is not a valid id.
     * @param n id of the element to get.
     * @return the element or null.
     */
    protected abstract Description getNth(int n);

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

}
