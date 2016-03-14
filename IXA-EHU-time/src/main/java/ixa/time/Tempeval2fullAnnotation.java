package ixa.time;

public class Tempeval2fullAnnotation {

	
	public Tempeval2fullAnnotation() {
		
   	}
    
	
	public static void main(String[] args) {

		crfClient client = new crfClient();
		Dataset dataset = new Dataset();
		try {
			System.out.println("Annotating time:");
			System.out.println("\t...loading data");
			dataset.loadTokensFromTempeval2(args[0] + "/base-segmentation.tab");
			dataset.loadTokenFeaturesFromNAF(args[1]);		
			
			dataset.createEventRecogCRFTag();
			System.out.println("\t...recogniting events");
			dataset.crfY = client.tag(dataset.crfX, args[2] + "/event-extents");
			dataset.newEventExtents();
			System.out.println("\t...printing event extents");
			dataset.printTempEval2EventExtents(args[3] + "/event-extents.tab");
			
			dataset.createTimexRecogCRFTag();
			System.out.println("\t...recogniting timexs");
			dataset.crfY = client.tag(dataset.crfX, args[2] + "/timex-extents");
			dataset.newTimexExtents();
			System.out.println("\t...printing timex extents");
			dataset.printTempEval2TimexExtents(args[3] + "/timex-extents.tab");
			
			dataset.loadEventFeaturesFromNAF(args[1]);
			dataset.createEventClassCRFTag();
			System.out.println("\t...classifying events");
			dataset.crfY = client.tag(dataset.crfX, args[2] + "/event-attributes");
			dataset.tagEvents();
			System.out.println("\t...printing event classes");
			dataset.printTempEval2Events(args[3] + "/event-attributes.tab");
			
			dataset.loadTimexFeaturesFromNAF(args[1]);
			dataset.createTimexClassCRFTag();
			System.out.println("\t...classifying timexs");
			dataset.crfY = client.tag(dataset.crfX, args[2] + "/timex-attributes");
			dataset.tagTimexs();
			System.out.println("\t...printing timex classes");
			dataset.printTempEval2Timexs(args[3] + "/timex-attributes.tab");
			
			dataset.createUnknownTlinks(args[1]);
			dataset.loadTlinkTimexEventFeaturesFromNAF(args[1]);
			dataset.createTlinkCategorizationCRFTag();
			if (dataset.crfX.size() == 0) {
				System.out.println("\t...no tlinks categorization candidates!!!");
			} else {
				System.out.println("\t...categorizing tlinks");
				dataset.crfY = client.tag(dataset.crfX, args[2] + "/tlinks-timex-event");
				dataset.tagTlinks();
				System.out.println("\t...printing tlinks");
				dataset.printTempEval2Tlinks(args[3] + "/tlinks-timex-event.tab");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Finish");
	}
}
