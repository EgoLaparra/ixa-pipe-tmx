package ixa.time;

import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.util.Iterator;
import java.util.List;

public class TempEval2 {

	public TempEval2 () {
		
	}
	
	public void printEventExtent (List<EventRecognitionInstance> input, File outFile) {
		
		try {
			FileWriter fw = new FileWriter(outFile.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);		
			int eventIdx = 0;
			String antFileName = "";
			Iterator<EventRecognitionInstance> iterInput = input.iterator();
			while (iterInput.hasNext()) {
				EventRecognitionInstance instance = iterInput.next();
				String fileName = instance.file;
				if (!fileName.equals(antFileName)) {
					eventIdx = 0;
				}
				antFileName = fileName;
				String sentId = instance.sentid.toString();
				String tokenId = instance.tokenid.toString();
				if (instance.label.equals("b-event")) {
					eventIdx++;
					String event = "e" + eventIdx;
					String label = instance.label.replace("b-", "");
					String outputLine = fileName + "\t" + sentId + "\t" + tokenId + "\t" + label + "\t" + event + "\t" + "1";
					//System.out.println(outputLine);
					bw.write(outputLine + "\n");
				} else if (instance.label.equals("i-event")) {
					String event = "e" + eventIdx;
					String label = instance.label.replace("i-", "");
					String outputLine = fileName + "\t" + sentId + "\t" + tokenId + "\t" + label + "\t"+ event + "\t" + "1" ;
					//System.out.println(outputLine);
					bw.write(outputLine + "\n");
				}
			}
			System.out.println("\t... output file written");
			bw.close();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
}
