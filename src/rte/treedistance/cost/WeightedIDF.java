package rte.treedistance.cost;

import rte.pairs.SentenceNode;
import rte.util.IDFCalculator;

public class WeightedIDF implements TreeEditCost {

	IDFCalculator idfCalc;

	public WeightedIDF(IDFCalculator idfs) {
		idfCalc = idfs;
	}

	public double cost(SentenceNode m, SentenceNode n) {

		// Deletion
		if (n == null)
			return 0.0;

		// Insert
		if (m == null) {
			return idfCalc.getValueFor(n.word);
		}
		
		// Substitute
		if (n.word.equals(m.word)) {
			return 0.0;
		} else {
			return 1.0;
		}
	}

}
