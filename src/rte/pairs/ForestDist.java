package rte.pairs;

import java.util.HashMap;

public class ForestDist {

	HashMap<ForestTuple, Integer> distances = new HashMap<ForestTuple, Integer>();
	
	public ForestDist() {
		distances.put(new ForestTuple(null, null), 0);
	}

}
