package icecreamyou.LodeRunner;
import javax.swing.JOptionPane;
import javax.swing.JDialog;
import javax.swing.JTextField;
import java.awt.Frame;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Inspired by the CustomDialog implementation by Oracle at http://j.mp/eAJChw
 * To use, create a new instance and then call getResult() to get the string the user typed.
 * If the resulting string is null, then don't save anything.
 */
public class LevelSaveDialog extends JDialog implements ActionListener, PropertyChangeListener {

	/**
	 * Automatically generated. Required by all subclasses of JDialog.
	 */
	private static final long serialVersionUID = -557404226635442982L;

	/**
	 * The text in the input field.
	 */
	private String typedText = null;
	/**
	 * The input field.
	 */
	private JTextField textField;
	/**
	 * The dialog pane.
	 */
	private JOptionPane optionPane;
	
	private static final String SAVE_BUTTON = "Save";
	private static final String CANCEL_BUTTON = "Cancel";

	/**
	 * Get the user's input (the text typed into the input field or null if the dialog was canceled).
	 */
	public String getResult() {
		return typedText;
	}

	public LevelSaveDialog(Frame aFrame) {
		super(aFrame, "Save level", true);
		textField = new JTextField(10);
		Object[] message = {"Enter a title for the level.", textField};
		Object[] buttons = {SAVE_BUTTON, CANCEL_BUTTON};
		optionPane = new JOptionPane(message,
									 JOptionPane.QUESTION_MESSAGE,
									 JOptionPane.OK_CANCEL_OPTION,
									 null,
									 buttons,
									 buttons[0]
		);
		setContentPane(optionPane);

		// Handle window closing correctly.
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				optionPane.setValue(new Integer(JOptionPane.CLOSED_OPTION));
			}
		});

		// Ensure the text field always gets the first focus.
		addComponentListener(new ComponentAdapter() {
			public void componentShown(ComponentEvent ce) {
				textField.requestFocusInWindow();
			}
		});

		// Register an event handler that puts the text into the option pane.
		textField.addActionListener(this);

		// Register an event handler that reacts to option pane state changes.
		optionPane.addPropertyChangeListener(this);
	}

	// Ensure that pressing "Enter" while in the textfield will have the same effect as clicking the "Save" button.
	public void actionPerformed(ActionEvent e) {
		optionPane.setValue(SAVE_BUTTON);
	}

	/** React to state changes in the option pane. */
	public void propertyChange(PropertyChangeEvent e) {
		String prop = e.getPropertyName();

		if (isVisible()
				&& (e.getSource() == optionPane)
				&& (JOptionPane.VALUE_PROPERTY.equals(prop) ||
						JOptionPane.INPUT_VALUE_PROPERTY.equals(prop))) {
			Object value = optionPane.getValue();
			if (value == JOptionPane.UNINITIALIZED_VALUE) {
				// Ignore reset.
				return;
			}
			// Reset the JOptionPane's value so that we can detect the next time it changes.
			optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);

			if (SAVE_BUTTON.equals(value)) {
				typedText = textField.getText();
				// Validate.
				if (typedText.matches("[\\-\\w\\s]+") && !typedText.equals("CAMPAIGN")) {
					boolean willOverwrite = false;
					for (Object level : LodeRunner.getLevels()) {
						if (typedText.equals(level)) {
							willOverwrite = true;
						}
					}
					if (willOverwrite) {
						int warn = JOptionPane.showConfirmDialog(
								this,
								"Another level with the same name exists.\n"+
								"Do you want to overwrite it?",
								"Confirm overwrite",
								JOptionPane.YES_NO_OPTION,
								JOptionPane.WARNING_MESSAGE
						);
						if (warn == JOptionPane.NO_OPTION) {
							typedText = null;
							textField.requestFocusInWindow();
							return;
						}
					}
					clearAndHide();
				}
				else {
					// Invalid level name.
					String complaint = (typedText.equals("CAMPAIGN") ?
							"You cannot call a level \"CAMPAIGN.\"" :
							"Level titles can only contain alphanumeric characters, spaces, hyphens, and underscores.");
					textField.selectAll();
					JOptionPane.showMessageDialog(
							this,
							complaint,
							"Invalid title",
							JOptionPane.ERROR_MESSAGE);
					typedText = null;
					textField.requestFocusInWindow();
				}
			}
			else {
				// User closed save dialog or clicked cancel.
				typedText = null;
				clearAndHide();
			}
		}
	}

	// Clear and hide the dialog.
	public void clearAndHide() {
		textField.setText(null);
		setVisible(false);
	}
}
