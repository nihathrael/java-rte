package rte.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import jsl.measure.jwi.JiangConrathSimilarity;
import jsl.measure.jwi.LinSimilarity;
import edu.mit.jwi.Dictionary;

public class WordNetAccessor {

	private static WordNetAccessor instance = null;

	private Dictionary dict;

	private JiangConrathSimilarity jiangConrathSimilarity;

	private LinSimilarity linSimilarity;

	private WordNetAccessor() {
		try {
			Properties prop = new Properties();
			// load a properties file
			prop.load(new FileInputStream("config.properties"));

			String wordNetPath = prop.getProperty("wordnet.path");
			// construct the dictionary object and open it
			System.out.println("Loading WordNet dictionary from: "
					+ wordNetPath);
			URL url = new URL("file", null, wordNetPath);
			long t = System.currentTimeMillis();
			this.dict = new Dictionary(url);
			dict.open();
			jiangConrathSimilarity = new JiangConrathSimilarity(dict);
			linSimilarity = new LinSimilarity(dict);
			System.out.printf("Done (%1d msec ) \n ",
					System.currentTimeMillis() - t);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// look up first sense of the word " dog "
	}

	public static WordNetAccessor getInstance() {
		if (WordNetAccessor.instance == null) {
			WordNetAccessor.instance = new WordNetAccessor();
		}
		return WordNetAccessor.instance;
	}

	public double getJiangConrathSimilarity(String w1, String w2) {
		try {
			return jiangConrathSimilarity.calculateSimilarity(w1, w2);
		} catch (IllegalArgumentException argumentException) {
			return 0.0;
		}
	}

	public double getLinSimilarity(String w1, String w2) {
		try {
			return linSimilarity.calculateSimilarity(w1, w2);
		} catch (IllegalArgumentException argumentException) {
			return 0.0;
		}
	}

}
