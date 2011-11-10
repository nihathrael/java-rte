package rte.pairs;

import java.util.HashMap;

public class ForestDist {

	HashMap<ForestTuple, Double> distances = new HashMap<ForestTuple, Double>();
	
	public ForestDist() {
		distances.put(new ForestTuple(new Forest(-1, -1), new Forest(-1, -1)), 0.0);
	}
	
	public void p(ForestTuple i, Double b) {
		distances.put(i, b);
	}
	
	public Double get(ForestTuple i) {
		return distances.get(i);
	}

}
