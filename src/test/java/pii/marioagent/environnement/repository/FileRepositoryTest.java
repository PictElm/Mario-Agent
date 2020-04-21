package pii.marioagent.environnement.repository;

import org.junit.Assert;
import org.junit.Test;

import pii.marioagent.environnement.Action;
import pii.marioagent.environnement.Description;
import pii.marioagent.environnement.utils.TilePos;

public class FileRepositoryTest {

    @Test
    public void testLoadSave() {
        Action action = new Action(new boolean[][] { { false, true, false, true, false }, { true, false, true, false, true } });
        Description test = new Description(
            new int[][] { { 1, 0 }, { 0, -1 } },
            new TilePos(4, 2),
            12.34f,
            7,
            action,
            "tag",
            "generate"
        );

        // should be able to save and retrieve:
        //  - grid
        //  - preferred
        //  - weight
        //  - occurences
        //  - action
        //  - tag
        String save = FileRepository.getStringDump(test);
        Description d = FileRepository.getDescriptionLoad(save);

        Assert.assertArrayEquals(new int[][] { { 1, 0 }, { 0, -1 } }, d.getGrid());
        Assert.assertEquals(new TilePos(4, 2), d.getPreferredLocation());
        Assert.assertEquals(12.34f, d.getWeight(), .001);
        Assert.assertEquals(7, d.getOccurences());
        Assert.assertEquals(action, d.getAction());
        Assert.assertEquals("tag", d.tag);
    }
}
