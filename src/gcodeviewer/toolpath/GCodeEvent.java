package gcodeviewer.toolpath;

import gcodeviewer.utils.MutablePoint5d;
import gcodeviewer.utils.Point5d;
import replicatorg.ToolheadAlias;

//tagging interface?
public interface GCodeEvent {
	
	public class UnrecognisedCode implements GCodeEvent {
		public final String line;
		
		public UnrecognisedCode(String line) {
			this.line = line;
		}
	}
	
	public class StartExtrusion implements GCodeEvent {
		public final int direction;
		
		public StartExtrusion(int direction) {
			this.direction = direction;
		}
	}
	
	public final class SetToolheadTemp implements GCodeEvent {
		public final double temperature;
		
		public SetToolheadTemp(double temperature) {
			this.temperature = temperature;
		}
	}

	public final class SetToolhead implements GCodeEvent {
		public final ToolheadAlias tool;
		
		public SetToolhead(ToolheadAlias tool) {
			this.tool = tool;
		}
	}
	
	public final class SetPosition implements GCodeEvent {

		public final Point5d point;

		public SetPosition(MutablePoint5d point) {
			this.point = new Point5d(point);
		}
	}
	
	public final class SetPlatformTemp implements GCodeEvent {
		public final double temperature;
		
		public SetPlatformTemp(double temperature) {
			this.temperature = temperature;
		}
	}
	
	public class SetMotorSpeedRPM implements GCodeEvent {
		public final double speed;
		
		public SetMotorSpeedRPM(double speed) {
			this.speed = speed;
		}
	}
	
	public class SetMotorSpeedPWM implements GCodeEvent {
		public final int speed;
		
		public SetMotorSpeedPWM(int speed) {
			this.speed = speed;
		}
	}
	
	public class SetFeedrate implements GCodeEvent {
		public final double feedrate;

		public SetFeedrate(double feedrate) {
			this.feedrate = feedrate;
		}
	}
	
	public final class MoveTo implements GCodeEvent {
		public final Point5d point;

		public MoveTo(MutablePoint5d point) {
			this.point = new Point5d(point);
		}
	}
	
	// doesn't need to contain any information, it's existence is sufficient
	public class NewLayer implements GCodeEvent {

	}
	
	public class EndExtrusion implements GCodeEvent {

	}

}
