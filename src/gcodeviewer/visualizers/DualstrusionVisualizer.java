package gcodeviewer.visualizers;

import gcodeviewer.toolpath.GCodeEvent;
import gcodeviewer.toolpath.GCodeEventToolpath;
import gcodeviewer.toolpath.events.MoveTo;
import gcodeviewer.toolpath.events.SetPosition;
import gcodeviewer.toolpath.events.SetToolhead;
import gcodeviewer.utils.LineSegment;
import gcodeviewer.utils.Point5d;

import java.util.ArrayList;

import processing.core.PGraphics;
import replicatorg.ToolheadAlias;

public class DualstrusionVisualizer extends GCodeVisualizer implements LayerAware {

	private static final int T0_COLOR = color(32, 255, 128, 128);
	private static final int T1_COLOR = color(32, 128, 255, 128);
	
	private ArrayList<LineSegment> lines;
	private boolean ready = false;
	
	@Override
	public void draw(PGraphics g) {
		if(ready) {
			for(LineSegment ls : lines) {
				ls.draw(g);
			}
		}
	}
	
	@Override
	public void setToolpath(GCodeEventToolpath toolpath) {
		ready = false;
		super.setToolpath(toolpath);
		
		lines = new ArrayList<LineSegment>();
		
		int color = T0_COLOR;
		Point5d currentPos = new Point5d();
		
		for(GCodeEvent evt : toolpath.events()) {
			if(evt instanceof SetToolhead)
				color = ( ((SetToolhead)evt).tool == ToolheadAlias.RIGHT ? T0_COLOR : T1_COLOR);

			if(evt instanceof SetPosition)
				currentPos = null;
			
			if(evt instanceof MoveTo) {
				Point5d newPos = ((MoveTo)evt).point;
				if(currentPos == null)
					currentPos = newPos;
				lines.add(new LineSegment(currentPos, newPos, color));
				currentPos = newPos;
			}
		}
		lines.trimToSize();
		ready = true;
	}

	private final LayerAwareImpl layers = new LayerAwareImpl();
	
	@Override
	public void setMinimumLayer(float min) {
		layers.setMinimumLayer(min);
	}

	@Override
	public void setMaximumLayer(float max) {
		layers.setMaximumLayer(max);
	}

	@Override
	public int getNumLayers() {
		return layers.getNumLayers();
	}

}
