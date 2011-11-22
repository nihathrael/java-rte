package rte.recognizers;

import rte.pairs.SentenceNode;
import rte.util.WordNetAccessor;

public class LinSimilarityMatching extends BasicMatcher {

	public String getName() {
		return LinSimilarityMatching.class.getSimpleName();
	}

	@Override
	boolean match(SentenceNode hypoNode, SentenceNode textNode) {
		return WordNetAccessor.getInstance().getLinSimilarity(hypoNode.lemma, textNode.lemma) > 0.8;
	}
}
