package gcodeviewer.utils;

import replicatorg.AxisId;

public class MutablePoint5d extends Point5d{

	public MutablePoint5d() {
		super(0d, 0d, 0d, 0d, 0d);
	}

	public MutablePoint5d(double x, double y, double z, double a, double b) {
		super(x, y, z, a, b);
	}

	public MutablePoint5d(double x, double y, double z) {
		super(x, y, z, 0, 0);
	}

	public MutablePoint5d(Point5d p) {
		System.arraycopy(p.values, 0, values, 0, DIMENSIONS);
	}

	// setter for by-AxisId access
	public void setAxis(AxisId axis, double v) {
		values[axis.getIndex()] = v;
	}

	// setter for by-index access
	public void set(int idx, double v) {
		values[idx] = v;
	}

	// setters for by-name access
	public void setX(double x) {
		values[0] = x;
	}

	public void setY(double y) {
		values[1] = y;
	}

	public void setZ(double z) {
		values[2] = z;
	}

	public void setA(double a) {
		values[3] = a;
	}

	public void setB(double b) {
		values[4] = b;
	}

	public void add(MutablePoint5d p1) {
		for (int idx = 0; idx < DIMENSIONS; idx++) {
			values[idx] += p1.values[idx];
		}
	}

	public void sub(MutablePoint5d p1) {
		for (int idx = 0; idx < DIMENSIONS; idx++) {
			values[idx] -= p1.values[idx];
		}
	}

	public void sub(MutablePoint5d p1, MutablePoint5d p2) {
		for (int idx = 0; idx < DIMENSIONS; idx++) {
			values[idx] = p1.values[idx] - p2.values[idx];
		}
	}

	/**
	 * Set the value of each element of this point to be the the value of the respective element of
	 * p1 divided by p2: this.value[axis] = p1.value[axis] / p2.value[axis].
	 * 
	 * @param p1
	 *            numerator
	 * @param p2
	 *            denominator
	 */
	public void div(MutablePoint5d p1, MutablePoint5d p2) {
		for (int idx = 0; idx < DIMENSIONS; idx++) {
			values[idx] = p1.values[idx] / p2.values[idx];
		}
	}

	/**
	 * Set the value of each element of this point to be the the value of the respective element of
	 * p1 multiplied by p2: this.value[axis] = p1.value[axis] * p2.value[axis].
	 * 
	 * @param p1
	 *            multiplicand A
	 * @param p2
	 *            multiplicand B
	 */
	public void mul(MutablePoint5d p1, MutablePoint5d p2) {
		for (int idx = 0; idx < DIMENSIONS; idx++) {
			values[idx] = p1.values[idx] * p2.values[idx];
		}
	}

	/**
	 * Round each element of the point to the nearest integer (using Math.round).
	 */
	public void round() {
		for (int idx = 0; idx < DIMENSIONS; idx++) {
			values[idx] = Math.round(values[idx]);
		}
	}

	/**
	 * Round each element of the point to the nearest integer (using Math.round), storing the excess
	 * in the provided point object.
	 */
	public void round(MutablePoint5d excess) {
		for (int idx = 0; idx < DIMENSIONS; idx++) {
			double rounded = Math.round(values[idx]);
			excess.values[idx] = values[idx] - rounded;
			values[idx] = rounded;
		}
	}

	public void absolute() {
		for (int idx = 0; idx < DIMENSIONS; idx++) {
			values[idx] = Math.abs(values[idx]);
		}
	}

	public double distance(MutablePoint5d p) {
		double acc = 0d;
		for (int idx = 0; idx < DIMENSIONS; idx++) {
			double delta = values[idx] - p.values[idx];
			acc += (delta * delta);
		}
		return Math.sqrt(acc);
	}

}
