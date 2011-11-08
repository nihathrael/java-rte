import java.util.Comparator;


public class Pair implements Comparable<Pair> {

	public int id;
	public String task;
	public boolean entailment;
	
	public String t;
	public String h;
	
	public Pair(int id, String task, boolean entailment, String t, String h) {
		this.id = id;
		this.task = task;
		this.entailment = entailment;
		this.t = t;
		this.h = h;
	}
	
	public String toString() {
		return "Pair(" + id + "," + task + "," + entailment + ") \nh:" + h + "\nt:" + t;
	}
	

	@Override
	public int compareTo(Pair o) {
		return new Integer(id).compareTo(o.id);
	}

}
