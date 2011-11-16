package rte.treedistance.cost;

import rte.pairs.SentenceNode;

public class FreeDeletion implements TreeEditCost {

	public double cost(SentenceNode m, SentenceNode n) {	
		
		// Delete
		if(n == null) return 0.0;
		
		// Insert
		if(m == null) return 1.0;
		
		//substitute
		if (m.word.equals(n.word)) {
			return 0.0;
		} else {
			return 1.0;
		}
	}
	

}
