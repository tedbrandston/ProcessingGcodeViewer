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
		protected float min = 0, max = 0;
		
		// layer registration
		private final Map<LineSegment, Integer> layers = new HashMap<LineSegment, Integer>(100);
		private boolean newlayer = true;
		
		private int currentLayer = 0;
		
		public void newLayer() {
			// the next segment will start a new layer
			newlayer = true;
		}
		
		public void registerLineSegment(LineSegment ls) {
			if(newlayer) {
				layers.put(ls, numLayers);
				numLayers++;
				newlayer = false;
			}
		}
		
		public boolean checkSegment(LineSegment ls) {
			if(layers.containsKey(ls))
				currentLayer = layers.get(ls);
			
			// if min <= currentLayer <= max 
			return min <= currentLayer && currentLayer <= max;
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
