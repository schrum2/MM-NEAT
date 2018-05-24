package edu.utexas.cs.nn;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * This is a "constants with default values" class.
 *
 * To define custom values for these constants, use System.setProperty() or the
 * -D parameter to the JVM. For example, it's possible to set the MOVEMENT_SPEED
 * by specifying:
 *
 *    java ... -DMOVEMENT_SPEED=150
 *
 * @author Igor Karpov (ikarpov@cs.utexas.edu)
 */
public enum Constants {
    /**
     * Print debugging messages
     */
    DEBUG(false),
    
    /**
     * Tells the bot to print out its actions
     */
    PRINT_ACTIONS(true),

    /**
     * The name of the bot
     */
    BOT_NAME("UT^2"),

    /**
     * Number of steps the bot will take when following a human trace path
     * 0 means the bot will restart a random trace every time
     */
    PATH_LENGTH(10),

    /**
     * The amount of time (in Unreal seconds) the bot is willing to wait
     * between steps along a human trace path. The bot will use the time
     * difference in order to schedule the next step, but it does not
     * make sense to schedule steps that are too far apart because there may
     * not be a way to get from one to the next.
     */
    HUMAN_TRACE_ATTENTION_SPAN(1.0),

    /**
     * URL when using the MySQL database
     */
    DATABASE_URL("jdbc:mysql://localhost/botprize2010"),

    /**
     * username when using the MySQL database
     */
    DATABASE_USERNAME("botprize"),

    /**
     * Password when using the MySQL database
     */
    DATABASE_PASSWORD("humans-r-us"),

    /**
     * Human data file when using SQLite
     */
    HUMAN_DB("botprize2010.db"),

    /**
     * Human data directory when using plain files
     */
    HUMAN_DATA_PATH("DATA/human/Levels"),

    /**
     * Bot DB when recording heatmaps (timestamped) or reading pre-recorded
     * heatmaps and using SQLite
     */
    BOT_DB("heatmap.db"),

    /**
     * Unreal/GameBots2004 host for LoggingListener and similar connections
     */
    UNREAL_HOST("localhost"),

    /**
     * Unreal/GameBots2004 control port for LoggingListener and similar connections
     */
    UNREAL_PORT(3001),

    /**
     * Unreal/GameBots2004 observer port for LoggingListener and similar connections
     */
    UNREAL_OBSERVER_PORT(3002),

    /**
     * The maximum depth of the OcTree index generated over the pose recordings
     */
    OCTREE_DEPTH(6),

    /**
     * How many pose records are good enough to make an OcTree leaf out of
     */
    OCTREE_THRESH(15),
    
    /**
     * If true, the bot's heatmap will be written to a heatmap-TIMESTAMP.db file
     */
    RECORDING(false),

    /**
     * Whether to use the NavPointTree instead of the OcTree as the spatial index
     * in the HumanTraceController
     */
    NAVPOINT_HTC(true),

    /**
     * Whether or not the bot generates and uses human-based event predictions
     */
    HUMAN_PREDICT(false),

    /**
     * Whether or not the bot generates and uses history-based event predictions
     */
    BOT_PREDICT(false),

    /**
     * Whether or not to view the debugging output of the HumanRetraceController
     */
    VIEW_HRTC_MESSAGES(false),

    /** the distance factor for HRTC trace estimates */
    HRTC_DISTANCE_FACTOR("1.0"),

    /**
     * How many seconds RETRACE is disabled if it fails
     */
    RETRACE_REBOOT_DELAY("2.0"),

    /**
     * Whether the bot prints out performance/timing information in the logic() calls
     */
    TIMING(false),

    /**
     * Whether the bot uses the human trace controller to navigate paths (exp)
     */
    HUMAN_TRACE_PATH_CONTROLLER(false),

    /**
     * Don't keep planning new paths to the same thing
     */
    ITEM_IGNORE_TIME(10),
    
    /**
     * When bot is close enough to item to simply go directly to it
     */
    VERY_CLOSE_DISTANCE(500),

    
    /**
     * How little progress does the bot have to make within PROGRESS_TIME1 to be
     * considered stuck for lack of progress
     */
    PROGRESS_THRESHOLD_1(40.0),
    PROGRESS_TIME_1(0.3),

    /**
     * How little progress does the bot have to make within PROGRESS_TIME2 to be
     * considered stuck for lack of progress
     */
    PROGRESS_THRESHOLD_2(40.0),
    PROGRESS_TIME_2(1.0),

    /**
     * How little progress does the bot have to make within PROGRESS_TIME3 to be
     * considered stuck for lack of progress
     */
    PROGRESS_THRESHOLD_3(40.0),
    PROGRESS_TIME_3(3.0),
    
    /**
     * amount of time to track nearest enemy (if contact is lost)
     */
    MEMORY_TIME(20.0),
    
    /**
     * don't fight if below this health
     */
    MINIMUM_BATTLE_HEALTH(30),
    
    /**
     * don't fight if farther than this from opponent
     */
    MAX_BATTLE_DISTANCE(3000),
    
    /**
     * items within this distance can be easily picked up
     */
    NEAR_ITEM_DISTANCE(1000),
    
    /**
     * if the height diff to the item is less, can be easily picked up
     */
    NEAR_ITEM_HEIGHT(100),
    
    LOG_CONTROLLER_EVALUATIONS(true),
    
    CONTROLLER_EVALUATIONS_NAME("evaluations"),

    HUMAN_TRACE_DISTANCE_THRESHOLD(1000.0),
    
    /**
     * Minimal distance (in game units) to advance the trace
     */
    MIN_DISTANCE_SKIP(50),
    
    /**
     * Steps that a trace stays in time out corner
     */
    TRACE_TIME_OUT_DURATION(2),

    /**
     * Minimal time in seconds to advance the trace
     */
    MIN_TIME_SKIP(0.1),
    
    /**
     * the decay of the distance estimate
     */
    ESTIMATE_DECAY(0.9);


    private String defaultValue;

    Constants(Object o) {
        this.defaultValue = String.valueOf(o);
    }

    public String get() {
        return System.getProperty(name(), defaultValue);
    }

    public int getInt() {
        return Integer.valueOf(get());
    }

    public double getDouble() {
        return Double.valueOf(get());
    }

    public float getFloat() {
        return Float.valueOf(get());
    }

    public boolean getBoolean() {
        return Boolean.valueOf(get());
    }

    public static final Random random = new Random();

    public static String timestamp(String filename) {
        if (filename.endsWith(".db")) {
            filename = filename.substring(0,filename.length() - 3);
        }
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("-yyyyMMdd-HHmmss");
        filename = filename + df.format(date) + ".db";
        return filename;
    }
}
