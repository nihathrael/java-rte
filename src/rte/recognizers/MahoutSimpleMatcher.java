package rte.recognizers;

import java.util.ArrayList;

import org.apache.mahout.classifier.sgd.L1;
import org.apache.mahout.classifier.sgd.OnlineLogisticRegression;
import org.apache.mahout.math.RandomAccessSparseVector;
import org.apache.mahout.math.Vector;

import rte.pairs.AdvPair;
import rte.pairs.Text;
import rte.treedistance.cost.TreeEditCost;
import rte.util.LemmaIDFCalculator;
import rte.util.PolarityCalculator;

public class MahoutSimpleMatcher implements EntailmentRecognizer {

	int FEATURES = 7;
	private BleuScoreMatching bleuScoreMatching;
	private IDFLemmaMatching idfLemmaMatcher;
	private OnlineLogisticRegression learningAlgorithm;
	private LemmaMatching lemmaMatcher;

	private LemmaAndPosMatching lemmaAndPosMatcher;
	private SynonymMatching synonymMatching;

	public MahoutSimpleMatcher(TreeEditCost costFunction,
			ArrayList<AdvPair> trainingData, LemmaIDFCalculator idfCalc) {
		bleuScoreMatching = new BleuScoreMatching(2, false);
		idfLemmaMatcher = new IDFLemmaMatching(idfCalc);
		lemmaMatcher = new LemmaMatching();
		lemmaAndPosMatcher = new LemmaAndPosMatching();
		synonymMatching = new SynonymMatching();
		

		learningAlgorithm = new OnlineLogisticRegression(2, FEATURES, new L1());

		for (AdvPair pair : trainingData) {
			Vector v = encodeVector(pair.text, pair.hypothesis);
			int entails = 0;
			if (pair.entailment) {
				entails = 1;
			}
			learningAlgorithm.train(entails, v);
		}

	}

	private Vector encodeVector(Text text, Text hypothesis) {
		Vector v = new RandomAccessSparseVector(FEATURES);
		// Add Tree Dist
		v.set(1, bleuScoreMatching.entails(text, hypothesis));
		v.set(2, idfLemmaMatcher.entails(text, hypothesis));
		v.set(3, lemmaMatcher.entails(text, hypothesis));
		v.set(4, lemmaAndPosMatcher.entails(text, hypothesis));
		v.set(5, synonymMatching.entails(text, hypothesis));
		int pol = 0;
		if(PolarityCalculator.polarity(text, hypothesis)) {
			pol = 1;
		}
		v.set(0, pol);
		
		return v;
	}

	public double entails(Text text, Text hypothesis) {
		Vector v = encodeVector(text, hypothesis);
		return learningAlgorithm.classifyScalar(v);
	}

	public String getName() {
		return MahoutSimpleMatcher.class.getSimpleName();
	}
}