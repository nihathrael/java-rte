package rte.pairs;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Text {
	
	ArrayList<Sentence> sentences = new ArrayList<Sentence>();
	
	public static Text fromXML(Element textElement) {
		Text text = new Text();
		NodeList sentenceList = textElement.getElementsByTagName("sentence");
		
		for(int i = 0; i<sentenceList.getLength(); ++i) {
			text.sentences.add(Sentence.fromXML((Element)sentenceList.item(i)));
		}
		
		return text;
	}

}
