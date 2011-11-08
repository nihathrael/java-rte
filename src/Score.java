
public class Score {
	
	private static final String YES = "YES";
	private static final String NO = "NO";
	
	final private int id;
	final private boolean entailment; 
	
	public Score(int id, boolean entailment) {
		this.id = id;
		this.entailment = entailment;
	}
	
	public String toString() {
		if(entailment) {
			return id + " " + YES;
		} else {
			return id + " " + NO;
		}
		
	}

}
