package pii.marioagent.agents;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.marioai.engine.core.MarioAgent;
import org.marioai.engine.core.MarioForwardModel;
import org.marioai.engine.core.MarioGame;
import org.marioai.engine.core.MarioResult;
import org.marioai.engine.core.MarioTimer;
import org.marioai.engine.core.MarioRender.AddedRender;

import pii.marioagent.Main;
import pii.marioagent.environnement.Action;
import pii.marioagent.environnement.ForwardModel;

public abstract class BaseAgent implements MarioAgent {

    private Action currentAction;

    /**
     * Get the ongoing action if any or null.
     * @return the agent's action.
     */
    protected Action getCurrent() {
        return this.currentAction;
    }

    /**
     * Set a new ongoing action.
     * @param a action to be performed.
     */
    protected void setCurrent(Action a) {
        a.reset();
        this.currentAction = a;
    }

    /**
     * Does the agent have an ongoing action?
     * @return true if its the case.
     */
    protected boolean hasCurrent() {
        return this.currentAction != null && !this.currentAction.finished();
    }

    /**
     * Runs the agent's algorithm on the level from the provided file path.
     * <p> Settings from the agent itself are overridden by the provided ones.
     * @param levelFile path to the file containing the level.
     * @param settings the settings to use.
     * @param visual object to add a visual information.
     * @return the result of MarioGame.runGame.
     */
    public MarioResult run(String levelFile, AgentSettings settings, AddedRender... visual) {
        try {
            String level = new String(Files.readAllBytes(Paths.get(levelFile)));

            MarioGame game = settings.overrideStart ? new MarioGame(settings.startPercent < 0 ? Main.SETTINGS.random() : settings.startPercent, visual) : new MarioGame(visual);
            return game.runGame(this, level, settings.timer, settings.marioState, 0 < visual.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Runs the agent's algorithm on the level from the provided file path.
     * <p> Uses the settings provided by the agent's implementation itself.
     * @see BaseAgent.run(String, AgentSettings, AddedRender...)
     * @param levelFile path to the file containing the level.
     * @param visual object to add a visual information.
     * @return the result of MarioGame.runGame.
     */
    public MarioResult run(String levelFile, AddedRender... visual) {
        return this.run(levelFile, this.getSettings(), visual);
    }

    /**
     * Asks the agent for input given the game state.
     * @param model access to game state.
     * @return inputs to perform as a boolean array (0: Left, Right, Down, Speed, Jump).
     */
    protected abstract boolean[] feed(ForwardModel model);

    /**
     * Asks the agent for the specific setting expected.
     * @see BaseAgent.run(String, AddedRender...)
     * @return the settings to use.
     */
    protected abstract AgentSettings getSettings();

    @Override
    public void initialize(MarioForwardModel model, MarioTimer timer) {
        ;//System.out.println("BaseAgent.initialize (name: " + this.getAgentName() + " - " + this.getClass() + ")");
    }

    @Override
    public boolean[] getActions(MarioForwardModel model, MarioTimer timer) {
        return this.feed(new ForwardModel(model));
    }

    @Override
    public String getAgentName() {
        return "D/A";
    }

    public static class AgentSettings {

        public final int timer;
        public final int marioState;
        public final boolean overrideStart;
        public final float startPercent;

        public AgentSettings(int timer, float startPercent, int marioState) {
            this.timer = timer;
            this.marioState = marioState;
            this.overrideStart = 0 < startPercent;
            this.startPercent = startPercent;
        }

        /**
         * Default marioState is 0
         * @param timer
         * @param startPercent
         */
        public AgentSettings(int timer, float startPercent) {
            this(timer, startPercent, 0);
        }

        /**
         * Default marioState is 0
         * Default startPercent is 'do not override' (reads from level)
         * @param timer
         * @param startPercent
         */
        public AgentSettings(int timer) {
            this(timer, -1f);
        }

    }

}
