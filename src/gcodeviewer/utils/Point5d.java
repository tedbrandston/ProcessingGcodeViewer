package gcodeviewer.utils;

import replicatorg.AxisId;

public class Point5d {
	protected final static int DIMENSIONS = 5;
	protected final double values[] = new double[DIMENSIONS];

	public Point5d() {
		this(0d, 0d, 0d, 0d, 0d);
	}

	public Point5d(double x, double y, double z, double a, double b) {
		values[0] = x;
		values[1] = y;
		values[2] = z;
		values[3] = a;
		values[4] = b;
	}

	public Point5d(double x, double y, double z) {
		values[0] = x;
		values[1] = y;
		values[2] = z;
		values[3] = 0;
		values[4] = 0;
	}

	public Point5d(Point5d p) {
		System.arraycopy(p.values, 0, values, 0, DIMENSIONS);
	}

	// Getter/setter for by-AxisId access
	public double axis(AxisId axis) {
		return values[axis.getIndex()];
	}

	// Getter/setter for by-index access
	public double get(int idx) {
		return values[idx];
	}

	// Getters/setters for by-name access
	public double x() {
		return values[0];
	}

	public double y() {
		return values[1];
	}

	public double z() {
		return values[2];
	}

	public double a() {
		return values[3];
	}

	public double b() {
		return values[4];
	}

	public float xf() {
		return (float) values[0];
	}

	public float yf() {
		return (float) values[1];
	}

	public float zf() {
		return (float) values[2];
	}

	public float af() {
		return (float) values[3];
	}

	public float bf() {
		return (float) values[4];
	}

	public double distance(Point5d p) {
		double acc = 0d;
		for (int idx = 0; idx < DIMENSIONS; idx++) {
			double delta = values[idx] - p.values[idx];
			acc += (delta * delta);
		}
		return Math.sqrt(acc);
	}

	public double length() {
		double acc = 0d;
		for (int idx = 0; idx < DIMENSIONS; idx++) {
			double delta = values[idx];
			acc += (delta * delta);
		}
		return Math.sqrt(acc);
	}

	public double magnitude() {
		double acc = 0d;
		for (int idx = 0; idx < DIMENSIONS; idx++) {
			double delta = values[idx];
			acc += (delta * delta);
		}
		return Math.sqrt(acc);
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append('(');
		sb.append(values[0]);
		for (int idx = 1; idx < DIMENSIONS; idx++) {
			sb.append(',');
			sb.append(values[idx]);
		}
		sb.append(')');
		return sb.toString();
	}
}
