package rte.recognizers;

import rte.pairs.Text;

public interface EntailmentRecognizer {
	
	public boolean entails(Text text, Text hypothesis, double threshold);
	
	
}
