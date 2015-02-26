package allen2hpo.allen.parsing;

import java.util.*;
import java.io.*;


/**
*	<p>Abstract class to read an annotation file (probe/sample).
*	Skips first line (header) and continues to parse file line by line
*	Subclasses must override handleRow method (called for each line of file)</p>
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
	*	Constructor method implements lifecycle
	*	handleRow() Subclasses must overridden by subclasses
	*	@param string filename of file to be provided
	*/
	public void StartReading(String filename){

		/** Count number of lines in file, excluding the first line (header) */
		openFile(filename);
		countLines();
		scanner.close();

		/** Initialize name/id arrays with dimension size found previously */
		this.ids = new int[this.count];
		this.names = new String[this.count];

		/** Parse each line of the file */
		openFile(filename);
		readFile();
		scanner.close();
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
	*	Count number of lines in file
	*/
	private void countLines(){
		scanner.nextLine();
		while (scanner.hasNext()){
			scanner.nextLine();
			this.count++;
		}
	}

	/**
	*	Reads file in line by line, passing handling of the line the private method handleRow.
	*/
	private void readFile(){
		/** First line is a header : remove it */
		scanner.nextLine();

		/** 
		*	Parse each following line
		*	Handling of line is specified by subclasses of readAnnots
		*/
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
