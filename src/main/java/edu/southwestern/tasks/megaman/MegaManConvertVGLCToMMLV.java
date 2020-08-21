package edu.southwestern.tasks.megaman;

import java.util.List;
/**
 * This class converts the original VGLC data to a List<List<Integer>> format
 * @author Benjamin Capps
 *
 */
public class MegaManConvertVGLCToMMLV {
	public static void main(String[] args) {
		int firstLevel = 1;
		int lastLevel = 10;
		for(int i = firstLevel;i<=lastLevel;i++) {
			List<List<Integer>> level = MegaManVGLCUtil.convertMegamanVGLCtoListOfLists(MegaManVGLCUtil.MEGAMAN_LEVEL_PATH+"megaman_1_"+i+".txt");
			MegaManVGLCUtil.convertMegaManLevelToMMLV(level, i+"", MegaManVGLCUtil.MEGAMAN_MMLV_PATH);

		}
		
	}
}
