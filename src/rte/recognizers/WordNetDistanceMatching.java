package rte.recognizers;

import java.util.ArrayList;

import rte.pairs.Text;
import rte.util.WordNetAccessor;

public class WordNetDistanceMatching implements EntailmentRecognizer {

	WordNetAccessor wnet = WordNetAccessor.getInstance();

	public double entails(Text text, Text hypothesis) {

		ArrayList<String> ws1, ws2;
		ArrayList<Double> dists;
		ws1 = text.getWordArray();
		ws2 = hypothesis.getWordArray();
		dists = new ArrayList<Double>();

		String otherword = "";

		for (String w : ws2) {
			double maxDist = 0.0;
			for (String x : ws1) {
				double dist = wnet.getJiangConrathSimilarity(w, x);
				if (dist > maxDist) {
					maxDist = dist;
					otherword = x;
				}
			}
			ws1.remove(otherword);
			// System.out.println(w + " " + otherword + " " + maxDist);
			if (maxDist > 0.0) {
				dists.add(maxDist);
			}

		}

		double sum = 0.0;
		for (double d : dists) {
			sum += d;
		}

		return sum / (((double)dists.size()/ws2.size()) * ws2.size());
	}

	public String getName() {
		return "WordNetDistanceMatching";
	}

}
