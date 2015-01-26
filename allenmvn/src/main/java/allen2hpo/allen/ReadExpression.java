package allen2hpo.allen;

import java.util.*;
import java.io.*;

import allen2hpo.matrix.Matrix;




/**
*	Reads /MicroarrayExpression.csv file line by line, storing doubles in a data array
*	remember to set the size of the matrix rows : must know size of data file before (marked by ***)
*	Probe ID number is stored in ids array
*/
public class ReadExpression{
	
	private Scanner scanner = null;
	private Matrix matrix = null;
	private int []ids = null;



	/**
	*	Opens file and reads data into Matrix object, gene ids into separate 1d array
	*/	
	public ReadExpression(String filename, int r, int c){
		String file = filename+"/MicroarrayExpression.csv";
		openFile(file);
		readFile(r,c);
		scanner.close();
		System.out.println("BEFORRRRRE : "+c);
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
	*/
	private void readFile(int r, int c){
		double [][] array = new double[r][c];
		this.matrix = new Matrix(array);
		this.ids = new int[r];
		/*///FIRST LINE IS READ TO DETERMINE NUMBER OF COLUMNS
		handleFirstRow(scanner.nextLine(),matrix, dim);
		
		///EACH FOLLOWING ROW IS READ
		int ri = 1;*/
		int ri = 0; 
	    while (scanner.hasNext()) {
	    	handleRow(scanner.nextLine(),this.matrix,ri);
	    	ri++;
	    }
	}



	/**
	*	Stores data in a line to a matrix object andthe gene id to the ids array
	*/
	private void handleRow(String line, Matrix matrix, int ri){
		///INIT SCANNER TO READ LINE
		Scanner lineSc = new Scanner(line);
        lineSc.useDelimiter(",");
		
		///ADD GENE NAME TO ARRAY OF GENE NAMES
        this.ids[ri] = lineSc.nextInt();
        ///SAVE ALL VALUES IN MATRIX
		int i = 0;
	    while (lineSc.hasNext()) {
	    	matrix.setValueAtIndex(ri,i,lineSc.nextDouble());
			i++;
        }
        System.out.println("FINAL COUNT BBIS : "+i);
     }



    /**
    *	Determines number of columns of array and sets data matrix to that value. Stores first line of data into matrix;
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