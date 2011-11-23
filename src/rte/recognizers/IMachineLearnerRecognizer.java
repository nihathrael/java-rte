package rte.recognizers;

import java.util.ArrayList;

import rte.pairs.THPair;

public interface IMachineLearnerRecognizer extends EntailmentRecognizer {

	/**
	 * Initialize the machine learner with the set of training data. Calling this
	 * multiple times is expected to RESET the learning algorithm used.
	 */
	public void init(ArrayList<THPair> trainingData);

}
