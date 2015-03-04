package allen2hpo.allen;

import allen2hpo.matrix.*;
import allen2hpo.allen.parsing.*;
import allen2hpo.allen.transformations.*;


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
*	<li>Retrieve gene/tissue names given row/column indices. Used to get gene names when printing clusters</li>
*	<li>Retrieve expression data for given row/column indices.</li>
*	</ol>
*	</p>
*	@author Alex Hartenstein.
*/

public class AllenDataMngr{

	/** Ordered array of all gene abbreviations derived from probes.csv file. Order == rows in microarrayexpression.csv */
	private String[] geneNames = null;

	/** Ordered array of all gene ids from probes.csv file.*/
	private int[] geneIds = null;

	/** Ordered array of all sample (ie tissue) ids derived from SampleAnnot.csv. Order == columns in microarrayexpression.csv */
	private int[] tissueIds = null;

	/** Ordered array of all sample (ie tissue) names derived from SampleAnnot.csv. Order == columns in microarrayexpression.csv */
	private String[] tissueNames = null;

	/**	Matrix object containing all expression data. each row is a gene, each column a tissue sample */
	private Matrix data = null;

	/** String path to directory which should be parsed */
	private String dataPath = null;

	/**
	*	Constructor method sets data path of directory to be parsed
	*/
	public AllenDataMngr(String dir){
		this.dataPath = dir;
	}

	/** 
    *   Parse 3 major files in Allen Brain directory : Probes.csv, SampleAnnot.csv, MicroarrayExpression.csv
    */
	public void parseExpressionAndAnnotations(){
		
		/**
		*	Parse Probes.csv and set gene Ids and names
		*/
		ReadProbeAnnots probes = new ReadProbeAnnots(this.dataPath+"/Probes.csv");
		this.geneIds = probes.getIds();
		this.geneNames = probes.getNames();


		/**
		*	Parse SampleAnnot.csv and set tissue ids and names
		*/
		ReadTissueAnnots tissues = new ReadTissueAnnots(this.dataPath+"/SampleAnnot.csv");
		this.tissueIds = tissues.getIds();
		this.tissueNames = tissues.getNames();


		/**
		*	Parse MicroarrayExpression.csv
		*	Uses length of probes.csv and sampleannot.csv to specify dimensions of Matrix array storing data
		*	True because first column is a header specifying probe id (should be parsed and stored separately frome expression data)
		*/
		ReadExpression expression = new ReadExpression(this.dataPath+"/MicroarrayExpression.csv",this.geneNames.length,this.tissueIds.length,true);
		this.data = expression.getData();

	}

	/**
	*	Mean across rows (probes) that refer to single gene.
	*	Result is one gene one expression value.
	*/
	public void collapseRepeatProbesToUniqueGenes(){
		
		CollapseRows collapser = new CollapseRows();

		/** 
		*	Perform collapsing of rows of expression data using given row annotations (gene ids + names) 
		*/
		collapser.doCollapseRowsGivenGeneIds(this.data, this.geneIds, this.geneNames);

		/**
		*	Set this data to collapsed data
		*/
		this.data = collapser.getData();
		this.geneNames = collapser.getGeneNames();


		System.out.println("Number of unique genes : "+ this.data.getRowSize());
	}

	/**
	*	Matrix object mean normalizes itself using.<br> 
	*	normVal = val(x,y) - mean(x) - mean(y) + mean(all)
	*/
	public void meanNormalizeData(){
		this.data.meanNormalizeAcrossGenesAndSamples();
	}


	///GETTERS
	/** @return Matrix object containing all expression data */
	public Matrix getExpression(){
		return this.data;
	}

	/** @return String[] all gene names(abbreviations) corresponds to columns of expression data matrix */
	public String[] getAllGenes(){
		return this.geneNames;
	}

	/** @return int[] list of all sample/tissue ids. corresponds to columns of expression data matrix */
	public int[] getTissueIds(){
		return this.tissueIds;
	}

	/**
	*	@return String[][] of arrays of gene ids corresponding to index assignments.
	*	@param int[][] of arrays of indices of clustered genes
	*/
	public String[][] getGeneClusters(int[][] ci){
		///ci = cluster indicies
		///gc = gene clusters
		String[][] gc = new String[ci.length][];
		for(int i=0;i<ci.length;i++){
			String[] c = new String[ci[i].length];
        	gc[i] = c;
			for(int j=0; j<ci[i].length; j++){
				gc[i][j] = this.geneNames[ci[i][j]];
			}
		}
		return gc;
	}

	/**
	*	Creates a data matrix of selected columns (all rows) from original expression matrix
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

	/**
	*	Called getGenes/getTissues methods
	*	@return String[] of strings of
	*	@param int[] of ints, indices of desired name
	*	@param String[] of strings, the list from which names are selected from
	*/
	private String[] getNamesAtIndexes(int[] indexes, String[] allNames){
		String[] names = new String[indexes.length];
		for(int i = 0; i<indexes.length;i++){
			names[i] = (allNames[indexes[i]]);
		}

		return names;
	}
}
