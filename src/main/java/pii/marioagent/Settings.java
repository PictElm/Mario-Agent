package pii.marioagent;

import java.util.Random;

import org.marioai.engine.helper.MarioActions;

import pii.marioagent.agents.BaseAgent.AgentSettings;
import pii.marioagent.environnement.Description;

public class Settings {

    public final Random random;

    public final String testLevelFilePath;
    public final int trainingIterationCount;

    public final int[] randomActionGenerateMinMax;
    public final int[] randomActionExperimentMinMax;
    public final int numberOfAction;

    public final ReWeighter reWeighter;

    public final AgentSettings generateAgentSettings;
    public final AgentSettings experimentAgentSettings;
    public final AgentSettings userAgentSettings;
    public final AgentSettings trainedAgentSettings;

    public Settings(Random random, String testLevelFilePath, int trainingIterationCount, int[] randomActionGenerateMinMax, int[] randomActionExperimentMinMax, int numberOfAction, ReWeighter reWeighter, AgentSettings... agentSettings) {
        this.testLevelFilePath = testLevelFilePath;
        this.trainingIterationCount = trainingIterationCount;

        this.randomActionGenerateMinMax = randomActionGenerateMinMax;
        this.randomActionExperimentMinMax = randomActionExperimentMinMax;
        this.numberOfAction = numberOfAction;

        this.reWeighter = reWeighter;

        this.generateAgentSettings = agentSettings[0];
        this.experimentAgentSettings = agentSettings[1];
        this.userAgentSettings = agentSettings[2];
        this.trainedAgentSettings = agentSettings[3];

        this.random = random;
    }

    /**
     * previously used settings, as defaults
     */
    public Settings() {
        this(
            new Random(),

            "./src/main/resources/levels/test.txt",
            50,

            new int[] { 30, 30 + 1 },
            new int[] { 5, 5 + 1 },
            MarioActions.numberOfActions(),

            (Description it, Object... args) -> it.getWeight() - (float) args[0] /*min*/ + (float) args[1] /*max*/,

            new AgentSettings(20),
            new AgentSettings(5, -1),
            new AgentSettings(20),
            new AgentSettings(200)
        );
    }

    public final float random() {
        return this.random.nextFloat();
    }

    public interface ReWeighter {

        // TODO: `Object... args`? why so serious?
        public float reWeight(Description it, Object... args);

    }

}