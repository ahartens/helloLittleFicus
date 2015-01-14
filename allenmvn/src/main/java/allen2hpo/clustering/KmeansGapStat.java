package allen2hpo.clustering;

import allen2hpo.matrix.Matrix;

public class KmeansGapStat extends Kmeans{
	
	public KmeansGapStat(Matrix mat){
		///SET MATRIX FIELD AND INITIALIZE ARRAY STORING CLUSTER INDEX ASSIGNMENT
		super.setDataMatrix(mat);
		
		///SET K VALUE. IN FUTURE CAN BE EXTENDED TO USE GAPSTAT, ELBOW METHOD
		GetKable getK = new GapStat(mat);
		int kval = getK.getK();
		super.setK(kval);

		///INIT CLUSTER PROTOTYPES (AT THE MOMENT JUST TAKES FIRST 3 VALUES). CAN BE EXTENDED IN SUBLCASSES
		BasicInitClusters init = new BasicInitClusters();
		super.setInitClusters(init.initClusters(mat,kval));

		///BEGINS ITERATIVE CLUSTERING
		super.beginClustering(150);
	}

	
}