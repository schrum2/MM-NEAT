package edu.southwestern.util.file;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Filename filter that accepts files ending with the ser extension.
 * 
 * @author Jacob
 */
public class SERFilter implements FilenameFilter {

	@Override
	public boolean accept(File dir, String name) {
		return !name.startsWith(".") && name.endsWith("ser");
	}
}
