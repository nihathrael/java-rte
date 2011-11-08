import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Main {

	public Main() {
		System.out.println("Reading Pairs...");
		ArrayList<Pair> pairs = readPairs();
		Collections.sort(pairs);
		System.out.println("Done!");

		for (double i = 0.05; i < 1.0; i += 0.025) {
			System.out.println("Analyzing for threshold: " + i);
			ArrayList<Score> scores = analyzePairs(pairs, i);
			writeScores(scores);
			getEvaluation();
			System.out.println("Done!");
		}
	}

	private void writeScores(ArrayList<Score> scores) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(
					"data/results.txt"));
			writer.write("ranked: no");
			writer.newLine();
			for (Score score : scores) {
				writer.write(score.toString());
				writer.newLine();
			}

			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void getEvaluation() {
		try {
			// Execute command
			String[] commands = new String[] { "python2", "data/eval_rte.py",
					"data/RTE2_dev.xml", "data/results.txt" };
			Process child = Runtime.getRuntime().exec(commands);

			// Get the input stream and read from it
			BufferedReader in = new BufferedReader(new InputStreamReader(
					child.getInputStream()));
			String c;
			while ((c = in.readLine()) != null) {
				System.out.println(c);
			}
			in.close();
		} catch (IOException e) {
		}
	}

	private ArrayList<Score> analyzePairs(ArrayList<Pair> pairs,
			double threshold) {
		ArrayList<Score> scores = new ArrayList<Score>();
		for (Pair pair : pairs) {
			String[] words = pair.h.split(" ");
			int matches = 0;
			for (String word : words) {
				if (pair.t.contains(word)) {
					matches++;
				}
			}
			double wordNum = words.length;
			boolean entailment;
			if (matches / wordNum > threshold) {
				entailment = true;
			} else {
				entailment = false;
			}
			Score tmpScore = new Score(pair.id, entailment);
			scores.add(tmpScore);
		}
		return scores;
	}

	private ArrayList<Pair> readPairs() {
		ArrayList<Pair> pairs = new ArrayList<Pair>();

		try {

			File fXmlFile = new File("data/RTE2_dev.xml");
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
					String t = getTagValue("t", eElement);
					String h = getTagValue("h", eElement);

					int id = Integer.parseInt(eElement.getAttribute("id"));
					String task = eElement.getAttribute("task");
					boolean entailment;
					if (eElement.getAttribute("entailment").equals("yes")) {
						entailment = true;
					} else {
						entailment = false;
					}

					Pair pair = new Pair(id, task, entailment, t, h);
					pairs.add(pair);

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pairs;
	}

	private static String getTagValue(String sTag, Element eElement) {
		NodeList nlList = eElement.getElementsByTagName(sTag).item(0)
				.getChildNodes();

		Node nValue = (Node) nlList.item(0);

		return nValue.getNodeValue();
	}

	public static void main(String[] args) {
		System.out.println("Hi!");
		Main main = new Main();
	}
}
