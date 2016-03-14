package ixa.time;

public class Tempeval2fullTraining {

	
	public Tempeval2fullTraining() {
		
   	}
    
	
	public static void main(String[] args) {

		crfClient client = new crfClient();
		Dataset dataset = new Dataset();
		try {
			System.out.println("Training model:");
			System.out.println("\t...loading data");
			dataset.loadTokensFromTempeval2(args[1] + "/base-segmentation.tab");
			
			dataset.loadEventsFromTempeval2(args[1] + "/event-extents.tab");
			dataset.loadTokenFeaturesFromNAF(args[2]);
			dataset.createEventRecogCRFTrain();
			System.out.println("\t...training event recognition model");
			client.train(dataset.crfX, dataset.crfY, args[3] + "/event-extents");

			dataset.loadTimexsFromTempeval2(args[1] + "/timex-extents.tab");
			dataset.createTimexRecogCRFTrain();
			System.out.println("\t...training timex recognition model");
			client.train(dataset.crfX, dataset.crfY, args[3] + "/timex-extents");

			dataset.loadEventAttributesFromTempeval2(args[1] + "/event-attributes.tab");
			dataset.loadEventFeaturesFromNAF(args[2]);
			dataset.createEventClassCRFTrain();
			System.out.println("\t...training event classification model");
			client.train(dataset.crfX, dataset.crfY, args[3] + "/event-attributes");

			dataset.loadTimexAttributesFromTempeval2(args[1] + "/timex-attributes.tab");
			dataset.loadTimexFeaturesFromNAF(args[2]);
			dataset.createTimexClassCRFTrain();
			System.out.println("\t...training timex classification model");
			client.train(dataset.crfX, dataset.crfY, args[3] + "/timex-attributes");
			
			dataset.loadTlinksFromTempeval2(args[1] + "/tlinks-timex-event.tab");
			dataset.loadTlinkFeaturesFromNAF(args[2]);
			dataset.createTlinkCategorizationCRFTrain(Timex.class, Event.class);
			System.out.println("\t...training tlink categorization model");
			client.train(dataset.crfX, dataset.crfY, args[3] + "/tlinks-timex-event");

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Finish");
	}
}
