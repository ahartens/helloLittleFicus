package allen2hpo.allen;

import allen2hpo.matrix.*;



/**
*	Should store and handle all allen data for one brain. All functions relating to that brain go through here.
*/

public class AllenData{




	/**
	*	Ordered list of all gene abbreviations derived from probes.csv file. Order == gene expression matrix rows
	*/
	private String[] geneNames = null;

	/**
	*	still nothing
	*/
	private String[] tissueNames = null;

	/**
	*	Matrix object containing all expression data. each row is a gene, each column a tissue sample;
	*	Number of rows (genes) must be specified! is 'dim' in constructor method of allen data
	*/
	private Matrix data = null;





	public AllenData(String dir, int dim){

		ReadProbeAnnots probes = new ReadProbeAnnots(dir,dim);
		ReadTissueAnnots tissues = new ReadTissueAnnots(dir,1840);

		ReadExpression expression = new ReadExpression(dir,probes.getCount(),tissues.getCount());


		this.geneNames = probes.getData();
		this.data = expression.getData();


		//CovarMatrix cm = new CovarMatrix();
		//cm.covarCalcMatrix(this.data,1);


	}



	/**
	*	Returns matrix object containing all expression data
	*/
	public Matrix getData(){
		return this.data;
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
	*	returns an array of strings (Gene names) from an arry of indexes
	*/
	public String[] getGenesAtIndexes(int[] indexes){
		return getNamesAtIndexes(indexes,this.geneNames);
	}



	/**
	*	returns an array of strings (Tissue names) from an arry of indexes
	*/
	public String[] getTissuesAtIndexes(int[] indexes){
		return getNamesAtIndexes(indexes,this.tissueNames);
	}



	/**
	*	Called by get tissue or get gene. Returns array of Strings corresponding to index
	*/
	private String[] getNamesAtIndexes(int[] indexes, String[] allNames){
		String[] names = new String[indexes.length];


		for(int i = 0; i<indexes.length;i++){
			names[i] = (allNames[indexes[i]]);
		}

		return names;
	}
}
