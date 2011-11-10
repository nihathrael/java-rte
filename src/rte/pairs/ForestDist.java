package rte.pairs;

import java.util.HashMap;

public class ForestDist {

	HashMap<ForestTuple, Integer> distances = new HashMap<ForestTuple, Integer>();
	
	public ForestDist() {
		distances.put(new ForestTuple(new Forest(-1, -1), new Forest(-1, -1)), 0);
	}
	
	public void p(ForestTuple i, Integer b) {
		distances.put(i, b);
	}
	
	public Integer get(ForestTuple i) {
		return distances.get(i);
	}

}
