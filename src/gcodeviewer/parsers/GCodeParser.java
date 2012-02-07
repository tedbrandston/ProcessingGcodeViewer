package gcodeviewer.parsers;

import gcodeviewer.utils.Bounds;
import gcodeviewer.utils.GCodeSource;

import java.util.ArrayList;

public abstract class GCodeParser {

	public Bounds bounds = new Bounds();

	public GCodeSource source;

	public abstract void parse(ArrayList<String> gcode);

}
