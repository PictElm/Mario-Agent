package tests.units;

import environnement.Action;
import environnement.RandomAction;

public class ActionTest extends Action {

    public ActionTest() {
        super("");
    }

    public void testLoadSave() {
        String testString = "5";
        Action a = new Action(testString);

        while (!a.finished()) {
            for (boolean btnp : a.consume())
                System.out.print(btnp ? "1" : "0");
            System.out.println();
        }

        System.out.println(testString);
        System.out.println(a);
    }

    public void testRandomAction() {
        RandomAction random = new RandomAction(2, 4);

        for (int k = 0; k < 12; k++) {
            Action a = random.nextAction();
            System.out.println(a);
        }
    }

    public static void main(String[] args) {
        ActionTest o = new ActionTest();

        //o.testLoadSave();
        o.testRandomAction();
    }

}
