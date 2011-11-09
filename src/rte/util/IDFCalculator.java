package rte.util;

import java.util.ArrayList;
import java.util.HashMap;

import rte.pairs.AdvPair;
import rte.pairs.SentenceNode;
import rte.pairs.Text;

public abstract class IDFCalculator {
	
	HashMap<String, Integer> map = new HashMap<String, Integer>();
	
	
	public IDFCalculator(ArrayList<AdvPair> pairs) {
		for(AdvPair pair: pairs) {
			countWords(pair.text);
			countWords(pair.hypothesis);
		}
	}

	private void countWords(Text text) {
		for(SentenceNode node: text.getAllSentenceNodes()) {
			String key = selectKey(node);
			if(key != null) {
				if(map.containsKey(key)) {
					map.put(key, map.get(key)+1);
				} else {
					map.put(key, 1);
				}
			}
		}
	}

	abstract String selectKey(SentenceNode node);
	
	public double getValueFor(String word) {
		return 1.0/map.get(word);
	}

}
