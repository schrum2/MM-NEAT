package megaManMaker;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.fossgalaxy.object.annotations.Parameter;

import ch.idsia.mario.engine.level.Level;
import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.interactive.InteractiveGANLevelEvolutionTask;
import edu.southwestern.tasks.mario.gan.GANProcess;
import edu.southwestern.tasks.mario.gan.MarioGANUtil;
import edu.southwestern.tasks.mario.level.MarioLevelUtil;
import edu.southwestern.util.MiscUtil;
import edu.southwestern.util.datastructures.ArrayUtil;
import edu.southwestern.util.datastructures.Pair;

public class MegaManGANLevelBreederTask extends InteractiveGANLevelEvolutionTask{
	public static final int LEVEL_MIN_CHUNKS = 1;
	public static final int LEVEL_MAX_CHUNKS = 10;
	private boolean initializationComplete = false;
	protected JSlider levelChunksSlider;
	public MegaManGANLevelBreederTask() throws IllegalAccessException {
		super();

		levelChunksSlider = new JSlider(JSlider.HORIZONTAL, LEVEL_MIN_CHUNKS, LEVEL_MAX_CHUNKS, Parameters.parameters.integerParameter("megaManGANLevelChunks"));
		levelChunksSlider.setToolTipText("Determines the number of distinct latent vectors that are sent to the GAN to create level chunks which are patched together into a single level.");
		levelChunksSlider.setMinorTickSpacing(1);
		levelChunksSlider.setPaintTicks(true);
		Hashtable<Integer,JLabel> labels = new Hashtable<>();
		JLabel shorter = new JLabel("Shorter Level");
		JLabel longer = new JLabel("Longer Level");
		if(Parameters.parameters.booleanParameter("bigInteractiveButtons")) {
			shorter.setFont(new Font("Arial", Font.PLAIN, 23));
			longer.setFont(new Font("Arial", Font.PLAIN, 23));
		}
		labels.put(LEVEL_MIN_CHUNKS, shorter);
		labels.put(LEVEL_MAX_CHUNKS, longer);
		levelChunksSlider.setLabelTable(labels);
		levelChunksSlider.setPaintLabels(true);
		levelChunksSlider.setPreferredSize(new Dimension((int)(200 * (Parameters.parameters.booleanParameter("bigInteractiveButtons") ? 1.4 : 1)), 40 * (Parameters.parameters.booleanParameter("bigInteractiveButtons") ? 2 : 1)));

		/**
		 * Changed level width picture previews
		 */
		levelChunksSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if(!initializationComplete) return;
				// get value
				JSlider source = (JSlider)e.getSource();
				if(!source.getValueIsAdjusting()) {

					int oldValue = Parameters.parameters.integerParameter("megaManGANLevelChunks");
					int newValue = (int) source.getValue();
					Parameters.parameters.setInteger("megaManGANLevelChunks", newValue);
					//Parameters.parameters.setInteger("GANInputSize", 5*newValue); // Default latent vector size

					if(oldValue != newValue) {
						int oldLength = oldValue * GANProcess.latentVectorLength();
						int newLength = newValue * GANProcess.latentVectorLength();

						resizeGenotypeVectors(oldLength, newLength);
						resetButtons(true);

						// reset buttons
					}
				}
			}
		});

		if(!Parameters.parameters.booleanParameter("simplifiedInteractiveInterface")) {
			top.add(levelChunksSlider);	
		}

		initializationComplete = true;
	
	
	
	}

	
//	@Override
//	protected BufferedImage getButtonImage(ArrayList<Double> phenotype, int width, int height, double[] inputMultipliers) {
//		double[] doubleArray = ArrayUtil.doubleArrayFromList(phenotype);
//		Level level = MegaManGANUtil.generateLevelFromGAN(doubleArray);
//		BufferedImage image = MarioLevelUtil.getLevelImage(level);
//		return image;
//	}
	@Override
	public void configureGAN() {
		GANProcess.type = GANProcess.GAN_TYPE.MEGA_MAN;
		
	}

	@Override
	public String getGANModelParameterName() {
		// TODO Auto-generated method stub
		return "MegaManGANModel";
	}

	@Override
	public List<List<Integer>> levelListRepresentation(double[] latentVector) {
		// TODO Auto-generated method stub
		return MegaManGANUtil.generateOneLevelListRepresentationFromGAN(latentVector);
	}

	@Override
	public Pair<Integer, Integer> resetAndReLaunchGAN(String model) {
		return staticResetAndReLaunchGAN(model);
	}
	public static Pair<Integer, Integer> staticResetAndReLaunchGAN(String model) {
		int megaManGANLevelChunks = Parameters.parameters.integerParameter("megaManGANLevelChunks");
		int oldLength = megaManGANLevelChunks * GANProcess.latentVectorLength();
		Parameters.parameters.setInteger("GANInputSize", 5); // Default latent vector size
		
		GANProcess.terminateGANProcess();
		// Because Python process was terminated, latentVectorLength will reinitialize with the new params
		int newLength = megaManGANLevelChunks * GANProcess.latentVectorLength(); // new model
		return new Pair<>(oldLength,newLength);
	}
	@Override
	public String getGANModelDirectory() {
		return "src"+File.separator+"main"+File.separator+"python"+File.separator+"GAN"+File.separator+"MegaManGAN";
	}

	@Override
	public void playLevel(ArrayList<Double> phenotype) {
		double[] doubleArray = ArrayUtil.doubleArrayFromList(phenotype);
		List<List<Integer>> level = levelListRepresentation(doubleArray);
		int levelNumber = 2020;
		MegaManVGLCUtil.convertMegaManLevelToMMLV(level, levelNumber);
		//save level and play
	}

	@Override
	protected String getWindowTitle() {
		// TODO Auto-generated method stub
		return "MegaManGANLevelBreeder";
	}

	@Override
	protected BufferedImage getButtonImage(ArrayList<Double> phenotype, int width, int height,
			double[] inputMultipliers) {
		double[] doubleArray = ArrayUtil.doubleArrayFromList(phenotype);
		List<List<Integer>> level;
		if(Parameters.parameters.stringParameter("MegaManGANModel").startsWith("HORIZONTALONLY")) {
			level = levelListRepresentation(doubleArray);

//			System.out.println(Parameters.parameters.stringParameter("MegaManGANModel"));
//			MiscUtil.waitForReadStringAndEnterKeyPress();
		}else {
			level = MegaManGANUtil.generateOneLevelListRepresentationFromGANVertical(doubleArray);
		}
		//MegaManVGLCUtil.printLevel(level);
		BufferedImage[] images;
		//sets the height and width for the rendered level to be placed on the button 
		int width1 = MegaManRenderUtil.renderedImageWidth(level.get(0).size());
		int height1 = MegaManRenderUtil.renderedImageHeight(level.size());
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		try {

			images = MegaManRenderUtil.loadImagesForASTAR(MegaManRenderUtil.MEGA_MAN_TILE_PATH); //7 different tiles to display 
			image = MegaManRenderUtil.createBufferedImage(level,width1,height1, images);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;
	}
	
	
	/**
	 * Launches the level breeder, sets GAN input size to 5
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			MMNEAT.main(new String[]{"runNumber:0","randomSeed:1","bigInteractiveButtons:false","MegaManGANModel:VERTICALONLYMegaManAllLevelsWith7Tiles_5_Epoch5000.pth","GANInputSize:"+MegaManGANUtil.LATENT_VECTOR_SIZE,"showKLOptions:false","trials:1","mu:16","maxGens:500","io:false","netio:false","mating:true","fs:false","task:megaManMaker.MegaManGANLevelBreederTask","watch:true","cleanFrequency:-1","genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype","simplifiedInteractiveInterface:false","saveAllChampions:true","ea:edu.southwestern.evolution.selectiveBreeding.SelectiveBreedingEA","imageWidth:2000","imageHeight:2000","imageSize:200"});
		} catch (FileNotFoundException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

}
