package edu.southwestern.util.file;

import java.io.File;
import java.io.FilenameFilter;

/**
 * A filter object that accepts filenames beginning with a particular string
 * prefix.
 * 
 * @author Jacob
 */
public class PrefixFilter implements FilenameFilter {

	private final String prefix;

	public PrefixFilter(String prefix) {
		this.prefix = prefix;
	}

	@Override
	public boolean accept(File dir, String name) {
		return name.startsWith(prefix);
	}
}
