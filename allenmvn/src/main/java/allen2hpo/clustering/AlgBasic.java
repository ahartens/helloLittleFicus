package allen2hpo.clustering;

import allen2hpo.matrix.Matrix;

public class AlgBasic implements Kmeansable{
	private KmeansObject kmeans = null;
	
	public AlgBasic(Matrix m){
		kmeans = new KmeansObject(m);

	}

	public void setK(){
		kmeans.setK(3);
	}		
	public void setInitClusters(){
		kmeans.setInitClustersBasic();

	}					
	public void setDistCalc(DistComputable d){
		kmeans.setDistCalcBasic();

	}
	public void beginClustering(int i){
		kmeans.beginClustering(i);
	}
}