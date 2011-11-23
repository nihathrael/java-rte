package rte.recognizers;

import rte.pairs.Text;

public interface IEntailmentRecognizer {
	
	public double entails(Text text, Text hypothesis);
	
	public String getName();
	
}
