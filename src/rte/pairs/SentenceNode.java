package rte.pairs;

import org.w3c.dom.Element;

import rte.Main;

public class SentenceNode {
	final String id;
	String word = null;
	String lemma = null;
	String posTag = null;
	Relation relation = null;
	SentenceNode antecedent = null;
	
	public SentenceNode(String id) {
		this.id = id;
	}
	
	public static SentenceNode fromXML(Element textElement) {
		String id = textElement.getAttribute("id");
		SentenceNode sentenceNode = new SentenceNode(id);
		sentenceNode.word = Main.getTagValue("word", textElement);
		sentenceNode.lemma = Main.getTagValue("lemma", textElement);
		sentenceNode.posTag = Main.getTagValue("pos-tag", textElement);
		
		return sentenceNode;
	}
}
