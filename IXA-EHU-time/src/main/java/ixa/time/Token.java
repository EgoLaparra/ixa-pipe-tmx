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

public class Token extends Features {
	
	public String file;
	public String sentid;
	public String tokenid;
	public Integer idinsent;
	public String eventId;
	public boolean isEventHead;
	public String timexId;
	public boolean isTimexHead;
	public Hashtable<String, String> features;
	public boolean inDataset;

	
	public Token (String file, String sentid, String tokenid, Integer idInSent) {
		
		this.eventId = "";
		this.timexId = "";
		this.file = file;
		this.sentid = sentid;
		this.tokenid = tokenid;
		this.idinsent = idInSent;
		this.isEventHead = false;
		this.isTimexHead = false;
		this.inDataset = false;
	}
	
	
	public void loadFeaturesFromNaf(KAFDocument naf) {
		
		this.features = new Hashtable<String,String>();
		Integer sId = Integer.parseInt(this.sentid);
		List<Annotation> terms = naf.getBySent(Layer.TERMS, sId);

		Term term = (Term) terms.get(this.idinsent);
		this.features.put("lemma", getLemma(term));
		this.features.put("pos", getPos(term));
			
		Tree constituency = (Tree) naf.getBySent(Layer.CONSTITUENCY, sId).get(0);
		List<NonTerminal> path = new ArrayList<NonTerminal>();
		path = getConsituencyPath(term, constituency.getRoot(), path);
		this.features.put("phrase", getPhrase(path));
	}
}
