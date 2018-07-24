package edu.southwestern.tasks.mario.gan;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Imported from Mario GAN.
 * Jacob: I removed a lot of unnecessary code
 * that I believe was part of some other project this code was once
 * a part of.
 *
 * @author Jialin Liu
 */
public class Settings {
	public static final String WARN_MSG = "[WARN] ";

	public static final String DEBUG_MSG = "[DEBUG] ";
	public static final String ERROR_MSG = "[ERROR] ";
	public static final String INFO_MSG = "[INFO] ";

	public static final String PYTHON_BASE_PATH = "." + File.separator + "src" + File.separator + "main" + File.separator + "python" + File.separator + "MarioGAN" + File.separator;

	public static final String WASSERSTEIN_PATH = PYTHON_BASE_PATH + "generator_ws.py";
	public static final String WASSERSTEIN_GAN = PYTHON_BASE_PATH + "netG_epoch_5000.pth";
	public static final int GAN_DIM = 32;

	public static void printWarnMsg(String msg) {
		System.out.println(WARN_MSG + msg);
	}

	public static void printDebugMsg(String msg) {
		System.out.println(DEBUG_MSG + msg);
	}

	public static void printInfoMsg(String msg) {
		System.out.println(INFO_MSG + msg);
	}

	public static void printErrorMsg(String msg) {
		System.out.println(ERROR_MSG + msg);
	}
}
