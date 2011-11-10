package rte.pairs;

public class Forest {

	public int beginId;
	public int endId;

	public Forest(int begin, int end) {
		if (begin > end) {
			beginId = -1;
			endId = -1;
		} else {
			this.beginId = begin;
			this.endId = end;
		}
	}

	@Override
	public boolean equals(Object o2) {
		if (o2 instanceof Forest) {
			Forest o = (Forest) o2;
			return beginId == o.beginId && endId == o.endId;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return new Integer(beginId).hashCode() + new Integer(endId).hashCode();
	}

	
	public String toString() {
		return "Forest(" + beginId + "," + endId +")";
	}
}
