package allen2hpo.allen;

import allen2hpo.allen.*;

import java.util.*;
import java.io.*;


/**
*	Reads /Probes.csv file line by line, storing whatever info required in arrays
*	remember to set the size of the matrix rows : must know size of data file before (marked by ***)
*	Probe ID number is stored in ids array
*/

public class ReadProbeAnnots extends ReadAnnots{


	public ReadProbeAnnots(String dir, int dim){
		String file = dir+"/Probes.csv";
		super.StartReading(file,dim);
	}



	public void handleRow(String line, int ri){
		///INIT SCANNER TO READ LINE
		Scanner lineSc = new Scanner(line);
        lineSc.useDelimiter(",");

		///ADD GENE NAME TO ARRAY OF GENE NAMES
		int i = 0;
	    while (lineSc.hasNext()) {
	    	///ADD PROBE ID TO ID LIST
	    	if(i==2){
	    		super.setIdAtIndex(lineSc.nextInt(),ri);
	    	}
	    	///ADD GENE SYMBOL TO ARRAY
	    	else if(i==3){
	    		super.setNameAtIndex(lineSc.next().replace("\"",""),ri);
	    	}
	    	else{
	    		lineSc.next();
	    	}
			i++;
        }

     }
}
