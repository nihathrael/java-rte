package rte.recognizers;

import java.util.ArrayList;

import rte.pairs.SentenceNode;
import rte.pairs.Text;
import rte.util.IDFCalculator;

public abstract class BasicIDFMatcher implements EntailmentRecognizer {

	private final IDFCalculator idfCalculator;

	public BasicIDFMatcher(IDFCalculator idf) {
		this.idfCalculator = idf;
	}

	abstract String selectKey(SentenceNode node);

	public double entails(Text text, Text hypothesis) {

		ArrayList<SentenceNode> textNodes = text.getAllSentenceNodes();
		ArrayList<SentenceNode> hypoNodes = hypothesis.getAllSentenceNodes();

		double idfSumMatch = 0.0;
		double idfHypoSum = 0.0;
		for (SentenceNode hypoNode : hypoNodes) {
			for (SentenceNode textNode : textNodes) {
				if (match(hypoNode, textNode)) {
					idfSumMatch += idfCalculator
							.getValueFor(selectKey(hypoNode));
					break;
				}
			}

			if (selectKey(hypoNode) != null) {
				idfHypoSum += idfCalculator.getValueFor(selectKey(hypoNode));
			}
		}

		return (idfSumMatch / idfHypoSum);
	}

	boolean match(SentenceNode hypoNode, SentenceNode textNode) {
		return selectKey(textNode) != null && selectKey(hypoNode) != null
				&& selectKey(textNode).equals(selectKey(hypoNode));
	}

	public String getName() {
		return BasicIDFMatcher.class.getSimpleName();
	}

}