package rte.recognizers;

import java.util.ArrayList;

import rte.pairs.SentenceNode;
import rte.pairs.Text;

public class LexicalMatching implements EntailmentRecognizer {
	
	public boolean entails(Text text, Text hypothesis, double threshold) {
		
		ArrayList<SentenceNode> textNodes = text.getAllSentenceNodes();
		ArrayList<SentenceNode> hypoNodes = hypothesis.getAllSentenceNodes();
		
		int matches = 0;
		for (SentenceNode word : hypoNodes) {
			if(word.word == null) continue;
			for(SentenceNode textpart : textNodes) {
				if(textpart.word == null) continue;
				if(word.word.equals(textpart.word)) {
					matches++;
					break;
				}
			}	
		}
		return (double)matches / (double)hypoNodes.size() > threshold;
	}
	
	public String getName() {
		return LexicalMatching.class.getSimpleName();
	}

}
