package edu.utexas.cs.nn.tasks.mspacman.agentcontroller.pacman;

import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.graphics.DrawingPanel;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.tasks.mspacman.sensors.MsPacManControllerInputOutputMediator;
import edu.utexas.cs.nn.util.datastructures.ArrayUtil;
import edu.utexas.cs.nn.util.datastructures.Interval;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.LinkedList;

public abstract class MultinetworkMsPacManController<T extends Network> extends ReactiveNNPacManController {

    protected static DrawingPanel usageInfo = null;
    protected Network[] networks;
    protected MsPacManControllerInputOutputMediator[] inputMediators;
    protected static final int MODE_DOT_DIM = 3;
    protected static final int MODE_DOT_SPACE = 30;
    protected static final int SUBNET_INFO_HEIGHT = 200;
    protected static final int SUBNET_INFO_WIDTH = 700;
    protected static final int USAGE_INFO_PLACEMENT = 420;
    protected int disagree;
    protected int agree;
    protected int[] usage;
    public int[] fullUsage; // not reset on new level
    public LinkedList<Interval<Integer>>[] usageTimeFrames;

    @Override
    public String toString() {
        String result = this.getClass().getSimpleName();
        result += ":";
        for (int i = 0; i < networks.length; i++) {
            result += (networks[i] != null ? "Net" + i : "null") + ":";
        }
        for (int i = 0; i < inputMediators.length; i++) {
            result += inputMediators[i].getClass().getSimpleName() + ":";
        }
        return result;
    }

    public MultinetworkMsPacManController(Genotype<T>[] genotypes, MsPacManControllerInputOutputMediator[] inputMediators) {
        super(genotypes[0].getPhenotype());
        assert (genotypes.length == inputMediators.length) : "Genotypes length and mediators length are not the same";
        networks = new Network[genotypes.length];
        for (int i = 0; i < genotypes.length; i++) {
            networks[i] = genotypes[i].getPhenotype();
            assert inputMediators[i] != null : "Mediator " + i + " is null!";
        }
        this.inputMediators = inputMediators;
        this.inputMediator = inputMediators[0];
        if (usageInfo != null) {
            usageInfo.dispose();
        }
        if (CommonConstants.showSubnetAnalysis) {
            usageInfo = new DrawingPanel(SUBNET_INFO_WIDTH, SUBNET_INFO_HEIGHT, "Subnet Usage");
            usageInfo.setLocation(USAGE_INFO_PLACEMENT, 0);
        }
        this.agree = 0;
        this.disagree = 0;
        this.fullUsage = new int[inputMediators.length];
        this.usage = new int[inputMediators.length];
        for (Network n : networks) {
            ((TWEANN) n).canDraw = false;
        }
        this.usageTimeFrames = new LinkedList[inputMediators.length];
        for (int i = 0; i < usageTimeFrames.length; i++) {
            usageTimeFrames[i] = new LinkedList<Interval<Integer>>();
        }
    }

    public int parentAction(GameFacade game, long timeDue, boolean processNetwork) {
        return processNetwork ? super.getAction(game, timeDue) : -1;
    }

    @Override
    public int getAction(GameFacade game, long timeDue) {
        // Consistently perform reset across all of the networks.
        int checkLives = game.getPacmanNumberOfLivesRemaining();
        if (game.levelJustChanged() || checkLives < lives) {
            reset();
        }

        int[][] results = getAllControllerActions(game, timeDue);
        int action = results[0][0];
        int mode = results[1][0];
        if (mode == -1) { // For ensembles
            for (int i = 0; i < fullUsage.length; i++) {
                fullUsage[i]++;
            }
        } else {
            fullUsage[mode]++;
        }
        int[] actions = results[2];

        boolean justAgreed = trackAgreement(actions);

        if (CommonConstants.showSubnetAnalysis) {
            // Visually depict mode usage
            Graphics2D g = usageInfo.getGraphics();
            int currentScale = scaledTime(game.getCurrentLevelTime());
            //System.out.println(totalActions + ":" + currentScale);
            drawModeUsage(game, g, actions, action, currentScale, mode);
            drawAgreementAndPercents(justAgreed, g, game.getCurrentLevelTime(), currentScale);
        }

        modeUpdate(game, mode);
        return action;
    }

    @Override
    public void reset() {
        super.reset();
        //System.out.println("Full reset");
        for (Network n : networks) {
            n.flush();
        }
        for (MsPacManControllerInputOutputMediator m : inputMediators) {
            m.reset();
        }

        if (CommonConstants.showSubnetAnalysis) {
            usageInfo.clear();
        }
        if (CommonConstants.showNetworks) {
            for (Network n : networks) {
                ((TWEANN) n).canDraw = false;
            }
        }
        this.usage = new int[inputMediators.length];
    }

    protected int scaledTime(int currentLevelTime) {
        return (int) ((currentLevelTime / CommonConstants.pacManLevelTimeLimit) * (SUBNET_INFO_WIDTH - 20)) + 10;
    }

    protected int scaledMode(int mode) {
        return MODE_DOT_SPACE * (1 + mode);
    }

    protected boolean trackAgreement(int[] actions) {
        boolean justAgreed = ArrayUtil.allSame(actions);
        if (justAgreed) {
            agree++;
        } else {
            disagree++;
        }
        return justAgreed;
    }

    protected void drawAgreementAndPercents(boolean justAgreed, Graphics2D g, int totalActions, int currentScale) {
        if (!justAgreed) {
            g.setColor(Color.RED);
        } else {
            g.setColor(Color.WHITE);
        }
        g.fillRect(currentScale, scaledMode(inputMediators.length), MODE_DOT_DIM, MODE_DOT_DIM);
        for (int i = 0; i < usage.length; i++) {
            g.setColor(Color.WHITE);
            g.fillRect(SUBNET_INFO_WIDTH - 30, scaledMode(i) - 15, 30, 15);
            g.setColor(Color.BLACK);
            g.drawString("" + ((usage[i] * 100.0) / totalActions), SUBNET_INFO_WIDTH - 30, scaledMode(i));
        }
        g.setColor(Color.WHITE);
        g.fillRect(SUBNET_INFO_WIDTH - 30, scaledMode(usage.length) - 15, 30, 15);
        g.setColor(Color.BLACK);
        g.drawString("" + ((disagree * 100.0) / (agree + disagree)), SUBNET_INFO_WIDTH - 30, scaledMode(usage.length));

    }

    protected abstract void drawModeUsage(GameFacade game, Graphics2D g, int[] actions, int action, int currentScale, int mode);

    protected abstract int[][] getAllControllerActions(GameFacade game, long timeDue);

    // Really only used by selector, not by ensemble
    protected void modeUpdate(GameFacade game, int mode) {
    }
}
