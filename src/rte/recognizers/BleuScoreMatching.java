package rte.recognizers;

import rte.pairs.Text;
import rte.util.BleuScoreCalculator;

public class BleuScoreMatching implements EntailmentRecognizer {

	BleuScoreCalculator bsc;
	
	
	public BleuScoreMatching(int maxN_gramLength, boolean useArithmeticMean) {
		bsc = new BleuScoreCalculator(maxN_gramLength, useArithmeticMean);
	}
	
	public double entails(Text text, Text hypothesis) {
		return bsc.bleuScore(text, hypothesis);
	}
	
	public String getName() {
		if(bsc.mean) {
			return BleuScoreMatching.class.getSimpleName() + " with arithmetic mean, depth=" + bsc.getDepth();
		} else {
			return BleuScoreMatching.class.getSimpleName() + " without arithmetic mean, depth=" + bsc.getDepth();
		}
	}	

}
