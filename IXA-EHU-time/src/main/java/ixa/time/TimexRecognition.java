package ixa.time;

public class TimexRecognition {

	
	public TimexRecognition() {
		
   	}
    
	
	public static void main(String[] args) {

		crfClient client = new crfClient();
		Dataset dataset = new Dataset();
		try {
			if (args[0].equals("train")) {
				System.out.println("Training model:");
				System.out.println("\t...loading data");
				dataset.loadTokensFromTempeval2(args[1] + "/base-segmentation.tab");
				dataset.loadTimexsFromTempeval2(args[1] + "/timex-extents.tab");
				dataset.loadTokenFeaturesFromNAF(args[2]);
				dataset.createTimexRecogCRFTrain();
				System.out.println("\t...training model");
				client.train(dataset.crfX, dataset.crfY, args[3]);
				
			} else {
				System.out.println("Classifying:");
				System.out.println("\t...loading data");
				dataset.loadTokensFromTempeval2(args[1] + "/base-segmentation.tab");
				dataset.loadTokenFeaturesFromNAF(args[2]);
				dataset.createTimexRecogCRFTag();
				System.out.println("\t...tagging");
				dataset.crfY = client.tag(dataset.crfX, args[3]);
				dataset.newTimexExtents();
				System.out.println("\t...printing output");
				dataset.printTempEval2TimexExtents(args[4]);
			}
				
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Finish");
	}
}
