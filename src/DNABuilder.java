import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/*
	This class is used to create an XML file and store in it the Edit Script
*/

public class DNABuilder {

	public static void createXMLFile(String DNASequence) {

		try {

			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("DNADataBank");
			doc.appendChild(rootElement);
			
			Element DNA = doc.createElement("DNA");
			rootElement.appendChild(DNA);
			
			Element accession = doc.createElement("accession");
			accession.appendChild(doc.createTextNode(" "));
			DNA.appendChild(accession);
			
			Element description = doc.createElement("description");
			description.appendChild(doc.createTextNode("DNA Sequence generated"));
			DNA.appendChild(description);
			
			Element length = doc.createElement("length");
			length.appendChild(doc.createTextNode("" + DNASequence.length()));
			DNA.appendChild(length);
			
			Element sequence = doc.createElement("sequence");
			sequence.appendChild(doc.createTextNode(DNASequence));
			DNA.appendChild(sequence);

			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);

			// Output to console for testing

			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			StreamResult result = new StreamResult(new File(generateFileName()));

			transformer.transform(source, result);
			
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}

	}
	
	//We check if any file has been previously generated in order not to overwrite existing files
	public static String generateFileName() {
		String fileName = "generatedDNA.xml";
		
		File f = new File(fileName);
		File f2;
		
		if(f.exists() && !f.isDirectory()) { 
			int i = 1;
			
			do {
				fileName = "generatedDNA_" + i +".xml";
				f2 = new File(fileName);
				i++;
			} while(f2.exists() && !f2.isDirectory());
		}
			
		return fileName;
	}
}