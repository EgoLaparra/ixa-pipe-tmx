package ixa.time;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import ixa.kaflib.Annotation;
import ixa.kaflib.KAFDocument;
import ixa.kaflib.NonTerminal;
import ixa.kaflib.Term;
import ixa.kaflib.Tree;
import ixa.kaflib.TreeNode;
import ixa.kaflib.KAFDocument.Layer;

public class Dataset {

	public List<Event> events;
	public List<Token> tokens;
	public List<Timex> timexs;
	public List<Tlink> tlinks;
	public List<List<Hashtable<String, String>>> crfX;
	public List<List<String>> crfY;

	
	public Dataset () {
		
	}
	
	
	public Token getToken (String fileName, String sid, String tid) {
		
		Token token = null;
		Iterator<Token> tokenIter = this.tokens.iterator();
		while (tokenIter.hasNext()) {
			Token nextToken = tokenIter.next();
			if (nextToken.file.equals(fileName) && nextToken.sentid.equals(sid) && nextToken.tokenid.equals(tid)) {
				token = nextToken;
			}
		}
		return token;
	}
	

	public Event getEvent (String fileName, String id) {
		
		Event event = null;
		Iterator<Event> eventIter = this.events.iterator();
		while (eventIter.hasNext()) {
			Event nextEvent = eventIter.next();
			if (nextEvent.file.equals(fileName) && nextEvent.id.equals(id)) {
				event = nextEvent;
			}
		}
		return event;
	}
	
	
	public Timex getTimex (String fileName, String id) {
		
		Timex timex = null;
		Iterator<Timex> timexIter = this.timexs.iterator();
		while (timexIter.hasNext()) {
			Timex nextTimex = timexIter.next();
			if (nextTimex.file.equals(fileName) && nextTimex.id.equals(id)) {
				timex = nextTimex;
			}
		}
		return timex;
	}
	
	
	//
	//
	//  Event Recoginition
	//
	//
	
	
	public void createEventRecogCRFTrain () {
		
		this.crfX = new ArrayList<List<Hashtable<String, String>>>();
		this.crfY = new ArrayList<List<String>>();
		String prevFile = "";
		String prevSent = "";
		List<Hashtable<String, String>> tokenListX = new ArrayList<Hashtable<String, String>>();
		List<String> tokenListY = new ArrayList<String>();

		Iterator<Token> tokenIter = tokens.iterator();
		while (tokenIter.hasNext()) {
			Token tokenNext = tokenIter.next();
			if (!(prevFile.equals(tokenNext.file) && prevSent.equals(tokenNext.sentid))) {
				if (tokenListX.size() > 0) {
					this.crfX.add(tokenListX);
					tokenListX = new ArrayList<Hashtable<String, String>>();
				}
				if (tokenListY.size() > 0) {
					this.crfY.add(tokenListY);
					tokenListY = new ArrayList<String>();
				}
				prevFile = tokenNext.file;
				prevSent = tokenNext.sentid;
			}
			if (tokenNext.features != null) {
				tokenNext.inDataset = true;
				tokenListX.add(tokenNext.features);
				 if (tokenNext.eventId.equals(""))
					 tokenListY.add("0");
				 else if (tokenNext.isEventHead)
					 tokenListY.add("b-event");
				 else
					 tokenListY.add("i-event");
			}
		}

		if (tokenListX.size() > 0)
			this.crfX.add(tokenListX);
		if (tokenListY.size() > 0)
			this.crfY.add(tokenListY);
	}
	
	
	public void createEventRecogCRFTag () {
		
		this.crfX = new ArrayList<List<Hashtable<String, String>>>();
		String prevFile = "";
		String prevSent = "";
		List<Hashtable<String, String>> tokenListX = new ArrayList<Hashtable<String, String>>();


		Iterator<Token> tokenIter = tokens.iterator();
		while (tokenIter.hasNext()) {
			Token tokenNext = tokenIter.next();
			if (!(prevFile.equals(tokenNext.file) && prevSent.equals(tokenNext.sentid))) {
				if (tokenListX.size() > 0)
					this.crfX.add(tokenListX);
				prevFile = tokenNext.file;
				prevSent = tokenNext.sentid;
				tokenListX = new ArrayList<Hashtable<String, String>>();
			}
			
			if (tokenNext.features != null) {
				tokenNext.inDataset = true;
				tokenListX.add(tokenNext.features);
			}
		}
		if (tokenListX.size() > 0)
			this.crfX.add(tokenListX);
	}
	
	
	public void newEventExtents () {
	
		this.events = new ArrayList<Event>();
		Event newEvent = null;
		Integer eIdx = 0;
		int tokenIdx = 0;
		Iterator<List<String>> yIter = this.crfY.iterator();
		while (yIter.hasNext()) {
			Iterator<String> tagIter = yIter.next().iterator();
			while (tagIter.hasNext()) {
				String tag = tagIter.next();
				while (!this.tokens.get(tokenIdx).inDataset)
					tokenIdx++;
				Token token = this.tokens.get(tokenIdx);
				if (tag.equals("b-event")) {
					eIdx++;
					newEvent = new Event(token.file, token.sentid, token, "e" + eIdx.toString());
		    		this.events.add(newEvent);
				} else if (tag.equals("i-event")) {
		    		newEvent.addExtent(this.tokens.get(tokenIdx));
				}
				tokenIdx++;
			}
		}
	}
	

	//
	//
	//  Timex Recoginition
	//
	//
	
	
	public void createTimexRecogCRFTrain () {
		
		this.crfX = new ArrayList<List<Hashtable<String, String>>>();
		this.crfY = new ArrayList<List<String>>();
		String prevFile = "";
		String prevSent = "";
		List<Hashtable<String, String>> tokenListX = new ArrayList<Hashtable<String, String>>();
		List<String> tokenListY = new ArrayList<String>();

		Iterator<Token> tokenIter = tokens.iterator();
		while (tokenIter.hasNext()) {
			Token tokenNext = tokenIter.next();
			if (!(prevFile.equals(tokenNext.file) && prevSent.equals(tokenNext.sentid))) {
				if (tokenListX.size() > 0) {
					this.crfX.add(tokenListX);
					tokenListX = new ArrayList<Hashtable<String, String>>();
				}
				if (tokenListY.size() > 0) {
					this.crfY.add(tokenListY);
					tokenListY = new ArrayList<String>();
				}
				prevFile = tokenNext.file;
				prevSent = tokenNext.sentid;
			}
			if (tokenNext.features != null) {
				tokenNext.inDataset = true;
				tokenListX.add(tokenNext.features);
				 if (tokenNext.timexId.equals(""))
					 tokenListY.add("0");
				 else if (tokenNext.isTimexHead)
					 tokenListY.add("b-timex");
				 else
					 tokenListY.add("i-timex");
			}
		}

		if (tokenListX.size() > 0)
			this.crfX.add(tokenListX);
		if (tokenListY.size() > 0)
			this.crfY.add(tokenListY);
	}
	
	
	public void createTimexRecogCRFTag () {
		
		this.crfX = new ArrayList<List<Hashtable<String, String>>>();
		String prevFile = "";
		String prevSent = "";
		List<Hashtable<String, String>> tokenListX = new ArrayList<Hashtable<String, String>>();


		Iterator<Token> tokenIter = tokens.iterator();
		while (tokenIter.hasNext()) {
			Token tokenNext = tokenIter.next();
			if (!(prevFile.equals(tokenNext.file) && prevSent.equals(tokenNext.sentid))) {
				if (tokenListX.size() > 0)
					this.crfX.add(tokenListX);
				prevFile = tokenNext.file;
				prevSent = tokenNext.sentid;
				tokenListX = new ArrayList<Hashtable<String, String>>();
			}
			
			if (tokenNext.features != null) {
				tokenNext.inDataset = true;
				tokenListX.add(tokenNext.features);
			}
		}
		if (tokenListX.size() > 0)
			this.crfX.add(tokenListX);
	}
	
	
	public void newTimexExtents () {
	
		this.timexs = new ArrayList<Timex>();
		Timex newTimex = null;
		Integer tIdx = 0;
		int tokenIdx = 0;
		Iterator<List<String>> yIter = this.crfY.iterator();
		while (yIter.hasNext()) {
			Iterator<String> tagIter = yIter.next().iterator();
			while (tagIter.hasNext()) {
				String tag = tagIter.next();
				while (!this.tokens.get(tokenIdx).inDataset)
					tokenIdx++;
				Token token = this.tokens.get(tokenIdx);
				if (tag.equals("b-timex")) {
					tIdx++;
					newTimex = new Timex(token.file, token.sentid, token, "tmx" + tIdx.toString());
		    		this.timexs.add(newTimex);
				} else if (tag.equals("i-timex")) {
		    		newTimex.addExtent(this.tokens.get(tokenIdx));
				}
				tokenIdx++;
			}
		}
	}
	
	
	//
	//
	//  Event Classification
	//
	//
	
	
	public void createEventClassCRFTrain () {
		
		this.crfX = new ArrayList<List<Hashtable<String, String>>>();
		this.crfY = new ArrayList<List<String>>();
		String prevFile = "";
		String prevSent = "";
		List<Hashtable<String, String>> eventListX = new ArrayList<Hashtable<String, String>>();
		List<String> eventListY = new ArrayList<String>();

		Iterator<Event> eventIter = events.iterator();
		while (eventIter.hasNext()) {
			Event eventNext = eventIter.next();
			if (!(prevFile.equals(eventNext.file) && prevSent.equals(eventNext.sentid))) {
				if (eventListX.size() > 0) {
					this.crfX.add(eventListX);
					eventListX = new ArrayList<Hashtable<String, String>>();
				}
				if (eventListY.size() > 0) {
					this.crfY.add(eventListY);
					eventListY = new ArrayList<String>();
				}
				prevFile = eventNext.file;
				prevSent = eventNext.sentid;
			}
			if (eventNext.features != null && !eventNext.eclass.equals("")) {
				eventNext.inDataset = true;
				eventListX.add(eventNext.features);
				eventListY.add(eventNext.eclass);
			}
		}

		if (eventListX.size() > 0)
			this.crfX.add(eventListX);
		if (eventListY.size() > 0)
			this.crfY.add(eventListY);
	}
	
	
	public void createEventClassCRFTag () {
		
		this.crfX = new ArrayList<List<Hashtable<String, String>>>();
		String prevFile = "";
		String prevSent = "";
		List<Hashtable<String, String>> eventListX = new ArrayList<Hashtable<String, String>>();


		Iterator<Event> eventIter = events.iterator();
		while (eventIter.hasNext()) {
			Event eventNext = eventIter.next();
			if (!(prevFile.equals(eventNext.file) && prevSent.equals(eventNext.sentid))) {
				if (eventListX.size() > 0)
					this.crfX.add(eventListX);
				prevFile = eventNext.file;
				prevSent = eventNext.sentid;
				eventListX = new ArrayList<Hashtable<String, String>>();
			}
			
			if (eventNext.features != null) {
				eventNext.inDataset = true;
				eventListX.add(eventNext.features);
			}
		}
		if (eventListX.size() > 0)
			this.crfX.add(eventListX);
	}
	
	
	public void tagEvents () {
	
		int eventIdx = 0;
		Iterator<List<String>> yIter = this.crfY.iterator();
		while (yIter.hasNext()) {
			Iterator<String> tagIter = yIter.next().iterator();
			while (tagIter.hasNext()) {
				String tag = tagIter.next();
				while (!this.events.get(eventIdx).inDataset)
					eventIdx++;
				this.events.get(eventIdx).eclass = tag;
				eventIdx++;
			}
		}
	}
	

	//
	//
	//  Timex Classification
	//
	//
	
	
	public void createTimexClassCRFTrain () {
		
		this.crfX = new ArrayList<List<Hashtable<String, String>>>();
		this.crfY = new ArrayList<List<String>>();
		String prevFile = "";
		String prevSent = "";
		List<Hashtable<String, String>> timexListX = new ArrayList<Hashtable<String, String>>();
		List<String> timexListY = new ArrayList<String>();

		Iterator<Timex> timexIter = timexs.iterator();
		while (timexIter.hasNext()) {
			Timex timexNext = timexIter.next();
			if (!(prevFile.equals(timexNext.file) && prevSent.equals(timexNext.sentid))) {
				if (timexListX.size() > 0) {
					this.crfX.add(timexListX);
					timexListX = new ArrayList<Hashtable<String, String>>();
				}
				if (timexListY.size() > 0) {
					this.crfY.add(timexListY);
					timexListY = new ArrayList<String>();
				}
				prevFile = timexNext.file;
				prevSent = timexNext.sentid;
			}
			if (timexNext.features != null && !timexNext.type.equals("")) {
				timexNext.inDataset = true;
				timexListX.add(timexNext.features);
				timexListY.add(timexNext.type);
			}
		}

		if (timexListX.size() > 0)
			this.crfX.add(timexListX);
		if (timexListY.size() > 0)
			this.crfY.add(timexListY);
	}
	
	
	public void createTimexClassCRFTag () {
		
		this.crfX = new ArrayList<List<Hashtable<String, String>>>();
		String prevFile = "";
		String prevSent = "";
		List<Hashtable<String, String>> timexListX = new ArrayList<Hashtable<String, String>>();


		Iterator<Timex> timexIter = timexs.iterator();
		while (timexIter.hasNext()) {
			Timex timexNext = timexIter.next();
			if (!(prevFile.equals(timexNext.file) && prevSent.equals(timexNext.sentid))) {
				if (timexListX.size() > 0)
					this.crfX.add(timexListX);
				prevFile = timexNext.file;
				prevSent = timexNext.sentid;
				timexListX = new ArrayList<Hashtable<String, String>>();
			}
			
			if (timexNext.features != null) {
				timexNext.inDataset = true;
				timexListX.add(timexNext.features);
			}
		}
		if (timexListX.size() > 0)
			this.crfX.add(timexListX);
	}
	
	
	public void tagTimexs () {
	
		int timexIdx = 0;
		Iterator<List<String>> yIter = this.crfY.iterator();
		while (yIter.hasNext()) {
			Iterator<String> tagIter = yIter.next().iterator();
			while (tagIter.hasNext()) {
				String tag = tagIter.next();
				while (!this.timexs.get(timexIdx).inDataset)
					timexIdx++;
				this.timexs.get(timexIdx).type = tag;
				timexIdx++;
			}
		}
	}


	//
	//
	//  Tlinks Categorization
	//
	//
	
	
	public void createUnknownTlinks (String nafDir) {

		tlinks = new ArrayList<Tlink>();
		Iterator<Event> eventIter = this.events.iterator();
		while (eventIter.hasNext()) {
			Event event = eventIter.next();
			Iterator<Timex> timexIter = this.timexs.iterator();
			while (timexIter.hasNext()) {
				Timex timex = timexIter.next();
				File nafFile = new File(nafDir + "/" + timex.file + ".naf");
				if (nafFile.exists()) {
					if (event.file.equals(timex.file) && event.sentid.equals(timex.sentid)) {
						Tlink tlink = new Tlink(event.file, event, timex);
						this.tlinks.add(tlink);
					}
				}
			}
		}
	}
	

	public void createUnknownTlinks (KAFDocument naf) {

		this.tlinks = new ArrayList<Tlink>();
		Iterator<Event> eventIter = this.events.iterator();
		while (eventIter.hasNext()) {
			Event event = eventIter.next();
			Iterator<Timex> timexIter = this.timexs.iterator();
			while (timexIter.hasNext()) {
				Timex timex = timexIter.next();
				if (event.file.equals(timex.file) && event.sentid.equals(timex.sentid)) {
					Integer sId = Integer.parseInt(event.sentid);
					Integer eventIdinSent = event.headid.idinsent;
					Integer timexIdinSent = timex.headid.idinsent;
					
					List<Annotation> terms = naf.getBySent(Layer.TERMS, sId);
					Tree constituency = (Tree) naf.getBySent(Layer.CONSTITUENCY, sId).get(0);
					List<NonTerminal> eventPath = new ArrayList<NonTerminal>();
			    	eventPath = event.getConsituencyPath((Term) terms.get(eventIdinSent), constituency.getRoot(), eventPath);
			    	List<NonTerminal> timexPath = new ArrayList<NonTerminal>();
			    	timexPath = timex.getConsituencyPath((Term) terms.get(timexIdinSent), constituency.getRoot(), timexPath);

			    	
			    	if(isTlink(eventPath,timexPath)) {
				    	Tlink tlink = new Tlink(event.file, event, timex);
				    	this.tlinks.add(tlink);
			    	}
				}
			}
		}
	}

	
	public boolean isTlink (List<NonTerminal> eventPath, List<NonTerminal> timexPath) {
	
			boolean istlink = false;
			
			NonTerminal eventNT = eventPath.get(eventPath.size() - 1);
		   	List<NonTerminal> retainedList = new ArrayList<NonTerminal>();
	    	retainedList.addAll(eventPath);
	    	retainedList.retainAll(timexPath);
	    	Iterator<NonTerminal> ntIter = retainedList.iterator();
	    	while(ntIter.hasNext()) {
	    		NonTerminal nt = ntIter.next();
	    		if (nt.getLabel().equals("NP") || nt.getLabel().equals("SN")) {
	    			istlink = true;
	    		}
	    	}
	    	
	    	Iterator<TreeNode> childIter = retainedList.get(retainedList.size() - 1).getChildren().iterator();
    		while(childIter.hasNext()) {
    			TreeNode child = childIter.next();
    			if (!child.isTerminal()) {
    				NonTerminal childNT = (NonTerminal) child;
    				if (childNT.equals(eventNT) && childNT.getHead()) {
    					istlink = true;
					} else if (childNT.getHead()) {
						childIter = childNT.getChildren().iterator();
					}
    			}
    		}
			return istlink;
	}
	
	public void createTlinkCategorizationCRFTrain () {
		
		this.crfX = new ArrayList<List<Hashtable<String, String>>>();
		this.crfY = new ArrayList<List<String>>();
		String prevFile = "";
		String prevSent = "";
		List<Hashtable<String, String>> tlinkListX = new ArrayList<Hashtable<String, String>>();
		List<String> tlinkListY = new ArrayList<String>();

		Iterator<Tlink> tlinkIter = this.tlinks.iterator();
		while (tlinkIter.hasNext()) {
			Tlink tlinkNext = tlinkIter.next();
			if (!(prevFile.equals(tlinkNext.file) && prevSent.equals(tlinkNext.event.sentid))) {
				if (tlinkListX.size() > 0) {
					this.crfX.add(tlinkListX);
					tlinkListX = new ArrayList<Hashtable<String, String>>();
				}
				if (tlinkListY.size() > 0) {
					this.crfY.add(tlinkListY);
					tlinkListY = new ArrayList<String>();
				}
				prevFile = tlinkNext.file;
				prevSent = tlinkNext.event.sentid;
			}
			if (tlinkNext.features != null && !tlinkNext.category.equals("")) {
				tlinkNext.inDataset = true;
				tlinkListX.add(tlinkNext.features);
				tlinkListY.add(tlinkNext.category);
			}
		}

		if (tlinkListX.size() > 0)
			this.crfX.add(tlinkListX);
		if (tlinkListY.size() > 0)
			this.crfY.add(tlinkListY);
	}
	
	
	public void createTlinkCategorizationCRFTag () {
		
		this.crfX = new ArrayList<List<Hashtable<String, String>>>();
		String prevFile = "";
		String prevSent = "";
		List<Hashtable<String, String>> tlinkListX = new ArrayList<Hashtable<String, String>>();

		Iterator<Tlink> tlinkIter = this.tlinks.iterator();
		while (tlinkIter.hasNext()) {
			Tlink tlinkNext = tlinkIter.next();
			if (!(prevFile.equals(tlinkNext.file) && prevSent.equals(tlinkNext.event.sentid))) {
				if (tlinkListX.size() > 0) {
					this.crfX.add(tlinkListX);
				}
				prevFile = tlinkNext.file;
				prevSent = tlinkNext.event.sentid;
				tlinkListX = new ArrayList<Hashtable<String, String>>();
			}

			if (tlinkNext.features != null) {
				tlinkNext.inDataset = true;
				tlinkListX.add(tlinkNext.features);
			}
		}
		if (tlinkListX.size() > 0)
			this.crfX.add(tlinkListX);
	}
	
	
	public void tagTlinks () {
	
		int tlinkIdx = 0;
		Iterator<List<String>> yIter = this.crfY.iterator();
		while (yIter.hasNext()) {
			Iterator<String> tagIter = yIter.next().iterator();
			while (tagIter.hasNext()) {
				String tag = tagIter.next();
				while (!this.tlinks.get(tlinkIdx).inDataset)
					tlinkIdx++;
				this.tlinks.get(tlinkIdx).category = tag;
				tlinkIdx++;
			}
		}
	}

	
	//
	//
	//  Tempeval2 In&Out
	//
	//
	
	public void loadTokensFromTempeval2(String tempeval2File) {
    	
		this.tokens = new ArrayList<Token>();
		Tempeval2.loadTokens(tempeval2File, this);
	}
	
	public void loadEventsFromTempeval2(String tempeval2File) {
    	
		this.events = new ArrayList<Event>();
		Tempeval2.loadEvents(tempeval2File, this);
	}

	public void loadEventAttributesFromTempeval2(String tempeval2File) {
    	
		Tempeval2.loadEventAttributes(tempeval2File, this);
	}
	
	public void loadTimexsFromTempeval2(String tempeval2File) {
	
		this.timexs = new ArrayList<Timex>();
		Tempeval2.loadTimexs(tempeval2File, this);
	}
	
	public void loadTimexAttributesFromTempeval2(String tempeval2File) {
    	
		Tempeval2.loadTimexAttributes(tempeval2File, this);
	}
	
	public void loadTlinksTimexEventFromTempeval2(String tempeval2File) {
		
		this.tlinks = new ArrayList<Tlink>();
		Tempeval2.loadTlinksTimexEvent(tempeval2File, this);
	}

	public void loadUnknownTlinksFromTempeval2(String tempeval2File, String nafDir) {
		
		this.tlinks = new ArrayList<Tlink>();
		Tempeval2.loadUnknownTlinks(tempeval2File, nafDir,this);
	}
	
	public void printTempEval2EventExtents(String outputFile) {
    	
		Tempeval2.printEventExtents(outputFile, this);
	}

	public void printTempEval2TimexExtents(String outputFile) {
    	
		Tempeval2.printTimexExtents(outputFile, this);
	}
	
	public void printTempEval2Events(String outputFile) {
    	
		Tempeval2.printEvents(outputFile, this);
	}

	public void printTempEval2Timexs(String outputFile) {
    	
		Tempeval2.printTimexs(outputFile, this);
	}

	public void printTempEval2Tlinks(String outputFile) {
    	
		Tempeval2.printTlinks(outputFile, this);
	}
	
	
	//
	//
	//  NAF In&Out
	//
	//
	
	public void loadTokenFeaturesFromNAF(String nafDir) {
    	
		NAF.loadTokenFeatures(nafDir, this);
	}
	
	public void loadTokenFeaturesFromNAF(KAFDocument naf) {
    	
		NAF.loadTokenFeatures(naf, this);
	}
	
	public void loadEventFeaturesFromNAF(String nafDir) {
    	
		NAF.loadEventFeatures(nafDir, this);
	}

	public void loadEventFeaturesFromNAF(KAFDocument naf) {
    	
		NAF.loadEventFeatures(naf, this);
	}

	
	public void loadTimexFeaturesFromNAF(String nafDir) {
    	
		NAF.loadTimexFeatures(nafDir, this);
	}

	public void loadTimexFeaturesFromNAF(KAFDocument naf) {
    	
		NAF.loadTimexFeatures(naf, this);
	}
	
	public void loadTlinkTimexEventFeaturesFromNAF(String nafDir) {
		
		NAF.loadTlinkTimexEventFeatures(nafDir, this);
	}
	
	public void loadTlinkTimexEventFeaturesFromNAF(KAFDocument naf) {
		
		NAF.loadTlinkTimexEventFeatures(naf, this);
	}

	public void loadNAFDocument(KAFDocument naf) {
		
		NAF.loadDocument(naf, this);
	}
	
	public void writeTimeInNAF(KAFDocument naf) {
		
		NAF.writeTime(naf, this);
	}

}