package ixa.time;

import ixa.kaflib.Annotation;
import ixa.kaflib.KAFDocument;

import java.util.List;
import ixa.kaflib.*;
import ixa.kaflib.KAFDocument.Layer;

public class EventRecognitionInstance extends Features {
	public String file;
	public String sentid;
	public String tokenid;
/*	public String label;
	public String lemma;*/
	
	public EventRecognitionInstance(KAFDocument naf, Term term, String fileName) {
		this.label = "0";
		this.file = fileName.replace(".naf", "");
    	this.sentid = term.getSent().toString();
    	Integer tokenIdx = (Integer) naf.getBySent(Layer.TERMS, term.getSent()).indexOf(term);
    	this.tokenid = tokenIdx.toString();
	}
	
	public EventRecognitionInstance(String l) {
		this.label = l;
	}
	
	public void setFeatures (KAFDocument naf, Term term) {
    	this.setLemma(term.getLemma());
    	this.setPoS(term.getPos());
    	List<Annotation> constituency = naf.getBySent(Layer.CONSTITUENCY, term.getSent());
    	this.setPhrase(term, constituency);
	}
}
