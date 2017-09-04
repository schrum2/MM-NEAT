package edu.southwestern.tasks.mspacman.sensors;

import edu.southwestern.tasks.mspacman.sensors.blocks.MsPacManSensorBlock;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

/**
 * Like MultipleInputOutputMediator, but doesn't keep the mediators separate.
 * Actually merges the sensor blocks from each mediator as a set (no
 * duplicates).
 *
 * @author Jacob Schrum
 */
public abstract class UnionInputOutputMediator extends BlockLoadedInputOutputMediator {

	/**
	 * Finds the union of the mediators given, not including any sensor block
	 * duplicates from any mediator
	 * 
	 * @param mediators
	 *            given mediators to find the union of
	 */
	public UnionInputOutputMediator(List<BlockLoadedInputOutputMediator> mediators) {
		super();
		Comparator<MsPacManSensorBlock> c = new Comparator<MsPacManSensorBlock>() {
			public int compare(MsPacManSensorBlock o1, MsPacManSensorBlock o2) {
				return (o1.getClass().getName() + o1.hashCode()).compareTo(o2.getClass().getName() + o2.hashCode());
			}
		};
		TreeSet<MsPacManSensorBlock> union = new TreeSet<MsPacManSensorBlock>(c);
		for (BlockLoadedInputOutputMediator m : mediators) {
			union.addAll(m.blocks);
		}
		this.blocks.addAll(union);
		// for(int i = 0; i < blocks.size(); i++){
		// System.out.println(i +":" +
		// blocks.get(i).getClass().getSimpleName());
		// }
	}
}
