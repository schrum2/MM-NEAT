package edu.southwestern.tasks.interactive.picbreeder;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import edu.southwestern.evolution.genotypes.TWEANNGenotype;
import edu.southwestern.evolution.genotypes.TWEANNGenotype.LinkGene;
import edu.southwestern.evolution.genotypes.TWEANNGenotype.NodeGene;
import edu.southwestern.networks.ActivationFunctions;
import edu.southwestern.networks.TWEANN;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.util.MiscUtil;
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
	public static final int SIZE = 300;
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {		
		Parameters.initializeParameterCollections(new String[] {});
		TWEANNGenotype tg = new TWEANNGenotype(PicbreederTask.CPPN_NUM_INPUTS, PicbreederTask.CPPN_NUM_OUTPUTS, 0);
		//System.out.println(tg);
		// Now, load TWEANN structure from file
		File inputFile = new File("data\\picbreeder\\originalGenomes\\4041_Doplhin.xml");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(inputFile);
        doc.getDocumentElement().normalize();
		
        int inputs = 0;
        int outputs = 0;
        
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
            	NodeGene n = tg.nodes.get(tg.outputStartIndex() + (outputs++));
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

        DrawingPanel panel = new DrawingPanel(TWEANN.NETWORK_VIEW_DIM, TWEANN.NETWORK_VIEW_DIM, "Network");
		TWEANN network = tg.getPhenotype();
		network.draw(panel);
		
        // Now show the image
		BufferedImage image = GraphicsUtil.imageFromCPPN(network, SIZE, SIZE);
		DrawingPanel picture = GraphicsUtil.drawImage(image, "Image", SIZE, SIZE);
		// Wait for user
		MiscUtil.waitForReadStringAndEnterKeyPress();
		picture.dispose();
	}
	
	public static int getFType(String name) {
		switch(name) {
		case "identity(x)":
			return ActivationFunctions.FTYPE_ID;
		case "gaussian(x)":
			return ActivationFunctions.FTYPE_GAUSS;
		case "sin(x)":
			return ActivationFunctions.FTYPE_SINE;
		case "sigmoid(x)":
			return ActivationFunctions.FTYPE_SIGMOID;
		default:
			throw new IllegalArgumentException("Invalid activation function: " + name);
		}
	}
}
