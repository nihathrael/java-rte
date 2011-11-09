package rte.recognizers;

import rte.pairs.SentenceNode;

public class LemmaMatching extends BasicMatcher {

	public String getName() {
		return LemmaMatching.class.getSimpleName();
	}

	@Override
	boolean match(SentenceNode hypoNode, SentenceNode textNode) {
		return textNode.lemma != null && hypoNode.lemma != null
				&& textNode.lemma.equals(hypoNode.lemma);
	}
}
