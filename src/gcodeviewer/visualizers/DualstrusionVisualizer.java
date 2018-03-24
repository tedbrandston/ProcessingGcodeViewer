package gcodeviewer.visualizers;

import gcodeviewer.toolpath.GCodeEvent;
import gcodeviewer.toolpath.GCodeEvent.MoveTo;
import gcodeviewer.toolpath.GCodeEvent.NewLayer;
import gcodeviewer.toolpath.GCodeEvent.SetPosition;
import gcodeviewer.toolpath.GCodeEvent.SetToolhead;
import gcodeviewer.toolpath.GCodeEventToolpath;
import gcodeviewer.utils.LineSegment;
import gcodeviewer.utils.Point5d;

import java.util.ArrayList;

import processing.core.PGraphics;
import replicatorg.ToolheadAlias;

public class DualstrusionVisualizer extends GCodeVisualizer implements LayerAware {

	private static final int T0_COLOR = color(32, 255, 128, 128);
	private static final int T1_COLOR = color(32, 128, 255, 128);

	private final LayerAwareImpl layers = new LayerAwareImpl();
	
	private ArrayList<LineSegment> lines;
	private boolean ready = false;
	
	@Override
	public void draw(PGraphics g) {
		if(ready) {
			for(LineSegment ls : lines) {
				if(layers.checkSegment(ls)) {
					ls.draw(g);
				}
			}
		}
	}
	
	@Override
	public void setToolpath(GCodeEventToolpath toolpath) {
		ready = false;
		super.setToolpath(toolpath);
		
		setMinimumLayer(0);
		setMaximumLayer(toolpath.getNumLayers());
		
		lines = new ArrayList<LineSegment>();
		
		int color = T0_COLOR;
		Point5d currentPos = new Point5d();
		
		for(GCodeEvent evt : toolpath.events()) {
			
			if(evt instanceof SetToolhead)
				color = ( ((SetToolhead)evt).tool == ToolheadAlias.RIGHT ? T0_COLOR : T1_COLOR);

			if(evt instanceof SetPosition)
				currentPos = null;
			
			if(evt instanceof NewLayer)
				layers.newLayer();
			
			if(evt instanceof MoveTo) {
				Point5d newPos = ((MoveTo)evt).point;
				if(newPos.a() != 0 || newPos.b() != 0) {
					if(currentPos == null)
						currentPos = newPos;
					LineSegment newSegment = new LineSegment(currentPos, newPos, color);
					lines.add(newSegment);
					layers.registerLineSegment(newSegment);
					currentPos = newPos;
				}
				
			}
		}
		lines.trimToSize();
		ready = true;
	}
	
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
