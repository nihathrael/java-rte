package rte.util;

import java.util.ArrayList;

import rte.pairs.Text;

public class GramCalculator {

	public static ArrayList<String> calcN_grams(ArrayList<String> words, int n) {
		ArrayList<String> result = new ArrayList<String>();
		for (int i = 0; i <= words.size() - n; i++) {
			StringBuilder gram = new StringBuilder();
			for (int j = 0; j < n; j++) {
				gram.append(words.get(i + j));
				gram.append(" ");
			}

			result.add(gram.toString().trim());
		}
		return result;
	}
	
	public static int getNrOfGramMatches(Text A, Text B, int n) {
		ArrayList<String> g1 = calcN_grams(A.getWordArray(), n);
		ArrayList<String> g2 = calcN_grams(B.getWordArray(), n);
		
		return getMatches(g1,g2).size();
		

		
	}
	
	public static ArrayList<String> getMatches(ArrayList<String> grams1, ArrayList<String> grams2) {
		ArrayList<String> matchingGrams = new ArrayList<String>();
		for(String g1 : grams1) {
			for(String g2: grams2) {
				if(g1.equals(g2)) {
					matchingGrams.add(g1);
				}
			}
		}
		return matchingGrams;
	}
	
	
}
