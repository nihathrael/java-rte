package rte.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;

import jsl.measure.jwi.JiangConrathSimilarity;
import jsl.measure.jwi.LinSimilarity;
import rte.pairs.SentenceNode;
import edu.mit.jwi.Dictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;

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
			this.dict = new RamDictWrapper(url);
			dict.open();
			jiangConrathSimilarity = new JiangConrathSimilarity(dict);
			linSimilarity = new LinSimilarity(dict);
			System.out.printf("Done (%1d msec )\n", System.currentTimeMillis()
					- t);
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

	public boolean synonymsOverlap(SentenceNode w1, SentenceNode w2) {
		try {
			if(w1.lemma.equals(w2.lemma)) {
				return true;
			}
			IIndexWord indexWord = dict.getIndexWord(w1.lemma,
					getPos(w1));
			IIndexWord indexWord2 = dict.getIndexWord(w2.lemma,
					getPos(w2));
			if(indexWord == null || indexWord2 == null) {
				return false;
			}
			HashSet<String> lemmas1 = collectSynonyms(indexWord);
			HashSet<String> lemmas2 = collectSynonyms(indexWord2);
			for (String lemma : lemmas1) {
				for (String lemma2 : lemmas2) {
					if (lemma.equals(lemma2)) {
						return true;
					}
				}
			}
		} catch (IllegalArgumentException e) {
			//System.out.println(e.getMessage());
		}
		return false;
	}

	public HashSet<String> collectSynonyms(IIndexWord indexWord) {
		HashSet<String> words = new HashSet<String>();
		List<IWordID> wordIds = indexWord.getWordIDs();
			for (IWordID wordId : wordIds) {
				ISynset synset = dict.getWord(wordId).getSynset();
				for (IWord w : synset.getWords()) {
					words.add(w.getLemma());
				}
			}
		return words;
	}

	private POS getPos(SentenceNode node) {
		if (node.posTag.equals("N")) {
			return POS.NOUN;
		} else if (node.posTag.equals("V")) {
			return POS.VERB;
		} else if (node.posTag.equals("A")) {
			if(node.word.endsWith("ly")) {
				return POS.ADVERB;
			}
			return POS.ADJECTIVE;
		}
		throw new IllegalArgumentException("No such POS Tag + " + node.posTag);
	}

}
