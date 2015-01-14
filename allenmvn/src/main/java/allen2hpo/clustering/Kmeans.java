package allen2hpo.clustering;

import allen2hpo.matrix.Matrix;


public class Kmeans{
	
	private int k;
	private Matrix m = null;
	private double[][] cp = null;

	/**

	*	Constructor method. Takes a matrix object containing data that is to be clustered. Classes subclassing Kmeans are required to call the same methods as found here

	*	@param Takes a matrix object of the data that is to be clustered

	*/
	public Kmeans(Matrix mat){

		///SET MATRIX FIELD
		setMatrix(mat);
		///SET K VALUE. IN FUTURE CAN BE EXTENDED TO USE GAPSTAT, ELBOW METHOD
		setK(3);	
		///INIT CLUSTER PROTOTYPES (AT THE MOMENT JUST TAKES FIRST 3 VALUES). CAN BE EXTENDED IN SUBLCASSES
		initClusters();
	}

	

	///SETTERS
	
	/**

	*	@param Takes a matrix object of the data that is to be clustered

	*/
	public void setMatrix(Matrix mat){
		this.m = mat;
	}

	public void setK(int val){
		this.k = k;
	}

	public void setClusterProtos(){
		this.cp = new double[this.k][m.getColumnSize()];
	}

	


	public void initClusters(){
		///INITIALIZE ARRAY HOLDING CLUSTER PROTYPES WITH LENGTH K (HOLDING VALUES OF MAT.COLUMN SIZE DIMENSIONS)
		setClusterProtos();
	    
	    int i,j;
	    ///INIT K CLUSTER SEEDS
	    for (i = 0; i<this.k; i++) {
	        ///FOR NOW JUST TAKE FIRST 3 POINTS OF DATA TABLE
	        for (j = 0; j< this.m.getColumnSize(); j++) {
	            cp[i][j] = this.m.getValueAtIndex(i,j);
	        }
	    }

	    System.out.println("CLUST PROTOS");
	    print();

	}

	


	public void assignPointsToCluster(){

	}

	

	public void calcClusterMean(){

	}

	


	private void print(){
		 for(int i = 0;i<this.cp.length;i++){
            for(int j = 0;j<this.cp[0].length;j++){
                System.out.printf("%.3f\t",this.cp[i][j]);
            }
            System.out.printf("\n");
        }
	}
}