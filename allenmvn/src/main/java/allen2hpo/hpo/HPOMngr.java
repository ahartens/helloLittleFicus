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

	ArrayList<String> uniqueGeneSymbols = null;


	ArrayList<ArrayList<String>> hpoTermNamesOrganized = null;


	ArrayList<String> uniqueHpoTerms = null;

	ArrayList<ArrayList<Integer>> geneNamesOrganized = null;


	ArrayList<double[]> expressionForAnnotatedGenes = null;

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

       	organizeGeneNamesAndAnnotations();
	}
	
	/**
	*	Creates two parallel arrays
	*	this.uniqueGeneSymbols is singular list of all genes with annotations
	*	this.hpoTermNamesOrganized contains corresponding hpo terms that are 
	*	annotated to gene (gene located at same index in this.uniqueGeneSymbols)
	*/
	public void organizeGeneNamesAndAnnotations(){
		 /**
        *	Read through parsed data organizing into unique genes and 
        *	corresponding annotations
        */
        
        /**
        *	Init array for all genes with hpo annotation (one gene per row)
        */
        this.uniqueGeneSymbols = new ArrayList<String>();

        /**
        *	Init array of arrays. each row contains all hpo terms for row gene
        */
        this.hpoTermNamesOrganized = new ArrayList<ArrayList<String>>();

        /**
        *	Init array for all genes with hpo annotation (one gene per row)
        */
        this.uniqueHpoTerms = new ArrayList<String>();

        /**
        *	Init array of arrays. each row contains all hpo terms for row gene
        */
        this.geneNamesOrganized = new ArrayList<ArrayList<Integer>>();

        /**
        *	temporary array holding all hpo terms for current unique gene
        */
        ArrayList<String> currentHpoTerms = new ArrayList<String>();

       	/**
       	*	Add first gene name/corresponding term to arrays and set variable
       	*	previous gene name
       	*	Compare each new gene name to previous gene name: if the same just
       	*	store annotation
       	*	Otherwise add new gene name to unique gene names array
       	*/
        currentHpoTerms.add(this.hpoTermNames.get(0));
        this.uniqueGeneSymbols.add(this.entrezGeneSymbols.get(0));
       

        this.uniqueHpoTerms.add(this.hpoTermNames.get(0));
    	ArrayList rowForTerm = new ArrayList<Integer>();
		rowForTerm.add(0);
		this.geneNamesOrganized.add(rowForTerm);

        String previousGeneName = this.entrezGeneSymbols.get(0);
        int geneIndex = 0;
        for (int i = 1; i<this.entrezGeneSymbols.size(); i++) 
        {
        	String currentGeneName = this.entrezGeneSymbols.get(i);

        	// Still on same gene. 
        	if (currentGeneName.equals(previousGeneName)) 
        	{
        		/**
        		*	Add hpo term to array of terms corresponding to current gene
        		*/
        		currentHpoTerms.add(this.hpoTermNames.get(i));

        		/*
        		*	Add gene index to array of gene indices corresponding to hpo
        		*	term
        		*/
        		addGeneIndexToHpoOrganizedList(i,geneIndex);

        	}
        	else{
        		previousGeneName = currentGeneName;
        		this.hpoTermNamesOrganized.add(currentHpoTerms);
        		currentHpoTerms = new ArrayList<String>();
        		this.uniqueGeneSymbols.add(currentGeneName);
        		geneIndex ++;
        		/*
        		*	Add gene index to array of gene indices corresponding to hpo
        		*	term
        		*/
        		addGeneIndexToHpoOrganizedList(i,geneIndex);
        	}
        }
        log.info("Finished parsing hpo : " +this.entrezGeneSymbols.size());
	}

	private void addGeneIndexToHpoOrganizedList(int i, int geneIndex){
		//	Check if have seen hpo term before.
		//	If previously seen, get array of gene indices corresponding 
		//	to that term and add current index
		if(this.uniqueHpoTerms.contains(this.hpoTermNames.get(i))){
			int indexOfHpoTerm = this.uniqueHpoTerms.indexOf(this.hpoTermNames.get(i));
			ArrayList<Integer> rowForTerm = this.geneNamesOrganized.get(indexOfHpoTerm);
			rowForTerm.add(geneIndex);
		}
		//	If haven't see this hpoterm yet, add to unique term list and
		//	create array to add genes that correspond to it;
		else
		{
			this.uniqueHpoTerms.add(this.hpoTermNames.get(i));
			ArrayList<Integer> rowForTerm = new ArrayList<Integer>();
			rowForTerm.add(geneIndex);
			this.geneNamesOrganized.add(rowForTerm);
		}
	}

	/**
	*	Creates Matrix object containing all expression data of genes for which 
	*	an annotation exists
	*/
	public void getExpressionDataForHpoAnnotatedGenes(AllenDataMngr allenData){
		/**
		*	Get list of all genes for which expression data exists in current brain
		*/
		ArrayList<String> allenAllGenes = allenData.getAllGenes();
		/**
		*	Get expression data for all genes
		*/
		Matrix allenExpressionData = allenData.getExpression();

		/**
		*	Init array for genes for which annotation and expression exists
		*/
		ArrayList<String> hpoGenesWithExpression = new ArrayList<String>();

		/**
		*	Init array for expression of genes with annotation and expression
		*/
		this.expressionForAnnotatedGenes = new ArrayList<double[]>();

		/**
		*	For each gene with hpo annotation, search for gene in list of all
		*	genes in allen brain object (ie expression data exists)
		*	If expression data exists, store expression and gene
		*/
		boolean geneFound = false;
		ArrayList<Integer> genesNotFoundIndices = new ArrayList<Integer>();
		ArrayList<String> genesNotFoundNames = new ArrayList<String>();
		for (int i = 0; i<this.uniqueGeneSymbols.size(); i++)
		{

			String currentName = this.uniqueGeneSymbols.get(i);
			geneFound = false;
			for (int j = 0; j<allenAllGenes.size(); j++)
			{
				if(allenAllGenes.get(j).equals(currentName))
				{
					double[] row = allenExpressionData.getRowAtIndex(j);

					this.expressionForAnnotatedGenes.add(row);
					hpoGenesWithExpression.add(currentName);
					geneFound = true;
					break;
				}
			}
			if (geneFound == false) {
				genesNotFoundNames.add(currentName);
				genesNotFoundIndices.add(i);
			}
		}

		/*printGenesInHpoButNotInAllenData(genesNotFoundNames
			, genesNotFoundIndices);*/
        log.info("found : " +this.expressionForAnnotatedGenes.size());
        Matrix dataMtrx = new Matrix(this.expressionForAnnotatedGenes);
        dataMtrx.printToFile("/Users/ahartens/Desktop/HpoannotatedTerms.csv");
        writeHpoAnnotatedGenesWithExpressionToFile(hpoGenesWithExpression);

        organizeExpressionDataByHpo(dataMtrx,hpoGenesWithExpression,genesNotFoundNames);
        printGeneNamesOrganizedByHpoTermToTerminal();
	}

	public void organizeExpressionDataByHpo(Matrix m, ArrayList<String>geneAnnots, ArrayList<String>genesInHpoButNotInAllenNames){
		ArrayList<double[]> tValues = new ArrayList<double[]>();
		ArrayList<String> tValueAnnots = new ArrayList<String>();
		ArrayList<Integer> tValueSizeN = new ArrayList<Integer>();
		log.info("Started to organize exppresion data by hpo");
		for(int i = 0; i<this.geneNamesOrganized.size(); i++)
		{
			ArrayList<Integer>geneIndicesForTerm = this.geneNamesOrganized.get(i);
			int n = 0;
			if (geneIndicesForTerm.size() >= 2) {
				

				

				ArrayList<double[]>geneExpressionForTerm = new ArrayList<double[]>();

				for(int j=0; j<geneIndicesForTerm.size(); j++)
				{
					int geneIndex = geneIndicesForTerm.get(j);
					String geneName = this.uniqueGeneSymbols.get(geneIndex);
					if(genesInHpoButNotInAllenNames.contains(geneName)){
						log.info(geneName +" in hpo but not in allen");
					}
					else{
						log.info(geneName +" added to matrix");
						int geneIndexInMatrix = geneAnnots.indexOf(geneName);
						geneExpressionForTerm.add(m.getRowAtIndex(geneIndexInMatrix));
						n++;
					}
				}
				if (geneExpressionForTerm.size() > 2) {
					
				
					Matrix matrixForTerm = new Matrix(geneExpressionForTerm);
					matrixForTerm.calcSummary();
					matrixForTerm.calcColumnStdDevs();

					double[] tScores = new double[matrixForTerm.getColumnSize()];
					for(int j=0; j<matrixForTerm.getColumnSize(); j++){
						tScores[j] = matrixForTerm.getColumnMean(j)/(matrixForTerm.getColumnStdDev(j)/Math.sqrt(n));
					}
					tValues.add(tScores);
					tValueAnnots.add(this.uniqueHpoTerms.get(i));
					tValueSizeN.add(n);
				}
			}
		}
		Matrix tValMatrix = new Matrix(tValues);
		tValMatrix.printToFile("/Users/ahartens/Desktop/AllTValues.csv");

		for(int i =0; i<tValueAnnots.size(); i++){
			System.out.println(i  + " : " + tValueSizeN.get(i) + " : " + tValueAnnots.get(i));
		}
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

        for(int i=0; i<uniqueGeneSymbols.size(); i++)
        {
            writer.writeString(uniqueGeneSymbols.get(i));
            writer.writeNextLine();
        }
        writer.closeFile();
	}

	/**
	*	Prints this.geneNamesOrganized (by hpo term) to terminal
	*	Each row has hpo term then index of gene in this.uniqueGeneSymbols 
	*	followed by gene name 
	*/
	public void printGeneNamesOrganizedByHpoTermToTerminal(){
		for (int i = 0; i<this.geneNamesOrganized.size() ; i++ ) {
        	ArrayList<Integer> row = this.geneNamesOrganized.get(i);

        	for (int j=0; j<row.size() ; j++ ) {
        		int ind = row.get(j);
        		System.out.println(this.uniqueHpoTerms.get(i)+" : "+ind+" : "
        			+this.uniqueGeneSymbols.get(ind));
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

}