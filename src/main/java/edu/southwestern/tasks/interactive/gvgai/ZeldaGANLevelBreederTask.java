package edu.southwestern.tasks.interactive.gvgai;

import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.gvgai.zelda.ZeldaGANLevelTask;
import edu.southwestern.tasks.gvgai.zelda.ZeldaGANUtil;
import edu.southwestern.tasks.gvgai.zelda.dungeon.Dungeon;
import edu.southwestern.tasks.gvgai.zelda.dungeon.DungeonUtil;
import edu.southwestern.tasks.gvgai.zelda.dungeon.GraphDungeon;
import edu.southwestern.tasks.gvgai.zelda.dungeon.ZeldaDungeon;
import edu.southwestern.tasks.gvgai.zelda.dungeon.ZeldaDungeon.Level;
import edu.southwestern.tasks.gvgai.zelda.level.ZeldaLevelUtil;
import edu.southwestern.tasks.interactive.InteractiveGANLevelEvolutionTask;
import edu.southwestern.tasks.mario.gan.GANProcess;
import edu.southwestern.util.datastructures.ArrayUtil;
import edu.southwestern.util.datastructures.Pair;
import me.jakerg.rougelike.Tile;

/**
 * Evolve Zelda rooms using a GAN
 * 
 * @author Jacob Schrum
 */
public class ZeldaGANLevelBreederTask extends InteractiveGANLevelEvolutionTask {

	private static final int DUNGEONIZE_BUTTON_INDEX = -19;
	
	private ZeldaDungeon<ArrayList<Double>> sd;
	
	/**
	 * Initializes the InteractiveGANLevelEvolutionTask and everything required for GVG-AI
	 * @throws IllegalAccessException
	 */
	public ZeldaGANLevelBreederTask() throws IllegalAccessException {
		// false: Has Dungeonize instead of ability to play one room.
		super(false); // Initialize InteractiveGANLevelEvolutionTask
		
		sd = new GraphDungeon();
		
		JButton dungeonize = new JButton("Dungeonize");
		dungeonize.setName("" + DUNGEONIZE_BUTTON_INDEX);
		dungeonize.setToolTipText("Take selected rooms and randomly combine them into a playable dungeon (may not use all rooms).");
		dungeonize.addActionListener(this);
		
		if(Parameters.parameters.booleanParameter("bigInteractiveButtons")) {
			dungeonize.setFont(new Font("Arial", Font.PLAIN, BIG_BUTTON_FONT_SIZE));
		}
		
		top.add(dungeonize);
		
		JPanel rulesAndBackbones = new JPanel();
		rulesAndBackbones.setLayout(new BoxLayout(rulesAndBackbones, BoxLayout.Y_AXIS));
		
		
		String[] ruleChoices = { "Standard", "Complex" };
		JComboBox<String> ruleChoice = new JComboBox<String>(ruleChoices);
		ruleChoice.setSize(40, 40);
		ruleChoice.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				// TODO Auto-generated method stub
				@SuppressWarnings("unchecked")
				JComboBox<String> source = (JComboBox<String>)e.getSource();
				//it it's horizontal, it's not vertical
				if(source.getSelectedItem().toString() == "Standard") {
					Parameters.parameters.setClass("zeldaGrammarRules", edu.southwestern.tasks.gvgai.zelda.level.ZeldaHumanSubjectStudy2019GraphGrammar.class);
				} else if(source.getSelectedItem().toString() == "Complex"){ 
					Parameters.parameters.setClass("zeldaGrammarRules", edu.southwestern.tasks.gvgai.zelda.level.MoreInterestingGraphGrammarRules.class);

				}
				//reset buttons
				//resetButtons(true); // Not needed, since rooms are the same ... only dungeon generation changes.
			}
			});
		JPanel rulePanel = new JPanel();
		rulePanel.setLayout(new BoxLayout(rulePanel, BoxLayout.X_AXIS));
		JLabel ruleLabel = new JLabel();
		ruleLabel.setText("Grammar Rules: ");
		rulePanel.add(ruleLabel);		
		rulePanel.add(ruleChoice);
		//top.add(rulePanel);
		
		
		String[] backboneChoices = { "Standard", "Simple", "Boring", "Interesting", "Raft Test" };
		JComboBox<String> backboneChoice = new JComboBox<String>(backboneChoices);
		backboneChoice.setSize(40, 40);
		backboneChoice.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				// TODO Auto-generated method stub
				@SuppressWarnings("unchecked")
				JComboBox<String> source = (JComboBox<String>)e.getSource();
				//it it's horizontal, it's not vertical
				if(source.getSelectedItem().toString() == "Standard") {
					Parameters.parameters.setClass("zeldaGraphBackBone", edu.southwestern.tasks.gvgai.zelda.level.graph.HumanSubjectStudy2019Graph.class);
				} else if(source.getSelectedItem().toString() == "Boring"){ 
					Parameters.parameters.setClass("zeldaGraphBackBone", edu.southwestern.tasks.gvgai.zelda.level.graph.BoringDungeonBackbone.class);

				} else if(source.getSelectedItem().toString() == "Simple"){ 
					Parameters.parameters.setClass("zeldaGraphBackBone", edu.southwestern.tasks.gvgai.zelda.level.graph.SimpleDungeonBackbone.class);

				} else if(source.getSelectedItem().toString() == "Interesting"){ 
					Parameters.parameters.setClass("zeldaGraphBackBone", edu.southwestern.tasks.gvgai.zelda.level.graph.InterestingZeldaGraph.class);

				} else if(source.getSelectedItem().toString() == "Raft Test"){ 
					Parameters.parameters.setClass("zeldaGraphBackBone", edu.southwestern.tasks.gvgai.zelda.level.graph.RaftTestingGraph.class);

				}
				//reset buttons
				//resetButtons(true); // Not needed, since rooms are the same ... only dungeon generation changes.
			}
			});
		JPanel backbonePanel = new JPanel();
		backbonePanel.setLayout(new BoxLayout(backbonePanel, BoxLayout.X_AXIS));
		JLabel backboneLabel = new JLabel();
		backboneLabel.setText("Grammar backbones: ");
		backbonePanel.add(backboneLabel);		
		backbonePanel.add(backboneChoice);
		//top.add(backbonePanel);
		
		rulesAndBackbones.add(rulePanel);
		rulesAndBackbones.add(backbonePanel);
	}

	/**
	 * Override to set the window title to associate with our ZeldaGAN
	 * @return String for title of window
	 */
	@Override
	protected String getWindowTitle() {
		return "ZeldaGAN Level Breeder";
	}
	
	/**
	 * Creates a BufferedImage that represents the level on the button
	 * @param phenotype Latent vector
	 * @param width Width of image in pixels
	 * @param height Height of image in pixels
	 * @param inputMultipliers Determines whether CPPN is on or off, not used in function
	 * @returns BufferedImage image of the level on the button
	 */
	@Override
	protected BufferedImage getButtonImage(ArrayList<Double> phenotype, int width, int height, double[] inputMultipliers) {
		Dungeon dummy = new Dungeon();
		List<List<Integer>> ints = ZeldaGANUtil.generateOneRoomListRepresentationFromGAN(ArrayUtil.doubleArrayFromList(phenotype));
		//Prevents doors from being displayed before Dungeonize is clicked
		ints.get(ZeldaLevelUtil.CLOSE_EDGE_DOOR_COORDINATE).set(ZeldaLevelUtil.SMALL_DOOR_COORDINATE_START, Tile.WALL.getNum());
		ints.get(ZeldaLevelUtil.CLOSE_EDGE_DOOR_COORDINATE).set(ZeldaLevelUtil.SMALL_DOOR_COORDINATE_END, Tile.WALL.getNum());
		ints.get(ZeldaLevelUtil.FAR_SHORT_EDGE_DOOR_COORDINATE).set(ZeldaLevelUtil.SMALL_DOOR_COORDINATE_START, Tile.WALL.getNum());
		ints.get(ZeldaLevelUtil.FAR_SHORT_EDGE_DOOR_COORDINATE).set(ZeldaLevelUtil.SMALL_DOOR_COORDINATE_END, Tile.WALL.getNum());
		ints.get(ZeldaLevelUtil.BIG_DOOR_COORDINATE_START).set(ZeldaLevelUtil.CLOSE_EDGE_DOOR_COORDINATE, Tile.WALL.getNum());
		ints.get(ZeldaLevelUtil.BIG_DOOR_COORDINATE_START+1).set(ZeldaLevelUtil.CLOSE_EDGE_DOOR_COORDINATE, Tile.WALL.getNum());
		ints.get(ZeldaLevelUtil.BIG_DOOR_COORDINATE_END).set(ZeldaLevelUtil.CLOSE_EDGE_DOOR_COORDINATE, Tile.WALL.getNum());
		ints.get(ZeldaLevelUtil.BIG_DOOR_COORDINATE_START).set(ZeldaLevelUtil.FAR_LONG_EDGE_DOOR_COORDINATE, Tile.WALL.getNum());
		ints.get(ZeldaLevelUtil.BIG_DOOR_COORDINATE_START+1).set(ZeldaLevelUtil.FAR_LONG_EDGE_DOOR_COORDINATE, Tile.WALL.getNum());
		ints.get(ZeldaLevelUtil.BIG_DOOR_COORDINATE_END).set(ZeldaLevelUtil.FAR_LONG_EDGE_DOOR_COORDINATE, Tile.WALL.getNum());
		for(List<Integer> row : ints) {
			for(Integer i : row) {
				System.out.print(i + ", ");
			}
			System.out.println();
		}
		Level level = new Level(ints);
		Dungeon.Node n = null;
		try {
			n = dummy.newNode("ASDF", level);
		} catch (Exception e) {
			System.out.println("Error creating Zelda Dungeon");
			e.printStackTrace();
			System.exit(1);
		}
		dummy.setCurrentLevel("ASDF");
		return DungeonUtil.getLevelImage(n, dummy);		
	}

	/**
	 * Set the GAN Process to type ZELDA
	 */
	@Override
	public void configureGAN() {
		GANProcess.type = GANProcess.GAN_TYPE.ZELDA;
	}

	/**
	 * Function to get the file name of the Zelda GAN Model
	 * @returns String the file name of the GAN Model
	 */
	@Override
	public String getGANModelParameterName() {
		return "zeldaGANModel";
	}

	/**
	 * 
	 * @param model Name of the model to reconfigure
	 * @returns Pair of the old latent vector and the net latent vector
	 */
	@Override
	public Pair<Integer, Integer> resetAndReLaunchGAN(String model) {
		return staticResetAndReLaunchGAN(model);
	}

	public static Pair<Integer, Integer> staticResetAndReLaunchGAN(String model) {
		int oldLength = GANProcess.latentVectorLength(); // for old model
		// Need to parse the model name to find out the latent vector size
		String dropDataSource = model.substring(model.indexOf("_")+1);
		String dropEpochs = dropDataSource.substring(dropDataSource.indexOf("_")+1);
		String latentSize = dropEpochs.substring(0,dropEpochs.indexOf("."));
		int size = Integer.parseInt(latentSize);
		Parameters.parameters.setInteger("GANInputSize", size);

		boolean fixed = model.startsWith("ZeldaFixed");
		Parameters.parameters.setBoolean("zeldaGANUsesOriginalEncoding", !fixed);
		
		GANProcess.terminateGANProcess();
		// Because Python process was terminated, latentVectorLength will reinitialize with the new params
		int newLength = GANProcess.latentVectorLength(); // new model
		return new Pair<>(oldLength, newLength);
	}

	/**
	 * Set the path of the Zelda GAN Model
	 * @returns String path to GAN model
	 */
	@Override
	public String getGANModelDirectory() {
		return GANProcess.PYTHON_BASE_PATH+"ZeldaGAN";
	}
	
	@Override
	protected void save(String file, int i) {
		ArrayList<Double> phenotype = scores.get(i).individual.getPhenotype();
		double[] latentVector = ArrayUtil.doubleArrayFromList(phenotype);
		List<List<Integer>> level = ZeldaGANUtil.generateOneRoomListRepresentationFromGAN(latentVector);
		int[][] levelArray = ZeldaLevelUtil.listToArray(level);
		int distance = ZeldaLevelUtil.findMaxDistanceOfLevel(levelArray, 5, 7);

		/**
		 * Rather than save a text representation of the level, I simply save
		 * the latent vector and the model name, which are sufficient to
		 * recreate any level
		 */
		try {
			PrintStream ps = new PrintStream(new File(file));
			// Write String array to text file 
			ps.println(Parameters.parameters.stringParameter(getGANModelParameterName()));
			ps.println(phenotype);
			for(List<Integer> row : level) {
				for(Integer tile : row) {
					ps.print(tile + " ");
				}
				ps.println();
			}
			ps.println("Max Distance : " + distance);
			ps.close();
		} catch (FileNotFoundException e) {
			System.out.println("Could not save file: " + file);
			e.printStackTrace();
			return;
		}
	}

	protected boolean respondToClick(int itemID) {
		boolean undo = super.respondToClick(itemID);
		if (undo) return true;
		if(itemID == DUNGEONIZE_BUTTON_INDEX) {
			if(selectedItems.size() == 0) {
				JOptionPane.showMessageDialog(null, "Must select rooms to build the dungeon with.");
				return false; // Nothing to explore
			}
			
			ArrayList<ArrayList<Double>> phenotypes = new ArrayList<>();
			for(Integer i : selectedItems) {
				phenotypes.add(scores.get(i).individual.getPhenotype());
			}
			
			int exceptionCount = 0;
			boolean success = false;
			// Give 3 chances to get it right
			while(exceptionCount < 3 && ! success) {
				try {
					sd.showDungeon(phenotypes, 10);
					success = true;
				} catch (Exception e) {
					// On failure, just try again ... there seem to be occasional crashes. Need to fix eventually.
					exceptionCount++;
					e.printStackTrace();
				}
			}
		}
		return false;
	}
	
	public static void main(String[] args) {
		try {
			// Run the MMNeat Main method with parameters specifying that we want to run the Zedla GAN 
			//MMNEAT.main(new String[]{"runNumber:0","randomSeed:1","showKLOptions:false","allowInteractiveEvolution:false","trials:1","mu:16","zeldaGANModel:ZeldaFixedDungeonsAll_5000_10.pth","maxGens:500","io:false","netio:false","GANInputSize:10","mating:true","fs:false","task:edu.southwestern.tasks.interactive.gvgai.ZeldaGANLevelBreederTask","genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype","watch:false","cleanFrequency:-1","simplifiedInteractiveInterface:false","saveAllChampions:true","cleanOldNetworks:false","ea:edu.southwestern.evolution.selectiveBreeding.SelectiveBreedingEA","imageWidth:2000","imageHeight:2000","imageSize:200", "zeldaGANUsesOriginalEncoding:false"});
			//MMNEAT.main(new String[]{"runNumber:0","randomSeed:1","showKLOptions:false","showLatentSpaceOptions:false","trials:1","mu:16","zeldaGANModel:ZeldaFixedDungeonsAll_5000_10.pth","maxGens:500","io:false","netio:false","GANInputSize:10","mating:true","fs:false","task:edu.southwestern.tasks.interactive.gvgai.ZeldaGANLevelBreederTask","genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype","watch:false","cleanFrequency:-1","simplifiedInteractiveInterface:false","saveAllChampions:true","cleanOldNetworks:false","ea:edu.southwestern.evolution.selectiveBreeding.SelectiveBreedingEA","imageWidth:2000","imageHeight:2000","imageSize:200", "zeldaGANUsesOriginalEncoding:false"});
			MMNEAT.main(new String[]{"runNumber:0","randomSeed:1","arrayCrossover:edu.southwestern.evolution.crossover.ArrayCrossover","bigInteractiveButtons:false","showKLOptions:false","trials:1","mu:16","zeldaGANModel:ZeldaFixedDungeonsAll_5000_10.pth","maxGens:500","io:false","netio:false","GANInputSize:10","mating:true","fs:false","task:edu.southwestern.tasks.interactive.gvgai.ZeldaGANLevelBreederTask","genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype","watch:false","cleanFrequency:-1","simplifiedInteractiveInterface:false","saveAllChampions:true","cleanOldNetworks:false","ea:edu.southwestern.evolution.selectiveBreeding.SelectiveBreedingEA","imageWidth:2000","imageHeight:2000","imageSize:200", "zeldaGANUsesOriginalEncoding:false", "zeldaGraphBackBone:edu.southwestern.tasks.gvgai.zelda.level.graph.SimpleDungeonBackbone"});
		} catch (FileNotFoundException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Transform latent vector into a 2D list representing a Zelda level
	 * @param latentVector 1D double array of the latent vector
	 * @returns List<List<Integer>> 2D list of integers representing tiles of the Zelda level
	 */
	@Override
	public List<List<Integer>> levelListRepresentation(double[] latentVector) {
		return ZeldaGANUtil.generateOneRoomListRepresentationFromGAN(latentVector);
	}

	@Override
	public double[] getUpperBounds() {
		return ZeldaGANLevelTask.getStaticUpperBounds();
	}

	@Override
	public double[] getLowerBounds() {
		return ZeldaGANLevelTask.getStaticLowerBounds();
	}

	@Override
	public void playLevel(ArrayList<Double> phenotype) {
		throw new UnsupportedOperationException("Can't play level without GVG-AI? Can this be reached?");
	}
}
