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
	private String outputPath = null;

	/** Logger object to output info/warnings */
    static Logger log = Logger.getLogger(HPOMngr.class.getName());

	ArrayList<String> genesInHpoButNotInAllenNames = null;
	
	/**	Matrix object containing tvalues for gene expression corresponding to 
	*	hpo term (one t value per tissue sample) */ 
	Matrix tValMatrix = null;

	/**	Expression and annotations of genes for which hpo annotation and allen
	*	data exists */
	AllenDataMngr hpoAnnotatedGeneExpressionMngr = null;


	ArrayList<String> ptg_parsedGenes = null;
	ArrayList<String> ptg_parsedPhenotypes = null;

	ArrayList<String> ptg_PhenotypeList = null;
	ArrayList<ArrayList<Integer>> ptg_GeneIndicesForPL = null;



	ArrayList<String> gtp_parsedGenes = null;
	ArrayList<String> gtp_parsedPhenotypes = null;

	ArrayList<String> gtp_GeneList = null;
	ArrayList<ArrayList<String>> gtp_PhenotypesForGL = null;
	ArrayList<String> gtp_PhenotypeList = null;
	ArrayList<ArrayList<Integer>> gtp_GeneIndicesForPL = null;

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


	public void parseHPO(){
		//	Read file line by line, storing column 2 + 3 in gene names and terms		
		log.info("Started parsing genes to phenotype");		
		ReadHPOAnnotsGeneToPhenotype gtpParser = 
			new ReadHPOAnnotsGeneToPhenotype(this.geneToPhenotypePath);
        this.gtp_parsedGenes = gtpParser.getEntrezGeneSymbols();
        this.gtp_parsedPhenotypes = gtpParser.getHpoTermNames();
        gtpParser = null;
		log.info("Finished parsing genes to phenotype : "
			+this.gtp_parsedGenes.size() 
			+" lines");
		

		//	Read PhenotypeToGene file line by line storing column
		log.info("Started parsing phenotypes to gene");
		ReadHPOAnnotsPhenotypeToGene ptgParser = 
			new ReadHPOAnnotsPhenotypeToGene(this.phenotypeToGenePath);
		this.ptg_parsedGenes = ptgParser.getEntrezGeneSymbols();
        this.ptg_parsedPhenotypes = ptgParser.getHpoTermNames();
        ptgParser = null;
        log.info("Finished parsing phenotypes to gene : "
			+this.ptg_parsedPhenotypes.size() 
			+" lines");

        gtp_OrganizeParsedData();
		ptg_OrganizeParsedData();
	}

	/**
	*	Creates Matrix object containing all expression data of genes for which 
	*	an hpo annotation exists
	*	<br>Creates AllenDataMngr object that contains this Matrix object and
	*	corresponding Gene annotations
	*	@param AllenDataMngr containing expression data + gene names
	*/
	public void getExpressionDataForHpoAnnotatedGenes(AllenDataMngr allenData){
		
		//	List of all genes for which expression data exists in allen data
		ArrayList<String> allenAllGenes = allenData.getAllGenes();
		Matrix allenExpressionData = allenData.getExpression();

		//	Init arrays to store info pertaining to genes for which annotation 
		//	and expression exists
		ArrayList<String> hpoGenesWithExpression = new ArrayList<String>();
		ArrayList<double[]> expressionForAnnotatedGenes = 
			new ArrayList<double[]>();

		//	For each gene with hpo annotation, search for gene in list of all
		//	genes in allen brain object (ie expression data exists)
		//	If expression data exists, store expression and gene
		boolean geneFound = false;
		ArrayList<Integer> genesNotFoundIndices = new ArrayList<Integer>();
		this.genesInHpoButNotInAllenNames = new ArrayList<String>();
		
		//	Iterate through every gene with hpo annotation
		for (int i = 0; i<this.gtp_GeneList.size(); i++)
		{
			String currentName = this.gtp_GeneList.get(i);
			geneFound = false;

			//	Iterate through every allen brain gene (probe)
			for (int j = 0; j<allenAllGenes.size(); j++)
			{
				//	Check if gene exists in allen brain data
				if (allenAllGenes.get(j).equals(currentName))
				{

					//	Add gene name + expression 
					double[] row = allenExpressionData.getRowAtIndex(j);
					expressionForAnnotatedGenes.add(row);
					hpoGenesWithExpression.add(currentName);

					geneFound = true;
					break;
				}
			}
			//	If gene not found add name to list
			if (geneFound == false) {
				this.genesInHpoButNotInAllenNames.add(currentName);
				genesNotFoundIndices.add(i);
			}
		}

		/*printGenesInHpoButNotInAllenData(this.genesInHpoButNotInAllenNames
			, genesNotFoundIndices);*/
        log.info("Intersection of hpo genes and allen brain genes : "
        	+expressionForAnnotatedGenes.size());

        //	Create Matrix object with expression data 
        Matrix dataMtrx = new Matrix(expressionForAnnotatedGenes);

        //	Write to file
        dataMtrx.printToFile("/Users/ahartens/Desktop/HpoannotatedTerms.csv");


        this.hpoAnnotatedGeneExpressionMngr = new AllenDataMngr();
        this.hpoAnnotatedGeneExpressionMngr.setExpression(dataMtrx);
        this.hpoAnnotatedGeneExpressionMngr.setGeneAnnotations(hpoGenesWithExpression);

	}


	/**
	*	Does unpaired t test on all genes annotated to a term. Uses phenotype to
	*	Gene parsed data so ALL GENES annotated to a term (including all its
	*	children) 
	*	Get gene expression values corresponding to this.ptg_GeneIndicesForPL
	*	Creates a 3d array 
	*	Each hpo term has a set of genes annotated to it
	*	Get expression for each of these genes and organize into a matrix
	*	So each hpo term has a matrix of expression values, each row a gene
	*	assigned to hpo term
	*	Do unpaired t test on this expression data, column by column
	*	As data is mean normalized, null hypothesis is that the mean is zero
	*/
	public void ptg_organizeHPOAnnotatedExpressionDataByHpo()
	{
		Matrix m = this.hpoAnnotatedGeneExpressionMngr.getExpression();
		ArrayList<String>geneAnnots = this.hpoAnnotatedGeneExpressionMngr.getAllGenes();
		//	Init required arrays
		ArrayList<double[]> tValues = new ArrayList<double[]>();
		ArrayList<String> tValueAnnots = new ArrayList<String>();
		ArrayList<Integer> tValueSizeN = new ArrayList<Integer>();


		log.info("Started to organize expression data by hpo");

		//	Iterate through each hpo term 
		for(int i = 0; i<this.ptg_PhenotypeList.size(); i++)
		{

			//	Get indices of genes annotated to hpo term
			ArrayList<Integer>geneIndicesForTerm = 
				this.ptg_GeneIndicesForPL.get(i);
			//	Init counter for # of genes annotated to hpo term
			int n = 0;

			//	If more than two genes annotated to term
			//	1) get expression and store in a matrix object
			//	2) do t test on each column
			if (geneIndicesForTerm.size() >= 2) 
			{
				ArrayList<double[]>expressionForTerm = 
					new ArrayList<double[]>();
				ArrayList<String> geneNamesForTerm = new ArrayList<String>();

				//	For each gene assigned to term
				//	1) get index of expression data for gene
				//	2) add expression to 
				for(int j=0; j<geneIndicesForTerm.size(); j++)
				{
					//	Index of gene in this.geneOrganizedNames
					int geneIndex = geneIndicesForTerm.get(j);
					String geneName = this.gtp_GeneList.get(geneIndex);
					//	Check if gene has hpo annotation but no allen data
					if(this.genesInHpoButNotInAllenNames.contains(geneName)){
						//log.info(geneName +" in hpo but not in allen");
					}
					//	Have both allen + hpo data
					else{
						//	Index of expression in expression matrix
						int geneIdxInMtrx = geneAnnots.indexOf(geneName);
						expressionForTerm.add(m.getRowAtIndex(geneIdxInMtrx));
						geneNamesForTerm.add(geneName);
						n++;
					}
				}
				//	Have to check again for size because some genes are not in the
				//	allen brain data and therefore some terms may have less than
				//	2 rows of expression data
				if (expressionForTerm.size() > 2) {
					
					calculateTscoreForMatrix(expressionForTerm,geneNamesForTerm,"ptg",i, tValues, tValueAnnots, tValueSizeN);

				}
			}
		}
		
		this.tValMatrix = new Matrix(tValues);
		writeTvalueMatrixToFile(this.tValMatrix, tValueAnnots,"/Users/ahartens/Desktop/Robinson/RESULTS/hpoData/PTGAllTValues.csv");
	}

	private void calculateTscoreForMatrix(ArrayList<double[]> expressionForTerm, 
		ArrayList<String> geneNames, 
		String source, 
		int i, 
		ArrayList<double[]> tValues, 
		ArrayList<String> tValueAnnots, 
		ArrayList<Integer> tValueSizeN)
	{
		Matrix matrixForTerm = new Matrix(expressionForTerm);
		//matrixForTerm.printToFile("/Users/ahartens/Desktop/Robinson/RESULTS/hpoData/"+this.ptg_PhenotypeList.get(i).replace("/","").replace(" ","_")+".csv");

		matrixForTerm.calcSummary();
		matrixForTerm.calcColumnStdDevs();

		double[] tScores = new double[matrixForTerm.getColumnSize()];
		for(int j=0; j<matrixForTerm.getColumnSize(); j++){
			double stdError = matrixForTerm.getColumnStdDev(j)/Math.sqrt(matrixForTerm.getRowSize());
			tScores[j] = matrixForTerm.getColumnMean(j)/stdError;
		}
		tValues.add(tScores);
		tValueSizeN.add(matrixForTerm.getRowSize());
		String fileName = null;
		if (source.equals("ptg")) {
			tValueAnnots.add(this.ptg_PhenotypeList.get(i));
			fileName = "/Users/ahartens/Desktop/Robinson/RESULTS/hpoData/ptg/"+this.ptg_PhenotypeList.get(i).replace("/","").replace(" ","_")+".csv";
		}
		else{
			tValueAnnots.add(this.gtp_PhenotypeList.get(i));

			fileName = "/Users/ahartens/Desktop/Robinson/RESULTS/hpoData/gtp/"+this.gtp_PhenotypeList.get(i).replace("/","").replace(" ","_")+".csv";
		}
		writeTermMatrixWithAnnotationsToFile(matrixForTerm,geneNames,fileName,tScores);

	}
	
	/**
	*	Does unpaired t test on all genes annotated to a term. Uses phenotype to
	*	Gene parsed data so ALL GENES annotated to a term (including all its
	*	children) 
	*	Get gene expression values corresponding to this.ptg_GeneIndicesForPL
	*	Creates a 3d array 
	*	Each hpo term has a set of genes annotated to it
	*	Get expression for each of these genes and organize into a matrix
	*	So each hpo term has a matrix of expression values, each row a gene
	*	assigned to hpo term
	*	Do unpaired t test on this expression data, column by column
	*	As data is mean normalized, null hypothesis is that the mean is zero
	*/
	public void gtp_organizeHPOAnnotatedExpressionDataByHpo()
	{
		Matrix m = this.hpoAnnotatedGeneExpressionMngr.getExpression();
		ArrayList<String>geneAnnots = this.hpoAnnotatedGeneExpressionMngr.getAllGenes();
		
		//	Init required arrays
		ArrayList<double[]> tValues = new ArrayList<double[]>();
		ArrayList<String> tValueAnnots = new ArrayList<String>();
		ArrayList<Integer> tValueSizeN = new ArrayList<Integer>();


		log.info("Started to organize expression data by hpo");

		//	Iterate through each hpo term 
		for(int i = 0; i<this.gtp_PhenotypeList.size(); i++)
		{
			//	Get indices of genes annotated to hpo term
			ArrayList<Integer>geneIndicesForTerm = 
				this.gtp_GeneIndicesForPL.get(i);
			//	Init counter for # of genes annotated to hpo term
			int n = 0;

			//	If more than two genes annotated to term
			//	1) get expression and store in a matrix object
			//	2) do t test on each column
			if (geneIndicesForTerm.size() >= 2) 
			{
				ArrayList<double[]>expressionForTerm = 
					new ArrayList<double[]>();
				ArrayList<String> geneNamesForTerm = new ArrayList<String>();
				//	For each gene assigned to term
				//	1) get index of expression data for gene
				//	2) add expression to 
				for(int j=0; j<geneIndicesForTerm.size(); j++)
				{
					//	Index of gene in this.geneOrganizedNames
					int geneIndex = geneIndicesForTerm.get(j);
					String geneName = this.gtp_GeneList.get(geneIndex);
					//	Check if gene has hpo annotation but no allen data
					if(this.genesInHpoButNotInAllenNames.contains(geneName)){
						//log.info(geneName +" in hpo but not in allen");
					}
					//	Have both allen + hpo data
					else{
						//	Index of expression in expression matrix
						int geneIdxInMtrx = geneAnnots.indexOf(geneName);
						expressionForTerm.add(m.getRowAtIndex(geneIdxInMtrx));
						geneNamesForTerm.add(geneName);

						n++;
					}
				}
				//	Have to check again for size because some genes are not in the
				//	allen brain data and therefore some terms may have less than
				//	2 rows of expression data
				if (expressionForTerm.size() > 2) {
					calculateTscoreForMatrix(expressionForTerm,geneNamesForTerm,"gtp",i, tValues,tValueAnnots,tValueSizeN);
				}

			}
		}
		
		this.tValMatrix = new Matrix(tValues);
		writeTvalueMatrixToFile(this.tValMatrix, tValueAnnots,"/Users/ahartens/Desktop/Robinson/RESULTS/hpoData/GTPAllTValues.csv");
	}


	//__________________________________________________________________________
    //
    //	Gene to Phenotype                              
    //__________________________________________________________________________

	/**
	*	<p>
	*	Organizes Gene To Phenotype hpo parsed data. Requires that file is 
	*	parsed into :
	*	<ol>
	*	<li>gtp_parsedGenes : contains all genes found in file. File is
	*	organized to this list, so see multiple repeats of a single gene
	*	in consecutive cells</li>
	*	<li>gtp_parsedPhenotypes : MOST SPECIFIC phenotype annotated to gene
	*	(at same index in parsedGenes). So will contain 'dystonia' rather than
	*	'neurological disorder' (as opposed to phenotype to gene, which is most 
	*	general)</li>
	*	</ol>
	*	Then organizes parsed data by gene and by phenotype.
	*	<ol>
	*	<li>Gene Organized
		*	<ol>
	   	*	<li>GeneList : list of all genes found (unique)</li>
	    *	<li>PhenotypesForGL : most specific phenotypes annotated to gene at
	    *	same row in geneList.</li>
	    *	</ol>
    *	</li>
    *	<li>Phenotype organized
		*	<ol>
	   	*	<li>PhenotypeList : list of all phenotypes found</li>
	    *	<li>GeneIndicesForPhenotypeList : index of all genes annotated to 
	    *	(most specific) phenotype at row. New array made each new phenotype 
	    *	found.</li>
	    *	</ol>
    *	</li>
    *	</ol>
    *	</p>
	*/
	public void gtp_OrganizeParsedData(){
   
        //	Init neccessary arrays
        this.gtp_GeneList = new ArrayList<String>();
        this.gtp_PhenotypesForGL = new ArrayList<ArrayList<String>>();
        
        this.gtp_PhenotypeList = new ArrayList<String>();
        this.gtp_GeneIndicesForPL = new ArrayList<ArrayList<Integer>>();


       	//	Set values for first gene
        this.gtp_GeneList.add(this.gtp_parsedGenes.get(0));
        this.gtp_PhenotypeList.add(this.gtp_parsedPhenotypes.get(0));
    	

    	//	Gene organized : Temporary array holding all phenotypes for current 
    	//	unique gene. New array made each time new gene is found
        ArrayList<String> phenotypesForCurrentGene = new ArrayList<String>();
        phenotypesForCurrentGene.add(this.gtp_parsedPhenotypes.get(0));


    	//	Phenotype organized : New array made each new phenotype found, gene
    	//	indices added as they are come across
    	ArrayList<Integer> geneIndicesForHpoTerm = new ArrayList<Integer>();
		geneIndicesForHpoTerm.add(0);
		this.gtp_GeneIndicesForPL.add(geneIndicesForHpoTerm);


        String previousGeneName = this.gtp_parsedGenes.get(0);
        //	Counter for unique gene names
        int geneIndex = 0;		
		
		//	Compare each new gene name to previous gene name: if equal just
       	//	store annotation
       	//	Otherwise add new gene name to unique gene names array        
        for (int i = 1; i<this.gtp_parsedGenes.size(); i++) 
        {
        	String currentGeneName = this.gtp_parsedGenes.get(i);

        	//	Still on same gene. Add hpo term to array of terms corresponding
        	//	to current gene
        	if (currentGeneName.equals(previousGeneName)) 
        	{
        		phenotypesForCurrentGene.add(this.gtp_parsedPhenotypes.get(i));
        	}

        	//	New gene found. 
        	//	1. Store hpoterms annotated to previous gene and
        	//	2. Init new array for current gene, add first hpo term to it
        	//	3. Add new gene to list of gene names
        	else{
        		previousGeneName = currentGeneName;
        		this.gtp_PhenotypesForGL.add(phenotypesForCurrentGene);
        		phenotypesForCurrentGene = new ArrayList<String>();
        		this.gtp_GeneList.add(currentGeneName);
        		geneIndex ++;
				
        	}
        	//	Store hpo organized info
        	gtp_AddGeneToGeneIndexForPL(i,geneIndex);
        }
        log.info("Finished organizing hpo. Unique genes with annotations : "
        	+this.gtp_GeneList.size()+" Unique HPO phenotype terms : "
        	+this.gtp_PhenotypesForGL.size());

	}

	/**
	*	<p>
	*	Called by gtp_OrganizeParsedData at each iteration to store 
	*	gene index to proper hpo phenotype annotation.
	*	</p>
	*/
	private void gtp_AddGeneToGeneIndexForPL(int i, int geneIndex ){
		
		String currentPhenotype = this.gtp_parsedPhenotypes.get(i);

		//	Check if have seen hpo term before.
		//	If previously seen, get array of gene indices corresponding 
		//	to that term and add current index
		if(this.gtp_PhenotypeList.contains(currentPhenotype)){
			//	Get array of gene indices
			//	HPO term itself is stored 
			int indexOfHpoTerm = 
				this.gtp_PhenotypeList.indexOf(currentPhenotype);
			ArrayList<Integer> geneIndicesForHpoTerm = 
				this.gtp_GeneIndicesForPL.get(indexOfHpoTerm);
			geneIndicesForHpoTerm.add(geneIndex);
		}
		
		//	If haven't see this hpoterm yet, add to unique term list and
		//	create array to add genes that correspond to it;
		else
		{
			this.gtp_PhenotypeList.add(currentPhenotype);
			ArrayList<Integer> geneIndicesForHpoTerm = new ArrayList<Integer>();
			geneIndicesForHpoTerm.add(geneIndex);
			this.gtp_GeneIndicesForPL.add(geneIndicesForHpoTerm);
		}
	}



	//__________________________________________________________________________
    //
    //	Phenotype to gene                              
    //__________________________________________________________________________


	public void ptg_OrganizeParsedData(){
		//	Init array to hold one phenotype per line
		this.ptg_PhenotypeList = new ArrayList<String>();

		//	Init first values to first row
		String currentPhenotype = this.ptg_parsedPhenotypes.get(0);
		this.ptg_PhenotypeList.add(currentPhenotype);

		//	Init array of arrays to hold gene indices corresponding to phenotype
		this.ptg_GeneIndicesForPL = new ArrayList<ArrayList<Integer>>();
		
		//	Init first array to hold indices for first phenotype
		ArrayList<Integer> currentPhenotypeGeneIndices = 
			new ArrayList<Integer>();
		ptg_addGeneIndexInGtpToPhenotype(0,currentPhenotypeGeneIndices);


		//	Iterate through each 'line'
		for (int i = 1; i< this.ptg_parsedPhenotypes.size(); i++)
		{
			//	Gene on this line belongs to current phenotype. add index
			if (currentPhenotype.equals(this.ptg_parsedPhenotypes.get(i))) 
			{
				ptg_addGeneIndexInGtpToPhenotype(i,currentPhenotypeGeneIndices);

			}
			//	New phenotype. Init new array to store gene indices
			else{
				this.ptg_GeneIndicesForPL.add(currentPhenotypeGeneIndices);
				currentPhenotypeGeneIndices = new ArrayList<Integer>();
				
				ptg_addGeneIndexInGtpToPhenotype(i,currentPhenotypeGeneIndices);
				
				currentPhenotype = this.ptg_parsedPhenotypes.get(i);
				this.ptg_PhenotypeList.add(currentPhenotype);
			}
		}

		//	Add gene indices for last phenotype
		this.ptg_GeneIndicesForPL.add(currentPhenotypeGeneIndices);

	}

	/**
	*	Get Index of current gene corresponding to expression data (which is
	*	organized by gene to phenotype file parsed data) and add to current
	*	phenotype
	*/
	private void ptg_addGeneIndexInGtpToPhenotype(int i, 
		ArrayList<Integer> currentPhenotypeGeneIndices)
	{
		String currentGene = this.ptg_parsedGenes.get(i);
		int indexCurrentGene = this.gtp_GeneList.indexOf(currentGene);
		currentPhenotypeGeneIndices.add(indexCurrentGene);
	}


	/**
	*	Print list of genes for which annotation and gene expression exists
	*/
	public void writeTermMatrixWithAnnotationsToFile(Matrix m, ArrayList<String> genes, String name, double[] tscores)
	{
		FileWriter writer = new FileWriter();
        writer.createFileWithName(name);
        writer.writeString("tscores :");
        writer.writeDelimit();
        int i;
        for(i=0; i<tscores.length-2; i++){
        	writer.writeDouble(tscores[i]);
        	writer.writeDelimit();
        }
        writer.writeDouble(tscores[i+1]);
        writer.writeNextLine();
        writer.writeNextLine();

        for(i=0; i<m.getRowSize(); i++)
        {
        	writer.writeString(genes.get(i));
        	writer.writeDelimit();
        	int j;
        	for(j = 0; j<m.getColumnSize()-2; j++){
        		writer.writeDouble(m.getValueAtIndex(i,j));
        		writer.writeDelimit();
        	}
        	writer.writeDouble(m.getValueAtIndex(i,j+1));

        	writer.writeNextLine();
        }


        writer.closeFile();
	}

	/**
	*	Print list of genes for which annotation and gene expression exists
	*/
	public void writeTvalueMatrixToFile(Matrix m, ArrayList<String> phenotypes, String name)
	{
		FileWriter writer = new FileWriter();
        writer.createFileWithName(name);

        for(int i=0; i<m.getRowSize(); i++)
        {
        	writer.writeString(phenotypes.get(i));
        	writer.writeDelimit();
        	for(int j = 0; j<m.getColumnSize(); j++){
        		writer.writeDouble(m.getValueAtIndex(i,j));
        		writer.writeDelimit();
        	}
        	writer.writeNextLine();
        }


        writer.closeFile();
	}




	/**
	*	Print list of genes for which annotation and gene expression exists
	*/
	public void writeHpoAnnotatedGenesWithExpressionToFile(
		ArrayList<String> hpoGenesWithExpression)
	{
		FileWriter writer = new FileWriter();
        writer.createFileWithName("/Users/ahartens/Desktop/Robinson/RESULTS/hpoData/HpoannotatedGenesWithExpression.txt");

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

        for(int i=0; i<this.gtp_GeneList.size(); i++)
        {
            writer.writeString(this.gtp_GeneList.get(i));
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
		for (int i = 0; i<this.ptg_GeneIndicesForPL.size() ; i++ ) {
        	ArrayList<Integer> row = this.ptg_GeneIndicesForPL.get(i);

        	for (int j=0; j<row.size() ; j++ ) {
        		int ind = row.get(j);
        		System.out.println(this.ptg_PhenotypeList.get(i)+" : "+ind+" : "
        			+this.gtp_GeneList.get(ind));
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

	public void setPtg_GeneIndicesForPL(ArrayList<ArrayList<Integer>>array){
		this.ptg_GeneIndicesForPL = array;
	}

	public void setHpoAnnotatedGeneExpressionMngr(AllenDataMngr mngr){
		this.hpoAnnotatedGeneExpressionMngr = mngr;
	}

	public void setPtg_PhenotypeList(ArrayList<String>array){
		this.ptg_PhenotypeList = array;
	}

	public void setGtp_GeneList(ArrayList<String>array){
		this.gtp_GeneList = array;
	}

	public void setGenesInHpoButNotInAllenNames(ArrayList<String>array){
		this.genesInHpoButNotInAllenNames = array;
	}

	public Matrix getTvalMatrix(){
		return this.tValMatrix;
	}

	//__________________________________________________________________________
    //
    //  Getters : till now used only by junit testing                              
    //__________________________________________________________________________

    public AllenDataMngr getHpoAnnotedGeneExpression(){
    	return this.hpoAnnotatedGeneExpressionMngr;
    }
}