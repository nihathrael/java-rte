package rte.recognizers;

import rte.pairs.Text;

public interface EntailmentRecognizer {
	
	public double entails(Text text, Text hypothesis);
	
	public String getName();
	
	
}
