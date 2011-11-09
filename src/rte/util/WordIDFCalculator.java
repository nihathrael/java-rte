package rte.util;

import java.util.ArrayList;

import rte.pairs.AdvPair;
import rte.pairs.SentenceNode;

public class WordIDFCalculator extends IDFCalculator {
	
	public WordIDFCalculator(ArrayList<AdvPair> pairs) {
		super(pairs);
	}

	String selectKey(SentenceNode node) {
		return node.word;
	}

}
