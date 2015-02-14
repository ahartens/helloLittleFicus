package allen2hpo.allen;

import allen2hpo.matrix.*;

/**
*	This class is responsible for handling all data corresponding to one brain.
*	It is able to parse all data corresponding to an allen brain directory
*	eg (Expression, Probe/Sample (ie gene/tissue) Annotations)
*	@author Alex Hartenstein.
*/

public class AllenDataMngr{

	/** Ordered list of all gene abbreviations derived from probes.csv file. Order == rows in microarrayexpression.csv */
	private String[] geneNames = null;

	/** Ordered list of all gene ids from probes.csv file.*/
	private int[] geneIds = null;

	/** Ordered array of all sample (ie tissue) ids derived from SampleAnnot.csv. Order == columns in microarrayexpression.csv */
	private int[] tissueIds = null;

	/** Ordered array of all sample (ie tissue) names derived from SampleAnnot.csv. Order == columns in microarrayexpression.csv */
	private String[] tissueNames = null;

	/**	Matrix object containing all expression data. each row is a gene, each column a tissue sample */
	private Matrix data = null;

	/**
	*	Constructor method parses data provided in allenbrain directory
	*/
	public AllenDataMngr(String dir, int dim){


		//Parse gene + sample tissue names
		readAnnotations(dir,dim);

		//Parse expression data
		ReadExpression expression = new ReadExpression(dir+"/MicroarrayExpression.csv",this.geneNames.length,this.tissueIds.length,true);
		//this.data = expression.getData();

		///Mean across rows that refer to single gene
		CollapseRows collapser = new CollapseRows(expression.getData(),this.geneIds, this.geneNames);
		this.data = collapser.getData();
		this.geneNames = collapser.getGeneNames();
		System.out.println("Number of unique genes : "+ this.data.getRowSize());

		this.data.meanNormalizeAcrossGenesAndSamples();


		//CovarMatrix cm = new CovarMatrix();
		//cm.covarCalcMatrix(this.data,1);

	}

	/**
	*	Parses annotations corresponding to given microarray experiment (probes + samples)
	*	@param string of directory where files are to be found
	*	@param int number of expected rows
	*/
	private void readAnnotations(String dir, int dim){

		///Parse gene names
		ReadProbeAnnots probes = new ReadProbeAnnots(dir,dim);
		ReadTissueAnnots tissues = new ReadTissueAnnots(dir+"/SampleAnnot.csv",1840);

		///Set variables
		this.geneIds = probes.getIds();
		this.geneNames = probes.getNames();

		this.tissueIds = tissues.getIds();
		this.tissueNames = tissues.getNames();
	}

	///GETTERS
	/** @return matrix object containing all expression data */
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
	*	@return array of arrays of gene ids corresponding to index assignments.
	*	@param array of arrays of indices of clustered genes
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
	*	Creates a datamatrix of selected columns (all rows) from original expression matrix
	*	@return Matrix object
	*	@param array of int (desired column indices)
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
	*	@return 1d array of strings
	*	@param 1d array of ints, corresponding to rows indices
	*/
	public String[] getGenesAtIndexes(int[] indexes){
		return getNamesAtIndexes(indexes,this.geneNames);
	}

	/**
	*	@return 1d array of strings
	*	@param 1d array of ints, corresponding to rows indices
	*/
	public String[] getTissuesAtIndexes(int[] indexes){
		return getNamesAtIndexes(indexes,this.tissueNames);
	}

	/**
	*	Called getGenes/getTissues methods
	*	@return array of strings of
	*	@param array of ints, indices of desired name
	*	@param array of strings, the list from which names are selected from
	*/
	private String[] getNamesAtIndexes(int[] indexes, String[] allNames){
		String[] names = new String[indexes.length];
		for(int i = 0; i<indexes.length;i++){
			names[i] = (allNames[indexes[i]]);
		}

		return names;
	}
}
