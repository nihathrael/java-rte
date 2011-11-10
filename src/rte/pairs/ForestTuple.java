package rte.pairs;

public class ForestTuple {

	public Forest f1;
	public Forest f2;

	public ForestTuple(Forest f1, Forest f2) {
		this.f1 = f1;
		this.f2 = f2;
	}

	@Override
	public boolean equals(Object o2) {
		if (o2 instanceof ForestTuple) {
			ForestTuple o = (ForestTuple) o2;
			return f1.equals(o.f1) && f2.equals(o.f2);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return f1.hashCode() + f2.hashCode();
	}
	
	public String toString() {
		return "ForestTuple(" + f1 + "," + f2 +")";
	}

}
