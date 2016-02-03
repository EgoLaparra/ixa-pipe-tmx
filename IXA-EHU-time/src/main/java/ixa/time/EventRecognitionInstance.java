package ixa.time;

public class EventRecognitionInstance extends Features {
	public String file;
	public String sentid;
	public String tokenid;
/*	public String label;
	public String lemma;*/
	
	public EventRecognitionInstance() {
		this.label = "0";
	}
	
	public EventRecognitionInstance(String l) {
		this.label = l;
	}
}
