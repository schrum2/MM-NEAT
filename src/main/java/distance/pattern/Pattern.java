package distance.pattern;

import java.util.Arrays;
import java.util.HashMap;

public class Pattern implements Comparable<Pattern> {

    public static void main(String[] args) {
        int[] x = {0, 1, 2, 3, 3};
        int[] ix1 = {0, 3};
        int[] ix2 = {0, 4};
        int[] ix3 = {0, 1};
        Pattern p1 = new Pattern().setPattern(x, ix1);
        Pattern p2 = new Pattern().setPattern(x, ix2);
        Pattern p3 = new Pattern().setPattern(x, ix3);

        HashMap<Pattern, Integer> test = new HashMap<>();
        test.put(p1, 1);
        test.put(p2, 2);
        test.put(p3, 3);
        System.out.println(test);

    }

    public int[] v;

    public Pattern setPattern(int[] v) {
        this.v = v;
        return this;
    }

    public Pattern setPattern(int[] x, int[] ix) {
        v = new int[ix.length];
        for (int i=0; i<ix.length; i++) {
            v[i] = x[ix[i]];
        }
        return this;
    }

    public int hashCode() {
        return Arrays.hashCode(v);
    }

    public  boolean equals(Object pattern) {
        try {
            Pattern p = (Pattern) pattern;
            for (int i = 0; i < v.length; i++) {
                if (v[i] != p.v[i]) return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public int compareTo(Pattern p) {
        try {
            // now iterate over all the values
            for (int i=0; i<v.length; i++) {
                if (v[i] > p.v[i]) {
                    return 1;
                }
                if (v[i] < p.v[i]) {
                    return -1;
                }
            }
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }

    }

    public String toString() {
        return Arrays.toString(v);
    }

}
