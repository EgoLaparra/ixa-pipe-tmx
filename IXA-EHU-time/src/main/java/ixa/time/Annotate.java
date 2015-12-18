package ixa.time;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import ixa.kaflib.KAFDocument;

public class Annotate {

	public void TMLtoKAF(KAFDocument kaf, String tmlFile){
		Document tmlDOM;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		try {
			db = dbf.newDocumentBuilder();
			tmlDOM = db.parse(tmlFile);
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
}
