package rte.treedistance.cost;

import rte.pairs.SentenceNode;

public class FreeDeletion implements TreeEditCost {

	@Override
	public double cost(SentenceNode m, SentenceNode n) {	
		if(n==null && m==null) return 0.0;
		
		if(n == null) return 0.0;
		if(m == null) return 1.0;
		
		if(m.word == null || n.word == null)
			return 0.0;
		
		if (m.word.equals(n.word)) {
			return 0.0;
		} else {
			return 1.0;
		}
	}
	

}
