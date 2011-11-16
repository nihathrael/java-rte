package rte.recognizers;

import java.util.ArrayList;

import org.apache.mahout.classifier.sgd.L1;
import org.apache.mahout.classifier.sgd.OnlineLogisticRegression;
import org.apache.mahout.math.DenseVector;
import org.apache.mahout.math.RandomAccessSparseVector;
import org.apache.mahout.math.Vector;
import org.apache.mahout.vectorizer.encoders.ContinuousValueEncoder;
import org.apache.mahout.vectorizer.encoders.StaticWordValueEncoder;
import org.apache.mahout.vectorizer.encoders.WordValueEncoder;

import rte.pairs.AdvPair;
import rte.pairs.Text;
import rte.treedistance.cost.TreeEditCost;
import rte.util.LemmaIDFCalculator;
import rte.util.PolarityCalculator;

public class MahoutMatcher implements EntailmentRecognizer {

	private ContinuousValueEncoder encoder;
	private ContinuousValueEncoder encoder2;
	private ContinuousValueEncoder encoder3;

	int FEATURES = 10;
	private TreeDistMatcher treeDistMatcher;
	private BleuScoreMatching bleuScoreMatching;
	private IDFLemmaMatching idfLemmaMatcher;
	private OnlineLogisticRegression learningAlgorithm;
	private WordValueEncoder wve;

	public MahoutMatcher(TreeEditCost costFunction, ArrayList<AdvPair> trainingData, LemmaIDFCalculator idfCalc) {
		treeDistMatcher = new TreeDistMatcher(costFunction);
		bleuScoreMatching = new BleuScoreMatching(2, false);
		idfLemmaMatcher = new IDFLemmaMatching(idfCalc);
		wve = new StaticWordValueEncoder("polarity");
		encoder = new ContinuousValueEncoder("TreeDist");
		encoder2 = new ContinuousValueEncoder("BleuScore");
		encoder3 = new ContinuousValueEncoder("IDFLemma");
		

		learningAlgorithm = new OnlineLogisticRegression(
				2, FEATURES, new L1());

		for (AdvPair pair: trainingData) {
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
		encoder.addToVector(
				String.valueOf(treeDistMatcher.entails(text, hypothesis)), v);
		encoder2.addToVector(String.valueOf(bleuScoreMatching.entails(text, hypothesis)), v);
		encoder3.addToVector(String.valueOf(idfLemmaMatcher.entails(text, hypothesis)), v);
		wve.addToVector(String.valueOf(PolarityCalculator.polarity(text, hypothesis)), v);
		return v;
	}

	public double entails(Text text, Text hypothesis) {
		Vector v = encodeVector(text, hypothesis);
		Vector p = new DenseVector(FEATURES);
		learningAlgorithm.classifyFull(p, v);
		int estimated = p.maxValueIndex();
		return p.get(estimated);
	}

	public String getName() {
		return MahoutMatcher.class.getSimpleName();
	}
}