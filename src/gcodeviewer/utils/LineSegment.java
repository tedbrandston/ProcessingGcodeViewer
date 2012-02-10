package gcodeviewer.utils;

import processing.core.PGraphics;

public final class LineSegment {

	private final Point5d first, second;
	private final int color;
	
	private static boolean useTubes = false;
	
	public LineSegment(Point5d first, Point5d second, int color) {
		this.first = first;
		this.second = second;
		this.color = color;
	}
	
	public void draw(PGraphics g) {
		g.stroke(color);
		if(useTubes)
			tube(g);
		else
			g.line(first.xf(), first.yf(), first.zf(), second.xf(), second.yf(), second.zf());
	}
	
	private void tube(PGraphics g) {
		
	}
	
}
