package rte.pairs;

import org.w3c.dom.Element;

import rte.Main;

public class SentenceNode {
	public final String id;
	public String word = null;
	public String lemma = null;
	public String posTag = null;
	public Relation relation = null;
	public SentenceNode antecedent = null;
	
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
