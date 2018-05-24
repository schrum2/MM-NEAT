package edu.utexas.cs.nn.bots;

import cz.cuni.amis.pogamut.base.utils.guice.AgentScoped;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.utils.UT2004BotRunner;
import cz.cuni.amis.utils.exception.PogamutException;
import edu.utexas.cs.nn.Constants;
import edu.utexas.cs.nn.Point;
import edu.utexas.cs.nn.bots.BaseBot;
import edu.utexas.cs.nn.retrace.PoseSequence;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;


/**
 * This bot moves through a sequence of time, pose points and collects the
 * ego-centric sensor data corresponding to each
 */
@AgentScoped
public class HumanRetraceBot extends BaseBot {
    
    /** how long is too long to wait between samples */
    public static final double TIME_DISCONTINUITY = 5.0;

    /** how far is too far between samples */
    public static final double SPACE_DISCONTINUITY = 250; //100.0;

    private List<PoseSequence> sequences = new LinkedList<PoseSequence>();

    private boolean initialized = false;

    @Override
    public void prepareBot(UT2004Bot bot) throws PogamutException {
        super.prepareBot(bot);
    }

    private void myInitialize() {
        if (initialized)
            return;
        else
            initialized = true;
        // reading pose sequences
        File traceHome = new File(Constants.HUMAN_DATA_PATH.get());
        if (this.game == null || this.game.getMapName() == null) {
            this.getLog().severe("Could not find level-specific pose data");
            return;
        }
        File levelSpecificTraceHome = new File(traceHome, this.game.getMapName());
        if (!levelSpecificTraceHome.exists()) {
            this.getLog().log(Level.SEVERE, "Could not find level-specific pose data for " + this.game.getMapName());
            return;
        }
        File[] subdirs = levelSpecificTraceHome.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });
        if (subdirs == null) return;
        for (File subdir : subdirs) {
            File[] dataFiles = subdir.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.getName().endsWith(".dat");
                }
            });

            if (dataFiles != null) {
                for (File dataFile : dataFiles) {
                    System.out.println(dataFile.getName());
                    try {
                        BufferedReader reader = new BufferedReader(new FileReader(dataFile));
                        sequences.addAll(PoseSequence.readSequences(reader));
                    } catch (FileNotFoundException ex) {
                        this.getLog().log(Level.SEVERE, "Data file not found", ex);
                    } catch (IOException ioe) {
                        this.getLog().log(Level.SEVERE, "Error while reading pose data file", ioe);
                    }
                }
            }

            System.out.println("done reading sequences: " + sequences.size() + " in subdir " + subdir);
        }
    }

    private Iterator<PoseSequence> sequenceIter = null;

    private PoseSequence sequence = null;

    private double lastTime = -1;

    @Override
    public void logic() throws PogamutException {
        super.logic();
        myInitialize();
        double levelTime = this.game.getTime();
        double speed = this.bot.getVelocity().size();
        double skipTime = 0;
        if (lastTime > 0) {
            skipTime = levelTime - lastTime;
        }
        double distance_estimate = skipTime * speed;
        if (sequence != null && sequence.hasNext()) {
            Point pose;
            if (distance_estimate > 0) {
                pose = sequence.nextByDistance(distance_estimate);
            } else {
                pose = sequence.nextByTime(skipTime);
            }
            
            Location current = this.getAgentMemory().getAgentLocation().getLocation();
            Location next = pose.getLocation();
            if (next.getDistance(current) > SPACE_DISCONTINUITY) {
                System.out.println("RESPAWN: space discontinuity");
                this.body.getAction().respawn(pose.getLocation(), pose.getRotation());
            } else {
                this.move.moveTo(pose.getLocation());
            }
            System.out.println("current: " + current + " next: " + next);
            System.out.println("level: " + levelTime + " data: " + pose.getT() + " skip: " + skipTime);
        } else {
            if (sequenceIter == null) {
                sequenceIter = sequences.iterator();
            }
            if (sequenceIter.hasNext()) {
                sequence = sequenceIter.next();
                if (sequence.hasNext()) {
                    Point pose = sequence.nextByTime(skipTime);
                    this.body.getAction().respawn(pose.getLocation(), pose.getRotation());
                }
            }
        }
        lastTime = levelTime;
    }

    public static void main(String args[]) throws PogamutException {
        new UT2004BotRunner(HumanRetraceBot.class, "HumanRetraceBot").setMain(true).startAgent();
    }
}
