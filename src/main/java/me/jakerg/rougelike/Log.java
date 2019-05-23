package me.jakerg.rougelike;

import java.util.ArrayList;

public class Log {
	private ArrayList<Message> messages;
	public ArrayList<Message> messages() { return messages; }
	
	private int maxMessages;
	public int max() { return maxMessages; }
	
	public Log(int max) {
		this.maxMessages = max;
		messages = new ArrayList<>(max);
	}
	
	public void addMessage(String m) {
		overflow();
		messages.add(0, new Message(m));
	}
	
	private void overflow() {
		if(messages.size() + 1 > max())
			messages.remove(messages.size() - 1);
	}
}
