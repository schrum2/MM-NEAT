package distance.pattern;

public class PatternCount implements Comparable<PatternCount> {
    public Pattern pattern;
    public Integer count;

    public PatternCount(Pattern pattern, Integer count) {
        this.pattern = pattern;
        this.count = count;
    }

    @Override
    public int compareTo(PatternCount o) {
        return count.compareTo(o.count);
    }

    public String toString() {
        return count + " : " + pattern;
    }
}
