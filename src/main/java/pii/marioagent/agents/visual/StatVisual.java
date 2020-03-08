package pii.marioagent.agents.visual;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.JFrame;

import pii.marioagent.environnement.Description;

public class StatVisual extends JComponent {

    private static final long serialVersionUID = 61802L;

    private JFrame window;

    public StatVisual() {
        Dimension size = new Dimension(16 * 20, 16 * 20);

        super.setPreferredSize(size);
        super.setMinimumSize(size);
        super.setMaximumSize(size);
        super.setSize(size);

        super.setFocusable(true);

        this.window = new JFrame("Statistics");
        this.window.setContentPane(this);
        this.window.pack();
        this.window.setResizable(false);
        this.window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.window.setLocation(720, 102);
        this.window.setVisible(true);
    }

    public void renderDescriptions(Description[] des) {
        Graphics g = super.getGraphics();

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 16 * 20, 16 * 20);

        Color[] palette = new Color[] { Color.RED, Color.GREEN, Color.BLUE };

        for (int k = 0; k <3; k++) {
            if (des[k] != null) {
                g.setColor(palette[k]);
                for (int i = 0; i < des[k].width; i++)
                    for (int j = 0; j < des[k].height; j++)
                        if (des[k].getAt(i, j) != 0)
                            g.fillRect(i * 20, j * 20, 20, 20);
            }
        }
    }

    public void renderText(String text) {
        Graphics g = super.getGraphics();

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 16 * 20, 16 * 20);

        g.setColor(Color.BLACK);
        g.drawString(text, 42, 42);
    }

}
