package rte.util;

import java.util.ArrayList;

import rte.pairs.THPair;
import rte.pairs.SentenceNode;

public class LemmaIDFCalculator extends IDFCalculator {
	
	public LemmaIDFCalculator(ArrayList<THPair> pairs) {
		super(pairs);
	}

	String selectKey(SentenceNode node) {
		return node.lemma;
	}

}
