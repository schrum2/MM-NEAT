package com.aqwis;

import com.aqwis.models.OverlappingWFCModel;
import com.aqwis.models.SimpleTiledWFCModel;
import com.aqwis.models.WFCModel;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import static javafx.application.Platform.exit;

public class Main {

    private static Document document;
    private static WFCModel wfcModel;

    private static Boolean attributeFromString(Node item, Boolean defaultValue) { return item == null ? defaultValue : Boolean.parseBoolean(item.getNodeValue()); }
    private static Integer attributeFromString(Node item, Integer defaultValue) { return item == null ? defaultValue : Integer.parseInt(item.getNodeValue()); }
    private static String attributeFromString(Node item, String defaultValue) { return item == null ? defaultValue : item.getNodeValue(); }

    public static void main(String[] args, int index) throws Exception {
        Random random = new Random();
        File xmlFile = new File("WaveFunctionCollapse/MySamples.xml");
        //File xmlFile = new File("samples.xml");
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            document = docBuilder.parse(xmlFile);
            //document.getDocumentElement().normalize();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        NodeList nodeList = document.getFirstChild().getChildNodes();
        int outerCounter = 1;
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            NamedNodeMap attributes = node.getAttributes();
            String nodeName = node.getNodeName();

            if (nodeName.equals("#text") || nodeName.equals("#comment")) {
                continue;
            }

            System.out.println(String.format("< %s", attributes.getNamedItem("name").getNodeValue()));

            if (nodeName.equals("overlapping")) {
                wfcModel = new OverlappingWFCModel(
                        attributes.getNamedItem("name").getNodeValue(),
                        attributeFromString(attributes.getNamedItem("N"), 2),
                        attributeFromString(attributes.getNamedItem("width"), 48),
                        attributeFromString(attributes.getNamedItem("height"), 48),
                        attributeFromString(attributes.getNamedItem("periodicInput"), true),
                        attributeFromString(attributes.getNamedItem("periodic"), false),
                        attributeFromString(attributes.getNamedItem("symmetry"), 8),
                        attributeFromString(attributes.getNamedItem("foundation"), 0)
                        );
            } else if (nodeName.equals("simpletiled")) {
                wfcModel = new SimpleTiledWFCModel(
                        attributes.getNamedItem("name").getNodeValue(),
                        attributeFromString(attributes.getNamedItem("subset"), (String) null),
                        attributeFromString(attributes.getNamedItem("width"), 10),
                        attributeFromString(attributes.getNamedItem("height"), 10),
                        attributeFromString(attributes.getNamedItem("periodic"), false),
                        attributeFromString(attributes.getNamedItem("black"), false)
                );
            } else {
                continue;
            }

            for (int k = 0; k < 10; k++) {
            	System.out.print("> ");
            	int seed = random.nextInt();
            	boolean finished = wfcModel.run(seed, attributeFromString(attributes.getNamedItem("limit"), 0));

            	if (finished) {
            		System.out.println("DONE");

            		BufferedImage graphics = wfcModel.graphics();
            		String username = System.getProperty("user.name");
            		//File file = new File(String.format("C:\\Users\\"+username+"\\Desktop/picbreederZentangle"+index+".jpg", attributes.getNamedItem("name").getNodeValue()));
            		File file = new File(String.format("WaveFunctionCollapse/samples/picbreederZentangle"+index+".jpg", attributes.getNamedItem("name").getNodeValue()));
            		try {
            			ImageIO.write(graphics, "jpg", file);
            		} catch (IOException e) {
            			e.printStackTrace();
            			System.exit(1);
            		}
            		break;
            	} else {
            		System.out.println("CONTRADICTION");
            	}
            }


            outerCounter++;
        }
    }
}

