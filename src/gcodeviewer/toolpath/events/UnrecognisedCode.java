package gcodeviewer.toolpath.events;

import gcodeviewer.toolpath.GCodeEvent;

public class UnrecognisedCode implements GCodeEvent {
	public final String line;
	
	public UnrecognisedCode(String line) {
		this.line = line;
	}
}
