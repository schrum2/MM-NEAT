package edu.southwestern.tasks.evocraft.shapegeneration;

import static org.junit.Assert.*;

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
import edu.southwestern.util.ClassCreation;

public class ShapeGeneratorTest {


	private static Genotype<Network> netGen;
	private static SnakeGenerator<Network> generator;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		
		
		generator = new SnakeGenerator<Network>();
		
		netGen = new Genotype<Network>() {

			@Override
			public void addParent(long id) {
				// Not used
				
			}

			@Override
			public List<Long> getParentIDs() {
				// Not used
				return null;
			}

			@Override
			public Genotype<Network> copy() {
				// Not used
				return null;
			}

			@Override
			public void mutate() {
				// Not used
			}

			@Override
			public Genotype<Network> crossover(Genotype<Network> g) {
				// Not used
				return null;
			}

			
			private int counter = 0;
			@Override
			public Network getPhenotype() {
				return new Network() {
					
					
					boolean redirectSnake = Parameters.parameters.booleanParameter("minecraftRedirectConfinedSnakes");
					boolean stopSnakes = Parameters.parameters.booleanParameter("minecraftStopConfinedSnakes");
					boolean evolveOrientation = Parameters.parameters.booleanParameter("minecraftEvolveOrientation");

					@Override
					public int numInputs() {
						return 5;
					}

					@Override
					public int numOutputs() {
						if(redirectSnake || stopSnakes) return generator.getNetworkOutputLabels().length;
						else return ShapeGenerator.defaultNetworkOutputLabels(MMNEAT.blockSet).length;
					}

					@Override
					public int effectiveNumOutputs() {
						return ShapeGenerator.defaultNetworkOutputLabels(MMNEAT.blockSet).length;
					}

					@Override
					public double[] process(double[] inputs) {
						double[] outputs = new double[numOutputs()];
						int direction = 0;
						int orientation = 0;
						// Presence and block value set to 1
						outputs[0] = 1.0;
						outputs[1] = 1.0;
						if(counter % 2 == 0) {
							System.out.println("Counter is even: " + counter);
							if(redirectSnake || stopSnakes) {
								direction = 4; // (1,0,0)
							}
							if(evolveOrientation) {
								orientation = 3; // south
							}
						} else {
							System.out.println("Counter is odd: " + counter);
							if(redirectSnake || stopSnakes) {
								direction = 3; // (0,0,-1)
							}
							if(evolveOrientation) {
								orientation = 2; // west
							}
						}
						counter++;
						System.out.println("Counter after adding: " + counter);
						int numBlocks = MMNEAT.blockSet.getPossibleBlocks().length;
						
						if(redirectSnake || stopSnakes) {
							outputs[1 + numBlocks + direction]= 1.0;
							outputs[outputs.length - 1] = 1.0; // so snake generation will continue
						}
						
						if(evolveOrientation) {
							outputs[1 + numBlocks + orientation] = 1.0;
						}
						
						if((redirectSnake || stopSnakes) && evolveOrientation) {
							outputs[1+ numBlocks + ShapeGenerator.NUM_DIRECTIONS + direction] = 1.0;
							outputs[1 + numBlocks + orientation] = 1.0;
							outputs[outputs.length - 1] = 1.0;
						}
						
						return outputs;
					}

					@Override
					public void flush() {
						// Not needed
					}

					@Override
					public boolean isMultitask() {
						// Not needed
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
						return 0;
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
				// Not needed
				return null;
			}

			@Override
			public long getId() {
				// Not needed
				return 0;
			}
			
		};
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void testGenerateBlock() {
		
		// Corner used for the tests
		MinecraftCoordinates corner = new MinecraftCoordinates(0,5,0);
		
		// First Test: no snek and evolve orientation
		Parameters.initializeParameterCollections(new String[] {"objectBreederDistanceInEachPlane:false","minecraftEvolveOrientation:true"});
		// Block list which one block
		List<Block> b = new ArrayList<>();
		b.add(new Block(1,1,1,BlockType.QUARTZ_BLOCK,Orientation.SOUTH));
		// Using the machine blockSet
		BlockSet blockSet = new MachineBlockSet();
		MMNEAT.blockSet = blockSet;
		// Ranges
		MinecraftCoordinates ranges = new MinecraftCoordinates(Parameters.parameters.integerParameter("minecraftXRange"),
				Parameters.parameters.integerParameter("minecraftYRange"),
				Parameters.parameters.integerParameter("minecraftZRange"));
		
		//System.out.println(ShapeGenerator.generateBlock(corner,blockSet, b, netGen.getPhenotype(), ranges, Parameters.parameters.booleanParameter("objectBreederDistanceInEachPlane"), 0, 0, 0));

		assertEquals(null,ShapeGenerator.generateBlock(corner,blockSet, b, netGen.getPhenotype(), ranges, Parameters.parameters.booleanParameter("objectBreederDistanceInEachPlane"), 0, 0, 0));
		
		// Second Test: redirect snakes and evolve orientation
		Parameters.initializeParameterCollections(new String[] {"objectBreederDistanceInEachPlane:false","minecraftEvolveOrientation:true","minecraftRedirectConfinedSnakes:true",
		"minecraftShapeGenerator:edu.southwestern.tasks.evocraft.shapegeneration.SnakeGenerator"});
	
		// For this test, the shape generator is a snake one
		MMNEAT.shapeGenerator = generator;

		assertEquals(new MinecraftCoordinates(1,0,0),ShapeGenerator.generateBlock(corner,blockSet, b, netGen.getPhenotype(), ranges, Parameters.parameters.booleanParameter("objectBreederDistanceInEachPlane"), 0, 0, 0));
	
		// Third test: evolve orientation and stop snakes
		Parameters.initializeParameterCollections(new String[] {"objectBreederDistanceInEachPlane:false","minecraftEvolveOrientation:true","minecraftRedirectConfinedSnakes:false","minecraftStopConfinedSnakes:true",
		"minecraftShapeGenerator:edu.southwestern.tasks.evocraft.shapegeneration.SnakeGenerator"});
		
		assertEquals(new MinecraftCoordinates(0,1,0),ShapeGenerator.generateBlock(corner,blockSet, b, netGen.getPhenotype(), ranges, Parameters.parameters.booleanParameter("objectBreederDistanceInEachPlane"), 0, 0, 0));

		// Fourth test: no evolve orientation and stop snakes
		Parameters.initializeParameterCollections(new String[] {"objectBreederDistanceInEachPlane:false","minecraftEvolveOrientation:false","minecraftRedirectConfinedSnakes:false","minecraftStopConfinedSnakes:true",
		"minecraftShapeGenerator:edu.southwestern.tasks.evocraft.shapegeneration.SnakeGenerator"});
		
		assertEquals(new MinecraftCoordinates(1,0,0),ShapeGenerator.generateBlock(corner,blockSet, b, netGen.getPhenotype(), ranges, Parameters.parameters.booleanParameter("objectBreederDistanceInEachPlane"), 0, 0, 0));

		//System.out.println(ShapeGenerator.generateBlock(corner,blockSet, b, netGen.getPhenotype(), ranges, Parameters.parameters.booleanParameter("objectBreederDistanceInEachPlane"), 0, 0, 0));
	
		// Last test: no evolve orientation and redirect snakes
		Parameters.initializeParameterCollections(new String[] {"objectBreederDistanceInEachPlane:false","minecraftEvolveOrientation:false","minecraftRedirectConfinedSnakes:true","minecraftStopConfinedSnakes:false",
		"minecraftShapeGenerator:edu.southwestern.tasks.evocraft.shapegeneration.SnakeGenerator"});
		
		assertEquals(new MinecraftCoordinates(0,1,0),ShapeGenerator.generateBlock(corner,blockSet, b, netGen.getPhenotype(), ranges, Parameters.parameters.booleanParameter("objectBreederDistanceInEachPlane"), 0, 0, 0));
		
		//System.out.println(ShapeGenerator.generateBlock(corner,blockSet, b, netGen.getPhenotype(), ranges, Parameters.parameters.booleanParameter("objectBreederDistanceInEachPlane"), 0, 0, 0));

	}

}
