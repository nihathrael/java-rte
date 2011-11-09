package rte.recognizers;

import rte.pairs.SentenceNode;



public class LemmaAndPosMatching extends BasicMatcher {

	
	boolean match(SentenceNode hypoNode, SentenceNode textNode) {
		return textNode.lemma != null && hypoNode.lemma != null
				&& textNode.lemma.equals(hypoNode.lemma)
				&& textNode.posTag.matches(hypoNode.posTag);
	}
	
	public String getName() {
		return LemmaAndPosMatching.class.getSimpleName();
	}

}
