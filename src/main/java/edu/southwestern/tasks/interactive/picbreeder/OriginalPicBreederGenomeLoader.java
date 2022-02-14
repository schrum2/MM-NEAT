package edu.southwestern.tasks.interactive.picbreeder;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import edu.southwestern.evolution.genotypes.TWEANNGenotype;
import edu.southwestern.evolution.genotypes.TWEANNGenotype.LinkGene;
import edu.southwestern.evolution.genotypes.TWEANNGenotype.NodeGene;
import edu.southwestern.networks.ActivationFunctions;
import edu.southwestern.networks.Network;
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
		File inputFile = new File("data\\picbreeder\\originalGenomes\\5736_ShinyRedApple.xml"); // Crash from loop?
		//File inputFile = new File("data\\picbreeder\\originalGenomes\\4547_Face.xml"); // Crash from loop?
		//File inputFile = new File("data\\picbreeder\\originalGenomes\\4376_ButterflyColor.xml"); // Output loops back to hidden neuron
		//File inputFile = new File("data\\picbreeder\\originalGenomes\\3674_Mystic.xml"); // Infinite loop?
		//File inputFile = new File("data\\picbreeder\\originalGenomes\\3257_Quadravision.xml");
		//File inputFile = new File("data\\picbreeder\\originalGenomes\\2914_Firefly.xml"); // Infinite loop?
		//File inputFile = new File("data\\picbreeder\\originalGenomes\\1009_ButterflyGreyscale.xml"); // PERFECT
		//File inputFile = new File("data\\picbreeder\\originalGenomes\\765_PlaneOnRunway.xml"); // PERFECT
		//File inputFile = new File("data\\picbreeder\\originalGenomes\\745_LetterG.xml"); // PERFECT
		//File inputFile = new File("data\\picbreeder\\originalGenomes\\576_Skull.xml"); // PERFECT
		//File inputFile = new File("data\\picbreeder\\originalGenomes\\542_GhostFaceSpooky.xml"); // Unsure: looks good
		//File inputFile = new File("data\\picbreeder\\originalGenomes\\409_Moonlight.xml");
		//File inputFile = new File("data\\picbreeder\\originalGenomes\\395_SpotlightCastingShadow.xml"); // PERFECT
		//File inputFile = new File("data\\picbreeder\\originalGenomes\\121_ShortSDCoif.xml"); // PERFECT
		//File inputFile = new File("data\\picbreeder\\originalGenomes\\4041_Doplhin.xml");
		//File inputFile = new File("data\\picbreeder\\originalGenomes\\simple.xml"); // PERFECT
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(inputFile);
        doc.getDocumentElement().normalize();
		
        int inputs = 0;
        
        NodeList nList = doc.getElementsByTagName("node");
        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node nNode = nList.item(temp);
            //System.out.println("\nCurrent Element :" + nNode.getNodeName() + ":" + nNode.getAttributes().getNamedItem("type").getNodeValue());
            String type = nNode.getAttributes().getNamedItem("type").getNodeValue();
            NodeList subList = nNode.getChildNodes();
            long innovation = Long.parseLong(subList.item(1).getAttributes().getNamedItem("id").getNodeValue());
            String activation = subList.item(3).getFirstChild().getNodeValue();
            //System.out.println(type + ":" + innovation + ":" + activation);
            if(type.equals("in")) {
            	NodeGene n = tg.nodes.get(inputs++);
            	n.innovation = innovation;
            	n.ftype = getFType(activation);
            } else if(type.equals("hidden")) {
            	NodeGene newGene = TWEANNGenotype.newNodeGene(getFType(activation), TWEANN.Node.NTYPE_HIDDEN, innovation);
            	// Adding the gene here may not be the right order
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
        // Get nodes in right order according to the links
        TWEANNGenotype.sortNodeGenesByLinkConnectivity(tg);
        System.out.println("AFTER");
        System.out.println(tg.toString());
        
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
	}
	
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
