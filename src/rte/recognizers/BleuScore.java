package rte.recognizers;

import java.util.ArrayList;

import rte.pairs.Text;

public class BleuScore implements EntailmentRecognizer {

	private int maxN;
	private boolean mean;
	
	public BleuScore(int maxN_gramLength, boolean useArithmeticMean) {
		maxN = maxN_gramLength;
		mean = useArithmeticMean;
	}
	
	public boolean entails(Text text, Text hypothesis, double threshold) {
		ArrayList<ArrayList<String>> textGrams, hypoGrams;
		textGrams = calcSetOfN_grams(text.getWordArrayWithoutPunctuation(), 1, maxN);
		hypoGrams = calcSetOfN_grams(hypothesis.getWordArrayWithoutPunctuation(), 1, maxN);
		double bleuScore=0.0;
		
		for(int i=0; i<maxN; i++) {
			int matches = calcGramMatches(hypoGrams.get(i), textGrams.get(i));
			double score = (double)matches / (double)hypoGrams.get(i).size();
			//System.out.println(i+1 + "-gram Precision: " + matches + " / " + hypoGrams.get(i).size());
			if(mean) bleuScore += Math.log(score);
			else bleuScore += score/2;
		}
		
		if(mean) bleuScore = Math.exp(bleuScore) / maxN;
		//System.out.println("BleuScore: " + bleuScore);
		return bleuScore > threshold;
	}
	
	
	private int calcGramMatches(ArrayList<String> gramSetA, ArrayList<String> gramSetB) {
		int matches=0;
		for(int a=0; a<gramSetA.size(); a++) {
			String gramA = gramSetA.get(a);
			for(int b=0; b<gramSetB.size(); b++) {
				String gramB = gramSetB.get(b);
				if(gramA.equals(gramB)) {
					matches++;
					break;
				}
			}
		}
		return matches;
	}

	private ArrayList<ArrayList<String>> calcSetOfN_grams(
			ArrayList<String> words, int n_from, int n_to) {
		
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		
		for(int n=n_from; n<=n_to; n++) {
			result.add(calcN_grams(words, n));
		}
			
		return result;
	}
	
	
	private ArrayList<String> calcN_grams(ArrayList<String> words, int n) {
		ArrayList<String> result = new ArrayList<String>();
		for(int i=0; i<=words.size()-n; i++) {
			StringBuilder gram = new StringBuilder();
			for(int j=0; j<n; j++) {
				gram.append(words.get(i+j));
				gram.append(" ");
			}
			
			result.add(gram.toString().trim());	
		}
		return result;
	}
	
	public String getName() {
		if(mean) return BleuScore.class.getSimpleName() + " with arithmetic mean, depth=" + maxN;
		else return BleuScore.class.getSimpleName() + "without weighting, depth=" + maxN;
	}	

}
