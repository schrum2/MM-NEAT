package edu.utexas.cs.nn.tasks.interactive.objectbreeder;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.vecmath.Vector3d;

/**
 * Imported class that constructs a three-dimensional tetrahedral object.
 * 
 * @author Rogach
 *
 */
public class Construct3DObject {
	private static JSlider headingSlider;
	private static JSlider pitchSlider;
	private static Graphics2D g2;
	private static JPanel renderPanel;

	public Construct3DObject() {
		JFrame frame = new JFrame();
		Container pane = frame.getContentPane();
		pane.setLayout(new BorderLayout());

		// slider to control horizontal rotation
		headingSlider = new JSlider(-180, 180, 0);
		pane.add(headingSlider, BorderLayout.SOUTH);

		// slider to control vertical rotation
		pitchSlider = new JSlider(SwingConstants.VERTICAL, -90, 90, 0);
		pane.add(pitchSlider, BorderLayout.EAST);

		@SuppressWarnings("serial")
		JPanel renderPanel = new JPanel() {
			public void paintComponent(Graphics g) {
				g2 = (Graphics2D) g;
				g2.setColor(Color.BLACK);
				g2.fillRect(0, 0, getWidth(), getHeight());			
			}
		};

		pane.add(renderPanel, BorderLayout.CENTER);

		headingSlider.addChangeListener(e -> renderPanel.repaint());
		pitchSlider.addChangeListener(e -> renderPanel.repaint());

		frame.setSize(400, 400);
		frame.setVisible(true);

	}

	public static void main(String[] args) {
		// panel to display render results
		Vertex center = new Vertex(0,0,0);
		List<Triangle> tris = cubeConstructor(center, 100.0, Color.RED);
		drawObject(g2, tris);


	}

	public static void drawObject(Graphics2D g2, List<Triangle> tris) {
		double heading = Math.toRadians(headingSlider.getValue());
		Matrix3 headingTransform = new Matrix3(new double[] {
				Math.cos(heading), 0, -Math.sin(heading),
				0, 1, 0,
				Math.sin(heading), 0, Math.cos(heading)
		});
		double pitch = Math.toRadians(pitchSlider.getValue());
		Matrix3 pitchTransform = new Matrix3(new double[] {
				1, 0, 0,
				0, Math.cos(pitch), Math.sin(pitch),
				0, -Math.sin(pitch), Math.cos(pitch)
		});
		Matrix3 transform = headingTransform.multiply(pitchTransform);

		BufferedImage img = new BufferedImage(renderPanel.getWidth(), renderPanel.getHeight(), BufferedImage.TYPE_INT_ARGB);

		double[] zBuffer = new double[img.getWidth() * img.getHeight()];
		// initialize array with extremely far away depths
		for (int q = 0; q < zBuffer.length; q++) {
			zBuffer[q] = Double.NEGATIVE_INFINITY;
		}

		for (Triangle t : tris) {
			Vertex v1 = transform.transform(t.v1);
			v1.x += renderPanel.getWidth() / 2;
			v1.y += renderPanel.getHeight() / 2;
			Vertex v2 = transform.transform(t.v2);
			v2.x += renderPanel.getWidth() / 2;
			v2.y += renderPanel.getHeight() / 2;
			Vertex v3 = transform.transform(t.v3);
			v3.x += renderPanel.getWidth() / 2;
			v3.y += renderPanel.getHeight() / 2;

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

		g2.drawImage(img, 0, 0, null);
	}

	public static Color getShade(Color color, double shade) {
		double redLinear = Math.pow(color.getRed(), 2.4) * shade;
		double greenLinear = Math.pow(color.getGreen(), 2.4) * shade;
		double blueLinear = Math.pow(color.getBlue(), 2.4) * shade;

		int red = (int) Math.pow(redLinear, 1/2.4);
		int green = (int) Math.pow(greenLinear, 1/2.4);
		int blue = (int) Math.pow(blueLinear, 1/2.4);

		return new Color(red, green, blue);
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
}

class Vertex extends Vector3d {
	public Vertex(double x, double y, double z) {
		super(x,y,z);
	}

	/**
	 * Copy constructor
	 * @param other
	 */
	public Vertex(Vertex other) {
		this(other.x, other.y, other.z);
	}

	public Vertex add(Vertex v) {
		Vertex newV = new Vertex(this);
		newV.add((Vector3d) v);
		return newV;
	}
}


class Triangle {
	Vertex v1;
	Vertex v2;
	Vertex v3;
	Color color;
	Triangle(Vertex v1, Vertex v2, Vertex v3, Color color) {
		this.v1 = v1;
		this.v2 = v2;
		this.v3 = v3;
		this.color = color;
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
