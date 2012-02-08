package gcodeviewer.toolpath.events;

import gcodeviewer.toolpath.GCodeEvent;

public class SetMotorSpeedPWM implements GCodeEvent {
	public final int speed;
	
	public SetMotorSpeedPWM(int speed) {
		this.speed = speed;
	}
}
