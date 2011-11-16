package rte.util;

import rte.pairs.Text;

public class PolarityCalculator {
	
	private final static String[] negatives = {
		"not", "refuse", "wrong", "deny", "no", "false", "ignore",
		"cannot", "never", "unsuccessfully"};
	
	
	public static boolean polarity(Text T, Text H) {
		return getPolIndex(T.toString()) == getPolIndex(H.toString());
	}
	
	
	private static int getPolIndex(String s) {
		int count=0;
		for(String n : negatives) {
			if(s.contains(n)) count++;
		}
		return count;
	}
	
}
