package agents.meta;

import org.marioai.engine.core.MarioForwardModel;

import environnement.Description;

public class Statistics {

    public Statistics() {
        ;
    }

    /**
     * Used to follow the progress of the agent.
     * @param model environnement.
     */
    public void progressReport(MarioForwardModel model) {
        ;
    }

    /**
     * Records the elements the agent used to describe its environnement and which it chose as most important.
     * @param choices the description the agent found fitting.
     * @param which the description the agent found most fitting.
     */
    public void choiceReport(Description[] choices, int which) {
        ;
    }

}
