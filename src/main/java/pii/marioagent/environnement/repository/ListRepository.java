package pii.marioagent.environnement.repository;

import java.util.ArrayList;
import java.util.Arrays;

import pii.marioagent.environnement.Description;

/**
 * An implementation of the BaseRepository using a simple <code>ArrayList&lt;Description&gt;</code>.
 */
public class ListRepository extends BaseRepository {

    private final ArrayList<Description> data;
    private boolean sorted;

    public ListRepository(Description... des) {
        this.data = new ArrayList<>();
        this.sorted = false;

        this.add(des);
    }

    protected void sort() {
        if (!this.sorted)
            this.data.sort((Description e1, Description e2) -> (int) Math.signum(e2.getWeight() - e1.getWeight()));
        this.sorted = true;
    }

    protected Description[] getAll() {
        return this.data.toArray(new Description[this.data.size()]);
    }

    @Override
    protected Description getNth(int n) {
        if (-1 < n && n < this.data.size()) {
            if (!this.sorted) this.sort();
            return this.data.get(n);
        }
        return null;
    }

    @Override
    public void add(Description... des) {
        this.sorted = false;
        this.data.addAll(Arrays.asList(des));
    }

    @Override
    public void remove(Description... des) {
        this.data.removeAll(Arrays.asList(des));
    }

    @Override
    public int count() {
        return this.data.size();
    }

    /**
     * Trim the unvalued element at the end of the repository.
     * @param ratio from 1 to trim all to 0 to trim nothing (e.g.: .5f will keep half of the unvalued elements)
     * @return how many elements where removed.
     */
    public int trim(float ratio) {
        if (!this.sorted) this.sort();

        int uslFrom = 0;
        while (0 < this.data.get(uslFrom++).getWeight());
        uslFrom+= (int) ((this.data.size() - uslFrom) * (1f - ratio));

        int r = this.data.size() - uslFrom;

        Object[] selected = this.data.subList(0, uslFrom).toArray();
        this.data.clear();

        for (Object it : selected)
            this.data.add((Description) it);

        return r;
    }

}
