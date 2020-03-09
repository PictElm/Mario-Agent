package pii.marioagent.environnement.repository;

import pii.marioagent.environnement.Description;

public class OneRepository extends BaseRepository {

    private Description d;

    public OneRepository(Description d) {
        this.d = d;
    }

    @Override
    protected Description getNth(int n) {
        return d;
    }

    @Override
    public void add(Description... des) {
        ;
    }

    @Override
    public void remove(Description... d) {
        ;
    }

    @Override
    public int count() {
        return 1;
    }

}
