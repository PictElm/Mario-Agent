package pii.marioagent.environnement.repository;

import java.util.ArrayList;
import java.util.Arrays;

import pii.marioagent.environnement.Description;

/**
 * An implementation of the BaseRepository which loads from a file.
 * <p>Descriptions are stored as: <code>"gridStr|prefX|prefY|weight|occurences|actionStr"</code>
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
            this.data.sort((Description e1, Description e2) -> (int) (e2.getWeight() - e1.getWeight()));
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

}
