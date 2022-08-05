package edu.southwestern.data;

import java.util.concurrent.Callable;

import edu.southwestern.util.file.Serialization;

/**
 * Save file in a thread, so that the file system operations can be distributed.
 *
 * @author Jacob Schrum
 * @param <T>
 *            The class being saved
 */
public class SaveThread<T> implements Callable<Boolean> {

	private final String filename;
	private final T object;

	public SaveThread(T object, String filename) {
		this.object = object;
		this.filename = filename;
	}

	@Override
	public Boolean call() {
		try {
			Serialization.save(object, filename);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
