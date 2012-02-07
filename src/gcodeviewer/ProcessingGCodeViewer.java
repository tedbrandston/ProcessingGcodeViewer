/*
Author: Noah Levy
Modified by Ted Brandston

This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see http://www.gnu.org/licenses.
 */
package gcodeviewer;

import gcodeviewer.parsers.GCodeParser;
import gcodeviewer.parsers.MightyParser;
import gcodeviewer.utils.LineSegment;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import peasy.PeasyCam;
import processing.core.PApplet;

import com.processinghacks.arcball.ArcBall;

public class ProcessingGCodeViewer extends PApplet {

	private static int staticColor(int x, int y, int z) {
		return 0xff000000 | (x << 16) | (y << 8) | z;
	}

	private static int staticColor(int x, int y, int z, int a) {
		return (a << 24) | (x << 16) | (y << 8) | z;
	}

	private static int staticColor(int c, int a) {
		c = c & 0x00ffffff;
		return (a << 24) | c;
	}

	private enum ColoringStyle {
		DUALSTRUSION {
			@Override
			public int getColor(LineSegment l) {
				int alpha = l.getExtruding() ? 0xff : 0x00;
				if (l.getToolhead() == 0)
					return staticColor(BLUE, SOLID);
				return staticColor(GREEN, SOLID);
			}
		},
		FEEDRATE {
			@Override
			public int getColor(LineSegment l) {
				int speed = (int) PApplet.map(l.getSpeed(), 0, 5000, 0, 255);
				return staticColor(speed, speed, speed);
			}
		},
		TEMPERATURE {
			@Override
			public int getColor(LineSegment l) {
				// white
				return WHITE;
			}
		};

		public abstract int getColor(LineSegment l);
	}

	private final boolean dualExtrusionColoring = false;

	private final boolean is2D = false;
	private boolean isDrawable = false; // True if a file is loaded; false if not
	private final boolean isPlatformed = true;
	private final boolean isSpeedColored = true;
	private String sourceFile; // The path of the gcode File
	private GCodeParser gcode; // An ArrayList of linesegments composing the model
	private final int curScale = 1;
	private final int curLayer = 0;
	private final ColoringStyle style = ColoringStyle.DUALSTRUSION;

	// //////////ALPHA VALUES//////////////

	private final static int TRANSPARENT = 20;
	private final static int SOLID = 100;
	private final static int SUPERSOLID = 255;

	// ////////////////////////////////////

	// //////////COLOR VALUES/////////////

	private final static int RED = staticColor(255, 200, 200);
	private final static int BLUE = staticColor(0, 255, 255);
	private final static int PURPLE = staticColor(242, 0, 255);
	private final static int YELLOW = staticColor(237, 255, 0);
	private final static int OTHER_YELLOW = staticColor(234, 212, 7);
	private final static int GREEN = staticColor(33, 255, 0);
	private final static int WHITE = staticColor(255, 255, 255);

	// ////////////////////////////////////

	// /////////SPEED VALUES///////////////

	private final float LOW_SPEED = 700;
	private final float MEDIUM_SPEED = 1400;
	private final float HIGH_SPEED = 1900;

	// ////////////////////////////////////

	// ////////SLIDER VALUES/////////////

	private final int minSlider = 1;
	private int maxSlider;
	private int defaultValue;

	// //////////////////////////////////

	// ///////Canvas Size///////////////

	private final int xSize = 5 * screen.width / 6;
	private final int ySize = 5 * screen.height / 6;

	private ArcBall arcball;
	private PeasyCam cam;

	/*
	 * private static String argument =null;
	 * 
	 * 
	 * public static void main(String args[]) { PApplet.main(new String[] {"ProcessingGcodeViewer"
	 * }); if(args.length >= 1) { argument = args[0]; } }
	 */

	@Override
	public void setup() {

		size(xSize, ySize, P3D); // OpenGL is the renderer; untested with p3d

		background(0); // Make background black

		noSmooth();
		selectFile();
		// sourceFile = "C:/Users/thbrandston/Documents/5ddualcube.gcode";

		if (sourceFile != null) {
			generateObject();
		}
		setupCamera();
	}

	/*
	 * public void controlEvent(ControlEvent theEvent) { if(theEvent.isGroup()) {
	 * if(theEvent.group().name() == "2DBox") { int i = 0; int choice2D =
	 * (int)theEvent.group().arrayValue()[0]; println("2D view is" + choice2D); if(choice2D == 1) {
	 * make2D(); } if(choice2D == 0) { make3D(); } int dualChoice =
	 * (int)theEvent.group().arrayValue()[1];
	 * 
	 * if(dualChoice == 1) { dualExtrusionColoring = true;
	 * 
	 * } if(dualChoice == 0) { dualExtrusionColoring = false; } int platformChoice =
	 * (int)theEvent.group().arrayValue()[2]; if(platformChoice == 1) { isPlatformed = true;
	 * 
	 * } if(platformChoice == 0) { isPlatformed = false; } } } else if(theEvent.controller().name()
	 * == "Choose File...") { selectFile(); } else if(theEvent.controller().name() == "lowSpeed") {
	 * LOW_SPEED = Float.parseFloat(theEvent.controller().stringValue()); } else
	 * if(theEvent.controller().name() == "mediumSpeed") { MEDIUM_SPEED =
	 * Float.parseFloat(theEvent.controller().stringValue()); } else if(theEvent.controller().name()
	 * == "highSpeed") { HIGH_SPEED = Float.parseFloat(theEvent.controller().stringValue()); } else
	 * { float pos[] = cam.getLookAt(); if(theEvent.controller().name() == "Left") {
	 * cam.lookAt(pos[0] - 1,pos[1],pos[2],0); } else if(theEvent.controller().name() == "Up") {
	 * cam.lookAt(pos[0],pos[1] - 1,pos[2],0); } else if(theEvent.controller().name() == "Right") {
	 * cam.lookAt(pos[0] + 1,pos[1],pos[2],0); } else if(theEvent.controller().name() == "Down") {
	 * cam.lookAt(pos[0],pos[1] + 1,pos[2],0); } } }
	 */

	public void generateObject() {
		// scale(1, -1, 1); // orient cooridnate plane right-handed props to
		// whosawwhatsis for discovering this

		gcode = new MightyParser();
		gcode.parse(readFiletoArrayList(sourceFile));
		isDrawable = true;
	}

	@Override
	public void draw() {
		background(0);

		if (isPlatformed) {
			fill(6, 13, 137);
			rect(-50, -50, 100, 100);
			noFill();
		}
		if (isDrawable) {

			float[] points = new float[6];

			int curTransparency = 0;
			int curColor = 255;

			for (LineSegment ls : gcode.source.getSourceList()) {
				stroke(style.getColor(ls));

				// if (!is2D || (ls.getLayer() == maxLayer)) {
				points = ls.getPoints(curScale);

				line(points[0], points[1], points[2], points[3], points[4], points[5]);
			}
			// endShape();
			// if ((curLayer != maxLayer) && is2D) {
			// cam.setDistance(cam.getDistance() + (maxLayer - curLayer) * .3, 0);
			// }
			// curLayer = maxLayer;
		}
	}

	public void setupCamera() {

		// arcball = new ArcBall(width / 2, height / 2, -500, 500, this);

		float fov = (float) (PI / 3.0);
		float cameraZ = (height / 2.0f) / tan(fov / 2.0f);
		// perspective(fov, width / height, 0.1f, cameraZ * 10.0f);
		// Calling perspective allows me to set 0.1 as the frustum's zNear which prevents a bunch of
		// clipping issues.
		cam = new PeasyCam(this, 0, 0, 0, 100); // parent, x, y, z, initial distance

		cam.setMinimumDistance(2);
		cam.setMaximumDistance(200);
		cam.setResetOnDoubleClick(false);
	}

	void selectFile() {

		try {

			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					JFileChooser fc = new JFileChooser(".");
					FileFilter gcodeFilter = new FileNameExtensionFilter("Gcode file", "gcode",
							"ngc");
					fc.setDialogTitle("Choose a file...");
					fc.setFileFilter(gcodeFilter);

					int returned = fc.showOpenDialog(frame);
					if (returned == JFileChooser.APPROVE_OPTION) {
						isDrawable = false;
						File file = fc.getSelectedFile();
						sourceFile = file.getPath();
						println(sourceFile);
						generateObject();

					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public ArrayList<String> readFiletoArrayList(String s) {
		ArrayList<String> vect;
		String lines[] = loadStrings(s);
		vect = new ArrayList<String>(Arrays.asList(lines));
		return vect;
	}
}