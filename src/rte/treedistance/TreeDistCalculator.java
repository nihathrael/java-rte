package rte.treedistance;

import java.util.ArrayList;

import rte.pairs.Forest;
import rte.pairs.ForestDist;
import rte.pairs.ForestTuple;
import rte.pairs.SentenceNode;
import rte.treedistance.cost.TreeEditCost;

public class TreeDistCalculator {

	private final ArrayList<SentenceNode> T1;
	private final ArrayList<SentenceNode> T2;

	private final int[] l1;
	private final int[] l2;

	private final ArrayList<Integer> krs1;
	private final ArrayList<Integer> krs2;

	private final double[][] tree_dist;
	
	private final TreeEditCost cost;
	
	private static Forest NULLFOREST = new Forest(-1, -1);

	public TreeDistCalculator(SentenceNode o1, SentenceNode o2, TreeEditCost cost) {
		
		this.cost = cost;
		
		T1 = o1.postOrder();
		T2 = o2.postOrder();

		l1 = o1.leftMostLeafDescendants(T1);
		l2 = o2.leftMostLeafDescendants(T2);

		krs1 = o1.keyRoots(l1, T1);
		krs2 = o2.keyRoots(l2, T2);

		tree_dist = new double[T1.size()][T2.size()];
		
		
	}

	public double calculate() {
		for (Integer i : krs1) {
			for (Integer j : krs2) {
				calcDist(i, j);
			}
		}
		return tree_dist[l1.length-1][l2.length-1];
	}

	private void calcDist(int i, int j) {

		// Autofilled with (null, null) = 0;
		ForestDist fdist = new ForestDist();

		for (int i1 = l1[i]; i1 <= i; i1++) {
			fdist.p(ft(forest(l1[i],i1), NULLFOREST), fdist.get(ft(forest(l1[i],i1-1), NULLFOREST)) + gamma(i1,null));
		}
		
		for (int j1 = l2[j]; j1 <= j; j1++) {
			fdist.p(ft(NULLFOREST, forest(l2[j],j1)), fdist.get(ft(NULLFOREST, forest(l2[j],j1-1))) + gamma(null,j1));
		}
		
		for (int i1 = l1[i]; i1 <= i; i1++) {
			for (int j1 = l2[j]; j1 <= j; j1++) {
				if(l1[i1] == l1[i] && l2[j1] == l2[j]) {
					double min = min(
						fdist.get(ft(forest(l1[i],i1-1), forest(l2[j],j1)))   + gamma(i1,	null),
						fdist.get(ft(forest(l1[i],i1),   forest(l2[j],j1-1))) + gamma(null,	j1),
						fdist.get(ft(forest(l1[i],i1-1), forest(l2[j],j1-1))) + gamma(i1,	j1)
					);
					fdist.p(ft(forest(l1[i],i1), forest(l2[j],j1)), min);
					tree_dist[i1][j1] = min; // Store in permanent array 
				} else {
					double min = min(
							fdist.get(ft(forest(l1[i],i1-1), forest(l2[j],j1)))   + gamma(i1,	null),
							fdist.get(ft(forest(l1[i],i1),   forest(l2[j],j1-1))) + gamma(null,	j1),
							fdist.get(ft(forest(l1[i],i1-1), forest(l2[j],j1-1))) + tree_dist[i1][j1]
						);
					fdist.p(ft(forest(l1[i],i1), forest(l2[j],j1)), min);
				}
			}
		}
	}

	private double min(double a, double b, double c) {
		return Math.min(Math.min(a, b), c);
	}
	
	private Forest forest(int i, int b) {
		return new Forest(i, b);
	}

	private ForestTuple ft(Forest f1, Forest f2) {
		return new ForestTuple(f1, f2);
	}


	private double gamma(Integer m, Integer n) {
		SentenceNode a=null, b=null;
		
		if(m!=null) a=T1.get(m);
		if(n!=null) b=T2.get(n);
	
		return cost.cost(a,b);
	}

}
