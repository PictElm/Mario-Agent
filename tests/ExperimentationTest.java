package tests;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map.Entry;

import org.marioai.engine.core.MarioAgent;
import org.marioai.engine.core.MarioGame;
import org.marioai.engine.core.MarioResult;

import agents.BaseAgent;
import agents.ExperimentAgent;
import agents.ExperimentAgent.TaskType;
import agents.UseAgent;
import agents.meta.Recorder;
import agents.meta.Statistics;
import environnement.Description;
import environnement.RandomAction;
import environnement.repository.BaseRepository;
import environnement.repository.FileRepository;

public class ExperimentationTest {

    public static MarioResult run(MarioAgent agent) {
        try {
            String level = new String(Files.readAllBytes(Paths.get("tests/lvl-1.txt")));
            return new MarioGame().runGame(agent, level, 20, 0, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        BaseRepository repo = new FileRepository();

        // create a first agent to generate some descriptions
        BaseAgent agentGenerate = new ExperimentAgent(new RandomAction(30), new Recorder(repo));
        ExperimentationTest.run(agentGenerate);

        // create a new agent to use the generated descriptions
        Statistics useSta = new Statistics();
        BaseAgent agentUse = new UseAgent(repo, useSta);
        ExperimentationTest.run(agentUse);

        // analyse the usage statistics of the descriptions and make selections
        for (Entry<Description, ArrayList<Float>> pair : useSta.getBests())
            if (Collections.max(pair.getValue()) <= 0)
                repo.remove(pair.getKey());

        // create another agent to experiment on those descriptions
        BaseAgent agentExperiment = new ExperimentAgent(new RandomAction(5), repo, new Recorder(repo), TaskType.X_ACTION);
        ExperimentationTest.run(agentExperiment);
    }

}
