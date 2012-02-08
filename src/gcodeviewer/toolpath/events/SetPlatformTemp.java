package gcodeviewer.toolpath.events;

import gcodeviewer.toolpath.GCodeEvent;

public final class SetPlatformTemp implements GCodeEvent {
	public final double temperature;
	
	public SetPlatformTemp(double temperature) {
		this.temperature = temperature;
	}
}
