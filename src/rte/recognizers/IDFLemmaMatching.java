package rte.recognizers;

import rte.pairs.SentenceNode;
import rte.util.LemmaIDFCalculator;

public class IDFLemmaMatching extends BasicIDFMatcher {


	public IDFLemmaMatching(LemmaIDFCalculator idf) {
		super(idf);
	}

	String selectKey(SentenceNode hypoNode) {
		return hypoNode.lemma;
	}
	
	public String getName() {
		return IDFLemmaMatching.class.getSimpleName();
	}

}