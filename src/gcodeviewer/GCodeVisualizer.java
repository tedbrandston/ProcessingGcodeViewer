package gcodeviewer;

import gcodeviewer.toolpath.GCodeEventToolpath;
import processing.core.PGraphics;

public abstract class GCodeVisualizer {

	protected GCodeEventToolpath toolpath;
	
	/**
	 * draw onto the PGraphics
	 */
	public abstract void draw(PGraphics g);
	
	public void setToolpath(GCodeEventToolpath toolpath) {
		this.toolpath = toolpath;
	}
	

	protected static int color(int x, int y, int z) {
		return 0xff000000 | (x << 16) | (y << 8) | z;
	}

	protected static int color(int x, int y, int z, int a) {
		return (a << 24) | (x << 16) | (y << 8) | z;
	}

	protected static int color(int c, int a) {
		c = c & 0x00ffffff;
		return (a << 24) | c;
	}
}
