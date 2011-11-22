package rte.recognizers;

import rte.pairs.SentenceNode;
import rte.util.WordNetAccessor;

public class SynonymMatching extends BasicMatcher {

	public String getName() {
		return SynonymMatching.class.getSimpleName();
	}

	@Override
	boolean match(SentenceNode hypoNode, SentenceNode textNode) {
		return WordNetAccessor.getInstance().synonymsOverlap(hypoNode, textNode);
	}
}
