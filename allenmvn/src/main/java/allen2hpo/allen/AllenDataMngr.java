package allen2hpo.allen;

import allen2hpo.matrix.*;
import allen2hpo.allen.parsing.*;
import allen2hpo.allen.transformations.*;
import allen2hpo.allen.ontology.*;

import java.io.Serializable;
import java.util.ArrayList;

import org.apache.log4j.Logger;

/**
*	<p>
*	This class is responsible for handling all data corresponding to one brain.
*	<br>It is able to :
*	<ol>
*	<li>parse and store all data corresponding to an allen brain directory
*	(expression, probe/sample (ie gene/tissue) annotations).</li>
*	<li>Mean across all rows that refer to the same gene.</li>
*	<li>Mean normalize all expression data.</li>
*	</ol>
*	In addition, is responsible for relating expression data to annotations.
*	<ol>
*	<li>Retrieve gene/tissue names given row/column indices. Used to get gene 
*	names when printing clusters</li>
*	<li>Retrieve expression data for given row/column indices.</li>
*	</ol>
*	</p>
*	@author Alex Hartenstein.
*/

public class AllenDataMngr implements Serializable{

	//__________________________________________________________________________
    //
    //  Variables                              
    //__________________________________________________________________________

	/** Ordered array of all gene abbreviations derived from probes.csv file. 
	*	Order == rows in microarrayexpression.csv */
	private ArrayList<String> geneNames = null;

	/** Ordered array of all gene ids from probes.csv file.*/
	private ArrayList<Integer> geneIds = null;

	/** Ordered array of all sample (ie tissue) ids derived from 
	*	SampleAnnot.csv. Order == columns in microarrayexpression.csv */
	private ArrayList<Integer> tissueIds = null;

	/** Ordered array of all sample (ie tissue) names derived from 
	*	SampleAnnot.csv. Order == columns in microarrayexpression.csv */
	private ArrayList<String> tissueNames = null;

	/**	Ordered array of MRI voxels for tissues parsed from SampleAnnot */
	private double[][] tissueLocations = null;

	/** ArrayList containing row index of probes whose probe name and gene name
	*	are identical (column 1 and column 2 of probes.csv are identical) */
	private ArrayList<Integer> indicesUnknownProbes = null;

	/**	Matrix object containing all expression data. each row is a gene, each 
	*	column a tissue sample */
	private Matrix data = null;

	/** String path to directory which should be parsed */
	private String dataPath = null;

	/** Logger object to output info/warnings */
    static Logger log = Logger.getLogger(AllenDataMngr.class.getName());



	//__________________________________________________________________________
    //
    //  Constructor                              
    //__________________________________________________________________________
	
	/**
	*	Constructor method sets data path of directory to be parsed
	*/
	public AllenDataMngr(String dir){
		this.dataPath = dir;
	}



	//__________________________________________________________________________
    //
    //  Public Methods                              
    //__________________________________________________________________________

	/** 
    *   Parse 3 major files in Allen Brain directory. 
    *	<br>(Probes.csv, SampleAnnot.csv, MicroarrayExpression.csv)
    */
	public void parseExpressionAndAnnotations(){
		
		/**
		*	Parse Probes.csv and set gene Ids and names
		*/
		ReadProbeAnnots probes = 
			new ReadProbeAnnots(this.dataPath+"/Probes.csv");
		this.geneIds = probes.getIds();
		this.geneNames = probes.getNames();
		this.indicesUnknownProbes = probes.getIndicesUnknownProbes();


		/**
		*	Parse SampleAnnot.csv and set tissue ids and names
		*/
		ReadTissueAnnots tissues = 
			new ReadTissueAnnots(this.dataPath+"/SampleAnnot.csv");
		this.tissueIds = tissues.getIds();
		this.tissueNames = tissues.getNames();
		this.tissueLocations = tissues.getMriVoxel();

		/**
		*	Parse MicroarrayExpression.csv
		*	Uses length of probes.csv and sampleannot.csv to specify dimensions 
		*	of Matrix array storing data
		*	True because first column is a header specifying probe id (should 
		*	be parsed and stored separately frome expression data)
		*/
		ReadExpression expression = 
			new ReadExpression(this.dataPath+"/MicroarrayExpression.csv",
				this.geneNames.size(),this.tissueIds.size(),true);
		this.data = expression.getData();

		/*System.out.println("sample annotations parsed");
		if (logger.isInfoEnabled()){
            logger.info("Sample Annotations parsed");
        }*/
        log.info("Sample Annotations parsed");

    }

   

	/**
	*	Mean across rows (probes) that refer to single gene.
	*	Result is one gene one expression value.
	*/
	public void collapseRepeatProbesToUniqueGenes(){
		
		CollapseRows collapser = new CollapseRows();

		/** 
		*	Perform collapsing of rows of expression data using given row 
		*	annotations (gene ids + names) 
		*/
		collapser.doCollapseRowsGivenGeneIds(this.data, this.geneIds, 
			this.geneNames);

		/**
		*	Set this data to collapsed data
		*/
		this.data = collapser.getData();
		this.geneNames = collapser.getGeneNames();


		log.info("Number of unique genes : "
			+ this.data.getRowSize());
	}

	public void collapseTissuesToSelectedParents(OntologyDataMngr ontology){
		 /*
        *   
        */
        CollapseColumns make5columns = new CollapseColumns(this.data,this.tissueIds,ontology);
        
        make5columns.collapseExample();

        this.data = make5columns.getDataCollapsed();
	}

	/**
	*	Matrix object mean normalizes itself using.<br> 
	*	normVal = val(x,y) - mean(x) - mean(y) + mean(all)
	*/
	public void meanNormalizeData(){
		this.data.meanNormalizeAcrossGenesAndSamples();
	}


	/**
	*	Calculate distance matrix for each tissue/sample to every other tissue
	*	sample based on mri voxel.
	*/
	public void calculateDistanceMatrixForTissueLocations(){

		DistanceMatrix tissueLocationsMatrix = 
			new DistanceMatrix(new Matrix(this.tissueLocations));
	}

	public void removeUnknownProbeData(){
		log.info("Number of unknown probes : "
				+ this.indicesUnknownProbes.size());
		log.info("Number of gene Names : "
			+ this.geneNames.size());
		log.info("Number of gene Ids Names : "
			+ this.geneIds.size());
		log.info("Number of expression rows : "
			+ this.data.getRowSize());

		for (int i = 0; i<this.indicesUnknownProbes.size(); i++){
			System.out.println("unknown :"+this.indicesUnknownProbes.get(i));
		}

		for(int i=0; i<this.geneNames.size(); i++){
			System.out.println(this.geneNames.get(i));
			//System.out.println(this.geneIds.get(i));

		}
		for(int i=this.indicesUnknownProbes.size()-1; i>=0; i--){
			System.out.println("removing "+i);
			int index = this.indicesUnknownProbes.get(i);
			this.geneNames.remove(index);
			this.geneIds.remove(index);
			this.data.removeRowAtIndex(index);

		}

		for(int i=0; i<this.geneNames.size(); i++){
			System.out.println(this.geneNames.get(i));
			//System.out.println(this.geneIds.get(i));

		}
	}


	//__________________________________________________________________________
    //
    //  Getters                              
    //__________________________________________________________________________

    /** 
	*	@return Matrix object containing all expression data 
	*/
	public Matrix getExpression(){
		return this.data;
	}

	/** 
	*	@return String []all gene names(abbreviations) corresponds to columns of
	*	expression data matrix 
	*/
	public ArrayList<String> getAllGenes(){
		return this.geneNames;
	}

	/** 
	*	@return int [] list of all sample/tissue ids. corresponds to columns of 
	*	expression data matrix 
	*/
	public ArrayList<Integer> getTissueIds(){
		return this.tissueIds;
	}

	/**
	*	@return String [][] of arrays of gene ids corresponding to index 
	*	assignments.
	*	@param int [][] of arrays of indices of clustered genes
	*/
	public String[][] getGeneClusters(int[][] ci){
		///ci = cluster indicies
		///gc = gene clusters
		String[][] gc = new String[ci.length][];
		for(int i=0;i<ci.length;i++){
			String[] c = new String[ci[i].length];
        	gc[i] = c;
			for(int j=0; j<ci[i].length; j++){
				gc[i][j] = this.geneNames.get(ci[i][j]);
			}
		}
		return gc;
	}

	/**
	*	Creates a data matrix of selected columns (all rows) from original 
	*	expression matrix
	*	@return Matrix object
	*	@param int[] of int (desired column indices)
	*/
	public Matrix getExpressionDataForTissues(int[] tissueIndices){
		double[][] e = new double[this.data.getRowSize()][tissueIndices.length];
		for (int i=0; i<e.length; i++){
			for (int j=0; j<e[0].length; j++){
				e[i][j] = this.data.getValueAtIndex(i,tissueIndices[j]);
			}
		}
		Matrix m = new Matrix(e);
		return m;
	}

	/**
	*	@return String[] array of strings
	*	@param int[] array of ints, corresponding to rows indices
	*/
	public String[] getGenesAtIndexes(int[] indexes){
		return getNamesAtIndexes(indexes,this.geneNames);
	}

	/**
	*	@return String[] 1d array of strings
	*	@param int[] 1d array of ints, corresponding to rows indices
	*/
	public String[] getTissuesAtIndexes(int[] indexes){
		return getNamesAtIndexes(indexes,this.tissueNames);
	}

	public double[][] getTissueLocations(){
		return this.tissueLocations;
	}

	/**
	*	Called getGenes/getTissues methods
	*	@return String[] of strings of
	*	@param int[] indices of desired name
	*	@param String[] list from which names are selected from
	*/
	private String[] getNamesAtIndexes(int[] indexes, ArrayList<String> allNames){
		String[] names = new String[indexes.length];
		for(int i = 0; i<indexes.length;i++){
			names[i] = allNames.get(indexes[i]);
		}

		return names;
	}

}
