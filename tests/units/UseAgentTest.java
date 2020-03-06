package tests.units;

import agents.UseAgent;
import environnement.Description;
//import environnement.repository.StubRepository;
import environnement.utils.TilePos;

public class UseAgentTest extends UseAgent {

    private int[][] testScene;

    public UseAgentTest() {
        super(null/*new StubRepository()*/, null);

        this.testScene = new int[][] {
            { 0, 0, 1, 1, 0 },
            { 0, 0, 1, 0, 0 },
            { 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0 }
        };
    }

    public void testFitDescription() {
        Description d = super.prov.getFirst(1)[0];
        TilePos[] r = UseAgent.fitDescription(d, this.testScene);

        System.out.println(d);
        System.out.println(r[0]);
    }

    public void testDescribeEnvironnement() {
        Description[] result = new Description[3];
        float[] weighted = new float[result.length];
        describeEnvironnement(this.testScene, result, weighted, null);

        for (int k = 0; k < result.length; k++) {
            System.out.print(weighted[k]);
            System.out.print(": ");
            System.out.println(result[k]);
        }
    }

    public static void main(String[] args) {
        UseAgentTest o = new UseAgentTest();

        //o.testFitDescription();
        o.testDescribeEnvironnement();
    }

}
