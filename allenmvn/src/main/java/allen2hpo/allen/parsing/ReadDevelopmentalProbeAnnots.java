package allen2hpo.allen.parsing;

import allen2hpo.allen.parsing.ReadAnnots;

import java.util.Scanner;
import java.util.ArrayList;



/**
*	Reads /Probes.csv file line by line, storing whatever info required in 
*	arrays
*	Probe ID number is stored in ids array
*/
public class ReadDevelopmentalProbeAnnots extends ReadAnnots{

	//__________________________________________________________________________
    //
    //  Constructor                             
    //__________________________________________________________________________

	public ReadDevelopmentalProbeAnnots(String file){
		
		//	Set filename to be parsed
		super.setFilename(file);

		//	Count number of lines
		super.countLines();
		
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
		
		//	[0]	   [1]	      [2]   [3]           [4]
		//	_,_,   "ensembl   ",   "gene symbol   ",entrzid
		
		//	_,_,   "_          ",   "_             ",_

		//	Thus first split line into components separated by commas. 
		//	Handle results separately


		
		//	Split line by quotes
		
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
			super.setNameAtIndex(lineSplitByQuotes[3],ri);

		}
    }
}
