package rte.recognizers;

import rte.pairs.Text;
import rte.util.BleuScoreCalculator;

public class BleuScoreMatching implements IEntailmentRecognizer {

	BleuScoreCalculator bsc;
	
	
	public BleuScoreMatching(int maxN_gramLength, boolean useArithmeticMean) {
		bsc = new BleuScoreCalculator(maxN_gramLength, useArithmeticMean);
	}
	
	public double entails(Text text, Text hypothesis) {
		return bsc.bleuScore(text, hypothesis);
	}
	
	public String getName() {
		if(bsc.mean) {
			return BleuScoreMatching.class.getSimpleName() + " depth " + bsc.getDepth() + " + arithmetic mean";
		} else {
			return BleuScoreMatching.class.getSimpleName() + " depth " + bsc.getDepth();
		}
	}	

}
