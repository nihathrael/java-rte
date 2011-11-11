package rte.treedistance.cost;

import rte.pairs.SentenceNode;
import rte.util.IDFCalculator;

public class WeightedIDF implements TreeEditCost {
	
	IDFCalculator idfCalc;
	
	public WeightedIDF(IDFCalculator idfs) {
		idfCalc = idfs;
	}

	@Override
	public double cost(SentenceNode m, SentenceNode n) {
		
		if(n==null && m==null) return 0.0;
		if(n == null) return 0.0;		
		
		if(n.word==null) return 0.0;
		
		if(m == null) {
			return idfCalc.getValueFor(n.word);
		}
		if(m.word == null) return 0.0;		
		if (n.word.equals(m.word)) {
			return 0.0;
		} else {
			return 1.0;
		}
	}
	
	
}
