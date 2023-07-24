package edu.southwestern.tasks.evocraft.fitness;

import java.util.ArrayList;
import java.util.List;



import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.tasks.evocraft.MinecraftUtilClass;
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.datastructures.Vertex;

/**
 * NOTE: This fitness function seems to not really work. Evaluation freezes, and not sure why.
 * 
 * @author schrum2
 *
 */
public class SpecificTargetFitness extends TimedEvaluationMinecraftFitnessFunction {
	
	@Override
	public double calculateFinalScore(ArrayList<Pair<Long, List<Block>>> history, MinecraftCoordinates corner,
			List<Block> originalBlocks) {
		int xOffset = Parameters.parameters.integerParameter("minecraftTargetDistancefromShapeX");
		int yOffset = Parameters.parameters.integerParameter("minecraftTargetDistancefromShapeY");
		int zOffset = Parameters.parameters.integerParameter("minecraftTargetDistancefromShapeZ");
		MinecraftCoordinates targetCornerOffset = new MinecraftCoordinates(xOffset, yOffset, zOffset);
		double fitness = 0;
		MinecraftCoordinates targetCorner = new MinecraftCoordinates(targetCornerOffset.add(corner));
		Vertex blockLocation =new Vertex(targetCorner.x(),targetCorner.y(), targetCorner.z());
		fitness = MinecraftUtilClass.getCenterOfMass(history.get(history.size()-1).t2).distance(blockLocation);
		return -fitness;
	}

	@Override
	public double maxFitness() {
		return 0; // No distance to target
	}

	@Override
	public boolean shapeIsWorthSaving(double fitnessScore, ArrayList<Pair<Long, List<Block>>> history, MinecraftCoordinates shapeCorner, List<Block> originalBlocks) {
		//change later
		int xOffset = Parameters.parameters.integerParameter("minecraftTargetDistancefromShapeX");
		int yOffset = Parameters.parameters.integerParameter("minecraftTargetDistancefromShapeY");
		int zOffset = Parameters.parameters.integerParameter("minecraftTargetDistancefromShapeZ");
		MinecraftCoordinates targetCornerOffset = new MinecraftCoordinates(xOffset, yOffset, zOffset);
		
		MinecraftCoordinates targetCorner = new MinecraftCoordinates(targetCornerOffset.add(shapeCorner));
		Vertex blockLocation =new Vertex(targetCorner.x(),targetCorner.y(), targetCorner.z());
		double distance = MinecraftUtilClass.getCenterOfMass(history.get(history.size()-1).t2).distance(blockLocation);
		return (fitnessScore > (distance/2.0));
	}
	
	
	
}
