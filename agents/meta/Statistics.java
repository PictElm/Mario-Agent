package agents.meta;

import environnement.Description;
import environnement.ForwardModel;

public class Statistics {

    private float percent;
    private Description choice;
    private Description previous;

    private Description best;
    private float bestPercent;

    public Statistics() {
        ;
    }

    /**
     * Used to follow the progress of the agent.
     * @param model environnement.
     */
    public void progressReport(ForwardModel model) {
        if (this.previous != null) {
            float newPercent = model.getCompletionPercentage();
            float diff = newPercent - this.percent;

            System.out.print((diff < 0 ? "" : "+") + 100 * diff + "% by doing: '");
            System.out.println(this.previous.getAction() + "'");

            if (this.bestPercent < diff) {
                this.best = this.previous;
                this.bestPercent = diff;
            }

            this.percent = newPercent;
            this.previous = null;
        }
    }

    /**
     * Records the elements the agent used to describe its environnement and which it chose as most important.
     * @param choices the description the agent found fitting.
     * @param which the description the agent found most fitting.
     */
    public void choiceReport(Description[] choices, int which) {
        this.previous = this.choice;
        this.choice = choices[which];
    }

    /**
     * Records any other noticeable events.
     * @param status the status to report.
     */
    public void statusReport(Statistics.Status status) {
        ; //System.out.println(status + " reported");
    }

    public enum Status {
        DEAD_END
    }

    public Description getBest() {
        return this.best;
    }

    public float getBestPercent() {
        return this.bestPercent;
    }

}
