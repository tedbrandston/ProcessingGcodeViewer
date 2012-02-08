package gcodeviewer.toolpath.events;

import gcodeviewer.toolpath.GCodeEvent;
import replicatorg.ToolheadAlias;

public final class SetToolhead implements GCodeEvent {
	public final ToolheadAlias tool;
	
	public SetToolhead(ToolheadAlias tool) {
		this.tool = tool;
	}
}
