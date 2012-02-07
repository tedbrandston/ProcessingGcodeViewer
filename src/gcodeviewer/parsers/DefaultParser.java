package gcodeviewer.parsers;

import gcodeviewer.utils.GCodeSource;
import gcodeviewer.utils.LineSegment;
import gcodeviewer.utils.Point5d;

import java.util.ArrayList;

public class DefaultParser extends GCodeParser {

	private static boolean debugVals = false;

	@Override
	public void parse(ArrayList<String> gcode) {
		float speed = 2; // DEFAULTS to 2
		Point5d lastPoint = null;
		Point5d curPoint = null;
		int curLayer = 0;
		int curToolhead = 0;
		float parsedX, parsedY, parsedZ, parsedF;

		source = new GCodeSource();
		float[] lastCoord = { 0.0f, 0.0f, 0.0f };
		boolean currentExtruding = false;
		for (String s : gcode) {
			if (s.matches(".*M101.*")) {
				currentExtruding = true;
			}
			if (s.matches(".*M103.*")) {
				currentExtruding = false;
			}
			if (s.matches("\\(\\</layer\\>\\)")) {
				curLayer++;
			}
			if (s.matches(".*T0.*")) {
				curToolhead = 0;
			}
			if (s.matches(".*T1.*")) {
				curToolhead = 1;
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
					speed = parsedF;
				}
				if (!(Float.isNaN(lastCoord[0]) || Float.isNaN(lastCoord[1]) || Float
						.isNaN(lastCoord[2]))) {

					if (debugVals) {
						System.out.println(lastCoord[0] + "," + lastCoord[1] + "," + lastCoord[2]
								+ ", speed =" + speed + ", layer=" + curLayer);
					}
					curPoint = new Point5d(lastCoord[0], lastCoord[1], lastCoord[2]);
					if (currentExtruding && curLayer > 5) {
						bounds.add(curPoint);
					}
					if (lastPoint != null) {
						source.add(new LineSegment(lastPoint, curPoint, curLayer, speed,
								curToolhead, currentExtruding));
					}
					lastPoint = curPoint;
				}
			}

		}

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
