package rte.treedistance.cost;

import rte.pairs.SentenceNode;
import rte.util.IDFCalculator;

public class WeightedLemmaIDF implements TreeEditCost {

	IDFCalculator idfCalc;

	public WeightedLemmaIDF(IDFCalculator idfs) {
		idfCalc = idfs;
	}

	public double cost(SentenceNode m, SentenceNode n) {

		// Deletion
		if (n == null) {
			return 0.0;
		}

		// Insert
		if (m == null) {
			return idfCalc.getValueFor(n.lemma);
		}
		
		// Substitute
		if (n.lemma.equals(m.lemma)) {
			return 0.0;
		} else {
			return idfCalc.getValueFor(n.lemma);
		}
	}

}
