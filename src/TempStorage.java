import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/*
	This class is used to store temporary and recover the last 2 DNA sequence compared
*/

public class TempStorage {
	public static void updateTempStorage(String DNASourceSequence, String DNADestinationSequence) {
		try {

			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("TempStorage");
			doc.appendChild(rootElement);
			
			Element sourceSequence = doc.createElement("sourceSequence");
			sourceSequence.appendChild(doc.createTextNode(DNASourceSequence));
			rootElement.appendChild(sourceSequence);
			
			Element destinationSequence = doc.createElement("destinationSequence");
			destinationSequence.appendChild(doc.createTextNode(DNADestinationSequence));
			rootElement.appendChild(destinationSequence);

			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);

			// Output to console for testing

			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			StreamResult result = new StreamResult(new File("tempStorage.xml"));

			transformer.transform(source, result);
			
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}
	}
	

	public static String[] readStorage() throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse("tempStorage.xml");
		
		String[] DNASequences = new String[2];

		DNASequences[0] = doc.getElementsByTagName("sourceSequence").item(0).getTextContent();
		DNASequences[1] = doc.getElementsByTagName("destinationSequence").item(0).getTextContent();

		return DNASequences;
	}
}
