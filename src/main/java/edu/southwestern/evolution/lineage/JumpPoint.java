package edu.southwestern.evolution.lineage;


	public class JumpPoint {

		int objective;
		double jump;
		Offspring individual;
		int generation;
		long comparisonId;

		public JumpPoint(int objective, double jump, Offspring individual, int generation, boolean firstParent) {
			this(objective, jump, individual, generation, firstParent ? individual.parentId1 : individual.parentId2);
		}

		public JumpPoint(int objective, double jump, Offspring individual, int generation, long comparisonId) {
			this.objective = objective;
			this.jump = jump;
			this.individual = individual;
			this.generation = generation;
			this.comparisonId = comparisonId;
		}

		@Override
		public String toString() {
			String result = "Obj. " + objective + " is " + jump + " higher in " + individual.offspringId + " than in "
					+ comparisonId + " at Gen. " + generation;
			return result;
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof JumpPoint) {
				JumpPoint jp = (JumpPoint) o;
				return jp.objective == objective && jp.individual.offspringId == individual.offspringId
						&& jp.generation == generation && jp.comparisonId == comparisonId;
			}
			return false;
		}

		@Override
		public int hashCode() {
			int hash = 7;
			hash = 79 * hash + this.objective;
			hash = 79 * hash + (this.individual != null ? this.individual.hashCode() : 0);
			hash = 79 * hash + this.generation;
			hash = 79 * hash + (int) (this.comparisonId ^ (this.comparisonId >>> 32));
			return hash;
		}
	}
