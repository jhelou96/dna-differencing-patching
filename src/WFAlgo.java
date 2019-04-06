import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.SynchronousQueue;

import javax.swing.JOptionPane;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

/*
	This class is used to build the SED and generate an Edit Script from 2 DNA sequences
*/

public class WFAlgo {
	private String DNA1, DNA2;
	private int delC = 1, insC = 1;
	private HashMap<Character, String> ambiguityHash;

	private String ambiguity = "RYKMSWBDHVN";
	private String nucleotide = "AGCT";

	private double[][] table; //Will contain the SED table
	private ArrayList<EditAction> actionList; //Will contain the list of edit actions --> Edit script
	private double SED;
	private double similarity;
	
	private NumberFormat NBFormatter;
	
	public WFAlgo(File DNA1File, File DNA2File) throws ParserConfigurationException, SAXException, IOException {

		DNAParser DNA1Parser = new DNAParser(DNA1File); // Extract DNA1 Sequence
		DNAParser DNA2Parser = new DNAParser(DNA2File); // Extract DNA2 sequence
		
		DNA1 = DNA1Parser.getDNAsequence();
		DNA2 = DNA2Parser.getDNAsequence();
		
		if(DNA1.length() == 0 || DNA2.length() == 0) {
			JOptionPane.showMessageDialog(null, "DNA Sequences cannot be empty !", "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
		
		// HashMap for ambiguities
		ambiguityHash = new HashMap<Character, String>();
		ambiguityHash.put('R', "GA");
		ambiguityHash.put('Y', "TC");
		ambiguityHash.put('K', "GT");
		ambiguityHash.put('M', "AC");
		ambiguityHash.put('S', "GC");
		ambiguityHash.put('W', "AT");
		ambiguityHash.put('B', "GTC");
		ambiguityHash.put('D', "GAT");
		ambiguityHash.put('H', "ACT");
		ambiguityHash.put('V', "GCA");
		ambiguityHash.put('N', "AGCT");
		
		table = new double[DNA1.length()+1][DNA2.length()+1];
		actionList = new ArrayList<EditAction>();
		
		buildTable();
		
		ESWriter();
		
		//We store the edit script inside an XML file
		ScriptBuilder.createXMLFile(actionList);
		
		//Formatter to format doubles
		NBFormatter = new DecimalFormat("#0.00");
		
		//We store temporary the 2 DNA sequences in a file to keep track of which is the source and which is the destination
		TempStorage.updateTempStorage(DNA1, DNA2);
	}

	//Check price of update
	public int updateSimpleCost(char char1, char char2) {
		if (char1 == char2)
			return 0;
		else
			return 1;
	}

	//Get full update cost between 2 chars
	public double updateCost(char char1, char char2) {
		double cost = 0.0;
		
		String str1; // Ambiguity String 1
		String str2; // Ambiguity String 2

		// case of two ambiguities
		if (ambiguity.indexOf(char1) >= 0 && ambiguity.indexOf(char2) >= 0) {

			if (char1 == char2) // check if the characters are the same then no cost
				return cost;

			str1 = ambiguityHash.get(char1); //Get Ambiguity related to char1
			str2 = ambiguityHash.get(char2); //Get Ambiguity related to char2

			//Compute update cost in case char1 and char2 contain 2 ambiguities
			for (int i = 0; i < str1.length(); i++) {
				for (int j = 0; j < str2.length(); j++) {
					cost += ((double) 1 / str1.length()) * ((double) 1 / str2.length()) * (updateSimpleCost(str1.charAt(i), str2.charAt(j)));

				}
			}

		}

		else if (ambiguity.indexOf(char1) >= 0 && nucleotide.indexOf(char2) >= 0) { // case of char1 ambiguity
		
			if (char1 == char2) // check if the characters are the same then no cost
				return cost;

			str1 = ambiguityHash.get(char1); //Get Ambiguity related to char1

			//Compute update cost in case char1 contains 1 ambiguity
			for (int i = 0; i < str1.length(); i++) {
				cost += ((double) 1 / str1.length()) * (updateSimpleCost(str1.charAt(i), char2));
			}

		} else if (nucleotide.indexOf(char1) >= 0 && ambiguity.indexOf(char2) >= 0) { // case of char2 ambiguity

			if (char1 == char2) // check if the characters are the same then no cost
				return cost;

			str2 = ambiguityHash.get(char2); //Get Ambiguity related to char2

			//Compute update cost in case char2 contains 1 ambiguity
			for (int i = 0; i < str2.length(); i++) {
				cost += ((double) 1 / str2.length()) * (updateSimpleCost(str2.charAt(i), char1));
			}

		} else if (nucleotide.indexOf(char1) >= 0 && nucleotide.indexOf(char2) >= 0) { // case of two nucleotides
			
			//Compute cost in case of 0 ambiguity
			cost = updateSimpleCost(char1, char2);
			
		} else { // case of Invalid DNA Sequence
			JOptionPane.showMessageDialog(null, "Please provide valid DNA sequences !", "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}

		return cost;

	}

	//Build the SED table using Wagner Fisher algorithm
	public void buildTable() {
		table[0][0] = 0;

		for (int i = 1; i <= DNA1.length(); i++)
			table[i][0] = table[i - 1][0] + delC;
		for (int j = 1; j <= DNA2.length(); j++)
			table[0][j] = table[0][j - 1] + insC;

		for (int i = 1; i <= DNA1.length(); i++) {
			for (int j = 1; j <= DNA2.length(); j++) {

				double insV = table[i][j - 1] + insC;
				double delV = table[i - 1][j] + delC;
				double updV = table[i - 1][j - 1] + updateCost(DNA1.charAt(i - 1), DNA2.charAt(j - 1));

				table[i][j] = Math.min(delV, Math.min(insV, updV));

			}
		}
		
		SED = table[DNA1.length()][DNA2.length()];
		similarity = 1 / (SED+1);
	}
	
	//Print the array in form of a table
	public String formatTable() {
		String formattedTable = "";
		
		for (int row = 0; row < table.length; ++row) {
			// printing data row
			formattedTable += "|";
			for (int col = 0; col < table[row].length; ++col) {
				formattedTable += NBFormatter.format(table[row][col]);
				formattedTable += "|";
			}

			formattedTable += "\n";
		}
		
		return formattedTable;
	}

	//Retrieve the Edit Script with Update priority (Used for patching)
	public void ESWriter() {
		//If SED = 0, no edit script
		if (SED == 0)
			return;

		double upguess, insguess, delguess; //Will store cost of update, delete and insert
		
		int i = DNA1.length(); // size of DNA1
		int j = DNA2.length(); // size of DNA2
		
		//We go through the entire table
		while (i >= 0 && j >= 0) {
			EditAction editAction = new EditAction();

			// If we reach i = 0 and j > 0 --> Only option is to insert until j = 0
			if (i == 0 && j > 0) {
				editAction.setOperation("Insert");
				editAction.setSourceCharacter(DNA1.charAt(0));
				editAction.setDestinationCharacter(DNA2.charAt(j-1));
				editAction.setSourceIndex(-1); //Because we insert at position 0
				editAction.setDestinationIndex(j-1);

				actionList.add(editAction);

				j = j - 1;
			} else if (i > 0 && j == 0) { // If we reach j = 0 and i > 0 --> Only option is to delete until j = 0
				editAction.setOperation("Delete");
				editAction.setSourceCharacter(DNA1.charAt(i - 1));
				editAction.setDestinationCharacter(DNA2.charAt(0));
				editAction.setSourceIndex(i-1);
				editAction.setDestinationIndex(0);
				actionList.add(editAction);

				i = i - 1;
			} else if (i == 0 & j == 0) { // If we reach i = 0 and j = 0 --> Edit script is fully obtained and we exit the loop
				i = -1;
				j = -1;
			} else {
				//Else, we have to check for the minimal cost operation
				
				upguess = table[i - 1][j - 1];// diagonal to the specific node
				insguess = table[i][j - 1]; // next to the specific node
				delguess = table[i - 1][j]; // above the specific node
				
				boolean allowUpd = false, allowDel = false, allowIns = false;
				double tempUpdCost = 9999999, tempDelCost = 9999999, tempInsCost = 9999999; //These values will be updated if del, ins and upd operations are allowed
				
				//We check if we can update
				if(Math.abs(upguess + updateCost(DNA1.charAt(i-1), DNA2.charAt(j-1)) - table[i][j]) <= 0.001) { 
					allowUpd = true;
					tempUpdCost = upguess;
				}
				
				//We check if we can insert
				if(Math.abs((insguess + insC) - table[i][j]) <= 0.001) {
					allowIns = true;
					tempInsCost = delguess;
				}
				
				//We check if we can delete
				if(Math.abs((delguess + delC) - table[i][j]) <= 0.001) {
					allowDel = true;
					tempDelCost = delguess;
				}
				
				if(Math.min(tempUpdCost, Math.min(tempDelCost, tempInsCost)) == tempUpdCost) {
					// check if the upgrade node is the minimum node
					//If char1 != char2 --> update
					if (DNA1.charAt(i-1) != DNA2.charAt(j-1)) {
						editAction.setOperation("Update");
						editAction.setSourceCharacter(DNA1.charAt(i-1));
						editAction.setDestinationCharacter(DNA2.charAt(j-1));
						editAction.setSourceIndex(i-1);
						editAction.setDestinationIndex(j-1);
	
						actionList.add(editAction); // add the editAction in the arrayList
	
						//Move diagonally
						i = i - 1;
						j = j - 1;
	
					} else { //If char1 = char2 --> no need to update
						//Move diagonally
						i = i - 1;
						j = j - 1;
					}

				} else if (Math.min(tempUpdCost, Math.min(tempInsCost, tempDelCost)) == tempInsCost) {
					// check if the insert node is the minimum node
					editAction.setOperation("Insert");
					editAction.setSourceCharacter(DNA1.charAt(i-1));
					editAction.setDestinationCharacter(DNA2.charAt(j-1));
					editAction.setSourceIndex(i-1);
					editAction.setDestinationIndex(j-1);

					actionList.add(editAction); // add the editAction in the arrayList

					//Move horizontally
					j = j - 1;

				} else if (Math.min(tempUpdCost, Math.min(tempInsCost, tempDelCost)) == tempDelCost) {
					// check if the delete node is the minimum
					editAction.setOperation("Delete");
					editAction.setSourceCharacter(DNA1.charAt(i-1));
					editAction.setDestinationCharacter(DNA2.charAt(j-1));
					editAction.setSourceIndex(i-1);
					editAction.setDestinationIndex(j-1);
					actionList.add(editAction); // add the editAction in the arrayList

					//Move vertically
					i = i - 1;
				}
			}
		}
	
		//Reverse the edit script
		ArrayList<EditAction> reversedActionList = new ArrayList<EditAction>();
		
		for (int size = actionList.size()-1; size >= 0; size--)
			reversedActionList.add(actionList.get(size));

		actionList = reversedActionList;
	}
	
	//Format properly Edit Script
	public String formatES() {
		String finalES = "";
		
		for (int i = 0; i < actionList.size(); i++) {

			switch (actionList.get(i).getOperation()) {

			case "Update":

				finalES = finalES + "(Upd(" + "A" + (actionList.get(i).getSourceIndex()+1) + "), B"
						+ (actionList.get(i).getDestinationIndex()+1) + "),";
				break;

			case "Insert":

				finalES = finalES + "(Ins(" + "B" + (actionList.get(i).getDestinationIndex() + 1) + "), "
						+ (actionList.get(i).getSourceIndex() + 1) + "),";
				break;

			case "Delete":

				finalES = finalES + "(Del(" + "A" + (actionList.get(i).getSourceIndex()+1) + "),";
				break;

			}
		}
		
		//We remove the extra ","
		if(!finalES.isEmpty())
			finalES= finalES.substring(0, finalES.length()-1);

		return "ES(A,B): {" + finalES+"}";
	}
	
	//Format properly reversed  Edit Script --> When we go from destination to source
	public String formatReversedES() {
		String finalES = "";
		
		for (int i = 0; i < actionList.size(); i++) {

			switch (actionList.get(i).getOperation()) {

			case "Update":

				finalES = finalES + "(Upd(" + "B" + (actionList.get(i).getDestinationIndex()+1) + "),A" + (actionList.get(i).getSourceIndex()+1) + "),";
				break;

			case "Delete":

				finalES = finalES + "(Ins(" + "B" + (actionList.get(i).getSourceIndex() + 1) + "), "
						+ (actionList.get(i).getDestinationIndex()+1) + "),";
				break;

			case "Insert":

				finalES = finalES + "(Del(" + "B" + (actionList.get(i).getDestinationIndex()+1) + "),";
				break;

			}
		}
		
		//We remove the extra ","
		if(!finalES.isEmpty())
			finalES= finalES.substring(0, finalES.length()-1);

		return "ES(B,A): {" + finalES+"}";
	}
	
	
	//Retrieve the Edit Script with Delete Priority (Used for patching)
	public String ESWriterWithDeletePriority() {
		ArrayList<EditAction> actionListWithDelPriority = new ArrayList<EditAction>();
		
		//If SED = 0, no edit script
		if (SED == 0)
			return null;

		double upguess, insguess, delguess; //Will store cost of update, delete and insert
		
		int i = DNA1.length(); // size of DNA1
		int j = DNA2.length(); // size of DNA2
		
		//We go through the entire table
		while (i >= 0 && j >= 0) {
			EditAction editAction = new EditAction();

			// If we reach i = 0 and j > 0 --> Only option is to insert until j = 0
			if (i == 0 && j > 0) {
				editAction.setOperation("Insert");
				editAction.setSourceCharacter(DNA1.charAt(0));
				editAction.setDestinationCharacter(DNA2.charAt(j-1));
				editAction.setSourceIndex(-1); //Because we insert at position 0
				editAction.setDestinationIndex(j-1);

				actionListWithDelPriority.add(editAction);

				j = j - 1;
			} else if (i > 0 && j == 0) { // If we reach j = 0 and i > 0 --> Only option is to delete until j = 0
				editAction.setOperation("Delete");
				editAction.setSourceCharacter(DNA1.charAt(i - 1));
				editAction.setDestinationCharacter(DNA2.charAt(0));
				editAction.setSourceIndex(i-1);
				editAction.setDestinationIndex(0);
				actionListWithDelPriority.add(editAction);

				i = i - 1;
			} else if (i == 0 & j == 0) { // If we reach i = 0 and j = 0 --> Edit script is fully obtained and we exit the loop
				i = -1;
				j = -1;
			} else {
				//Else, we have to check for the minimal cost operation
				
				upguess = table[i - 1][j - 1];// diagonal to the specific node
				insguess = table[i][j - 1]; // next to the specific node
				delguess = table[i - 1][j]; // above the specific node
				
				double tempUpdCost = 9999999, tempDelCost = 9999999, tempInsCost = 9999999; //These values will be updated if del, ins and upd operations are allowed
				
				//We check if we can update
				if(Math.abs(upguess + updateCost(DNA1.charAt(i-1), DNA2.charAt(j-1)) - table[i][j]) <= 0.001) 
					tempUpdCost = upguess;
				
				//We check if we can insert
				if(Math.abs((insguess + insC) - table[i][j]) <= 0.001) 
					tempInsCost = delguess;
				
				//We check if we can delete
				if(Math.abs((delguess + delC) - table[i][j]) <= 0.001) 
					tempDelCost = delguess;
				
				if(Math.min(tempUpdCost, Math.min(tempInsCost, tempDelCost)) == tempDelCost) {
					// check if the delete node is the minimum node
					editAction.setOperation("Delete");
					editAction.setSourceCharacter(DNA1.charAt(i-1));
					editAction.setDestinationCharacter(DNA2.charAt(j-1));
					editAction.setSourceIndex(i-1);
					editAction.setDestinationIndex(j-1);
					actionListWithDelPriority.add(editAction); // add the editAction in the arrayList

					//Move vertically
					i = i - 1;
					
				} else if (Math.min(tempUpdCost, Math.min(tempInsCost, tempDelCost)) == tempInsCost) {
					// check if the insert node is the minimum node
					editAction.setOperation("Insert");
					editAction.setSourceCharacter(DNA1.charAt(i-1));
					editAction.setDestinationCharacter(DNA2.charAt(j-1));
					editAction.setSourceIndex(i-1);
					editAction.setDestinationIndex(j-1);

					actionListWithDelPriority.add(editAction); // add the editAction in the arrayList

					//Move horizontally
					j = j - 1;

				} else if (Math.min(tempUpdCost, Math.min(tempDelCost, tempInsCost)) == tempUpdCost) {
					// check if the update node is the minimum
					//If char1 != char2 --> update
					if (DNA1.charAt(i-1) != DNA2.charAt(j-1)) {
						editAction.setOperation("Update");
						editAction.setSourceCharacter(DNA1.charAt(i-1));
						editAction.setDestinationCharacter(DNA2.charAt(j-1));
						editAction.setSourceIndex(i-1);
						editAction.setDestinationIndex(j-1);
	
						actionListWithDelPriority.add(editAction); // add the editAction in the arrayList
	
						//Move diagonally
						i = i - 1;
						j = j - 1;
	
					} else { //If char1 = char2 --> no need to update
						//Move diagonally
						i = i - 1;
						j = j - 1;
					}
				}
			}
		}

		//Reverse the edit script
		ArrayList<EditAction> reversedActionList = new ArrayList<EditAction>();
		
		for (int size = actionListWithDelPriority.size()-1; size >= 0; size--)
			reversedActionList.add(actionListWithDelPriority.get(size));
	
		actionListWithDelPriority = reversedActionList;
		
		String finalES = "";
		
		for (int index = 0; index < actionListWithDelPriority.size(); index++) {

			switch (actionListWithDelPriority.get(index).getOperation()) {

			case "Update":

				finalES = finalES + "(Upd(" + "A" + (actionListWithDelPriority.get(index).getSourceIndex()+1) + "), B"
						+ (actionListWithDelPriority.get(index).getDestinationIndex()+1) + "),";
				break;

			case "Insert":

				finalES = finalES + "(Ins(" + "B" + (actionListWithDelPriority.get(index).getDestinationIndex() + 1) + "), "
						+ (actionListWithDelPriority.get(index).getSourceIndex() + 1) + "),";
				break;

			case "Delete":

				finalES = finalES + "(Del(" + "A" + (actionListWithDelPriority.get(index).getSourceIndex()+1) + "),";
				break;

			}
		}
		
		//We remove the extra ","
		if(!finalES.isEmpty())
			finalES= finalES.substring(0, finalES.length()-1);

		return "ES(A,B): {" + finalES+"}";
	}
	
	//Retrieve the Edit Script with Delete Priority (Used for patching)
	public String ESWriterWithInsertPriority() {
		ArrayList<EditAction> actionListWithInsPriority = new ArrayList<EditAction>();
		
		//If SED = 0, no edit script
		if (SED == 0)
			return null;

		double upguess, insguess, delguess; //Will store cost of update, delete and insert
		
		int i = DNA1.length(); // size of DNA1
		int j = DNA2.length(); // size of DNA2
		
		//We go through the entire table
		while (i >= 0 && j >= 0) {
			EditAction editAction = new EditAction();

			// If we reach i = 0 and j > 0 --> Only option is to insert until j = 0
			if (i == 0 && j > 0) {
				editAction.setOperation("Insert");
				editAction.setSourceCharacter(DNA1.charAt(0));
				editAction.setDestinationCharacter(DNA2.charAt(j-1));
				editAction.setSourceIndex(-1); //Because we insert at position 0
				editAction.setDestinationIndex(j-1);

				actionListWithInsPriority.add(editAction);

				j = j - 1;
			} else if (i > 0 && j == 0) { // If we reach j = 0 and i > 0 --> Only option is to delete until j = 0
				editAction.setOperation("Delete");
				editAction.setSourceCharacter(DNA1.charAt(i - 1));
				editAction.setDestinationCharacter(DNA2.charAt(0));
				editAction.setSourceIndex(i-1);
				editAction.setDestinationIndex(0);
				actionListWithInsPriority.add(editAction);

				i = i - 1;
			} else if (i == 0 & j == 0) { // If we reach i = 0 and j = 0 --> Edit script is fully obtained and we exit the loop
				i = -1;
				j = -1;
			} else {
				//Else, we have to check for the minimal cost operation
				
				upguess = table[i - 1][j - 1];// diagonal to the specific node
				insguess = table[i][j - 1]; // next to the specific node
				delguess = table[i - 1][j]; // above the specific node
				
				double tempUpdCost = 9999999, tempDelCost = 9999999, tempInsCost = 9999999; //These values will be updated if del, ins and upd operations are allowed
				
				//We check if we can update
				if(Math.abs(upguess + updateCost(DNA1.charAt(i-1), DNA2.charAt(j-1)) - table[i][j]) <= 0.001) 
					tempUpdCost = upguess;
				
				//We check if we can insert
				if(Math.abs((insguess + insC) - table[i][j]) <= 0.001) 
					tempInsCost = delguess;
				
				//We check if we can delete
				if(Math.abs((delguess + delC) - table[i][j]) <= 0.001) 
					tempDelCost = delguess;
				
				if(Math.min(tempUpdCost, Math.min(tempInsCost, tempDelCost)) == tempInsCost) {
					// check if the insert node is the minimum node
					editAction.setOperation("Insert");
					editAction.setSourceCharacter(DNA1.charAt(i-1));
					editAction.setDestinationCharacter(DNA2.charAt(j-1));
					editAction.setSourceIndex(i-1);
					editAction.setDestinationIndex(j-1);

					actionListWithInsPriority.add(editAction); // add the editAction in the arrayList

					//Move horizontally
					j = j - 1;
				} else if (Math.min(tempUpdCost, Math.min(tempInsCost, tempDelCost)) == tempDelCost) {
					// check if the delete node is the minimum node
					editAction.setOperation("Delete");
					editAction.setSourceCharacter(DNA1.charAt(i-1));
					editAction.setDestinationCharacter(DNA2.charAt(j-1));
					editAction.setSourceIndex(i-1);
					editAction.setDestinationIndex(j-1);
					actionListWithInsPriority.add(editAction); // add the editAction in the arrayList

					//Move vertically
					i = i - 1;
				} else if (Math.min(tempUpdCost, Math.min(tempDelCost, tempInsCost)) == tempUpdCost) {
					// check if the update node is the minimum
					//If char1 != char2 --> update
					if (DNA1.charAt(i-1) != DNA2.charAt(j-1)) {
						editAction.setOperation("Update");
						editAction.setSourceCharacter(DNA1.charAt(i-1));
						editAction.setDestinationCharacter(DNA2.charAt(j-1));
						editAction.setSourceIndex(i-1);
						editAction.setDestinationIndex(j-1);
	
						actionListWithInsPriority.add(editAction); // add the editAction in the arrayList
	
						//Move diagonally
						i = i - 1;
						j = j - 1;
	
					} else { //If char1 = char2 --> no need to update
						//Move diagonally
						i = i - 1;
						j = j - 1;
					}
				}
			}
		}

		//Reverse the edit script
		ArrayList<EditAction> reversedActionList = new ArrayList<EditAction>();
		
		for (int size = actionListWithInsPriority.size()-1; size >= 0; size--)
			reversedActionList.add(actionListWithInsPriority.get(size));
	
		actionListWithInsPriority = reversedActionList;
		
		String finalES = "";
		
		for (int index = 0; index < actionListWithInsPriority.size(); index++) {

			switch (actionListWithInsPriority.get(index).getOperation()) {

			case "Update":

				finalES = finalES + "(Upd(" + "A" + (actionListWithInsPriority.get(index).getSourceIndex()+1) + "), B"
						+ (actionListWithInsPriority.get(index).getDestinationIndex()+1) + "),";
				break;

			case "Insert":

				finalES = finalES + "(Ins(" + "B" + (actionListWithInsPriority.get(index).getDestinationIndex() + 1) + "), "
						+ (actionListWithInsPriority.get(index).getSourceIndex() + 1) + "),";
				break;

			case "Delete":

				finalES = finalES + "(Del(" + "A" + (actionListWithInsPriority.get(index).getSourceIndex()+1) + "),";
				break;

			}
		}
		
		//We remove the extra ","
		if(!finalES.isEmpty())
			finalES= finalES.substring(0, finalES.length()-1);

		return "ES(A,B): {" + finalES+"}";
	}
	
	
	//GETTERS
	public double[][] getTable() {
		return table;
	}
	
	public ArrayList<EditAction> getActionList() {
		return actionList;
	}
	
	public double getSED() {
		return SED;
	}
	
	public double getSimilarity() {
		return similarity;
	}
	
	public String getDNASequence1() {
		return DNA1;
	}
	
	public String getDNASequence2() {
		return DNA2;
	}
}
