package pogamut.ctfbot;

public interface IGoal extends Comparable<IGoal> {
	
	void perform();

	double getPriority();

	boolean hasFailed();

	boolean hasFinished();

	void abandon();
	
}
