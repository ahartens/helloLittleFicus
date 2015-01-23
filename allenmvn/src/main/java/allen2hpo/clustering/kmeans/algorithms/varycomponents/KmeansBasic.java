package allen2hpo.clustering;

import allen2hpo.matrix.Matrix;

public class KmeansBasic implements Kmeansable{
	private KmeansObject kmeans = null;
	
	public KmeansBasic(Matrix m){
		kmeans = new KmeansObject(m);
		setK();
		setInitClusters();
		setDistCalc();
		beginClustering();

		int[] clusters = kmeans.getClusterAssignments();
		int[][] clusterIndics = kmeans.getClusterIndices();
		for (int i =0;i<clusters.length;i++){
			System.out.println(clusters[i]);
		}

		for (int i =0;i<clusterIndics.length;i++){
			System.out.printf("cluster : %d\n",i);

			for (int j = 0;j<clusterIndics[i].length;j++){

				System.out.printf("%d ,",clusterIndics[i][j]);
			}
			System.out.printf("\n");
		}

	}


	/*kmeansable implementation*/

	public void setK(){
		kmeans.setK(4);
	}	

	public void setInitClusters(){
		kmeans.setInitClustersBasic();
	}	

	public void setDistCalc(){
		kmeans.setDistCalcBasic();
	}

	public void beginClustering(){
		kmeans.beginClustering(30);
	}
}