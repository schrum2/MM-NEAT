package edu.southwestern.tasks.zelda;

import edu.southwestern.evolution.genotypes.CPPNOrDirectToGANGenotype;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.tasks.gvgai.zelda.dungeon.Dungeon;

@SuppressWarnings("rawtypes")
public class ZeldaCPPNOrDirectToGANDungeonTask extends ZeldaDungeonTask {

	@SuppressWarnings("unchecked")
	@Override
	public Dungeon getZeldaDungeonFromGenotype(Genotype individual) {
		// TODO Auto-generated method stub
		CPPNOrDirectToGANGenotype m = new CPPNOrDirectToGANGenotype();
		if(m.getFirstForm()) {
			System.out.println();

			System.out.println("First form");

			System.out.println();

			ZeldaCPPNtoGANDungeonTask k = new ZeldaCPPNtoGANDungeonTask();
			return k.getZeldaDungeonFromGenotype(individual);
		}else {
			System.out.println();

			System.out.println("Second form");

			System.out.println();

			ZeldaGANDungeonTask l = new ZeldaGANDungeonTask();
			return l.getZeldaDungeonFromGenotype(individual);
		}
	}

}
