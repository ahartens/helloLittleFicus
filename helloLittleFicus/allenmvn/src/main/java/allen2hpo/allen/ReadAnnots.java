package allen2hpo.allen;

import java.util.*;
import java.io.*;




/**
*	Reads /Probes.csv file line by line, storing whatever info required in arrays
*	remember to set the size of the matrix rows : must know size of data file before (marked by ***)
*	Probe ID number is stored in ids array
*/
abstract class ReadAnnots{

	private Scanner scanner = null;
	private String [] names = null;
	private int []ids = null;
	private int count;

	public abstract void handleRow(String line, int ri);


	public void StartReading(String filename, int dim){
		this.ids = new int[dim];
		this.names = new String[dim];

		openFile(filename);
		readFile();

		if (this.count != dim){

			int[] tempIds = new int[this.count];
			String [] tempNames = new String[this.count];

			for (int i=0; i<this.count; i++){
				tempIds[i] = this.ids[i];
				tempNames[i] = this.names[i];

			}
			this.ids = tempIds;
			this.names = tempNames;

		}
		scanner.close();
	}


	/**
	*	@return Returns matrix object filled with data
	*/
	public String[] getData(){
		return this.names;
	}

	public int getCount(){
		return this.count;
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
		this.count = 0;
	    while (scanner.hasNext()) {
	    	handleRow(scanner.nextLine(),this.count);
	    	this.count++;
	    }
	}


	public void setNameAtIndex(String name, int i){
		this.names[i] = name;
	}

	public void setIdAtIndex(int id, int i){
		this.ids[i] = id;
	}


}
