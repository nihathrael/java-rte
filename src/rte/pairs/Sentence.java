package rte.pairs;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import rte.Main;

public class Sentence {

	final int serial;
	public final ArrayList<SentenceNode> nodes = new ArrayList<SentenceNode>();

	public Sentence(int serial) {
		this.serial = serial;
	}

	public static Sentence fromXML(Element textElement) {
		int serial = Integer.parseInt(textElement.getAttribute("serial"));
		Sentence sentence = new Sentence(serial);
		NodeList sentenceNodeList = textElement.getElementsByTagName("node");

		// Create nodes
		for (int i = 0; i < sentenceNodeList.getLength(); ++i) {
			SentenceNode fromXML = SentenceNode
					.fromXML((Element) sentenceNodeList.item(i));
			if (fromXML.id.startsWith("E")) {
				continue;
			}
			sentence.nodes.add(fromXML);
		}

		// Create relations
		for (int i = 0; i < sentenceNodeList.getLength(); ++i) {
			Element node = (Element) sentenceNodeList.item(i);
			Element relation = (Element) node.getElementsByTagName("relation")
					.item(0);
			String nodeId = node.getAttribute("id");
			if(nodeId.contains("E")) {
				continue; // Bad E node, skip!
			}
			SentenceNode mainNode = getSentenceNodeById(nodeId, sentence);
			if (relation != null) {
				String parentId = relation.getAttribute("parent").trim();
				if (!parentId.contains("E")) {
					SentenceNode parent = getSentenceNodeById(parentId, sentence);
					
					String type = Main.getTagValue("relation", node);
					
					Relation rel = new Relation(mainNode, parent, type);
					mainNode.relation = rel;
					parent.children.add(mainNode);
					// System.out.println("Relation created!");
				}
			}
			String antecedentId = Main.getTagValue("antecedent", node);
			if (antecedentId != null) {
				SentenceNode antecNode = getSentenceNodeById(antecedentId,
						sentence);
				mainNode.antecedent = antecNode;
				// System.out.println("Antecedent set!");
			}
		}

		return sentence;
	}

	private static SentenceNode getSentenceNodeById(String id, Sentence sentence) {
		for (SentenceNode node : sentence.nodes) {
			if (node.id.equals(id)) {
				return node;
			}
		}
		System.err.println("Bad! No node found with id: " + id);
		return null;
	}

	public ArrayList<SentenceNode> getAllSentenceNodes() {
		return nodes;
	}

	public String toString() {
		ArrayList<SentenceNode> nodes = getAllSentenceNodes();
		StringBuilder result = new StringBuilder();
		for (SentenceNode node : nodes) {
			result.append(node.word);
			result.append(" ");
		}
		return result.toString().trim();
	}

	public SentenceNode getRootNode() {
		int[] counts = new int[nodes.size()];
		int biggest = 0;
		int biggestIndex = -1;
		for (SentenceNode node : nodes) {
			int size = node.postOrder().size();
			int index = nodes.indexOf(node);
			counts[index] = size;
			if (node.word != null && size > biggest) {
				biggest = size;
				biggestIndex = index;
			}

		}

		return nodes.get(biggestIndex);
	}

}
