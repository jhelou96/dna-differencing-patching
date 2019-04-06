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

public class ScriptBuilder {

	public static void createXMLFile(ArrayList<EditAction> actionList) {

		try {

			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("actionList");
			doc.appendChild(rootElement);

			for (int i = 0; i < actionList.size(); i++) {

				// List action elements
				Element action = doc.createElement("action");
				rootElement.appendChild(action);

				// set attribute to action element
				Attr attr = doc.createAttribute("id");
				attr.setValue(i + "");
				action.setAttributeNode(attr);

				// type elements
				Element type = doc.createElement("type");
				type.appendChild(doc.createTextNode(actionList.get(i).getOperation()));
				action.appendChild(type);

				// destinationChar elements
				Element destinationChar = doc.createElement("destinationChar");
				destinationChar.appendChild(doc.createTextNode(actionList.get(i).getDestinationCharacter() + ""));
				action.appendChild(destinationChar);

				// destinationIndex elements
				Element destinationIndex = doc.createElement("destinationIndex");
				destinationIndex.appendChild(doc.createTextNode(actionList.get(i).getDestinationIndex() + ""));
				action.appendChild(destinationIndex);

				// sourceChar elements
				Element sourceChar = doc.createElement("sourceChar");
				sourceChar.appendChild(doc.createTextNode(actionList.get(i).getSourceCharacter() + ""));
				action.appendChild(sourceChar);

				// sourceIndex elements
				Element sourceIndex = doc.createElement("sourceIndex");
				sourceIndex.appendChild(doc.createTextNode(actionList.get(i).getSourceIndex() + ""));
				action.appendChild(sourceIndex);

			}

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
		String fileName = "diff.xml";
		
		File f = new File(fileName);
		File f2;
		
		if(f.exists() && !f.isDirectory()) { 
			int i = 1;
			
			do {
				fileName = "diff_" + i +".xml";
				f2 = new File(fileName);
				i++;
			} while(f2.exists() && !f2.isDirectory());
		}
			
		return fileName;
	}

}