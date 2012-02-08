package gcodeviewer.parsers;

import gcodeviewer.toolpath.events.EndExtrusion;
import gcodeviewer.toolpath.events.MoveTo;
import gcodeviewer.toolpath.events.NewLayer;
import gcodeviewer.toolpath.events.SetFeedrate;
import gcodeviewer.toolpath.events.SetToolhead;
import gcodeviewer.toolpath.events.StartExtrusion;
import gcodeviewer.utils.MutablePoint5d;

import java.util.ArrayList;

import replicatorg.ToolModel;
import replicatorg.ToolheadAlias;

public class DefaultParser extends GCodeParser {

	@Override
	public void parse(ArrayList<String> gcode) {
		MutablePoint5d curPoint = null;
		float parsedX, parsedY, parsedZ, parsedF;
		
		float[] lastCoord = { 0.0f, 0.0f, 0.0f };
		boolean currentExtruding = false;
		for (String s : gcode) {
			if (s.matches(".*M101.*")) {
				path.addEvent(new StartExtrusion(ToolModel.MOTOR_CLOCKWISE));
			}
			if (s.matches(".*M103.*")) {
				path.addEvent(new EndExtrusion());
			}
			if (s.matches("\\(\\</layer\\>\\)")) {
				path.addEvent(new NewLayer());
			}
			if (s.matches(".*T0.*")) {
				path.addEvent(new SetToolhead(ToolheadAlias.RIGHT));
			}
			if (s.matches(".*T1.*")) {
				path.addEvent(new SetToolhead(ToolheadAlias.LEFT));
			}
			if (s.matches(".*G1.*")) {
				String[] sarr = s.split(" ");
				parsedX = parseCoord(sarr, 'X');
				parsedY = parseCoord(sarr, 'Y');
				parsedZ = parseCoord(sarr, 'Z');
				parsedF = parseCoord(sarr, 'F');

				// System.out.println(Arrays.toString(sarr));
				if (!Float.isNaN(parsedX)) {
					lastCoord[0] = parsedX;
				}
				if (!Float.isNaN(parsedY)) {
					lastCoord[1] = parsedY;
				}
				if (!Float.isNaN(parsedZ)) {
					/*
					 * if (!(Math.abs(parsedZ - lastCoord[2]) <= tolerance)) { curLayer++; }
					 */
					lastCoord[2] = parsedZ;

				}
				if (!Float.isNaN(parsedF)) {
					path.addEvent(new SetFeedrate(parsedF));
				}
				if (!(Float.isNaN(lastCoord[0]) || Float.isNaN(lastCoord[1]) || Float
						.isNaN(lastCoord[2]))) {

					curPoint = new MutablePoint5d(lastCoord[0], lastCoord[1], lastCoord[2]);
					
					path.addEvent(new MoveTo(curPoint));
				}
			}

		}
		path.finish();

	}

	private float parseCoord(String[] sarr, char c) {
		for (String t : sarr) {
			if (t.matches("\\s*[" + c + "]\\s*-*[\\d|\\.]+")) {
				// System.out.println("te : " + t);
				return Float.parseFloat(t.substring(1, t.length()));
			}
		}
		return Float.NaN;
	}
}
