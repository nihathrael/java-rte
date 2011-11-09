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
	
	public ArrayList<SentenceNode> getAllSentenceNodes() {
		ArrayList<SentenceNode> result = new ArrayList<SentenceNode>();
		
		for(Sentence sentence : sentences) {
			result.addAll(sentence.getAllSentenceNodes());
		}
		
		return result;
	}
	
	public ArrayList<String> getWordArray() {
		ArrayList<String> result = new ArrayList<String>();
		ArrayList<SentenceNode> allnodes = getAllSentenceNodes();
		for(SentenceNode n : allnodes) {
			if(n.word != null)
				result.add(n.word);
		}
		return result;
	}

	public ArrayList<String> getWordArrayWithoutPunctuation() {
		ArrayList<String> result = new ArrayList<String>();
		ArrayList<SentenceNode> allnodes = getAllSentenceNodes();
		for(SentenceNode n : allnodes) {
			if(n.word != null) {
				if(n.word.equals(',') || n.word.equals('.'))
					continue;
			
				result.add(n.word);
			}
		}
		return result;
	}
	
}
