package rte.recognizers;

import java.util.ArrayList;

import rte.pairs.SentenceNode;
import rte.pairs.Text;
import rte.util.PolarityCalculator;

public abstract class BasicMatcher implements EntailmentRecognizer {


	public double entails(Text text, Text hypothesis) {
	
		ArrayList<SentenceNode> textNodes = text.getAllSentenceNodes();
		ArrayList<SentenceNode> hypoNodes = hypothesis.getAllSentenceNodes();
	
		int matches = 0;
		for (SentenceNode hypoNode : hypoNodes) {
			for (SentenceNode textNode : textNodes) {
				if (match(hypoNode, textNode)) {
					matches++;
					break;
				}
			}
		}
	
		double wordNum = hypoNodes.size();
	
		//if(!PolarityCalculator.polarity(text, hypothesis)) matches = 0;
		
		return matches / wordNum;
	}

	abstract boolean match(SentenceNode hypoNode, SentenceNode textNode);

	@Override
	public String getName() {
		return null;
	}

}