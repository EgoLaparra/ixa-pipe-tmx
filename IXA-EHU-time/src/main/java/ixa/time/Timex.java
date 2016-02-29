package ixa.time;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import ixa.kaflib.Annotation;
import ixa.kaflib.KAFDocument;
import ixa.kaflib.NonTerminal;
import ixa.kaflib.Term;
import ixa.kaflib.Tree;
import ixa.kaflib.KAFDocument.Layer;

public class Timex extends Features{

	public String file;
	public String id;
	public String sentid;
	public Token headid;
	public List<Token> extent;
	public String type;
	public String value;
	public Hashtable<String, String> features;
	public boolean inDataset;

	
	public Timex(String fileName, String sId, Token token, String id) {
		
		this.type = "";
		this.value = "";
		this.file = fileName;
		this.id = id;
    	this.sentid = sId;
    	this.headid = token;
    	this.extent = new ArrayList<Token>();
    	this.extent.add(token);
    	token.timexId = this.id;
    	token.isTimexHead = true;
    	this.inDataset = false;
	}
	
	
	public void addExtent(Token token) {
	
		this.extent.add(token);
		token.timexId = this.id;
	}
	
	
	public void loadFeaturesFromNaf(KAFDocument naf) {
		
		this.features = new Hashtable<String,String>();
		Integer sId = Integer.parseInt(this.sentid);
		List<Annotation> terms = naf.getBySent(Layer.TERMS, sId);

		String extentLemma = "";
		String extentPos = "";				
		Iterator<Token> extenIter = this.extent.iterator();
		while (extenIter.hasNext()) {
			Token extentToken = extenIter.next();
			Integer tIdinSent = extentToken.idinsent;
			if (extentLemma.equals(""))
				extentLemma = getLemma((Term) terms.get(tIdinSent));
			else
				extentLemma = extentLemma + "+" + getLemma((Term) terms.get(tIdinSent));
			if (extentPos.equals(""))
				extentPos = getPos((Term) terms.get(tIdinSent));
			else
				extentPos = extentPos + "+" + getPos((Term) terms.get(tIdinSent));
		}
		this.features.put("lemma", extentLemma);
		this.features.put("pos", extentPos);
		
		Tree constituency = (Tree) naf.getBySent(Layer.CONSTITUENCY, sId).get(0);
		Integer hIdinSent = this.headid.idinsent;
		List<NonTerminal> path = new ArrayList<NonTerminal>();
    	path = getConsituencyPath((Term) terms.get(hIdinSent), constituency.getRoot(), path);
		this.features.put("phrase", getPhrase(path));
	}
}
