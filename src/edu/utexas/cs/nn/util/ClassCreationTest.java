package edu.utexas.cs.nn.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.awt.Point;
import java.lang.reflect.Constructor;

import org.junit.Test;

import edu.utexas.cs.nn.parameters.Parameters;

public class ClassCreationTest {

	@Test
	public void testCreateObjectString() throws NoSuchMethodException {
		/*mapping "task" to "java.awt.Point" or key "substrateMapping" to "java.lang.String" would never be done in actual use of this code,
		 *however for testing purposed it was important to construct objects with null parameters, so it is sufficient here to prove that
		 *the method is functioning.*/
		Parameters.initializeParameterCollections(new String[] { "io:false", "netio:false", "task:java.awt.Point", "substrateMapping:java.lang.String"});

		Point p = null;
		p = (Point) ClassCreation.createObject("task");
		assertNotNull(p);
		assertEquals(p, new Point(0,0));
		
		String s = null;
		s = (String) ClassCreation.createObject("substrateMapping");
		assertNotNull(s);
		assertEquals(s, "");
	}

	@Test
	public void testCreateObjectClass() throws NoSuchMethodException {  
		Point p = null;
		p = (Point) ClassCreation.createObject(Point.class);
		assertNotNull(p);
		assertEquals(p, new Point(0,0));
	}

	@Test
	public void testCreateObjectConstructor() throws NoSuchMethodException, SecurityException {
		Constructor<Point> pointConstructor = Point.class.getConstructor(new Class[0]);
		Point p = null;
		p = (Point) ClassCreation.createObject(pointConstructor);
		assertNotNull(p);
		assertEquals(p, new Point(0,0));
	}

	@Test
	public void testCreateObjectConstructorObjectArray() throws NoSuchMethodException, SecurityException {
		Constructor<Point> pointConstructor = Point.class.getConstructor(new Class[0]);
		Point p = null;
		p = (Point) ClassCreation.createObject(pointConstructor, new Object[0]);
		assertNotNull(p);
		assertEquals(p, new Point(0,0));
		
		
		pointConstructor = Point.class.getConstructor(new Class[]{Integer.TYPE, Integer.TYPE});
		p = null;
		p = (Point) ClassCreation.createObject(pointConstructor, new Object[]{4,5});
		assertNotNull(p);
		assertEquals(p, new Point(4,5));
	}

}
