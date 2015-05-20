package allen2hpo.allen.parsing;

import allen2hpo.allen.parsing.ReadAnnots;

import java.util.Scanner;
import java.util.ArrayList;



/**
*	Reads /Probes.csv file line by line, storing whatever info required in 
*	arrays
*	Probe ID number is stored in ids array
*/
public class ReadDevelopmentalTissueAnnots extends ReadAnnots{

	//__________________________________________________________________________
    //
    //  Variables                             
    //__________________________________________________________________________

	/**	list of entrez gene symbols */
	ArrayList<String> timepoints = null;



	//__________________________________________________________________________
    //
    //  Getters                             
    //__________________________________________________________________________

	/**
	*
	*/
	public ArrayList<String> getTimepoints(){
		return this.timepoints;
	}



	//__________________________________________________________________________
    //
    //  Constructor                             
    //__________________________________________________________________________

	public ReadDevelopmentalTissueAnnots(String file){
		
		//	Set filename to be parsed
		super.setFilename(file);

		//	Count number of lines
		super.countLines();

		//	Init array to store location of tissue
		this.timepoints = new ArrayList<String>();
		
		//	Begin reading file line by line. Line is handled here.
		super.parseFile();
	}



	//__________________________________________________________________________
    //
    //  Methods                             
    //__________________________________________________________________________

	@Override 
	public void handleRow(String line, int ri){
		
		//	Input is a csv and some cells contains further commas (in 
		//	tissue name). These cells have quotes delimiting cell.
		//	Format :
		//	[0]		[1]	 [2]      [3]  [4]   [5]
		//	_,_,_,   "_   ",_,_,   "_   ",   "_   "
		
		//	[0]		[1]	    [2]            [3]       [4]   [5]
		//	_,_,_,   "age   ",gender,id,   "acronym   ",   "structure   "

		//	Thus first split line into components separated by commas. 
		//	Handle results separately


		
		//	Split line by quotes
		
		Scanner quoteSc = new Scanner(line);
		quoteSc.useDelimiter("\"");
		String[] lineSplitByQuotes = new String[6];
		int quoteCount = 0;
		while(quoteSc.hasNext()){
			lineSplitByQuotes[quoteCount] = quoteSc.next();
			quoteCount ++;
		}


		if (quoteCount != 6) {
			System.out.println("Incorrect sample input");
		}
		else
		{

			//	Time point is position 1
			this.timepoints.add(lineSplitByQuotes[1]);
			

			//	SECOND STRING : contains tissue name (which contains commas) 
			super.setNameAtIndex(lineSplitByQuotes[5],ri);
			//super.setIdAtIndex(lineSc.nextInt(),ri);

		}
    }
}
