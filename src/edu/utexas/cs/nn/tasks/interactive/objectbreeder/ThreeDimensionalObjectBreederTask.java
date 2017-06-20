package edu.utexas.cs.nn.tasks.interactive.objectbreeder;

import edu.utexas.cs.nn.tasks.interactive.animationbreeder.AnimationBreederTask;

/**
 * Interface that interactively evolves three-dimensional
 * objects that are created originally with a CPPN. To complete this,
 * the program uses the interactive evolution interface
 * Original endless forms paper: http://yosinski.com/media/papers/Clune__2012__EndlessFormscomCollaborativelyEvolvingObjectsAnd3DPrinting.pdf
 * 
 * @author Isabel Tweraser
 *
 */
public class ThreeDimensionalObjectBreederTask extends AnimationBreederTask {

	public ThreeDimensionalObjectBreederTask() throws IllegalAccessException {
		super();
		
	}
	
	@Override
	protected String getWindowTitle() {
		return "3DObjectBreeder";
	}

}
