package rte.util;

import java.io.IOException;
import java.net.URL;

import jsl.measure.jwi.JiangConrathSimilarity;
import edu.mit.jwi.Dictionary;

public class WordNetAccessor {

	private static WordNetAccessor instance = null;

	private Dictionary dict;

	private JiangConrathSimilarity jiangConrathSimilarity;

	private WordNetAccessor() {
		// construct the URL to the Wordnet dictionary directory
		String path = "/usr/share/wordnet/dict/";
		try {
			URL url = new URL("file", null, path);
			// construct the dictionary object and open it
			System.out.println("Loading WordNet dictionary...");
			long t = System.currentTimeMillis();
			this.dict = new Dictionary(url);
			dict.open();
			jiangConrathSimilarity = new JiangConrathSimilarity(dict);
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
		return jiangConrathSimilarity.calculateSimilarity(w1, w2);
	}

}
