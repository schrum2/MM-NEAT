package edu.southwestern.tasks.molecules;

import static org.junit.Assert.*;

import org.junit.Test;

public class MoleculeMutatorProcessTest {

	@Test
	public void testNormalizeSMILESString() {
		assertEquals(MoleculeMutatorProcess.normalizeSMILESString("C-C(-N(-C))-N"), "C-C(-N-C)-N");
		assertEquals(MoleculeMutatorProcess.normalizeSMILESString("C-C(-N-C)-N"), "C-C(-N-C)-N");
		assertEquals(MoleculeMutatorProcess.normalizeSMILESString("C-C(-N-C)-N(-C)"), "C-C(-N-C)-N-C");

		assertEquals(MoleculeMutatorProcess.normalizeSMILESString("C1=C-C(-N-C1)-N"), "C1=C-C(-N-C1)-N");
		assertEquals(MoleculeMutatorProcess.normalizeSMILESString("C1=C-C(-N(-C)-C1)-N"), "C1=C-C(-N(-C)-C1)-N");
		assertEquals(MoleculeMutatorProcess.normalizeSMILESString("C1=C-C(-N(-C)-C1(-C))-N"), "C1=C-C(-N(-C)-C1-C)-N");
	}

}
