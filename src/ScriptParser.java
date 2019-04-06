import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/*
	This class is used to parse the Edit Script from an XML file
*/

public class ScriptParser {

	private int sourceIndex, destinatonIndex;
	private char destinationCharacter, sourceCharacter;
	private String operation,  actionId;
	private ArrayList<EditAction> ExtractedScript = new ArrayList<EditAction>();

	public ScriptParser(File f) throws ParserConfigurationException, SAXException, IOException {
		File fXmlFile = f;
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
		
		NodeList nList = doc.getElementsByTagName("action");
		
		if(nList.getLength() == 0) {
			JOptionPane.showMessageDialog(null, "Please provide a valid diff file !", "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
		
		for (int temp = 0; temp < nList.getLength(); temp++) {
			
			Node nNode = nList.item(temp);
			
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {

				Element eElement = (Element) nNode;
				
				actionId = eElement.getAttribute("id");
				
				
				operation = eElement.getElementsByTagName("type").item(0).getTextContent();
				destinationCharacter = eElement.getElementsByTagName("destinationChar").item(0).getTextContent().charAt(0);
				destinatonIndex = Integer.parseInt(eElement.getElementsByTagName("destinationIndex").item(0).getTextContent());
				sourceCharacter = eElement.getElementsByTagName("sourceChar").item(0).getTextContent().charAt(0);
				sourceIndex = Integer.parseInt(eElement.getElementsByTagName("sourceIndex").item(0).getTextContent());
			
				EditAction tempObject = new EditAction(); // EditAction objects creations
				tempObject.setOperation(operation); // Ins/Del/Up
				tempObject.setDestinationCharacter(destinationCharacter);
				tempObject.setDestinationIndex(destinatonIndex);
				tempObject.setSourceCharacter(sourceCharacter);
				tempObject.setSourceIndex(sourceIndex);

				ExtractedScript.add(tempObject);		
			 }	
		}
	}

	public int getActionId() {
		return ExtractedScript.get(ExtractedScript.size()-1).getactionId();
	}
	
	public int getOperationSourceIndex(int i) {
		return ExtractedScript.get(i).getSourceIndex();
	}

	public char getOperationSourceCharacter(int i) {
		return ExtractedScript.get(i).getSourceCharacter();
	}
	
	public char getOperationDestinationCharacter(int i) {
		return ExtractedScript.get(i).getDestinationCharacter();
	}

	public int getOperationDestinationIndex(int i) {
		return ExtractedScript.get(i).getDestinationIndex();
	}

	public String getOperationType(int i) {
		return ExtractedScript.get(i).getOperation();
	}

	public int size() {
		return ExtractedScript.size();
	}
	
	//Format extracted edit script in form of a string
	public String getEditScript() {
		String finalES = "";
		
		for (int i = 0; i < ExtractedScript.size(); i++) {

			switch (ExtractedScript.get(i).getOperation()) {

			case "Update":

				finalES = finalES + "(Upd(" + "A" + ExtractedScript.get(i).getSourceIndex() + ", B"
						+ ExtractedScript.get(i).getDestinationIndex() + "),";
				break;

			case "Insert":

				finalES = finalES + "(Ins(" + "B" + ExtractedScript.get(i).getDestinationIndex() + ", "
						+ (ExtractedScript.get(i).getSourceIndex() + 1) + "),";
				break;

			case "Delete":

				finalES = finalES + "(Del(" + "A" + ExtractedScript.get(i).getSourceIndex() + "),";
				break;

			}
		}
		
		//We remove the extra ","
		finalES= finalES.substring(0, finalES.length()-1);

		return "ES(A,B): {" + finalES+"}";
	}
	
	//Format properly reversed  Edit Script --> When we go from destination to source
	public String getReversedEditScript() {
		String finalES = "";
		
		for (int i = 0; i < ExtractedScript.size(); i++) {

			switch (ExtractedScript.get(i).getOperation()) {

			case "Update":

				finalES = finalES + "(Upd(" + "B" + (ExtractedScript.get(i).getDestinationIndex()+1) + ",A" + (ExtractedScript.get(i).getSourceIndex()+1) + "),";
				break;

			case "Delete":

				finalES = finalES + "(Ins(" + "B" + ExtractedScript.get(i).getSourceIndex() + ", "
						+ (ExtractedScript.get(i).getDestinationIndex() + 1) + "),";
				break;

			case "Insert":

				finalES = finalES + "(Del(" + "B" + (ExtractedScript.get(i).getDestinationIndex()+1) + "),";
				break;

			}
		}
		
		//We remove the extra ","
		if(!finalES.isEmpty())
			finalES= finalES.substring(0, finalES.length()-1);

		return "ES(B,A): {" + finalES+"}";
	}
}