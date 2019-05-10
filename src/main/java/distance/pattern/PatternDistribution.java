package distance.pattern;

import distance.kl.KLDiv;
import utilities.StatSummary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/*

Based on Sparse Distribution, but for Patterns
 */

public class PatternDistribution {

    public static void main(String[] args) {

        PatternDistribution p = new PatternDistribution();
        PatternDistribution q = new PatternDistribution();

        Pattern p1 = new Pattern().setPattern(new int[]{0, 1});
        Pattern p2 = new Pattern().setPattern(new int[]{1, 1});
        Pattern p3 = new Pattern().setPattern(new int[]{3, 1});
        Pattern p4 = new Pattern().setPattern(new int[]{1, 2});
        p.add(p1, 2);
        p.add(p2, 2);
        p.add(p3, 1);

        q.add(p1);
        q.add(p2);
        q.add(p2);
        q.add(p4, 1);

        System.out.println(KLDiv.klDiv(p, p));
        System.out.println(KLDiv.klDiv(p, q));
        System.out.println();
        System.out.println(KLDiv.klDiv(q, p));
        System.out.println(KLDiv.klDiv(q, q));

        System.out.println();
        System.out.println(KLDiv.klDivSymmetric(p, q));
        System.out.println(KLDiv.klDivSymmetric(q, p));
    }

    double epsilon = 1e1;

    public HashMap<Pattern, StatSummary> statMap;
    public int tot = 0;

    public PatternDistribution() {
        statMap = new HashMap<>();
    }

    public PatternDistribution add(Pattern p) {
        add(p, 1);
        return this;
    }

    public PatternDistribution add(PatternDistribution pd) {
        for (Map.Entry<Pattern, StatSummary> pair : pd.statMap.entrySet()) {
            add(pair.getKey(), pair.getValue().sum());
        }
        return this;
    }

    public ArrayList<PatternCount> getFrequencyList() {
        ArrayList<PatternCount> list = new ArrayList<>();
        for (Map.Entry<Pattern, StatSummary> pair : statMap.entrySet()) {
            list.add(new PatternCount(pair.getKey(), pair.getValue().n()));
        }
        Collections.sort(list);
        return list;
    }

    public PatternDistribution add(Pattern p, double w) {
        StatSummary ss = statMap.get(p);
        if (ss == null) {
            ss = new StatSummary();
            statMap.put(p, ss);
        }
        ss.add(w);
        tot+=w;
        return this;
    }

    public double getProb(Pattern key) {
        StatSummary ss = statMap.get(key);
        if (ss != null) {
            return epsilon + ss.sum() / tot;
        } else {
            return epsilon;
        }
    }

    public double getRawProb(Pattern key) {
        // no epsilon safety
        StatSummary ss = statMap.get(key);
        if (ss != null) {
            return ss.sum() / tot;
        } else {
            return 0.0;
        }
    }

}
