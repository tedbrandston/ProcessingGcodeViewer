package gcodeviewer.parsers;

import gcodeviewer.utils.GCodeSource;
import gcodeviewer.utils.LineSegment;
import gcodeviewer.utils.Point5d;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;

import replicatorg.AxisId;
import replicatorg.GCodeCommand;
import replicatorg.GCodeEnumeration;
import replicatorg.ToolModel;

public class MightyParser extends GCodeParser {

	// our current position
	Point5d prev, current;

	// not sure if I need these?
	Point5d home;

	// for homing
	Point5d endstopsMax = new Point5d(227, 148, Double.MAX_VALUE, Double.MAX_VALUE,
			Double.MAX_VALUE);
	Point5d endstopsMin = new Point5d(Double.MIN_VALUE, Double.MIN_VALUE, 150, Double.MIN_VALUE,
			Double.MIN_VALUE);

	// false = incremental; true = absolute
	boolean absoluteMode = false;

	// Feedrate in mm/minute.
	double feedrate = 0.0;

	private final ToolModel LEFT = new ToolModel();
	private final ToolModel RIGHT = new ToolModel();
	{
		LEFT.setIndex(1);
		RIGHT.setIndex(0);
	}
	// current selected tool
	private ToolModel tool = RIGHT;

	// unit variables.
	public static final int UNITS_MM = 0;
	public static final int UNITS_INCHES = 1;
	protected int units = UNITS_MM;

	public static final int CLOCKWISE = 0;
	public static final int COUNTERCLOCKWISE = 1;

	EnumMap<AxisId, ToolModel> extruderHijackedMap = new EnumMap<AxisId, ToolModel>(AxisId.class);
	{
		extruderHijackedMap.put(AxisId.A, RIGHT);
		extruderHijackedMap.put(AxisId.B, LEFT);
	}

	Point5d stepsPerMM = new Point5d(95, 95, 400, 95, 95);

	Point5d[] offsetSystems = new Point5d[7];
	{
		for (int i = 0; i < offsetSystems.length; i++)
			offsetSystems[i] = new Point5d();
	}
	Point5d currentOffset = offsetSystems[0];

	@Override
	public void parse(ArrayList<String> gcode) {
		source = new GCodeSource();

		current = new Point5d();
		prev = new Point5d();

		boolean abort = false;
		for (String line : gcode) {

			// First, parse the GCode string into an object we can query.
			GCodeCommand cmd = new GCodeCommand(line);

			if (cmd.hasCode('G')) {
				abort = buildGCodes(cmd);
			} else if (cmd.hasCode('M')) {
				abort = buildMCodes(cmd);
			}
			if (abort)
				return;
		}
	}

	private boolean buildMCodes(GCodeCommand gcode) {
		// If this machine handles multiple active toolheads, we always honor a T code
		// as being a annotation to send the given command to the given toolheads. Be
		// aware that appending a T code to an M code will not necessarily generate a
		// change tool request! Use M6 for that.
		// M6 was historically used to wait for toolheads to get up to temperature, so
		// you may wish to avoid using M6.
		if (gcode.hasCode('T')) {
			if (((int) gcode.getCodeValue('T')) == LEFT.getIndex())
				tool = LEFT;
			if (((int) gcode.getCodeValue('T')) == RIGHT.getIndex())
				tool = RIGHT;
		}

		// handle unrecognised GCode
		if (GCodeEnumeration.getGCode("M", (int) gcode.getCodeValue('M')) == null) {
			String message = "Unrecognized MCode! M" + (int) gcode.getCodeValue('M');
			System.err.println(message);
		}

		switch (GCodeEnumeration.getGCode("M", (int) gcode.getCodeValue('M'))) {
		case M0:
			// M0 == unconditional halt
			return true;
		case M1:
			// M1 == optional halt
			return false;
		case M2:
			// M2 == program end
			return true;
		case M6:
			// System.out.println("Waiting for toolhead to heat...");
			break;
		// enable drives
		case M17:
			break;
		// disable drives
		case M18:
			break;
		case M70:
			// print message
		case M71:
			// User-clearable pause
			break;
		case M72:
			// Play a tone or song as stored on the machine
			break;
		case M73:
			// Manually sets the percent complete info on the bot.
			break;
		// turn extruder on, forward
		case M101:
			tool.setMotorDirection(CLOCKWISE);
			tool.enableMotor();
			break;
		// turn extruder on, reverse
		case M102:
			tool.setMotorDirection(COUNTERCLOCKWISE);
			tool.enableMotor();
			break;
		// turn extruder off
		case M103:
			tool.disableMotor();
			break;
		// custom code for temperature control
		case M104:
			if (gcode.hasCode('S'))
				tool.setCurrentTemperature(gcode.getCodeValue('S'));
			break;
		case M105:
			break;
		// turn AutomatedBuildPlatform on
		case M106:
			break;
		// turn AutomatedBuildPlatform off
		case M107:
			break;
		// set max extruder speed, RPM
		case M108:
			if (gcode.hasCode('S'))
				tool.setMotorSpeedPWM((int) gcode.getCodeValue('S'));
			else if (gcode.hasCode('R'))
				tool.setMotorSpeedRPM(gcode.getCodeValue('R'));
			break;
		// set build platform temperature
		case M109:
		case M140: // skeinforge chamber code for HBP
			break;
		// set build chamber temperature
		case M110:
			break;
		// valve open
		case M126:
			break;
		// valve close
		case M127:
			break;
		// where are we?
		case M128:
			break;
		// Instruct the machine to store it's current position to EEPROM
		case M131: { // these braces provide a new level of scope to avoid name clash on axes
			EnumSet<AxisId> axes = getAxes(gcode);
			if (home == null)
				home = new Point5d();

			if (axes.contains(AxisId.X))
				home.setX(current.x());
			if (axes.contains(AxisId.Y))
				home.setY(current.y());
			if (axes.contains(AxisId.Z))
				home.setZ(current.z());
			if (axes.contains(AxisId.A))
				home.setA(current.a());
			if (axes.contains(AxisId.B))
				home.setB(current.b());
		}
			break;
		// Instruct the machine to restore it's current position from EEPROM
		case M132:
			if (home != null) {
				EnumSet<AxisId> axes = getAxes(gcode);

				if (axes.contains(AxisId.X))
					current.setX(home.x());
				if (axes.contains(AxisId.Y))
					current.setY(home.y());
				if (axes.contains(AxisId.Z))
					current.setZ(home.z());
				if (axes.contains(AxisId.A))
					current.setA(home.a());
				if (axes.contains(AxisId.B))
					current.setB(home.b());
			}
			break;
		// Silently ignore these
		case M141: // skeinforge chamber plugin chamber temperature code
		case M142: // skeinforge chamber plugin holding pressure code
			break;

		// initialize to default state.
		case M200:
			break;
		// set servo 1 position
		case M300:
			break;
		// set servo 2 position
		case M301:
			break;
		// Start data capture
		case M310:
			break;

		// Stop data capture
		case M311:
			break;

		// Log a note to the data capture store
		case M312:
			break;
		default:
			System.err.print("Unrecognized GCode" + gcode);
			return true;
		}
		return false;
	}

	private boolean buildGCodes(GCodeCommand gcode) {

		// start us off at our current position...
		Point5d pos = new Point5d(current);

		// initialize our points, etc.
		double xVal = convertToMM(gcode.getCodeValue('X'), units); // / X units
		double yVal = convertToMM(gcode.getCodeValue('Y'), units); // / Y units
		double zVal = convertToMM(gcode.getCodeValue('Z'), units); // / Z units
		double aVal = convertToMM(gcode.getCodeValue('A'), units); // / A units
		double bVal = convertToMM(gcode.getCodeValue('B'), units); // / B units
		// Note: The E axis is treated internally as the A or B axis
		double eVal = convertToMM(gcode.getCodeValue('E'), units); // / E units

		// adjust for our offsets
		xVal += currentOffset.x();
		yVal += currentOffset.y();
		zVal += currentOffset.z();

		// absolute just specifies the new position
		if (absoluteMode) {
			if (gcode.hasCode('X'))
				pos.setX(xVal);
			if (gcode.hasCode('Y'))
				pos.setY(yVal);
			if (gcode.hasCode('Z'))
				pos.setZ(zVal);
			if (gcode.hasCode('A'))
				pos.setA(aVal);
			if (gcode.hasCode('E')) {
				if (tool == RIGHT)
					pos.setA(eVal);
				else if (tool == LEFT)
					pos.setB(eVal);
			}
			if (gcode.hasCode('B'))
				pos.setB(bVal);
		}
		// relative specifies a delta
		else {
			if (gcode.hasCode('X'))
				pos.setX(pos.x() + xVal);
			if (gcode.hasCode('Y'))
				pos.setY(pos.y() + yVal);
			if (gcode.hasCode('Z'))
				pos.setZ(pos.z() + zVal);
			if (gcode.hasCode('A'))
				pos.setA(pos.a() + aVal);
			if (gcode.hasCode('E')) {
				if (tool == RIGHT)
					pos.setA(pos.a() + eVal);
				else if (tool == LEFT)
					pos.setB(pos.b() + eVal);
			}
			if (gcode.hasCode('B'))
				pos.setB(pos.b() + bVal);
		}

		// Get feedrate if supplied
		if (gcode.hasCode('F')) {
			// Read feedrate in mm/min.
			feedrate = gcode.getCodeValue('F');
		}

		GCodeEnumeration codeEnum = GCodeEnumeration.getGCode("G", (int) gcode.getCodeValue('G'));

		// handle unrecognised GCode
		if (codeEnum == null) {
			String message = "Unrecognized GCode! G" + (int) gcode.getCodeValue('G');
			System.err.println(message);
		}

		switch (codeEnum) {
		// these are basically the same thing, but G0 is supposed to do it as quickly as possible.
		// Rapid Positioning
		case G0:
			if (gcode.hasCode('F')) {
				// Allow user to explicitly override G0 feedrate if they so desire.
				// already did it
			} else {
				// Compute the most rapid possible rate for this move.
				Point5d diff = current;
				diff.sub(pos);
				diff.absolute();
				double length = diff.length();
				double selectedFR = Double.MAX_VALUE;
				Point5d maxFR = new Point5d(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE,
						Double.MAX_VALUE, Double.MAX_VALUE);
				// Compute the feedrate using assuming maximum feed along each axis, and select
				// the slowest option.
				for (int idx = 0; idx < 3; idx++) {
					double axisMove = diff.get(idx);
					if (axisMove == 0) {
						continue;
					}
					double candidate = maxFR.get(idx) * length / axisMove;
					if (candidate < selectedFR) {
						selectedFR = candidate;
					}
				}
				// Add a sane default for the null move, just in case.
				if (selectedFR == Double.MAX_VALUE) {
					selectedFR = maxFR.get(0);
				}
				feedrate = selectedFR;
			}
			queuePoint(pos);
			break;
		// Linear Interpolation
		case G1:
			// set our target.
			// System.out.println(current + "\t" + pos);
			queuePoint(pos);
			break;
		// dwell
		case G4:
			break;
		case G10:
			if (gcode.hasCode('P')) {
				int offsetSystemNum = ((int) gcode.getCodeValue('P'));
				if (offsetSystemNum >= 0 && offsetSystemNum <= 6) {
					if (gcode.hasCode('X'))
						offsetSystems[offsetSystemNum].setX(gcode.getCodeValue('X'));
					if (gcode.hasCode('Y'))
						offsetSystems[offsetSystemNum].setY(gcode.getCodeValue('Y'));
					if (gcode.hasCode('Z'))
						offsetSystems[offsetSystemNum].setZ(gcode.getCodeValue('Z'));
					if (!gcode.hasCode('X') && !gcode.hasCode('Y') && !gcode.hasCode('Z')) {
						offsetSystems[offsetSystemNum].setX(gcode.getCodeValue('X'));
						offsetSystems[offsetSystemNum].setY(gcode.getCodeValue('Y'));
						offsetSystems[offsetSystemNum].setZ(gcode.getCodeValue('Z'));
					}
					System.out.println("Setting offset " + offsetSystemNum + "="
							+ offsetSystems[offsetSystemNum]);
				}
			}
			break;
		// Inches for Units
		case G20:
		case G70:
			units = UNITS_INCHES;
			break;
		// mm for Units
		case G21:
		case G71:
			units = UNITS_MM;
			break;
		// This should be "return to home". We need to introduce new GCodes for homing.
		// replaced by G161, G162
		case G162:
		case G28: {
			// home all axes?
			EnumSet<AxisId> axes = getAxes(gcode);

			if (axes.contains(AxisId.X))
				current.setX(endstopsMax.x());
			if (axes.contains(AxisId.Y))
				current.setY(endstopsMax.y());
			if (axes.contains(AxisId.Z))
				current.setZ(endstopsMax.z());
			if (axes.contains(AxisId.A))
				current.setA(endstopsMax.a());
			if (axes.contains(AxisId.B))
				current.setB(endstopsMax.b());
			source.add(createLineSegment(tool.isMotorEnabled()));
		}
			break;
		// New code: home negative.
		case G161: {
			// home all axes?
			EnumSet<AxisId> axes = getAxes(gcode);

			if (axes.contains(AxisId.X))
				current.setX(endstopsMin.x());
			if (axes.contains(AxisId.Y))
				current.setY(endstopsMin.y());
			if (axes.contains(AxisId.Z))
				current.setZ(endstopsMin.z());
			if (axes.contains(AxisId.A))
				current.setA(endstopsMin.a());
			if (axes.contains(AxisId.B))
				current.setB(endstopsMin.b());

			source.add(createLineSegment(tool.isMotorEnabled()));
		}
			break;
		// master offset
		case G53:
			current.sub(currentOffset);
			currentOffset = offsetSystems[0];
			current.add(currentOffset);
			source.add(createLineSegment(false));
			System.out.println("Switching to offset " + currentOffset);
			break;
		// fixture offset 1
		case G54:
			current.sub(currentOffset);
			currentOffset = offsetSystems[1];
			current.add(currentOffset);
			source.add(createLineSegment(false));
			System.out.println("Switching to offset " + currentOffset);
			break;
		// fixture offset 2
		case G55:
			current.sub(currentOffset);
			currentOffset = offsetSystems[2];
			current.add(currentOffset);
			source.add(createLineSegment(false));
			System.out.println("Switching to offset " + currentOffset);
			break;
		// fixture offset 3
		case G56:
			current.sub(currentOffset);
			currentOffset = offsetSystems[3];
			current.add(currentOffset);
			source.add(createLineSegment(false));
			System.out.println("Switching to offset " + currentOffset);
			break;
		// fixture offset 4
		case G57:
			current.sub(currentOffset);
			currentOffset = offsetSystems[4];
			current.add(currentOffset);
			source.add(createLineSegment(false));
			System.out.println("Switching to offset " + currentOffset);
			break;
		// fixture offset 5
		case G58:
			current.sub(currentOffset);
			currentOffset = offsetSystems[5];
			current.add(currentOffset);
			source.add(createLineSegment(false));
			System.out.println("Switching to offset " + currentOffset);
			break;
		// fixture offset 6
		case G59:
			current.sub(currentOffset);
			currentOffset = offsetSystems[6];
			current.add(currentOffset);
			source.add(createLineSegment(false));
			System.out.println("Switching to offset " + currentOffset);
			break;
		// Absolute Positioning
		case G90:
			absoluteMode = true;
			break;
		// Incremental Positioning
		case G91:
			absoluteMode = false;
			break;
		// Set position
		case G92:
			if (gcode.hasCode('X'))
				current.setX(xVal);
			if (gcode.hasCode('Y'))
				current.setY(yVal);
			if (gcode.hasCode('Z'))
				current.setZ(zVal);
			if (gcode.hasCode('A'))
				current.setA(aVal);
			// Note: The E axis is treated internally as the A axis
			if (gcode.hasCode('E'))
				current.setA(eVal);
			if (gcode.hasCode('B'))
				current.setB(bVal);
			source.add(createLineSegment(tool.isMotorEnabled()));
			break;
		case G130:
			break;
		// error, error!
		default:
			System.err.print("Unrecognized GCode" + gcode);
			return true;
		}
		return false;
	}

	private double convertToMM(double value, int units) {
		if (units == UNITS_INCHES) {
			return value * 25.4;
		}
		return value;
	}

	private EnumSet<AxisId> getAxes(GCodeCommand gcode) {
		EnumSet<AxisId> axes = EnumSet.noneOf(AxisId.class);

		if (gcode.hasCode('X'))
			axes.add(AxisId.X);
		if (gcode.hasCode('Y'))
			axes.add(AxisId.Y);
		if (gcode.hasCode('Z'))
			axes.add(AxisId.Z);
		if (gcode.hasCode('A'))
			axes.add(AxisId.A);
		if (gcode.hasCode('B'))
			axes.add(AxisId.B);

		return axes;
	}

	public double getSafeFeedrate(Point5d delta) {
		double feedrateTmp = feedrate;

		Point5d maxFeedrates = new Point5d(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE,
				Double.MAX_VALUE, Double.MAX_VALUE);

		// System.out.println("max feedrates: " + maxFeedrates);

		// If the current feedrate is 0, set it to the maximum feedrate of any
		// of the machine axis. If those are also all 0 (misconfiguration?),
		// set the feedrate to 1.
		// TODO: Where else is feedrate set?
		if (feedrateTmp == 0) {
			for (int i = 0; i < 5; i++) {
				feedrateTmp = Math.max(feedrateTmp, maxFeedrates.get(i));
			}
			feedrateTmp = Math.max(feedrateTmp, 1);
		}

		// Determine the magnitude of this delta
		double length = delta.length();

		// For each axis: if the current feedrate will cause this axis to move
		// faster than it's maximum feedrate, lower the system feedrate so
		// that it will be compliant.
		for (int i = 0; i < 5; i++) {
			if (delta.get(i) != 0) {
				if (feedrateTmp * delta.get(i) / length > maxFeedrates.get(i)) {
					feedrateTmp = maxFeedrates.get(i) * length / delta.get(i);
				}
			}
		}

		// Return the feedrate, which is how fast the toolhead will be moving (magnitude of the
		// toolhead velocity)
		return feedrateTmp;
	}

	public void queuePoint(final Point5d p) {

		/*
		 * So, it looks like points specified in A/E/B commands turn in the opposite direction from
		 * turning based on tool RPM
		 * 
		 * I recieve all points as absolute values, and, really, all extruder values should be sent
		 * as relative values, just in case we end up with an overflow?
		 */
		Point5d target = new Point5d(p);
		// is this point even step-worthy? Only compute nonzero moves
		double deltaSteps = current.distance(target);
		if (deltaSteps > 0.0) {
			// relative motion in mm
			Point5d deltaMM = new Point5d();
			deltaMM.sub(target, current); // delta = p - current

			// A and B are always sent as relative, rec'd as absolute, so adjust our target
			// accordingly
			// Also, our machine turns the wrong way? make it negative.
			target.setA(-deltaMM.a());
			target.setB(-deltaMM.b());

			// calculate the time to make the move
			Point5d delta3d = new Point5d();
			delta3d.setX(deltaMM.x());
			delta3d.setY(deltaMM.y());
			delta3d.setZ(deltaMM.z());
			double minutes = delta3d.distance(new Point5d()) / getSafeFeedrate(deltaMM);

			// if minutes == 0 here, we know that this is just an extrusion in place
			// so we need to figure out how long it will take
			if (minutes == 0) {
				Point5d delta2d = new Point5d();
				delta2d.setA(deltaMM.a());
				delta2d.setB(deltaMM.b());

				minutes = delta2d.distance(new Point5d()) / getSafeFeedrate(deltaMM);
			}

			// if either a or b is 0, but their motor is on, create a distance for them
			if (deltaMM.a() == 0) {
				ToolModel aTool = extruderHijackedMap.get(AxisId.A);
				if (aTool != null && aTool.isMotorEnabled()) {
					// minute * revolution/minute
					double numRevolutions = minutes * aTool.getMotorSpeedRPM();
					// steps/revolution * mm/steps
					double mmPerRevolution = aTool.getMotorSteps() * (1 / stepsPerMM.a());
					// set distance
					target.setA(-(numRevolutions * mmPerRevolution));
				}
			}
			if (deltaMM.b() == 0) {
				ToolModel bTool = extruderHijackedMap.get(AxisId.B);
				if (bTool != null && bTool.isMotorEnabled()) {
					// minute * revolution/minute
					double numRevolutions = minutes * bTool.getMotorSpeedRPM();
					// steps/revolution * mm/steps
					double mmPerRevolution = bTool.getMotorSteps() * (1 / stepsPerMM.b());
					// set distance
					target.setB(-(numRevolutions * mmPerRevolution));
				}
			}

			// because of the hinky stuff we've been doing with A & B axes, just pretend we've
			// moved where we thought we were moving
			Point5d fakeOut = new Point5d(target);
			fakeOut.setA(p.a());
			fakeOut.setB(p.b());
			current = fakeOut;

			source.add(createLineSegment(target.a() != 0 || target.b() != 0));
			System.out.println(target.a() != 0 || target.b() != 0);
		}
	}

	private LineSegment createLineSegment(boolean extruding) {
		LineSegment result = new LineSegment(prev, current, 0, (float) feedrate, tool.getIndex(),
				extruding);

		prev = current;
		return result;
	}
}
