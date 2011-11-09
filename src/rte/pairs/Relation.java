package rte.pairs;

public class Relation {
	final SentenceNode parent;
	final SentenceNode child;
	final String type;
	
	public Relation(SentenceNode child, SentenceNode parent, String type) {
		this.parent = parent;
		this.child = child;
		this.type = type;
	}
}
