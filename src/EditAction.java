/*
	This class defines every single action of the Edit Script
*/
public class EditAction {

	private int sourceIndex, destinatonIndex, actionId;
	private char destinationCharacter, sourceCharacter;
	private String operation;
	

	public int getactionId() {
		return actionId;
	}
	
	public int getSourceIndex() {
		return sourceIndex;
	}

	public void setSourceIndex(int sourceIndex) {
		this.sourceIndex = sourceIndex;
	}

	public char getSourceCharacter() {
		return sourceCharacter;
	}

	public void setSourceCharacter(char sourceCharacter) {
		this.sourceCharacter = sourceCharacter;
	}

	public char getDestinationCharacter() {
		return destinationCharacter;
	}

	public void setDestinationCharacter(char destinationCharacter) {
		this.destinationCharacter = destinationCharacter;
	}

	public int getDestinationIndex() {
		return destinatonIndex;
	}

	public void setDestinationIndex(int destinatonIndex) {
		this.destinatonIndex = destinatonIndex;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

}
