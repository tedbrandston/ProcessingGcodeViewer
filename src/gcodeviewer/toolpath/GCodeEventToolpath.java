package gcodeviewer.toolpath;

import gcodeviewer.toolpath.events.MoveTo;
import gcodeviewer.toolpath.events.NewLayer;
import gcodeviewer.utils.Bounds;

import java.util.ArrayList;
import java.util.List;

/**
 * Instead of turning GCode straight into line segments, we have an intermediate stage here.
 * 
 * This means that not all parsers/line segments need to know about all kinds of things GCode can
 * do. We just collect whatever events the Parser spits out, and let the ToolpathColorer figure out
 * what to show.
 */
public final class GCodeEventToolpath {

	private final Bounds bounds = new Bounds();
	private int numLayers = 0;

	private final ArrayList<GCodeEvent> eventList = new ArrayList<GCodeEvent>();

	public void addEvent(GCodeEvent evt) {
		if (evt instanceof MoveTo)
			bounds.add(((MoveTo) evt).point);
		else if (evt instanceof NewLayer)
			numLayers++;

		eventList.add(evt);
	}

	public void finish() {
		eventList.trimToSize();
	}
	
	public int getNumLayers() {
		return numLayers;
	}

	public Bounds getBounds() {
		return bounds;
	}
	
	public List<GCodeEvent> events() {
		return eventList;
	}
}
