package rte.treedistance.cost;

import rte.pairs.SentenceNode;

public interface TreeEditCost {
	public double cost(SentenceNode m, SentenceNode n);
}
