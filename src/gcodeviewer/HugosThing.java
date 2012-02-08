package gcodeviewer;

import gcodeviewer.toolpath.GCodeEvent;
import gcodeviewer.toolpath.GCodeEventToolpath;
import gcodeviewer.toolpath.events.EndExtrusion;
import gcodeviewer.toolpath.events.MoveTo;
import gcodeviewer.toolpath.events.SetFeedrate;
import gcodeviewer.toolpath.events.SetMotorSpeedRPM;
import gcodeviewer.toolpath.events.StartExtrusion;
import gcodeviewer.utils.Point5d;
import replicatorg.ToolModel;

public class HugosThing {

	public void writeToScad(GCodeEventToolpath path) {
		double feedrate = 0;
		double extrusionSpeed = 0;
		boolean motorEnabled = false;
		Point5d lastPoint = null;
		for(GCodeEvent evt : path.events()) {

			if(evt instanceof SetMotorSpeedRPM) {
				extrusionSpeed = ((SetMotorSpeedRPM)evt).speed;
			}
			if(evt instanceof SetFeedrate) {
				feedrate = ((SetFeedrate)evt).feedrate;
			}

			if(evt instanceof StartExtrusion) {
				if(((StartExtrusion)evt).direction == ToolModel.MOTOR_COUNTER_CLOCKWISE)
					extrusionSpeed = Math.abs(extrusionSpeed)*-1;
				if(((StartExtrusion)evt).direction == ToolModel.MOTOR_CLOCKWISE)
					extrusionSpeed = Math.abs(extrusionSpeed);
				motorEnabled = true;
			}

			if(evt instanceof EndExtrusion) {
				motorEnabled = false;
			}
			
			if(evt instanceof MoveTo) {
				Point5d newPoint = ((MoveTo)evt).point;
				if(lastPoint != null)
					System.out.println("magick("+lastPoint.x()+", "+lastPoint.y()+", "+lastPoint.z()+", "+newPoint.x()+", "+newPoint.y()+", "+newPoint.z()+", "+feedrate+", "+(motorEnabled ? extrusionSpeed : 0)+");");
				lastPoint = newPoint;
			}
		}
	}
}
