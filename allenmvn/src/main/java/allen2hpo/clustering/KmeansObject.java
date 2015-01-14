package allen2hpo.clustering;

import allen2hpo.matrix.Matrix;


public class Kmeans{
	
	/**
	*	The number of clusters
	*/
	private int k = 0;

	/**
	*	An m x n matrix of data to be clustered
	*/
	private Matrix m = null;

	/**
	*	cluster prototypes. The centroids of the clusters being calculated. A k x n two dimensional array
	*/
	private double[][] cp = null;

	/**
	*	cluster index. An m x 1 matrix. Stores the index of the cluster to which each data point in matrix belongs to
	*/
	private int[] ci = null;
	

	public Kmeans(){

	}


	/**

	*	Constructor method. Takes a matrix object containing data that is to be clustered. 

	*	Classes subclassing Kmeans are required to call four methods :					
	
	*	1) setDataMatrix(Matrix m).					
	
	*	2) setK(int kval).				
	
	*	3) setInitClusters(double[] cp)  cp = cluster prototypes. A k x n two dimensional array. 					
	
	*	4) beginClustering(150).				

	*	@param Takes a matrix object of the data that is to be clustered

	*/
	public Kmeans(Matrix mat){
		
		///SET MATRIX FIELD
		this.m = mat;
		this.ci = new int[mat.getRowSize()];		
		
	/*	
		///INIT CLUSTER PROTOTYPES (AT THE MOMENT JUST TAKES FIRST 3 VALUES). CAN BE EXTENDED IN SUBLCASSES
		BasicInitClusters init = new BasicInitClusters();
		setInitClusters(init.initClusters(this.m,this.k));

		///BEGINS ITERATIVE CLUSTERING
		beginClustering(150);*/
	}

	public void setK(int kval){
		this.k = kval;	
	}

	public void setInitClusters(double[][] clusterSeeds){
		this.cp = clusterSeeds;

	}

	public int getK(){
		return this.k;
	}

	public Matrix getData(){
		return this.m;
	}

	/**

	*	Goes through each data point, assigning data points to nearest cluster. Then recalculates cluster means

	*	@param takes an int for number of iterations to be run

	*/
	public void beginClustering(int x){
		if (this.m == null)
			throw new IllegalArgumentException("Data not initialized");
		if (this.k == 0)
			throw new IllegalArgumentException("k not initialized");
		if (this.cp ==null)
			throw new IllegalArgumentException("cluster prototypes not initialized");

		 ///ITERATE CLUSTER ASSIGNMENT AND CLUSTER MEAN RECALCULATION STEPS
	    for (int i = 0; i<x; i++) {
	    	assignPointsToCluster();
	    	calcClusterMean();
	    }
	}





	private void assignPointsToCluster(){

	   	///INIT ARRAY WHERE ALL CALCULATED SSES WILL BE STORED (REUSED FOR EACH DATA POINT)
	    double[] allSSE = new double[this.k];
	    double dist;

	    ///GO THROUGH EACH DATA POINT
	    for (int i=0; i<this.m.getRowSize(); i++) {

	        ///FOR EACH DATA POINT CALCULATE THE DISTANCE FROM ALL CLUSTER CENTERS(SEED POINTS)
	        for (int j=0; j<this.k; j++) {
	            ///CALCULATE DISTANCE OF EACH DIMENSION FROM SELECTED MEAN
	            dist = 0;
	            for(int z=0;z<this.m.getColumnSize();z++){
	                ///DISTANCE OF EACH DIMENSION TO CORRESPONDING DIMENSION OF CLUSTER MEAN.
	               dist +=  Math.pow(m.getValueAtIndex(i,z) - cp[j][z],2.0);
	            }
	            ///CALCULATE SSE AND SAVE IN ARRAY
	            allSSE[j] = Math.sqrt(dist);
	        }

	        
	        ///FIND OUT WHICH MEAN IS THE CLOSEST (LOWEST SSE)
	        ///INIT MINSSE AS THE FIRST VALUE
	        double minSSE = allSSE[0];
	        int indexOfMinSSE = 0;
	        
	        ///ENUMERATE THROUGH REMAINING SSES AND COMPARE
	        for (int j = 1; j<this.k; j++) {
	            if (allSSE[j]<minSSE) {
	                minSSE = allSSE[j];
	                indexOfMinSSE = j;

	            }
	        }
	        
	        ///SAVE INDEX OF JUST ASSIGNED CLUSTER TO DATA POINT
	        this.ci[i] = indexOfMinSSE;
	    }

	}

	

	private void calcClusterMean(){
	    ///INIT ARRAYS WHERE DATA IS STORED
	    double[][] clusterSums = new double[this.k][this.m.getColumnSize()];
	    int[] clusterCounts = new int[this.k];

	    
	    ///ENUMERATE THROUGHD DATA POINTS
	    ///SUM UP ALL DATA AND COUNT VALUES
	    for (int i = 0; i<this.m.getRowSize(); i++) {
	        clusterCounts[this.ci[i]] ++;
	        for (int j = 0;j<this.m.getColumnSize();j++){
	            clusterSums[this.ci[i]][j] += this.m.getValueAtIndex(i,j);
	        }
	    }
	    
	    ///CALCULATE AVERAGE OF EVERY DIMENSION
	    for (int i=0; i<this.k; i++) {
	        for (int j=0; j<this.m.getColumnSize(); j++) {
	            this.cp[i][j] = clusterSums[i][j]/clusterCounts[i];
	        }
	    }

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