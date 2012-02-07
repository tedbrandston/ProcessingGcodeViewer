package gcodeviewer.utils;

public class Bounds {

	// these define a BoundingBox
	Point5f lower, upper;

	// this is a bounding sphere
	float radius;

	public Bounds() {
		lower = new Point5f(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
		upper = new Point5f(Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE);
		radius = 1;
	}

	public void add(Point5f p5f) {
		if (p5f.x > upper.x)
			upper.x = p5f.x;
		if (p5f.y > upper.y)
			upper.y = p5f.y;
		if (p5f.z > upper.z)
			upper.z = p5f.z;

		if (p5f.x < lower.x)
			lower.x = p5f.x;
		if (p5f.y < lower.y)
			lower.y = p5f.y;
		if (p5f.z < lower.z)
			lower.z = p5f.z;

		double r = Math.pow(p5f.x, 2) + (float) Math.pow(p5f.y, 2) + (float) Math.pow(p5f.z, 2);
		r = Math.sqrt(r);
		if (r > radius) {
			radius = (float) r;
		}
	}
}
