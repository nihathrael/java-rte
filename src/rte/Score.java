package rte;

public class Score {
	
	private static final String YES = "YES";
	private static final String NO = "NO";
	
	final private int id;
	final private double entailment; 
	
	public Score(int id, double entailment) {
		this.id = id;
		this.entailment = entailment;
	}
	
	public String getOutputString(double threshold) {
		if(entailment > threshold) {
			return id + " " + YES;
		} else {
			return id + " " + NO;
		}
		
	}

}
