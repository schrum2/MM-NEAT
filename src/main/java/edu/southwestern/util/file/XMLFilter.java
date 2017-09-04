package edu.southwestern.util.file;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Filename filter that accepts files ending with the xml extension.
 * 
 * @author Jacob
 */
public class XMLFilter implements FilenameFilter {

	@Override
	public boolean accept(File dir, String name) {
		return !name.startsWith(".") && name.endsWith("xml");
	}
}
