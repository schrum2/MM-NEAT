package popacman.prediction;

import java.util.BitSet;

/**
 * Created by Piers on 27/06/2016.
 */
public class PillModel {
    private BitSet pills;
    private BitSet powerPills;
    private int powerPillsEaten;
    private int pillsEaten;
    private double totalPills;

    public PillModel(int indices) {
        this.totalPills = indices;
        this.pills = new BitSet(indices);
        this.powerPills = new BitSet(indices);
    }

    // Pacman visited this index
    public void update(int index) {
        if (pills.get(index)) {
            pillsEaten++;
            pills.set(index, false);
        }
        
        if (powerPills.get(index)) {
        	powerPillsEaten++;
        	powerPills.set(index, false);
        }
    }

    // There is a pill here!
    public void observePill(int index, boolean pillThere) {
        pills.set(index, pillThere);
    }
    
    public void observePowerPill(int index, boolean pillThere) {
        powerPills.set(index, pillThere);
    }

    public int getPillsEaten() {
        return pillsEaten;
    }
    
    public int getPowerPillsEaten() {
    	return powerPillsEaten;
    }

    public double getPillsFraction() {
        return pillsEaten / totalPills;
    }
    
    public double getPowerPillsfraction() {
    	//TODO: maybe don't hardcode this?
    	return powerPillsEaten / 4;
    }

    public PillModel copy() {
        PillModel other = new PillModel((int) this.totalPills);
        other.pills = (BitSet) this.pills.clone();
        other.totalPills = this.totalPills;
        other.pillsEaten = pillsEaten;
        other.powerPills = powerPills;
        other.powerPillsEaten = powerPillsEaten;
        return other;
    }

    public BitSet getPills() {
        return pills;
    }
    
    public BitSet getPowerPills() {
    	return powerPills;
    }
}
