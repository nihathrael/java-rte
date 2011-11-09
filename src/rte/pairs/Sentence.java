package rte.pairs;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Sentence {
	
	final int serial;
	ArrayList<SentenceNode> nodes = new ArrayList<SentenceNode>();

	public Sentence(int serial) {
		this.serial = serial;
	}
	
	public static Sentence fromXML(Element textElement) {
		int serial = Integer.parseInt(textElement.getAttribute("serial"));
		Sentence sentence = new Sentence(serial);
		NodeList sentenceNodeList = textElement.getElementsByTagName("node");
		
		for(int i = 0; i<sentenceNodeList.getLength(); ++i) {
			sentence.nodes.add(SentenceNode.fromXML((Element)sentenceNodeList.item(i)));
		}
		
		return sentence;
	}
	
}
