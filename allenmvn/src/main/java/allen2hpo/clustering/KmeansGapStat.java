package allen2hpo.clustering;

import allen2hpo.matrix.Matrix;

public class KmeansGapStat implements Kmeansable{
	
	private Kmeans kmeans;

	public KmeansGapStat(Matrix mat){
		kmeans = new Kmeans(mat);
	}

	public void setK(){
		///SET K VALUE.
		GetKable getK = new GapStat(kmeans.getData());
		int kval = getK.getK();
		kmeans.setK(kval);
	}	
	
	public void setInitClusters(){
		///INIT CLUSTER PROTOTYPES (AT THE MOMENT JUST TAKES FIRST 3 VALUES). CAN BE EXTENDED IN SUBLCASSES
		BasicInitClusters init = new BasicInitClusters();
		kmeans.setInitClusters(init.initClusters(kmeans.getData(),kmeans.getK()));

	}				
	
	public void beginClustering(int i){
		kmeans.beginClustering(i);
	}
	
}