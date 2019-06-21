package com.aqwis;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

import com.aqwis.models.SimpleTiledZentangleWFCModel;
import com.aqwis.models.WFCModel;

/**
 * The normal Main class for WFC is too general and depends on loading files from disk.
 * This simpler, more specialized version accomplishes the same launch specifically for
 * the creation of a Zentangle that is passed images from Picbreeder.
 * 
 * @author schrum2
 *
 */
public class SimpleTiledZentangle {

	public static String getSaveDirectory() {
		return "zentangle";
	}
	
	public static void simpleTiledZentangle(int index) throws Exception {

		Random random = new Random(index);
		
		// Only support simpletiled for now

//		if (nodeName.equals("overlapping")) {
//			wfcModel = new OverlappingWFCModel(
//					attributes.getNamedItem("name").getNodeValue(),
//					attributeFromString(attributes.getNamedItem("N"), 2),
//					attributeFromString(attributes.getNamedItem("width"), 48),
//					attributeFromString(attributes.getNamedItem("height"), 48),
//					attributeFromString(attributes.getNamedItem("periodicInput"), true),
//					attributeFromString(attributes.getNamedItem("periodic"), false),
//					attributeFromString(attributes.getNamedItem("symmetry"), 8),
//					attributeFromString(attributes.getNamedItem("foundation"), 0)
//					);
//		} else if (nodeName.equals("simpletiled")) {
			WFCModel wfcModel = new SimpleTiledZentangleWFCModel(
					"picbreeder", // name (the save directory?)
					null, // subset
					30,   // width
					30,   // height
					false,// periodic?
					false // black?
					);
//		} else {
//			continue;
//		}

		for (int k = 0; k < 10; k++) {
			System.out.print("> ");
			int seed = random.nextInt();
			boolean finished = wfcModel.run(seed, 0); // The "limit" is 0?

			if (finished) {
				System.out.println("DONE");

				BufferedImage graphics = wfcModel.graphics();
				File file = new File(getSaveDirectory()+"/picbreederZentangle"+index+".jpg");
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


	}
}
