package gcodeviewer.toolpath.events;

import gcodeviewer.toolpath.GCodeEvent;

public class StartExtrusion implements GCodeEvent {
	public final int direction;
	
	public StartExtrusion(int direction) {
		this.direction = direction;
	}
}
