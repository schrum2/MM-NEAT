package edu.utexas.cs.nn.tasks.mspacman.agentcontroller.pacman;

import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.graphics.DrawingPanel;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.tasks.mspacman.multitask.MsPacManModeSelector;
import edu.utexas.cs.nn.tasks.mspacman.sensors.MsPacManControllerInputOutputMediator;
import edu.utexas.cs.nn.util.CombinatoricUtilities;
import edu.utexas.cs.nn.util.datastructures.Interval;
import java.awt.Graphics2D;

public class MultinetworkSelectorMsPacManController<T extends Network> extends MultinetworkMsPacManController {

    private int lastMode = -1;
    private final boolean animateNetwork;
    private final boolean limitedRecurrentMemory;

    public MultinetworkSelectorMsPacManController(Genotype<T>[] genotypes, MsPacManControllerInputOutputMediator[] inputMediators, MsPacManModeSelector modeSelector) {
        super(genotypes, inputMediators);
        this.ms = modeSelector;
        this.animateNetwork = Parameters.parameters.booleanParameter("animateNetwork");
        this.limitedRecurrentMemory = Parameters.parameters.booleanParameter("limitedRecurrentMemory");
    }

    @Override
    public void reset() {
        super.reset();
        if (CommonConstants.showNetworks && animateNetwork && TWEANN.panel != null) {
            TWEANN.panel.dispose();
        }
        lastMode = -1;
    }

    protected void drawModeUsage(GameFacade game, Graphics2D g, int[] actions, int action, int currentScale, int mode) {
        g.setColor(CombinatoricUtilities.colorFromInt(mode));
        if (lastMode != -1) {
            g.drawLine(scaledTime(game.getCurrentLevelTime() - 1), scaledMode(lastMode), scaledTime(game.getCurrentLevelTime()), scaledMode(mode));
        }
        g.fillRect(currentScale, scaledMode(mode), MODE_DOT_DIM, MODE_DOT_DIM);
    }

    protected int[][] getAllControllerActions(GameFacade game, long timeDue) {
        ms.giveGame(game);
        int mode = ms.mode();

        if (limitedRecurrentMemory && mode != lastMode) {
            for (Network net : networks) {
                net.flush();
            }
        }

        usage[mode]++;
        int[] actions = new int[inputMediators.length];
        for (int i = 0; i < inputMediators.length; i++) {
            nn = networks[i];
            inputMediator = inputMediators[i];
            actions[i] = parentAction(game, timeDue, !CommonConstants.minimalSubnetExecution || i == mode);
        }
        int action = actions[mode];

        int[][] result = new int[3][];
        result[0] = new int[]{action};
        result[1] = new int[]{mode};
        result[2] = actions;
        return result;
    }

    @Override
    protected void modeUpdate(GameFacade game, int mode) {
        if (mode != lastMode) {
            if (lastMode != -1) {
                ((Interval<Integer>) usageTimeFrames[lastMode].getLast()).t2 = game.getTotalTime();
            }
            usageTimeFrames[mode].add(new Interval<Integer>(true, game.getTotalTime(), Integer.MAX_VALUE, false)); // end to be filled in later

            nn = networks[mode];
            inputMediator = inputMediators[mode];
            //System.out.println(mode + ":" + nn.numInputs() + ":" + inputMediator.getClass().getSimpleName());
            if (CommonConstants.showNetworks && animateNetwork && nn instanceof TWEANN) {
                if (lastMode != -1) {
                    ((TWEANN) networks[lastMode]).canDraw = false;
                }
                if (TWEANN.panel != null) {
                    TWEANN.panel.dispose();
                }
                TWEANN.panel = new DrawingPanel(TWEANN.NETWORK_VIEW_DIM, TWEANN.NETWORK_VIEW_DIM, "Multinetwork " + mode);
                TWEANN.panel.setLocation(0, 0);
                ((TWEANN) nn).canDraw = true;
                ((TWEANN) nn).draw(TWEANN.panel);
            }
            lastMode = mode;
            //System.out.println("Mode: " + mode);
        }
    }
}
