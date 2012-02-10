package gcodeviewer;

import gcodeviewer.parsers.GCodeParser;
import gcodeviewer.parsers.MightyParser;
import gcodeviewer.visualizers.DualstrusionVisualizer;
import gcodeviewer.visualizers.GCodeVisualizer;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.miginfocom.swing.MigLayout;

public class ViewerFrame extends JFrame {

	private final JTabbedPane controls;

	private File gcodeFile;

	private final GCodeParser parser;
	private final GCodeVisualizer visualizer;
	
	public ViewerFrame() {
		super("Processing GCode Viewer w/ Swing");

		ProcessingGCodeViewer gcview = new ProcessingGCodeViewer();
		
		parser = new MightyParser();
		visualizer = new DualstrusionVisualizer();

		setLayout(new MigLayout("gap 0, ins 0, fill"));
		add(gcview, "wrap, growx, growy");

		controls = new JTabbedPane();
		add(controls, "growx, growy");
		controls.addTab("Layer Controls", layerControls());
		controls.addTab("Options", generalControls());

		gcview.init();
		gcview.visualizer = visualizer;
		selectFile();
	}

	private JPanel generalControls() {
		JPanel panel = new JPanel();

		return panel;
	}

	private JPanel layerControls() {
		JPanel panel = new JPanel();

		return panel;
	}
	
	private void loadFile(File file) {
		gcodeFile = file;
		loadFile();
	}
	
	private void loadFile() {
		parser.parse(gcodeFile);
		visualizer.setToolpath(parser.getPath());
	}

	public void selectFile() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFileChooser fc = new JFileChooser(".");
				FileFilter gcodeFilter = new FileNameExtensionFilter("Gcode file", "gcode",	"ngc");
				fc.setDialogTitle("Choose a file...");
				fc.setFileFilter(gcodeFilter);

				int returned = fc.showOpenDialog(ViewerFrame.this);
				if (returned == JFileChooser.APPROVE_OPTION) {
					loadFile(fc.getSelectedFile());
				}
			}
		});
	}
	
	public static void main(String args[]) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				ViewerFrame frame = new ViewerFrame();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.pack();
				frame.setVisible(true);
			}

		});

	}
}
