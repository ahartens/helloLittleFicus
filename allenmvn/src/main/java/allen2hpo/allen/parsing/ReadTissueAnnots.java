package allen2hpo.allen.parsing;

import allen2hpo.allen.parsing.ReadAnnots;

import java.util.Scanner;
import java.util.ArrayList;



/**
*	Reads /Probes.csv file line by line, storing whatever info required in 
*	arrays
*	Probe ID number is stored in ids array
*/
public class ReadTissueAnnots extends ReadAnnots{

	//__________________________________________________________________________
    //
    //  Variables                             
    //__________________________________________________________________________

	/**	list of entrez gene symbols */
	double[][] mriVoxel = null;



	//__________________________________________________________________________
    //
    //  Getters                             
    //__________________________________________________________________________

	/**
	*
	*/
	public double[][] getMriVoxel(){
		return this.mriVoxel;
	}



	//__________________________________________________________________________
    //
    //  Constructor                             
    //__________________________________________________________________________

	public ReadTissueAnnots(String file){
		/*
		*	Set filename to be parsed
		*/
		super.setFilename(file);

		/*
		*	Count number of lines
		*/
		super.countLines();

		/*
		*	Init array to store location of tissue
		*/
		this.mriVoxel = new double[super.getCount()][3];

		/*
		*	Begin reading file line by line. Line is handled here.
		*/
		super.parseFile();
	}



	//__________________________________________________________________________
    //
    //  Methods                             
    //__________________________________________________________________________

	@Override 
	public void handleRow(String line, int ri){
		
		/*
		*	Input is a csv and some cells contains further commas (in 
		*	tissue name). These cells have quotes delimiting cell.
		*	Format :
		*	[0]		   [1]		 [2] [3]         [4]  
		*	(id,_,_,_)"(acronym)"(,)"(long name)"(_,x,y,z,_,_,_)
		*	Thus first split line into components separated by commas. 
		*	Handle results separately
		*/

		/*
		*	Split line by quotes
		*/
		Scanner quoteSc = new Scanner(line);
		quoteSc.useDelimiter("\"");
		String[] lineSplitByQuotes = new String[5];
		int quoteCount = 0;
		while(quoteSc.hasNext()){
			lineSplitByQuotes[quoteCount] = quoteSc.next();
			quoteCount ++;
		}

		if (quoteCount != 5) {
			System.out.println("Incorrect sample input");
		}
		else
		{

			/*
			*	FIRST STRING : contains id and acronym
			*/
			Scanner lineSc = new Scanner(lineSplitByQuotes[0]);
			lineSc.useDelimiter(",");
			int i = 0;
			while (lineSc.hasNext()) {
				if(i==0){
					super.setIdAtIndex(lineSc.nextInt(),ri);
				}
				else{
					lineSc.next();
				}
				i++;
			}

			/*	
			*	SECOND STRING : contains tissue name (which contains commas) 
			*/
			super.setNameAtIndex(lineSplitByQuotes[3],ri);


			/*
			*	THIRD STRING : contains location 
			*/
			lineSc = new Scanner(lineSplitByQuotes[4]);
			lineSc.useDelimiter(",");
			i = 0;
			while (lineSc.hasNext()) 
			{
				if (i == 1)
				{
					mriVoxel[ri][0] = (double)lineSc.nextInt();
				}
				else if (i == 2)
				{
					mriVoxel[ri][1] = (double)lineSc.nextInt();
				}
				else if (i == 3)
				{
					mriVoxel[ri][2] = (double)lineSc.nextInt();
				}
				else
				{
					lineSc.next();
				}
				i++;
			}
		}
    }
}
