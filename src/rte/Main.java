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

import rte.pairs.AdvPair;
import rte.recognizers.BleuScoreMatching;
import rte.recognizers.EntailmentRecognizer;
import rte.recognizers.IDFLemmaMatching;
import rte.recognizers.IDFLexicalMatching;
import rte.recognizers.LemmaAndPosMatching;
import rte.recognizers.LemmaMatching;
import rte.recognizers.LexicalMatching;
import rte.recognizers.LinSimilarityMatching;
import rte.recognizers.MahoutMatcher;
import rte.recognizers.MahoutSimpleMatcher;
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
		ArrayList<AdvPair> pairs = readAdvancedPairs();
		Collections.sort(pairs);
		System.out.println("Done!");
		
		System.out.println("Calculating IDFs...");
		wordIdfs = new WordIDFCalculator(pairs);
		lemmaIdfs = new LemmaIDFCalculator(pairs);
		System.out.println("Done!");
		
		crossValidate(pairs);

		ArrayList<EntailmentRecognizer> recognizers = new ArrayList<EntailmentRecognizer>();

		// Basic stuff
		recognizers.add(new LexicalMatching());
		recognizers.add(new LemmaMatching());
		recognizers.add(new LemmaAndPosMatching());
		recognizers.add(new IDFLexicalMatching(wordIdfs));
		recognizers.add(new IDFLemmaMatching(lemmaIdfs));
		
		// TreeDistance
		TreeEditCost costFunction3 = new WeightedLemmaIDF(lemmaIdfs);
		recognizers.add(new TreeDistMatcher(costFunction3));
		
		TreeEditCost costFunction2 = new WeightedIDF(wordIdfs);
		recognizers.add(new TreeDistMatcher(costFunction2));
		
		TreeEditCost costFunction1 = new FreeDeletion();
		recognizers.add(new TreeDistMatcher(costFunction1));
		
		// Bleu Score
		for(int i=2; i<5; i++) {
			recognizers.add(new BleuScoreMatching(i, true));
		}
		for(int i=2; i<5; i++) {
			recognizers.add(new BleuScoreMatching(i, false));
		}
		
		// WordNet Magic
		recognizers.add(new WordNetDistanceMatching());
		recognizers.add(new LinSimilarityMatching());
		recognizers.add(new SynonymMatching());
		
		
		// Run them all!
		for(EntailmentRecognizer recog: recognizers) {
			findBestThreshold(pairs, recog);
		}
		
		
	}

	public void crossValidate(ArrayList<AdvPair> pairs) {
		/*
		10-fold Cross-Validation (CV) procedure:
			1. divide data in 10 stratifed folds of approximately equal size
			2. for i = 1 to 10 do:
			I use foldi as test data
			I use other 9 folds as training data
			I calculate accuracy score on foldi
			3. Finally average 10 scores to get overall accuracy score
		*/
		
		
		System.out.println("Cross-Validation with 10 samples started");
		
		ArrayList<ArrayList<AdvPair>> folds = new ArrayList<ArrayList<AdvPair>>();

		
		for(int i=0; i<10; i++) {
			folds.add(new ArrayList<AdvPair>());
		}
		
		for(int i=0; i<pairs.size(); i++) {
			folds.get(i%10).add(pairs.get(i));
		}
		
		ArrayList<AdvPair> learningData, testData;
		learningData = new ArrayList<AdvPair>();
		testData = new ArrayList<AdvPair>();
		
		double avgScore=0.0;
		
		for(int i=0; i<10; i++) {
			
			testData.addAll(folds.get(i));
			for(int j=0; j<10; j++) {
				if(i!=j)
					learningData.addAll(folds.get(j));
			}
			
			TreeEditCost costFunction3 = new WeightedLemmaIDF(lemmaIdfs);
			EntailmentRecognizer mlearing = new MahoutMatcher(costFunction3, learningData, lemmaIdfs);
			double best = findBestThreshold(testData, mlearing);
			
			avgScore += best;
			
			learningData.clear();
			testData.clear();
		}
		
		avgScore /= 10;
		
		System.out.println("Cross-Validation with 10 folds finished. Average Score is: " + avgScore);

	}


	private double findBestThreshold(ArrayList<AdvPair> pairs, EntailmentRecognizer recognizer) {
		System.out.println("Searching for best threshold using " + recognizer.getName() + "...");
		long t = System.currentTimeMillis();
		double bestScore = 0.0;
		double bestThres = 0.05;
		
		ArrayList<Score> scores = analyzePairs(pairs, recognizer);
		
		int correct = 0;
		double tmpScore = 0.0; 
		for (double i = 0.05; i < 1.0; i += 0.025) {
			correct = 0;
			for(Score score: scores) {
				if(score.entailment > i && scoreMapping.get(score.id)) {
					correct++;
				}
				if(score.entailment <= i && !scoreMapping.get(score.id)) {
					correct++;
				}
			}
			tmpScore = ((double)correct)/scores.size();
			if(tmpScore > bestScore) {
				bestScore = tmpScore;
				bestThres = i;
			}
		}
		writeScores(scores, "results/" + recognizer.getName().toLowerCase(), bestThres);
		System.out.println("Done ("+(System.currentTimeMillis() - t)+"ms)! Found best results for " + bestThres + " with score: " + bestScore);
		
		return bestScore;
	}
	
	
	

	private String writeScores(ArrayList<Score> scores, String outputFile, double threshold) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(
					outputFile));
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

	private ArrayList<Score> analyzePairs(ArrayList<AdvPair> pairs, 
			EntailmentRecognizer recognizer) {
		
		ArrayList<Score> scores = new ArrayList<Score>();
		for (AdvPair pair : pairs) {
			
			double entailment = recognizer.entails(pair.text, pair.hypothesis);
			
			Score tmpScore = new Score(pair.id, entailment);
			scores.add(tmpScore);
		}
		return scores;
	}
	
	private ArrayList<AdvPair> readAdvancedPairs() {
		ArrayList<AdvPair> pairs = new ArrayList<AdvPair>();

		try {

			File fXmlFile = new File("data/RTE2_dev.preprocessed.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();

			NodeList nList = doc.getElementsByTagName("pair");

			for (int i = 0; i < nList.getLength(); i++) {

				Node nNode = nList.item(i);

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					AdvPair pair = AdvPair.fromXML(eElement);
					scoreMapping.put(pair.id, pair.entailment);
					pairs.add(pair);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pairs;
	}

	public static String getTagValue(String sTag, Element eElement) {
		Node tmp = eElement.getElementsByTagName(sTag).item(0);
		if(tmp == null) {
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
