package megaManMaker;

import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;


public class MegaManConvertMMLVToJSON {
	public static int maxX = 0;
	public static int maxY = 0;
	public static HashSet<Point> visited = new HashSet<Point>();
	public static int enemyNumber = -1;
	public static String enemyString = null;
	public static void main(String[] args) {
	
		
		List<List<Integer>> level = convertMMLVtoInt(MegaManVGLCUtil.MEGAMAN_MMLV_PATH+"proto man level 1"+".mmlv");
		MegaManVGLCUtil.printLevel(level);
		MegaManVGLCUtil.convertMegaManLevelToMMLV(level, 1010101010);
	}
	
	public static List<List<Integer>> convertMMLVtoInt(String mmlvFile) {
		File mmlv = new File(mmlvFile);
//		String oldString = "Hello my name is kec";
//		String newString = oldString.replace("k", "d").trim();
//		System.out.println(newString);
		HashSet<Point> activatedScreen = new HashSet<>();
		Scanner scan = null;
		try {
			scan = new Scanner(mmlv);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//int k = scan.nextInt();
		List<List<Integer>> blockxyIDList = new ArrayList<>();
		while(scan.hasNext()) {
			String l = scan.next();
			//IntStream intStr = l.chars();
			
			if(!l.startsWith("2")&&!l.startsWith("1")&&!l.startsWith("4")&&!l.startsWith("0")
					&&!l.startsWith("o")&&!l.startsWith("b")
					&&!l.startsWith("f")&&!l.startsWith("g")&&!l.startsWith("h")&&
					!l.startsWith("j")&&!l.startsWith("k")&&!l.startsWith("l")&&!l.startsWith("m")&&
					!l.startsWith("n")&&!l.startsWith("[")
					) { //shows us all blocks (solid, spike, ladder), enemies, player
				boolean isEnemy = false;

				if(l.startsWith("i")) {
					
					String k = l;
					k=k.replace("i", "i ");
					k=k.replace(",", " ");
					k=k.replace("\"", "");
					k=k.replace("=", " ");
					k=k.replace(".000000", "");
					documentxyAndAddToListi(activatedScreen, blockxyIDList, k);
				}else if(l.startsWith("e")) { //ex,y=177 water, e 45 breakable
					String k = l;
					k=k.replace("e", "e ");
					k=k.replace(",", " ");
					k=k.replace("\"", "");
					k=k.replace("=", " ");
					k=k.replace(".000000", "");
					enemyString = k;
					System.out.println(k);
					documentxyAndAddToListe(activatedScreen, blockxyIDList, k);

					
				} //ex,y=177 water, e 45 breakable
				else if(l.startsWith("d")) { //TODO add else/if for MegaMan, bosses, enemies, doors, etc d - 6 = breakable
					String k = l;
					k=k.replace("d", "d ");
					k=k.replace(",", " ");
					k=k.replace("\"", "");
					k=k.replace("=", " ");
					k=k.replace(".000000", "");
					//System.out.println(k);
					if(k.endsWith("5")) {
						isEnemy=true;
						if(enemyString==null) {
							enemyString = k;
						}
					}
					if(!k.endsWith("6"))
					documentxyAndAddToListi(activatedScreen, blockxyIDList, k);

					
				}
				if (l.startsWith("a")) {
					enemyString = null;
				}
				if(isEnemy&&enemyString!=null) {
//					System.out.println("not a failure case");
//					MiscUtil.waitForReadStringAndEnterKeyPress();
					documentxyAndAddToListenemy(activatedScreen, blockxyIDList, enemyString);

				}
				
			}
			
		}
		List<List<Integer>> complete = new ArrayList<>();		
		for(int y = 0;y<=maxY;y++) {
			List<Integer> row = new ArrayList<>();
			for(int x = 0;x<=maxX;x++) {
				row.add(7);
			}
			complete.add(row);
		}
		for(int i = 0;i<blockxyIDList.size();i++) {
			if(blockxyIDList.get(i).get(2)!=4) //if not breakable
			complete.get(blockxyIDList.get(i).get(1)).set(blockxyIDList.get(i).get(0), blockxyIDList.get(i).get(2));
			else { //breakable needs 4 blocks to be considered
				complete.get(blockxyIDList.get(i).get(1)).set(blockxyIDList.get(i).get(0), blockxyIDList.get(i).get(2));
				complete.get(blockxyIDList.get(i).get(1)-1).set(blockxyIDList.get(i).get(0), blockxyIDList.get(i).get(2));
				complete.get(blockxyIDList.get(i).get(1)).set(blockxyIDList.get(i).get(0)-1, blockxyIDList.get(i).get(2));
				complete.get(blockxyIDList.get(i).get(1)-1).set(blockxyIDList.get(i).get(0)-1, blockxyIDList.get(i).get(2));
			}
		}
		
		System.out.println(activatedScreen.size());
		for(Point p : activatedScreen) {
			System.out.println("("+p.getX()+", "+p.getY()+")");
			for(int x = 0;x<16;x++) {
				for(int y = 0;y<14;y++) {
					if(p.getY()+y<complete.size()&&p.getX()+x<complete.get(0).size()&&complete.get((int) (p.getY()+y)).get((int) (p.getX()+x))==7) {
						complete.get((int) (p.getY()+y)).set((int) (p.getX()+x), 0);
					}
				}
			}
		}
		return complete;
		
		
		
	}

	private static void documentxyAndAddToListenemy(HashSet<Point> activatedScreen, List<List<Integer>> blockxyIDList,
			String enemyString) {
		List<Integer> xyID = new ArrayList<>();
		if(enemyString!=null) {
		Scanner kScan = new Scanner(enemyString);

		kScan.next(); //get past letter
		int xcoord = kScan.nextInt()/16;
		xyID.add(xcoord);
		int ycoord = kScan.nextInt()/16;
		xyID.add(ycoord);
		//if(!visited.contains(new Point(xcoord, ycoord))) {
			visited.add(new Point(xcoord, ycoord));
	
			int e = kScan.nextInt();
			//make all enemies map 11-15
			int enemyOneThruFive = 11+e%5;
			xyID.add(enemyOneThruFive);
		
			int howManySquaresX = xcoord/16;
			int howManySquaresY = ycoord/14;
			int screenX = howManySquaresX*16;
			int screenY = howManySquaresY*14;
			activatedScreen.add(new Point(screenX, screenY));
			if(xcoord>maxX) {
				maxX = xcoord+1;
			}
			if(ycoord>maxY) {
				maxY = ycoord+1;
			}
			//System.out.println(k);
			//System.out.println(l);
			kScan.close();
			blockxyIDList.add(xyID);
		}
		
	}

	private static void documentxyAndAddToListe(HashSet<Point> activatedScreen, List<List<Integer>> blockxyIDList,
			String k) {
		List<Integer> xyID = new ArrayList<>();
		Scanner kScan = new Scanner(k);

		kScan.next(); //get past letter
		int xcoord = kScan.nextInt()/16;
		xyID.add(xcoord);
		int ycoord = kScan.nextInt()/16;
		xyID.add(ycoord);
		if(!visited.contains(new Point(xcoord, ycoord))) {
			visited.add(new Point(xcoord, ycoord));
	
			int e = kScan.nextInt();
			
			if(e==29||e==4||e==3||e==50||e==52||e==51||e==68) { //cannon/shooter
				xyID.add(6);
			}else if (e==5||e==56) { //appearing/disappearing
				xyID.add(1);
			}else if (e==45) { //breakable
				xyID.add(4);				
			}else if (e==31||e==40||e==36||e==67||e==10||e==47) { //moving plat
				xyID.add(5);
			}else if ((e>=177&&e<=194)||(e>=621&&e<=626)) xyID.add(10); //water //177-194 or 621-626
			
			else {
				xyID.add(0);
			}
			int howManySquaresX = xcoord/16;
			int howManySquaresY = ycoord/14;
			int screenX = howManySquaresX*16;
			int screenY = howManySquaresY*14;
			activatedScreen.add(new Point(screenX, screenY));
			if(xcoord>maxX) {
				maxX = xcoord+1;
			}
			if(ycoord>maxY) {
				maxY = ycoord+1;
			}
			//System.out.println(k);
			//System.out.println(l);
			kScan.close();
			blockxyIDList.add(xyID);
		
		}		
	}

	private static void documentxyAndAddToListi(HashSet<Point> activatedScreen, List<List<Integer>> blockxyIDList, String k) {
		List<Integer> xyID = new ArrayList<>();
		Scanner kScan = new Scanner(k);
		kScan.next(); //get past letter
		int xcoord = kScan.nextInt()/16;
		xyID.add(xcoord);
		int ycoord = kScan.nextInt()/16;
		xyID.add(ycoord);
		if(!visited.contains(new Point(xcoord, ycoord))&&kScan.hasNextInt()) {
			
			int itemID = kScan.nextInt();
			visited.add(new Point(xcoord, ycoord));
	
			
			if(itemID==3) { //if ladder
				xyID.add(2); //map to ladder
			}else if(itemID==2) { //if hazard
				xyID.add(3); //map to hazard
			}
			
			else if(itemID==4) { //player
				xyID.add(8); //json player
			}
			else { //solid block still 1
				xyID.add(itemID);
			}
			int howManySquaresX = xcoord/16;
			int howManySquaresY = ycoord/14;
			int screenX = howManySquaresX*16;
			int screenY = howManySquaresY*14;
			activatedScreen.add(new Point(screenX, screenY));
			if(xcoord>maxX) {
				maxX = xcoord+1;
			}
			if(ycoord>maxY) {
				maxY = ycoord+1;
			}
			//System.out.println(k);
			//System.out.println(l);
			kScan.close();
			blockxyIDList.add(xyID);
		
		}
	}

//	private static int convertMMLVTilesToInt(String string) {
//		// TODO Auto-generated method stub
//		return 0;
//	}

	
}
