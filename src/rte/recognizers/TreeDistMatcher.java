package rte.recognizers;

import rte.pairs.Sentence;
import rte.pairs.SentenceNode;
import rte.pairs.Text;
import rte.treedistance.TreeDistCalculator;
import rte.treedistance.cost.TreeEditCost;

public class TreeDistMatcher implements EntailmentRecognizer {
	
	TreeEditCost cost; 
	
	public TreeDistMatcher(TreeEditCost costFunction) {
		this.cost = costFunction;
	}


	public double entails(Text text, Text hypothesis) {

		Sentence bestmatchSentenceH=null, bestmatchSentenceT=null;
		
		double minDistance = Double.MAX_VALUE;
		for(Sentence sentence: hypothesis.sentences) {
			SentenceNode hypoNode = sentence.getRootNode();
			int hsize = sentence.getAllSentenceNodes().size();
			for(Sentence sentence2: text.sentences) {
				SentenceNode textNode = sentence2.getRootNode();
				//System.out.println("Comparing: " + node + node2);
				TreeDistCalculator calculator = new TreeDistCalculator(textNode, hypoNode, cost);
				double dist = calculator.calculate();
				if(dist< minDistance) {
					minDistance = dist / hsize;
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
		
		double value = 1.0 / (1.0+minDistance);
		//System.out.println(value);
		//minDistance = Math.pow(minDistance, 2.0);
		return 1-minDistance*10;
	}

	@Override
	public String getName() {
		return TreeDistMatcher.class.getSimpleName() + 
				" with cost function: " + cost.getClass().getSimpleName();
	}

}