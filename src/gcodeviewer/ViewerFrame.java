package gcodeviewer;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

public class ViewerFrame extends JFrame {

	JTabbedPane controls;

	public ViewerFrame() {
		super("Processing GCode Viewer w/ Swing");

		ProcessingGCodeViewer gcview = new ProcessingGCodeViewer();

		setLayout(new MigLayout("gap 0, ins 0, fill"));
		add(gcview, "wrap, growx, growy");

		controls = new JTabbedPane();
		add(controls, "growx, growy");
		controls.addTab("Layer Controls", layerControls());
		controls.addTab("Tab 2", new JPanel());

		gcview.init();
	}

	private JPanel layerControls() {
		JPanel panel = new JPanel();

		return panel;
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
