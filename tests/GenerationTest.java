package tests;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.marioai.engine.core.MarioGame;
import org.marioai.engine.core.MarioResult;

import agents.BaseAgent;
import agents.ExperimentAgent;
import agents.meta.Recorder;
import environnement.Description;
import environnement.RandomAction;
import environnement.repository.FileRepository;

public class GenerationTest {

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
        String level = GenerationTest.getLevel("tests/lvl-1.txt");

        // create a random agent and record its actions
        Recorder rec = new Recorder();
        BaseAgent agent = new ExperimentAgent(new RandomAction(30), rec);

        game.runGame(agent, level, 20, 0, true);

        // save the recorded actions raw (as a FileRepository)
        Description[] des = rec.getRecords();
        System.out.println("Generated " + des.length + " description/action");

        String save = String.join("\n", FileRepository.getStringDump(des));

        try {
            Files.write(Paths.get("./tests/generated.txt"), save.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
