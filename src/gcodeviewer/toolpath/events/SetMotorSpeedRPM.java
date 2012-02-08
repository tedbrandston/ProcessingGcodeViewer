package gcodeviewer.toolpath.events;

import gcodeviewer.toolpath.GCodeEvent;

public class SetMotorSpeedRPM implements GCodeEvent {
	public final double speed;
	
	public SetMotorSpeedRPM(double speed) {
		this.speed = speed;
	}
}
