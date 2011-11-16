package rte.recognizers;

import java.util.ArrayList;

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
		return BleuScoreMatching.class.getSimpleName() + " with arithmetic mean, depth=" + bsc.getDepth();
	}	

}
