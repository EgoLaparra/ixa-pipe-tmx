package ixa.time;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Iterator;

public class Tempeval2 {

	
	public Tempeval2 () {
		
	}
	
	
	public static void loadTokens(String tempeval2File, Dataset dataset) {
    	
		String prevSent = "";
		Integer idInSent = 0;
    	try {
        	BufferedReader in = new BufferedReader(new FileReader(tempeval2File));
        	String line = in.readLine();
            while (line != null) {
            	String[] fields = line.split("\\t");
            	if (!prevSent.equals(fields[0] + " " + fields[1]))
            			idInSent = 0;
            	Token token = new Token(fields[0], fields[1], fields[2], idInSent);
            	dataset.tokens.add(token);
            	prevSent = fields[0] + " " + fields[1];
            	idInSent++;
            	line = in.readLine();
            }
            in.close();
        } catch (Exception e) {
			e.printStackTrace();
        }    	
	}
	
	
	public static void loadEvents(String tempeval2File, Dataset dataset) {
    	
    	try {
        	BufferedReader in = new BufferedReader(new FileReader(tempeval2File));
        	String line = in.readLine();
            while (line != null) {
            	String[] fields = line.split("\\t");
            	Token token = dataset.getToken(fields[0], fields[1], fields[2]);
            	Event event = dataset.getEvent(fields[0], fields[4]);
            	if (event != null) {
            		event.addExtent(token);
            	} else {
            		Event newEvent = new Event(fields[0], fields[1], token, fields[4]);
            		dataset.events.add(newEvent);
            	}
            	line = in.readLine();
            }
            in.close();
        } catch (Exception e) {
			e.printStackTrace();
        }    	
	}


	public static void loadEventAttributes(String tempeval2File, Dataset dataset) {
    	
    	try {
        	BufferedReader in = new BufferedReader(new FileReader(tempeval2File));
        	String line = in.readLine();
            while (line != null) {
            	String[] fields = line.split("\\t");
            	if (fields.length == 8) {
		        	Event event = dataset.getEvent(fields[0], fields[4]);
		        	if (fields[6].equals("polarity")) {
		        		event.polarity = fields[7];
		        	} else if (fields[6].equals("modality")) {
		        		event.modality = fields[7];
		        	} else if (fields[6].equals("pos")) {
		        		event.pos = fields[7];
		        	} else if (fields[6].equals("tense")) {
		        		event.tense = fields[7];
		        	} else if (fields[6].equals("aspect")) {
		        		event.aspect = fields[7];
		        	} else if (fields[6].equals("class")) {
		        		event.eclass = fields[7];
		        	}
            	}
            	line = in.readLine();
            }
            in.close();
        } catch (Exception e) {
			e.printStackTrace();
        }    	
	}
	
	
	public static void loadTimexs(String tempeval2File, Dataset dataset) {
    	
    	try {
        	BufferedReader in = new BufferedReader(new FileReader(tempeval2File));
        	String line = in.readLine();
            while (line != null) {
            	String[] fields = line.split("\\t");
            	Token token = dataset.getToken(fields[0], fields[1], fields[2]);
            	Timex timex = dataset.getTimex(fields[0], fields[4]);
            	if (timex != null) {
            		timex.addExtent(token);
            	} else {
            		Timex newTimex = new Timex(fields[0], fields[1], token, fields[4]);
            		dataset.timexs.add(newTimex);
            	}
            	line = in.readLine();
            }
            in.close();
        } catch (Exception e) {
			e.printStackTrace();
        }    	
	}


	public static void loadTimexAttributes(String tempeval2File, Dataset dataset) {
    	
    	try {
        	BufferedReader in = new BufferedReader(new FileReader(tempeval2File));
        	String line = in.readLine();
            while (line != null) {
            	String[] fields = line.split("\\t");
            	Timex timex = dataset.getTimex(fields[0], fields[4]);
            	if (fields[6].equals("value")) {
            		timex.value = fields[7];
            	} else if (fields[6].equals("type")) {
            		timex.type = fields[7];
            	}
            	line = in.readLine();
            }
            in.close();
        } catch (Exception e) {
			e.printStackTrace();
        }    	
	}

	
	public static void loadTlinksTimexEvent(String tempeval2File, Dataset dataset) {
    	
    	try {
        	BufferedReader in = new BufferedReader(new FileReader(tempeval2File));
        	String line = in.readLine();
            while (line != null) {
            	String[] fields = line.split("\\t");
            	Event event = dataset.getEvent(fields[0], fields[1]);
            	Timex timex = null;
            	if (event != null) {
            		timex = dataset.getTimex(fields[0], fields[2]);
            		if (timex != null) {
                		Tlink tlink = new Tlink (fields[0], event, timex);
                		tlink.category = fields[3];
                		dataset.tlinks.add(tlink);
            		}
            	} else {
            		event = dataset.getEvent(fields[0], fields[2]);
            		if (event != null) {
            			timex = dataset.getTimex(fields[0], fields[1]);
            			if (timex != null) {
                       		Tlink tlink = new Tlink (fields[0], timex, event);
                    		tlink.category = fields[3];
                    		dataset.tlinks.add(tlink);            				
            			}
            		}
            	}
            	line = in.readLine();
            }
            in.close();
        } catch (Exception e) {
			e.printStackTrace();
        }
	}


	public static void loadUnknownTlinks(String tempeval2File, String nafDir, Dataset dataset) {
    	
    	try {
        	BufferedReader in = new BufferedReader(new FileReader(tempeval2File));
        	String line = in.readLine();
            while (line != null) {
            	String[] fields = line.split("\\t");
				File nafFile = new File(nafDir + "/" + fields[0] + ".naf");
				if (nafFile.exists()) {
	            	Event event = dataset.getEvent(fields[0], fields[1]);
	            	Timex timex = null;
	            	if (event != null) {
	            		timex = dataset.getTimex(fields[0], fields[2]);
	            		if (timex != null) {
	                		Tlink tlink = new Tlink (fields[0], event, timex);
	                		tlink.category = fields[3];
	                		dataset.tlinks.add(tlink);
	            		}
	            	} else {
	            		event = dataset.getEvent(fields[0], fields[2]);
	            		if (event != null) {
	            			timex = dataset.getTimex(fields[0], fields[1]);
	            			if (timex != null) {
	                       		Tlink tlink = new Tlink (fields[0], timex, event);
	                    		tlink.category = fields[3];
	                    		dataset.tlinks.add(tlink);            				
	            			}
	            		}
	            	}
				}
            	line = in.readLine();
            }
            in.close();
        } catch (Exception e) {
			e.printStackTrace();
        }
	}

	
	
	public static void printEventExtents (String fileName, Dataset dataset) {
    	
    	try {
	    	PrintWriter writer = new PrintWriter(fileName, "UTF-8");
	    	Iterator<Event> eventIter = dataset.events.iterator();
    		while (eventIter.hasNext()) {
    			Event event = eventIter.next();
				String outEvent = event.file;
				outEvent = outEvent.concat("\t" + event.sentid);
				Iterator<Token> tokenIter = event.extent.iterator();
				while (tokenIter.hasNext()) {
					Token token = tokenIter.next();
					String outEventToken = outEvent.concat("\t" + token.tokenid);
					outEventToken = outEventToken.concat("\tevent");
					outEventToken = outEventToken.concat("\t" + event.id);
					outEventToken = outEventToken.concat("\t1");
    				writer.write(outEventToken + "\n");
				}
    		}
    		writer.close();
	    	
    	} catch (Exception e) {
			e.printStackTrace();
        }
	}
    		

	public static void printTimexExtents (String fileName, Dataset dataset) {
    	
    	try {
	    	PrintWriter writer = new PrintWriter(fileName, "UTF-8");
	    	Iterator<Timex> timexIter = dataset.timexs.iterator();
    		while (timexIter.hasNext()) {
    			Timex timex = timexIter.next();
				String outTimex = timex.file;
				outTimex = outTimex.concat("\t" + timex.sentid);
				Iterator<Token> tokenIter = timex.extent.iterator();
				while (tokenIter.hasNext()) {
					Token token = tokenIter.next();
					String outTimexToken = outTimex.concat("\t" + token.tokenid);
					outTimexToken = outTimexToken.concat("\ttimex");
					outTimexToken = outTimexToken.concat("\t" + timex.id);
					outTimexToken = outTimexToken.concat("\t1");
    				writer.write(outTimexToken + "\n");
				}
    		}
    		writer.close();
	    	
    	} catch (Exception e) {
			e.printStackTrace();
        }
	}
	
	
	public static void printEvents (String fileName, Dataset dataset) {
    	
    	try {
	    	PrintWriter writer = new PrintWriter(fileName, "UTF-8");
    		Iterator<Event> eventIter = dataset.events.iterator();
    		while (eventIter.hasNext()) {
    			Event event = eventIter.next();
				String outEvent = event.file;
				outEvent = outEvent.concat("\t" + event.sentid);
				outEvent = outEvent.concat("\t" + event.headid.tokenid);
				outEvent = outEvent.concat("\tevent");
				outEvent = outEvent.concat("\t" + event.id);
				outEvent = outEvent.concat("\t1");
    			if (!event.eclass.equals("")) {
    				String outEventClass = outEvent.concat("\tclass");
    				outEventClass = outEventClass.concat("\t" + event.eclass);
    				writer.write(outEventClass + "\n");
    			}
    			if (!event.polarity.equals("")) {
    				String outEventPol = outEvent.concat("\tpolarity");
    				outEventPol = outEventPol.concat("\t" + event.polarity);
    				writer.write(outEventPol + "\n");
    			}
    			if (!event.modality.equals("")) {
    				String outEventMod = outEvent.concat("\tmodality");
    				outEventMod = outEventMod.concat("\t" + event.modality);
    				writer.write(outEventMod + "\n");
    			}
    			if (!event.pos.equals("")) {
    				String outEventPos = outEvent.concat("\tpos");
    				outEventPos = outEventPos.concat("\t" + event.pos);
    				writer.write(outEventPos + "\n");
    			}
    			if (!event.tense.equals("")) {
    				String outEventTense = outEvent.concat("\ttense");
    				outEventTense = outEventTense.concat("\t" + event.tense);
    				writer.write(outEventTense + "\n");
    			}
    			if (!event.aspect.equals("")) {
    				String outEventAspect = outEvent.concat("\taspect");
    				outEventAspect = outEventAspect.concat("\t" + event.aspect);
    				writer.write(outEventAspect + "\n");
    			}
    		}
			writer.close();    	
    	} catch (Exception e) {
			e.printStackTrace();
        }
 
    }
    

	public static void printTimexs (String fileName, Dataset dataset) {
    	
    	try {
	    	PrintWriter writer = new PrintWriter(fileName, "UTF-8");
    		Iterator<Timex> timexIter = dataset.timexs.iterator();
    		while (timexIter.hasNext()) {
    			Timex timex = timexIter.next();	
				String outTimex = timex.file;
				outTimex = outTimex.concat("\t" + timex.sentid);
				outTimex = outTimex.concat("\t" + timex.headid.tokenid);
				outTimex = outTimex.concat("\ttimex");
				outTimex = outTimex.concat("\t" + timex.id);
				outTimex = outTimex.concat("\t1");
				if (!timex.type.equals("")) {
					String outTimexType = outTimex.concat("\ttype");
					outTimexType = outTimexType.concat("\t" + timex.type);
    				writer.write(outTimexType + "\n");
    			}
				if (!timex.value.equals("")) {
					String outTimexVal = outTimex.concat("\tvalue");
					outTimexVal = outTimexVal.concat("\t" + timex.value);
    				writer.write(outTimexVal + "\n");
    			}
    		}
			writer.close();    	
    	} catch (Exception e) {
			e.printStackTrace();
        }
 
    }

	
	public static void printTlinks (String fileName, Dataset dataset) {
    	
    	try {
	    	PrintWriter writer = new PrintWriter(fileName, "UTF-8");
    		Iterator<Tlink> tlinkIter = dataset.tlinks.iterator();
    		while (tlinkIter.hasNext()) {
    			Tlink tlink = tlinkIter.next();	
				String outTlink = tlink.file;
				if (tlink.from instanceof Event) {
					outTlink = outTlink.concat("\t" + tlink.event.id);
					outTlink = outTlink.concat("\t" + tlink.timex.id);
				} else {
					outTlink = outTlink.concat("\t" + tlink.timex.id);
					outTlink = outTlink.concat("\t" + tlink.event.id);
				}
				outTlink = outTlink.concat("\t" + tlink.category);
				writer.write(outTlink + "\n");
    			}
			writer.close();    	
    	} catch (Exception e) {
			e.printStackTrace();
        }
 
	}
	
}
