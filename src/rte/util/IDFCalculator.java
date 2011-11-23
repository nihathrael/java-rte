package rte.util;

import java.util.ArrayList;
import java.util.HashMap;

import rte.pairs.THPair;
import rte.pairs.SentenceNode;
import rte.pairs.Text;

public abstract class IDFCalculator {
	
	HashMap<String, Integer> map = new HashMap<String, Integer>();
	
	
	public IDFCalculator(ArrayList<THPair> pairs) {
		addPairs(pairs);
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
	
	public void addPairs(ArrayList<THPair> pairs) {
		for(THPair pair: pairs) {
			countWords(pair.text);
			countWords(pair.hypothesis);
		}
	}

}
