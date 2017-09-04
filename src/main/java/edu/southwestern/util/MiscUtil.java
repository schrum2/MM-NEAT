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
}
