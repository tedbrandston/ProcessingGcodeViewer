package gcodeviewer.parsers;

import gcodeviewer.toolpath.GCodeEventToolpath;

import java.util.ArrayList;

public abstract class GCodeParser {

	// public GCodeSource source;
	protected final GCodeEventToolpath path = new GCodeEventToolpath();

	public abstract void parse(ArrayList<String> gcode);

	public GCodeEventToolpath getPath() {
		return path;
	}
}
