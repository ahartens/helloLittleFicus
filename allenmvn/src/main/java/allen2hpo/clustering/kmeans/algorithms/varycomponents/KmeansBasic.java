package allen2hpo.clustering;

import allen2hpo.matrix.Matrix;

public class KmeansBasic implements Kmeansable{
	
    private KmeansObject kmeans = null;
	
	public KmeansBasic(Matrix m){
		kmeans = new KmeansObject(m);
		setK(kmeans);
		setInitClusters(kmeans);
		setDistCalc(kmeans);
		beginClustering(kmeans);

		int[] clusters = kmeans.getClusterAssignments();
		int[][] clusterIndics = kmeans.getClusterIndices();
		for (int i =0;i<clusters.length;i++){
			System.out.println(clusters[i]);
		}
	}


	/*kmeansable implementation*/

	public void setK(KmeansObject kmo){
		kmo.setK(4);
	}	

	public void setInitClusters(KmeansObject kmo){
		kmo.setInitClustersBasic();
	}	

	public void setDistCalc(KmeansObject kmo){
		kmo.setDistCalcBasic();
	}

	public void beginClustering(KmeansObject kmo){
		kmo.beginClustering(30);
	}

	public int[][] getClusterIndices(){
		return kmeans.getClusterIndices();
	}

	
}