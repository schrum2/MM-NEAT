package me.jakerg.rougelike;

/**
 * A class to represent an on screen message
 * @author gutierr8
 *
 */
public class Message {
	private String message;
	public String message() { return message; };
	public void setMessage(String m) { message = m; }
	
	public Message(String m) {
		message = m;
	}
}
