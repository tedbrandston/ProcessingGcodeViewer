package gcodeviewer.utils;

public class LineSegment {

	private final int layer;
	private int toolhead = 0; // DEFAULT TOOLHEAD ASSUMED TO BE 0!
	private final Point5f first, second;
	private final boolean isExtruding;
	private final float extrusionSpeed;

	public LineSegment(Point5f lastPoint, Point5f curPoint, int curLayer, float speed,
			int curToolhead, boolean currentExtruding) {

		first = lastPoint;
		second = curPoint;
		layer = curLayer;
		extrusionSpeed = speed;
		toolhead = curToolhead;
		isExtruding = currentExtruding;
	}

	public Point5f[] getPointArray() {
		Point5f[] pointarr = { first, second };
		return pointarr;
	}

	public float[] getPoints() {
		float[] points = { first.x, first.y, first.z, second.x, second.y, second.z };
		return points;
	}

	public float[] getPoints(float scale) {
		float[] points = { first.x * scale, first.y * scale, first.z * scale, second.x * scale,
				second.y * scale, second.z * scale };
		return points;
	}

	public int getToolhead() {
		return toolhead;
	}

	public float getSpeed() {
		return extrusionSpeed;
	}

	public int getLayer() {
		return layer;
	}

	public boolean getExtruding() {
		return isExtruding;
	}

}
