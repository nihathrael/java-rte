package rte;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import rte.pairs.AdvPair;
import rte.pairs.SentenceNode;
import rte.recognizers.BleuScore;
import rte.recognizers.EntailmentRecognizer;
import rte.recognizers.IDFLemmaMatching;
import rte.recognizers.IDFLexicalMatching;
import rte.recognizers.LemmaAndPosMatching;
import rte.recognizers.LemmaMatching;
import rte.recognizers.LexicalMatching;
import rte.recognizers.TreeDistMatcher;
import rte.util.LemmaIDFCalculator;
import rte.util.TreeDistCalculator;
import rte.util.WordIDFCalculator;

public class Main {
	
	WordIDFCalculator wordIdfs = null;
	LemmaIDFCalculator lemmaIdfs = null;

	public Main() {
		
		System.out.println("Reading Pairs...");
		ArrayList<AdvPair> pairs = readAdvancedPairs();
		Collections.sort(pairs);
		System.out.println("Done!");
		
		System.out.println("Calculating IDFs...");
		wordIdfs = new WordIDFCalculator(pairs);
		lemmaIdfs = new LemmaIDFCalculator(pairs);
		System.out.println("Done!");
		

		EntailmentRecognizer rec7 = new TreeDistMatcher(wordIdfs);
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
			EntailmentRecognizer rec6 = new BleuScore(i, true);
			findBestThreshold(pairs, rec6);
		}
		for(int i=2; i<5; i++) {
			EntailmentRecognizer rec6 = new BleuScore(i, false);
			findBestThreshold(pairs, rec6);
		}
		
		
		
	}




	private void findBestThreshold(ArrayList<AdvPair> pairs, EntailmentRecognizer recognizer) {
		System.out.println("Searching for best threshold using " + recognizer.getName() + "...");
		double bestScore = 0.0;
		double bestThres = 0.05;
		
		ArrayList<Score> scores = analyzePairs(pairs, recognizer);
		
		for (double i = 0.05; i < 1.0; i += 0.025) {
			String file = writeScores(scores, "data/results.txt", i);
			double score = getEvaluation(file);
			if(score > bestScore) {
				bestScore = score;
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

	private double getEvaluation(String resultsFile) {
		double results = -1.0;
		try {
			// Execute command
			String[] commands = new String[] { "python2", "data/eval_rte.py",
					"data/RTE2_dev.xml", resultsFile };
			Process child = Runtime.getRuntime().exec(commands);

			// Get the input stream and read from it
			BufferedReader in = new BufferedReader(new InputStreamReader(
					child.getInputStream()));
			String c;
			while ((c = in.readLine()) != null) {
				String[] parts = c.split(" = ");
				results = Double.parseDouble(parts[1]);
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return results;

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
