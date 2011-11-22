package rte.recognizers;

import java.util.ArrayList;

import org.apache.mahout.classifier.sgd.L1;
import org.apache.mahout.classifier.sgd.OnlineLogisticRegression;
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

	private ContinuousValueEncoder encoder1;
	private ContinuousValueEncoder encoder2;
	private ContinuousValueEncoder encoder3;
	private ContinuousValueEncoder encoder4;
	private ContinuousValueEncoder encoder5;
	private ContinuousValueEncoder encoder7;

	int FEATURES = 7;
	private TreeDistMatcher treeDistMatcher;
	private BleuScoreMatching bleuScoreMatching;
	private IDFLemmaMatching idfLemmaMatcher;
	private OnlineLogisticRegression learningAlgorithm;
	private WordValueEncoder wve;
	private LemmaMatching lemmaMatcher;

	private LemmaAndPosMatching lemmaAndPosMatcher;
	private LinSimilarityMatching linSimMatching;
	private SynonymMatching synonymMatching;
	private ContinuousValueEncoder encoder8;

	public MahoutMatcher(TreeEditCost costFunction,
			ArrayList<AdvPair> trainingData, LemmaIDFCalculator idfCalc) {
		treeDistMatcher = new TreeDistMatcher(costFunction);
		bleuScoreMatching = new BleuScoreMatching(2, false);
		idfLemmaMatcher = new IDFLemmaMatching(idfCalc);
		lemmaMatcher = new LemmaMatching();
		lemmaAndPosMatcher = new LemmaAndPosMatching();
		linSimMatching = new LinSimilarityMatching();
		synonymMatching = new SynonymMatching();
		wve = new StaticWordValueEncoder("polarity");
		encoder1 = new ContinuousValueEncoder("TreeDist");
		encoder2 = new ContinuousValueEncoder("BleuScore");
		encoder3 = new ContinuousValueEncoder("IDFLemma");
		encoder4 = new ContinuousValueEncoder("Lemma");
		encoder5 = new ContinuousValueEncoder("LemmaAndPos");
		encoder7 = new ContinuousValueEncoder("LinSimilarity");
		encoder8 = new ContinuousValueEncoder("Synonyms");
		

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
		//encoder1.addToVector(String.valueOf(treeDistMatcher.entails(text, hypothesis)), v);
		encoder2.addToVector(String.valueOf(bleuScoreMatching.entails(text, hypothesis)), v);
		encoder3.addToVector(String.valueOf(idfLemmaMatcher.entails(text, hypothesis)), v);
		encoder4.addToVector(String.valueOf(lemmaMatcher.entails(text, hypothesis)), v);
		encoder5.addToVector(String.valueOf(lemmaAndPosMatcher.entails(text, hypothesis)), v);
		//encoder7.addToVector(String.valueOf(linSimMatching.entails(text, hypothesis)), v);
		wve.addToVector(String.valueOf(PolarityCalculator.polarity(text, hypothesis)), v);
		encoder8.addToVector(String.valueOf(synonymMatching.entails(text, hypothesis)), v);
		
		
		return v;
	}

	public double entails(Text text, Text hypothesis) {
		Vector v = encodeVector(text, hypothesis);
		return learningAlgorithm.classifyScalar(v);
	}

	public String getName() {
		return MahoutMatcher.class.getSimpleName();
	}
}