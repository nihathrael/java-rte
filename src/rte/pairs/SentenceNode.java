package rte.pairs;

import java.util.ArrayList;

import org.w3c.dom.Element;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

import rte.Main;

public class SentenceNode {
	public final String id;
	public String word = null;
	public String lemma = null;
	public String posTag = null;
	public Relation relation = null;
	public SentenceNode antecedent = null;
	public ArrayList<SentenceNode> children = new ArrayList<SentenceNode>();

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

	public boolean isLeaf() {
		return children.isEmpty();
	}

	public SentenceNode leftChild() {
		return children.get(0);
	}

	public ArrayList<SentenceNode> postOrder() {
		ArrayList<SentenceNode> ret = new ArrayList<SentenceNode>();
		if (!isLeaf()) {
			for (SentenceNode child : children) {
				ret.addAll(child.postOrder());
			}
		}
		ret.add(this);
		return ret;
	}

	public SentenceNode leftMostLeafDescendant() {
		if (isLeaf()) {
			return this;
		} else {
			return children.get(0).leftMostLeafDescendant();
		}
	}

	/**
	 * Return a list of the left most leaf descendents, traversing the nodes in
	 * postorder
	 */
	public ArrayList<SentenceNode> findLMLD() {
		ArrayList<SentenceNode> nodes = new ArrayList<SentenceNode>();
		if (!isLeaf()) {
			for (SentenceNode child : children) {
				nodes.addAll(child.findLMLD());
			}
		}
		nodes.add(leftMostLeafDescendant());
		return nodes;
	}

	public ArrayList<SentenceNode> keyNodes() {
		ArrayList<SentenceNode> keyNodes = new ArrayList<SentenceNode>();
		keyNodes.add(this);
		keyNodes.addAll(findKeyNodes());
		return keyNodes;
	}

	private ArrayList<SentenceNode> findKeyNodes() {
		ArrayList<SentenceNode> keyNodes = new ArrayList<SentenceNode>();
		if (children.size() > 1) {
			for (int i = 1; i < children.size() - 1; ++i) {
				keyNodes.add(children.get(i));
			}
		}
		for (SentenceNode node : children) {
			keyNodes.addAll(node.findKeyNodes());
		}
		return keyNodes;
	}

	public int distance(SentenceNode o) {
		int distance = 0;

		ArrayList<SentenceNode> T1 = postOrder();
		ArrayList<SentenceNode> T2 = o.postOrder();

		ArrayList<SentenceNode> l1 = findLMLD();
		ArrayList<SentenceNode> l2 = o.findLMLD();

		ArrayList<SentenceNode> kr1 = keyNodes();
		ArrayList<SentenceNode> kr2 = o.keyNodes();

		for (SentenceNode i : kr1) {
			for (SentenceNode j : kr2) {
				editDist(i, j, T1, T2, l1, l2);
			}
		}

		return distance;
	}

	public void editDist(SentenceNode kr1,
			SentenceNode kr2, ArrayList<SentenceNode> T1,
			ArrayList<SentenceNode> T2, ArrayList<SentenceNode> l1,
			ArrayList<SentenceNode> l2) {
		ForestDist tmp = new ForestDist();

	}

	private int unitCosts(SentenceNode n, SentenceNode m) {
		if (n == null || m == null) {
			return 1;
		}

		if (n.id.equals(m.id)) {
			return 0;
		} else {
			return 1;
		}
	}
}
