package allen2hpo.allen;

import allen2hpo.matrix.*;


/**
*	This class is responsible for handling all data corresponding to one brain.
*	It is able to parse all data corresponding to an allen brain directory (Expression, Gene/ Tissue Annotations)
*	@author Alex Hartenstein.
*/

public class AllenData{

	/** Ordered list of all gene abbreviations derived from probes.csv file. Order == rows in microarrayexpression.csv */
	private String[] geneNames = null;

	/** Ordered array of all sample (ie tissue) ids derived from SampleAnnot.csv. Order == columns in microarrayexpression.csv */
	private int[] tissueIds = null;

	/** Ordered array of all sample (ie tissue) names derived from SampleAnnot.csv. Order == columns in microarrayexpression.csv */
	private String[] tissueNames = null;

	/** OntologyData class responsible for all queries/handling of Ontology.csv */
	private OntologyData ontology = null;

	/**	Matrix object containing all expression data. each row is a gene, each column a tissue sample */
	private Matrix data = null;


	/**
	*	Constructor method parses
	*/
	public AllenData(String dir, int dim){


		///Parse gene + sample tissue names

		readAnnotations(dir,dim);

		///Parse ontology (all tissue names)
		this.ontology = new OntologyData(dir,dim);

		///Parse expression data
		//ReadExpression expression = new ReadExpression(dir+"/MicroarrayExpression.csv",probes.getCount(),tissues.getCount(),true);




		//this.data = expression.getData();
		//this.data.meanNormalizeAcrossGenesAndSamples();


		//CovarMatrix cm = new CovarMatrix();
		//cm.covarCalcMatrix(this.data,1);


		/*ReadExpression clusters = new ReadExpression("/Users/ahartens/Desktop/RCorrelation.csv",947,tissues.getCount(),false);
		this.data = clusters.getData();
		DataPrinterForGraphs printer = new DataPrinterForGraphs();
		print2dMatrixAsJson(this.data);*/


		ReadExpression clusters = new ReadExpression(dir + "/ClusterPrototypes.csv",10,this.tissueIds.length,true);
		this.data = clusters.getData();
		int[] brain = {4009,4268,4219,4084,4132, 4180};
		collapseSamples(brain);

		///frontal, insula, limbic lob, parietal,temporal, occipital
		//int[] brain = {4007,4391,9001,4833,9512};
		///Tel,di,mes,met,myl
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
		this.geneNames = probes.getData();
		this.tissueIds = tissues.getIds();
		this.tissueNames = tissues.getNames();
	}


	/**
	*	Compresses a wide data array by calculating mean expression of all children structures of given parent structures
	*	@param an array of ontological parent structures
	*/
	public void collapseSamples(int[] samples){
		double[][] collapsed = new double[this.data.getRowSize()][samples.length];
		///Array of array. One array for each parent structure, containing indices of tissue samples that belong to it
		int[][] allChildrenIndices = new int[samples.length][];
		for(int i=0; i<samples.length; i++){
			allChildrenIndices[i] = this.ontology.getIndicesOfChildrenOfStructureWithId(samples[i],this.tissueIds);
		}

		///Go through parent tissue and collapse corresponding data
		for(int i=0; i<this.data.getRowSize(); i++){
			for(int j=0; j<allChildrenIndices.length; j++){

				double sum = 0;
				for(int x=0; x<allChildrenIndices[j].length; x++){
					sum += this.data.getValueAtIndex(i,allChildrenIndices[j][x]);

				}
				collapsed[i][j] = sum/allChildrenIndices[j].length;

			}
		}


		Matrix clpsd = new Matrix(collapsed);
		clpsd.printToFile("/Users/ahartens/Desktop/collapsed.csv");


		/*for(int i=0; i<this.data.getRowSize(); i++){
			System.out.printf("%f",collapsed[i][0]);

			for(int j=1; j<samples.length; j++){
				System.out.printf(",%f",collapsed[i][j]);
			}
			System.out.printf("\n");
		}*/

	}

	private void collapseGeneData(){
		String previousName = "";

		for (int i=0; i<this.geneNames.length; i++){
			if (this.geneNames[i].equals(previousName)){

			}
		}
	}


	/**
	*	Prints an brain structure and all its children
	*/
	public void printStructureAtIndex(int i){
		this.ontology.printStructureAtIndex(i);
	}

	/**
	*	@return matrix object containing all expression data
	*/
	public Matrix getData(){
		return this.data;
	}

	/**
	*	@return all gene names
	*/
	public String[] getAllGenes(){
		return this.geneNames;
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
