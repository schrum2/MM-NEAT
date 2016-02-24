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
public class XMLFilter implements FilenameFilter {

    @Override
    public boolean accept(File dir, String name) {
        return !name.startsWith(".") && name.endsWith("xml");
    }
}
