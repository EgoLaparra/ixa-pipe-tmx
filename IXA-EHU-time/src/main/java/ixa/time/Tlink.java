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
	public String category;
	public Hashtable<String, String> features;
	public boolean inDataset;


	public Tlink (String file, Object from, Object to) {
		
		this.category = "";
		this.file = file;
		this.from = from;
		this.to = to;
		this.inDataset = false;
	}
	
	public Tlink (String file, Event evt, Timex tmx) {
		
		this.category = "";
		this.file = file;
		this.from = evt;
		this.to = tmx;
		this.inDataset = false;
	}
	
	
	public Tlink (String file, Timex tmx, Event evt) {
		
		this.category = "";
		this.file = file;
		this.from = tmx;
		this.to = evt;
		this.inDataset = false;
	}
	
	public void loadFeaturesFromNaf(KAFDocument naf) {
	
		if (this.from.getClass() == Event.class) {
			if (this.to.getClass() == Event.class)
				this.loadEventEventFeaturesFromNaf(naf,  (Event) this.to, (Event) this.from);
			else if (this.to.getClass() == Timex.class)
				this.loadTimexEventFeaturesFromNaf(naf, (Timex) this.to, (Event) this.from);
			else if (this.to.getClass() == DCT.class)
				this.loadDCTEventFeaturesFromNaf(naf, (DCT) this.to, (Event) this.from);
		} else if (this.from.getClass() == Timex.class) {
			if (this.to.getClass() == Event.class)
				this.loadTimexEventFeaturesFromNaf(naf,  (Timex) this.from, (Event) this.to);
			else if (this.to.getClass() == Timex.class)
				this.loadTimexTimexFeaturesFromNaf(naf, (Timex) this.to, (Timex) this.from);
			else if (this.to.getClass() == DCT.class)
				this.loadDCTTimexFeaturesFromNaf(naf, (DCT) this.to, (Timex) this.from);	
		} else if (this.from.getClass() == DCT.class) {
			if (this.to.getClass() == Event.class)
				this.loadDCTEventFeaturesFromNaf(naf, (DCT) this.from, (Event) this.to);
			else if (this.to.getClass() == Timex.class)
				this.loadDCTTimexFeaturesFromNaf(naf, (DCT) this.from, (Timex) this.to);
		}
	}
	
	public void loadTimexEventFeaturesFromNaf(KAFDocument naf, Timex timex, Event event) {
		
		this.features = new Hashtable<String,String>();
		
		if (event.sentid.equals(timex.sentid)) {
			Integer sId = Integer.parseInt(event.sentid);
			Integer eventIdinSent = event.headid.idinsent;
			Integer timexIdinSent = timex.headid.idinsent;
			
			List<Annotation> terms = naf.getBySent(Layer.TERMS, sId);
			Tree constituency = (Tree) naf.getBySent(Layer.CONSTITUENCY, sId).get(0);
			List<NonTerminal> eventPath = new ArrayList<NonTerminal>();
	    	eventPath = getConsituencyPath((Term) terms.get(eventIdinSent), constituency.getRoot(), eventPath);
	    	List<NonTerminal> timexPath = new ArrayList<NonTerminal>();
	    	timexPath = getConsituencyPath((Term) terms.get(timexIdinSent), constituency.getRoot(), timexPath);
			
	    	this.features.put("eHeadingPrep", getHeadingPreposition(eventPath));
			this.features.put("tHeadingPrep", getHeadingPreposition(timexPath));
			//this.features.put("synRelation", getSyntacticRelation(eventPath, timexPath));
			//this.features.put("eTense", event.tense);
			//this.features.put("eAspect", event.aspect);
			//this.features.put("tType", timex.type);
		}
	}

	public void loadEventEventFeaturesFromNaf(KAFDocument naf, Event eventFrom, Event eventTo) {
		
		this.features = new Hashtable<String,String>();
			
		Integer sIdFrom = Integer.parseInt(eventFrom.sentid);
		Integer eventFromIdinSent = eventFrom.headid.idinsent;
		Integer sIdTo = Integer.parseInt(eventTo.sentid);
		Integer eventToIdinSent = eventTo.headid.idinsent;

		List<Annotation> terms = naf.getBySent(Layer.TERMS, sIdFrom);
		Tree constituency = (Tree) naf.getBySent(Layer.CONSTITUENCY, sIdFrom).get(0);
		List<NonTerminal> eventFromPath = new ArrayList<NonTerminal>();
		eventFromPath = getConsituencyPath((Term) terms.get(eventFromIdinSent), constituency.getRoot(), eventFromPath);
		
		terms = naf.getBySent(Layer.TERMS, sIdTo);
		constituency = (Tree) naf.getBySent(Layer.CONSTITUENCY, sIdTo).get(0);
		List<NonTerminal> eventToPath = new ArrayList<NonTerminal>();
		eventToPath = getConsituencyPath((Term) terms.get(eventToIdinSent), constituency.getRoot(), eventToPath);
	    
		if (sIdFrom.equals(sIdTo)) {
			this.features.put("synRelation", getSyntacticRelation(eventFromPath, eventToPath));
	    } else {
		   	this.features.put("synRelation", "differentsentence");
	    }
		this.features.put("eHeadingPrepFrom", getHeadingPreposition(eventFromPath));
		this.features.put("eHeadingPrepTo", getHeadingPreposition(eventToPath));
		this.features.put("eTenseFrom", eventFrom.tense);
		this.features.put("eAspectFrom", eventFrom.aspect);
		this.features.put("eTenseTo", eventTo.tense);
		this.features.put("eAspectTo", eventTo.aspect);	
	}
	
	
	
	public void loadTimexTimexFeaturesFromNaf(KAFDocument naf, Timex timexFrom, Timex timexTo) {
		
		this.features = new Hashtable<String,String>();
		
		Integer sIdFrom = Integer.parseInt(timexFrom.sentid);
		Integer timexFromIdinSent = timexFrom.headid.idinsent;
		Integer sIdTo = Integer.parseInt(timexTo.sentid);
		Integer timexToIdinSent = timexTo.headid.idinsent;

		List<Annotation> terms = naf.getBySent(Layer.TERMS, sIdFrom);
		Tree constituency = (Tree) naf.getBySent(Layer.CONSTITUENCY, sIdFrom).get(0);
		List<NonTerminal> timexFromPath = new ArrayList<NonTerminal>();
		timexFromPath = getConsituencyPath((Term) terms.get(timexFromIdinSent), constituency.getRoot(), timexFromPath);
		
		terms = naf.getBySent(Layer.TERMS, sIdTo);
		constituency = (Tree) naf.getBySent(Layer.CONSTITUENCY, sIdTo).get(0);
		List<NonTerminal> timexToPath = new ArrayList<NonTerminal>();
		timexToPath = getConsituencyPath((Term) terms.get(timexToIdinSent), constituency.getRoot(), timexToPath);
    	
	    if (sIdFrom.equals(sIdTo)) {
	    	this.features.put("synRelation", getSyntacticRelation(timexFromPath, timexToPath));
    	} else {
	    	this.features.put("synRelation", "differentsentence");
    	}
	    this.features.put("tHeadingPrepFrom", getHeadingPreposition(timexFromPath));
	    this.features.put("tHeadingPrepTo", getHeadingPreposition(timexToPath));
	    this.features.put("tTypeFrom", timexFrom.type);
		this.features.put("tTypeTo", timexTo.type);

	}
	
	public void loadDCTTimexFeaturesFromNaf(KAFDocument naf, DCT dct, Timex timex) {
		
		this.features = new Hashtable<String,String>();
		
		Integer sId = Integer.parseInt(timex.sentid);
		Integer timexIdinSent = timex.headid.idinsent;
		
		List<Annotation> terms = naf.getBySent(Layer.TERMS, sId);
		Tree constituency = (Tree) naf.getBySent(Layer.CONSTITUENCY, sId).get(0);
		List<NonTerminal> timexPath = new ArrayList<NonTerminal>();
		timexPath = getConsituencyPath((Term) terms.get(timexIdinSent), constituency.getRoot(), timexPath);
				
		this.features.put("tHeadingPrep", getHeadingPreposition(timexPath));
		this.features.put("tType", timex.type);
	}
	
	public void loadDCTEventFeaturesFromNaf(KAFDocument naf, DCT dct, Event event) {
		
		this.features = new Hashtable<String,String>();
		
		Integer sId = Integer.parseInt(event.sentid);
		Integer eventIdinSent = event.headid.idinsent;
		
		List<Annotation> terms = naf.getBySent(Layer.TERMS, sId);
		Tree constituency = (Tree) naf.getBySent(Layer.CONSTITUENCY, sId).get(0);
		List<NonTerminal> eventPath = new ArrayList<NonTerminal>();
		eventPath = getConsituencyPath((Term) terms.get(eventIdinSent), constituency.getRoot(), eventPath);
				
		this.features.put("eHeadingPrep", getHeadingPreposition(eventPath));
		this.features.put("eTense", event.tense);

	}
}
