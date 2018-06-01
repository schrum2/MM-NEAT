package edu.southwestern.util.graphics;

import java.io.IOException;
import java.util.List;

import org.deeplearning4j.zoo.util.imagenet.ImageNetLabels;

/**
 * This class is necessary because the new DL4J 1.0.0-beta makes the getLabels
 * method protected. This is a work-around.
 * 
 * @author Jacob Schrum
 */
public class AccessibleImageNetLabels extends ImageNetLabels {
	public AccessibleImageNetLabels() throws IOException {
		super();
	}

	public List<String> labels() throws IOException {
		return getLabels();
	}
}