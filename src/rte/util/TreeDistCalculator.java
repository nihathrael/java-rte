package rte.util;

import java.util.ArrayList;

import rte.pairs.Forest;
import rte.pairs.ForestDist;
import rte.pairs.ForestTuple;
import rte.pairs.SentenceNode;

public class TreeDistCalculator {

	ArrayList<SentenceNode> T1;
	ArrayList<SentenceNode> T2;

	int[] l1;
	int[] l2;

	ArrayList<Integer> krs1;
	ArrayList<Integer> krs2;

	int[][] tree_dist;
	
	private static Forest NULLFOREST = new Forest(-1, -1);

	public TreeDistCalculator(SentenceNode o1, SentenceNode o2) {
		T1 = o1.postOrder();
		T2 = o2.postOrder();

		l1 = o1.leftMostLeafDescendants(T1);
		l2 = o2.leftMostLeafDescendants(T2);

		krs1 = o1.keyRoots(l1, T1);
		krs2 = o2.keyRoots(l2, T2);

		tree_dist = new int[T1.size()][T2.size()];
	}

	public int calculate() {
		for (Integer i : krs1) {
			for (Integer j : krs2) {
				calcDist(i, j);
			}
		}
		return tree_dist[l1.length-1][l2.length-1];
	}

	public void calcDist(int i, int j) {

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
					int min = min(
						fdist.get(ft(forest(l1[i],i1-1), forest(l2[j],j1)))   + gamma(i1,	null),
						fdist.get(ft(forest(l1[i],i1),   forest(l2[j],j1-1))) + gamma(null,	j1),
						fdist.get(ft(forest(l1[i],i1-1), forest(l2[j],j1-1))) + gamma(i1,	j1)
					);
					fdist.p(ft(forest(l1[i],i1), forest(l2[j],j1)), min);
					tree_dist[i1][j1] = min; // Store in permanent array 
				} else {
					int min = min(
							fdist.get(ft(forest(l1[i],i1-1), forest(l2[j],j1)))   + gamma(i1,	null),
							fdist.get(ft(forest(l1[i],i1),   forest(l2[j],j1-1))) + gamma(null,	j1),
							fdist.get(ft(forest(l1[i],i1-1), forest(l2[j],j1-1))) + tree_dist[i1][j1]
						);
					fdist.p(ft(forest(l1[i],i1), forest(l2[j],j1)), min);
				}
			}
		}
		
		

	}

	public int min(int a, int b, int c) {
		int min = a;
		if(b<min)
			min = b;
		if(c<min)
			min = c;
		return min;
	}
	
	private Forest forest(int i, int b) {
		return new Forest(i, b);
	}

	private ForestTuple ft(Forest f1, Forest f2) {
		return new ForestTuple(f1, f2);
	}


	private int gamma(Integer n, Integer m) {
		if (n == null) {
			return 1;
		}
		
		if(m == null) {
			return 0;
		}
		
		
		SentenceNode t1Node = T1.get(n);
		SentenceNode t2Node = T2.get(m);
		
		if(t1Node.word == null || t2Node.word == null) {
			return 1;
		}
		
	
		if (t1Node.word.equals(t2Node.word)) {
			return 0;
		} else {
			return 1;
		}
	}

}
