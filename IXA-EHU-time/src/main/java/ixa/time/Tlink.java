package ixa.time;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import ixa.kaflib.Annotation;
import ixa.kaflib.KAFDocument;
import ixa.kaflib.NonTerminal;
import ixa.kaflib.Term;
import ixa.kaflib.Tree;
import ixa.kaflib.KAFDocument.Layer;

public class Tlink extends Features{

	public String file;
	public Object from;
	public Object to;
	public Event event;
	public Timex timex;
	public String category;
	public Hashtable<String, String> features;
	public boolean inDataset;


	public Tlink (String file, Event evt, Timex tmx) {
		
		this.category = "";
		this.file = file;
		this.event = evt;
		this.timex = tmx;
		this.from = evt;
		this.to = tmx;
		this.inDataset = false;
	}
	
	
	public Tlink (String file, Timex tmx, Event evt) {
		
		this.category = "";
		this.file = file;
		this.event = evt;
		this.timex = tmx;
		this.from = tmx;
		this.to = evt;
		this.inDataset = false;
	}
	
	public void loadFeaturesFromNaf(KAFDocument naf) {
		
		this.features = new Hashtable<String,String>();
		
		if (this.event.sentid.equals(this.timex.sentid)) {
			Integer sId = Integer.parseInt(this.event.sentid);
			Integer eventIdinSent = this.event.headid.idinsent;
			Integer timexIdinSent = this.timex.headid.idinsent;
			
			List<Annotation> terms = naf.getBySent(Layer.TERMS, sId);
			Tree constituency = (Tree) naf.getBySent(Layer.CONSTITUENCY, sId).get(0);
			List<NonTerminal> eventPath = new ArrayList<NonTerminal>();
	    	eventPath = getConsituencyPath((Term) terms.get(eventIdinSent), constituency.getRoot(), eventPath);
	    	List<NonTerminal> timexPath = new ArrayList<NonTerminal>();
	    	timexPath = getConsituencyPath((Term) terms.get(timexIdinSent), constituency.getRoot(), timexPath);
			
	    	this.features.put("eHeadingPrep", getHeadingPreposition(eventPath));
			this.features.put("tHeadingPrep", getHeadingPreposition(timexPath));
			//this.features.put("synRelation", getSyntacticRelation(eventPath, timexPath));
			//this.features.put("timextype", this.timex.type);
		}
	}

}
