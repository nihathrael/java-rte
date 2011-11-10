package rte.recognizers;

import rte.pairs.Sentence;
import rte.pairs.SentenceNode;
import rte.pairs.Text;
import rte.util.TreeDistCalculator;

public class TreeDistMatcher implements EntailmentRecognizer {


	public boolean entails(Text text, Text hypothesis, double threshold) {

		int minDistance = Integer.MAX_VALUE;
		for(Sentence sentence: hypothesis.sentences) {
			SentenceNode node = sentence.getRootNode();
			for(Sentence sentence2: text.sentences) {
				SentenceNode node2 = sentence2.getRootNode();
				//System.out.println("Comparing: " + node + node2);
				TreeDistCalculator calculator = new TreeDistCalculator(node, node2);
				int dist = calculator.calculate();
				if(dist< minDistance) {
					minDistance = dist;
				}
			}
		}
		
		double value = 10/(minDistance+1.0);
		//System.out.println(value);
		
		return value > threshold;
	}

	@Override
	public String getName() {
		return TreeDistMatcher.class.getSimpleName();
	}

}