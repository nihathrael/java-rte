package rte.util;

import java.util.ArrayList;

import rte.pairs.THPair;
import rte.pairs.SentenceNode;

public class WordIDFCalculator extends IDFCalculator {
	
	public WordIDFCalculator(ArrayList<THPair> pairs) {
		super(pairs);
	}

	String selectKey(SentenceNode node) {
		return node.word;
	}

}
