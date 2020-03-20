package pii.marioagent.agents;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import pii.marioagent.environnement.Description;
import pii.marioagent.environnement.repository.ListRepository;
import pii.marioagent.environnement.utils.TilePos;

public class UseAgentTest {

    private static int[][] scene;
    private static Description d1;
    private static Description d2;
    private static ListRepository repo;

    @BeforeClass
    public static void loadTestScene() {
        scene = new int[][] {
            { 0, 0, 1, 1, 0 },
            { 0, 0, 1, 0, 0 },
            { 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0 }
        };
        d1 = new Description(new int[][] { { 1, 1 }, { -1, 0 }, { 0, 0 } }, new TilePos(0, 0), 1f, "d1");
        d2 = new Description(new int[][] { { 1, -1, 0 }, { -1, 0, 0 } }, new TilePos(1, 2), 1f, "d2");
        repo = new ListRepository(d1, d2);
    }

    @Test
    public void testFitDescription() {

        // d1 should be found once at (0, 2)
        TilePos[] r1 = d1.findInScene(scene);
        Assert.assertEquals(1, r1.length);
        Assert.assertEquals(new TilePos(0, 2), r1[0]);

        // d2 should be found twice at (0, 2) and (1, 2) -- in order of appearance
        TilePos[] r2 = d2.findInScene(scene);
        Assert.assertEquals(2, r2.length);
        Assert.assertEquals(new TilePos(0, 2), r2[0]);
        Assert.assertEquals(new TilePos(1, 2), r2[1]);

    }

    @Test
    public void testDescribeEnvironnement() {
        Description[] result = new Description[3];
        float[] weighted = new float[result.length];
        TilePos[] foundAt = new TilePos[result.length];

        // will try to fit 3 elements an should be able to
        boolean wasAbleTo = new UseAgent(repo).describeEnvironnement(scene, result, weighted, foundAt);
        Assert.assertTrue(wasAbleTo);

        // last one should by null (repo only contains 2)
        Assert.assertNull(result[2]);

        // d2 should be reWeight-ed as most interesting
        Assert.assertEquals("d1", result[0].tag);
        Assert.assertEquals("d2", result[1].tag);
        Assert.assertTrue(weighted[0] < weighted[1]);
    }

}
