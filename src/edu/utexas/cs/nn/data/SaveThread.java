package edu.utexas.cs.nn.data;

import java.util.concurrent.Callable;
import wox.serial.Easy;

/**
 * Save file in a thread, so that the file system operations can be distributed.
 *
 * @author Jacob Schrum
 */
public class SaveThread<T> implements Callable<Boolean> {

    private final String filename;
    private final T object;

    public SaveThread(T object, String filename) {
        this.object = object;
        this.filename = filename;
    }

    public Boolean call() {
        try {
            Easy.save(object, filename);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
