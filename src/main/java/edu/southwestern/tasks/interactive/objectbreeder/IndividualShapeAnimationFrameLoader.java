package edu.southwestern.tasks.interactive.objectbreeder;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.TWEANNGenotype;
import edu.southwestern.networks.ActivationFunctions;
import edu.southwestern.networks.TWEANN;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.util.file.Serialization;
import edu.southwestern.util.graphics.AnimationUtil;
import edu.southwestern.util.graphics.DrawingPanel;
import edu.southwestern.util.graphics.GraphicsUtil;

/**
 * Quick and dirty class to take a CPPN for creating 3D animations,
 * and output high-quality images of specific frames of animation.
 * 
 * @author Jacob Schrum
 *
 */
public class IndividualShapeAnimationFrameLoader {
	public static final int SIZE = 1000;
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {		
		Parameters.initializeParameterCollections(new String[] {"io:false","netio:false","allowMultipleFunctions:true","recurrency:false","allowCubeDisplacement:true"});
		MMNEAT.loadClasses();
		ActivationFunctions.resetFunctionSet();
		//TWEANNGenotype tg = (TWEANNGenotype) Easy.load("ThreeDimensionalAnimationBreeder-Control19_gen15_3.xml");
		
		// THIS WILL NOT WORK
		if(true) throw new UnsupportedOperationException("Serialization method has changed. Cannot load old xml files.");
		TWEANNGenotype tg = (TWEANNGenotype) Serialization.load("ThreeDimensionalAnimationBreeder-Control19_gen15_3.xml");
		System.out.println(tg);
		//EvolutionaryHistory.initArchetype(0, null, (TWEANNGenotype) tg.copy());
				
		TWEANN network = tg.getPhenotype();

		double pitch = (Parameters.parameters.integerParameter("defaultPitch")/(double) ThreeDimensionalAnimationBreederTask.MAX_ROTATION) * 2 * Math.PI; 
		double heading = (Parameters.parameters.integerParameter("defaultHeading")/(double) ThreeDimensionalAnimationBreederTask.MAX_ROTATION) * 2 * Math.PI;
		// Now show the image
		// Hard coded CUBE_SIDE_LENGTH to be 4 times bigger to make the shape bigger. Need to generalize this.
		BufferedImage[] images = AnimationUtil.shapesFromCPPN(network, SIZE,SIZE, 0, 50, new Color(223,233,244), heading, pitch, new double[] {1,1,1,1,1,1});

		for(int x = 0; x < images.length; x++) {
			DrawingPanel picture = GraphicsUtil.drawImage(images[x], "Image", SIZE, SIZE);
			picture.save("ShapeFrame"+x+".jpg");
			// Tried using GraphicsUtil.save, but the color palette was weird
		}
		
		DrawingPanel panel = new DrawingPanel(SIZE, SIZE, "Network");
		network.draw(panel, true, false);
		panel.save("ComplexShapeNet.jpg");
	}	
}
