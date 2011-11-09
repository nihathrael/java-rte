package rte.recognizers;

import rte.pairs.Text;

public class LexicalMatching implements EntailmentRecognizer {
	
	public boolean entails(Text text, Text hypothesis, double threshold) {
		/*
		String[] words = pair.h.split(" ");
		int matches = 0;
		for (String word : words) {
			if (pair.t.contains(word)) {
				matches++;
			}
		}
		double wordNum = words.length;
		boolean entailment;
		if (matches / wordNum > threshold) {
			entailment = true;
		} else {
			entailment = false;
		}
		*/
		return false;
		
	}
	
	public String getName() {
		return LexicalMatching.class.getSimpleName();
	}

}
