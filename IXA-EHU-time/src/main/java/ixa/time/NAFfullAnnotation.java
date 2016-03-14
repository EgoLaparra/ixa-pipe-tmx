package ixa.time;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;

import ixa.kaflib.KAFDocument;

public class NAFfullAnnotation {

	
	public NAFfullAnnotation() {
		
   	}
    
	
	public static void main(String[] args) {

		String lang = args[0];
		crfClient client = new crfClient();
		Dataset dataset = new Dataset();
		try {
			if (lang.equals("en")) {
				lang = "english";
			} else if (lang.equals("es")) {
				lang = "spanish";
			} else {
				throw new Exception("Invalid language");
			}
			//BufferedReader stdInReader = new BufferedReader(new InputStreamReader(System.in,"UTF-8"));
			//KAFDocument naf = KAFDocument.createFromStream(stdInReader);
			//stdInReader.close();
			File file = null;
			if (lang.equals("english"))
				file = new File("/home/egoitz/Data/Datasets/Time/tempeval2-data//naf/english/training/wsj_1040.naf");
			else
				file = new File("/home/egoitz/Data/Datasets/Time/tempeval2-data//naf/spanish/training/12547_20000119.txt.naf");
			KAFDocument naf = KAFDocument.createFromFile(file);
			
			
			System.out.println("Annotating time:");
			System.out.println("\t...loading data");
			dataset.loadNAFDocument(naf);
			
			dataset.createTimexRecogCRFTag();
			if (dataset.crfX.size() == 0) {
				System.out.println("\t...no timexs recognition candidates!!!");
			} else {
				System.out.println("\t...recogniting timexs");
				dataset.crfY = client.tag(dataset.crfX, args[1] + "/" + lang + "/timex-extents");
				dataset.newTimexExtents();
			}

			dataset.loadEventFeaturesFromNAF(naf);
			dataset.createEventClassCRFTag();
			if (dataset.crfX.size() == 0) {
				System.out.println("\t...no event classification candidates!!!");
			} else {
				System.out.println("\t...classifying events");
				dataset.crfY = client.tag(dataset.crfX, args[1] + "/" + lang +  "/event-attributes");
				dataset.tagEvents();
			}
			
			dataset.loadTimexFeaturesFromNAF(naf);
			dataset.createTimexClassCRFTag();
			if (dataset.crfX.size() == 0) {
				System.out.println("\t...no timexs classification candidates!!!");
			} else {
				System.out.println("\t...classifying timexs");
				dataset.crfY = client.tag(dataset.crfX, args[1] + "/" + lang +  "/timex-attributes");
				dataset.tagTimexs();
			}
			
			dataset.createUnknownTlinks(naf);
			dataset.loadTlinkFeaturesFromNAF(naf);
			dataset.createTlinkCategorizationCRFTag(Timex.class, Event.class);
			if (dataset.crfX.size() == 0) {
				System.out.println("\t...no tlinks categorization candidates!!!");
			} else {
				System.out.println("\t...categorizing tlinks");
				dataset.crfY = client.tag(dataset.crfX, args[1] + "/" + lang +  "/tlinks-timex-event");
				dataset.tagTlinks();
			}
			
			dataset.writeTimeInNAF(naf);
//			BufferedWriter stdOutWriter = new BufferedWriter(new OutputStreamWriter(System.out,"UTF-8"));
//			stdOutWriter.write(naf.toString());
//			stdOutWriter.close();
			
			
			PrintWriter writer = new PrintWriter("/home/egoitz/Data/Datasets/Time/tempeval2-data/out."+ lang +".naf", "UTF-8");
			writer.println(naf.toString());
			writer.close();


		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Finish");
	}
}
