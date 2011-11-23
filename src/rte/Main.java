package rte;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import rte.pairs.THPair;
import rte.recognizers.BasicMahoutMatcher;
import rte.recognizers.BleuScoreMatching;
import rte.recognizers.IEntailmentRecognizer;
import rte.recognizers.IDFLemmaMatching;
import rte.recognizers.IDFLexicalMatching;
import rte.recognizers.IMachineLearnerRecognizer;
import rte.recognizers.LemmaAndPosMatching;
import rte.recognizers.LemmaMatching;
import rte.recognizers.LexicalMatching;
import rte.recognizers.LinSimilarityMatching;
import rte.recognizers.MahoutMatcher;
import rte.recognizers.SynonymMatching;
import rte.recognizers.TreeDistMatcher;
import rte.recognizers.WordNetDistanceMatching;
import rte.treedistance.cost.FreeDeletion;
import rte.treedistance.cost.TreeEditCost;
import rte.treedistance.cost.WeightedIDF;
import rte.treedistance.cost.WeightedLemmaIDF;
import rte.util.LemmaIDFCalculator;
import rte.util.WordIDFCalculator;
import rte.util.WordNetAccessor;

public class Main {

	WordIDFCalculator wordIdfs = null;
	LemmaIDFCalculator lemmaIdfs = null;

	HashMap<Integer, Boolean> scoreMapping = new HashMap<Integer, Boolean>();

	public Main() {

		// Load Wordnet!
		WordNetAccessor.getInstance();

		System.out.println("Reading Pairs...");
		ArrayList<THPair> pairs = readPairs("data/RTE2_dev.preprocessed.xml");
		System.out.println("Done!");

		System.out.println("Calculating IDFs...");
		wordIdfs = new WordIDFCalculator(pairs);
		lemmaIdfs = new LemmaIDFCalculator(pairs);
		System.out.println("Done!");

		System.out.println("\n****************************");
		System.out.println("Part 1&2 - Basic Recognizers");
		System.out.println("****************************");

		ArrayList<IEntailmentRecognizer> recognizers = new ArrayList<IEntailmentRecognizer>();

		// Basic stuff
		recognizers.add(new LexicalMatching());
		recognizers.add(new LemmaMatching());
		recognizers.add(new LemmaAndPosMatching());
		recognizers.add(new IDFLexicalMatching(wordIdfs));
		recognizers.add(new IDFLemmaMatching(lemmaIdfs));

		// Bleu Score
		for (int i = 2; i < 5; i++) {
			recognizers.add(new BleuScoreMatching(i, true));
		}
		for (int i = 2; i < 5; i++) {
			recognizers.add(new BleuScoreMatching(i, false));
		}

		// TreeDistance
		TreeEditCost costFunction3 = new WeightedLemmaIDF(lemmaIdfs);
		recognizers.add(new TreeDistMatcher(costFunction3));

		TreeEditCost costFunction2 = new WeightedIDF(wordIdfs);
		recognizers.add(new TreeDistMatcher(costFunction2));

		TreeEditCost costFunction1 = new FreeDeletion();
		recognizers.add(new TreeDistMatcher(costFunction1));

		// WordNet Magic
		recognizers.add(new WordNetDistanceMatching());
		recognizers.add(new LinSimilarityMatching());
		recognizers.add(new SynonymMatching());

		// Run them all!
		for (IEntailmentRecognizer recog : recognizers) {
			findBestThreshold(pairs, recog);
		}

		//
		// Part 3
		//
		System.out.println("\n**************************************************");
		System.out.println("Part 3 - Machine Learning 10-Fold Cross-Validation");
		System.out.println("**************************************************");
		
		IMachineLearnerRecognizer matcher = new BasicMahoutMatcher(costFunction3, lemmaIdfs);
		crossValidate(pairs, matcher);
		
		matcher = new MahoutMatcher(costFunction3, lemmaIdfs);
		crossValidate(pairs, matcher);

		//
		// Part 4
		//
		// Use MahoutMatcher with previously analyzed best threshold
		// Best threshold: 0.5225

		System.out.println("\n**********************************");
		System.out.println("Part 4 - Running on Blind TestData");
		System.out.println("**********************************");

		System.out.println("Reading TestData Pairs...");
		ArrayList<THPair> testPairs = readPairs("data/preprocessed-blind-test-data.xml");
		lemmaIdfs.addPairs(testPairs);
		System.out.println("Done!");
		
		double mahoutBestThreshold = 0.5225;

		System.out.println("Learning Data...");
		IMachineLearnerRecognizer mlearning = new MahoutMatcher(costFunction3, lemmaIdfs);
		mlearning.init(pairs);
		System.out.println("Analyze Test Pairs...");
		ArrayList<Score> scores = analyzePairs(testPairs, mlearning);
		System.out.println("Write results....");
		writeScores(scores, "results/" + "test_funke_kinnen.txt", mahoutBestThreshold);
		
		System.out.println("Analyze Dev Pairs...");
		mlearning.init(pairs); // RESET
		scores = analyzePairs(pairs, mlearning);
		System.out.println("Write results....");
		writeScores(scores, "results/" + "dev_funke_kinnen.txt", mahoutBestThreshold);
		System.out.println("DevData gave result: " + evaluateScores(scores, mahoutBestThreshold));
		
		System.out.println("Done!");

	}

	
	/**
	 * Runs a ten-fold cross-validation using the mahout matcher
	 */
	public void crossValidate(ArrayList<THPair> pairs, IMachineLearnerRecognizer mlearner) {
		/*
		 * 10-fold Cross-Validation (CV) procedure: 1. divide data in 10
		 * stratifed folds of approximately equal size 2. for i = 1 to 10 do: I
		 * use foldi as test data I use other 9 folds as training data I
		 * calculate accuracy score on foldi 3. Finally average 10 scores to get
		 * overall accuracy score
		 */

		System.out.println("Cross-Validation with 10 samples started");

		ArrayList<ArrayList<THPair>> folds = new ArrayList<ArrayList<THPair>>();

		for (int i = 0; i < 10; i++) {
			folds.add(new ArrayList<THPair>());
		}

		for (int i = 0; i < pairs.size(); i++) {
			folds.get(i % 10).add(pairs.get(i));
		}

		ArrayList<THPair> learningData, testData;
		learningData = new ArrayList<THPair>();
		testData = new ArrayList<THPair>();

		double avgScore = 0.0;

		for (int i = 0; i < 10; i++) {

			testData.addAll(folds.get(i));
			for (int j = 0; j < 10; j++) {
				if (i != j)
					learningData.addAll(folds.get(j));
			}

			TreeEditCost costFunction3 = new WeightedLemmaIDF(lemmaIdfs);
			mlearner.init(learningData);
			double best = findBestThreshold(testData, mlearner);

			avgScore += best;

			learningData.clear();
			testData.clear();
		}

		avgScore /= 10;

		System.out
				.println("Cross-Validation with 10 folds finished. Average Score is: " + avgScore);

	}

	/**
	 * Find the best threshold to use for the given recognizer.
	 * 
	 * @return The best achieved score
	 */
	private double findBestThreshold(ArrayList<THPair> pairs, IEntailmentRecognizer recognizer) {
		System.out.println("Find best threshold for: \"" + recognizer.getName() + "\" ... ");
		long t = System.currentTimeMillis();
		double bestScore = 0.0;
		double bestThres = 0.05;

		ArrayList<Score> scores = analyzePairs(pairs, recognizer);

		double tmpScore = 0.0;
		for (double i = 0.05; i < 1.0; i += 0.025) {
			tmpScore = evaluateScores(scores, i);
			if (tmpScore > bestScore) {
				bestScore = tmpScore;
				bestThres = i;
			}
		}
		writeScores(scores, "results/" + recognizer.getName().toLowerCase(), bestThres);
		System.out.printf("Score: %.5f | Threshold: %.3f | Time: %5dms\n\n", bestScore, bestThres,
				(System.currentTimeMillis() - t));

		return bestScore;
	}

	/**
	 * Evaluates the given scores for the given threshold.
	 */
	private double evaluateScores(ArrayList<Score> scores, double threshold) {
		int correct = 0;
		for (Score score : scores) {
			if (score.entailment > threshold && scoreMapping.get(score.id)) {
				correct++;
			}
			if (score.entailment <= threshold && !scoreMapping.get(score.id)) {
				correct++;
			}
		}
		double tmpScore = ((double) correct) / scores.size();
		return tmpScore;
	}

	/**
	 * Write the scores to the outputFile. Uses the given threshold to decide if
	 * score is ranked yes or no
	 */
	private String writeScores(ArrayList<Score> scores, String outputFile, double threshold) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
			writer.write("ranked: no");
			writer.newLine();
			for (Score score : scores) {
				writer.write(score.getOutputString(threshold));
				writer.newLine();
			}

			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return outputFile;
	}

	/**
	 * Analyze the pairs using the given {@link IEntailmentRecognizer}.
	 * 
	 * @return Returns a List of scores resulting from the analysis
	 */
	private ArrayList<Score> analyzePairs(ArrayList<THPair> pairs, IEntailmentRecognizer recognizer) {

		ArrayList<Score> scores = new ArrayList<Score>();
		for (THPair pair : pairs) {

			double entailment = recognizer.entails(pair.text, pair.hypothesis);

			Score tmpScore = new Score(pair.id, entailment);
			scores.add(tmpScore);
		}
		return scores;
	}

	/**
	 * Reads in T-H {@link THPair} from the given file name. They are sorted by
	 * id in ascending order.
	 * 
	 * @return List of resulting {@link THPair}s
	 */
	private ArrayList<THPair> readPairs(String file) {
		ArrayList<THPair> pairs = new ArrayList<THPair>();

		try {

			File fXmlFile = new File(file);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();

			NodeList nList = doc.getElementsByTagName("pair");

			for (int i = 0; i < nList.getLength(); i++) {

				Node nNode = nList.item(i);

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					THPair pair = THPair.fromXML(eElement);
					scoreMapping.put(pair.id, pair.entailment);
					pairs.add(pair);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		Collections.sort(pairs);
		return pairs;
	}

	/**
	 * Utility method to get the value of a certain tag
	 */
	public static String getTagValue(String sTag, Element eElement) {
		Node tmp = eElement.getElementsByTagName(sTag).item(0);
		if (tmp == null) {
			return null;
		}
		NodeList nlList = tmp.getChildNodes();
		Node nValue = (Node) nlList.item(0);

		return nValue.getNodeValue().trim();
	}

	public static void main(String[] args) {
		System.out.println("Java RTE - Welcome!");
		new Main();
	}
}
