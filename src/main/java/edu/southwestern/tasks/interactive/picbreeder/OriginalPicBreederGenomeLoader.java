package edu.southwestern.tasks.interactive.picbreeder;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.southwestern.evolution.genotypes.TWEANNGenotype;
import edu.southwestern.evolution.genotypes.TWEANNGenotype.LinkGene;
import edu.southwestern.evolution.genotypes.TWEANNGenotype.NodeGene;
import edu.southwestern.networks.ActivationFunctions;
import edu.southwestern.networks.TWEANN;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.util.MiscUtil;
import edu.southwestern.util.datastructures.ArrayUtil;
import edu.southwestern.util.graphics.DrawingPanel;
import edu.southwestern.util.graphics.GraphicsUtil;

/**
 * I got some genomes from:
 * https://github.com/Evolving-AI-Lab/cppnx/tree/master/CanalizationPicbreederGenomes
 * which I believe were transferred from the original Picbreeder.
 * In any case, they represent some of the most prominent images
 * from that site. This class loads those genomes into my CPPN format
 * and then displays the pictures.
 * 
 * @author Jacob Schrum
 */
public class OriginalPicBreederGenomeLoader {
	public static final int SIZE = 600; //64;
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {		
		Parameters.initializeParameterCollections(new String[] {"io:false","netio:false","allowMultipleFunctions:true","finalPassOnOutputActivation:true"});
		TWEANNGenotype tg = new TWEANNGenotype(PicbreederTask.CPPN_NUM_INPUTS, PicbreederTask.CPPN_NUM_OUTPUTS, -1);
		//System.out.println(tg);
		// Now, load TWEANN structure from file
		String[] fileNames = {"5736_ShinyRedApple.xml","4547_Face.xml","4376_ButterflyColor.xml",
				"3674_Mystic.xml", //infinite loop???
				"3257_Quadravision.xml",//, nothing connected to the output neurons???
				"2914_Firefly.xml",// Infinite loop?
				"1009_ButterflyGreyscale.xml","765_PlaneOnRunway.xml","745_LetterG.xml","576_Skull.xml","542_GhostFaceSpooky.xml",
				"409_Moonlight.xml","395_SpotlightCastingShadow.xml","121_ShortSDCoif.xml","4041_Doplhin.xml","simple.xml"
				};
		
		for(int i = 0 ; i < fileNames.length; i++) {
			render(tg, fileNames[i]);
		}
	}

	/**
	 * Kick-off method to the kick-off method that creates a String called
	 * defaultPath that represents the beginning of the File path before 
	 * getting to xml. 
	 * 
	 * @param tg Neural network that represents a CPPN
	 * @param xml Name of the file with xml extension
	 * @throws ParserConfigurationException xml File not configured correctly
	 * @throws SAXException
	 * @throws IOException File not found
	 */
	private static void render(TWEANNGenotype tg, String xml) throws ParserConfigurationException, SAXException, IOException {
		String defaultPath = "data\\\\picbreeder\\\\originalGenomes";
		render(tg, defaultPath, xml);
	}
	
	/**
	 * Kick-off method to render that creates a new File with the path to 
	 * the desired xml file
	 * 
	 * @param tg Neural network that represents a CPPN
	 * @param path String of the path to where the xml is stored
	 * @param xml Name of the file with xml extension
	 * @throws ParserConfigurationException xml File not configured correctly
	 * @throws SAXException
	 * @throws IOException File not found
	 */
	private static void render(TWEANNGenotype tg, String path, String xml) throws ParserConfigurationException, SAXException, IOException {
		render(tg, new File(path + "\\\\" + xml));
	}	
	
	/**
	 * Given a valid input File, this method will render an image based on
	 * the one stored in the file
	 * 
	 * @param tg Neural network that represents a CPPN
	 * @param inputFile File that corresponds with the path to the xml file
	 * @throws ParserConfigurationException xml file was not configured correctly
	 * @throws SAXException 
	 * @throws IOException File not found
	 */
	private static void render(TWEANNGenotype tg, File inputFile)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(inputFile);
        doc.getDocumentElement().normalize();
		
        int inputs = 0;
        
        NodeList nList = doc.getElementsByTagName("node");
        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node nNode = nList.item(temp);
            System.out.println("\nCurrent Element :" + nNode.getNodeName() + ":" + nNode.getAttributes().getNamedItem("type").getNodeValue());
            String type = nNode.getAttributes().getNamedItem("type").getNodeValue();
            NodeList subList = nNode.getChildNodes();
            long innovation = Long.parseLong(subList.item(1).getAttributes().getNamedItem("id").getNodeValue());
            String activation = subList.item(3).getFirstChild().getNodeValue();
            System.out.println(type + ":" + innovation + ":" + activation);
            if(type.equals("in")) {
            	NodeGene n = tg.nodes.get(inputs++);
            	n.innovation = innovation;
            	n.ftype = getFType(activation);
            } else if(type.equals("hidden")) {
            	NodeGene newGene = TWEANNGenotype.newNodeGene(getFType(activation), TWEANN.Node.NTYPE_HIDDEN, innovation);
            	// Adding the gene here may not be the right order
            	System.out.println("Adding "+activation+":hidden:"+innovation+ " at "+tg.outputStartIndex());
            	tg.nodes.add(tg.outputStartIndex(), newGene);
            } else if(type.equals("out")) {
            	String label = nNode.getAttributes().getNamedItem("label").getNodeValue();
            	NodeGene n;
            	if(label.equals("ink") || label.equals("brightness")) {
            		n = tg.nodes.get(tg.nodes.size() - 1);
                } else if(label.equals("saturation")) {
            		n = tg.nodes.get(tg.nodes.size() - 2);
                } else { // hue
            		n = tg.nodes.get(tg.nodes.size() - 3);
                }
            	n.innovation = innovation;
            	n.ftype = getFType(activation);
            }
        }
                
        tg.links.clear();
        NodeList linkList = doc.getElementsByTagName("link");
        for (int temp = 0; temp < linkList.getLength(); temp++) {
            Node linkNode = linkList.item(temp);
            NodeList subList = linkNode.getChildNodes();
            long innovation = Long.parseLong(subList.item(1).getAttributes().getNamedItem("id").getNodeValue());
            long sourceInnovation = Long.parseLong(subList.item(3).getAttributes().getNamedItem("id").getNodeValue());
            long targetInnovation = Long.parseLong(subList.item(5).getAttributes().getNamedItem("id").getNodeValue());
            double weight = Double.parseDouble(subList.item(7).getFirstChild().getNodeValue());
            //System.out.println(innovation + ":" + sourceInnovation + ":" + targetInnovation + ":" + weight);
            LinkGene lg = TWEANNGenotype.newLinkGene(sourceInnovation, targetInnovation, weight, innovation, false);
            tg.links.add(lg);
        }

        System.out.println("BEFORE");
        System.out.println(tg.toString());
        
        for(LinkGene lg: tg.links) {
        	System.out.println("Source = " + lg.sourceInnovation);
        	System.out.println("Target = " + lg.targetInnovation);
        }
       
        // Get nodes in right order according to the links
        // deleting specific nodes??? 
        //TWEANNGenotype.sortNodeGenesByLinkConnectivity(tg);
       
        //moveInputToEnd(tg);
        
        System.out.println("AFTER");
        System.out.println(tg.toString());
        
        System.out.println("-------------------------------------------------");
        
        for(LinkGene lg: tg.links) {
        	System.out.println("Source = " + lg.sourceInnovation);
        	System.out.println("Target = " + lg.targetInnovation);
        }
        
        DrawingPanel panel = new DrawingPanel(800, 800, "Network");
		TWEANN network = tg.getPhenotype();
		network.draw(panel, true, false);
		
        // Now show the image
		//BufferedImage image = GraphicsUtil.imageFromCPPN(network, SIZE, SIZE);
		double scale = 1.0;
		BufferedImage image = GraphicsUtil.imageFromCPPN(network, SIZE, SIZE, ArrayUtil.doubleOnes(network.numInputs()), -1, scale, 0, 0, 0);
		DrawingPanel picture = GraphicsUtil.drawImage(image, "Image", SIZE, SIZE);
		// Wait for user
		String result = MiscUtil.waitForReadStringAndEnterKeyPress();
		if(!result.trim().equals("")) {
			// Save the image
			GraphicsUtil.saveImage(image, result.trim());
		}
		picture.dispose();
		panel.dispose();
	}
	
	/**
	 * Moves all output nodes to the end of the list
	 * @param t Neural network that represents a CPPN
	 */
	public static void moveInputToEnd(TWEANNGenotype t) {
		int numberOfNodesMoved = 0;
		for(int i = 0; i < t.nodes.size() - numberOfNodesMoved; i++) { // not sure about the num of nodes moved...
			if(t.nodes.get(i).ntype == TWEANN.Node.NTYPE_OUTPUT) { // if t is an output node, move to the end
				NodeGene removed = t.nodes.remove(i); // removes the one in that position
				t.nodes.add(removed); // adds this to the end of the list
				numberOfNodesMoved++; // increment counter
			}
		}
    }
	
	/**
	 * Given a string with a valid activation function name, return the
	 * activation function that corresponds with the string. If it is invalid,
	 * it will throw an IllegalArgumentException.
	 * 
	 * @param name String that contains the valid 
	 * @return Activation function that corresponds with the String name
	 * @throws IllegalArgumentException if the String does not contain valid
	 * 				activation function
	 */
	public static int getFType(String name) {
		switch(name) {
		case "identity(x)":
			return ActivationFunctions.FTYPE_ID;
		case "gaussian(x)":
			return ActivationFunctions.FTYPE_FULLGAUSS;
		case "sin(x)":
			return ActivationFunctions.FTYPE_SINE;
		case "cos(x)":
			return ActivationFunctions.FTYPE_COS;
		case "sigmoid(x)":
			return ActivationFunctions.FTYPE_FULLSIGMOID;
		default:
			throw new IllegalArgumentException("Invalid activation function: " + name);
		}
	}
}
