package rte.recognizers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.apache.mahout.classifier.sgd.L1;
import org.apache.mahout.classifier.sgd.OnlineLogisticRegression;
import org.apache.mahout.math.DenseVector;
import org.apache.mahout.math.RandomAccessSparseVector;
import org.apache.mahout.math.Vector;
import org.apache.mahout.vectorizer.encoders.ContinuousValueEncoder;
import org.apache.mahout.vectorizer.encoders.StaticWordValueEncoder;

import rte.pairs.AdvPair;
import rte.pairs.Sentence;
import rte.pairs.SentenceNode;
import rte.pairs.Text;
import rte.treedistance.TreeDistCalculator;
import rte.treedistance.cost.TreeEditCost;

public class MahoutMatcher {

	TreeEditCost cost;
	private StaticWordValueEncoder lemmasEncoder;
	private ContinuousValueEncoder encoder;

	int FEATURES = 2;

	public MahoutMatcher(TreeEditCost costFunction, ArrayList<AdvPair> trainingData) {
		this.cost = costFunction;
		lemmasEncoder = new StaticWordValueEncoder("lemmas-text");
		encoder = new ContinuousValueEncoder("TreeDist");
		ArrayList<AdvPair> pairs = new ArrayList<AdvPair>(trainingData); 

		OnlineLogisticRegression learningAlgorithm = new OnlineLogisticRegression(
				2, FEATURES, new L1());

		int learningSamples = (int) (pairs.size() * 0.8);

		Random rand = new Random();
		for (int i = 0; i <= learningSamples; ++i) {
			int choice = rand.nextInt(pairs.size());
			AdvPair pair = pairs.remove(choice);

			Vector v = encodeVector(pair);
			int entails = 0;
			if (pair.entailment) {
				entails = 1;
			}
			learningAlgorithm.train(entails, v);
		}

		for (AdvPair pair : pairs) {
			Vector v = encodeVector(pair);
			Vector p = new DenseVector(FEATURES);
			learningAlgorithm.classifyFull(p, v);
			int estimated = p.maxValueIndex();
			System.out.println(p.get(estimated));
		}

	}

	private Vector encodeVector(AdvPair pair) {
		Vector v = new RandomAccessSparseVector(FEATURES);
		// Add Tree Dist
		encoder.addToVector(
				String.valueOf(getTreeCosts(pair.text, pair.hypothesis)), v);
		// for(SentenceNode node: pair.text.getAllSentenceNodes()) {
		// lemmasEncoder.addToVector(node.lemma, v);
		// }
		// for(SentenceNode node: pair.hypothesis.getAllSentenceNodes()) {
		// lemmasEncoder2.addToVector(node.lemma, v);
		// }
		return v;
	}

	public double getTreeCosts(Text text, Text hypothesis) {

		Sentence bestmatchSentenceH = null, bestmatchSentenceT = null;

		double minDistance = Double.MAX_VALUE;
		for (Sentence sentence : hypothesis.sentences) {
			SentenceNode hypoNode = sentence.getRootNode();
			int hsize = sentence.getAllSentenceNodes().size();
			for (Sentence sentence2 : text.sentences) {
				SentenceNode textNode = sentence2.getRootNode();
				// System.out.println("Comparing: " + node + node2);
				TreeDistCalculator calculator = new TreeDistCalculator(
						textNode, hypoNode, cost);
				double dist = calculator.calculate();
				if (dist < minDistance) {
					minDistance = dist / hsize;
					bestmatchSentenceH = sentence;
					bestmatchSentenceT = sentence2;
				}
			}
		}

		double value = 1.0 / (1.0 + minDistance);
		// System.out.println(value);
		// minDistance = Math.pow(minDistance, 2.0);
		return 1 - minDistance * 10;
	}
}