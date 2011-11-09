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
import rte.recognizers.EntailmentRecognizer;
import rte.recognizers.LemmaAndPosMatching;
import rte.recognizers.LemmaMatching;
import rte.recognizers.LexicalMatching;

public class Main {

	public Main() {
		
		System.out.println("Reading Pairs...");
		ArrayList<AdvPair> advPairs = readAdvancedPairs();
		Collections.sort(advPairs);
		System.out.println("Done!");
		
		EntailmentRecognizer rec1 = new LexicalMatching();
		
		findBestThreshold(advPairs, rec1);
		
		EntailmentRecognizer rec2 = new LemmaMatching();
		findBestThreshold(advPairs, rec2);
		
		EntailmentRecognizer rec3 = new LemmaAndPosMatching();
		findBestThreshold(advPairs, rec3);
		
	}




	private void findBestThreshold(ArrayList<AdvPair> pairs, EntailmentRecognizer recognizer) {
		System.out.println("Searching for best threshold using " + recognizer.getName() + "...");
		double bestScore = 0.0;
		double bestThres = 0.05;
		for (double i = 0.05; i < 1.0; i += 0.025) {
			ArrayList<Score> scores = analyzePairs(pairs, recognizer, i);
			String file = writeScores(scores, "data/results.txt");
			double score = getEvaluation(file);
			if(score > bestScore) {
				bestScore = score;
				bestThres = i;
			}
		}
		System.out.println("Done! Found best results for " + bestThres + " with score: " + bestScore);
	}
	
	
	

	private String writeScores(ArrayList<Score> scores, String outputFile) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(
					outputFile));
			writer.write("ranked: no");
			writer.newLine();
			for (Score score : scores) {
				writer.write(score.toString());
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
			EntailmentRecognizer recognizer, double threshold) {
		
		ArrayList<Score> scores = new ArrayList<Score>();
		for (AdvPair pair : pairs) {
			
			boolean entailment = recognizer.entails(pair.text, pair.hypothesis, threshold);
			
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
