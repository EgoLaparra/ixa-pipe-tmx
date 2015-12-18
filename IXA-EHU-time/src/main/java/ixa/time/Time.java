package ixa.time;

import ixa.kaflib.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cognitionis.tipsem.*;
import com.cognitionis.external_tools.*;

public class Time {
	
	private static final Pattern JARPATH_PATTERN_BEGIN = Pattern.compile("file:");
	private static final Pattern JARPATH_PATTERN_END = Pattern.compile("[^/]+jar!.+");

	static final String HOST = "localhost";
	static int PORT;
	
	public Time() {
		
		try {
			String jarpath = this.getClass().getResource("").getPath();
	        Matcher matcher = JARPATH_PATTERN_BEGIN.matcher(jarpath);
	        jarpath = matcher.replaceAll("");		
	        matcher = JARPATH_PATTERN_END.matcher(jarpath);
	        jarpath = matcher.replaceAll("");
			
			BufferedReader stdInReader = null;
			stdInReader = new BufferedReader(new InputStreamReader(System.in,"UTF-8"));
			BufferedWriter w = null;
			w = new BufferedWriter(new OutputStreamWriter(System.out, "UTF-8"));
			
			stdInReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/egoitz/Tecnico/java/ixa-pipe-time/external-files/naf-examples/10021_First_Airbus_A380_delivered.xml.noTime.naf"),Charset.forName("UTF-8")));
			KAFDocument kaf = ixa.kaflib.KAFDocument.createFromStream(stdInReader);
		
			TempInfo tmpInfo = createTemporalRaw(kaf);	
			//Runtime.getRuntime().exec("java -jar " + jarpath + "/TipSemB-1.0/TIMEE.jar -ap dct=" + tmpInfo.DCT + " " + tmpInfo.filePath);
			//Runtime.getRuntime().exec("java -jar /home/egoitz/Tecnico/java/ixa-pipe-time/external-files/TIPSemB-1.0/TIMEE.jar -ap dct=" + tmpInfo.DCT + " " + tmpInfo.filePath);

			SVM.test("/home/egoitz/Tecnico/java/ixa-pipe-time/external-files/TIPSemB-1.0/program-data/SVM/models/TIPSemB_categ_e-t_ES.SVMmodel");
			
			Annotate annotator = null;
			//annotator.TMLtoKAF(kaf,tmpInfo.filePath + ".tml");
			
			w.write(kaf.toString());
			w.close();
		} catch (Exception e) {
			//System.err.println("KKK");
			//e.printStackTrace();
		}
	}


	
	private static TempInfo createTemporalRaw(KAFDocument kaf)
	{
		TempInfo tmpI = new TempInfo();

		PrintWriter writer;
		try {
			Date date = new Date();
			tmpI.filePath = "/tmp/Time" + date + ".tmp";
			writer = new PrintWriter(tmpI.filePath, "UTF-8");
			List<List<WF>> sentences = kaf.getSentences();
			Iterator<List<WF>> sentencesIterator = sentences.iterator();	
			while (sentencesIterator.hasNext()) {
				String sentence = "";
				Iterator<WF> wordFormsIterator = sentencesIterator.next().iterator();
				while (wordFormsIterator.hasNext()) {
					String wordForm = wordFormsIterator.next().getForm();
					if (sentence.equals(""))
						sentence = wordForm;
					else
						sentence = sentence + " " + wordForm;
				}
				writer.println(sentence);
			}
			writer.close();	
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
			
		tmpI.DCT = kaf.getFileDesc().creationtime;
		
		return tmpI;
	}
	
	public static void main(String[] args) {
		
		String lang = "eng";
		if (args[0].equals("en")) {
			lang = "eng";
		} else if (args[0].equals("es")) {
			lang = "spa";
		}
	    new Time();
	}
}