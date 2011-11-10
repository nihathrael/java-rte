package rte.recognizers;

import rte.pairs.Sentence;
import rte.pairs.SentenceNode;
import rte.pairs.Text;
import rte.util.TreeDistCalculator;

public class TreeDistMatcher implements EntailmentRecognizer {


	public boolean entails(Text text, Text hypothesis, double threshold) {

		Sentence bestmatchSentenceT = null, bestmatchSentenceH = null;
		
		int minDistance = Integer.MAX_VALUE;
		for(Sentence sentence: hypothesis.sentences) {
			SentenceNode node = sentence.getRootNode();
			int hsize = sentence.getAllSentenceNodes().size();
			for(Sentence sentence2: text.sentences) {
				SentenceNode node2 = sentence2.getRootNode();
				//System.out.println("Comparing: " + node + node2);
				TreeDistCalculator calculator = new TreeDistCalculator(node2, node);
				int dist = calculator.calculate();
				if(dist == 0) {
					System.err.println(dist);
				}
				if(dist< minDistance) {
					minDistance = dist;
					bestmatchSentenceT = sentence2;
					bestmatchSentenceH = sentence;
				}
			}
		}
		
		/*
		System.out.println("Best matching Sentences with Distance: " + minDistance);
		System.out.println("Hypothesis: " + bestmatchSentenceH.toString());
		System.out.println("Text: " + bestmatchSentenceT.toString());
		System.out.println();
		*/
		
		double value = 1.0 / (1.0+minDistance);
		System.out.println(value);
		return value > threshold;
	}

	@Override
	public String getName() {
		return TreeDistMatcher.class.getSimpleName();
	}

}