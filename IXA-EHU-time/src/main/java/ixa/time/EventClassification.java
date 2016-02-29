package ixa.time;

public class EventClassification {

	
	public EventClassification() {
		
   	}
    
	
	public static void main(String[] args) {

		crfClient client = new crfClient();
		Dataset dataset = new Dataset();
		try {
			if (args[0].equals("train")) {
				System.out.println("Training model:");
				System.out.println("\t...loading data");
				dataset.loadTokensFromTempeval2(args[1] + "/base-segmentation.tab");
				dataset.loadEventsFromTempeval2(args[1] + "/event-extents.tab");
				dataset.loadEventAttributesFromTempeval2(args[1] + "/event-attributes.tab");
				dataset.loadEventFeaturesFromNAF(args[2]);
				dataset.createEventClassCRFTrain();
				System.out.println("\t...training model");
				client.train(dataset.crfX, dataset.crfY, args[3]);
				
			} else {
				System.out.println("Classifying:");
				System.out.println("\t...loading data");
				dataset.loadTokensFromTempeval2(args[1] + "/base-segmentation.tab");
				dataset.loadEventsFromTempeval2(args[1] + "/event-extents.tab");
				dataset.loadEventFeaturesFromNAF(args[2]);
				dataset.createEventClassCRFTag();
				System.out.println("\t...tagging");
				dataset.crfY = client.tag(dataset.crfX, args[3]);
				dataset.tagEvents();
				System.out.println("\t...printing output");
				dataset.printTempEval2Events(args[4]);
			}
				
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Finish");
	}
}
