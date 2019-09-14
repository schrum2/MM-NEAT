package distance.kl;

import distance.pattern.Pattern;
import distance.pattern.PatternDistribution;

public class KLDiv {
    public static double klDiv(double p, double q) {
        return p * (Math.log(p / q));
    }

    public static double klDivSymmetric(double p, double q) {
        return p * Math.log(p / q) + q * Math.log(q/p);
    }

    public static double klDivSymmetric(PatternDistribution pDis, PatternDistribution qDis) {
        return klDiv(pDis, qDis) + klDiv(qDis, pDis);
    }

    public static double klDiv(PatternDistribution pDis, PatternDistribution qDis) {
        double tot = 0;
        // iterate only over the values in p
        // since any ones not in p will have a contribution of zero
        for (Pattern key : pDis.statMap.keySet()) {
            double p = pDis.getProb(key);
            double q = qDis.getProb(key);
            // epsilon is already included in the getProb function
            tot += p * Math.log(p/q);
        }
        return tot;
    }
}
