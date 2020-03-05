package tests;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import org.marioai.engine.core.MarioGame;

import agents.BaseAgent;
import agents.UseAgent;
import agents.meta.Statistics;
import environnement.Description;
import environnement.repository.FileRepository;

public class ExperimentationTest {

    public static String getLevel(String filePath) {
        String r = "";

        try {
            r = new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return r;
    }

    public static Statistics runOnce(String level, String fileRepository) {
        Statistics r = new Statistics();

        try {
            // use the descriptions loaded in the FileRepository to make choices
            BaseAgent agent = new UseAgent(new FileRepository(Paths.get(fileRepository)), r);

            // run the simulation
            new MarioGame().runGame(agent, level, 20, 0, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return r;
    }

    public static void saveBest(Statistics from, String fileRepository) {
        List<Entry<Description, ArrayList<Float>>> entries = from.getBests();
        ArrayList<Description> des = new ArrayList<Description>();

        for (int k = 0; k < entries.size(); k++) {
            if (0 < Collections.max(entries.get(k).getValue())) {
                Description old = entries.get(k).getKey();
                des.add(new Description(old.getGrid(), old.getPreferredLocation(), old.getWeight() + (entries.size() - k), 0, old.getAction()));
            } else break;
        }

        try {
            // save the recorded actions raw (as a FileRepository)
            String save = String.join("\n", FileRepository.getStringDump(des.toArray(new Description[des.size()])));

            Files.write(Paths.get(fileRepository), save.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String level = ExperimentationTest.getLevel("tests/lvl-1.txt");

        Statistics rec = ExperimentationTest.runOnce(level, "./tests/generated.txt");
        ExperimentationTest.saveBest(rec, "./tests/results.txt");
    }

}
