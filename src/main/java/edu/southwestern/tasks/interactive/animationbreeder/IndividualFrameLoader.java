package edu.southwestern.tasks.interactive.animationbreeder;

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
 * Quick and dirty class to take a CPPN for creating 2D animations,
 * and output high-quality images of specific frames of animation.
 * 
 * @author Jacob Schrum
 *
 */
public class IndividualFrameLoader {
	public static final int SIZE = 1000;
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {		
		Parameters.initializeParameterCollections(new String[] {"io:false","netio:false","allowMultipleFunctions:true","recurrency:false"});
		MMNEAT.loadClasses();
		ActivationFunctions.resetFunctionSet();
		// THIS WILL NOT WORK
		if(true) throw new UnsupportedOperationException("Serialization method has changed. Cannot load old xml files.");
		TWEANNGenotype tg = (TWEANNGenotype) Serialization.load("AnimationBreeder-Control19_gen14_4.xml");
		System.out.println(tg);
		//EvolutionaryHistory.initArchetype(0, null, (TWEANNGenotype) tg.copy());
				
		TWEANN network = tg.getPhenotype();

		// Now show the image
		BufferedImage[] images = AnimationUtil.imagesFromCPPN(network, SIZE, SIZE, 0, 50, new double[]{1,1,1,1,1});

		for(int x = 0; x < images.length; x++) {
			GraphicsUtil.saveImage(images[x], "Frame"+x+".jpg");
		}
		
		DrawingPanel panel = new DrawingPanel(SIZE, SIZE, "Network");
		network.draw(panel, true, false);
		panel.save("ComplexHSVNet.jpg");
	}	
}
