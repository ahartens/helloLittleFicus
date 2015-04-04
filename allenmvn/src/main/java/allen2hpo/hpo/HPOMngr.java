package allen2hpo.hpo;

import allen2hpo.allen.parsing.*;
import allen2hpo.hpo.ReadHPOAnnotsGeneToPhenotype;
import allen2hpo.allen.AllenDataMngr;
import allen2hpo.matrix.Matrix;
import allen2hpo.matrix.*;

import java.util.ArrayList;
import java.io.File;
import java.io.Serializable;

import org.apache.log4j.Logger;

/**
*	<p>
*	This class is responsible for handling all hpo annotations.
*	</p>
*	@author Alex Hartenstein.
*/

public class HPOMngr implements Serializable{

	//__________________________________________________________________________
    //
    //  Variables                              
    //__________________________________________________________________________
	
	/** String path to directory which should be parsed */
	private String geneToPhenotypePath = null;
	private String phenotypeToGenePath = null;

	/** Logger object to output info/warnings */
    static Logger log = Logger.getLogger(HPOMngr.class.getName());

	/**	list of entrez gene symbols */
	ArrayList<String> entrezGeneSymbols = null;

	/**	list of hpo terms */
	ArrayList<String> hpoTermNames = null;

	/**	All genes with hpo annotation (one gene per row), no repeats */
	ArrayList<String> geneOrganizedNames = null;

    /** Parallel array to geneOrganizedNames. Each row contains corresponding 
    *	hpo terms for gene at row index */
	ArrayList<ArrayList<String>> geneOrganizedHpoTerms = null;

	/**	All hpo annotations, one term per row, no repeats */
	ArrayList<String> hpoOrganizedNames = null;

	/**	All genes annotated to hpo term found in parallel array 
	*	hpoOrganizedNames*/
	ArrayList<ArrayList<Integer>> hpoOrganizedGeneIndices = null;


	ArrayList<double[]> expressionForAnnotatedGenes = null;

	Matrix tValMatrix = null;

	AllenDataMngr hpoAnnotatedGeneExpressionMngr = null;

	//__________________________________________________________________________
    //
    //  Constructor                              
    //__________________________________________________________________________
	
	/**
	*	Constructor method sets data path of directory to be parsed
	*/
	public HPOMngr(String gtp, String ptg){
		this.geneToPhenotypePath = gtp;
		this.phenotypeToGenePath = ptg;
	}

	public HPOMngr(){
	}


	//__________________________________________________________________________
    //
    //  Public Methods                              
    //__________________________________________________________________________

	/** 
    *   Parse 3 major files in Allen Brain directory. 
    *	<br>(Probes.csv, SampleAnnot.csv, MicroarrayExpression.csv)
    */
	public void parseHPO(){
		log.info("Started parsing hpo");

		/**
		*	Read file line by line, storing column 2 + 3 in gene names and terms
		*/
		ReadHPOAnnotsGeneToPhenotype parser = 
			new ReadHPOAnnotsGeneToPhenotype(this.geneToPhenotypePath);
        this.entrezGeneSymbols = parser.getEntrezGeneSymbols();
        this.hpoTermNames = parser.getHpoTermNames();
        parser = null;
		log.info("Finished parsing hpo : "+this.entrezGeneSymbols.size() 
			+" lines");

       	organizeGeneNamesAndAnnotations();
	}
	
	/**
	*	Creates two parallel arrays
	*	this.geneOrganizedNames is list of all genes with annotations
	*	this.geneOrganizedHpoTerms contains corresponding hpo terms that are 
	*	annotated to gene (gene located at same index in 
	*	this.geneOrganizedNames)
	*	Read through parsed data organizing into unique genes and corresponding 
	*	annotations
	*/
	public void organizeGeneNamesAndAnnotations(){
        
        //	Init neccessary arrays
        this.geneOrganizedNames = new ArrayList<String>();
        this.geneOrganizedHpoTerms = new ArrayList<ArrayList<String>>();
        this.hpoOrganizedNames = new ArrayList<String>();
        this.hpoOrganizedGeneIndices = new ArrayList<ArrayList<Integer>>();


       	//	Set values for first gene
        this.geneOrganizedNames.add(this.entrezGeneSymbols.get(0));
        this.hpoOrganizedNames.add(this.hpoTermNames.get(0));
    	

    	//	Gene organized : Temporary array holding all hpo terms for current 
    	//	unique gene. New array made each time new gene is found
        ArrayList<String> hpoTermsForGene = new ArrayList<String>();
        hpoTermsForGene.add(this.hpoTermNames.get(0));


    	//	Hpo Organized : Array for gene indices annotated to whatever hpo 
    	//	term currently looking at. New array made each new hpo term found
    	//	and stored in hpoOrganizedGeneIndices
    	ArrayList geneIndicesForHpoTerm = new ArrayList<Integer>();
		geneIndicesForHpoTerm.add(0);
		this.hpoOrganizedGeneIndices.add(geneIndicesForHpoTerm);


        String previousGeneName = this.entrezGeneSymbols.get(0);
        //	Counter for unique gene names
        int geneIndex = 0;		
		
		//	Compare each new gene name to previous gene name: if equal just
       	//	store annotation
       	//	Otherwise add new gene name to unique gene names array        
        for (int i = 1; i<this.entrezGeneSymbols.size(); i++) 
        {
        	String currentGeneName = this.entrezGeneSymbols.get(i);

        	//	Still on same gene. Add hpo term to array of terms corresponding
        	//	to current gene
        	if (currentGeneName.equals(previousGeneName)) 
        	{
        		hpoTermsForGene.add(this.hpoTermNames.get(i));
        	}

        	//	New gene found. 
        	//	1. Store hpoterms annotated to previous gene and
        	//	2. Init new array for current gene, add first hpo term to it
        	//	3. Add new gene to list of gene names
        	else{
        		previousGeneName = currentGeneName;
        		this.geneOrganizedHpoTerms.add(hpoTermsForGene);
        		hpoTermsForGene = new ArrayList<String>();
        		this.geneOrganizedNames.add(currentGeneName);
        		geneIndex ++;
				
        	}
        	//	Store hpo organized info
        	addGeneIndexToHpoOrganizedList(i,geneIndex);
        }
        log.info("Finished organizing hpo. Unique genes with annotations : "
        	+this.geneOrganizedNames.size()+" Unique HPO phenotype terms : "
        	+this.hpoOrganizedNames.size());
	}

	/**
	*	Called by organizeGeneNamesAndAnnotations at each iteration to store 
	*	gene index at proper hpo annotation
	*/
	private void addGeneIndexToHpoOrganizedList(int i, int geneIndex){
		
		//	Check if have seen hpo term before.
		//	If previously seen, get array of gene indices corresponding 
		//	to that term and add current index
		if(this.hpoOrganizedNames.contains(this.hpoTermNames.get(i))){
			int indexOfHpoTerm = 
				this.hpoOrganizedNames.indexOf(this.hpoTermNames.get(i));
			ArrayList<Integer> geneIndicesForHpoTerm = 
				this.hpoOrganizedGeneIndices.get(indexOfHpoTerm);
			geneIndicesForHpoTerm.add(geneIndex);
		}
		
		//	If haven't see this hpoterm yet, add to unique term list and
		//	create array to add genes that correspond to it;
		else
		{
			this.hpoOrganizedNames.add(this.hpoTermNames.get(i));
			ArrayList<Integer> geneIndicesForHpoTerm = new ArrayList<Integer>();
			geneIndicesForHpoTerm.add(geneIndex);
			this.hpoOrganizedGeneIndices.add(geneIndicesForHpoTerm);
		}
	}

	/**
	*	Creates Matrix object containing all expression data of genes for which 
	*	an hpo annotation exists
	*	@param AllenDataMngr containing expression data + gene names
	*/
	public void getExpressionDataForHpoAnnotatedGenes(AllenDataMngr allenData){
		
		//	List of all genes for which expression data exists in allen data
		ArrayList<String> allenAllGenes = allenData.getAllGenes();
		Matrix allenExpressionData = allenData.getExpression();

		//	Init arrays to store info pertaining to genes for which annotation 
		//	and expression exists
		ArrayList<String> hpoGenesWithExpression = new ArrayList<String>();
		this.expressionForAnnotatedGenes = new ArrayList<double[]>();

		//	For each gene with hpo annotation, search for gene in list of all
		//	genes in allen brain object (ie expression data exists)
		//	If expression data exists, store expression and gene
		boolean geneFound = false;
		ArrayList<Integer> genesNotFoundIndices = new ArrayList<Integer>();
		ArrayList<String> genesNotFoundNames = new ArrayList<String>();
		
		//	Iterate through every gene with hpo annotation
		for (int i = 0; i<this.geneOrganizedNames.size(); i++)
		{
			String currentName = this.geneOrganizedNames.get(i);
			geneFound = false;

			//	Iterate through every allen brain gene (probe)
			for (int j = 0; j<allenAllGenes.size(); j++)
			{
				//	Check if gene exists in allen brain data
				if (allenAllGenes.get(j).equals(currentName))
				{

					//	Add gene name + expression 
					double[] row = allenExpressionData.getRowAtIndex(j);
					this.expressionForAnnotatedGenes.add(row);
					hpoGenesWithExpression.add(currentName);

					geneFound = true;
					break;
				}
			}
			//	If gene not found add name to list
			if (geneFound == false) {
				genesNotFoundNames.add(currentName);
				genesNotFoundIndices.add(i);
			}
		}

		/*printGenesInHpoButNotInAllenData(genesNotFoundNames
			, genesNotFoundIndices);*/
        log.info("Intersection of hpo genes and allen brain genes : "
        	+this.expressionForAnnotatedGenes.size());

        //	Create Matrix object with expression data 
        Matrix dataMtrx = new Matrix(this.expressionForAnnotatedGenes);

        //	Write to file
        dataMtrx.printToFile("/Users/ahartens/Desktop/HpoannotatedTerms.csv");
        writeHpoAnnotatedGenesWithExpressionToFile(hpoGenesWithExpression);
        printhpoOrganizedGeneIndicesByHpoTermToTerminal();

        organizeExpressionDataByHpo(dataMtrx,hpoGenesWithExpression,
        	genesNotFoundNames);

        this.hpoAnnotatedGeneExpressionMngr = new AllenDataMngr();
        this.hpoAnnotatedGeneExpressionMngr.setExpression(dataMtrx);
        this.hpoAnnotatedGeneExpressionMngr.setGeneAnnotations(hpoGenesWithExpression);

	}

	/**
	*	Get gene expression values corresponding to this.hpoOrganizedGeneIndices
	*	In effect 3d array 
	*	Each hpo term has a set of genes annotated to it
	*	Get expression for each of these genes and organize into a matrix
	*	So each hpo term has a matrix of expression values, each row a gene
	*	assigned to it
	*	Do unpaired t test on this expression data, column by column
	*	As data is mean normalized, null hypothesis is that the mean is zero and
	*	standard deviation is 1.
	*	@param Matrix expression data for genes with hpo annotation and allen
	*	brain expression value
	*	@param ArrayList<String> annotations to the matrix m. Each row is a gene
	*	name whose expression data is at same row index in matrix 
	*	@param ArrayList<String> gene names for which no expression data exists
	*/
	public void organizeExpressionDataByHpo(Matrix m, 
		ArrayList<String>geneAnnots, 
			ArrayList<String>genesInHpoButNotInAllenNames)
	{
		//	Init required arrays
		ArrayList<double[]> tValues = new ArrayList<double[]>();
		ArrayList<String> tValueAnnots = new ArrayList<String>();
		ArrayList<Integer> tValueSizeN = new ArrayList<Integer>();


		log.info("Started to organize expression data by hpo");

		//	Iterate through each hpo term 
		for(int i = 0; i<this.hpoOrganizedGeneIndices.size(); i++)
		{
			//	Get indices of genes annotated to hpo term
			ArrayList<Integer>geneIndicesForTerm = 
				this.hpoOrganizedGeneIndices.get(i);
			//	Init counter for # of genes annotated to hpo term
			int n = 0;

			//	If more than two genes annotated to term
			//	1) get expression and store in a matrix object
			//	2) do t test on each column
			if (geneIndicesForTerm.size() >= 2) 
			{
				ArrayList<double[]>expressionForTerm = 
					new ArrayList<double[]>();

				//	For each gene assigned to term
				//	1) get index of expression data for gene
				//	2) add expression to 
				for(int j=0; j<geneIndicesForTerm.size(); j++)
				{
					//	Index of gene in this.geneOrganizedNames
					int geneIndex = geneIndicesForTerm.get(j);
					String geneName = this.geneOrganizedNames.get(geneIndex);
					//	Check if gene has hpo annotation but no allen data
					if(genesInHpoButNotInAllenNames.contains(geneName)){
						log.info(geneName +" in hpo but not in allen");
					}
					//	Have both allen + hpo data
					else{
						//	Index of expression in expression matrix
						int geneIdxInMtrx = geneAnnots.indexOf(geneName);
						expressionForTerm.add(m.getRowAtIndex(geneIdxInMtrx));
						n++;
					}
				}
				//	Have to check again for size because 
				if (expressionForTerm.size() > 2) {
					
				
					Matrix matrixForTerm = new Matrix(expressionForTerm);
					matrixForTerm.calcSummary();
					matrixForTerm.calcColumnStdDevs();

					double[] tScores = new double[matrixForTerm.getColumnSize()];
					for(int j=0; j<matrixForTerm.getColumnSize(); j++){
						double stdError = matrixForTerm.getColumnStdDev(j)/Math.sqrt(matrixForTerm.getRowSize());
						tScores[j] = matrixForTerm.getColumnMean(j)/stdError;
					}
					tValues.add(tScores);
					tValueAnnots.add(this.hpoOrganizedNames.get(i));
					tValueSizeN.add(n);
				}
			}
		}
		
		this.tValMatrix = new Matrix(tValues);
		this.tValMatrix.printToFile("/Users/ahartens/Desktop/AllTValues.csv");
	}

	/**
	*	Print list of genes for which annotation and gene expression exists
	*/
	public void writeHpoAnnotatedGenesWithExpressionToFile(
		ArrayList<String> hpoGenesWithExpression)
	{
		FileWriter writer = new FileWriter();
        writer.createFileWithName("/Users/ahartens/Desktop/HpoannotatedGenesWithExpression.txt");

        for(int i=0; i<hpoGenesWithExpression.size(); i++)
        {
            writer.writeString(hpoGenesWithExpression.get(i));
            writer.writeNextLine();
        }
        writer.closeFile();
	}

	/**
	*	Print list of all genes that have an hpo annotation
	*/
	public void writeUniqueAnnotatedGenesToFile(){

        FileWriter writer = new FileWriter();
        writer.createFileWithName("/Users/ahartens/Desktop/HpoannotatedTermsGeneNames.txt");

        for(int i=0; i<geneOrganizedNames.size(); i++)
        {
            writer.writeString(geneOrganizedNames.get(i));
            writer.writeNextLine();
        }
        writer.closeFile();
	}

	/**
	*	Prints this.hpoOrganizedGeneIndices (by hpo term) to terminal
	*	Each row has hpo term then index of gene in this.geneOrganizedNames 
	*	followed by gene name 
	*/
	public void printhpoOrganizedGeneIndicesByHpoTermToTerminal(){
		for (int i = 0; i<this.hpoOrganizedGeneIndices.size() ; i++ ) {
        	ArrayList<Integer> row = this.hpoOrganizedGeneIndices.get(i);

        	for (int j=0; j<row.size() ; j++ ) {
        		int ind = row.get(j);
        		System.out.println(this.hpoOrganizedNames.get(i)+" : "+ind+" : "
        			+this.geneOrganizedNames.get(ind));
        	}
        }
	}

	private void printGenesInHpoButNotInAllenData(ArrayList<String>names, 
		ArrayList<Integer>indices){
		for(int i =0;i<names.size(); i++){
			log.info("not found : "+indices.get(i)
				+" : "+names.get(i));
		}
	}



	//__________________________________________________________________________
    //
    //  Setters : till now used only by junit testing                              
    //__________________________________________________________________________


	public void setHpoOrganizedGeneIndices(ArrayList<ArrayList<Integer>> array){
		this.hpoOrganizedGeneIndices = array;
	}

	public void setGeneOrganizedNames(ArrayList<String> array){
		this.geneOrganizedNames = array;
	}

	public void setHpoOrganizedNames(ArrayList<String> array){
		this.hpoOrganizedNames = array;
	}

	public Matrix getTvalMatrix(){
		return this.tValMatrix;
	}

	//__________________________________________________________________________
    //
    //  Setters : till now used only by junit testing                              
    //__________________________________________________________________________

    public AllenDataMngr getHpoAnnotedGeneExpression(){
    	return this.hpoAnnotatedGeneExpressionMngr;
    }
}