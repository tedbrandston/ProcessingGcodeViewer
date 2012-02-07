package gcodeviewer.utils;

public class LineSegment {

	private final int layer;
	private int toolhead = 0; // DEFAULT TOOLHEAD ASSUMED TO BE 0!
	private final Point5d first, second;
	private final boolean isExtruding;
	private final float extrusionSpeed;

	public LineSegment(Point5d lastPoint, Point5d curPoint, int curLayer, float speed,
			int curToolhead, boolean currentExtruding) {

		first = lastPoint;
		second = curPoint;
		layer = curLayer;
		extrusionSpeed = speed;
		toolhead = curToolhead;
		isExtruding = currentExtruding;
	}

	public Point5d[] getPointArray() {
		Point5d[] pointarr = { first, second };
		return pointarr;
	}

	public float[] getPoints() {
		float[] points = { first.xf(), first.yf(), first.zf(), second.xf(), second.yf(),
				second.zf() };
		return points;
	}

	public float[] getPoints(float scale) {
		float[] points = { first.xf() * scale, first.yf() * scale, first.zf() * scale,
				second.xf() * scale, second.yf() * scale, second.zf() * scale };
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
