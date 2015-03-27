package allen2hpo.allen.parsing;

import java.util.*;
import java.io.*;

import java.util.ArrayList;

/**
*	<p>Abstract class to read an annotation file (probe/sample).
*	<br>Skips first line (header) and continues to parse file line by line
*	<br>Subclasses must override handleRow method (called for each line of file)
*	</p>
*	@param String filename of file to be provided
*	@author Alex Hartenstein
*/

abstract class ReadAnnots{

	//__________________________________________________________________________
    //
    //  Variables                              
    //__________________________________________________________________________

	/**	File name to be parsed */
	private String filename;

	/**	Reads line by line */
	private Scanner scanner = null;

	/**	Probes/samples both have names and ids */
	private ArrayList<String> names = null;

	/**	Probes/samples both have names and ids */
	private ArrayList<Integer> ids = null;

	/**	As reading specifies current line being read. Later is number of rows
	*	total */
	private int count;

	/**	
	*	Abstract method declaration that must be implemented by subclasses. 
	*/
	public abstract void handleRow(String line, int ri);



	//__________________________________________________________________________
    //
    //  Public Methods                              
    //__________________________________________________________________________

	/**
	*	Get number of lines in file.
	*/
	public void countLines(){

		openFile(this.filename);
		
		/*	Ignore header */
		this.scanner.nextLine();

		while (this.scanner.hasNext())
		{
			this.scanner.nextLine();
			this.count++;
		}		

		scanner.close();
	}

	/**
	*	Reads file line by line and sends parsing of line to subclassed objects
	*	to specify cells to be saved. 
	*	handleRow() Subclasses must overridden by subclasses
	*	@param String filename of file to be provided
	*/
	public void parseFile(){

		/** Initialize name/id arrays with dimension size found previously */
		this.ids = new ArrayList<Integer>();
		this.names = new ArrayList<String>();

		/** Parse each line of the file */
		openFile(this.filename);
		readFile();
		scanner.close();
	}



	//__________________________________________________________________________
    //
    //  Private Methods                              
    //__________________________________________________________________________

    /**
	*	private method. opens file with scanner or fails.
	*/
	private void openFile(String filename){
		try{
			scanner = new Scanner(new File(this.filename));
		}
		catch (Exception e){
		}
	}

	/**
	*	Reads file in line by line, passing handling of the line the private 
	*	method handleRow.
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



	//__________________________________________________________________________
    //
    //  Getters                              
    //__________________________________________________________________________

    /** @return String[] name or abbreviation */
	public ArrayList<String> getNames(){
		return this.names;
	}

	/** @return int[] id number */
	public ArrayList<Integer> getIds(){
		return this.ids;
	}

	/** @return int number of rows in parsed file */
	public int getCount(){
		return this.count;
	}



	//__________________________________________________________________________
    //
    //  Setters                              
    //__________________________________________________________________________

	/**
	*	Called by handle row of subclassed objects.
	*/
	public void setNameAtIndex(String name, int i){
		this.names.add(name);
	}

	/**
	*	Called by handle row of subclassed objects.
	*/
	public void setIdAtIndex(int id, int i){
		this.ids.add(id);
	}

	/**
	*	Set file name
	*/
	public void setFilename(String name){
		this.filename = name;
	}
}
