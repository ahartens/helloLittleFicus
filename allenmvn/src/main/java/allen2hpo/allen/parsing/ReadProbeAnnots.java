package allen2hpo.allen.parsing;

import allen2hpo.allen.parsing.ReadAnnots;

import java.util.Scanner;

import java.util.ArrayList;
/**
*	Reads Probes.csv file line by line, storing whatever info required in 
*	arrays.
*	Probe ID number is stored in ids array
*/

public class ReadProbeAnnots extends ReadAnnots{

	private ArrayList<Integer> indicesOfUnknownProbes = null;
	//__________________________________________________________________________
    //
    //  Constructor                             
    //__________________________________________________________________________

	public ReadProbeAnnots(String file){		
		/*
		*	Set filename to be parsed
		*/
		super.setFilename(file);

		/*
		*	Count number of lines
		*/
		super.countLines();

		this.indicesOfUnknownProbes = new ArrayList<Integer>();

		/*
		*	Begin reading file line by line. Line is handled here.
		*/
		super.parseFile();
	}



	//__________________________________________________________________________
    //
    //	Getters                              
    //__________________________________________________________________________

	public ArrayList<Integer> getIndicesUnknownProbes(){
		return this.indicesOfUnknownProbes;
	}


	
	//__________________________________________________________________________
    //
    //	Methods                              
    //__________________________________________________________________________

	/**
	*	Handle single row of Probes.csv table. Store data in relevant fields.
	*/
	public void handleRow(String line, int ri)
	{

		/*
		*	Init scanner to read line with csv delimiter
		*/
		Scanner lineSc = new Scanner(line);
        lineSc.useDelimiter(",");

        /*
        *	Read through fields.
        */
		int i = 0;

		String probeName = null;
		String geneName = null;
	    while (lineSc.hasNext()) 
	    {
	    	if (i == 1){
	    		probeName = lineSc.next();
	    	}
	    	/*
	    	*	Add probe ID to id list
	    	*/
	    	else if(i==2)
	    	{
	    		super.setIdAtIndex(lineSc.nextInt(),ri);
	    	}
	    	/*
	    	*	Add gene symbol to array (gene name)
	    	*/
	    	else if(i==3)
	    	{
	    		geneName = lineSc.next();
	    		if (probeName.equals(probeName)){
	    			this.indicesOfUnknownProbes.add(ri);
	    		}
	    		super.setNameAtIndex(geneName.replace("\"",""),ri);
	    	}
	    	else{
	    		lineSc.next();
	    	}
			i++;
        }
     }
}
