package gcodeviewer.toolpath.events;

import gcodeviewer.toolpath.GCodeEvent;
import gcodeviewer.utils.Point5d;
import gcodeviewer.utils.MutablePoint5d;

public final class SetPosition implements GCodeEvent {

	public final Point5d point;

	public SetPosition(MutablePoint5d point) {
		this.point = new Point5d(point);
	}
}
