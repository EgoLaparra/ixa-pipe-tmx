package ixa.time;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ixa.kaflib.KAFDocument;
import ixa.kaflib.Predicate;
import ixa.kaflib.Term;
import ixa.kaflib.Timex3;
import ixa.kaflib.WF;

public class NAF {


	public NAF () {
		
	}

	
	public static void loadTokenFeatures(String nafDir, Dataset dataset) {
    	
		KAFDocument naf = null;
		String fileName = "";
    	try {
    		Iterator<Token> tokenIter = dataset.tokens.iterator();
    		while (tokenIter.hasNext()) {
    			Token tokenNext = tokenIter.next();
    			if (!fileName.equals(tokenNext.file)) {
    				File nafFile = new File(nafDir + "/" + tokenNext.file + ".naf");
    				fileName = tokenNext.file;
    				if (nafFile.exists()) {
    					naf = KAFDocument.createFromFile(nafFile);
    				} else {
    					naf = null;
    				}
    			}
    			if (naf != null) {
    				tokenNext.loadFeaturesFromNaf(naf);
				}
    		}
        } catch (Exception e) {
			e.printStackTrace();
        }
	} 

	
	public static void loadTokenFeatures(KAFDocument naf, Dataset dataset) {
    	
		Iterator<Token> tokenIter = dataset.tokens.iterator();
		while (tokenIter.hasNext()) {
			Token tokenNext = tokenIter.next();
			tokenNext.loadFeaturesFromNaf(naf);
	    }
	} 

	
	public static void loadEventFeatures(String nafDir, Dataset dataset) {
    	
		KAFDocument naf = null;
		String fileName = "";
    	try {
    		Iterator<Event> eventIter = dataset.events.iterator();
    		while (eventIter.hasNext()) {
    			Event eventNext = eventIter.next();
    			if (!fileName.equals(eventNext.file)) {
    				File nafFile = new File(nafDir + "/" + eventNext.file + ".naf");
    				fileName = eventNext.file;
    				if (nafFile.exists()) {
    					naf = KAFDocument.createFromFile(nafFile);
    				} else {
    					naf = null;
    				}
    			}
    			if (naf != null) {
    				eventNext.loadFeaturesFromNaf(naf);
				}
    		}
        } catch (Exception e) {
			e.printStackTrace();
        }
	}  
	

	public static void loadEventFeatures(KAFDocument naf, Dataset dataset) {
    	
		Iterator<Event> eventIter = dataset.events.iterator();
		while (eventIter.hasNext()) {
			Event eventNext = eventIter.next();
			eventNext.loadFeaturesFromNaf(naf);
		}
	}  
	
	
	public static void loadTimexFeatures(String nafDir, Dataset dataset) {
    	
		KAFDocument naf = null;
		String fileName = "";
    	try {
    		Iterator<Timex> timexIter = dataset.timexs.iterator();
    		while (timexIter.hasNext()) {
    			Timex timexNext = timexIter.next();
    			if (!fileName.equals(timexNext.file)) {
    				File nafFile = new File(nafDir + "/" + timexNext.file + ".naf");
    				fileName = timexNext.file;
    				if (nafFile.exists()) {
    					naf = KAFDocument.createFromFile(nafFile);
    				} else {
    					naf = null;
    				}
    			}
    			if (naf != null) {
    				timexNext.loadFeaturesFromNaf(naf);
				}
    		}
        } catch (Exception e) {
			e.printStackTrace();
        }
	}
	

	public static void loadTimexFeatures(KAFDocument naf, Dataset dataset) {
    	
		Iterator<Timex> timexIter = dataset.timexs.iterator();
		while (timexIter.hasNext()) {
			Timex timexNext = timexIter.next();
			timexNext.loadFeaturesFromNaf(naf);
		}
	}
	
	
	public static void loadTlinkFeatures(String nafDir, Dataset dataset) {

		KAFDocument naf = null;
		String fileName = "";
    	try {
    		Iterator<Tlink> tlinkIter = dataset.tlinks.iterator();
    		while (tlinkIter.hasNext()) {
    			Tlink tlinkNext = tlinkIter.next();
    			if (!fileName.equals(tlinkNext.file)) {
    				File nafFile = new File(nafDir + "/" + tlinkNext.file + ".naf");
    				if (nafFile.exists()) {
    					naf = KAFDocument.createFromFile(nafFile);
    				} else {
    					naf = null;
    				}
    			}
    			if (naf != null) {
    				tlinkNext.loadFeaturesFromNaf(naf);
				}
    		}
        } catch (Exception e) {
			e.printStackTrace();
        }

	}

	
	
	public static void loadTlinkFeatures(KAFDocument naf, Dataset dataset) {
	
		Iterator<Tlink> tlinkIter = dataset.tlinks.iterator();
		while (tlinkIter.hasNext()) {
			Tlink tlinkNext = tlinkIter.next();
			tlinkNext.loadFeaturesFromNaf(naf);
		}
	}
	
	
	public static void loadDocument(KAFDocument naf, Dataset dataset) {
		
		String fileName = "stdIn";
								
		dataset.tokens = new ArrayList<Token>();
		Integer inInSent = 0;
		String prevSent = "";
		Iterator<Term> termIter = naf.getTerms().iterator();
		while (termIter.hasNext()) {
			Term term = termIter.next();
			String sentId = term.getSent().toString();
			String termId = term.getId();
			if (!prevSent.equals(sentId))
				inInSent = 0;
			Token token = new Token(fileName, sentId, termId, inInSent);
			inInSent++;
			dataset.tokens.add(token);
			token.loadFeaturesFromNaf(naf);
			prevSent = sentId;
		}
		
		dataset.events = new ArrayList<Event>();
		Iterator<Predicate> predIter = naf.getPredicates().iterator();
		while (predIter.hasNext()) {
			Predicate predicate = predIter.next();
			String predicateId = predicate.getId();
			Term target = predicate.getSpan().getFirstTarget();
			String sentId = target.getSent().toString();
			String termId = target.getId();
			Token token = dataset.getToken(fileName, sentId, termId);
			Event event = new Event(fileName, sentId, token, predicateId);
			dataset.events.add(event);
			event.loadFeaturesFromNaf(naf);
		}		
	}

	public static void writeTime(KAFDocument naf, Dataset dataset) {
		
//		if (dataset.timexs != null) {	
//			Iterator<Timex> timexIter = dataset.timexs.iterator();
//			while(timexIter.hasNext()) {
//				Timex timex = timexIter.next();
//				Timex3 timexNAF = naf.newTimex3(timex.id, timex.type);
//				List<WF> span = new ArrayList<WF>();
//				Iterator<Token> tokenIter = timex.extent.iterator();
//				while(tokenIter.hasNext()) {
//					Token token = tokenIter.next();
//					Iterator<WF> wfIter = naf.getSentenceTerms(Integer.parseInt(token.sentid)).get(token.idinsent).getWFs().iterator();
//					while(wfIter.hasNext()) {
//						WF wf = wfIter.next();
//						span.add(wf);
//					}
//				}
//				timexNAF.setSpan(KAFDocument.newWFSpan(span));
//			}
//		}
//		
//		if (dataset.tlinks != null) {
//			Iterator<Tlink> tlinkIter = dataset.tlinks.iterator();
//			while(tlinkIter.hasNext()) {
//				Tlink tlink = tlinkIter.next();
//				Timex3 timex3NAF = null;
//				Iterator<Timex3> timex3Iter = naf.getTimeExs().iterator();
//				while(timex3Iter.hasNext()) {
//					Timex3 timex3 = timex3Iter.next();
//					if (timex3.getId().equals(tlink.timex.id)) {
//						timex3NAF = timex3;
//					}
//				}
//				Term termNAF = naf.getSentenceTerms(Integer.parseInt(tlink.event.sentid)).get(tlink.event.headid.idinsent);
//				Predicate predicateNAF = naf.getPredicatesByTerm(termNAF).get(0);
//				if (tlink.from instanceof Event)
//					naf.newTLink(predicateNAF, timex3NAF, tlink.category);
//				else
//					naf.newTLink(timex3NAF, predicateNAF, tlink.category);
//			}
//		}
	}
}
