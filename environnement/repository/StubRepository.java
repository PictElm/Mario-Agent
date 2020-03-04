package environnement.repository;

import environnement.Description;

public class StubRepository extends BaseRepository {

    private final static Description[] DATA = {
        new Description("1,1,0;-1,0,0;0,0,0", 0, 0, 0, 0, "5"),
        new Description("1,-1,0;1,0,0;0,0,0", 0, 0, 0, 0, "5"),
    };

    @Override
    protected Description getNth(int n) {
        if (-1 < n && n < StubRepository.DATA.length)
            return StubRepository.DATA[n];
        return null;
    }

}
