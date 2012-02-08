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
import gcodeviewer.visualizers.DualstrusionVisualizer;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import peasy.PeasyCam;
import processing.core.PApplet;

public class ProcessingGCodeViewer extends PApplet {

	private final boolean is2D = false;
	private float currentLayer;
	private final boolean isPlatformed = true;
	private final int scale = 1;
	

	private String sourceFile; // The path of the gcode File
	private final GCodeParser parser = new MightyParser(); // An ArrayList of linesegments composing the model
	private final GCodeVisualizer visualizer = new DualstrusionVisualizer();
	
	private PeasyCam cam;
	
	@Override
	public void setup() {

		size(500, 500, P3D); // OpenGL is the renderer; untested with p3d

		background(0); // Make background black
		noSmooth();
		
		setupCamera();
		
		selectFile();
	}

	@Override
	public void draw() {
		background(0);

		if (isPlatformed) {
			fill(6, 13, 137);
			rect(-50, -50, 100, 100);
			noFill();
		}
		// g is the PGraphics object on which drawing happens
		visualizer.draw(g);

	}

	public void setupCamera() {
		cam = new PeasyCam(this, 0, 0, 0, 100); // parent, x, y, z, initial distance

		cam.setMinimumDistance(2);
		cam.setMaximumDistance(200);
		cam.setResetOnDoubleClick(false);
	}

	public void generate() {
		parser.parse(readFiletoArrayList(sourceFile));
		visualizer.setToolpath(parser.getPath());
	}
	
	public void selectFile() {
		try {

			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					JFileChooser fc = new JFileChooser(".");
					FileFilter gcodeFilter = new FileNameExtensionFilter("Gcode file", "gcode",	"ngc");
					fc.setDialogTitle("Choose a file...");
					fc.setFileFilter(gcodeFilter);

					int returned = fc.showOpenDialog(frame);
					if (returned == JFileChooser.APPROVE_OPTION) {
						File file = fc.getSelectedFile();
						sourceFile = file.getPath();
						generate();
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