import java.util.Random;

/*
	This class is used to generate a DNA sequence and store it in a file
*/

public class DNASequenceGenerator {

	private String dna;
	private int size1;
	
	public DNASequenceGenerator(int length){
		this.size1 = length;
		DNAGenerator();
		DNABuilder.createXMLFile(dna);
	}
	
	public String DNAGenerator() {
		
		String Ambnucleo = "AGCTRYKMSWBDHVN";
		
		String s1 ="";
		Random randomgen = new Random();
		
		for (int k = 0; k < size1; k++) {
			s1 += Ambnucleo.charAt(randomgen.nextInt(Ambnucleo.length()));
		}
		
		
		dna = s1;
		return dna;
	}
}