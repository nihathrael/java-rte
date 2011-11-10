package rte.pairs;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import rte.Main;

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
		
		// Create nodes
		for(int i = 0; i<sentenceNodeList.getLength(); ++i) {
			sentence.nodes.add(SentenceNode.fromXML((Element)sentenceNodeList.item(i)));
		}
		
		// Create relations
		for(int i = 0; i<sentenceNodeList.getLength(); ++i) {
			Element node = (Element)sentenceNodeList.item(i);
			Element relation = (Element)node.getElementsByTagName("relation").item(0);
			String nodeId = node.getAttribute("id");
			SentenceNode mainNode = getSentenceNodeById(nodeId, sentence);
			if(relation != null) {
				String parentId = relation.getAttribute("parent");
				SentenceNode parent = getSentenceNodeById(parentId, sentence);
				
				String type = Main.getTagValue("relation", node);
				
				Relation rel = new Relation(mainNode, parent, type);
				mainNode.relation = rel;
				parent.children.add(mainNode);
				//System.out.println("Relation created!");
			}
			String antecedentId = Main.getTagValue("antecedent", node);
			if(antecedentId != null) {
				SentenceNode antecNode = getSentenceNodeById(antecedentId, sentence);
				mainNode.antecedent = antecNode;
				//System.out.println("Antecedent set!");
			}
		}
		
		return sentence;
	}
	
	private static SentenceNode getSentenceNodeById(String id, Sentence sentence) {
		for(SentenceNode node: sentence.nodes) {
			if(node.id.equals(id)) {
				return node;
			}
		}
		System.err.println("Bad! No node found with id: " + id);
		return null;
	}

	public ArrayList<SentenceNode> getAllSentenceNodes() {
		return nodes;
	}
	
}
