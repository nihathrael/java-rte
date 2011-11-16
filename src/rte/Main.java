package rte;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

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
import rte.recognizers.MahoutMatcher;
import rte.recognizers.TreeDistMatcher;
import rte.treedistance.cost.FreeDeletion;
import rte.treedistance.cost.TreeEditCost;
import rte.treedistance.cost.WeightedIDF;
import rte.treedistance.cost.WeightedLemmaIDF;
import rte.util.LemmaIDFCalculator;
import rte.util.WordIDFCalculator;

public class Main {
	
	WordIDFCalculator wordIdfs = null;
	LemmaIDFCalculator lemmaIdfs = null;
	
	HashMap<Integer, Boolean> scoreMapping = new HashMap<Integer, Boolean>();

	public Main() {
		
		System.out.println("Reading Pairs...");
		ArrayList<AdvPair> pairs = readAdvancedPairs();
		Collections.sort(pairs);
		System.out.println("Done!");
		
		System.out.println("Calculating IDFs...");
		wordIdfs = new WordIDFCalculator(pairs);
		lemmaIdfs = new LemmaIDFCalculator(pairs);
		System.out.println("Done!");
		
		int learningSamples = (int) (pairs.size() * 0.8);
		ArrayList<AdvPair> learningData = new ArrayList<AdvPair>();
		ArrayList<AdvPair> testData = new ArrayList<AdvPair>(pairs); 

		Random rand = new Random();
		for (int i = 0; i <= learningSamples; ++i) {
			int choice = rand.nextInt(testData.size());
			AdvPair pair = testData.remove(choice);
			learningData.add(pair);
		}
		
		System.out.println(learningData.size());
		System.out.println(testData.size());

		
		TreeEditCost costFunction3 = new WeightedLemmaIDF(lemmaIdfs);
		MahoutMatcher mlearing = new MahoutMatcher(costFunction3, learningData, lemmaIdfs);
		findBestThreshold(pairs, mlearing);
		
		
		EntailmentRecognizer rec9 = new TreeDistMatcher(costFunction3);
		findBestThreshold(pairs, rec9);
		
		TreeEditCost costFunction2 = new WeightedIDF(wordIdfs);
		EntailmentRecognizer rec8 = new TreeDistMatcher(costFunction2);
		findBestThreshold(pairs, rec8);
		
		TreeEditCost costFunction1 = new FreeDeletion();
		EntailmentRecognizer rec7 = new TreeDistMatcher(costFunction1);
		findBestThreshold(pairs, rec7);
		
		EntailmentRecognizer rec1 = new LexicalMatching();
		findBestThreshold(pairs, rec1);
		
		EntailmentRecognizer rec2 = new LemmaMatching();
		findBestThreshold(pairs, rec2);
		
		EntailmentRecognizer rec3 = new LemmaAndPosMatching();
		findBestThreshold(pairs, rec3);
		
		EntailmentRecognizer rec4 = new IDFLexicalMatching(wordIdfs);
		findBestThreshold(pairs, rec4);
		
		EntailmentRecognizer rec5 = new IDFLemmaMatching(lemmaIdfs);
		findBestThreshold(pairs, rec5);
		
		
		for(int i=2; i<5; i++) {
			EntailmentRecognizer rec6 = new BleuScoreMatching(i, true);
			findBestThreshold(pairs, rec6);
		}
		for(int i=2; i<5; i++) {
			EntailmentRecognizer rec6 = new BleuScoreMatching(i, false);
			findBestThreshold(pairs, rec6);
		}
		
		
		
	}




	private void findBestThreshold(ArrayList<AdvPair> pairs, EntailmentRecognizer recognizer) {
		System.out.println("Searching for best threshold using " + recognizer.getName() + "...");
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
			}
			System.out.println(i);
			tmpScore = ((double)correct)/scores.size();
			if(tmpScore > bestScore) {
				bestScore = tmpScore;
				bestThres = i;
			}
		}
		System.out.println("Done! Found best results for " + bestThres + " with score: " + bestScore);
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
