package allen2hpo.clustering;

import allen2hpo.matrix.Matrix;

public class KmeansGapStat implements Kmeansable{
	
	private KmeansObject kmeans;

	public KmeansGapStat(Matrix mat){
		kmeans = new KmeansObject(mat);
	}

	public void setK(){
		///SET K VALUE.
		GetKable getK = new GapStat(kmeans.getData());
		int kval = getK.getK();
		kmeans.setK(kval);
	}	
	
	public void setInitClusters(){
		
	}				
	
	public void beginClustering(int i){
		kmeans.beginClustering(i);
	}
	
}