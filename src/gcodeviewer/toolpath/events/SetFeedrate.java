package gcodeviewer.toolpath.events;

import gcodeviewer.toolpath.GCodeEvent;

public class SetFeedrate implements GCodeEvent {
	public final double feedrate;

	public SetFeedrate(double feedrate) {
		this.feedrate = feedrate;
	}
}
