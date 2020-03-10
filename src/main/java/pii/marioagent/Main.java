package pii.marioagent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import org.graphstream.graph.ElementNotFoundException;
import org.graphstream.graph.Graph;
import org.graphstream.graph.IdAlreadyInUseException;
import org.graphstream.graph.implementations.SingleGraph;

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

    public static final String TEST_LEVEL = "./src/main/resources/levels/test.txt";
    public static final boolean QUIET = true;

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

        for (int k = 0; k < 10; k++) {
            System.out.println("Iteration " + k + ".");

            // create a first agent to generate some descriptions
            new ExperimentAgent(new RandomAction(30), new Recorder(repo)).run(Main.TEST_LEVEL);

            if (!Main.QUIET) System.out.println(repo.count() + " entries to work with.");

            // create a new agent to use and evaluate the descriptions
            Statistics usage = new Statistics();
            new UseAgent(repo, usage).run(Main.TEST_LEVEL);
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

                if (!Main.QUIET) System.out.print(it.tag + ": from " + min + " to " + max);
                if (!Main.QUIET) System.out.println(" (" + it.getWeight() + " x " + it.getOccurences() + ")");

                // if it helped
                if (0 < max) {
                    save.add(it);
                    // keep it for next turn
                    repo.add(it);
                    // experiment on it
                    ExperimentAgent expage = new ExperimentAgent(new RandomAction(5), new OneRepository(it), null, TaskType.X_ACTION);
                    repo.add(expage.alterAction(it));
                    repo.add(expage.alterDescription(it));
                }
            }

            //save.save(Paths.get("./run/it" + k + ".txt"));

            if (!Main.QUIET) System.out.println("\n");
        }

        //save.save(Paths.get("./save.txt"));
        System.out.println("Repository size: " + save.count() + ".");
        Statistics visual = new Statistics();
        new UseAgent(save, visual).run(Main.TEST_LEVEL, visual);
    }

}
