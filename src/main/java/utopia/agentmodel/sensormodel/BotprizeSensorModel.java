package utopia.agentmodel.sensormodel;

import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.AutoTraceRay;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import mockcz.cuni.pogamut.Client.AgentBody;
import mockcz.cuni.pogamut.Client.AgentMemory;
import mockcz.cuni.pogamut.MessageObjects.Triple;
import java.util.ArrayList;
import utopia.Utils;

public class BotprizeSensorModel extends SensorModel {

    public static final double PI = Math.PI;
    private double[] sliceLimits;
    private int secondsHistory;
    private int crosshairID;
    private double rayLength = 5000;
    private int levelRaySensors;
    private int airRaySensors;
    private int groundRaySensors;
    protected double frontLeftDist;
    protected double frontRightDist;
    protected boolean playerInCrosshair;

    public BotprizeSensorModel(int levelTraces, int airTraces, int groundTraces, double[] sliceLimits, int secondsHistory) {
        this.levelRaySensors = levelTraces;
        this.airRaySensors = airTraces;
        this.groundRaySensors = groundTraces;
        this.sliceLimits = sliceLimits;
        this.secondsHistory = secondsHistory;
    }

    public BotprizeSensorModel() {
        this(12, 6, 4, new double[]{0, PI / 128, PI / 32, PI / 4, PI / 2, PI}, 3);
    }

    @Override
    public void prepareSensors(AgentBody body) {
        body.removeAllRaysFromAutoTrace();

        // Ray straight ahead traces actors
        this.crosshairID = 0;
        body.addRayToAutoTrace(crosshairID, new Triple(1, 0, 0), rayLength, false, true);
        int numRays = 1;

        double angle;
        double x;
        double y;
        // Rays parallel to even ground
        angle = (2 * Math.PI) / this.levelRaySensors;
        for (int i = 0; i < levelRaySensors; i++) {
            x = Math.cos(angle * i);
            y = Math.sin(angle * i);
            //Carefull, not tracing actors here!
            body.addRayToAutoTrace(numRays, new Triple(x, y, 0), rayLength, false, false);
            numRays++;
        }

        double liftAngle;
        double z;

        // Rays in the air
        liftAngle = (2 * Math.PI) / 8;
        z = Math.sin(liftAngle);
        angle = (2 * Math.PI) / this.airRaySensors;
        for (int i = 0; i < this.airRaySensors; i++) {
            x = Math.cos(angle * i);
            y = Math.sin(angle * i);
            //Carefull, not tracing actors here!
            body.addRayToAutoTrace(numRays, new Triple(x, y, z), rayLength, false, false);
            numRays++;
        }

        // Rays into ground
        liftAngle = -liftAngle;
        z = Math.sin(liftAngle);
        angle = (2 * Math.PI) / this.groundRaySensors;
        for (int i = 0; i < this.groundRaySensors; i++) {
            x = Math.cos(angle * i);
            y = Math.sin(angle * i);
            //Carefull, not tracing actors here!
            body.addRayToAutoTrace(numRays, new Triple(x, y, z), rayLength, false, false);
            numRays++;
        }

        body.setAutoTrace(true);
    }

    @Override
    public double[] getSensors(AgentMemory memory) {
        double[] sensors = new double[getNumSensors()];

        double[] traces = getAllTraces(memory);
        double[] slices = getSlices(memory);
        double[] misc = getMiscSensors(memory);

        System.arraycopy(traces, 0, sensors, 0, traces.length);
        System.arraycopy(slices, 0, sensors, traces.length, slices.length);
        System.arraycopy(misc, 0, sensors, traces.length+slices.length, misc.length);

        return sensors;
    }

    public double[] getAllTraces(AgentMemory memory){
        double[] sensors = new double[getNumTraces()];
        ArrayList<AutoTraceRay> traces = memory.getAutoTraces(); // get autotraces from the memory
        for(AutoTraceRay trace : traces) {
            sensors[(int) Integer.parseInt(trace.getId().getStringId())] = trace.isResult() ? scale(Triple.distanceInSpace(memory.getAgentLocation(),Triple.locationToTriple(trace.getHitLocation())), rayLength) : 0;
        }
        //System.out.println(Arrays.toString(sensors));
        return sensors;
    }

    public double[] getSlices(AgentMemory memory){
        double[] slices = new double[getNumSlices()];
        ArrayList<Player> seenPlayers = memory.seenPlayers(secondsHistory);
        frontLeftDist = 0;
        frontRightDist = 0;
        int numPlayers = 0;
        int num = 0;
        UnrealId[] playerIDs = new UnrealId[seenPlayers.size()];
        for (Player seenPlayer : seenPlayers) {
            playerIDs[num] = seenPlayer.getId();
            num++;
            Triple playerLocation = Triple.locationToTriple(seenPlayer.getLocation());

            //schrum2: make sure a player location was actually found, otherwise skip
            Triple agentLocation = memory.getAgentLocation();
            if(playerLocation == null || agentLocation == null) continue;

            double distance = scaleDistance(Triple.distanceInSpace(agentLocation, playerLocation));
            numPlayers++;
            double angle = Utils.relativeAngleToTarget(memory.getAgentLocation(), memory.getAgentRotation(), playerLocation);
            if (angle > 0) {
                for (int i = 0; i < sliceLimits.length - 1; i++) {
                    if (sliceLimits[i] < angle && angle <= sliceLimits[i + 1]) {
                        slices[i]++;
                        if(i == 0) {
                            frontLeftDist = distance;
                        }
                        break;
                    }
                }
            } else {
                for (int i = 0; i < sliceLimits.length - 1; i++) {
                    if (-sliceLimits[i + 1] < angle && angle <= -sliceLimits[i]) {
                        slices[i + sliceLimits.length - 1]++;
                        if(i == 0) {
                            frontRightDist = distance;
                        }
                        break;
                    }
                }
            }
        }

        AutoTraceRay crosshair = memory.getAutoTrace(crosshairID);
        if(crosshair != null && crosshair.isResult()) {
            playerInCrosshair = false;
            for(UnrealId playerID : playerIDs) {
                playerInCrosshair = playerInCrosshair || (playerID.equals(crosshair.getHitId()));
            }
        }

        return slices;
    }

    public double[] getMiscSensors(AgentMemory memory){
        double[] sensors = new double[getNumMiscSensors()];

        sensors[0] = this.frontLeftDist;
        sensors[1] = this.frontRightDist;
        sensors[2] = memory.isBeingDamaged() ? 1 : 0;

        ArrayList<Item> healths = memory.seenHealths(this.secondsHistory);
        Item nearest = null;
        double distance = Double.MAX_VALUE;
        if(!healths.isEmpty()){
            Triple agent = memory.getAgentLocation();
            for(Item h : healths){
                double d = Triple.distanceInSpace(agent, h.getLocation());
                if(d < distance){
                    distance = d;
                    nearest = h;
                }
            }

            sensors[3] = scaleDistance(nearest.getLocation().x - agent.x);
            sensors[4] = scaleDistance(nearest.getLocation().y - agent.y);
            sensors[5] = scaleDistance(nearest.getLocation().z - agent.z);
            sensors[6] = scaleDistance(distance);
        }

        sensors[7] = playerInCrosshair ? 1 : 0;

        return sensors;
    }

    public int getNumTraces(){
        return 1        // crosshair trace of opponents
                + levelRaySensors   // traces parallel to ground
                + airRaySensors     // traces angled upward
                + groundRaySensors; // traces angled downward
    }

    public int getNumSlices(){
        return ((sliceLimits.length - 1) * 2);
    }

    public int getNumMiscSensors(){
        return 2        // scaled value to enemy in left/right fields of vision
                + 1     // being damaged sensor
                + 4     // relative position and value to nearest health
                + 1;    // crosshair on target indicator
    }

    public static double scale(double distance, double max){
        return Math.exp(-distance / max);
    }

    public static double scaleDistance(double value){
        return scale(value,1000);
    }

    @Override
    public int getNumSensors() {
        return getNumTraces()       // all traces
                + getNumSlices()    // pie slice bot sensors
                + getNumMiscSensors();  // everything else
    }
}
