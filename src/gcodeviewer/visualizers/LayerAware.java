package gcodeviewer.visualizers;

import gcodeviewer.utils.LineSegment;

import java.util.HashMap;
import java.util.Map;

public interface LayerAware {

	/**
	 * floats in the range 0 to 100 inclusive are mapped to the actual layer numbers
	 */
	public void setMinimumLayer(float min);
	public void setMaximumLayer(float max);
	public int getNumLayers();
	
	public class LayerAwareImpl implements LayerAware {

		private Integer numLayers = 0;
		protected float min, max, current;
		
		// layer registration
		private final Map<LineSegment, Integer> layers = new HashMap<LineSegment, Integer>(100);
		private boolean newlayer = true;
//		private int 
		
		public void newLayer() {
			// the next segment will start a new layer
			newlayer = true;
		}
		
		public void registerLineSegment(LineSegment ls) {
			if(newlayer) {
				layers.put(ls, numLayers);
				numLayers++;
			}
		}
		
		public boolean checkSegment(LineSegment ls) {
			return false;
		}
		
		
		@Override
		public void setMinimumLayer(float min) {
			this.min = min;
		}

		@Override
		public void setMaximumLayer(float max) {
			this.max = max;
		}

		@Override
		public int getNumLayers() {
			return numLayers;
		}
		
	}
}
