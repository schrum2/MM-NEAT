package icecreamyou.LodeRunner;
import javax.swing.JLabel;

/**
 * ScoreLabels are JLabels that keep track of a numeric value that is part of what they display.
 */
public class ScoreLabel extends JLabel {
	
	/**
	 * Automatically generated. Required by subclasses of JLabel.
	 */
	private static final long serialVersionUID = -301615975983003358L;
	/**
	 * The value to keep track of.
	 */
	private int value = 0;
	/**
	 * What the value starts as.
	 */
	private int initialValue = 0;
	/**
	 * The text to show to describe the value.
	 */
	private String label = "";
	
	/**
	 * Create a new ScoreLabel.
	 * @param initial The complete initial String to show on the label.
	 * @param label The text to show to describe the value.
	 * @param initialValue The initial value of the score.
	 */
	public ScoreLabel(String initial, String label, int initialValue) {
		super(initial);
		this.label = label;
		this.value = initialValue;
		this.initialValue = initialValue;
	}
	
	/**
	 * Increase the internal value.
	 * @param val The amount by which to increase the value.
	 */
	public void addValue(int val) {
		value += val;
		setText(getString());
	}
	
	/**
	 * Decrease the internal value.
	 * @param val The amount by which to decrease the value.
	 */
	public void subtractValue(int val) {
		value -= val;
		setText(getString());
	}
	
	/**
	 * Get the current value.
	 */
	public int getValue() {
		return value;
	}
	/**
	 * Reset the current value to its initial state.
	 */
	public void resetValue() {
		value = initialValue;
		setText(getString());
	}
	
	/**
	 * Generate a String for display based on the value and label.
	 */
	private String getString() {
		return label +": "+ value;
	}
	
}
