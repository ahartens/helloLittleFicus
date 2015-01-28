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
		setK(kmeans);
		setInitClusters(kmeans);
		setDistCalc(kmeans);
		beginClustering(kmeans);
	}



	///KMEANSABLE METHODS
	
	/**
	*	Implementation of GapStat.
	*/
	public void setK(KmeansObject kmo){
		///SET K VALUE.

		GetKable getK = new GapStat(kmo.getData());
		int kval = getK.getK();
		System.out.println("THIS IS THE CALCULTED K:"+kval);

		kmo.setK(kval);
	}	

	public void setInitClusters(KmeansObject kmo){
		kmo.setInitClustersBasic();
	}		

	public void setDistCalc(KmeansObject kmo){
		kmo.setDistCalcBasic();
	}
		
	public void beginClustering(KmeansObject kmo){
		kmo.beginClustering(10);
	}

	public int[][] getClusterIndices(){
		return kmeans.getClusterIndices();
	}
	
}