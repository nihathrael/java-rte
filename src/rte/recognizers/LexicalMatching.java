package rte.recognizers;

import rte.pairs.SentenceNode;

public class LexicalMatching extends BasicMatcher {

	public String getName() {
		return LexicalMatching.class.getSimpleName();
	}

	@Override
	boolean match(SentenceNode hypoNode, SentenceNode textNode) {
		return textNode.word != null && hypoNode.word != null
				&& textNode.word.equals(hypoNode.word);
	}

}
