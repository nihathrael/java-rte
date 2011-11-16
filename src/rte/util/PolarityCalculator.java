package rte.util;

import java.util.ArrayList;

import rte.pairs.Text;

public class PolarityCalculator {
	
	private final static String[] negatives = {
		"not", "refuse", "wrong", "deny", "no", "false", "ignore",
		"cannot", "never", "unsuccessfully"};
	
	
	public static boolean polarity(Text T, Text H) {
		return getPolIndex(T.getWordArray()) == getPolIndex(H.getWordArray());
	}
	
	
	private static int getPolIndex(ArrayList<String> s) {
		int count=0;
		for(String n : negatives) {
			for(String word: s) {
				if(word.equals(n)){
					count++;
				}
			}
		}
		return count;
	}
	
}
