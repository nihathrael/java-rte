package rte.util;

import java.util.ArrayList;

import rte.pairs.AdvPair;
import rte.pairs.SentenceNode;

public class LemmaIDFCalculator extends IDFCalculator {
	
	public LemmaIDFCalculator(ArrayList<AdvPair> pairs) {
		super(pairs);
	}

	String selectKey(SentenceNode node) {
		return node.lemma;
	}

}
