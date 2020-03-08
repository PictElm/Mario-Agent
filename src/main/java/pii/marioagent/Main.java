package pii.marioagent;

import java.awt.Graphics;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import org.graphstream.graph.ElementNotFoundException;
import org.graphstream.graph.Graph;
import org.graphstream.graph.IdAlreadyInUseException;
import org.graphstream.graph.implementations.SingleGraph;
import org.marioai.engine.core.MarioAgent;
import org.marioai.engine.core.MarioGame;
import org.marioai.engine.core.MarioResult;
import org.marioai.engine.core.MarioRender;
import org.marioai.engine.core.MarioRender.AddedRender;

import pii.marioagent.agents.ExperimentAgent;
import pii.marioagent.agents.ExperimentAgent.TaskType;
import pii.marioagent.agents.UseAgent;
import pii.marioagent.agents.meta.Recorder;
import pii.marioagent.agents.meta.Statistics;
import pii.marioagent.environnement.Description;
import pii.marioagent.environnement.RandomAction;
import pii.marioagent.environnement.repository.FileRepository;
import pii.marioagent.environnement.repository.OneRepository;

public class Main {

    public static final boolean quiet = true;

    public static MarioResult run(MarioAgent agent, AddedRender... visual) {
        try {
            String level = new String(Files.readAllBytes(Paths.get("./src/main/res/levels/test.txt")));
            return new MarioGame(visual).runGame(agent, level, 20, 0, 0 < visual.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void addNode(Graph graph, Description d) {
        Description p = d.getFrom();
        try {
            graph.addNode(d.tag).setAttribute("label", d.tag);
            if (p != null)
                graph.addEdge(p.tag + "-" + d.tag, p.tag, d.tag).setAttribute("layout.weight", 2);
        } catch (ElementNotFoundException e) {
            Main.addNode(graph, p);
            graph.addEdge(p.tag + "-" + d.tag, p.tag, d.tag).setAttribute("layout.weight", 2);
        } catch (IdAlreadyInUseException e) {}
    }

    public static void removeNode(Graph graph, Description d) {
        try {
            graph.removeNode(d.tag);
        } catch (ElementNotFoundException e) {}
    }

    public static void main(String[] args) throws IOException {
        FileRepository repo = new FileRepository();
        FileRepository save = null;

        Graph graph = new SingleGraph("g1");
        graph.display(true);

        for (int k = 0; k < 100; k++) {
            System.out.println("Iteration " + k + ".");

            // create a first agent to generate some descriptions
            Main.run(new ExperimentAgent(new RandomAction(30), new Recorder(repo)));

            if (!Main.quiet) System.out.println(repo.count() + " entries to work with.");

            // create a new agent to use and evaluate the descriptions
            Statistics usage = new Statistics();
            Main.run(new UseAgent(repo, usage));
            List<Entry<Description, ArrayList<Float>>> bests = usage.getBests();

            for (Description it : repo.getFirst(repo.count())) {
                boolean found = false;
                for (Entry<Description, ArrayList<Float>> pair : bests)
                    if (pair.getKey() == it) {
                        found = true;
                        break;
                    }
                if (!found)
                    Main.removeNode(graph, it);
                else
                    Main.addNode(graph, it);
            }

            save = new FileRepository();
            repo = new FileRepository();

            // for each of the entries
            int c = 0;
            for (Entry<Description, ArrayList<Float>> pair : bests) {
                Description it = pair.getKey();
                float min = Collections.min(pair.getValue());
                float max = Collections.max(pair.getValue());

                //it.setWeight(it.getWeight() + 10f / ++c);
                //it.setWeight(it.getWeight() - ++c);
                //it.setWeight(it.getWeight() + 1f / (it.getOccurences() + 1));
                it.setWeight(it.getWeight() + min);

                if (!Main.quiet) System.out.print(it.tag + ": from " + min + " to " + max);
                if (!Main.quiet) System.out.println(" (" + it.getWeight() + " x " + it.getOccurences() + ")");

                // if it helped
                if (0 < max) {
                    save.add(it);
                    // keep it for next turn
                    repo.add(it);
                    // experiment on it
                    Main.run(new ExperimentAgent(new RandomAction(5), new OneRepository(it), new Recorder(repo), TaskType.X_ACTION));
                }
            }

            //save.save(Paths.get("./run/it" + k + ".txt"));

            if (!Main.quiet) System.out.println("\n");
        }

        //save.save(Paths.get("./save.txt"));
        System.out.println("Repository size: " + save.count() + ".");
        Statistics visu = new Statistics();
        Main.run(new UseAgent(save, visu), visu);
    }

}
