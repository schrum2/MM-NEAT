package edu.southwestern.tasks.evocraft.shapegeneration;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.networks.Network;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.BlockType;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.tasks.evocraft.MinecraftClient.Orientation;
import edu.southwestern.tasks.evocraft.blocks.BlockSet;
import edu.southwestern.tasks.evocraft.blocks.MachineBlockSet;

public class SnakeGeneratorTest {

	private static SnakeGenerator<Network> generator;
	private static Genotype<Network> netGen;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Parameters.initializeParameterCollections(new String[] {"objectBreederDistanceInEachPlane:false","minecraftEvolveOrientation:true","minecraftRedirectConfinedSnakes:true",
				"minecraftShapeGenerator:edu.southwestern.tasks.evocraft.shapegeneration.SnakeGenerator"});
		//MMNEAT.loadClasses();
		generator = new SnakeGenerator<Network>();
		
		netGen = new Genotype<Network>() {

			@Override
			public void addParent(long id) {
				// Won't be used
			}

			@Override
			public List<Long> getParentIDs() {
				// Won't be used
				return null;
			}

			@Override
			public Genotype<Network> copy() {
				// Won't be used
				return null;
			}

			@Override
			public void mutate() {
				// Won't be used
			}

			@Override
			public Genotype<Network> crossover(Genotype<Network> g) {
				// Won't be used
				return null;
			}

			@Override
			public Network getPhenotype() {
				// Return a fake Network that behaves the way we want
				return new Network() {

					private int counter = 0;
					
					@Override
					public int numInputs() {
						return 5; // Length of MinecraftShapeTask sensorLabels
					}

					@Override
					public int numOutputs() {
						return generator.getNetworkOutputLabels().length;
					}

					@Override
					public int effectiveNumOutputs() {
						return generator.getNetworkOutputLabels().length;
					}

					@Override
					public double[] process(double[] inputs) {
						int direction;
						double[] outputs = new double[numOutputs()];
						// Presence and a block value set to 1.0
						outputs[0]=1.0;
						outputs[1]=1.0;
						if(counter % 2 == 0) {
							// return some outputs
							direction = 5; // for (0,0,-1)
						} else {
							// return some other outputs
							direction = 2; // for (0,0,1)
						} 
						counter++;
						int numBlocks = MMNEAT.blockSet.getPossibleBlocks().length;
						outputs[1 + numBlocks + direction] = 1.0; // Is this orientation or snake movement direction?
						outputs[1 + numBlocks + ShapeGenerator.NUM_DIRECTIONS + direction] = 1.0; // Is this orientation or snake movement direction?
						outputs[outputs.length - 1] = 1.0; // so snake generation will continue
						return outputs;
					}

					@Override
					public void flush() {
						// Nothing
					}

					@Override
					public boolean isMultitask() {
						return false;
					}

					@Override
					public void chooseMode(int mode) {
						// Not needed
					}

					@Override
					public int lastModule() {
						// Not needed
						return 0;
					}

					@Override
					public double[] moduleOutput(int mode) {
						// Not needed
						return null;
					}

					@Override
					public int numModules() {
						// Not needed
						return 1;
					}

					@Override
					public int[] getModuleUsage() {
						// Not needed
						return null;
					}
					
				};
			}

			@Override
			public Genotype<Network> newInstance() {
				// Won't be used
				return null;
			}

			@Override
			public long getId() {
				// Won't be used
				return 0;
			}
			
		};
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void testGenerateShape() throws NoSuchMethodException {
		BlockSet blockSet = new MachineBlockSet();
		MMNEAT.blockSet = blockSet;
		MinecraftCoordinates corner = new MinecraftCoordinates(0,5,0);
		MMNEAT.shapeGenerator = generator;
		
		//System.out.println(generator.generateShape(netGen,corner,blockSet));
		
		List<Block> result = new ArrayList<>();
		result.add(new Block(5,10,5,BlockType.QUARTZ_BLOCK,Orientation.DOWN));
		result.add(new Block(5,10,6,BlockType.QUARTZ_BLOCK,Orientation.SOUTH));
		
		assertEquals(result,generator.generateShape(netGen,corner,blockSet));
	}

}
