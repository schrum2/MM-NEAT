/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.util.file;

import java.io.File;
import java.io.FilenameFilter;

/**
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
