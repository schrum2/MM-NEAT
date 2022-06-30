package edu.southwestern.tasks.zelda;

import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.tasks.gvgai.zelda.dungeon.Dungeon;

/**
 * Simply used to organize existing Zelda dungeons into MAP Elites archives.
 * 
 * @author schrum2
 *
 * @param <T>
 */
public class FakeZeldaDungeonTask<T> extends ZeldaDungeonTask<T> {

	@Override
	public Dungeon getZeldaDungeonFromGenotype(Genotype<T> individual) {
		throw new UnsupportedOperationException();
	}

}
