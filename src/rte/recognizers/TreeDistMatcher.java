package rte.recognizers;

import rte.pairs.Sentence;
import rte.pairs.SentenceNode;
import rte.pairs.Text;
import rte.util.IDFCalculator;
import rte.util.TreeDistCalculator;

public class TreeDistMatcher implements EntailmentRecognizer {
	
	IDFCalculator calculator; 
	
	public TreeDistMatcher(IDFCalculator calculator) {
		this.calculator = calculator;
	}


	public double entails(Text text, Text hypothesis) {

		Sentence bestmatchSentenceH=null, bestmatchSentenceT=null;
		
		double minDistance = Double.MAX_VALUE;
		for(Sentence sentence: hypothesis.sentences) {
			SentenceNode node = sentence.getRootNode();
			int hsize = sentence.getAllSentenceNodes().size();
			for(Sentence sentence2: text.sentences) {
				SentenceNode node2 = sentence2.getRootNode();
				//System.out.println("Comparing: " + node + node2);
				TreeDistCalculator calculator = new TreeDistCalculator(node2, node, this.calculator);
				double dist = calculator.calculate();
				if(dist< minDistance) {
					minDistance = dist/hsize;
					bestmatchSentenceH = sentence;
					bestmatchSentenceT = sentence2;
				}
			}
		}
		
		/*
		System.out.println("Best matching Sentences with Distance: " + minDistance);
		System.out.println("Hypothesis: " + bestmatchSentenceH.toString());
		System.out.println("Text: " + bestmatchSentenceT.toString());
		System.out.println();
		*/
		
		//double value = 1.0 / (1.0+minDistance);
		//System.out.println(value);
		minDistance = Math.pow(minDistance, 2.0);
		return minDistance;
	}

	@Override
	public String getName() {
		return TreeDistMatcher.class.getSimpleName();
	}

}