package gcodeviewer.parsers;

import gcodeviewer.toolpath.GCodeEventToolpath;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public abstract class GCodeParser {

	// public GCodeSource source;
	protected final GCodeEventToolpath path = new GCodeEventToolpath();

	public abstract void parse(File gcode);

	public GCodeEventToolpath getPath() {
		return path;
	}
	
	protected List<String> readFile(File file) {
		try {
			Scanner input = new Scanner(file);
			List<String> result = new ArrayList<String>();
			
			while(input.hasNext()) {
				result.add(input.nextLine());
			}
			
			return result;
		} catch (FileNotFoundException e) {
			System.out.println("I can't parse a file if I can't find it");
		}
		return null;
	}
}
