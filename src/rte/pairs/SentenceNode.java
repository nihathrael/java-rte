package rte.pairs;

import java.util.ArrayList;
import java.util.Collections;

import org.w3c.dom.Element;

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
	
	private SentenceNode leftMostLeafDescendant() {
		if (isLeaf()) {
			return this;
		} else {
			return children.get(0).leftMostLeafDescendant();
		}
	}
	
	public int[] leftMostLeafDescendants(ArrayList<SentenceNode> postOrderList) {
		int[] lmlds = new int[postOrderList.size()];
		for(int i = 0; i<postOrderList.size(); i++) {
			SentenceNode lmfd = postOrderList.get(i).leftMostLeafDescendant();
			lmlds[i] = postOrderList.indexOf(lmfd);
		}
		return lmlds;
	}
	
	public ArrayList<Integer> keyRoots(int[] lmlds, ArrayList<SentenceNode> postOrderList) {
		ArrayList<Integer> krs = new ArrayList<Integer>();
		
		for(int i = 0; i<postOrderList.size()-1; i++) {
			SentenceNode node = postOrderList.get(i);
			SentenceNode parent = node.relation.parent;
			
			// LR_keyroots(T)= {k there exists no k’> k such that l(k)= l(p(k))}.
			// That is, if k is in LR_keyroots(T) then either k is the root of T or l(k) != l(p(k)),
			// i.e., k has a left sibling.
			if(lmlds[i] != lmlds[postOrderList.indexOf(parent)]) {
				krs.add(i);
			}
		}
		
		// Root node, is always keyroot
		krs.add(postOrderList.indexOf(postOrderList.get(postOrderList.size()-1)));
		
		// Sort, make sure they are in the correct ordering
		Collections.sort(krs);
		
		return krs;
	}
	
	public String toString() {
		return "SN(" + id + "," + word + "," + relation + "";
	}
	
}
