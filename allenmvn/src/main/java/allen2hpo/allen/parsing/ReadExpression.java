package allen2hpo.allen.parsing;

import allen2hpo.matrix.Matrix;

import java.util.*;
import java.io.*;


/**
*	Reads /MicroarrayExpression.csv file line by line, storing doubles in a data
*	array
*	<br>Size of matrix must be specified ahead of time (found during parsing of 
*	expression data annotation files).
*	before
*	<br>Probe ID is stored in ids array if clip is specified as true
*	@author Alex Hartenstein
*/

public class ReadExpression{

	//__________________________________________________________________________
    //
    //  Class Variables                                   
    //__________________________________________________________________________

	/** Reads file line by line */
	private Scanner scanner = null;

	/** Matrix object is wrapper for 2d array where file data is stored */
	private Matrix matrix = null;

	/** Stores probe id, which can be used to find corresponding gene */
	private int []ids = null;



	//__________________________________________________________________________
    //
    //  Getters                                   
    //__________________________________________________________________________

	/**
	*	@return Returns matrix object filled with data
	*/
	public Matrix getData()
	{
		return this.matrix;
	}


	//__________________________________________________________________________
    //
    //  Methods                                  
    //__________________________________________________________________________

	/**
	*	Open file and read data into Matrix, gene ids into separate array.
	*	@param String corresponding to exact file from expression should be read
	*	@param int number of rows in the file
	*	@param int number of columns in file (excluding first column)
	*	@param boolean yes = clip first column and store as id, no = don't clip
	*/
	public ReadExpression(String file, int r, int c, boolean clip)
	{
		openFile(file);
		readFile(r,c,clip);
		scanner.close();
	}

	/**
	*	Open File.
	*/
	private void openFile(String filename)
	{
		try
		{
			scanner = new Scanner(new File(filename));
		}
		catch (Exception e)
		{
		}
	}

	/**
	*	Reads file in line by line.
	*	Line is sent to handleRow for further parsing.
	*	Stores data in a matrix object
	*	Stores ids (first column, if clip == yes)
	*/
	private void readFile(int r, int c, boolean clip)
	{
		/*
		*	Initialize matrix and ids
		*/
		double [][] array = new double[r][c];
		this.matrix = new Matrix(array);
		int ri = 0;

		/*
		*	Two separate cases so that boolean clip checkonly only once 
		*	(rather than 63,000x)
		*/
		if(clip == true)
		{
			this.ids = new int[r];
		    while (scanner.hasNext())
		    {
				handleRowStoreFirstColumn(scanner.nextLine(), this.matrix, ri);
		    	ri++;
		    }
		}
		else
		{
			while (scanner.hasNext()) 
			{
				handleRowNoId(scanner.nextLine(), this.matrix, ri);
				ri++;
			}
		}
	}

	/**
	*	Parses a single line/row when clip = true (gene id present).
	*	First column (id) stored.
	*/
	private void handleRowStoreFirstColumn(String line, Matrix matrix, int ri){
		/*
		*	Init scanner to read line using csv delimiter
		*/
		Scanner lineSc = new Scanner(line);
        lineSc.useDelimiter(",");

		/*
		*	Save Probe Id (in first column)
		*/
		this.ids[ri] = lineSc.nextInt();

		/*
		*	Save each following cell to matrix
		*/
		int i = 0;
	    while (lineSc.hasNext()) {
	    	matrix.setValueAtIndex(ri,i,lineSc.nextDouble());
			i++;
        }
     }

	/**
	*	Parses a line, storing all values in a data matrix
	*/
	private void handleRowNoId(String line, Matrix matrix, int ri){
		/*
		*	Init scanner to read line using csv delimiter
		*/
		Scanner lineSc = new Scanner(line);
		lineSc.useDelimiter(",");

		/*
		*	Save each following cell to matrix
		*/		int i = 0;
		while (lineSc.hasNext()) 
		{
			matrix.setValueAtIndex(ri,i,lineSc.nextDouble());
			i++;
		}
	}
}
