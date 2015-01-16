package allen2hpo.clustering;

import allen2hpo.matrix.Matrix;


/**
*	Implementaiton of Kmeans that uses Gap Stat to find the number of clusters to be used.
*	@param takes a Matrix object as an argument
*/
public class KmeansGapStat implements Kmeansable{
	
	/**
	*	Required kmeansobject for any class that implements interface kmeansable. 
	*/
	private KmeansObject kmeans;



	public KmeansGapStat(Matrix mat){
		kmeans = new KmeansObject(mat);
	}



	///KMEANSABLE METHODS
	
	/**
	*	Implementation of GapStat.
	*/
	public void setK(){
		///SET K VALUE.
		GetKable getK = new GapStat(kmeans.getData());
		int kval = getK.getK();
		kmeans.setK(kval);
	}	

	public void setInitClusters(){
		kmeans.setInitClustersBasic();
	}		

	public void setDistCalc(DistanceComputable d){
		kmeans.setDistCalcBasic();
	}
		
	public void beginClustering(int i){
		kmeans.beginClustering(i);
	}

	
}