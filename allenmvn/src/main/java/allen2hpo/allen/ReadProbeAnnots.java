package allen2hpo.allen;

import java.util.*;
import java.io.*;




/**
*	Reads /Probes.csv file line by line, storing whatever info required in arrays
*	remember to set the size of the matrix rows : must know size of data file before (marked by ***)
*	Probe ID number is stored in ids array
*/
public class ReadProbeAnnots{
	
	private Scanner scanner = null;
	private String [] geneNames = null;
	private int []ids = null;



	public ReadProbeAnnots(String filename, int dim){
		this.ids = new int[dim];
		this.geneNames = new String[dim];


		String file = filename+"/Probes.csv";
		openFile(file);
		readFile();
		scanner.close();
	}


	/**
	*	@return Returns matrix object filled with data
	*/
	public String[] getData(){
		return geneNames;
	}
	


	///PRIVATE METHODS

	/**
	*	private method. opens file with scanner or fails.
	*/
	private void openFile(String filename){
		try{
			scanner = new Scanner(new File(filename));
			System.out.println("file Opened");
		}	
		catch (Exception e){
			System.out.println("File could not be opened");
		}
	}



	/**
	*	Reads file in line by line, passing handling of the line the private method handleRow. 
	*/
	private void readFile(){

		///FIRST LINE IS A HEADER : REMOVE IT
		scanner.nextLine();
		///EACH FOLLOWING ROW IS READ
		int ri = 0; 
	    while (scanner.hasNext()) {
	    	handleRow(scanner.nextLine(),ri);
	    	ri++;
	    }
	}



	private void handleRow(String line, int ri){
		
		///INIT SCANNER TO READ LINE
		Scanner lineSc = new Scanner(line);
        lineSc.useDelimiter(",");
		
		///ADD GENE NAME TO ARRAY OF GENE NAMES
		int i = 0;
	    while (lineSc.hasNext()) {
	    	///ADD PROBE ID TO ID LIST
	    	if(i==0){
	    		this.ids[ri] = lineSc.nextInt();
	    	}
	    	///ADD GENE SYMBOL TO ARRAY
	    	else if(i==3){
	    		this.geneNames[ri] = lineSc.next();
	    	}
	    	else{
	    		lineSc.next();
	    	}
			i++;
        }

     }


}