package edu.southwestern.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

/**
 *
 * @author Jacob Schrum
 */
public class MiscUtil {

	// Universal console Scanner that should be used everywhere 
	// in the code.
	public static final Scanner CONSOLE = new Scanner(System.in);

	public static double unitInvert(double x) {
		return x < 0 ? -1 - x  : 1 - x;
	}

	public static double scaleAndInvert(double x, double max) {
		return scale(max - x, max);
	}

	public static double scale(double x, double max) {
		return x > max ? 1.0 : (x < 0 ? 0 : x / max);
	}

	/**
	 * Pause the program to wait for the user to enter a String
	 * and press enter.
	 * @return String the user entered
	 */
	public static String waitForReadStringAndEnterKeyPress() {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String s = br.readLine();
			return s;
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return null;
	}

	/**
	 * Pause the program, prints msg, prints stack trace of calling class, and waits for the user to enter a String
	 * and press enter.
	 * @param the message
	 * @return
	 */
	public static String waitForReadStringAndEnterKeyPress(Object msg) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("\nmessage: " + msg);
			printStackTraceAndPause();
			String s = br.readLine();
			return s;
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return null;
	}
	
	/**
	 * Prints the stack trace and pauses execution
	 * source here: https://stackoverflow.com/questions/11306811/how-to-get-the-caller-class-in-java
	 */
	public static void printStackTraceAndPause() {
		StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
		String callerClassName = null;
		System.out.println("\nwaiting here:");
		for (int i=1; i<stElements.length; i++) {
			StackTraceElement ste = stElements[i];
			if (!ste.getClassName().equals(MiscUtil.class.getName())&& ste.getClassName().indexOf("java.lang.Thread")!=0) {
				if (callerClassName==null) {
					callerClassName = ste.getClassName();
				} else if (!callerClassName.equals(ste.getClassName())) {
					System.out.println(ste.getClassName());
				}
			}
		}
		waitForReadStringAndEnterKeyPress();
	}
}
