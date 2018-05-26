package edu.utexas.cs.nn;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Allows us to find the resource (such as a plan file) and return a filesystem
 * handle to it
 * @author Igor Karpov (ikarpov@cs.utexas.edu)
 */
public class ResourceFinder {
    private String name;
    private String path = null;

    public ResourceFinder(String name) {
        this.name = name;
    }

    /**
     * Get the file name of the resource wrapped in this finder
     * @return absolute file system path to the resource
     */
    public String getAbsolutePath() {
        if (path != null) return path; // if we already know the path
        URL url = getClass().getResource(name);
        try {
            String uri = url.toURI().toString();
            if (uri.startsWith("jar:")) {
                // we are running from inside a jar, so we copy to a
                // temporary file and return a handle to that file
                path = copyToFile();
            } else {
                path = new File(uri).getAbsolutePath();
            }
        } catch(URISyntaxException e) {
            path = new File(url.getPath()).getAbsolutePath();
        }
        return path;
    }

    /**
     * Create a temporary file and copy the resource contents into this file
     * @return absolute file system path fo the temporary file
     */
    private String copyToFile() {
        try {
            // Create temp file.
            File temp = File.createTempFile("utaustin", ".lap");
            // Delete temp file when program exits.
            temp.deleteOnExit();
            // copy the resource into a temporary file
            OutputStream output = new FileOutputStream(temp);
            InputStream input = getClass().getResourceAsStream(name);
            byte[] buf = new byte[1024];
            int len;
            while ( (len = input.read(buf)) > 0) {
                output.write(buf, 0, len);
            }
            input.close();
            output.close();
            // return the absolute path of the temporary file
            System.out.println("Resource copied into temporary file: " + temp);
            return temp.getAbsolutePath();
        } catch (IOException e) {
            System.err.println("Could not copy resource " + name + " to a temporary file");
            e.printStackTrace();
            return null;
        }
    }
}
