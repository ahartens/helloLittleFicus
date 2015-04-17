package allen2hpo.hpo;

import allen2hpo.allen.parsing.ReadAnnots;

import java.util.Scanner;
import java.util.ArrayList;
import java.io.File;



/**
*	Reads /Probes.csv file line by line, storing whatever info required in 
*	arrays
*	Probe ID number is stored in ids array
*/
public class ReadHPOAnnotsGeneToPhenotype extends ReadAnnots{

	//__________________________________________________________________________
    //
    //  Variables                             
    //__________________________________________________________________________

	/**	list of entrez gene symbols */
	ArrayList<String> entrezGeneSymbols = null;

	/**	list of hpo terms */
	ArrayList<String> hpoTermNames = null;

	//__________________________________________________________________________
    //
    //  Getters                             
    //__________________________________________________________________________

	/**
	*
	*/
	public ArrayList<String> getEntrezGeneSymbols(){
		return this.entrezGeneSymbols;
	}

	/**
	*
	*/
	public ArrayList<String> getHpoTermNames(){
		return this.hpoTermNames;
	}



	//__________________________________________________________________________
    //
    //  Constructor                             
    //__________________________________________________________________________

	public ReadHPOAnnotsGeneToPhenotype(String file){
		/*
		*	Set filename to be parsed
		*/
		super.setFilename(file);

		/*
		*	Init arraylists to read data
		*/
		this.entrezGeneSymbols = new ArrayList<String>();
		this.hpoTermNames = new ArrayList<String>();

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

		
		Scanner lineSc = new Scanner(line);
		lineSc.useDelimiter("\t");
		int i = 0;
		while (lineSc.hasNext()) {
			if(i==1){
				this.entrezGeneSymbols.add(lineSc.next());
			}
			else if(i==2){
				this.hpoTermNames.add(lineSc.next());
			}
			else{
				lineSc.next();
			}
			i++;
		}

			
    }
}
