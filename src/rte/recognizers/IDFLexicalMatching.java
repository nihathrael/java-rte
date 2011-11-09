package rte.recognizers;

import rte.pairs.SentenceNode;
import rte.util.WordIDFCalculator;

public class IDFLexicalMatching extends BasicIDFMatcher {
	
	public IDFLexicalMatching(WordIDFCalculator idf) {
		super(idf);
	}

	String selectKey(SentenceNode hypoNode) {
		return hypoNode.word;
	}
	
	public String getName() {
		return IDFLexicalMatching.class.getSimpleName();
	}

}