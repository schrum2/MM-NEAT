/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package machinelearning.networks;

import java.util.Hashtable;
import java.util.Vector;
import machinelearning.evolution.evolvables.Evolvable;

/**
 *
 * @author Jacob Schrum
 */
public class MultiWeaponTWEANN extends MultiModalTWEANN {

    private Hashtable<String, Integer> weaponToModeMap;
    private transient String currentWeapon;

    @Override
    public Evolvable getNewInstance() {
        TWEANN net = (TWEANN) super.getNewInstance();
        Hashtable<String, Integer> onlyWeaponMode = new Hashtable<String, Integer>();
        onlyWeaponMode.put("XWeapons.AssaultRiflePickup", 0);
        return new MultiWeaponTWEANN(net, net.getNumberOfOutputs(), 1, onlyWeaponMode);
    }

    private MultiWeaponTWEANN(TWEANN net, int outputWidth, int numModes, Hashtable<String, Integer> weaponToModeMap) {
        this(net.allnodes, net.genes, net.net_id, outputWidth, numModes, weaponToModeMap);
    }

    public MultiWeaponTWEANN(int numInputs, int numOutputs, boolean featureSelective, String firstWeapon) {
        super(numInputs, numOutputs, featureSelective);
        this.weaponToModeMap = new Hashtable<String, Integer>();
        this.weaponToModeMap.put(firstWeapon, 0);
    }

    public MultiWeaponTWEANN(Vector<NNode> in, Vector<NNode> out, Vector<NNode> all, int xnet_id, Vector<Gene> _genes, int outputWidth, int numModes, Hashtable<String, Integer> weaponToModeMap) {
        super(in, out, all, xnet_id, _genes, outputWidth, numModes);
        this.weaponToModeMap = (Hashtable<String, Integer>) weaponToModeMap.clone();
    }

    public MultiWeaponTWEANN(Vector<NNode> all, Vector<Gene> _genes, int xnet_id, int outputWidth, int numModes, Hashtable<String, Integer> weaponToModeMap) {
        super(all, _genes, xnet_id, outputWidth, numModes);
        this.weaponToModeMap = (Hashtable<String, Integer>) weaponToModeMap.clone();
    }

    @Override
    public TWEANN duplicateWithNewID() {
        return new MultiWeaponTWEANN(super.duplicateWithNewID(), this.outputWidth, this.numModes, this.weaponToModeMap);
    }

    public void setCurrentWeapon(String currentWeapon){
        this.currentWeapon = currentWeapon;
    }

    /*
     * ALWAYS set the current weapon before using this!
     */
    @Override
    public double[] propagate(double[] doubles) {
        int bestMode = 0;
        boolean known = this.weaponToModeMap.containsKey(currentWeapon);
        if(!known){
            // Add new mode for unknown weapon
            bestMode = add_output_mode();
            this.weaponToModeMap.put(currentWeapon, bestMode);
            //System.out.println("MT:Added mode for " + currentWeapon);
        } else{
            bestMode = this.weaponToModeMap.get(currentWeapon);
            //System.out.println("MT:Selected mode " + bestMode + " for " + currentWeapon);
        }

        double[] fullOutput = processInputsToOutputs(doubles);
        double[] modeOutput = new double[this.outputWidth];

        //System.out.println("Full: " + fullOutput.length + ": " + Arrays.toString(fullOutput));
        //System.out.println("Mode length: " + modeOutput.length);
        for (int x = 0; x < this.outputWidth; x++) {
            //System.out.println("Accessing " + x + "/" + ((bestMode * this.outputWidth) + x));
            modeOutput[x] = fullOutput[(bestMode * this.outputWidth) + x];
        }
        modeHistory.set(bestMode, modeHistory.get(bestMode) + 1);

        return modeOutput;
    }

    public int add_output_mode() {
        for (int i = 0; i < this.outputWidth; i++) {
            // Add nodes for new mode
            addOutputNode();
        }
        int result = this.numModes;
        this.numModes++;
        modeHistory.add(0);
        return result;
    }

    @Override
    public boolean deleteOutputMode(int mode) {
        int start = firstOutputOfMode(mode);
        boolean result = true;
        for (int i = outputWidth - 1; i >= 0; i--) {
            int id = outputs.get(start + i).node_id;
            result = result && deleteOutputNode(id);
        }
        return result;
    }

    private int firstOutputOfMode(int mode) {
        return mode * outputWidth;
    }

    @Override
    public void mutate() {
        super.mutate(false);
    }
}
