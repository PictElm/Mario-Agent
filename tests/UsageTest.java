package tests;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import org.marioai.engine.core.MarioGame;
import org.marioai.engine.core.MarioResult;

import agents.BaseAgent;
import agents.UseAgent;
import agents.meta.Statistics;
import environnement.Description;
import environnement.repository.FileRepository;

public class UsageTest {

    public static void printResults(MarioResult result) {
        System.out.println("****************************************************************");
        System.out.println("Game Status: " + result.getGameStatus().toString() + " Percentage Completion: " + result.getCompletionPercentage());
        System.out.println("Lives: " + result.getCurrentLives() + " Coins: " + result.getCurrentCoins() + " Remaining Time: " + (int) Math.ceil(result.getRemainingTime() / 1000f));
        System.out.println("Mario State: " + result.getMarioMode() + " (Mushrooms: " + result.getNumCollectedMushrooms() + " Fire Flowers: " + result.getNumCollectedFireflower() + ")");
        System.out.println("Total Kills: " + result.getKillsTotal() + " (Stomps: " + result.getKillsByStomp() + " Fireballs: " + result.getKillsByFire() + " Shells: " + result.getKillsByShell() + " Falls: " + result.getKillsByFall() + ")");
        System.out.println("Bricks: " + result.getNumDestroyedBricks() + " Jumps: " + result.getNumJumps() + " Max X Jump: " + result.getMaxXJump() + " Max Air Time: " + result.getMaxJumpAirTime());
        System.out.println("****************************************************************");
    }

    public static String getLevel(String filePath) {
        String r = "";

        try {
            r = new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return r;
    }

    public static void main(String[] args) {
        MarioGame game = new MarioGame();
        String level = UsageTest.getLevel("tests/lvl-1.txt");

        // record the choices made to determine most interesting descriptions
        Statistics rec = new Statistics();
        BaseAgent agent;
        try {
            // use the descriptions loaded in the FileRepository to make choices
            agent = new UseAgent(new FileRepository(Paths.get("./tests/generated.txt")), rec);

            game.runGame(agent, level, 20, 0, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Entry<Description, ArrayList<Float>>> bests = rec.getBests(3);
        System.out.println("Best 3 choices were: ");
        for (Entry<Description, ArrayList<Float>> it : bests)
            System.out.println("\t'" + it.getKey().getAction() + "' (up to +" + 100 * Collections.max(it.getValue()) + "%)");
    }

}
