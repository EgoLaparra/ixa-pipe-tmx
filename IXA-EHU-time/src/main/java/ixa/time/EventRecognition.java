package ixa.time;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import weka.classifiers.functions.Logistic;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.core.converters.ArffSaver;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;
import ixa.kaflib.*;
import ixa.kaflib.KAFDocument.Layer;

public class EventRecognition {
	// Classes
    public static String LABEL_B = "b-event";
    public static String LABEL_I = "i-event";
    public static String LABEL_0 = "0";

    private Logistic classifier;
    private StringToWordVector filter;
    private Instances datasetOriginal;
    private Instances datasetFiltered;
    
    public EventRecognition() {
    }

    public void init() {
        this.classifier = new Logistic();
        //this.classifier.setRidge();
    }
    
    private Instances buildDataset() {
        // List of classes
        FastVector clases = new FastVector(2);
        clases.addElement(LABEL_B);
        clases.addElement(LABEL_I);
        clases.addElement(LABEL_0);

        // List of features
        FastVector features = new FastVector(2);
        features.addElement(new Attribute("EVENT", clases));
        features.addElement(new Attribute("LEMMA", (FastVector) null));

        Instances dataset = new Instances("eventRecognition", features, 100);
        dataset.setClass(dataset.attribute("EVENT"));

        return (dataset);
    }

    
    private Instance createInstance(EventRecognitionInstance inputInstance) {
    	// Create instance
    	Instance instance = new Instance(datasetOriginal.numAttributes());
    	instance.setDataset(datasetOriginal);
    	instance.setValue(datasetOriginal.attribute("LEMMA"), inputInstance.lemma);
    	instance.setClassValue(inputInstance.label);
    	
    	return instance;
    }
    
    private void loadInstances(List<EventRecognitionInstance> input) {
    	// Load input instance in dataset
		Iterator<EventRecognitionInstance> inputIterator = input.iterator();
		while (inputIterator.hasNext()) {
			EventRecognitionInstance inputInstance = inputIterator.next();
			Instance instance = createInstance(inputInstance);
			datasetOriginal.add(instance);
		}
    }
       
    private StringToWordVector buildFilter(Instances dataset) throws Exception {
    	// Create the filter
        StringToWordVector tmpFilter = new StringToWordVector();
        tmpFilter.setInputFormat(dataset);
        int[] idxFeatures = {1}; // Features to be filtered
        tmpFilter.setAttributeIndicesArray(idxFeatures);     
        return (tmpFilter);
    }
    
    
    public void train(List<EventRecognitionInstance> input, String modelPath) throws Exception {
    	// Create and load dataset
    	datasetOriginal = buildDataset();
    	loadInstances(input);   	
        System.out.println("\t... train set loaded");
        
        // Transform text into vectors
        filter = buildFilter(datasetOriginal);
        datasetFiltered = Filter.useFilter(datasetOriginal, filter);
        datasetFiltered.setClass(datasetFiltered.attribute("EVENT"));
        System.out.println("\t... train set filtered");
        
        // Train classifier
        classifier.buildClassifier(datasetFiltered);
        System.out.println("\t... classifier trained");
        
        // Serialize trained model and filter 
        ObjectOutputStream coos = new ObjectOutputStream(
                new FileOutputStream(modelPath));
        coos.writeObject(classifier);
        coos.flush();
        coos.close();
        ObjectOutputStream foos = new ObjectOutputStream(
                new FileOutputStream(modelPath + ".ftr"));
        foos.writeObject(filter);
        foos.flush();
        foos.close();
        System.out.println("\t... model saved in " + modelPath);

        // Free instances
        datasetOriginal.delete();
        datasetFiltered.delete();
    }
    
    
    public void classify (List<EventRecognitionInstance> input, String modelPath) {
    	// Classify a list of instances
    	       
		try {
			// Deserialize model and filter 
	        ObjectInputStream cois;
			cois = new ObjectInputStream(
						new FileInputStream(modelPath));
			classifier = (Logistic) cois.readObject();
			cois.close();
			ObjectInputStream fois = new ObjectInputStream(
					new FileInputStream(modelPath + ".ftr"));
			filter = (StringToWordVector) fois.readObject();
			fois.close();
			System.out.println("\t... model loaded");
        
			//	Create Dataset
			datasetOriginal = buildDataset();
			loadInstances(input);   	
			System.out.println("\t... test set loaded");

			// Process instances one by one
			Iterator<EventRecognitionInstance> inputIterator = input.iterator();
			while (inputIterator.hasNext()) {
				EventRecognitionInstance inputInstance = inputIterator.next();
				Instance instanceOriginal = createInstance(inputInstance);

				// Filter instance
				filter.input(instanceOriginal);
				Instance instanceFiltered = filter.output();
			
				// Classify instance and get label
				int idxEvent = (int) classifier.classifyInstance(instanceFiltered);
				String Event = datasetOriginal.attribute("EVENT").value(idxEvent);
				inputInstance.label = Event;
				System.out.println(inputInstance.file + " " + inputInstance.sentid + " " + inputInstance.tokenid + " " + inputInstance.label);
				}
			
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    }	
    
    
    private EventRecognitionInstance getInstance (List<EventRecognitionInstance> instances, String key) {
     	Iterator<EventRecognitionInstance> instanceIterator = instances.iterator();
    	while (instanceIterator.hasNext()) {
    		EventRecognitionInstance instance = instanceIterator.next();
    		String ikey = instance.file + " " + instance.sentid + " " + instance.tokenid;
    		if (ikey.equals(key)) {
    			return instance;
    		}
    	}
    	return null;
    }
    
    private void labelInstances(String eventExtents, List<EventRecognitionInstance> instances) {
    	// Create a list of instances with labels from event-extents file
    	
    	try {
            String previous = "";
        	BufferedReader in = new BufferedReader(new FileReader(eventExtents));
            String line = in.readLine();
            while (line != null) {
            	String[] fields = line.split("\\t");
            	String label = "";
            	if (!(previous.equals(fields[0] + " " + fields[4]))){
            		label = "b-" + fields[3];
            	} else {
            		label = "i-" + fields[3];
            	}
            	String key = fields[0] + ".naf " + fields[1] + " " + fields[2];
            	EventRecognitionInstance instance = getInstance(instances, key);
            	if (instance != null){
            		instance.label = label;
            	}
            	previous = fields[0] + " " + fields[4];
            	line = in.readLine();
            }
            in.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
    

    
    private void getInstancesFromNAF(KAFDocument naf, List<EventRecognitionInstance> instances, String fileName) {
    	List<Term> terms = naf.getTerms();
    	Iterator<Term> termIterator = terms.iterator();
		while (termIterator.hasNext()) {
			Term term = termIterator.next();
        	EventRecognitionInstance newinstance = new EventRecognitionInstance();
        	newinstance.file = fileName;
        	newinstance.sentid = term.getSent().toString();
        	Integer tokenIdx = (Integer) naf.getBySent(Layer.TERMS, term.getSent()).indexOf(term);
        	newinstance.tokenid = tokenIdx.toString(); 
        	newinstance.lemma = term.getLemma();
        	naf.getBySent(Layer.CONSTITUENCY, term.getSent());
        	instances.add(newinstance);
		}
    }
    
    
    private void getInstancesFromDir(String nafDirectory, List<EventRecognitionInstance> instances) {
    	// Create a list of instances from naf files
        
    	try {
    		File fDir = new File(nafDirectory);
    		File[] files = fDir.listFiles();
    		for (int f=0;f<files.length;f++){
    			KAFDocument naf = KAFDocument.createFromFile(files[f]);
    			getInstancesFromNAF(naf, instances, files[f].getName());
    		}
    	} catch (IOException e) {
    		e.printStackTrace();
		}
    }
    
    
    
	public static void main(String[] args) {
		EventRecognition ev = new EventRecognition();
		List<EventRecognitionInstance> input = new ArrayList<EventRecognitionInstance>();
		ev.getInstancesFromDir(args[1], input);
		ev.labelInstances(args[0], input);
		try {
			ev.init();
			//ev.train(input, args[2]);
//			ev.classify(input, args[2]);
			//ev.getInstancesFromDir(args[1]);
			//System.out.println(input.get(0).label);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
}
