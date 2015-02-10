package allen2hpo.allen;

import java.util.*;
import java.io.*;


/**
*	Abstract class to read an annotation file (probe/sample).
*	Skips first line (header) and continues to parse file line by line
*	Subclasses can specify which cells should be stored in id/name array
*	by overriding handleRow method (called for each line)
*	@param string filename of file to be provided
*	@param general number of rows that are expected (arrays later clipped to proper size)
*	@author Alex Hartenstein
*/

abstract class ReadAnnots{

	private Scanner scanner = null;
	private String [] names = null;
	private int []ids = null;
	private int count;

	public abstract void handleRow(String line, int ri);


	/**
	*	Constructor method performs all necessary actions
	*	handleRow() must be overridding
	*	@param string filename of file to be provided
	*	@param general number of rows that are expected (arrays later clipped to proper size)
	*/
	public void StartReading(String filename, int dim){
		//Init arrays to store cell info with PREDEFINED dimension size
		this.ids = new int[dim];
		this.names = new String[dim];

		//Lifecycle
		openFile(filename);
		readFile();
		scanner.close();

		//Ensure that arrays are exactly the correct size of data they contain
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
	}


	///GETTERS
	/** @return String[] name or abbreviation */
	public String[] getNames(){
		return this.names;
	}

	/** @return int[] id number */
	public int[] getIds(){
		return this.ids;
	}

	/** @return int number of rows in parsed file */
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
