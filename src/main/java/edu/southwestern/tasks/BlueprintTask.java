/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.southwestern.tasks;

/**
 *
 * @author He_Deceives
 */
public interface BlueprintTask {

	public int getNumberBlueprintParentReferences();

	public int getTotalBlueprintReferences();

	public int getPreviousTotalBlueprintReferences();

	/**
	 * parent population *
	 */
	public int getPreviousNumberUnevaluatedReferences();

	/**
	 * child population *
	 */
	public int getNumberUnevaluatedReferences();

	public int getNumberFullParentBlueprints();

	public int getNumberFullChildBlueprints();
}
