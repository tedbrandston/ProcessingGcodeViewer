package gcodeviewer.utils;

import java.util.ArrayList;

public class GCodeSource {
	ArrayList<LineSegment> source = new ArrayList<LineSegment>();

	int numLayers;

	public void add(LineSegment l) {
		source.add(l);
		if (l.getLayer() > numLayers)
			numLayers = l.getLayer();
	}

	public ArrayList<LineSegment> getSourceList() {
		return source;
	}
}
