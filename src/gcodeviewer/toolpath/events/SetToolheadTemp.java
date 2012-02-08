package gcodeviewer.toolpath.events;

import gcodeviewer.toolpath.GCodeEvent;

public final class SetToolheadTemp implements GCodeEvent {
	public final double temperature;
	
	public SetToolheadTemp(double temperature) {
		this.temperature = temperature;
	}
}
