import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JOptionPane;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

/*
	This class is used to recover a DNA sequence from another + Edit Script
*/

public class Patching {
	private ScriptParser ScriptPars;
	private ArrayList<Character> DNACharacterList;
	private String DNASequence = "";
	
	public Patching(File ScriptFile, File DNAFile) throws ParserConfigurationException, SAXException, IOException {
		DNACharacterList = new ArrayList<Character>();
		
		//We parse the Edit Script from the XML file
		ScriptParser ScriptPars = new ScriptParser(ScriptFile);
		this.ScriptPars = ScriptPars;
		
		//We parse the DNA sequence from the XML file
		DNAParser DNAPars = new DNAParser(DNAFile);
		DNACharacterList = DNAPars.getDNAsequenceList();
		
		//We get the DNA sequence ini form of a string
		for(int i = 0; i < DNACharacterList.size(); i++)
			DNASequence += DNACharacterList.get(i);
		
		//We get the last 2 DNA sequences compared
		String[] DNASequences = TempStorage.readStorage();
		
		if(DNASequence.compareTo(DNASequences[0]) == 0) //If the DNA Sequence provided is the source
			recoverDestinationSequence();
		else if(DNASequence.compareTo(DNASequences[1]) == 0) //If the DNA Sequence provided is the destination
			recoverSourceSequence();
		else {
			JOptionPane.showMessageDialog(null, "DNA Sequence and Edit Script provided do not match !", "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
		
		//We save the recovered DNA Sequence in a file
		DNABuilder.createXMLFile(DNASequence);
	}
	
	public void recoverDestinationSequence() {
		int k = 0; // initialize the offset
		
		for (int i =0; i < ScriptPars.size(); i++) { // loop over all the Edit Script
		
			switch (ScriptPars.getOperationType(i)) { //take all cases Upd, Del, Ins

			case "Update":
				
				DNACharacterList.set(ScriptPars.getOperationSourceIndex(i)+k, ScriptPars.getOperationDestinationCharacter(i));
				
				// keep k as is since size of the array remains as it is
				
				break;

			case "Insert":
				
				//we insert the element at index+1 because we want to insert it after the current index
				DNACharacterList.add(ScriptPars.getOperationSourceIndex(i)+1+k, ScriptPars.getOperationDestinationCharacter(i));
				
				//We increment k since the size of the array increase
				k+=1; 
				
				break;
				
			case "Delete":
				
				DNACharacterList.remove(ScriptPars.getOperationSourceIndex(i)+k);
				
				//We decrement k since the size of the array decrease
				k-=1; 
				
				break;
			}
		}
	}
	
	public void recoverSourceSequence() {
		int k = 0; // initialize the offset
		
		for (int i =0; i < ScriptPars.size(); i++) { // loop over all the Edit Script
		
			switch (ScriptPars.getOperationType(i)) { //take all cases Upd, Del, Ins

			case "Update":
				
				DNACharacterList.set(ScriptPars.getOperationDestinationIndex(i)+k, ScriptPars.getOperationSourceCharacter(i));
				 // keep k as is
				break;

			case "Insert":
				
				DNACharacterList.remove(ScriptPars.getOperationDestinationIndex(i)+k);
				
				k-=1; // decrement k
				break;
				
			case "Delete":
				
				DNACharacterList.add(ScriptPars.getOperationDestinationIndex(i)+k, ScriptPars.getOperationSourceCharacter(i));
				k+=1; // increment k
				break;
				
			}
			
			
		}
	}
	
	//GETTERS
	public String getDNASequence() {
		return DNASequence;
	}
	
	//Get the newly built DNA Sequence in String format
	public String getBuiltDNASequence() {
		String builtDNASequence = "";
		for(int i = 0; i < DNACharacterList.size(); i++)
			builtDNASequence += DNACharacterList.get(i);
		
		return builtDNASequence;
	}
	
	public String getEditScript() {
		return ScriptPars.getEditScript();
	}
	
	public String getReversedEditScript() {
		return ScriptPars.getReversedEditScript();
	}
}
