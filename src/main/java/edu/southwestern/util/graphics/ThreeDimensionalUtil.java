package edu.southwestern.util.graphics;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import edu.southwestern.networks.Network;
import edu.southwestern.tasks.interactive.objectbreeder.ThreeDimensionalObjectBreederTask;
import edu.southwestern.util.CartesianGeometricUtilities;
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.datastructures.Triangle;
import edu.southwestern.util.datastructures.Vertex;

/**
 * Series of utility methods associated with rendering 
 * a 3D object created from a series of vertexes
 * 
 * @author Isabel Tweraser
 *
 */
public class ThreeDimensionalUtil {
	
	public static final int THREE_DIMENSIONAL_VOXEL_INDEX = 0;
	public static final int THREE_DIMENSIONAL_HUE_INDEX = 1;
	public static final int THREE_DIMENSIONAL_SATURATION_INDEX = 2;
	public static final int THREE_DIMENSIONAL_BRIGHTNESS_INDEX = 3;
	public static final int THREE_DIMENSIONAL_X_DISPLACEMENT_INDEX = 4;
	public static final int THREE_DIMENSIONAL_Y_DISPLACEMENT_INDEX = 5;
	public static final int THREE_DIMENSIONAL_Z_DISPLACEMENT_INDEX = 6;
	
	public static final double SHADE_CONSTANT = 2.4;
	public static final double VOXEL_EXPRESSION_THRESHOLD = 0.1;

	/**
	 * Constructs a BufferedImage of the current 3D image rotation based on the current heading and pitch.
	 * 
	 * @param tris list of triangles
	 * @param width width of image
	 * @param height height of image
	 * @param heading Input horizontal position from JSlider
	 * @param pitch Input vertical position from JSlider
	 * @return BufferedImage representing current view of 3D image
	 */
	public static BufferedImage imageFromTriangles(List<Triangle> tris, int width, int height, double heading, double pitch, Color color) {
		Matrix3 transform = getTransform(heading, pitch);
		return imageFromTriangles(tris, width, height, transform, color);
	}

	/**
	 * Creates Matrix3 instance that is used to transform/manipulate a list of Triangles into a 
	 * rendered cube.
	 * 
	 * @param heading Input horizontal position from JSlider
	 * @param pitch Input vertical position from JSlider
	 * @return Matrix3 used to manipulate vectors of triangles in list
	 */
	private static Matrix3 getTransform(double heading, double pitch) {
		Matrix3 headingTransform = new Matrix3(new double[] {
				Math.cos(heading), 0, -Math.sin(heading),
				0, 1, 0,
				Math.sin(heading), 0, Math.cos(heading)
		});
		Matrix3 pitchTransform = new Matrix3(new double[] {
				1, 0, 0,
				0, Math.cos(pitch), Math.sin(pitch),
				0, -Math.sin(pitch), Math.cos(pitch)
		});
		Matrix3 transform = headingTransform.multiply(pitchTransform);
		return transform;
	}

	/**
	 * Constructs BufferedImage from list of triangles based on the input Matrix3 specifications
	 * (positions of JSliders determining rotation of 3D image)
	 * 
	 * @param tris list of triangles
	 * @param width width of image
	 * @param height height of image
	 * @param transform Matrix3 instance determining where JSlider rotation occurs for image construction
	 * @param bgColor Color of the background in the image
	 * @return BufferedImage representing current view of 3D image
	 */
	private static BufferedImage imageFromTriangles(List<Triangle> tris, int width, int height, Matrix3 transform, Color bgColor) {
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		if(bgColor != null) {
			for(int i = 0; i < width; i++) {
				for(int j = 0; j < height; j++) {
					img.setRGB(i, j, bgColor.getRGB());
				}
			}
		}

		double[] zBuffer = new double[img.getWidth() * img.getHeight()];
		// initialize array with extremely far away depths
		for (int q = 0; q < zBuffer.length; q++) {
			zBuffer[q] = Double.NEGATIVE_INFINITY;
		}

		for (Triangle t : tris) {
			Vertex v1 = transform.transform(t.v1);
			v1.x += width / 2;
			v1.y += height / 2;
			Vertex v2 = transform.transform(t.v2);
			v2.x += width / 2;
			v2.y += height / 2;
			Vertex v3 = transform.transform(t.v3);
			v3.x += width / 2;
			v3.y += height / 2;

			Vertex norm = getNorm(v1, v2, v3);

			double angleCos = Math.abs(norm.z);

			int minX = (int) Math.max(0, Math.ceil(Math.min(v1.x, Math.min(v2.x, v3.x))));
			int maxX = (int) Math.min(img.getWidth() - 1, Math.floor(Math.max(v1.x, Math.max(v2.x, v3.x))));
			int minY = (int) Math.max(0, Math.ceil(Math.min(v1.y, Math.min(v2.y, v3.y))));
			int maxY = (int) Math.min(img.getHeight() - 1, Math.floor(Math.max(v1.y, Math.max(v2.y, v3.y))));

			double triangleArea = (v1.y - v3.y) * (v2.x - v3.x) + (v2.y - v3.y) * (v3.x - v1.x);
			for (int y = minY; y <= maxY; y++) {
				for (int x = minX; x <= maxX; x++) {
					double b1 = ((y - v3.y) * (v2.x - v3.x) + (v2.y - v3.y) * (v3.x - x)) / triangleArea;
					double b2 = ((y - v1.y) * (v3.x - v1.x) + (v3.y - v1.y) * (v1.x - x)) / triangleArea;
					double b3 = ((y - v2.y) * (v1.x - v2.x) + (v1.y - v2.y) * (v2.x - x)) / triangleArea;
					if (b1 >= 0 && b1 <= 1 && b2 >= 0 && b2 <= 1 && b3 >= 0 && b3 <= 1) {
						double depth = b1 * v1.z + b2 * v2.z + b3 * v3.z;
						int zIndex = y * img.getWidth() + x;
						if (zBuffer[zIndex] < depth) {
							img.setRGB(x, y, getShade(t.color, angleCos).getRGB());
							zBuffer[zIndex] = depth;
						}
					}
				}
			}
		}
		return img;
	}

	private static Vertex getNorm(Vertex v1, Vertex v2, Vertex v3) {
		Vertex ab = new Vertex(v2.x - v1.x, v2.y - v1.y, v2.z - v1.z);
		Vertex ac = new Vertex(v3.x - v1.x, v3.y - v1.y, v3.z - v1.z);
		Vertex norm = new Vertex(
				ab.y * ac.z - ab.z * ac.y,
				ab.z * ac.x - ab.x * ac.z,
				ab.x * ac.y - ab.y * ac.x
				);
		double normalLength = Math.sqrt(norm.x * norm.x + norm.y * norm.y + norm.z * norm.z);
		norm.x /= normalLength;
		norm.y /= normalLength;
		norm.z /= normalLength;
		return norm;
	}

	/**
	 * Returns shading of color based on angle to make rendering of three
	 * dimensions look more accurate.
	 * 
	 * @param color Color being shaded
	 * @param shade Result color after shading
	 * @return
	 */
	public static Color getShade(Color color, double shade) {
		double redLinear = Math.pow(color.getRed(), SHADE_CONSTANT) * shade;
		double greenLinear = Math.pow(color.getGreen(), SHADE_CONSTANT) * shade;
		double blueLinear = Math.pow(color.getBlue(), SHADE_CONSTANT) * shade;

		int red = (int) Math.pow(redLinear, 1/SHADE_CONSTANT);
		int green = (int) Math.pow(greenLinear, 1/SHADE_CONSTANT);
		int blue = (int) Math.pow(blueLinear, 1/SHADE_CONSTANT);

		return new Color(red, green, blue);
	}

	/**
	 * Takes in a list of vertexes, a triangle sidelength, and a desired color and 
	 * returns a list of triangles that construct a series of cubes centered at the various
	 * vertexes. 
	 * 
	 * @param centers List of vertexes where cubes will be constructed
	 * @param sideLength length of triangle side
	 * @param color desired color of cubes
	 * @return List of triangles that construct a series of cubes centered at the various vertexes
	 */
	public static List<Triangle> getShape(List<Vertex> centers, int imageWidth, int imageHeight, double sideLength, double[] inputMultiples, List<Color> colors) {
		List<Triangle> tris = new ArrayList<>();
		for(int i = 0; i < centers.size(); i++) { //construct individual cubes and add them to larger list
			tris.addAll(cubeConstructor(centers.get(i), sideLength, colors.get(i)));
		}
		return tris;
	}

	/**
	 * Creates a list of vertexes where cube pixels will be constructed in a shape based on 
	 * the CPPN
	 * 
	 * @param cppn network used to modify shape being constructed
	 * @param imageWidth width of screen
	 * @param imageHeight height of screen
	 * @param cubeSize size of cube
	 * @param shapeWidth width of shape being constructed (measured in # of cubes in dimension)
	 * @param shapeHeight height of shape being constructed (measured in # of cubes in dimension)
	 * @param shapeDepth depth of shape being constructed (measured in # of cubes in dimension)
	 * @param inputMultipliers determines whether inputs are turned on or off
	 * @return List of vertexes denoting center points of all cubes being constructed
	 */
	public static Pair<List<Vertex>,List<Color>> getVertexesFromCPPN(Network cppn, int imageWidth, int imageHeight, int cubeSize, int shapeWidth, int shapeHeight, int shapeDepth, double[] inputMultipliers, Color color, double time, boolean distanceInEachPlane) {
		List<Vertex> centers = new ArrayList<>();
		List<Color> colors = new ArrayList<>();
		double halfCube = (cubeSize/2.0);
		for(int x = 0; x < shapeWidth; x++) {
			for(int y = 0; y < shapeHeight; y++) {
				for(int z = 0; z < shapeDepth; z++) {
					double[] inputs = get3DObjectCPPNInputs(x, y, z, shapeWidth, shapeHeight, shapeDepth, time, distanceInEachPlane);
					//determine whether inputs are turned on or off
					for(int i = 0; i < inputMultipliers.length; i++) {
						inputs[i] = inputs[i] * inputMultipliers[i];
					}	
					double[] output = cppn.process(inputs);
					if(output[THREE_DIMENSIONAL_VOXEL_INDEX] > VOXEL_EXPRESSION_THRESHOLD) { // If cube is even present
						double actualX = -(cubeSize*shapeWidth/2.0) + halfCube + x*cubeSize;
						double actualY = -(cubeSize*shapeHeight/2.0) + halfCube + y*cubeSize;
						double actualZ = -(cubeSize*shapeDepth/2.0) + halfCube + z*cubeSize; 
						if(output.length > ThreeDimensionalObjectBreederTask.CPPN_NUM_OUTPUTS) { //if cube locations are displaced by CPPN
							actualX += output[THREE_DIMENSIONAL_X_DISPLACEMENT_INDEX]*halfCube;
							actualY += output[THREE_DIMENSIONAL_Y_DISPLACEMENT_INDEX]*halfCube;
							actualZ += output[THREE_DIMENSIONAL_Z_DISPLACEMENT_INDEX]*halfCube;
						}
						centers.add(new Vertex(actualX, actualY, actualZ));
						if(color == null) { // Means the color is evolved
							float[] hsb = GraphicsUtil.rangeRestrictHSB(new double[]{output[THREE_DIMENSIONAL_HUE_INDEX],output[THREE_DIMENSIONAL_SATURATION_INDEX],output[THREE_DIMENSIONAL_BRIGHTNESS_INDEX]});
							int rgb = Color.HSBtoRGB(hsb[GraphicsUtil.HUE_INDEX], hsb[GraphicsUtil.SATURATION_INDEX], hsb[GraphicsUtil.BRIGHTNESS_INDEX]);
							Color evolvedColor = new Color(rgb, true);
							colors.add(evolvedColor);
						} else {
							colors.add(color);
						}
					}
				}
			}
		}
		return new Pair<>(centers,colors);
	}

	/**
	 * Returns CPPN inputs for 3D object construction
	 * 
	 * @param x Current voxel x-coordinate
	 * @param y Current voxel y-coordinate
	 * @param z current voxel z-coordinate
	 * @param width width of shape
	 * @param height height of shape
	 * @param depth depth of shape
	 * @return inputs to CPPN (x, y, z coordinates of a Vertex and bias)
	 */
	public static double[] get3DObjectCPPNInputs(int x, int y, int z, int width, int height, int depth, double time, boolean distanceInEachPlane) {
		Vertex v = new Vertex(x, y, z);
		Vertex newV = CartesianGeometricUtilities.centerAndScale(v, width, height, depth);
		Vertex zero = new Vertex(0, 0, 0);
		double distanceFromOrigin = newV.distance(zero) * GraphicsUtil.SQRT2;
		if(distanceInEachPlane) {
			Vertex newXY = CartesianGeometricUtilities.centerAndScale(new Vertex(x, y, 0), width, height, depth); // vertex centered and scaled for XY
			Vertex newYZ = CartesianGeometricUtilities.centerAndScale(new Vertex(0, y, z), width, height, depth); // vertex centered and scaled for YZ
			Vertex newXZ = CartesianGeometricUtilities.centerAndScale(new Vertex(x, 0, z), width, height, depth); // vertex centered and scaled for XZ
			
			double XY = newXY.distance(zero) * GraphicsUtil.SQRT2; // distance from XY and zero
			double YZ = newYZ.distance(zero) * GraphicsUtil.SQRT2; // distance from YZ and zero
			double XZ = newXZ.distance(zero) * GraphicsUtil.SQRT2; // distance from XZ and zero
			
			return new double[]{newV.x, newV.y, newV.z, distanceFromOrigin, XY, YZ, XZ, GraphicsUtil.BIAS};
		} else {
			if(time == -1) {
				return new double[]{newV.x, newV.y, newV.z, distanceFromOrigin, GraphicsUtil.BIAS};
			} else {
				return new double[]{newV.x, newV.y, newV.z, distanceFromOrigin, GraphicsUtil.BIAS, time};
			}
		}
	}


	/**
	 * Method that takes in a color, a vertex and a sidelength of a desired cube
	 * and returns a list of triangles that can be used to construct the cube.
	 * 
	 * @param center vertex representing center of line
	 * @param sideLength length of one side of the cube
	 * @param color desired color of the cube
	 * @return list of triangles used to construct cube
	 */
	public static List<Triangle> cubeConstructor(Vertex center, double sideLength, Color color) {
		double halfLength = (sideLength/2);

		List<Triangle> tris = new ArrayList<>();
		tris.add(new Triangle(center.add(new Vertex(halfLength, halfLength, halfLength)),
				center.add(new Vertex(halfLength, halfLength, -halfLength)),
				center.add(new Vertex(-halfLength, halfLength, -halfLength)),
				color));
		tris.add(new Triangle(center.add(new Vertex(-halfLength, halfLength, halfLength)),
				center.add(new Vertex(halfLength, halfLength, halfLength)),
				center.add(new Vertex(-halfLength, halfLength, -halfLength)),
				color));

		tris.add(new Triangle(center.add(new Vertex(-halfLength, halfLength, halfLength)),
				center.add(new Vertex(-halfLength, -halfLength, halfLength)),
				center.add(new Vertex(halfLength, halfLength, halfLength)),
				color));
		tris.add(new Triangle(center.add(new Vertex(halfLength, halfLength, halfLength)),
				center.add(new Vertex(-halfLength, -halfLength, halfLength)),
				center.add(new Vertex(halfLength, -halfLength, halfLength)),
				color));

		tris.add(new Triangle(center.add(new Vertex(halfLength, halfLength, halfLength)),
				center.add(new Vertex(halfLength, halfLength, -halfLength)),
				center.add(new Vertex(halfLength, -halfLength, halfLength)),
				color));
		tris.add(new Triangle(center.add(new Vertex(halfLength, -halfLength, -halfLength)),
				center.add(new Vertex(halfLength, -halfLength, halfLength)),
				center.add(new Vertex(halfLength, halfLength, -halfLength)),
				color));

		tris.add(new Triangle(center.add(new Vertex(-halfLength, -halfLength, halfLength)),
				center.add(new Vertex(-halfLength, -halfLength, -halfLength)),
				center.add(new Vertex(halfLength, -halfLength, halfLength)),
				color));
		tris.add(new Triangle(center.add(new Vertex(halfLength, -halfLength, -halfLength)),
				center.add(new Vertex(halfLength, -halfLength, halfLength)),
				center.add(new Vertex(-halfLength, -halfLength, -halfLength)),
				color));

		tris.add(new Triangle(center.add(new Vertex(-halfLength, -halfLength, -halfLength)),
				center.add(new Vertex(-halfLength, halfLength, -halfLength)),
				center.add(new Vertex(halfLength, -halfLength, -halfLength)),
				color));
		tris.add(new Triangle(center.add(new Vertex(halfLength, halfLength, -halfLength)),
				center.add(new Vertex(halfLength, -halfLength, -halfLength)),
				center.add(new Vertex(-halfLength, halfLength, -halfLength)),
				color));

		tris.add(new Triangle(center.add(new Vertex(-halfLength, -halfLength, -halfLength)),
				center.add(new Vertex(-halfLength, halfLength, -halfLength)),
				center.add(new Vertex(-halfLength, -halfLength, halfLength)),
				color));
		tris.add(new Triangle(center.add(new Vertex(-halfLength, halfLength, halfLength)),
				center.add(new Vertex(-halfLength, -halfLength, halfLength)),
				center.add(new Vertex(-halfLength, halfLength, -halfLength)),
				color));
		return tris;
	}

	/**
	 * Produces an array of images that are meant to animate a 3-dimensional object
	 * rotating. This is done by repeatedly calling getImage() at different input 
	 * headings and pitches.
	 * 
	 * @param tris array of triangles representing cube
	 * @param imageWidth width of image
	 * @param imageHeight height of image
	 * @param startTime beginning of animation
	 * @param endTime end of animation
	 * @return Array of BufferedImages that can be played as an animation of a 3D object
	 */
	public static BufferedImage[] imagesFromTriangles(List<Triangle> tris, int imageWidth, int imageHeight, int startTime, int endTime, double heading, double pitch, Color color, boolean vertical) {
		BufferedImage[] images = new BufferedImage[(endTime-startTime)];
		for(int i = startTime; i < endTime; i++) {
			// Causes slight twitch at end of each rotation sequence
			if(vertical) {
				double newPitch = pitch + (2*Math.PI*i)/images.length;
				images[i-startTime] = imageFromTriangles(tris, imageWidth, imageHeight, heading, newPitch, color);
			} else {
				double newHeading = heading + (2*Math.PI*i)/images.length;
				images[i-startTime] = imageFromTriangles(tris, imageWidth, imageHeight, newHeading, pitch, color);
			}
		}
		return images;
	}

	/**
	 * Produces an array of images that are meant to animate a 3-dimensional object
	 * rotating based on an input CPPN. 
	 * 
	 * @param cppn input network
	 * @param imageWidth width of image
	 * @param imageHeight height of image
	 * @param sideLength size of cube (voxel)
	 * @param shapeWidth width of shape being generated
	 * @param shapeHeight height of shape being generated
	 * @param shapeDepth depth of shape being generated
	 * @param color desired color of object
	 * @param startTime start time of animation
	 * @param endTime end time of animation
	 * @param heading input x-rotation of image
	 * @param pitch input y-rotation of image
	 * @param inputMultipliers array determining whether to turn inputs on or off
	 * @return
	 */
	public static BufferedImage[] rotationSequenceFromCPPN(Network cppn, int imageWidth, int imageHeight, int sideLength, int shapeWidth, int shapeHeight, int shapeDepth, Color color, int startTime, int endTime, double heading, double pitch, double[] inputMultipliers, boolean vertical, boolean distanceInEachPlane) {
		List<Triangle> tris = trianglesFromCPPN(cppn, imageWidth, imageHeight, sideLength, shapeWidth, shapeHeight, shapeDepth, color, inputMultipliers, distanceInEachPlane);
		BufferedImage[] resultImages = imagesFromTriangles(tris, imageWidth, imageHeight, startTime, endTime, heading, pitch, color, vertical);
		return resultImages;
	}
	
	/**
	 * Produces a list of triangles used to construct a 3D object.
	 * 
	 * @param cppn input network
	 * @param imageWidth width of image
	 * @param imageHeight height of image
	 * @param sideLength length of cube
	 * @param shapeWidth width of shape
	 * @param shapeHeight height of shape
	 * @param shapeDepth depth of shape
	 * @param color desired color of object
	 * @param inputMultipliers array determining whether to turn inputs on or off
	 * @return
	 */
	public static List<Triangle> trianglesFromCPPN(Network cppn, int imageWidth, int imageHeight, int sideLength, int shapeWidth, int shapeHeight, int shapeDepth, Color color, double[] inputMultipliers, boolean distanceInEachPlane) {
		return trianglesFromCPPN(cppn, imageWidth, imageHeight, sideLength, shapeWidth, shapeHeight, shapeDepth, color, inputMultipliers, -1, distanceInEachPlane);
	
	}
	
	public static List<Triangle> trianglesFromCPPN(Network cppn, int imageWidth, int imageHeight, int sideLength, int shapeWidth, int shapeHeight, int shapeDepth, Color color, double[] inputMultipliers, double time, boolean distanceInEachPlane) {
		Pair<List<Vertex>, List<Color>> result = getVertexesFromCPPN(cppn, imageWidth, imageHeight, sideLength, shapeWidth, shapeHeight, shapeDepth, inputMultipliers, color, time, distanceInEachPlane);
		List<Vertex> cubeVertexes = result.t1;
		List<Color> colors = result.t2;
		List<Triangle> tris = getShape(cubeVertexes, imageWidth, imageHeight, sideLength, inputMultipliers, colors);
	
		return tris;

	}

	public static BufferedImage currentImageFromCPPN(Network cppn, int imageWidth, int imageHeight, int sideLength, int shapeWidth, int shapeHeight, int shapeDepth, Color color, double heading, double pitch, double[] inputMultipliers, boolean distanceInEachPlane) {
		return currentImageFromCPPN(cppn, imageWidth, imageHeight, sideLength, shapeWidth, shapeHeight, shapeDepth, color, heading, pitch, inputMultipliers, -1, distanceInEachPlane); // -1 means time is not used
	}
	
	/**
	 * Returns the current image to be displayed on the button based on the rotation 
	 * and the CPPN.
	 * 
	 * @param cppn Network manipulating image
	 * @param imageWidth width of image
	 * @param imageHeight height of image
	 * @param sideLength size of cube
	 * @param shapeWidth width of shape (measured in cubes)
	 * @param shapeHeight height of shape (measured in cubes)
	 * @param shapeDepth depth of shape (measured in cubes)
	 * @param bgColor desired color of image
	 * @param heading horizontal rotation of object
	 * @param pitch vertical rotation of object
	 * @param inputMultipliers indicates whether input checkboxes are turned on or off
	 * @return
	 */
	public static BufferedImage currentImageFromCPPN(Network cppn, int imageWidth, int imageHeight, int sideLength, int shapeWidth, int shapeHeight, int shapeDepth, Color bgColor, double heading, double pitch, double[] inputMultipliers, double time, boolean distanceInEachPlane) {
		List<Triangle> tris = trianglesFromCPPN(cppn, imageWidth, imageHeight, sideLength, shapeWidth, shapeHeight, shapeDepth, null, inputMultipliers, time, distanceInEachPlane); //TODO: instead of null, allow for set color options like in the objectbreeder?
		BufferedImage currentImage = imageFromTriangles(tris, imageWidth, imageHeight, heading, pitch, bgColor);
		return currentImage;
	}
}

class Matrix3 {
	double[] values;
	Matrix3(double[] values) {
		this.values = values;
	}
	Matrix3 multiply(Matrix3 other) {
		double[] result = new double[9];
		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 3; col++) {
				for (int i = 0; i < 3; i++) {
					result[row * 3 + col] +=
							this.values[row * 3 + i] * other.values[i * 3 + col];
				}
			}
		}
		return new Matrix3(result);
	}
	Vertex transform(Vertex in) {
		return new Vertex(
				in.x * values[0] + in.y * values[3] + in.z * values[6],
				in.x * values[1] + in.y * values[4] + in.z * values[7],
				in.x * values[2] + in.y * values[5] + in.z * values[8]
				);
	}
}
