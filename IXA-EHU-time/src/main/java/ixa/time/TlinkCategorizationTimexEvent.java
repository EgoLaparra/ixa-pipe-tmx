package ixa.time;

public class TlinkCategorizationTimexEvent {

	
	public TlinkCategorizationTimexEvent() {
		
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
				dataset.loadTimexsFromTempeval2(args[1] + "/timex-extents.tab");
				dataset.loadEventAttributesFromTempeval2(args[1] + "/event-attributes.tab");
				dataset.loadTimexAttributesFromTempeval2(args[1] + "/timex-attributes.tab");
				dataset.loadTlinksTimexEventFromTempeval2(args[1] + "/tlinks-timex-event.tab");
				dataset.loadTlinkTimexEventFeaturesFromNAF(args[2]);
				dataset.createTlinkCategorizationCRFTrain();
				System.out.println("\t...training model");
				client.train(dataset.crfX, dataset.crfY, args[3]);
				
			} else {
				System.out.println("Classifying:");
				System.out.println("\t...loading data");
				dataset.loadTokensFromTempeval2(args[1] + "/base-segmentation.tab");
				dataset.loadEventsFromTempeval2(args[1] + "/event-extents.tab");
				dataset.loadTimexsFromTempeval2(args[1] + "/timex-extents.tab");
				dataset.loadEventAttributesFromTempeval2(args[1] + "/event-attributes.tab");
				dataset.loadTimexAttributesFromTempeval2(args[1] + "/timex-attributes.tab");
				dataset.loadUnknownTlinksFromTempeval2(args[1] + "/tlinks-timex-event.tab", args[2]);
				//dataset.createUnknownTlinks(args[2]);
				dataset.loadTlinkTimexEventFeaturesFromNAF(args[2]);
				dataset.createTlinkCategorizationCRFTag();
				System.out.println("\t...tagging");
				dataset.crfY = client.tag(dataset.crfX, args[3]);
				dataset.tagTlinks();
				System.out.println("\t...printing output");
				dataset.printTempEval2Tlinks(args[4]);
			}
				
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Finish");
	}
}
