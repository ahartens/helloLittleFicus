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

	public Kmeans(){

	}
	public Kmeans(Matrix mat){
		///SET MATRIX FIELD
		this.m = mat;
		///SET K VALUE. IN FUTURE CAN BE EXTENDED TO USE GAPSTAT, ELBOW METHOD
		GetKBasic getK = new GetKBasic();
		this.k = getK.getK();	
		///INIT CLUSTER PROTOTYPES (AT THE MOMENT JUST TAKES FIRST 3 VALUES). CAN BE EXTENDED IN SUBLCASSES
		BasicInitClusters init = new BasicInitClusters();
		this.cp = init.initClusters(this.m,this.k);

		 ///ITERATE CLUSTER ASSIGNMENT AND CLUSTER MEAN RECALCULATION STEPS
	    for (int i = 0; i<150; i++) {
	      //  kmStp1AssignPointToCluster(allData, clusterProtos, dim);
	       // kmStp2CalcClusterMeans(allData, clusterProtos, dim);

	    }

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