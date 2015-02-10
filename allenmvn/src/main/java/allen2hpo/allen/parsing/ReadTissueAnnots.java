package allen2hpo.allen;

import java.util.*;
import java.io.*;


import allen2hpo.allen.*;


/**
*	Reads /Probes.csv file line by line, storing whatever info required in arrays
*	remember to set the size of the matrix rows : must know size of data file before (marked by ***)
*	Probe ID number is stored in ids array
*/
public class ReadTissueAnnots extends ReadAnnots{


	public ReadTissueAnnots(String dir, int dim){
		String file = dir;
		super.StartReading(file,dim);
	}


	@Override public void handleRow(String line, int ri){
		///Input is a csv and some cells may contain further commas. These cells have quotes
		///Thus first split line into components separated by commas. Handle results separately
		Scanner quoteSc = new Scanner(line);
		quoteSc.useDelimiter("\"");
		String[] lineSplitByQuotes = new String[5];
		int quoteCount = 0;
		while(quoteSc.hasNext()){
			lineSplitByQuotes[quoteCount] = quoteSc.next();
			quoteCount ++;
			System.out.println(quoteCount);
		}


		///There are no commas within the name. Read each cell sequentially
		if (quoteCount == 1){
			///Split line into cells
			Scanner lineSc = new Scanner(lineSplitByQuotes[0]);
			lineSc.useDelimiter(",");
			///Parse cells for necessary data
			int i = 0;

			while (lineSc.hasNext()) {
				///ADD PROBE ID TO ID LIST
				if(i==0){
					super.setIdAtIndex(lineSc.nextInt(),ri);
				}
				///ADD GENE SYMBOL TO ARRAY
				else if(i==5){
					super.setNameAtIndex(lineSc.next(),ri);
				}
				else{
					lineSc.next();
				}
				i++;
			}
		}

		///The name contains commas, thus must be handled differently
		else{

			///First string of 3 strings is the id and acronym
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

			///Second of 3 strings is the name (which contains commas)
			super.setNameAtIndex(lineSplitByQuotes[3],ri);


			///Third of 3 strings contains path


		}

     }


}
