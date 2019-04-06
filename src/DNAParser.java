import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/*
	This class is used to parse DNA sequences from XML files
*/

public class DNAParser {

	private  String DNASequence;
	private  File DNAXMLfile;
	private ArrayList<Character> DNASequenceList;

	public String getDNAsequence() {
		return DNASequence;
	}
	
	public ArrayList<Character> getDNAsequenceList() {
		return DNASequenceList;
	}

	public DNAParser(File f) throws ParserConfigurationException, SAXException, IOException {

		this.DNAXMLfile = f;
		DNAparsing();
	}

	private void DNAparsing() throws ParserConfigurationException, SAXException, IOException {
		DNASequenceList = new ArrayList<Character>();
		
		//We retrieve the DNA sequence from the given file
		File fXmlFile = DNAXMLfile;
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
		NodeList nList = doc.getElementsByTagName("DNA");
		String s = null;

		Node nNode = nList.item(0);

		if (nNode.getNodeType() == Node.ELEMENT_NODE) {

			Element eElement = (Element) nNode;

			s = eElement.getElementsByTagName("sequence").item(0).getTextContent();

		}
		
		DNASequence = s;
		
		//Each character is added to the array
		for (int i = 0; i < DNASequence.length(); i++) 
			DNASequenceList.add(DNASequence.charAt(i));
		
	}
	

}