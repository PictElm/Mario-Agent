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

import pii.marioagent.environnement.Action;
import pii.marioagent.environnement.ForwardModel;

public abstract class BaseAgent implements MarioAgent {

    private Action currentAction;

    protected Action getCurrent() {
        return this.currentAction;
    }

    protected void setCurrent(Action a) {
        a.reset();
        this.currentAction = a;
    }

    protected boolean hasCurrent() {
        return this.currentAction != null && !this.currentAction.finished();
    }

    public MarioResult run(String levelFile, AddedRender... visual) {
        try {
            String level = new String(Files.readAllBytes(Paths.get(levelFile)));
            
            AgentSettings s = this.getSettings();
            MarioGame game = s.overrideStart ? new MarioGame(s.startPercent, visual) : new MarioGame(visual);
            return game.runGame(this, level, s.timer, s.marioState, 0 < visual.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Ask the agent for input given the game state.
     * @param model access to game state.
     * @return inputs to perform as a boolean array (0: Left, Right, Down, Speed, Jump).
     */
    protected abstract boolean[] feed(ForwardModel model);

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

    protected class AgentSettings {

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

        public AgentSettings(int timer, float startPercent) {
            this(timer, startPercent, 0);
        }

        public AgentSettings(int timer) {
            this(timer, -1f);
        }

    }

}
