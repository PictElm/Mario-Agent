package pii.marioagent;

import java.util.Random;

import org.marioai.engine.helper.MarioActions;

import pii.marioagent.agents.BaseAgent.AgentSettings;
import pii.marioagent.environnement.Description;

public class Settings {

    public final Random random;

    public final String testLevelFilePath;
    public final String descRepoFilePath;
    public final int trainingIterationCount;

    public final int[] randomActionGenerateMinMax;
    public final int[] randomActionExperimentMinMax;
    /** Number of action the agent can do (see {@link org.marioai.engine.helper.MarioActions}). */
    public final int numberOfAction;

    public final ReWeighter reWeighter;
    public final AlterationSettings alter;

    public final AgentSettings generateAgentSettings;
    public final AgentSettings experimentAgentSettings;
    public final AgentSettings userAgentSettings;
    public final AgentSettings trainedAgentSettings;

    public Settings(Random random, String testLevelFilePath, String descRepoFilePath, int trainingIterationCount, int[] randomActionGenerateMinMax, int[] randomActionExperimentMinMax, int numberOfAction, ReWeighter reWeighter, AlterationSettings alter, AgentSettings... agentSettings) {
        this.testLevelFilePath = testLevelFilePath;
        this.descRepoFilePath = descRepoFilePath;
        this.trainingIterationCount = trainingIterationCount;

        this.randomActionGenerateMinMax = randomActionGenerateMinMax;
        this.randomActionExperimentMinMax = randomActionExperimentMinMax;
        this.numberOfAction = numberOfAction;

        this.reWeighter = reWeighter;
        this.alter = alter;

        this.generateAgentSettings = agentSettings[0];
        this.experimentAgentSettings = agentSettings[1];
        this.userAgentSettings = agentSettings[2];
        this.trainedAgentSettings = agentSettings[3];

        this.random = random;
    }

    /**
     * previously used settings, as defaults
     */
    public Settings(String testLevelFilePath, String descRepoFilePath) {
        this(
            new Random(),

            !testLevelFilePath.equals("") ? testLevelFilePath : "./src/main/resources/levels/test.txt",
            !descRepoFilePath.equals("") ? descRepoFilePath : "./repo_save.csv",
            50,

            new int[] { 30, 30 + 1 },
            new int[] { 5, 5 + 1 },
            MarioActions.numberOfActions(),

            (Description it, Object... args) -> it.getWeight() - (float) args[0] /*min*/ + (float) args[1] /*max*/,
            new AlterationSettings(),

            new AgentSettings(20),
            new AgentSettings(5, 2 /* > 1 so that it will use random */),
            new AgentSettings(200),
            new AgentSettings(200)
        );
    }

    /**
     * previously used settings, as defaults
     */
    public Settings() {
        this("", "");
    }

    public final float random() {
        return this.random.nextFloat();
    }

    public static class AlterationSettings {

        /** Used when altering an Action; divided by the length of the source Action to get the probability of starting a chain of mutations. */
        public final double actionMutationPropagate;
        /** Used when altering an Action as a probability of keeping a chain of mutations going, or snapping back to the source Action. */
        public final double actionMutationRate;

        /** Divided by the sum of with and height of the source Description to get the probability of a cell changing from 0/1 to -1 or back again. */
        public final double descriptionMutationRate;
        /** Order in which the cross product between the altered grid and the scene should occur: <ul><li>-1: scene x altered<li>0: no crossing<li>1: altered x scene<ul> */
        public final int descriptionCrossDirection;
        /** Priority param given to the Description crossing function. */
        public final int descriptionCrossPriority;

        public AlterationSettings(double actionMutationPropagate, double actionMutationRate, double descriptionMutationRate, int descriptionCrossDirection, int descriptionCrossPriority) {
            this.actionMutationPropagate = actionMutationPropagate;
            this.actionMutationRate = actionMutationRate;

            this.descriptionMutationRate = descriptionMutationRate;
            this.descriptionCrossDirection = descriptionCrossDirection;
            this.descriptionCrossPriority = descriptionCrossPriority;
        }

        public AlterationSettings() {
            this(
                .5d,
                1d,

                1d,
                1,
                0
            );
        }

    }

    public interface ReWeighter {

        // TODO: `Object... args`? why so serious?
        public float reWeight(Description it, Object... args);

    }

}