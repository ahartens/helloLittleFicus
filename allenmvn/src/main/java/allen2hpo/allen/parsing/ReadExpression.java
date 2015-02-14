package allen2hpo.allen.parsing;

import allen2hpo.matrix.Matrix;

import java.util.*;
import java.io.*;


/**
*	Reads /MicroarrayExpression.csv file line by line, storing doubles in a data array
*	remember to set the size of the matrix rows : must know size of data file before
*	Probe ID number is stored in ids array
*	@author Alex Hartenstein
*/

public class ReadExpression{

	/** Scanner object reads line by line through file */
	private Scanner scanner = null;

	/** Matrix object is wrapper around 2d array where data is stored */
	private Matrix matrix = null;

	/** Stores probe id, which can be used to find corresponding gene */
	private int []ids = null;


	/**
	*	Opens file and reads data into Matrix object, gene ids into separate 1d array
	*	@param String corresponding to exact file from expression should be read
	*	@param int number of rows in the file
	*	@param int number of columns in file (excluding first column)
	*	@param boolean yes = clip first column and store as id, no = don't clip
	*/
	public ReadExpression(String file, int r, int c, boolean clip){
		openFile(file);
		readFile(r,c,clip);
		scanner.close();
	}

	/**
	*	@return Returns matrix object filled with data
	*/
	public Matrix getData(){
		return this.matrix;
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
	*	Stores data in a matrix object
	*	Stores ids (first column, if clip == yes)
	*/
	private void readFile(int r, int c, boolean clip){
		///Initialize matrix and ids
		double [][] array = new double[r][c];
		this.matrix = new Matrix(array);
		int ri = 0;

		///Two separate cases so that boolean clip checkonly only 1ce (rather than 63,000x)
		if(clip == true){
			this.ids = new int[r];
		    while (scanner.hasNext()) {
				handleRowStoreFirstColumn(scanner.nextLine(), this.matrix, ri);
		    	ri++;
		    }
		}
		else{
			while (scanner.hasNext()) {
				handleRowNoId(scanner.nextLine(), this.matrix, ri);
				ri++;
			}
		}
	}

	/**
	*	Parses a line, storing first column as an id and all following in data matrix
	*/
	private void handleRowStoreFirstColumn(String line, Matrix matrix, int ri){
		///Init scanner to read line
		Scanner lineSc = new Scanner(line);
        lineSc.useDelimiter(",");

		///Save Probe Id
		this.ids[ri] = lineSc.nextInt();

		//Save each cell to matrix
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
		///Init scanner to read line
		Scanner lineSc = new Scanner(line);
		lineSc.useDelimiter(",");

		//Save each cell to matrix
		int i = 0;
		while (lineSc.hasNext()) {
			matrix.setValueAtIndex(ri,i,lineSc.nextDouble());
			i++;
		}
	}

    /**
    *	Determines number of columns of array and sets data matrix to that value. Stores first line of data into matrix;
	*	NOT USED ATM
	*	Can be used if don't want to feed in number of columns/rows
    */
	private void handleFirstRow(String line, Matrix matrix, int dim){

		///PURPOSE : DETERMINE NUMBER OF COLUMNS (SO THAT MATRIX DIMENSIONS FIT FILE)
		///INIT LINE SCANNER
		Scanner lineSc = new Scanner(line);
		lineSc.useDelimiter(",");


		///SAVE FIRST LINE IN TEMPORARY ARRAYS
		int maxColumns = 10000;
		int geneID = lineSc.nextInt();
		double temporaryData[] = new double[maxColumns];

		///ITERATE THROUGH COLUMNS, COUNTING NUMBER AND SAVING TEMPORARYILY
		int i = 0;
		while(lineSc.hasNext()){
			if (i<maxColumns) {
				temporaryData[i] = (lineSc.nextDouble());
				i++;
			}
			else{
				System.out.println("Too many columns");
			}
		}

        ///SAVE ALL VALUES IN MATRIX (COPY FROM TEMPORARY)
        ///***
       	this.matrix.setMatrixSize(dim,i);
       	this.ids = new int[dim];
		this.ids[0] = geneID;
		for(int j = 0;j<i;j++){
			//matrix.addColumnHeader(String.format("Column %d",j),j);
			matrix.setValueAtIndex(0,j,temporaryData[j]);

		}
	}
}
